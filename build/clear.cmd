@echo off

call ".\env.cmd"

rmdir /s /q %WRK_SPASE%
rmdir /s /q %WRK_SPASE_%

echo cleared.