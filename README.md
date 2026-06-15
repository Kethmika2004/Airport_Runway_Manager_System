# ✈️ Airport Runway Management System

A Java-based concurrent programming simulation that models an **Airport Runway Management System** using multithreading, semaphores, and synchronization techniques.

This project demonstrates how multiple airplanes can safely share a limited number of runways while ensuring:

- ✅ Mutual Exclusion
- ✅ Deadlock Prevention
- ✅ Starvation Freedom
- ✅ Fair Resource Allocation
- ✅ Thread Synchronization

---

## 📌 Project Overview

The system simulates an airport where:

- **10 Airplanes** operate concurrently as independent threads.
- **5 Runways** are shared resources.
- Airplanes request runway clearance before landing or taking off.
- The **Control Tower** manages runway allocation.
- A **fair semaphore** ensures orderly access to runways.

This project is inspired by the concepts behind the **Dining Philosophers Problem** and demonstrates practical solutions to common concurrency challenges.

---

## 🏗️ System Architecture

### Components

#### 1️⃣ ControlTower
Acts as the central coordinator.

Responsibilities:

- Manage all available runways.
- Grant runway clearance.
- Release runways after use.
- Prevent resource conflicts.
- Ensure fairness using a FIFO semaphore.

---

#### 2️⃣ Runway
Represents a physical runway.

Features:

- Unique runway ID.
- Occupied/available state tracking.
- Atomic occupancy management.

---

#### 3️⃣ Airplane
Represents an aircraft as a thread.

Responsibilities:

- Request runway clearance.
- Perform landing or takeoff operations.
- Release runway after completion.

---

#### 4️⃣ AirportRunwayManagerSystem
Main entry point of the application.

Responsibilities:

- Create runways.
- Create airplane threads.
- Start simulation.
- Wait for all planes to finish.

---

## 🔄 Concurrency Design

### Semaphore-Based Runway Pool

The system uses:

```java
Semaphore runwayPool = new Semaphore(runwayCount, true);
```

The second argument (`true`) enables:

- FIFO scheduling
- Fair access ordering
- Starvation prevention

---

### Mutual Exclusion

Only one airplane can occupy a runway at a time.

```java
if (rw.tryOccupy()) {
    return rw;
}
```

This prevents runway collisions.

---

### Deadlock Prevention

Deadlock is prevented because:

- Airplanes request clearance through a centralized controller.
- Semaphore permits match runway count.
- No circular waiting conditions exist.
- Resources are allocated in a controlled manner.

---

### Starvation Prevention

The fair semaphore guarantees:

```java
new Semaphore(runwayCount, true)
```

Airplanes receive runway access in the order they requested it.

---

## 📊 Liveness Guarantees

### ✅ Mutual Exclusion

Only one airplane can use a runway at any given time.

### ✅ No Deadlock

The semaphore-controlled runway pool prevents circular waiting.

### ✅ No Starvation

FIFO queuing ensures every airplane eventually receives runway access.

### ✅ Safe Resource Sharing

Runways are allocated and released safely using synchronization mechanisms.

---

## 🛠 Technologies Used

- Java
- Multithreading
- Synchronization
- Semaphore API
- Java Logging Framework
- Object-Oriented Programming (OOP)

---

## 🚀 How to Run

### Clone the Repository

```bash
git clone https://github.com/yourusername/Airport-Runway-Management-System.git
```

### Navigate to Project Directory

```bash
cd Airport-Runway-Management-System
```

### Compile

```bash
javac AirportRunwayManagerSystem.java
```

### Run

```bash
java AirportRunwayManagerSystem
```

---

## 📷 Sample Output

```text
[INFO ] Plane-1 wants to LAND.
[INFO ] Plane-2 wants to TAKE OFF.
[INFO ] Plane-1 requests runway clearance.
[INFO ] Plane-1 granted clearance → Runway-1
[INFO ] Plane-1 is LANDING on Runway-1.
[INFO ] Plane-2 granted clearance → Runway-2
[INFO ] Plane-2 is TAKE OFFING on Runway-2.
[INFO ] Plane-1 released Runway-1.
[INFO ] Plane-2 released Runway-2.
[INFO ] All planes have completed. Airport clear.
```

---

## 📚 Learning Outcomes

This project demonstrates:

- Java thread creation and management
- Resource synchronization
- Semaphore usage
- Concurrent system design
- Deadlock avoidance strategies
- Fair scheduling mechanisms
- Real-world application of operating system concepts

---

## 🎯 Academic Relevance

This simulation is suitable for courses involving:

- Operating Systems
- Concurrent Programming
- Distributed Systems
- Software Engineering
- Advanced Java Programming

---

## 📄 License

This project is available under the MIT License.

Feel free to use, modify, and distribute it for educational and research purposes.

---

## 👨‍💻 Author

**Yasandu Kethmika**

Computer Science & Engineering Undergraduate  
University of Moratuwa

GitHub: https://github.com/Kethmika2004 
LinkedIn: https://www.linkedin.com/in/yasandu-kethmika-88a428337/

---
⭐ If you found this project useful, consider giving it a star on GitHub.
