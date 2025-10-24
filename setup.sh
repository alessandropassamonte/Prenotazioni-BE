#!/bin/bash

# Desk Booking System - Setup Script
# Questo script facilita il setup del progetto

set -e  # Exit on error

echo "=================================="
echo "Desk Booking System - Setup"
echo "=================================="
echo ""

# Colori per output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Funzione per stampare messaggi
print_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check prerequisites
print_info "Controllo prerequisiti..."

# Check Java
if ! command -v java &> /dev/null; then
    print_error "Java non trovato. Installa Java 17 o superiore."
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 17 ]; then
    print_error "Java 17 o superiore richiesto. Versione attuale: $JAVA_VERSION"
    exit 1
fi
print_info "Java versione OK: $(java -version 2>&1 | head -n 1)"

# Check Maven
if ! command -v mvn &> /dev/null; then
    print_error "Maven non trovato. Installa Maven 3.8 o superiore."
    exit 1
fi
print_info "Maven versione: $(mvn -version | head -n 1)"

# Check Docker (optional)
if command -v docker &> /dev/null; then
    print_info "Docker trovato: $(docker --version)"
    DOCKER_AVAILABLE=true
else
    print_warn "Docker non trovato. Sarà necessario PostgreSQL installato localmente."
    DOCKER_AVAILABLE=false
fi

echo ""
echo "Seleziona modalità di setup:"
echo "1) Docker Compose (raccomandato - include database)"
echo "2) Setup locale (richiede PostgreSQL già installato)"
echo "3) Solo build (salta setup database)"
read -p "Scelta [1-3]: " SETUP_MODE

case $SETUP_MODE in
    1)
        if [ "$DOCKER_AVAILABLE" = false ]; then
            print_error "Docker non disponibile. Scegli opzione 2 o installa Docker."
            exit 1
        fi
        
        print_info "Avvio con Docker Compose..."
        
        # Build del progetto
        print_info "Build del progetto..."
        mvn clean package -DskipTests
        
        # Avvia Docker Compose
        print_info "Avvio servizi Docker..."
        docker-compose up -d
        
        print_info "Attesa avvio servizi..."
        sleep 10
        
        print_info "Setup completato!"
        echo ""
        echo "Servizi disponibili:"
        echo "  - API Backend: http://localhost:8080"
        echo "  - Swagger UI: http://localhost:8080/swagger-ui.html"
        echo "  - pgAdmin: http://localhost:5050 (admin@company.it / admin)"
        echo ""
        echo "Per vedere i log:"
        echo "  docker-compose logs -f app"
        echo ""
        echo "Per fermare i servizi:"
        echo "  docker-compose down"
        ;;
        
    2)
        print_info "Setup locale..."
        
        # Check PostgreSQL
        if ! command -v psql &> /dev/null; then
            print_error "PostgreSQL non trovato. Installa PostgreSQL 14 o superiore."
            exit 1
        fi
        
        read -p "Nome database [deskbooking]: " DB_NAME
        DB_NAME=${DB_NAME:-deskbooking}
        
        read -p "Username PostgreSQL [postgres]: " DB_USER
        DB_USER=${DB_USER:-postgres}
        
        read -sp "Password PostgreSQL: " DB_PASS
        echo ""
        
        # Crea database
        print_info "Creazione database..."
        PGPASSWORD=$DB_PASS createdb -U $DB_USER $DB_NAME 2>/dev/null || print_warn "Database già esistente"
        
        # Aggiorna application.properties
        print_info "Configurazione application.properties..."
        cat > src/main/resources/application-local.properties << EOF
spring.datasource.url=jdbc:postgresql://localhost:5432/$DB_NAME
spring.datasource.username=$DB_USER
spring.datasource.password=$DB_PASS
EOF
        
        # Build
        print_info "Build del progetto..."
        mvn clean install -DskipTests
        
        # Run
        print_info "Avvio applicazione..."
        echo ""
        mvn spring-boot:run -Dspring-boot.run.profiles=local
        ;;
        
    3)
        print_info "Solo build del progetto..."
        mvn clean install
        
        print_info "Build completata!"
        echo ""
        echo "JAR disponibile in: target/desk-booking-system-1.0.0.jar"
        echo ""
        echo "Per eseguire:"
        echo "  java -jar target/desk-booking-system-1.0.0.jar"
        ;;
        
    *)
        print_error "Scelta non valida"
        exit 1
        ;;
esac

echo ""
print_info "Setup completato con successo!"
