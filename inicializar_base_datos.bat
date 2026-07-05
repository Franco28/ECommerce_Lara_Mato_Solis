@echo off
where mvn >nul 2>nul
if errorlevel 1 (
    echo Maven no esta instalado o no esta agregado al PATH.
    echo Para inicializar SQLite, instala Maven y ejecuta:
    echo mvn clean compile exec:java -Dexec.mainClass=ecommerce.database.DatabaseInitializerApp
    exit /b 1
)

mvn clean compile exec:java -Dexec.mainClass=ecommerce.database.DatabaseInitializerApp
exit /b %ERRORLEVEL%
