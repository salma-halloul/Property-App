# Communication Asynchrone avec Apache Kafka

## Architecture

Cette implémentation met en place une communication asynchrone entre microservices utilisant Apache Kafka :

- **Booking Service** : Publie des événements `BOOKING_CREATED` lorsqu'une nouvelle réservation est créée
- **Notification Service** : Consomme ces événements et envoie un email de notification à l'administrateur

## Configuration

### 1. Démarrer Kafka avec Docker Compose

```bash
cd ms-app-immobiliere
docker-compose -f docker-compose-kafka.yml up -d
```

Cela démarrera :
- Zookeeper (port 2181)
- Kafka (port 9092)  
- Kafka UI (port 8080) - Interface web pour visualiser les topics

### 2. Configuration Email

Pour configurer l'envoi d'emails, vous devez définir ces variables d'environnement ou modifier le fichier `application.properties` du notification-service :

```bash
# Variables d'environnement
export EMAIL_USERNAME=your-email@gmail.com
export EMAIL_PASSWORD=your-app-password
export ADMIN_EMAIL=admin@property-app.com
export FROM_EMAIL=noreply@property-app.com
```

**Note** : Pour Gmail, utilisez un "App Password" au lieu de votre mot de passe principal.

### 3. Démarrer les Services

```bash
# Démarrer le notification-service
cd notification-service
./mvnw spring-boot:run

# Dans un autre terminal, démarrer le booking-service
cd booking-service  
./mvnw spring-boot:run
```

## Test de l'intégration

### 1. Test via création normale d'un booking

Créez une réservation via l'API du booking-service :

```bash
curl -X POST http://localhost:8082/api/bookings \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "propertyId": 1,
    "userDefinedDate": "2024-12-15T14:30:00"
  }'
```

### 2. Test direct de l'événement Kafka

Pour tester uniquement la partie Kafka sans authentification :

```bash
curl -X POST http://localhost:8082/api/test/kafka-event \
  -H "Content-Type: application/json" \
  -d '{
    "bookingId": 123,
    "propertyId": 1,
    "userId": "user123",
    "bookingDate": "2024-12-09T10:30:00",
    "userDefinedDate": "2024-12-15T14:30:00",
    "status": "PENDING",
    "eventType": "BOOKING_CREATED"
  }'
```

### 3. Vérifier les logs

- **Booking Service** : Vérifiez que l'événement est publié
- **Notification Service** : Vérifiez que l'événement est reçu et l'email envoyé
- **Kafka UI** : Visitez http://localhost:8080 pour voir les messages dans le topic `booking-events`

## Topics Kafka

- **booking-events** : Topic principal pour les événements de réservation
  - Format des messages : JSON avec le schema `BookingCreatedEvent`
  - Partition : 1 (par défaut)
  - Replication : 1 (pour développement local)

## Structure des Événements

```json
{
  "bookingId": 123,
  "propertyId": 1,  
  "userId": "user123",
  "bookingDate": "2024-12-09T10:30:00",
  "userDefinedDate": "2024-12-15T14:30:00",
  "status": "PENDING",
  "eventType": "BOOKING_CREATED",
  "timestamp": "2024-12-09T10:30:00"
}
```

## Avantages de cette Architecture

1. **Découplage** : Les services ne se connaissent pas directement
2. **Résilience** : Si le notification-service est down, les événements sont stockés dans Kafka
3. **Scalabilité** : Facile d'ajouter de nouveaux consumers pour d'autres types de notifications
4. **Fiabilité** : Kafka garantit la livraison des messages
5. **Observabilité** : Tous les événements sont tracés et peuvent être rejoués

## Extension Possible

- Ajouter d'autres types d'événements (BOOKING_CANCELLED, BOOKING_CONFIRMED)
- Implémenter des notifications SMS, push notifications
- Ajouter un système de retry avec Dead Letter Queue
- Implémenter des patterns comme Saga pour les transactions distribuées