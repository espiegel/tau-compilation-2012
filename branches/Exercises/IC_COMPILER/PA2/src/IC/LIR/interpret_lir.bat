@ECHO OFF
PUSHD %~dp0

java microLIR.Main .\..\..\..\test\T10.lir

:: .\..\..\..\test\T10.lir
:: .\..\..\..\test\Quicksort.lir
:: C:\microLir\test\test_T10.lir

POPD
PAUSE