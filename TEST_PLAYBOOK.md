# Version Lifecycle Test Playbook

## Running Full E2E Tests (Reproducible Steps)

### Prerequisites
- Application running on `http://localhost:8080`
- Test plan with ID `1` exists in database
- Directories for test files: `C:\temp\`

---

## Test 1: Single PDF Generation & Version Creation

### Command
```powershell
# Generate PDF for test plan 1
$pdf = Invoke-RestMethod -Uri "http://localhost:8080/api/testplans/1/document/pdf" `
    -Method Get `
    -OutFile "C:\temp\document-v1.pdf"

Write-Host "✅ PDF generated"
```

### Expected Result
- HTTP 200 response
- File saved to `C:\temp\document-v1.pdf`
- File size: ~1,300-1,500 bytes
- File starts with magic number: `%PDF`

### Verification
```powershell
# Check version was created
$versions = Invoke-RestMethod -Uri "http://localhost:8080/versions/1" -Method Get
$versions | ConvertTo-Json | Write-Host

# Expected output includes:
# - "id": 1
# - "versionNumber": 1
# - "format": "PDF"
# - "fileContent": (base64 encoded data)
```

---

## Test 2: Version Increment (Second Generation)

### Command
```powershell
# Generate second PDF (should create version 2)
$pdf2 = Invoke-RestMethod -Uri "http://localhost:8080/api/testplans/1/document/pdf" `
    -Method Get `
    -OutFile "C:\temp\document-v2.pdf"

# Verify version 2 exists
Start-Sleep -Seconds 1
$versions = Invoke-RestMethod -Uri "http://localhost:8080/versions/1" -Method Get

# Count versions
Write-Host "Total versions: $($versions | Measure-Object).Count"
```

### Expected Result
- Version 2 created with `versionNumber: 2`
- Version 1 still exists with `versionNumber: 1`
- Total versions: 2

---

## Test 3: Download Endpoint

### Command
```powershell
# Download version 1
$r1 = Invoke-WebRequest -Uri "http://localhost:8080/versions/download/1" `
    -Method Get `
    -OutFile "C:\temp\downloaded-v1.pdf" `
    -PassThru `
    -UseBasicParsing

Write-Host "Status: $($r1.StatusCode)"
Write-Host "Content-Type: $($r1.Headers['Content-Type'])"
Write-Host "Content-Disposition: $($r1.Headers['Content-Disposition'])"
```

### Expected Result
- HTTP Status: 200
- Content-Type: `application/pdf`
- Content-Disposition: `attachment; filename="document-v1.pdf"`
- File downloads successfully

---

## Test 4: Multiple Formats

### Command
```powershell
# Generate Word document
$word = Invoke-WebRequest -Uri "http://localhost:8080/api/testplans/1/document/word" `
    -Method Get `
    -OutFile "C:\temp\document-word.docx" `
    -PassThru `
    -UseBasicParsing

Write-Host "Word: $($word.StatusCode) | $($word.Headers['Content-Type'])"

# Generate Excel spreadsheet
$excel = Invoke-WebRequest -Uri "http://localhost:8080/api/testplans/1/document/excel" `
    -Method Get `
    -OutFile "C:\temp\document-excel.xlsx" `
    -PassThru `
    -UseBasicParsing

Write-Host "Excel: $($excel.StatusCode) | $($excel.Headers['Content-Type'])"

# Generate HTML
$html = Invoke-WebRequest -Uri "http://localhost:8080/api/testplans/1/document/html" `
    -Method Get `
    -UseBasicParsing

Write-Host "HTML: $($html.StatusCode) | $($html.Content.Length) bytes"
```

### Expected Results

| Format | Content-Type | Extension | Magic Signature |
|--------|--------------|-----------|-----------------|
| PDF | `application/pdf` | .pdf | `25 50 44 46` |
| Word | `application/vnd.openxmlformats-officedocument.wordprocessingml.document` | .docx | `50 4B 03 04` |
| Excel | `application/vnd.openxmlformats-officedocument.spreadsheetml.sheet` | .xlsx | `50 4B 03 04` |
| HTML | `text/html` | .html | (UTF-8 text) |

---

## Test 5: File Validity Check

### Command
```powershell
function Test-FileSignature {
    param($FilePath, $ExpectedSig, $Type)
    
    $bytes = [System.IO.File]::ReadAllBytes($FilePath) | Select-Object -First 4
    $hex = ($bytes | % { $_.ToString("X2") }) -join " "
    
    Write-Host "$Type | Signature: $hex | Expected: $ExpectedSig | $(
        if ($hex -eq $ExpectedSig) { '✅' } else { '❌' }
    )"
}

