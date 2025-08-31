# fix-duplicates.ps1
# Usage: .\fix-duplicates.ps1 -DryRun $true   # Only preview actions
param(
    [switch]$DryRun
)

# Define correct folders for file types
$folderMap = @{
    "controller" = "controller"
    "dto"        = "dto"
    "service"    = "service"
    "analyzer"   = "service\analyzer"
    "model"      = "model"
    "repository" = "repository"
    "exception"  = "exception"
    "config"     = "config"
}

# Get all .java files recursively
$files = Get-ChildItem -Recurse -Filter *.java

# Group by file name
$duplicates = $files | Group-Object Name | Where-Object { $_.Count -gt 1 }

foreach ($dup in $duplicates) {
    $fileName = $dup.Name
    $groups = $dup.Group

    # Decide which path to keep
    $keep = $null
    foreach ($folderKey in $folderMap.Keys) {
        $match = $groups | Where-Object { $_.FullName -match "\\$($folderMap[$folderKey])\\" }
        if ($match) {
            $keep = $match[0]
            break
        }
    }

    if (-not $keep) {
        # Default: keep the first one
        $keep = $groups[0]
    }

    Write-Host "Keeping: $($keep.FullName)" -ForegroundColor Green

    # Delete other duplicates
    foreach ($file in $groups) {
        if ($file.FullName -ne $keep.FullName) {
            if ($DryRun) {
                Write-Host "[DryRun] Would remove: $($file.FullName)" -ForegroundColor Yellow
            } else {
                Write-Host "Removing: $($file.FullName)" -ForegroundColor Red
                Remove-Item $file.FullName -Force
            }
        }
    }
}

Write-Host "Duplicate cleanup completed." -ForegroundColor Cyan