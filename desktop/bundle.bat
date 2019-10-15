@echo off
SETLOCAL

set DIR=%~dp0
set JPACKAGE_BINARYNAME="openjdk-14-jpackage+1-49_windows-x64_bin.zip"

set RELEASE_DESTINATIONPATH="%DIR%build\releases"

set PROJECT_NAME=AO-Java
set PROJECT_VENDOR=AO-Libre
set PROJECT_VERSION=0.1.12
set PROJECT_JARSDIR="%DIR%build\libs"
set PROJECT_ICON="%DIR%assets\data\icons\ao.ico"

set PROJECT_JREDIR="%DIR%jre"

REM We download the JPackage binary if wxists.
IF /I NOT EXIST "%DIR%%JPACKAGE_BINARYNAME%" wget.exe "https://download.java.net/java/early_access/jpackage/1/%JPACKAGE_BINARYNAME%"

REM We unZip the downloaded JPackage binary.
IF NOT EXIST "%DIR%jdk-14" (
	IF EXIST "C:\Program Files\WinRAR\WinRAR.exe" (
		"C:\Program Files\WinRAR\WinRAR.exe" x "%DIR%%JPACKAGE_BINARYNAME%" %DIR%
	) ELSE IF EXIST "C:\Program Files(x86)\WinRAR\WinRAR.exe" (
		"C:\Program Files(x86)\WinRAR\WinRAR.exe" x "%DIR%%JPACKAGE_BINARYNAME%" %DIR%
	)
)

REM Overwrite 'jre' dir if it alredy exists.
rmdir /S /Q %PROJECT_JREDIR%

REM Create an specific JRE for this proyect. Aprox. 30-40 MB.
jlink --module-path C:\Java\jmods --add-modules java.base,java.desktop,jdk.unsupported,java.logging --strip-debug --no-header-files --no-man-pages --strip-native-commands --vm=server --compress=2 --output %PROJECT_JREDIR%

REM Debloat even more the generated JRE.
cd %PROJECT_JREDIR%
rmdir /S /Q conf
rmdir /S /Q legal
cd bin
del "api*.dll"
cd %DIR%

REM Overwrite previous generated release.
rmdir /S /Q %RELEASE_DESTINATIONPATH%

"jdk-14\bin\jpackage.exe" --package-type app-image --name %PROJECT_NAME% --vendor %PROJECT_VENDOR% --app-version %PROJECT_VERSION% --dest %RELEASE_DESTINATIONPATH% --runtime-image %PROJECT_JREDIR% --input %PROJECT_JARSDIR% --main-class launcher.DesktopLauncher --main-jar desktop-%PROJECT_VERSION%.jar --icon %PROJECT_ICON%

REM Copy all resources in ${buildDir}/assets to the release destination path.
xcopy  /E /I /Y /Q "%DIR%assets" %RELEASE_DESTINATIONPATH%\%PROJECT_NAME%
copy "%DIR%Config.json" %RELEASE_DESTINATIONPATH%\%PROJECT_NAME%