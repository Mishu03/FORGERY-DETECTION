param(
    [switch]$DryRun = $true   # Set $false to apply changes
)

$root = "A:\MJR\forgery-detection\src\main\java"

Get-ChildItem -Path $root -Recurse -Filter *.java | ForEach-Object {

    $file = $_
    $content = Get-Content -Path $file.FullName -Raw -Encoding UTF8

    # Step 1: Remove BOM if exists
    if ($content.Length -gt 0 -and $content[0] -eq 65279) {
        if ($DryRun) {
            Write-Host "[DRY-RUN] Would remove BOM from '$($file.FullName)'"
        } else {
            $content = $content.Substring(1)
            Set-Content -Path $file.FullName -Value $content -Encoding UTF8
            Write-Host "[INFO] Removed BOM from '$($file.FullName)'"
        }
    }

    # Step 2: Extract package path
    $packageMatch = [regex]::Match($content, '^\s*package\s+([a-zA-Z0-9_.]+);', [System.Text.RegularExpressions.RegexOptions]::Multiline)
    $packagePath = if ($packageMatch.Success) {
        $packageMatch.Groups[1].Value -replace '\.', '\'
    } else { "" }

    # Step 3: Extract public class/interface/enum
    $match = [regex]::Match($content, 'public\s+(class|interface|enum)\s+(\w+)')
    if ($match.Success) {
        $declaredName = $match.Groups[2].Value
        $currentName = $file.BaseName
        $targetDir = if ($packagePath -ne "") { Join-Path $root $packagePath } else { $file.DirectoryName }
        $targetPath = Join-Path -Path $targetDir -ChildPath ($declaredName + ".java")

        if ($file.FullName -ne $targetPath) {
            if ($DryRun) {
                Write-Host "[DRY-RUN] Would move & rename '$($file.FullName)' -> '$targetPath'"
            } else {
                if (!(Test-Path $targetDir)) { New-Item -ItemType Directory -Path $targetDir -Force | Out-Null }
                Move-Item -Path $file.FullName -Destination $targetPath -Force
                Write-Host "[INFO] Moved & renamed '$($file.FullName)' -> '$targetPath'"
            }
        }
    } else {
        Write-Warning "[WARNING] No public class/interface/enum found in '$($file.FullName)'"
    }
}

# Step 4: Detect duplicates by filename
$duplicates = Get-ChildItem -Path $root -Recurse -Filter *.java |
    Group-Object Name |
    Where-Object { $_.Count -gt 1 }

foreach ($dup in $duplicates) {
    Write-Host "[DUPLICATE] Multiple files detected for '$($dup.Name)':"
    $dup.Group | ForEach-Object { Write-Host "    $($_.FullName)" }
    if (-not $DryRun) {
        # Optional: You could add logic here to delete or move duplicates
    }
}

Write-Host "✅ Script completed. DryRun mode: $DryRun"