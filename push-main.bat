echo off
REM Push subfolder's src/main to root src/main
setlocal
set ROOT_DIR=%~dp0..
set SUB_SRC=%~dp0src\main
set ROOT_SRC=%ROOT_DIR%\src\main

echo Deleting root src/main...
rmdir /S /Q "%ROOT_SRC%"

echo Copying from "%SUB_SRC%" to "%ROOT_SRC%"...
xcopy "%SUB_SRC%" "%ROOT_SRC%" /E /I /Y

echo Done.
endlocal