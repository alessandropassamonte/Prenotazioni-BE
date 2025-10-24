# Desk Booking System - Documentazione Completa del Backend

## 📋 Indice
1. [Overview del Progetto](#overview)
2. [Architettura](#architettura)
3. [Modello Dati](#modello-dati)
4. [API Endpoints](#api-endpoints)
5. [Setup e Deployment](#setup)
6. [Esempi di Utilizzo](#esempi)

---

## 🎯 Overview del Progetto {#overview}

Sistema completo di gestione prenotazioni postazioni di lavoro e locker per edifici aziendali, basato sulle planimetrie reali di:
- **Piano 1**: 74 postazioni, 76 locker (62 turnisti + 14 free)
- **Piano 3**: 80 postazioni, 100 locker (6 turnisti + 94 free)

### Funzionalità Principali
✅ Gestione piani e dipartimenti
✅ Prenotazione postazioni con calendario
✅ Check-in/Check-out
✅ Assegnazione locker (turnisti e free)
✅ Dashboard occupazione real-time
✅ Sistema multi-ruolo (Admin, Manager, User)
✅ API RESTful complete
✅ Documentazione Swagger interattiva

---

## 🏗️ Architettura {#architettura}

### Stack Tecnologico
```
┌─────────────────────────────────────┐
│         Frontend (Angular/React)    │ ← Da sviluppare
├─────────────────────────────────────┤
│         REST API Layer              │
│    (Spring Boot Controllers)        │
├─────────────────────────────────────┤
│         Business Logic              │
│       (Service Layer)               │
├─────────────────────────────────────┤
│         Data Access Layer           │
│    (Spring Data JPA Repositories)   │
├─────────────────────────────────────┤
│         Database                    │
│       (PostgreSQL)                  │
└─────────────────────────────────────┘
```

### Layers

#### 1. **Controller Layer** (`/controller`)
Gestisce le richieste HTTP e le trasforma in chiamate ai service.
- `FloorController`: Gestione piani
- `DeskController`: Gestione postazioni
- `BookingController`: Gestione prenotazioni
- `UserController`: Gestione utenti
- `LockerController`: Gestione locker

#### 2. **Service Layer** (`/service`)
Contiene la logica di business e le regole di validazione.
- `FloorService`: Logica gestione piani e statistiche
- `DeskService`: Logica disponibilità postazioni
- `BookingService`: Logica prenotazioni e check-in/out
- `UserService`: Gestione autenticazione e profili
- `LockerService`: Logica assegnazione locker

#### 3. **Repository Layer** (`/repository`)
Interfacce Spring Data JPA per l'accesso ai dati.
- Query custom ottimizzate
- Gestione relazioni complesse
- Supporto paginazione

#### 4. **Model Layer** (`/model`)
Entità JPA che mappano le tabelle del database.

#### 5. **DTO Layer** (`/dto`)
Data Transfer Objects per la comunicazione API.

---

## 💾 Modello Dati {#modello-dati}

### Diagramma ER Semplificato
```
┌──────────┐      ┌──────────┐      ┌──────────┐
│  Floor   │──1:N─│   Desk   │──N:1─│Department│
└──────────┘      └──────────┘      └──────────┘
                       │1
                       │
                       │N
                  ┌──────────┐
                  │ Booking  │
                  └──────────┘
                       │N
                       │
                       │1
                  ┌──────────┐
                  │   User   │
                  └──────────┘
                       │1
                       │
                       │N
            ┌─────────────────────┐
            │ LockerAssignment    │
            └─────────────────────┘
                       │N
                       │
                       │1
                  ┌──────────┐
                  │  Locker  │
                  └──────────┘
```

### Entità Principali

#### Floor (Piano)
```java
- id: Long
- name: String (es: "PRIMO PIANO")
- floorNumber: Integer (1, 3)
- code: String ("MQ 673")
- squareMeters: Integer
- totalDesks: Integer
- totalLockers: Integer
- mapImageUrl: String
- active: Boolean
```

#### Desk (Postazione)
```java
- id: Long
- deskNumber: String
- floor: Floor
- department: Department
- type: DeskType (STANDARD, HOT_DESK, MEETING_ROOM)
- status: DeskStatus (AVAILABLE, OCCUPIED, MAINTENANCE)
- positionX, positionY: Double (coordinate mappa)
- equipment: String
- nearWindow, nearElevator, nearBreakArea: Boolean
```

#### Booking (Prenotazione)
```java
- id: Long
- user: User
- desk: Desk
- bookingDate: LocalDate
- startTime, endTime: LocalTime
- status: BookingStatus (ACTIVE, CHECKED_IN, COMPLETED, CANCELLED)
- type: BookingType (FULL_DAY, MORNING, AFTERNOON)
- checkedInAt, checkedOutAt: LocalDateTime
```

#### User (Utente)
```java
- id: Long
- email: String (unique)
- firstName, lastName: String
- employeeId: String (unique)
- department: Department
- role: UserRole (ADMIN, MANAGER, USER)
- workType: WorkType (STANDARD, TURNISTA, HYBRID, REMOTE)
```

#### Locker
```java
- id: Long
- lockerNumber: String
- floor: Floor
- type: LockerType (TURNISTI, FREE)
- status: LockerStatus (FREE, ASSIGNED, MAINTENANCE)
```

#### LockerAssignment (Assegnazione Locker)
```java
- id: Long
- user: User
- locker: Locker
- startDate, endDate: LocalDate
- status: AssignmentStatus (ACTIVE, EXPIRED, REVOKED)
- accessCode: String
```

---

## 🔌 API Endpoints {#api-endpoints}

### Base URL
```
http://localhost:8080/api
```

### 1. Floors API

#### GET /floors
Ritorna tutti i piani.
```bash
curl -X GET http://localhost:8080/api/floors
```

Response:
```json
[
  {
    "id": 1,
    "name": "PRIMO PIANO",
    "floorNumber": 1,
    "code": "MQ 673",
    "squareMeters": 673,
    "totalDesks": 74,
    "totalLockers": 76,
    "active": true
  }
]
```

#### GET /floors/{id}/statistics?date=2025-01-15
Statistiche occupazione piano per una specifica data.
```json
{
  "totalDesks": 74,
  "availableDesks": 45,
  "occupiedDesks": 29,
  "occupancyRate": 39.19,
  "totalLockers": 76,
  "freeLockers": 50,
  "assignedLockers": 26
}
```

### 2. Desks API

#### GET /desks/available?date=2025-01-15&floorId=1
Postazioni disponibili per data e piano.
```json
[
  {
    "id": 5,
    "deskNumber": "5",
    "floorId": 1,
    "floorName": "PRIMO PIANO",
    "departmentId": 1,
    "departmentName": "TRADING",
    "type": "STANDARD",
    "status": "AVAILABLE",
    "availableForDate": true
  }
]
```

#### POST /desks
Crea una nuova postazione.
```json
{
  "deskNumber": "101",
  "floorId": 1,
  "departmentId": 2,
  "type": "STANDARD",
  "equipment": "Monitor doppio, Docking station",
  "nearWindow": true
}
```

### 3. Bookings API

#### POST /bookings/user/{userId}
Crea una prenotazione.
```json
{
  "deskId": 5,
  "bookingDate": "2025-01-15",
  "type": "FULL_DAY",
  "notes": "Lavoro in ufficio"
}
```

Response:
```json
{
  "id": 123,
  "userId": 1,
  "userName": "Mario Rossi",
  "deskId": 5,
  "deskNumber": "5",
  "bookingDate": "2025-01-15",
  "status": "ACTIVE",
  "type": "FULL_DAY"
}
```

#### GET /bookings/user/{userId}/upcoming
Prenotazioni future dell'utente.

#### POST /bookings/{id}/check-in
Effettua check-in sulla prenotazione.

#### POST /bookings/{id}/check-out
Effettua check-out dalla prenotazione.

#### DELETE /bookings/{id}
Cancella prenotazione.
```json
{
  "cancellationReason": "Malattia"
}
```

### 4. Locker API (Simile a Booking)

---

## 🚀 Setup e Deployment {#setup}

### Opzione 1: Docker Compose (Raccomandato)
```bash
# Clone repository
git clone <repo-url>
cd desk-booking-system

# Avvia tutto con Docker
docker-compose up -d

# Verifica che funzioni
curl http://localhost:8080/api/floors
```

Servizi disponibili:
- **API**: http://localhost:8080
- **Swagger**: http://localhost:8080/swagger-ui.html
- **pgAdmin**: http://localhost:5050 (admin@company.it / admin)

### Opzione 2: Setup Manuale
```bash
# 1. Setup Database
createdb deskbooking
psql deskbooking < src/main/resources/data.sql

# 2. Configura application.properties
# Aggiorna credenziali database se necessario

# 3. Build e Run
mvn clean install
mvn spring-boot:run
```

---

## 📝 Esempi di Utilizzo {#esempi}

### Scenario 1: Utente prenota una postazione

1. **Ricerca postazioni disponibili**
```bash
GET /api/desks/available?date=2025-01-20&floorId=1
```

2. **Crea prenotazione**
```bash
POST /api/bookings/user/1
{
  "deskId": 15,
  "bookingDate": "2025-01-20",
  "type": "FULL_DAY"
}
```

3. **Check-in giorno della prenotazione**
```bash
POST /api/bookings/123/check-in
```

4. **Check-out a fine giornata**
```bash
POST /api/bookings/123/check-out
```

### Scenario 2: Admin visualizza statistiche

```bash
# Statistiche Piano 1 per oggi
GET /api/floors/1/statistics?date=2025-01-15

# Tutte le prenotazioni attive oggi
GET /api/bookings/date/2025-01-15

# Prenotazioni per piano specifico
GET /api/bookings/date/2025-01-15/floor/1
```

### Scenario 3: Assegnazione locker turnista

```bash
# 1. Trova locker liberi per turnisti
GET /api/lockers?type=TURNISTI&status=FREE

# 2. Assegna locker
POST /api/locker-assignments
{
  "userId": 4,
  "lockerId": 10,
  "startDate": "2025-01-01",
  "endDate": null,
  "accessCode": "1234"
}
```

---

## 🎨 Integrazioni Frontend

### Esempio chiamata da Angular
```typescript
// booking.service.ts
createBooking(userId: number, request: CreateBookingRequest): Observable<BookingDTO> {
  return this.http.post<BookingDTO>(
    `${this.apiUrl}/bookings/user/${userId}`,
    request
  );
}

// Component
this.bookingService.createBooking(this.userId, {
  deskId: 15,
  bookingDate: '2025-01-20',
  type: 'FULL_DAY'
}).subscribe(
  booking => console.log('Prenotazione creata:', booking),
  error => console.error('Errore:', error)
);
```

### Esempio chiamata da React
```javascript
// api/bookings.js
export const createBooking = async (userId, bookingData) => {
  const response = await fetch(`${API_URL}/bookings/user/${userId}`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(bookingData)
  });
  return response.json();
};

// Component
const handleBooking = async () => {
  try {
    const booking = await createBooking(userId, {
      deskId: 15,
      bookingDate: '2025-01-20',
      type: 'FULL_DAY'
    });
    console.log('Booking created:', booking);
  } catch (error) {
    console.error('Error:', error);
  }
};
```

---

## 🔒 Security (Da Implementare)

Per completare il sistema, considera di aggiungere:
- Spring Security per autenticazione
- JWT Token per sessioni
- Rate limiting
- Input validation avanzata
- HTTPS in produzione

---

## 📊 Monitoring e Logging

Il sistema usa SLF4J/Logback per logging:
```properties
logging.level.it.company.deskbooking=DEBUG
```

Log importanti vengono generati per:
- Creazione/modifica/cancellazione entità
- Errori di validazione
- Conflitti di prenotazione
- Performance query

---

## 🧪 Testing

```bash
# Unit tests
mvn test

# Integration tests
mvn verify

# Con coverage report
mvn test jacoco:report
```

---

## 📞 Supporto

Per domande o problemi:
- Email: it@company.com
- Issue Tracker: GitHub Issues

---

## 📄 Licenza

Copyright © 2025 Company. Tutti i diritti riservati.
