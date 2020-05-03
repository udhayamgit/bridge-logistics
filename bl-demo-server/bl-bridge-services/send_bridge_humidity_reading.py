#!/usr/bin/env python
# -*- coding: utf-8 -*-
import json
from time import sleep

from paho.mqtt import publish


def send_meter(host, data):
    test_data = json.dumps(data)
    print("💧 Sending " + test_data)
    success = False
    while not success:
        try:
            publish.single("humidity", test_data, hostname=host)
            success = True
        except:
            print("🔴 Humidity MQTT service not ready yet. Press Ctr-C to stop. Retry in 10 seconds...")
            sleep(10)
    print("💧 Sent " + test_data)


if __name__ == '__main__':
    send_meter('127.0.0.1', {})
