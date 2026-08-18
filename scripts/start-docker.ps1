docker info *> $null

if ($LASTEXITCODE -ne 0) {
    Write-Host "Starting Docker Desktop..."

    Start-Process -FilePath "C:\Program Files\Docker\Docker\Docker Desktop.exe"

    Write-Host "Waiting for Docker..."

    do {
        Start-Sleep -Seconds 2
        docker info *> $null
    }
    until ($LASTEXITCODE -eq 0)
}
else {
    Write-Host "Docker is already running."
}

Write-Host "Starting Docker containers..."

docker compose up -d