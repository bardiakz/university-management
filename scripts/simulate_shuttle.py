#!/usr/bin/env python3
import math
import os
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

VEHICLE_NUMBER = os.getenv("VEHICLE_NUMBER", "GU-SHUTTLE-01")
ROUTE_NAME = os.getenv("ROUTE_NAME", "Guilan Campus Loop")
CAPACITY = int(os.getenv("CAPACITY", "24"))
UPDATE_INTERVAL = float(os.getenv("UPDATE_INTERVAL", "2.0"))
LOOPS = int(os.getenv("LOOPS", "5"))

# Approximate loop around University of Guilan (Rasht) campus area
ROUTE_POINTS = [
    (37.2800, 49.5950),
    (37.2808, 49.5985),
    (37.2820, 49.6015),
    (37.2835, 49.6040),
    (37.2850, 49.6020),
    (37.2858, 49.5990),
    (37.2840, 49.5965),
    (37.2820, 49.5945),
    (37.2805, 49.5935),
    (37.2795, 49.5940),
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


def get_or_create_shuttle(token):
    headers = {"Authorization": f"Bearer {token}"}
    list_url = urljoin(BASE_URL, "/api/tracking/shuttles")
    resp = requests.get(list_url, headers=headers, timeout=10)
    if resp.status_code == 200:
        for shuttle in resp.json():
            if shuttle.get("vehicleNumber") == VEHICLE_NUMBER:
                return shuttle.get("id")

    create_url = urljoin(BASE_URL, "/api/tracking/shuttles")
    params = {
        "vehicleNumber": VEHICLE_NUMBER,
        "routeName": ROUTE_NAME,
        "capacity": CAPACITY,
    }
    resp = requests.post(create_url, params=params, headers=headers, timeout=10)
    if resp.status_code not in (200, 201):
        print(f"Failed to create shuttle: {resp.status_code} {resp.text}")
        sys.exit(1)
    return resp.json().get("id")


def bearing_degrees(a, b):
    lat1, lon1 = math.radians(a[0]), math.radians(a[1])
    lat2, lon2 = math.radians(b[0]), math.radians(b[1])
    dlon = lon2 - lon1
    x = math.sin(dlon) * math.cos(lat2)
    y = math.cos(lat1) * math.sin(lat2) - math.sin(lat1) * math.cos(lat2) * math.cos(dlon)
    brng = math.degrees(math.atan2(x, y))
    return (brng + 360) % 360


def post_location(token, shuttle_id, lat, lon, speed, heading):
    headers = {"Authorization": f"Bearer {token}"}
    url = urljoin(BASE_URL, "/api/tracking/location")
    payload = {
        "shuttleId": shuttle_id,
        "latitude": lat,
        "longitude": lon,
        "speed": speed,
        "heading": heading,
        "accuracy": 8.0,
    }
    resp = requests.post(url, json=payload, headers=headers, timeout=10)
    if resp.status_code != 200:
        print(f"Location update failed: {resp.status_code} {resp.text}")


def main():
    token = login_and_get_token()
    if not token:
        print("No token obtained.")
        sys.exit(1)

    shuttle_id = get_or_create_shuttle(token)
    if not shuttle_id:
        print("No shuttle ID.")
        sys.exit(1)

    print(f"Simulating shuttle {VEHICLE_NUMBER} (id {shuttle_id})")

    for _ in range(LOOPS):
        for i in range(len(ROUTE_POINTS)):
            curr = ROUTE_POINTS[i]
            nxt = ROUTE_POINTS[(i + 1) % len(ROUTE_POINTS)]
            heading = bearing_degrees(curr, nxt)
            speed = 18.0  # km/h-ish
            post_location(token, shuttle_id, curr[0], curr[1], speed, heading)
            time.sleep(UPDATE_INTERVAL)

    print("Done.")


if __name__ == "__main__":
    main()
