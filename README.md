# namma-raste-health1

Android App Development using GenAI – Infrastructure Monitoring System

MindMatrix VTU Internship Program | Project Title: 33


📌 Overview

Namma-Raste Health is a smart Android-based Road Maintenance Tracker designed to improve rural infrastructure monitoring. The system creates a “Digital Life-Book” for every rural road, enabling citizens to report damages, track road conditions, and increase transparency in public infrastructure maintenance.

The goal is to empower citizens as **“Road Auditors”, ensuring long-term durability and accountability of government-built roads.


🚨 Problem Statement

Rural roads built under schemes like PMGSY often fail prematurely due to issues like:

* Water logging
* Overloaded vehicles
* Delayed maintenance
* Lack of accountability

Citizens notice road damage but lack a system to:

* Report issues easily
* Identify responsible contractors
* Track road maintenance history



💡 Solution Vision

Namma-Raste Health provides a digital monitoring system where:

* Every road has a **digital record (health history)**
* Citizens can report damages instantly
* Road condition is continuously updated
* Contractor details are transparently available

This transforms rural citizens into active participants in infrastructure maintenance.



🎯 Key Features

🗺️ Road Directory – Search and view road details
📸 Damage Reporting System – Upload photo + GPS-based location
🏗️ Contractor Information – View road construction details (simulated data)
📊 Road Health Dashboard – Visual status of road condition (Green/Red indicators)
🧭 Success Map View – Displays best-maintained roads in the region
⏱️ Timestamp Logging – Every report is recorded with time and location



🛠️ Tech Stack

* Platform: Android
* Language:Java / Kotlin
* Database: Room DB (SQLite abstraction)
* Maps Integration: Google Maps API (or simulation layer)
* UI Design: XML + Material Design
* Architecture: MVVM (recommended)

🏗️ System Architecture


Citizen/User
     ↓
Android Application
     ↓
Road Damage Reporting Module
     ↓
Database Management System
     ↓
Road Health Dashboard
     ↓
Authority Monitoring System




📁 Project Structure


Namma-Raste-Health/
│
├── app/
│   ├── src/main/
│   │   ├── java/              # Application logic
│   │   │   ├── activities/
│   │   │   ├── viewmodel/
│   │   │   ├── database/
│   │   │   └── repository/
│   │   │
│   │   ├── res/               # UI layouts, drawables, UI assets
│   │   └── AndroidManifest.xml
│
├── gradle/
├── build.gradle
└── README.md
```


##  How to Run the Project

## Using Android Studio

1. Clone the repository:

   ```bash
   git clone https://github.com/reema-rafeek/namma-raste-health1
   ```
2. Open Android Studio
3. Click on Open Project
4. Select the project folder
5. Wait for Gradle sync to complete
6. Run the app on:

   * Emulator OR
   * Physical Android device



🔹 Using APK File

1. Download the APK from the repository
2. Enable **Install from Unknown Sources** on Android
3. Install the APK
4. Launch the app



