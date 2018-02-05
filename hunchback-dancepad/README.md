# Hunchback program for dancepad

## Installation
1. pip3 install pyusb
2. sudo apt-get install libusb-1.0-0 (sudo apt-get install libusb-1.0-0-dev)
3. Update /lib/udev/rules.d/50-udev-default.rules
4. pip3 install requests (REST client)

## Koppla upp mot Raspberry:n (om man kör med ethernet kabel)
ssh pi@dancepad.local

## Running
python3 dance_pad.py -u hostname -p 8080 (där hostname är maskinen där man kör backend)
