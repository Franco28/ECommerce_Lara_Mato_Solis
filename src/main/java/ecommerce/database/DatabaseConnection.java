package ecommerce.database;

import ecommerce.exception.DatabaseException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Provee conexiones JDBC contra SQLite.
 *
 * Cada conexión activa las claves foráneas porque SQLite no las aplica
 * automáticamente si no se habilita PRAGMA foreign_keys = ON.
 */
public final class DatabaseConnection {

    private DatabaseConnection() {
    }

    public static Connection obtenerConexion() {
        DatabaseConfig.asegurarDirectorioBaseDatos();

        try {
            Connection connection = DriverManager.getConnection(DatabaseConfig.obtenerJdbcUrl());
            habilitarClavesForaneas(connection);
            return connection;
        } catch (SQLException ex) {
            throw new DatabaseException("No se pudo abrir la conexión con SQLite.", ex);
        }
    }

    public static boolean probarConexion() {
        try (Connection ignored = obtenerConexion()) {
            return true;
        } catch (SQLException | DatabaseException ex) {
            return false;
        }
    }

    private static void habilitarClavesForaneas(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
        }
    }
}
