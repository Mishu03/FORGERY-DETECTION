# -------------------------
# Step 1: Fix file encoding
# -------------------------
Write-Output "Fixing .java file encoding (UTF-8 without BOM)..."
Get-ChildItem -Recurse -Filter *.java | ForEach-Object {
    $file = $_.FullName
    $content = Get-Content $file -Raw
    Set-Content $file $content -Encoding utf8
}

# -------------------------
# Step 2: Detect folders with trailing spaces
# -------------------------
Write-Output "Checking for directories with trailing spaces..."
Get-ChildItem -Recurse -Directory | ForEach-Object {
    if ($_.Name -match "\s$") {
        Write-Warning "Folder with trailing space found: '$($_.FullName)'"
        # Optionally, remove trailing space (uncomment next line to auto-fix)
        # Rename-Item $_.FullName ($_.FullName.TrimEnd())
    }
}

# -------------------------
# Step 3: Clean Maven target
# -------------------------
Write-Output "Removing target directory..."
if (Test-Path ".\target") {
    Remove-Item -Recurse -Force ".\target"
}

# -------------------------
# Step 4: Run Maven clean and install
# -------------------------
Write-Output "Running 'mvn clean install'..."
mvn clean install