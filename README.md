# PS-BLE Scan Application Demo
A robust Android application built in Kotlin with XML for design. This application showcases the capabilities of Bluetooth Low Energy (BLE) by scanning for nearby ESP32 BLE devices.

## Preview

<img src="https://raw.githubusercontent.com/pia-sharma/ps-blescan/main/ps_ble_demo.gif" width="250px" height="500px"/>

## Features | Bluetooth Low Energy (BLE) Scan: 
Utilizing the power of BLE, this application efficiently scans for ESP32 devices in the vicinity.

## Languages, libraries and tools used
* [Kotlin](https://kotlinlang.org/)
* [Hilt](https://developer.android.com/training/dependency-injection/hilt-android)
* [Timber](https://github.com/JakeWharton/timber)
* [Android_BLE](https://source.android.com/docs/core/connect/bluetooth/ble)
* [TedPermission](https://github.com/ParkSangGwon/TedPermission)
* [Sdp](https://github.com/intuit/sdp)

## Architecture
The architecture of the project follows the principles of MVVM using Clean Architecture.

### User Interface
XML

## Project Setup Instructions

1. Install Android Studio

2. Clone the git repository: `git clone git@github.com:pia-sharma/ps-blescan.git`

3. Open the project in Android Studio and build the `app` scheme to build all targets, or the scheme that corresponds to the architecture that you need.
   
4. Build and Run: Build the project and run it on an Android emulator or a physical device.

5. Scan for ESP32 Devices: Use the application to initiate a BLE scan and discover nearby ESP32 devices.

6. Explore Device Information: Once the scan is complete, explore the information about the detected ESP32 devices, such as device name and signal strength.

## Contributing:
Contributions to enhance and improve this BLE Scan Application Demo are welcome. Feel free to fork the repository, make your changes, and submit a pull request.

Thank you for choosing the BLE Scan Application Demo for exploring BLE capabilities on Android. If you encounter any issues or have suggestions for improvements, please open an issue on the repository.
