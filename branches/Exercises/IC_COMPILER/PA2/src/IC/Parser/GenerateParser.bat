@ECHO OFF
PUSHD %~dp0

java java_cup.Main -parser LibraryParser -expect 50 Library.cup
PAUSE

java java_cup.Main -parser Parser IC.cup

POPD
PAUSE