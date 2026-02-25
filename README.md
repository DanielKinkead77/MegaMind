MegaMind is an Android application built using Java and Firebase that allows students to record and analyse their study sessions. The app provides visual analytics, including line and bar charts, to help users track time studied and monitor mood trends over time.

The goal of the project was to design a user-focused study tracking tool with secure authentication and real-time cloud-based data storage.

Features
  Secure user authentication using Firebase Authentication
  Cloud-based data storage with Firebase Firestore
  Log study sessions with duration and mood tracking
  Visual analytics using dynamic line and bar graphs
  Toggle between time-studied and mood-based insights
  User-specific data isolation (each user only sees their own sessions)

Tech Stack
  Java
  Android Studio
  Firebase Authentication
  Firebase Firestore
  MPAndroidChart
  RecyclerView
  Viewbinding

Analytics Functionality
  The application processes stored study session data and dynamically generates:  
    Line graphs to visualise study trends over time    
    Bar charts to compare total study duration and mood distribution    
    Filtered views to analyse different data categories  
  Graph data updates in real time based on Firestore queries.

Architecture & Structure
  Each screen is implemented as a dedicated Activity with its own layout
  Firebase Authentication manages user sessions and access control
  Firestore collections are scoped per user to ensure data isolation
  RecyclerView with a custom Adapter is used to display session history
  Study session and mood data are represented using dedicated model classes
  Asynchronous Firebase calls are handled to maintain a responsive UI

Screenshots
![MegaMindSidebar](https://github.com/user-attachments/assets/b72ac0d5-c033-4057-83a6-12fe082e1a4e)
![MegaMindMenu](https://github.com/user-attachments/assets/9b4848f8-1544-4d21-8afd-9868df3ade86)
![MegaMindDashboard](https://github.com/user-attachments/assets/e9f60bd0-20e5-474e-a658-88471fb18ab8)
![MegaMindAnalytics](https://github.com/user-attachments/assets/eb6d311f-c4ee-428f-b3f4-a9a886befbe1)