# Test each file
Test-FileSignature "C:\temp\v1-test.pdf" "25 50 44 46" "PDF"
Test-FileSignature "C:\temp\document-word.docx" "50 4B 03 04" "DOCX"
Test-FileSignature "C:\temp\document-excel.xlsx" "50 4B 03 04" "XLSX"
```

### Expected Result
- All files have correct magic numbers (file signatures)
- Files are valid and not corrupted

---

## Test 6: Version Persistence Check

### Command
```powershell
# Get all versions for test plan 1
$allVersions = Invoke-RestMethod -Uri "http://localhost:8080/versions/1" -Method Get

Write-Host "Total Versions: $($allVersions | Measure-Object).Count"
$allVersions | ForEach-Object {
    Write-Host "  V$($_.versionNumber): $($_.format) (ID: $($_.id), Size: $($_.fileContent.Length) bytes)"
}
```

### Expected Result
- All created versions still exist
- No versions lost or corrupted
- Proper version numbering sequence

---

## Test 7: Database Schema Validation

### Command
```powershell
# Check H2 console
Write-Host "Open http://localhost:8080/h2-console"
Write-Host "Database: docgen-db"
Write-Host "Username: sa"
Write-Host "Password: (empty)"
Write-Host ""
Write-Host "Run SQL:"
Write-Host "SELECT * FROM DOCUMENT_VERSION;"
```

### Expected Results
```
| ID | TEST_PLAN_ID | VERSION_NUMBER | FORMAT | GENERATED_AT | FILE_CONTENT | CONFIG_SNAPSHOT |
|----|--------------|----------------|--------|--------------|--------------|-----------------|
| 1  | 1            | 1              | PDF    | <timestamp>  | <binary>     | <json>          |
| 2  | 1            | 2              | PDF    | <timestamp>  | <binary>     | <json>          |
| 3  | 1            | 3              | WORD   | <timestamp>  | <binary>     | <json>          |
| 4  | 1            | 4              | EXCEL  | <timestamp>  | <binary>     | <json>          |
| 5  | 1            | 5              | HTML   | <timestamp>  | <binary>     | <json>          |
```

- FILE_CONTENT column: LONGBLOB (supports up to 2GB)
- CONFIG_SNAPSHOT column: TEXT/CLOB
- All data persisted correctly

---

## Test 8: Content-Type Verification

### Command
```powershell
# Create test function
function Test-ContentType {
    param($Id, $ExpectedType)
    
    $h = Invoke-WebRequest -Uri "http://localhost:8080/versions/download/$Id" `
        -Method Get `
        -OutFile "C:\temp\ct-test-$Id" `
        -PassThru `
        -UseBasicParsing
    
    $actual = $h.Headers['Content-Type']
    $match = if ($actual -eq $ExpectedType) { "✅" } else { "❌" }
    
    Write-Host "V$Id: $match Expected: $ExpectedType | Got: $actual"
}

# Test each version
Test-ContentType 1 "application/pdf"
Test-ContentType 2 "application/pdf"
Test-ContentType 3 "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
Test-ContentType 4 "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
Test-ContentType 5 "text/html"
```

### Expected Result
- All Content-Type headers correct
- Browsers will handle downloads properly
- Files open with correct applications

---

## Automated Test Script

Save as `E2E_Test.ps1`:

```powershell
#!/usr/bin/env pwsh

param(
    [string]$BaseUrl = "http://localhost:8080",
    [int]$TestPlanId = 1,
    [string]$OutputDir = "C:\temp\e2e-tests"
)

# Create output directory
if (-not (Test-Path $OutputDir)) {
    New-Item -ItemType Directory -Path $OutputDir | Out-Null
}

Write-Host "╔════════════════════════════════════════╗" -ForegroundColor Cyan
Write-Host "║   VERSION LIFECYCLE E2E TEST SUITE    ║" -ForegroundColor Cyan
Write-Host "╚════════════════════════════════════════╝" -ForegroundColor Cyan
Write-Host ""
Write-Host "Base URL: $BaseUrl"
Write-Host "Test Plan ID: $TestPlanId"
Write-Host "Output Dir: $OutputDir"
Write-Host ""

$passed = 0
$failed = 0

