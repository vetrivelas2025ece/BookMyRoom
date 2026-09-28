"""Add fictional BookMyRoom demo data through the existing Spring Boot API.
Run: python BookMyRoom_Demo_Data.py --preview
     python BookMyRoom_Demo_Data.py --apply
Requires only Python 3; never deletes or modifies existing records.
"""
import argparse
import json
from datetime import date, timedelta
from urllib.request import Request, urlopen
from urllib.error import HTTPError, URLError

BASE = 'http://localhost:8080'
ROOMS = [(str(n), typ, cap, rate) for n, typ, cap, rate in [
    (101,'Standard',2,1800),(102,'Standard',2,1800),(103,'Standard',2,2000),
    (104,'Standard',2,2000),(105,'Standard',3,2400),(106,'Deluxe',2,3000),
    (107,'Deluxe',2,3200),(108,'Deluxe',3,3500),(109,'Deluxe',3,3800),
    (110,'Deluxe',4,4200),(201,'Suite',2,5000),(202,'Suite',3,5500),
    (203,'Suite',4,6000),(204,'Family',4,4500),(205,'Family',5,5200)]]
NAMES = [
    'Arun Kumar','Priya Raman','Karthik S','Divya N','Rahul Menon',
    'Ananya Iyer','Sanjay R','Meera Krishnan','Vishal P','Kavya S',
    'Naveen Kumar','Harini M','Rohit Sharma','Swetha R','Ashwin B',
    'Nithya S','Pranav Raj','Aishwarya V','Dinesh K','Lakshmi P',
    'Surya M','Janani K','Akash R','Pavithra N','Suresh V']

def api(method, path, payload=None):
    body = None if payload is None else json.dumps(payload).encode('utf-8')
    request = Request(BASE + path, data=body, method=method,
                      headers={'Content-Type': 'application/json'})
    try:
        with urlopen(request, timeout=12) as response:
            raw = response.read()
            return json.loads(raw) if raw else None
    except HTTPError as error:
        detail = error.read().decode('utf-8', 'replace')[:350]
        raise RuntimeError(f'{method} {path}: HTTP {error.code}: {detail}') from error

def overlap(a, b, c, d):
    return a < d and b > c

def main():
    parser = argparse.ArgumentParser(description=__doc__)
    group = parser.add_mutually_exclusive_group(required=True)
    group.add_argument('--preview', action='store_true', help='Show current counts; make no changes')
    group.add_argument('--apply', action='store_true', help='Add missing sample records')
    args = parser.parse_args()
    try:
        rooms = api('GET', '/api/rooms')
        guests = api('GET', '/api/guests')
        bookings = api('GET', '/api/bookings')
    except (URLError, RuntimeError) as exc:
        print('Cannot reach BookMyRoom. Start Spring Boot and check localhost:8080.')
        print(exc)
        return
    print(f'Existing: {len(rooms)} rooms, {len(guests)} guests, {len(bookings)} bookings')
    print(f'Target: at least 15 rooms, 20 guests and 25 bookings (including existing records)')
    if args.preview:
        print('Preview only. No records have been changed.')
        print('To add sample data: python BookMyRoom_Demo_Data.py --apply')
        return
    created = {'rooms':0, 'guests':0, 'bookings':0}
    room_numbers = {str(r['roomNumber']) for r in rooms}
    for number, typ, cap, rate in ROOMS:
        if len(rooms) >= 15:
            break
        if number in room_numbers:
            continue
        try:
            item = api('POST','/api/rooms',{'roomNumber':number,'roomType':typ,
                                           'capacity':cap,'ratePerNight':rate})
            rooms.append(item)
            room_numbers.add(number)
            created['rooms'] += 1
        except RuntimeError as exc:
            print('Room skipped:', exc)
    guest_emails = {str(g['email']).lower() for g in guests}
    for idx, name in enumerate(NAMES, 1):
        if len(guests) >= 20:
            break
        email = f'bmr.demo{idx:02d}@example.com'
        if email in guest_emails:
            continue
        try:
            item = api('POST','/api/guests',{'guestName':name,'email':email,
                                            'phoneNumber':f'900000{idx:04d}'})
            guests.append(item)
            guest_emails.add(email)
            created['guests'] += 1
        except RuntimeError as exc:
            print('Guest skipped:', exc)
    if not rooms or not guests:
        print('Need at least one room and one guest before creating bookings.')
        return
    # New bookings use generated demo guests when possible, leaving existing guests untouched.
    demo_guests = [g for g in guests if str(g['email']).startswith('bmr.demo')]
    booking_guests = demo_guests or guests
    # Bookings start tomorrow and spread over ~3 months; checkout is exclusive.
    start = date.today() + timedelta(days=1)
    attempts = 0
    while len(bookings) < 25 and attempts < 250:
        i = attempts
        attempts += 1
        checkin = start + timedelta(days=i * 3)
        checkout = checkin + timedelta(days=1 + (i % 3))
        room = rooms[i % len(rooms)]
        room_id = room['roomId']
        if any(b['roomId'] == room_id and b['status'] == 'CONFIRMED'
               and overlap(checkin, checkout, date.fromisoformat(b['checkInDate']),
                           date.fromisoformat(b['checkOutDate'])) for b in bookings):
            continue
        guest = booking_guests[i % len(booking_guests)]
        payload = {'roomId':room_id,'guestId':guest['guestId'],
                   'checkInDate':checkin.isoformat(),'checkOutDate':checkout.isoformat()}
        try:
            booking = api('POST','/api/bookings',payload)
            bookings.append(booking)
            created['bookings'] += 1
        except RuntimeError as exc:
            print('Booking skipped:', exc)
    print('Added:', ', '.join(f'{v} {k}' for k,v in created.items()))
    print(f'Now: {len(rooms)} rooms, {len(guests)} guests, {len(bookings)} bookings')
    print('Refresh http://localhost:8080/ to view your demo data.')
    if len(rooms)<15 or len(guests)<20 or len(bookings)<25:
        print('One or more targets were not reached; see skipped-record messages above.')

if __name__ == '__main__':
    main()
