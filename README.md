# 🌐 Spring WebSocket Application

![Java](https://img.shields.io/badge/Java-11%2F17-blue?style=flat-square)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-2.x-green?style=flat-square)
![License](https://img.shields.io/badge/License-MIT-lightgrey?style=flat-square)

Spring WebSocket Application is a **real-time communication backend** built with **Spring Boot** and **WebSocket**.  
It provides **bi-directional, event-driven communication** between client and server, ideal for chat systems, live notifications, and other real-time applications.

---

## 🔹 Overview

This project demonstrates **WebSocket communication** in Spring Boot with a structured approach:

- Server pushes data instantly to clients without repeated HTTP requests
- Supports **STOMP protocol** and **SockJS fallback**
- Easy to integrate with frontend frameworks (React, Vue, Angular)

---

## 🛠 Technologies Used

| Technology       | Description |
|------------------|------------|
| Java 21          | Programming language |
| Spring Boot      | Framework for backend |
| Spring WebSocket | WebSocket implementation |
| STOMP Protocol   | Messaging protocol |
| SockJS           | Fallback for WebSocket |
| Maven            | Build & dependency management |
| Lombok           | Boilerplate code reduction |
| Postman          | API testing |
| WebSocket Client | Browser or frontend apps |

---

## ✨ Core Features

- Real-time, two-way communication
- WebSocket + STOMP protocol support
- Broadcasting messages to multiple clients
- Lightweight, event-driven architecture
- Clean layered structure (Controller → Service → Model)

---

## 🚀 Getting Started

### Prerequisites

- Java 11 or higher
- Maven
- Web browser or WebSocket client

### Clone the Repository

```bash
git clone https://github.com/adias311/spring-websocket.git
cd spring-websocket
```

## Configuration

Edit `application.properties` or `application.yml`:

**application.properties:**
```properties
server.port=8080
spring.application.name=spring-websocket
spring.data.mongodb.uri=mongodb://localhost:27017/spring-websocket-db
spring.data.mongodb.database=spring-websocket-db
```

## Run the Application

```bash
mvn spring-boot:run
```

## Postman API Collection

The Postman API collection for this project is available in the GitHub repository:

```bash
Spring Websocket.postman_collection.json
```

Import the Postman collection (.json) file into Postman to test all available endpoints.

## License

[MIT License](LICENSE)

---