# Test 1: PDF Generation
Write-Host "Test 1: PDF Generation..." -ForegroundColor Yellow
try {
    $null = Invoke-RestMethod -Uri "$BaseUrl/api/testplans/$TestPlanId/document/pdf" `
        -Method Get `
        -OutFile "$OutputDir\document-v1.pdf" `
        -ErrorAction Stop
    Write-Host "  ✅ PASSED" -ForegroundColor Green
    $passed++
} catch {
    Write-Host "  ❌ FAILED: $_" -ForegroundColor Red
    $failed++
}

# Test 2: Version Creation
Write-Host "Test 2: Version Creation..." -ForegroundColor Yellow
try {
    $versions = Invoke-RestMethod -Uri "$BaseUrl/versions/$TestPlanId" -Method Get -ErrorAction Stop
    if ($versions.versionNumber -gt 0) {
        Write-Host "  ✅ PASSED (Version: $($versions.versionNumber))" -ForegroundColor Green
        $passed++
    } else {
        throw "No version created"
    }
} catch {
    Write-Host "  ❌ FAILED: $_" -ForegroundColor Red
    $failed++
}

# Test 3: Download Endpoint
Write-Host "Test 3: Download Endpoint..." -ForegroundColor Yellow
try {
    $h = Invoke-WebRequest -Uri "$BaseUrl/versions/download/1" `
        -Method Get `
        -OutFile "$OutputDir\download-v1.pdf" `
        -PassThru `
        -UseBasicParsing `
        -ErrorAction Stop
    
    if ($h.StatusCode -eq 200) {
        Write-Host "  ✅ PASSED (HTTP 200)" -ForegroundColor Green
        $passed++
    } else {
        throw "HTTP $($h.StatusCode)"
    }
} catch {
    Write-Host "  ❌ FAILED: $_" -ForegroundColor Red
    $failed++
}

# Test 4: File Signature
Write-Host "Test 4: File Signature Validation..." -ForegroundColor Yellow
try {
    $bytes = [System.IO.File]::ReadAllBytes("$OutputDir\download-v1.pdf") | Select-Object -First 4
    $hex = ($bytes | % { $_.ToString("X2") }) -join " "
    
    if ($hex -eq "25 50 44 46") {
        Write-Host "  ✅ PASSED (Valid PDF: $hex)" -ForegroundColor Green
        $passed++
    } else {
        throw "Invalid signature: $hex"
    }
} catch {
    Write-Host "  ❌ FAILED: $_" -ForegroundColor Red
    $failed++
}

Write-Host ""
Write-Host "╔════════════════════════════════════════╗" -ForegroundColor Cyan
Write-Host "║          RESULTS SUMMARY              ║" -ForegroundColor Cyan
Write-Host "╚════════════════════════════════════════╝" -ForegroundColor Cyan
Write-Host "Passed: $passed"
Write-Host "Failed: $failed"
Write-Host "Success Rate: $([Math]::Round(($passed / ($passed + $failed)) * 100))%"

if ($failed -eq 0) {
    Write-Host ""
    Write-Host "🎉 ALL TESTS PASSED!" -ForegroundColor Green
    exit 0
} else {
    Write-Host ""
    Write-Host "⚠️  SOME TESTS FAILED" -ForegroundColor Red
    exit 1
}
```

### Usage
```powershell
.\E2E_Test.ps1
```

---

## Troubleshooting

| Issue | Solution |
|-------|----------|
| HTTP 404 on generation | Check application is running on port 8080 |
| HTTP 500 on generation | Check application logs for service errors |
| Download returns wrong file | Verify version ID exists in database |
| File signature invalid | Check file isn't corrupted during transfer |
| Version number not incrementing | Check database connection and transaction handling |
| Memory errors | Check available RAM, files might be too large |

---

## Performance Baselines

Expected performance (development machine):
- Document generation: 200-500ms
- Database insert: 50-100ms
- File download: <50ms
- Total E2E time: <1 second

If slower, check:
1. Database performance
2. File size
3. Network latency
4. CPU/memory availability

---

## Notes for Future Tests

When adding new features:

1. **Update this playbook** with new test cases
2. **Run full E2E suite** before committing
3. **Check file integrity** for any new formats
4. **Verify Content-Types** for any new endpoints
5. **Monitor memory usage** if file sizes increase

---

## Related Documentation

- See `E2E_TEST_RESULTS.md` for detailed results
- See `QUICKREF_TEST_RESULTS.md` for quick reference
- Check `ARCHITECTURE.md` for system design
- Review `HELP.md` for more information

