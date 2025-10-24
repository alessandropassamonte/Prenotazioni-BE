# Struttura Completa del Progetto

```
desk-booking-system/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── it/company/deskbooking/
│   │   │       │
│   │   │       ├── DeskBookingApplication.java          # Main Spring Boot Application
│   │   │       │
│   │   │       ├── config/                               # Configurazioni
│   │   │       │   └── OpenAPIConfig.java               # Config Swagger/OpenAPI
│   │   │       │
│   │   │       ├── model/                                # Entità JPA
│   │   │       │   ├── Floor.java                       # Entità Piano
│   │   │       │   ├── Department.java                  # Entità Dipartimento
│   │   │       │   ├── Desk.java                        # Entità Postazione
│   │   │       │   ├── Booking.java                     # Entità Prenotazione
│   │   │       │   ├── User.java                        # Entità Utente
│   │   │       │   ├── Locker.java                      # Entità Locker
│   │   │       │   └── LockerAssignment.java            # Entità Assegnazione Locker
│   │   │       │
│   │   │       ├── repository/                           # Repository JPA
│   │   │       │   ├── FloorRepository.java
│   │   │       │   ├── DepartmentRepository.java
│   │   │       │   ├── DeskRepository.java
│   │   │       │   ├── BookingRepository.java
│   │   │       │   ├── UserRepository.java
│   │   │       │   ├── LockerRepository.java
│   │   │       │   └── LockerAssignmentRepository.java
│   │   │       │
│   │   │       ├── service/                              # Business Logic
│   │   │       │   ├── FloorService.java
│   │   │       │   ├── DeskService.java
│   │   │       │   └── BookingService.java
│   │   │       │
│   │   │       ├── controller/                           # REST Controllers
│   │   │       │   ├── FloorController.java
│   │   │       │   ├── DeskController.java
│   │   │       │   └── BookingController.java
│   │   │       │
│   │   │       ├── dto/                                  # Data Transfer Objects
│   │   │       │   ├── FloorDTO.java
│   │   │       │   ├── DepartmentDTO.java
│   │   │       │   ├── DeskDTO.java
│   │   │       │   ├── BookingDTO.java
│   │   │       │   ├── UserDTO.java
│   │   │       │   └── LockerDTO.java
│   │   │       │
│   │   │       └── exception/                            # Gestione Eccezioni
│   │   │           ├── ResourceNotFoundException.java
│   │   │           ├── BookingException.java
│   │   │           └── GlobalExceptionHandler.java
│   │   │
│   │   └── resources/
│   │       ├── application.properties                    # Configurazione principale
│   │       └── data.sql                                  # Dati iniziali (Piano 1 e 3)
│   │
│   └── test/
│       └── java/
│           └── it/company/deskbooking/                  # Test unitari e integrazione
│
├── target/                                               # Cartella build (generata)
│
├── pom.xml                                               # Maven dependencies
├── Dockerfile                                            # Docker image definition
├── docker-compose.yml                                    # Docker Compose setup
├── .gitignore                                            # Git ignore rules
├── README.md                                             # Documentazione principale
├── DOCUMENTAZIONE.md                                     # Documentazione dettagliata
├── setup.sh                                              # Script di setup automatico
└── STRUTTURA_PROGETTO.md                                # Questo file
```

## 📊 Statistiche del Progetto

### File Generati
- **Entità (Model)**: 7 files
- **Repository**: 7 files
- **Service**: 3 files (core) + altri da implementare
- **Controller**: 3 files (core) + altri da implementare
- **DTO**: 6 files (con multiple classi interne)
- **Exception**: 3 files
- **Config**: 1 file
- **Totale file Java**: ~30+ files

### Linee di Codice (approssimativo)
- Model: ~1000 LOC
- Repository: ~500 LOC
- Service: ~1500 LOC
- Controller: ~500 LOC
- DTO: ~800 LOC
- Exception: ~100 LOC
- Config: ~50 LOC
- **Totale**: ~4500+ LOC

