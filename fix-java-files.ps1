# Save this as fix-java-files.ps1
# Run from your project root, e.g., A:\MJR\forgery-detection\

$rootDir = "A:\MJR\forgery-detection\src\main\java"

Get-ChildItem -Path $rootDir -Recurse -Filter *.java | ForEach-Object {
    $file = $_

    # Read file as raw bytes to handle BOM
    $bytes = [System.IO.File]::ReadAllBytes($file.FullName)

    # Detect BOM (EF BB BF for UTF-8)
    if ($bytes.Length -ge 3 -and $bytes[0] -eq 0xEF -and $bytes[1] -eq 0xBB -and $bytes[2] -eq 0xBF) {
        Write-Host "Removing BOM from '$($file.FullName)'"
        $bytes = $bytes[3..($bytes.Length - 1)]
        [System.IO.File]::WriteAllBytes($file.FullName, $bytes)
    }

    # Read file content as UTF8 (without BOM)
    $content = Get-Content -LiteralPath $file.FullName -Raw -Encoding UTF8

    if ([string]::IsNullOrWhiteSpace($content)) { return }

    # Match the public class/interface/enum declared in the file
    $match = [regex]::Match($content, 'public\s+(class|interface|enum)\s+([A-Za-z_][A-Za-z0-9_]*)')

    if ($match.Success) {
        $declaredClass = $match.Groups[2].Value
        $currentFileName = $file.BaseName
        $currentExtension = $file.Extension

        # Rename if filename doesn't match class
        if ($currentFileName -ne $declaredClass) {
            $newFilePath = Join-Path $file.DirectoryName ($declaredClass + $currentExtension)
            Write-Host "Renaming '$($file.FullName)' -> '$newFilePath'"
            Rename-Item -LiteralPath $file.FullName -NewName ($declaredClass + $currentExtension) -Force
        }
    } else {
        Write-Warning "No public class/interface/enum found in '$($file.FullName)'"
    }
}