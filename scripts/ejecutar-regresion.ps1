# Ejecuta RunnerTest, conserva la evidencia y, si hay SMTP configurado, envía el PDF.
$ErrorActionPreference = "Stop"
$raiz = (Resolve-Path (Join-Path $PSScriptRoot "..")).Path
Set-Location $raiz
$fecha = Get-Date -Format "yyyyMMdd-HHmmss"
$archivoSalida = Join-Path $raiz "reportes"
New-Item -ItemType Directory -Force -Path $archivoSalida | Out-Null
$log = Join-Path $archivoSalida "ejecucion-$fecha.log"
$pdfActual = Join-Path $raiz "target\cucumber\reporte-regresion.pdf"
$pdfCopia = Join-Path $archivoSalida "reporte-regresion-$fecha.pdf"

# No reutilizar por error un reporte de una ejecución anterior.
if (Test-Path $pdfActual) { Remove-Item $pdfActual -Force }
$mvn = Get-Command mvn.cmd -ErrorAction SilentlyContinue
if (-not $mvn) { $mvn = Get-Command mvn -ErrorAction SilentlyContinue }
$codigo = 1
if (-not $mvn) {
    "ERROR: Maven no está disponible en PATH del usuario de la tarea programada." | Out-File $log -Encoding utf8
} else {
    $errorPrevio = $ErrorActionPreference
    $ErrorActionPreference = "Continue"
    try {
        & $mvn.Source "-Dtest=RunnerTest" test *>&1 | Out-File $log -Encoding utf8
        $codigo = $LASTEXITCODE
        if ($null -eq $codigo) { $codigo = 1 }
    } finally {
        $ErrorActionPreference = $errorPrevio
    }
}
if (Test-Path $pdfActual) { Copy-Item $pdfActual $pdfCopia -Force }
$estado = if ($codigo -eq 0) { "APROBADO" } else { "CON FALLOS" }
Write-Host "Regresión QALAB: $estado (código $codigo)"
Write-Host "Log: $log"
if (Test-Path $pdfCopia) { Write-Host "PDF: $pdfCopia" }

# El envío es opcional: no se guardan claves SMTP dentro del proyecto.
$vars = @("QALAB_SMTP_HOST", "QALAB_SMTP_PORT", "QALAB_SMTP_USER", "QALAB_SMTP_PASSWORD", "QALAB_SMTP_FROM", "QALAB_SMTP_TO")
$incompletas = @($vars | Where-Object { [string]::IsNullOrWhiteSpace([Environment]::GetEnvironmentVariable($_)) })
if ($incompletas.Count -eq 0) {
    $mensaje = New-Object System.Net.Mail.MailMessage
    $smtp = $null
    try {
        $mensaje.From = $env:QALAB_SMTP_FROM
        foreach ($receptor in ($env:QALAB_SMTP_TO -split '[;,]')) {
            if ($receptor.Trim()) { $mensaje.To.Add($receptor.Trim()) }
        }
        $mensaje.Subject = "[QALAB] Regresión diaria - $estado - $fecha"
        $mensaje.Body = "Resultado: $estado`r`nCódigo de ejecución: $codigo`r`nLog: $log`r`nSe adjunta el reporte PDF si fue generado."
        if (Test-Path $pdfCopia) {
            $mensaje.Attachments.Add((New-Object System.Net.Mail.Attachment($pdfCopia)))
        }
        $smtp = New-Object System.Net.Mail.SmtpClient($env:QALAB_SMTP_HOST, [int]$env:QALAB_SMTP_PORT)
        $smtp.EnableSsl = $true
        $smtp.Credentials = New-Object System.Net.NetworkCredential($env:QALAB_SMTP_USER, $env:QALAB_SMTP_PASSWORD)
        $smtp.Send($mensaje)
        Write-Host "Correo de mantenimiento enviado."
    } catch {
        "Error enviando correo de mantenimiento: $($_.Exception.Message)" | Add-Content $log
        Write-Warning "No se pudo enviar el aviso por email. Verifica SMTP y red."
        if ($codigo -eq 0) { $codigo = 2 }
    } finally {
        if ($mensaje) { $mensaje.Dispose() }
        if ($smtp) { $smtp.Dispose() }
    }
} else {
    Write-Host "Correo omitido: faltan variables SMTP: $($incompletas -join ', ')"
}
exit $codigo
