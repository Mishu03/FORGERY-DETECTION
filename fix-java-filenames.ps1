# Save this as fix-java-filenames.ps1 and run it from your project root
# Example: .\fix-java-filenames.ps1

$rootDir = "A:\MJR\forgery-detection\src\main\java"

# Get all Java files recursively
Get-ChildItem -Path $rootDir -Recurse -Filter *.java | ForEach-Object {
    $file = $_
    $content = Get-Content -LiteralPath $file.FullName -Raw

    # Skip empty files
    if ([string]::IsNullOrWhiteSpace($content)) { return }

    # Match the public class or interface declared in the file
    $match = [regex]::Match($content, 'public\s+(class|interface|enum)\s+([A-Za-z_][A-Za-z0-9_]*)')

    if ($match.Success) {
        $declaredClass = $match.Groups[2].Value
        $currentFileName = $file.BaseName
        $currentExtension = $file.Extension

        # If filename does not match declared class, rename it
        if ($currentFileName -ne $declaredClass) {
            $newFilePath = Join-Path $file.DirectoryName ($declaredClass + $currentExtension)
            Write-Host "Renaming '$($file.FullName)' to '$newFilePath'"
            Rename-Item -LiteralPath $file.FullName -NewName ($declaredClass + $currentExtension) -Force
        }
    } else {
        Write-Warning "No public class/interface/enum found in '$($file.FullName)'"
    }
}