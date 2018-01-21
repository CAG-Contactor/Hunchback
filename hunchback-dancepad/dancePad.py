import usb.util
import requests
import time

arrows = {31: 'up', 47: 'down', 79: 'left', 143: 'right'}
other = {1: 'Triangle', 2: 'Square', 4: 'X', 8: 'O', 16: 'Select', 32: 'Start'}


def send_request(direction):
    resp = requests.get('http://192.168.86.27:8080/direction/{}/'.format(direction))
    if resp.status_code != 200:
        # This means something went wrong.
        raise ApiError('GET /tasks/ {}'.format(resp.status_code))


dev = usb.core.find(idVendor=0x79, idProduct=0x11)
interface = 0
endpoint = dev[0][(0,0)][0]

if dev.is_kernel_driver_active(interface) is True:
  dev.detach_kernel_driver(interface)
  usb.util.claim_interface(dev, interface)
while True:
    time.sleep(50/100)
    try:
        data = dev.read(endpoint.bEndpointAddress,endpoint.wMaxPacketSize)
        col5 = data[5]
        col6 = data[6]
        if col5 != 15:
            print(arrows.get(col5))
            send_request(arrows.get(col5))
        elif col6 != 0:
            print(other.get(col6))
    except usb.core.USBError as e:
        data = None
        if e.args == ('Operation timed out',):
            continue

usb.util.release_interface(dev, interface)
dev.attach_kernel_driver(interface)
