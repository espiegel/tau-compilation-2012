@ECHO OFF
PUSHD %~dp0

java java_cup.Main -parser Parser Library.cup

POPD
PAUSE