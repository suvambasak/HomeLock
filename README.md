# Home Lock
IoT based Smart Door lock system that's controlled by the mobile application. It has a camera and also a distance sensor to provide a robust security system. If someone presses the calling bell it takes a photo of the person and sends it to the owner's phone as a notification. The owner can lock or unlock the door from the application without any physical effort. The distance sensor is always active to detect an obstacle. Whenever it detects an object in a certain range and a certain amount of time it automatically takes the photo and informs the owner that makes it too hard to break into the home.

This repository is for the android app to control that Door Lock.
If you are interested in that **Door Lock** or hardware side visit this [**repository**](https://github.com/suvambasak/door-lock.git) and for the **backend server** then visit this [**repository**](https://github.com/suvambasak/lock-server.git).


## Features
1. The door lock, unlock and take image request and lock live status view.
2. Push notification with image on lock status change and calling bell press.
3. Adding family members via QR code and access block/unblock to specific members.

## Build Project
1. Install Android Studio and git
2. Clone the repository
```bash
$ git clone https://github.com/suvambasak/HomeLock.git
```
3. Open Android Studio > Open an Existing Project > select HomeLock directory.

## Snapshots
### Lock status view and lock/unlock
<br>
<p align='center' width='100%'>
    <img width='300' src='https://github.com/suvambasak/HomeLock/blob/master/doc/home_online.png?raw=true'>
    &nbsp;&nbsp;&nbsp;
    <img width='300' src='https://github.com/suvambasak/HomeLock/blob/master/doc/home_offline.png?raw=true'>
    &nbsp;&nbsp;&nbsp;
    <img width='300' src='https://github.com/suvambasak/HomeLock/blob/master/doc/home_accs_remove.png?raw=true'>
</p>

<p align='center' width='100%'>
    <img width='300' src='https://github.com/suvambasak/HomeLock/blob/master/doc/home_online.png?raw=true'>
    &nbsp;&nbsp;&nbsp;
    <img width='300' src='https://github.com/suvambasak/HomeLock/blob/master/doc/home_offline.png?raw=true'>
<p>
<br>

### Push notifications
<br>
<p align='center' width='100%'>
    <img width='300' src='https://github.com/suvambasak/HomeLock/blob/master/doc/noti_bell.png?raw=true'>
    &nbsp;&nbsp;&nbsp;
    <img width='300' src='https://github.com/suvambasak/HomeLock/blob/master/doc/noti_obj_dtc.png?raw=true'>
<p>
<p align='center' width='100%'>
    <img width='300' src='https://github.com/suvambasak/HomeLock/blob/master/doc/noti_online.png?raw=true'>
    &nbsp;&nbsp;&nbsp;
    <img width='300' src='https://github.com/suvambasak/HomeLock/blob/master/doc/noti_offline.png?raw=true'>
<p>
<br>

### Member Management
<br>
<p align='center' width='100%'>
    <img width='300' src='https://github.com/suvambasak/HomeLock/blob/master/doc/member_add.png?raw=true'>
    &nbsp;&nbsp;&nbsp;
    <img width='300' src='https://github.com/suvambasak/HomeLock/blob/master/doc/memeber_mng.png?raw=true'>
<p>
<br>