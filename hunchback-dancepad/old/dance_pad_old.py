import usb.util
import requests
import time


arrows = {31: 'direction/up', 47: 'direction/down', 79: 'direction/left', 143: 'direction/right'}
other = {1: 'Triangle', 2: 'Square', 4: 'X', 8: 'O', 16: 'Select', 32: 'game/restart'}

def send_request(url, action):
    print(url.format(action))
    resp = requests.get(url.format(action))
    if resp.status_code != 200:
        # This means something went wrong.
        raise ApiError('GET /tasks/ {}'.format(resp.status_code))

def main(path):
    dev = usb.core.find(idVendor=0x79, idProduct=0x11)
    interface = 0
    endpoint = dev[0][(0,0)][0]
    previous = ''

    if dev.is_kernel_driver_active(interface) is True:
      dev.detach_kernel_driver(interface)
      usb.util.claim_interface(dev, interface)
    while True:
        try:
            data = dev.read(endpoint.bEndpointAddress,endpoint.wMaxPacketSize)
            col5 = data[5]
            col6 = data[6]
            if col5 != 15:
                if previous != col5:
                    print(arrows.get(col5))
                    send_request(path, arrows.get(col5))
                    previous = col5
            elif col6 != 0:
                if col6 == 32:
                    send_request(path, other.get(col6))
                    previous = col6
            else:
                previous = ''
        except usb.core.USBError as e:
            data = None
            if e.args == ('Operation timed out',):
                continue

    usb.util.release_interface(dev, interface)
    dev.attach_kernel_driver(interface)


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
    path = 'http://' + options.url + ':' + str(options.port) + '/{}'
    main(path)

