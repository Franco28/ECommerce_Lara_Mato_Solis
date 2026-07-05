package ecommerce.database;

import ecommerce.exception.DatabaseException;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

/**
 * Ejecuta scripts SQL simples separados por punto y coma.
 * Es suficiente para el esquema inicial del proyecto.
 */
final class SqlScriptRunner {

    private SqlScriptRunner() {
    }

    static void ejecutar(Connection connection, String scriptSql) {
        if (scriptSql == null || scriptSql.isBlank()) {
            throw new DatabaseException("El script SQL está vacío.");
        }

        Arrays.stream(scriptSql.split(";"))
                .map(String::trim)
                .filter(sentencia -> !sentencia.isBlank())
                .forEach(sentencia -> ejecutarSentencia(connection, sentencia));
    }

    private static void ejecutarSentencia(Connection connection, String sentencia) {
        try (Statement statement = connection.createStatement()) {
            statement.execute(sentencia);
        } catch (SQLException ex) {
            throw new DatabaseException("Error al ejecutar sentencia SQL: " + sentencia, ex);
        }
    }
}