## 🎯 Componenti Chiave

### 1. Model Layer (Entità)
```
Floor → rappresenta piani edificio
  ↓
Desk → postazioni di lavoro
Department → dipartimenti aziendali
Booking → prenotazioni
User → utenti sistema
Locker → armadietti
LockerAssignment → assegnazioni locker
```

### 2. API Endpoints (REST)
```
/api/floors/*           → Gestione piani
/api/desks/*            → Gestione postazioni
/api/bookings/*         → Gestione prenotazioni
/api/departments/*      → Gestione dipartimenti (da completare)
/api/users/*            → Gestione utenti (da completare)
/api/lockers/*          → Gestione locker (da completare)
```

### 3. Database Schema
```
PostgreSQL Database: deskbooking

Tables:
- floors (piani)
- departments (dipartimenti)
- desks (postazioni)
- users (utenti)
- bookings (prenotazioni)
- lockers (locker)
- locker_assignments (assegnazioni locker)
```

## 🚀 Quick Start Commands

### Build e Run
```bash
# Con Docker
docker-compose up -d

# Locale con Maven
mvn spring-boot:run

# Script automatico
chmod +x setup.sh
./setup.sh
```

### Accesso Servizi
```bash
# API Backend
curl http://localhost:8080/api/floors

# Swagger UI
open http://localhost:8080/swagger-ui.html

# pgAdmin
open http://localhost:5050
```

### Testing
```bash
# Run tests
mvn test

# Build senza test
mvn clean install -DskipTests

# Package JAR
mvn clean package
```

## 📝 Dati Inizializzati

### Piano 1 (MQ 673)
- **74 postazioni** distribuite tra:
  - TRADING: 28 desk
  - IT APP: 10 desk
  - IT: 14 desk
  - TRAD. EDITOR - MKTG: 22 desk
- **76 locker**:
  - 62 turnisti
  - 14 free

### Piano 3 (MQ 673)
- **80 postazioni** distribuite tra:
  - IT ANALYTICS - DEMAND: 40 desk
  - IT INFRA - MID - DBA - DEV: 40 desk
- **100 locker**:
  - 6 turnisti
  - 94 free

### Utenti di Test
- admin@company.it (ADMIN)
- manager.trading@company.it (MANAGER)
- user.it@company.it (USER)
- turnista@company.it (USER - TURNISTA)

Password per tutti: `password123`

## 🔧 Configurazione

### application.properties
```properties
# Server
server.port=8080

# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/deskbooking
spring.datasource.username=postgres
spring.datasource.password=postgres

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

### Docker Compose Services
```yaml
services:
  - postgres:5432    # Database
  - app:8080         # Spring Boot App
  - pgadmin:5050     # DB Management UI
```

## 📚 Prossimi Passi

### Backend (Da Completare)
- [ ] UserService e UserController
- [ ] LockerService e LockerController
- [ ] DepartmentService e DepartmentController
- [ ] Spring Security + JWT
- [ ] Unit e Integration Tests
- [ ] Validazione input avanzata
- [ ] Gestione file upload (immagini mappe)

### Frontend (Da Sviluppare)
- [ ] Dashboard amministratore
- [ ] Vista mappa interattiva piani
- [ ] Calendario prenotazioni
- [ ] Gestione profilo utente
- [ ] Sistema notifiche
- [ ] Mobile responsive

### DevOps
- [ ] CI/CD Pipeline
- [ ] Monitoring (Prometheus/Grafana)
- [ ] Logging centralizzato
- [ ] Backup automatico DB
- [ ] Load balancing

## 📞 Supporto

Per assistenza o domande:
- 📧 Email: it@company.com
- 📖 Documentazione: DOCUMENTAZIONE.md
- 🐛 Bug Report: GitHub Issues

---

**Versione**: 1.0.0  
**Ultimo aggiornamento**: Ottobre 2025  
**Autore**: IT Team - Company
