# Сборка APK 3D-LAVKA-calculator
# Запуск: powershell -ExecutionPolicy Bypass -File build-apk.ps1

$ErrorActionPreference = "Stop"
$ProjectRoot = $PSScriptRoot
$ToolsDir = "$env:LOCALAPPDATA\3d-lavka-build-tools"
$JdkDir = "$ToolsDir\jdk-17"
$BuildDir = "C:\temp\3D-LAVRA-calculator"
$ApkOut = Join-Path $ProjectRoot "release\3D-LAVKA-calculator-debug.apk"

if (-not (Test-Path "$JdkDir\bin\java.exe")) {
    Write-Host "JDK не найден. Сначала запустите setup-env.ps1" -ForegroundColor Red
    exit 1
}

$env:JAVA_HOME = $JdkDir
$env:ANDROID_HOME = "$env:LOCALAPPDATA\Android\Sdk"
$env:ANDROID_SDK_ROOT = $env:ANDROID_HOME

if (-not (Test-Path $env:ANDROID_HOME)) {
    Write-Host "Android SDK не найден. Установите Android Studio." -ForegroundColor Red
    exit 1
}

Write-Host "=== Сборка APK ===" -ForegroundColor Cyan

# Копируем в ASCII-путь (избегаем проблем с кириллицей в пути на Windows)
if (Test-Path $BuildDir) { Remove-Item $BuildDir -Recurse -Force }
New-Item -ItemType Directory -Force -Path $BuildDir | Out-Null
robocopy $ProjectRoot $BuildDir /E /XD .gradle app\build build release .git /NFL /NDL /NJH /NJS /nc /ns /np | Out-Null

Push-Location $BuildDir

if (Test-Path ".\gradlew.bat") {
    & .\gradlew.bat assembleDebug --no-daemon
} else {
    & "$ToolsDir\gradle-8.9\bin\gradle.bat" assembleDebug --no-daemon
}

if ($LASTEXITCODE -ne 0) {
    Pop-Location
    Write-Host "Ошибка сборки!" -ForegroundColor Red
    exit 1
}

Pop-Location

New-Item -ItemType Directory -Force -Path (Join-Path $ProjectRoot "release") | Out-Null
Copy-Item "$BuildDir\app\build\outputs\apk\debug\app-debug.apk" $ApkOut -Force

$sizeMb = [math]::Round((Get-Item $ApkOut).Length / 1MB, 2)
Write-Host ""
Write-Host "=== Сборка завершена ===" -ForegroundColor Green
Write-Host "APK: $ApkOut"
Write-Host "Размер: $sizeMb MB"
