# Ejecutar desde PowerShell en Windows con los permisos necesarios.
$ErrorActionPreference = "Stop"
$ruta = (Resolve-Path (Join-Path $PSScriptRoot "ejecutar-regresion.ps1")).Path
$accion = New-ScheduledTaskAction -Execute "powershell.exe" -Argument ('-NoProfile -ExecutionPolicy Bypass -File "' + $ruta + '"') -WorkingDirectory (Resolve-Path (Join-Path $PSScriptRoot "..")).Path
$trigger = New-ScheduledTaskTrigger -Daily -At "05:00"
$config = New-ScheduledTaskSettingsSet -StartWhenAvailable -ExecutionTimeLimit (New-TimeSpan -Hours 1)
Register-ScheduledTask -TaskName "NTT-QALAB-Regresion-5AM" -Action $accion -Trigger $trigger -Settings $config -Description "Regresion Selenium QALAB y reporte PDF a las 05:00" -Force | Out-Null
Write-Host "Tarea creada: NTT-QALAB-Regresion-5AM, todos los días 05:00 (hora local del equipo)"
Write-Host "Verifica en Programador de tareas: cuenta que ejecuta, Maven, entorno SMTP y permisos."
