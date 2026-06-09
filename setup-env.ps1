# Настройка окружения для сборки 3D-LAVKA-calculator
# Запуск: powershell -ExecutionPolicy Bypass -File setup-env.ps1

$ErrorActionPreference = "Stop"
$ProjectRoot = $PSScriptRoot
$ToolsDir = "$env:LOCALAPPDATA\3d-lavka-build-tools"
$JdkDir = "$ToolsDir\jdk-17"
$GradleDir = "$ToolsDir\gradle-8.9"
$JdkZip = "$env:TEMP\OpenJDK17.zip"
$GradleZip = "$env:TEMP\gradle-8.9-bin.zip"

Write-Host "=== Настройка окружения 3D-LAVKA-calculator ===" -ForegroundColor Cyan

New-Item -ItemType Directory -Force -Path $ToolsDir | Out-Null

# JDK 17
if (-not (Test-Path "$JdkDir\bin\java.exe")) {
    Write-Host "Скачивание JDK 17..." -ForegroundColor Yellow
    Invoke-WebRequest -Uri "https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.13%2B11/OpenJDK17U-jdk_x64_windows_hotspot_17.0.13_11.zip" -OutFile $JdkZip
    Expand-Archive -Path $JdkZip -DestinationPath $ToolsDir -Force
    $extracted = Get-ChildItem $ToolsDir -Directory | Where-Object { $_.Name -like "jdk-17*" } | Select-Object -First 1
    if ($extracted -and $extracted.FullName -ne $JdkDir) {
        if (Test-Path $JdkDir) { Remove-Item $JdkDir -Recurse -Force }
        Rename-Item $extracted.FullName "jdk-17"
    }
    Write-Host "JDK 17 установлен: $JdkDir" -ForegroundColor Green
} else {
    Write-Host "JDK 17 уже установлен: $JdkDir" -ForegroundColor Green
}

# Gradle 8.9
if (-not (Test-Path "$GradleDir\bin\gradle.bat")) {
    Write-Host "Скачивание Gradle 8.9..." -ForegroundColor Yellow
    Invoke-WebRequest -Uri "https://services.gradle.org/distributions/gradle-8.9-bin.zip" -OutFile $GradleZip
    Expand-Archive -Path $GradleZip -DestinationPath $ToolsDir -Force
    Write-Host "Gradle установлен: $GradleDir" -ForegroundColor Green
} else {
    Write-Host "Gradle уже установлен: $GradleDir" -ForegroundColor Green
}

# Android SDK
$SdkDir = "$env:LOCALAPPDATA\Android\Sdk"
if (-not (Test-Path $SdkDir)) {
    Write-Host "ВНИМАНИЕ: Android SDK не найден в $SdkDir" -ForegroundColor Red
    Write-Host "Установите Android Studio или SDK Command-line Tools." -ForegroundColor Red
} else {
    Write-Host "Android SDK найден: $SdkDir" -ForegroundColor Green
}

# local.properties
$localProps = Join-Path $ProjectRoot "local.properties"
$sdkEscaped = $SdkDir -replace '\\', '\\'
"sdk.dir=$sdkEscaped" | Set-Content $localProps -Encoding UTF8
Write-Host "Создан local.properties" -ForegroundColor Green

# Gradle wrapper
if (-not (Test-Path (Join-Path $ProjectRoot "gradlew.bat"))) {
    Write-Host "Генерация Gradle Wrapper..." -ForegroundColor Yellow
    $env:JAVA_HOME = $JdkDir
    $env:ANDROID_HOME = $SdkDir
    Push-Location $ProjectRoot
    & "$GradleDir\bin\gradle.bat" wrapper --gradle-version 8.9 --no-daemon
    Pop-Location
}

Write-Host ""
Write-Host "=== Окружение готово ===" -ForegroundColor Cyan
Write-Host "JAVA_HOME: $JdkDir"
Write-Host "ANDROID_HOME: $SdkDir"
Write-Host ""
Write-Host "Для сборки APK запустите: .\build-apk.ps1" -ForegroundColor Yellow
