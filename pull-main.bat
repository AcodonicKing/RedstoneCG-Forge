@echo off
REM Pull root src/main to subfolder's src/main
setlocal
set ROOT_DIR=%~dp0..
set SUB_SRC=%~dp0src\main
set ROOT_SRC=%ROOT_DIR%\src\main

echo Deleting subfolder src/main...
rmdir /S /Q "%SUB_SRC%"

echo Copying from "%ROOT_SRC%" to "%SUB_SRC%"...
xcopy "%ROOT_SRC%" "%SUB_SRC%" /E /I /Y

echo Done.
endlocal