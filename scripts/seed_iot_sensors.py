#!/usr/bin/env python3
import json
import os
import random
import sys
import time
from urllib.parse import urljoin

try:
    import requests
except ImportError:
    print("Missing dependency: requests. Install with: pip install requests", file=sys.stderr)
    sys.exit(1)

BASE_URL = os.getenv("BASE_URL", "http://localhost:8080")
AUTH_TOKEN = os.getenv("AUTH_TOKEN")
USERNAME = os.getenv("USERNAME")
PASSWORD = os.getenv("PASSWORD")

SENSORS = [
    {
        "sensorId": "gu-rasht-classroom-a1-temp",
        "name": "Classroom A1 Temperature",
        "type": "TEMPERATURE",
        "location": "University of Guilan - Classroom A1",
        "unit": "C",
        "base": 22.5,
        "jitter": 0.6,
    },
    {
        "sensorId": "gu-rasht-classroom-a1-humidity",
        "name": "Classroom A1 Humidity",
        "type": "HUMIDITY",
        "location": "University of Guilan - Classroom A1",
        "unit": "%",
        "base": 48.0,
        "jitter": 3.0,
    },
    {
        "sensorId": "gu-rasht-classroom-a1-co2",
        "name": "Classroom A1 CO2",
        "type": "CO2",
        "location": "University of Guilan - Classroom A1",
        "unit": "ppm",
        "base": 620.0,
        "jitter": 80.0,
    },
    {
        "sensorId": "gu-rasht-library-light",
        "name": "Library Light",
        "type": "LIGHT",
        "location": "University of Guilan - Central Library",
        "unit": "lux",
        "base": 320.0,
        "jitter": 40.0,
    },
    {
        "sensorId": "gu-rasht-lab-occupancy",
        "name": "IoT Lab Occupancy",
        "type": "OCCUPANCY",
        "location": "University of Guilan - IoT Lab",
        "unit": "count",
        "base": 12.0,
        "jitter": 4.0,
    },
]


def login_and_get_token():
    if AUTH_TOKEN:
        return AUTH_TOKEN
    if not USERNAME or not PASSWORD:
        print("Set AUTH_TOKEN or USERNAME/PASSWORD env vars.")
        sys.exit(1)

    url = urljoin(BASE_URL, "/api/auth/login")
    resp = requests.post(url, json={"username": USERNAME, "password": PASSWORD}, timeout=10)
    if resp.status_code != 200:
        print(f"Login failed: {resp.status_code} {resp.text}")
        sys.exit(1)
    data = resp.json()
    return data.get("token") or data.get("jwt")


def register_sensor(token, sensor):
    url = urljoin(BASE_URL, "/api/iot/sensors/register")
    headers = {"Authorization": f"Bearer {token}"}
    resp = requests.post(url, json={
        "sensorId": sensor["sensorId"],
        "name": sensor["name"],
        "type": sensor["type"],
        "location": sensor["location"],
        "unit": sensor["unit"],
    }, headers=headers, timeout=10)

    if resp.status_code not in (200, 201):
        if "already" in resp.text.lower():
            print(f"Sensor exists: {sensor['sensorId']}")
            return
        print(f"Register failed for {sensor['sensorId']}: {resp.status_code} {resp.text}")
        return
    print(f"Registered: {sensor['sensorId']}")


def post_reading(token, sensor, value):
    url = urljoin(BASE_URL, f"/api/iot/sensors/{sensor['sensorId']}/reading")
    headers = {"Authorization": f"Bearer {token}"}
    resp = requests.post(url, json={"value": value}, headers=headers, timeout=10)
    if resp.status_code != 200:
        print(f"Reading failed for {sensor['sensorId']}: {resp.status_code} {resp.text}")


def main():
    token = login_and_get_token()
    if not token:
        print("No token obtained.")
        sys.exit(1)

    for sensor in SENSORS:
        register_sensor(token, sensor)

    rounds = int(os.getenv("READING_ROUNDS", "5"))
    delay = float(os.getenv("READING_DELAY", "2.0"))

    for _ in range(rounds):
        for sensor in SENSORS:
            base = sensor["base"]
            jitter = sensor["jitter"]
            value = base + random.uniform(-jitter, jitter)
            value = round(value, 2)
            post_reading(token, sensor, value)
        time.sleep(delay)

    print("Done.")


if __name__ == "__main__":
    main()
