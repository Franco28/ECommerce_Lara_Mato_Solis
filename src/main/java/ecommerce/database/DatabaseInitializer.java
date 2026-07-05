package ecommerce.database;

import ecommerce.exception.DatabaseException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;

/**
 * Inicializa la base de datos creando las tablas necesarias si no existen.
 */
public final class DatabaseInitializer {

    private static final String SCHEMA_RESOURCE = "/database/schema.sql";
    private static final Path SCHEMA_SOURCE_PATH = Path.of("src/main/resources/database/schema.sql");

    private DatabaseInitializer() {
    }

    public static void inicializarBaseDatos() {
        String schemaSql = cargarSchemaSql();

        try (Connection connection = DatabaseConnection.obtenerConexion()) {
            SqlScriptRunner.ejecutar(connection, schemaSql);
        } catch (DatabaseException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new DatabaseException("No se pudo inicializar la base de datos.", ex);
        }
    }

    private static String cargarSchemaSql() {
        try (InputStream inputStream = DatabaseInitializer.class.getResourceAsStream(SCHEMA_RESOURCE)) {
            if (inputStream != null) {
                return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            }

            if (Files.exists(SCHEMA_SOURCE_PATH)) {
                return Files.readString(SCHEMA_SOURCE_PATH, StandardCharsets.UTF_8);
            }

            throw new DatabaseException("No se encontró el archivo schema.sql.");
        } catch (IOException ex) {
            throw new DatabaseException("No se pudo leer el archivo schema.sql.", ex);
        }
    }
}
