# Script PowerShell pour arrêter l'environnement Kafka

Write-Host "=== Arrêt de l'environnement Kafka ===" -ForegroundColor Yellow

Write-Host "🛑 Arrêt de Kafka, Zookeeper et Kafka UI..." -ForegroundColor Yellow
docker-compose -f docker-compose-kafka.yml down

if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ Services Kafka arrêtés avec succès !" -ForegroundColor Green
} else {
    Write-Host "❌ Erreur lors de l'arrêt des services" -ForegroundColor Red
}

Write-Host ""
Write-Host "🧹 Pour nettoyer complètement (supprime les volumes) :"
Write-Host "   docker-compose -f docker-compose-kafka.yml down -v"