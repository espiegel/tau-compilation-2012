@ECHO OFF
PUSHD %~dp0
SET test=error_array_null_ref
MOVE .\..\..\..\test\%test%.lir .\..\..\..\test\%test%_.lir
REM javac .\..\Compiler.java 
CD ..\..\..\classes\IC
ECHO %CD%
DIR
java Compiler.Main
REM .\..\..\..\test\%test%.ic -L.\..\..\..\test\libic.sig -dump-symtab -print-lir
java microLIR.Main .\..\..\..\test\%test%.lir
java microLIR.Main .\..\..\..\test\%test%_.lir

:: .\..\..\..\test\T10.lir
:: .\..\..\..\test\Quicksort.lir
:: C:\microLir\test\test_T10.lir

POPD
PAUSE