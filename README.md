# Mini Transport Tycoon - Group 10
A simplified transportation-economic simulation game developed for the **Software Technology Practice** course (2025/2026 Spring) at **ELTE Faculty of Informatics**.

## 📖 Project Overview
The goal of **Mini Transport Tycoon** is to maximize profit by organizing road freight and passenger transport between cities and industrial facilities. Players must build road networks, establish strategic routes, and manage a fleet of vehicles while maintaining financial stability to avoid bankruptcy.



---

## 🚀 Team & Complexity Units (Total: 5.0)
To achieve the required complexity of 5.0 units, our team has selected the following features:

| Feature | Assigned Member | Complexity | Description |
| :--- | :--- | :--- | :--- |
| **Base Game** | All Members | 2.0 | Core mechanics, grid map, and economy. |
| **Forests** | Srinivas James Madoc | 0.5  | Trees grow on tiles and increase road building costs. |
| **Minimap** | Srinivas James Madoc | 0.5  | A navigable, small-scale map for easier orientation. |
| **Persistence** | Jiayu Kang | 0.5  | Save/load system for game states and vehicle positions. |
| **Continuous Movement** | Jiayu Kang | 0.5  | Smooth, animated vehicle movement between tiles. |
| **Traffic Lights** | Alizadeh Erfan | 1.0 | Manual/timed control of signals at intersections. |

---

## 🛠️ Functional Requirements

### Core Mechanics
* **Map System**: A 2D grid featuring fixed cities (min $3\times3$ tiles) and industrial facilities (min $2\times2$ tiles).
* **Infrastructure**: Players can build roads on empty tiles and place stops.
* **Logistics**:
    * Support for passengers and at least 3 types of industrial goods (e.g., wood, iron, steel).
    * Cities and industries generate production/demand that changes over time.
* **Transportation**:
    * Dedicated road vehicles (at least 2 types per category) for passengers and cargo.
    * Configurable circular routes (e.g., $A\rightarrow B\rightarrow C\rightarrow A$) for automated movement.
* **Time Management**: Four speed settings: Pause, Normal, Fast (2x), and Very Fast (4x).

### Sub-task Specifics
* **Vegetation**: Dynamic tree growth (1-4 trees per tile) on empty tiles; clearing trees adds to construction costs.
* **Navigation**: A scrollable interface with a dedicated navigable minimap for navigation.
* **Data Management**: Ability to handle multiple save files and resume journeys for vehicles in motion.
* **Traffic Control**: Player-installed traffic lights at junctions with adjustable green light intervals per direction.

---

## ⚙️ Non-Functional Requirements
* **Technology**: Java (JDK 17 or higher recommended).
* **Tools**: Git for version control, Maven/Gradle for build management.
* **UI Style**: 2D top-down perspective where individual tile content is illustrated by images.
* **Performance**: Smooth continuous animation for vehicles between tiles.



---

## 📂 Design Documentation (Wiki)
Detailed documentation can be found in the project Wiki:
1. **Use Case Diagram**: Interaction between players and the simulation engine.
2. **UI Plan / Wireframes**: Visual layout of the game interface and popups.
3. **User Stories**: Functional goals for manual testing and implementation verification.
4. **Class Diagram**: Structural architecture following the **MVC (Model-View-Controller)** pattern.

---

## 💻 Installation & Setup
1. **Clone the repository**:
   ```bash
   git clone [https://szofttech.inf.elte.hu/software-technology-2026/group-10/let-me-think.git](https://szofttech.inf.elte.hu/software-technology-2026/group-10/let-me-think.git)