# fix-invalid-chars-backup.ps1
param(
    [switch]$DryRun  # If specified, just show what would be fixed
)

# Backup folder
$backupFolder = Join-Path -Path (Get-Location) -ChildPath "java_backup"
if (-not (Test-Path $backupFolder)) {
    New-Item -ItemType Directory -Path $backupFolder | Out-Null
}

# Get all Java files recursively
Get-ChildItem -Recurse -Filter *.java | ForEach-Object {
    $file = $_.FullName
    Write-Host "Processing $file"

    # Backup file
    $relativePath = $file.Substring((Get-Location).Path.Length + 1)
    $backupPath = Join-Path -Path $backupFolder -ChildPath $relativePath
    $backupDir = Split-Path -Path $backupPath -Parent
    if (-not (Test-Path $backupDir)) {
        New-Item -ItemType Directory -Path $backupDir | Out-Null
    }

    Copy-Item -Path $file -Destination $backupPath -Force
    Write-Host "Backed up to $backupPath"

    # Read file as bytes
    $bytes = Get-Content -Path $file -Encoding Byte

    # Remove BOM if present
    if ($bytes.Length -ge 3 -and $bytes[0..2] -eq [byte[]](0xEF,0xBB,0xBF)) {
        if ($DryRun) {
            Write-Host "[DryRun] Would remove BOM from $file"
        } else {
            $bytes = $bytes[3..($bytes.Length-1)]
            Set-Content -Path $file -Value $bytes -Encoding Byte
            Write-Host "Removed BOM from $file"
        }
    }

    # Remove invalid UTF-8 characters
    $utf8 = [System.Text.Encoding]::UTF8
    try {
        $utf8.GetString($bytes) | Out-Null
    } catch {
        Write-Host "[Warning] Invalid UTF-8 detected in $file"
        if (-not $DryRun) {
            $cleanText = [System.Text.Encoding]::UTF8.GetString($bytes)
            Set-Content -Path $file -Value $cleanText -Encoding UTF8
            Write-Host "Removed invalid characters from $file"
        }
    }
}