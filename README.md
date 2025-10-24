# Desk Booking System - Backend

Sistema di gestione prenotazioni postazioni di lavoro e locker per uffici aziendali.

## Descrizione

Questo sistema permette di:
- Gestire piani dell'edificio con relative postazioni e locker
- Prenotare postazioni di lavoro per date specifiche
- Assegnare locker ai dipendenti (con distinzione tra turnisti e liberi)
- Monitorare l'occupazione in tempo reale
- Check-in/Check-out delle prenotazioni

## Struttura del Progetto

```
desk-booking-system/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── it/company/deskbooking/
│   │   │       ├── DeskBookingApplication.java
│   │   │       ├── model/          # Entità JPA
│   │   │       ├── repository/     # Repository JPA
│   │   │       ├── service/        # Logica di business
│   │   │       ├── controller/     # REST Controllers
│   │   │       ├── dto/            # Data Transfer Objects
│   │   │       └── exception/      # Gestione eccezioni
│   │   └── resources/
│   │       ├── application.properties
│   │       └── data.sql           # Dati iniziali
│   └── test/
└── pom.xml
```

## Tecnologie Utilizzate

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data JPA** - Persistenza dati
- **PostgreSQL** - Database principale
- **H2** - Database in-memory per testing
- **Lombok** - Riduzione boilerplate
- **MapStruct** - Mapping DTO
- **SpringDoc OpenAPI** - Documentazione API

## Entità Principali

### Floor (Piano)
- Rappresenta un piano dell'edificio (es: Piano 1, Piano 3)
- Contiene informazioni su metratura, numero totale desk e locker

### Desk (Postazione)
- Singola postazione di lavoro
- Associata a un piano e un dipartimento
- Stati: AVAILABLE, OCCUPIED, MAINTENANCE, RESERVED

### Booking (Prenotazione)
- Prenotazione di una postazione per una data specifica
- Stati: ACTIVE, CHECKED_IN, CHECKED_OUT, COMPLETED, CANCELLED, NO_SHOW
- Supporta check-in e check-out

### Locker
- Armadietto per dipendenti
- Tipi: TURNISTI (per turnisti), FREE (liberi)
- Stati: FREE, ASSIGNED, MAINTENANCE

### LockerAssignment (Assegnazione Locker)
- Assegnazione di un locker a un utente
- Può essere temporanea o permanente

### Department (Dipartimento)
- Dipartimenti aziendali (es: TRADING, IT, ANALYTICS)
- Associati a specifici piani

### User (Utente)
- Dipendenti dell'azienda
- Ruoli: ADMIN, MANAGER, USER
- Tipi di lavoro: STANDARD, TURNISTA, REMOTE, HYBRID

## Setup e Installazione

### Prerequisiti
- Java 17+
- Maven 3.8+
- PostgreSQL 14+ (o Docker)

### 1. Clona il repository
```bash
git clone <repository-url>
cd desk-booking-system
```

### 2. Configura il database

#### Opzione A: PostgreSQL locale
```bash
# Crea il database
createdb deskbooking

# Aggiorna le credenziali in application.properties se necessario
```

#### Opzione B: Docker
```bash
docker run --name postgres-deskbooking \
  -e POSTGRES_DB=deskbooking \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  -d postgres:14
```

### 3. Compila ed esegui
```bash
# Compila il progetto
mvn clean install

# Esegui l'applicazione
mvn spring-boot:run

# Oppure
java -jar target/desk-booking-system-1.0.0.jar
```

L'applicazione sarà disponibile su: `http://localhost:8080`

## API Endpoints

### Floors (Piani)
```
GET    /api/floors                    # Tutti i piani
GET    /api/floors/active             # Piani attivi
GET    /api/floors/{id}               # Dettagli piano
GET    /api/floors/number/{number}    # Piano per numero
GET    /api/floors/{id}/statistics    # Statistiche piano
POST   /api/floors                    # Crea piano
PUT    /api/floors/{id}               # Aggiorna piano
DELETE /api/floors/{id}               # Elimina piano
```

### Bookings (Prenotazioni)
```
GET    /api/bookings                           # Tutte le prenotazioni
GET    /api/bookings/{id}                      # Dettagli prenotazione
GET    /api/bookings/user/{userId}             # Prenotazioni utente
GET    /api/bookings/user/{userId}/upcoming    # Prenotazioni future utente
GET    /api/bookings/date/{date}               # Prenotazioni per data
GET    /api/bookings/date/{date}/floor/{id}    # Prenotazioni per data e piano
POST   /api/bookings/user/{userId}             # Crea prenotazione
PUT    /api/bookings/{id}                      # Aggiorna prenotazione
DELETE /api/bookings/{id}                      # Cancella prenotazione
POST   /api/bookings/{id}/check-in             # Check-in
POST   /api/bookings/{id}/check-out            # Check-out
```

### Desks (Postazioni)
```
GET    /api/desks                                 # Tutte le postazioni
GET    /api/desks/{id}                            # Dettagli postazione
GET    /api/desks/floor/{floorId}                 # Postazioni per piano
GET    /api/desks/department/{departmentId}       # Postazioni per dipartimento
GET    /api/desks/available                       # Postazioni disponibili
POST   /api/desks                                 # Crea postazione
PUT    /api/desks/{id}                            # Aggiorna postazione
DELETE /api/desks/{id}                            # Elimina postazione
```

## Documentazione API (Swagger)

La documentazione interattiva delle API è disponibile su:
```
http://localhost:8080/swagger-ui.html
```

## Dati di Test

Il sistema viene inizializzato con i dati dei piani (Piano 1 e Piano 3) come da piantine fornite:

### Piano 1 (MQ 673)
- 74 postazioni totali
- Dipartimenti: TRADING (28), IT APP (10), IT (14), TRAD. EDITOR - MKTG (22)
- 62 locker turnisti, 14 locker free

### Piano 3 (MQ 673)
- 80 postazioni totali
- Dipartimenti: IT ANALYTICS-DEMAND, IT INFRA-MID-DBA-DEV
- 6 locker turnisti, 94 locker free

## Testing

```bash
# Esegui tutti i test
mvn test

# Test con coverage
mvn test jacoco:report
```

## Build per Production

```bash
# Build senza test
mvn clean package -DskipTests

# Il JAR sarà in target/desk-booking-system-1.0.0.jar
```

## Deployment

### Docker
```dockerfile
FROM openjdk:17-slim
COPY target/desk-booking-system-1.0.0.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

## Configurazione Ambiente

### Development
```properties
spring.profiles.active=dev
```

### Production
```properties
spring.profiles.active=prod
spring.jpa.hibernate.ddl-auto=validate
```

## Contributi

1. Fork del progetto
2. Crea un branch per la feature (`git checkout -b feature/AmazingFeature`)
3. Commit delle modifiche (`git commit -m 'Add some AmazingFeature'`)
4. Push del branch (`git push origin feature/AmazingFeature`)
5. Apri una Pull Request

## Licenza

Proprietario - © 2025 Company

## Contatti

Team IT - it@company.com
