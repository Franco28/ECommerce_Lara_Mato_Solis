# E-Commerce Final - Programación Avanzada

Proyecto Java por etapas para el examen final integrador de Programación Avanzada.

## Etapa 1 - Modelo de dominio

Incluye:

- Enums principales del negocio.
- Interfaces obligatorias y complementarias.
- Clase abstracta `Producto`.
- Subclases `ProductoFisico`, `ProductoDigital` y `ProductoImportado`.
- Entidades principales del ciclo de compra:
  - Usuario
  - Categoria
  - Producto
  - InventarioMovimiento
  - Carrito
  - ItemCarrito
  - OrdenCompra
  - ItemOrden
  - Pago
  - Envio
  - Reclamo
  - Devolucion
  - Calificacion

## Etapa 2 - Excepciones personalizadas y validaciones base

Se agregó el paquete:

```text
src/main/java/ecommerce/exception
```

Excepciones obligatorias incorporadas:

- `ProductoDuplicadoException`
- `ProductoNoEncontradoException`
- `UsuarioNoEncontradoException`
- `StockInsuficienteException`
- `CarritoVacioException`
- `PagoRechazadoException`
- `OrdenNoEncontradaException`
- `EnvioNoEncontradoException`
- `CategoriaNoEncontradaException`
- `PermisoDenegadoException`
- `DatosInvalidosException`

También se agregó:

```text
src/main/java/ecommerce/util/ValidadorDominio.java
```

Ese validador centraliza reglas básicas como:

- campos obligatorios,
- IDs no negativos,
- cantidades mayores a cero,
- precios mayores a cero,
- valores no negativos,
- formato básico de email,
- objetos obligatorios.

Las entidades principales fueron ajustadas para usar excepciones propias del dominio en lugar de depender solamente de excepciones genéricas como `IllegalArgumentException`.

## Etapa 3 - Persistencia SQLite base

Se agregó infraestructura para preparar la persistencia real entre ejecuciones.

Archivos principales:

```text
src/main/java/ecommerce/database/DatabaseConfig.java
src/main/java/ecommerce/database/DatabaseConnection.java
src/main/java/ecommerce/database/DatabaseInitializer.java
src/main/java/ecommerce/database/DatabaseInitializerApp.java
src/main/java/ecommerce/database/SqlScriptRunner.java
src/main/resources/database/schema.sql
```

También se agregó:

```text
src/main/java/ecommerce/exception/DatabaseException.java
database/README.md
database/.gitkeep
```

La Etapa 3 crea el esquema inicial para:

- usuarios,
- categorías,
- productos,
- inventario,
- carrito,
- pagos,
- envíos,
- órdenes,
- reclamos,
- devoluciones,
- calificaciones.

## Decisión técnica

Las excepciones personalizadas extienden de `EcommerceException`, que a su vez extiende de `RuntimeException`.

Esto permite mantener los modelos limpios y evita llenar constructores, setters y métodos simples con firmas `throws`.

Para persistencia se utiliza SQLite porque el enunciado del examen lo menciona como implementación válida. En esta etapa se define la conexión, configuración y creación de tablas; en la siguiente etapa se implementará el patrón DAO completo.

## Compilar el proyecto

Con Java 17:

```powershell
$files = Get-ChildItem -Recurse -Filter *.java | ForEach-Object FullName
javac -d out $files
java -cp out ecommerce.Main
```

## Inicializar SQLite

Para inicializar la base de datos se recomienda Maven, porque el driver SQLite se declara como dependencia en `pom.xml`.

```bash
mvn clean compile exec:java -Dexec.mainClass=ecommerce.database.DatabaseInitializerApp
```

Eso genera el archivo:

```text
database/ecommerce.db
```

## Ejecutar con Maven

```bash
mvn clean compile exec:java
```

## Etapa 4 - DAO y Factory

La Etapa 4 agrega el patrón DAO sobre la infraestructura SQLite.

Paquetes principales:

```text
src/main/java/ecommerce/dao/interfaces
src/main/java/ecommerce/dao/sqlite
src/main/java/ecommerce/dao/factory
```

Interfaces DAO incluidas:

```text
UsuarioDAO
CategoriaDAO
ProductoDAO
InventarioDAO
PagoDAO
EnvioDAO
OrdenDAO
ReclamoDAO
```

Implementaciones SQLite incluidas:

```text
SQLiteUsuarioDAO
SQLiteCategoriaDAO
SQLiteProductoDAO
SQLiteInventarioDAO
SQLitePagoDAO
SQLiteEnvioDAO
SQLiteOrdenDAO
SQLiteReclamoDAO
```

Factory incluida:

```text
DAOFactory
SQLiteDAOFactory
```

Ejemplo de uso:

```java
DAOFactory factory = DAOFactory.obtenerFactory();
ProductoDAO productoDAO = factory.crearProductoDAO();
```
