# S3-like Object Storage System

This project implements a simplified version of **Amazon S3**, designed to demonstrate backend engineering concepts such as microservices architecture, event-driven communication, secure authentication, and scalable data modeling.

---

## Project Goal

The goal of this project is to allow authenticated users to:

- Create and manage buckets
- Upload, download, and delete objects
- Manage object metadata (tags, description, access level)
- Support object versioning
- Search objects using metadata

The system is built with a strong focus on clean architecture and loose coupling between services.

---

## Tech Stack

### Backend
- Java 17
- Spring Boot 3.x
- Spring Data JPA + Hibernate
- Maven

### Security
- JWT Authentication (Spring Security)

### Data & Storage
- PostgreSQL
- Flyway (schema migrations)
- Local filesystem for object storage

### Messaging
- Apache Kafka
- JSON (Jackson)

### Tooling
- Swagger / springdoc-openapi
- Docker (Kafka + PostgreSQL)

---

## Architecture Overview

The system is implemented as a set of independent microservices:

- **auth-service** – authentication and JWT issuance
- **bucket-service** – bucket lifecycle management
- **object-service** – file upload, download, and storage
- **metadata-service** – metadata, tags, search, and versioning
- **common module** – shared DTOs, events, and utilities

Services communicate via:
- REST APIs for synchronous operations
- Kafka events for asynchronous state propagation

---

## Event-Driven Design

- Services never access each other’s databases
- State changes are propagated using domain events
- Events are immutable and idempotent
- Eventual consistency is achieved using Kafka

---

## Documentation

Detailed documentation is available in the `docs/` folder:

- **Architecture** – `docs/architecture.md`
- **Event Flow** – `docs/event-flow.md`
- **Data Model** – `docs/data-model.md`
- **Demo Flow** – `docs/demo-flow.md`

---

## Key Highlights

- Strong separation of storage and metadata
- Bucket-level versioning propagation
- Metadata-driven search
- Idempotent event processing
- Production-inspired architecture

---

## How to Run

1. Start PostgreSQL and Kafka (Docker recommended)
2. Run database migrations using Flyway
3. Start services in the following order:
    - auth-service
    - bucket-service
    - object-service
    - metadata-service
4. Access API documentation via Swagger UI

---

## Conclusion

This project demonstrates a clean, scalable, and event-driven backend system inspired by real-world cloud storage platforms. It balances architectural rigor with practical implementation choices suitable for a capstone-level system.
