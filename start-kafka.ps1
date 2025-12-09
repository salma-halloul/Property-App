# Script PowerShell pour démarrer l'environnement Kafka

Write-Host "=== Démarrage de l'environnement Kafka ===" -ForegroundColor Green

# Vérifier si Docker est disponible
try {
    docker --version | Out-Null
    Write-Host "✅ Docker détecté" -ForegroundColor Green
} catch {
    Write-Host "❌ Docker n'est pas disponible. Veuillez installer Docker Desktop." -ForegroundColor Red
    exit 1
}

# Démarrer Kafka avec Docker Compose
Write-Host "🚀 Démarrage de Kafka, Zookeeper et Kafka UI..." -ForegroundColor Yellow
docker-compose -f docker-compose-kafka.yml up -d

if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ Kafka démarré avec succès !" -ForegroundColor Green
    Write-Host ""
    Write-Host "📊 Services disponibles :" -ForegroundColor Cyan
    Write-Host "   - Kafka: localhost:9092"
    Write-Host "   - Zookeeper: localhost:2181" 
    Write-Host "   - Kafka UI: http://localhost:8080"
    Write-Host ""
    Write-Host "📝 Pour tester l'intégration :"
    Write-Host "   1. Démarrez le notification-service: cd notification-service && ./mvnw spring-boot:run"
    Write-Host "   2. Démarrez le booking-service: cd booking-service && ./mvnw spring-boot:run"
    Write-Host "   3. Consultez le fichier KAFKA_INTEGRATION.md pour les tests"
} else {
    Write-Host "❌ Erreur lors du démarrage de Kafka" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "Vérification du statut des conteneurs :"
docker-compose -f docker-compose-kafka.yml ps