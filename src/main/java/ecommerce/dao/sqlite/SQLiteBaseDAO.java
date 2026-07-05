package ecommerce.dao.sqlite;

import ecommerce.database.DatabaseConnection;
import ecommerce.exception.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;

abstract class SQLiteBaseDAO {

    protected Connection obtenerConexion() {
        return DatabaseConnection.obtenerConexion();
    }

    protected PreparedStatement preparar(Connection connection, String sql, Object... parametros) {
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            cargarParametros(statement, parametros);
            return statement;
        } catch (SQLException ex) {
            throw new DatabaseException("No se pudo preparar la sentencia SQL.", ex);
        }
    }

    protected PreparedStatement prepararConGeneratedKeys(Connection connection, String sql, Object... parametros) {
        try {
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            cargarParametros(statement, parametros);
            return statement;
        } catch (SQLException ex) {
            throw new DatabaseException("No se pudo preparar la sentencia SQL.", ex);
        }
    }

    protected int obtenerIdGenerado(PreparedStatement statement) {
        try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            }
            throw new DatabaseException("No se pudo obtener el ID generado.");
        } catch (SQLException ex) {
            throw new DatabaseException("No se pudo leer el ID generado.", ex);
        }
    }

    protected void validarFilasAfectadas(int filas, String mensajeSiNoAfecto) {
        if (filas == 0) {
            throw new DatabaseException(mensajeSiNoAfecto);
        }
    }

    protected LocalDate leerLocalDate(ResultSet resultSet, String columna) throws SQLException {
        String valor = resultSet.getString(columna);
        return valor == null ? null : LocalDate.parse(valor);
    }

    protected LocalDateTime leerLocalDateTime(ResultSet resultSet, String columna) throws SQLException {
        String valor = resultSet.getString(columna);
        return valor == null ? null : LocalDateTime.parse(valor);
    }

    protected String textoEnum(Enum<?> valor) {
        return valor == null ? null : valor.name();
    }

    private void cargarParametros(PreparedStatement statement, Object... parametros) throws SQLException {
        for (int i = 0; i < parametros.length; i++) {
            Object parametro = parametros[i];
            int indice = i + 1;

            if (parametro instanceof LocalDate localDate) {
                statement.setString(indice, localDate.toString());
            } else if (parametro instanceof LocalDateTime localDateTime) {
                statement.setString(indice, localDateTime.toString());
            } else if (parametro instanceof Enum<?> enumValue) {
                statement.setString(indice, enumValue.name());
            } else if (parametro instanceof Integer intValue) {
                statement.setInt(indice, intValue);
            } else if (parametro instanceof Double doubleValue) {
                statement.setDouble(indice, doubleValue);
            } else if (parametro == null) {
                statement.setObject(indice, null);
            } else {
                statement.setObject(indice, parametro);
            }
        }
    }
}
