@rem
@rem Copyright 2015 the original author or authors.
@rem
@rem Licensed under the Apache License, Version 2.0 (the "License");
@rem you may not use this file except in compliance with the License.
@rem You may obtain a copy of the License at
@rem
@rem      https://www.apache.org/licenses/LICENSE-2.0
@rem
@rem Unless required by applicable law or agreed to in writing, software
@rem distributed under the License is distributed on an "AS IS" BASIS,
@rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@rem See the License for the specific language governing permissions and
@rem limitations under the License.
@rem

@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  ItemFinder startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Resolve any "." and ".." in APP_HOME to make it shorter.
for %%i in ("%APP_HOME%") do set APP_HOME=%%~fi

@rem Add default JVM options here. You can also use JAVA_OPTS and ITEM_FINDER_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto execute

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto execute

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\ItemFinder-1.0-SNAPSHOT.jar;%APP_HOME%\lib\SEED-master-SNAPSHOT.jar;%APP_HOME%\lib\FeatureUtils-58bf7a94026ffc5c691605c953ef0b25a26ca471.jar;%APP_HOME%\lib\TerrainUtils-605c46b94a5126df55bd347b82f277af443909c9.jar;%APP_HOME%\lib\BiomeUtils-b2065a0281342f269af31da9ef7349ffdc337534.jar;%APP_HOME%\lib\NoiseUtils-2cf64e1d2e7e674fbf5b7247f16e8dc56ae2a31c.jar;%APP_HOME%\lib\ChunkRandomReversal-6b76fb5cf2cd438de56e6a46cea2a83985831834.jar;%APP_HOME%\lib\MCUtils-1e5785a648a04454461ef02ee27cbc63e3599bff.jar;%APP_HOME%\lib\LattiCG-ec805b20bda95ead23216ab249a5ef5b8587e9e9.jar;%APP_HOME%\lib\SeedUtils-b6a383113ce5d8d09a59e91b28ff064fb97c0709.jar;%APP_HOME%\lib\MathUtils-5531c4a87b0f1bb85d1dab2bdd18ce375400626a.jar;%APP_HOME%\lib\kotlin-stdlib-jdk8-1.5.0-RC.jar;%APP_HOME%\lib\gzip-0.1.10.jar;%APP_HOME%\lib\kotlin-stdlib-jdk7-1.5.0-RC.jar;%APP_HOME%\lib\kotlin-stdlib-1.5.0-RC.jar;%APP_HOME%\lib\deflate-0.1.10.jar;%APP_HOME%\lib\annotations-13.0.jar;%APP_HOME%\lib\kotlin-stdlib-common-1.5.0-RC.jar;%APP_HOME%\lib\core-0.20.0.jar


@rem Execute ItemFinder
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %ITEM_FINDER_OPTS%  -classpath "%CLASSPATH%" and_penguin.Main %*

:end
@rem End local scope for the variables with windows NT shell
if "%ERRORLEVEL%"=="0" goto mainEnd

:fail
rem Set variable ITEM_FINDER_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
if  not "" == "%ITEM_FINDER_EXIT_CONSOLE%" exit 1
exit /b 1

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
