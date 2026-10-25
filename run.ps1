param([switch]$Test, [switch]$BuildOnly)

$ErrorActionPreference = 'Stop'
Set-Location -LiteralPath $PSScriptRoot

# Requires a JDK 17+. No Maven installation is needed for this launcher.
$compiler = Get-Command javac -ErrorAction SilentlyContinue
if ($env:JAVA_HOME -and (Test-Path (Join-Path $env:JAVA_HOME 'bin/javac.exe')) -and (Test-Path (Join-Path $env:JAVA_HOME 'bin/keytool.exe'))) {
    $jdkBin = Join-Path $env:JAVA_HOME 'bin'
} elseif ($compiler -and (Test-Path (Join-Path (Split-Path $compiler.Source) 'keytool.exe'))) {
    $jdkBin = Split-Path $compiler.Source
} else {
    $installed = Get-ChildItem "$env:ProgramFiles/Java" -Directory -Filter 'jdk*' -ErrorAction SilentlyContinue |
        Where-Object { Test-Path (Join-Path $_.FullName 'bin/javac.exe') } |
        Sort-Object LastWriteTime -Descending | Select-Object -First 1
    if (-not $installed) { throw 'Install JDK 17+ and set JAVA_HOME, then run this script again.' }
    $jdkBin = Join-Path $installed.FullName 'bin'
}
$javac = Join-Path $jdkBin 'javac.exe'
$java = Join-Path $jdkBin 'java.exe'
$keytool = Join-Path $jdkBin 'keytool.exe'

# Generate a new local lab key; private keys are never downloaded from Git.
$tls = Join-Path $PSScriptRoot 'src/main/resources/ssl'
New-Item -ItemType Directory -Force -Path $tls | Out-Null
$key = Join-Path $tls 'server.keystore'
$trust = Join-Path $tls 'client.truststore'
$cert = Join-Path $tls 'generated-server.cer'
if (-not (Test-Path $key)) {
    if (Test-Path $trust) { throw 'A truststore exists without a server key. Back up your TLS files before regenerating them.' }
    & $keytool -genkeypair -alias filesync-server -keyalg RSA -keysize 2048 -validity 365 -storetype PKCS12 -keystore $key -storepass filesync14 -keypass filesync14 -dname 'CN=localhost,OU=Lab,O=Nhom14,C=VN' -ext 'SAN=dns:localhost,ip:127.0.0.1' -noprompt
    if ($LASTEXITCODE -ne 0) { throw 'Could not generate the local TLS key.' }
}
if (-not (Test-Path $trust)) {
    & $keytool -exportcert -alias filesync-server -keystore $key -storepass filesync14 -file $cert
    if ($LASTEXITCODE -ne 0) { throw 'Could not export the lab certificate.' }
    & $keytool -importcert -alias filesync-server -storetype PKCS12 -keystore $trust -storepass filesync14 -file $cert -noprompt
    if ($LASTEXITCODE -ne 0) { throw 'Could not generate the local truststore.' }
}

$classes = Join-Path $PSScriptRoot 'target/launcher-classes'
New-Item -ItemType Directory -Force -Path $classes | Out-Null
$sources = @(Get-ChildItem 'src/main/java','src/test/java' -Recurse -Filter '*.java' | ForEach-Object { $_.FullName })
& $javac --release 17 -encoding UTF-8 -d $classes @sources
if ($LASTEXITCODE -ne 0) { throw 'Java compilation failed.' }
$classpath = $classes + ';' + (Join-Path $PSScriptRoot 'src/main/resources')
if ($BuildOnly) { Write-Host 'Build successful.'; exit 0 }
if ($Test) {
    # TestRunner deletes ./test_env; isolate it from any user project data.
    $testRoot = Join-Path ([IO.Path]::GetTempPath()) ('filesync-test-' + [Guid]::NewGuid().ToString('N'))
    New-Item -ItemType Directory -Path $testRoot | Out-Null
    Push-Location -LiteralPath $testRoot
    try {
        & $java '-Dfile.encoding=UTF-8' -cp $classpath com.nhom14.filesync.test.TestRunner
        $result = $LASTEXITCODE
    } finally { Pop-Location }
    exit $result
}
& $java '-Dfile.encoding=UTF-8' -cp $classpath com.nhom14.filesync.Main
exit $LASTEXITCODE
