package ecommerce.database;

import ecommerce.exception.DatabaseException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Centraliza la configuración de la base de datos SQLite.
 */
public final class DatabaseConfig {

    private static final String DEFAULT_DATABASE_PATH = "database/ecommerce.db";
    private static final String DATABASE_PATH_PROPERTY = "ecommerce.db.path";
    private static final String SQLITE_JDBC_PREFIX = "jdbc:sqlite:";

    private DatabaseConfig() {
    }

    public static String obtenerRutaBaseDatos() {
        return System.getProperty(DATABASE_PATH_PROPERTY, DEFAULT_DATABASE_PATH);
    }

    public static String obtenerJdbcUrl() {
        return SQLITE_JDBC_PREFIX + obtenerRutaBaseDatos();
    }

    public static Path obtenerPathBaseDatos() {
        return Path.of(obtenerRutaBaseDatos());
    }

    public static void asegurarDirectorioBaseDatos() {
        Path databasePath = obtenerPathBaseDatos();
        Path parent = databasePath.getParent();

        if (parent == null) {
            return;
        }

        try {
            Files.createDirectories(parent);
        } catch (IOException ex) {
            throw new DatabaseException("No se pudo crear el directorio de la base de datos.", ex);
        }
    }
}
