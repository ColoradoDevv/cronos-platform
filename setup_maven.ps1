$ErrorActionPreference = "Stop"
$mavenUrl = "https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.9.9/apache-maven-3.9.9-bin.zip"
$outputFile = "tools\maven.zip"
$destinationPath = "tools"

Write-Host "Downloading Maven..."
Invoke-WebRequest -Uri $mavenUrl -OutFile $outputFile

Write-Host "Extracting Maven..."
Expand-Archive -Path $outputFile -DestinationPath $destinationPath -Force

Write-Host "Maven setup complete."
