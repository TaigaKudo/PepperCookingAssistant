$ports = @(8080, 5173)

foreach ($port in $ports) {
    $connections = Get-NetTCPConnection `
        -LocalPort $port `
        -State Listen `
        -ErrorAction SilentlyContinue

    foreach ($connection in $connections) {
        Write-Host "Stopping process on port $port (PID: $($connection.OwningProcess))"

        Stop-Process `
            -Id $connection.OwningProcess `
            -Force `
            -ErrorAction SilentlyContinue
    }
}

Write-Host "Stopping Docker containers..."

docker compose down