import requests
import threading
import usb.util
import sys
import queue
import time

arrows = {31: 'up', 47: 'down', 79: 'left', 143: 'right'}
other = {1: 'Triangle', 2: 'Square', 4: 'X', 8: 'O', 16: 'Select', 32: 'Start'}


class DancePadUSBThread (threading.Thread):
    def __init__(self, thread_id, work_queue, work_queue_lock):
        threading.Thread.__init__(self)
        self.thread_id = thread_id
        self.work_queue = work_queue
        self.work_queue_lock = work_queue_lock
        self.dev = usb.core.find(idVendor=0x79, idProduct=0x11)
        self.interface = 0
        self.endpoint = self.dev[0][(0, 0)][0]
        self.previous = ''
        if self.dev.is_kernel_driver_active(self.interface) is True:
            self.dev.detach_kernel_driver(self.interface)
            usb.util.claim_interface(self.dev, self.interface)

    def run(self):
        if self.dev.is_kernel_driver_active(self.interface) is True:
            self.dev.detach_kernel_driver(self.interface)
            usb.util.claim_interface(self.dev, self.interface)
        while True:
            try:
                data = self.dev.read(self.endpoint.bEndpointAddress, self.endpoint.wMaxPacketSize)
                col5 = data[5]
                col6 = data[6]
                if col5 != 15:
                    if self.previous != col5:
                        with self.work_queue_lock:
                            self.work_queue.put(arrows.get(col5))
                        self.previous = col5
                #elif col6 != 0:
                #print(other.get(col6))
                else:
                    self.previous = ''
            except usb.core.USBError as e:
                data = None
                if e.args == ('Operation timed out',):
                    continue
        usb.util.release_interface(self.dev, self.interface)
        self.dev.attach_kernel_driver(self.interface)


class DancePadSendThread (threading.Thread):
    def __init__(self, thread_id, work_queue, work_queue_lock, url):
        threading.Thread.__init__(self)
        self.thread_id = thread_id
        self.work_queue = work_queue
        self.work_queue_lock = work_queue_lock
        self.url = url

    def run(self):
        while True:
            self.send_request()

    def send_request(self):
        with self.work_queue_lock:
            if not self.work_queue.empty():
                direction = self.work_queue.get()
                if direction:
                    resp = requests.get(self.url.format(direction))
                    print('Response {}'.format(resp.text))
                    if resp.status_code != 200:
                        print('send_request with call: {} failed with status {}'.format(self.url, resp.status_code))
                    self.work_queue.task_done()


def main(url):
    work_queue = queue.Queue(10)
    queue_lock = threading.Lock()
    usb_thread = DancePadUSBThread("USB-Thread", work_queue, queue_lock)
    send_thread = DancePadSendThread("Send-Thread", work_queue, queue_lock, url)
    usb_thread.start()
    send_thread.start()


if __name__ == '__main__':
    from optparse import OptionParser
    parser = OptionParser()
    parser.add_option('-u', '--url', default='127.0.0.1', type=str,
                      help='URL to backend.')
    parser.add_option('-p', '--port', default='8080', type=int,
                      help='Port where backend is running.')
    (options, args) = parser.parse_args()
    if options.url is None:
        parser.print_help(sys.stderr)
        sys.exit(1)
    if options.port is None:
        parser.print_help(sys.stderr)
        sys.exit(1)
    path = 'http://' + options.url + ':' + str(options.port) + '/direction/{}'
    main(path)

