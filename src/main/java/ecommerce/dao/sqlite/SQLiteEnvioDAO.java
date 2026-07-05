package ecommerce.dao.sqlite;

import ecommerce.dao.interfaces.EnvioDAO;
import ecommerce.enums.EstadoEnvio;
import ecommerce.enums.TipoEnvio;
import ecommerce.exception.DatabaseException;
import ecommerce.exception.EnvioNoEncontradoException;
import ecommerce.model.Envio;
import ecommerce.model.EnvioHistorialEstado;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SQLiteEnvioDAO extends SQLiteBaseDAO implements EnvioDAO {

    @Override
    public void guardar(Envio envio) {
        String sql = """
                INSERT INTO envios (codigo_seguimiento, direccion, provincia, ciudad, codigo_postal, tipo_envio, estado, costo)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = obtenerConexion();
             PreparedStatement statement = preparar(connection, sql,
                     envio.getCodigoSeguimiento(),
                     envio.getDireccion(),
                     envio.getProvincia(),
                     envio.getCiudad(),
                     envio.getCodigoPostal(),
                     envio.getTipoEnvio(),
                     envio.getEstado(),
                     envio.getCosto())) {

            statement.executeUpdate();
        } catch (SQLException ex) {
            throw new DatabaseException("No se pudo guardar el envío.", ex);
        }
    }

    @Override
    public Envio buscarPorCodigoSeguimiento(String codigoSeguimiento) throws EnvioNoEncontradoException {
        String sql = "SELECT * FROM envios WHERE codigo_seguimiento = ?";

        try (Connection connection = obtenerConexion();
             PreparedStatement statement = preparar(connection, sql, codigoSeguimiento.trim().toUpperCase());
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                return mapearEnvio(resultSet);
            }
            throw new EnvioNoEncontradoException("No se encontró un envío con código " + codigoSeguimiento + ".");
        } catch (SQLException ex) {
            throw new DatabaseException("No se pudo buscar el envío.", ex);
        }
    }

    @Override
    public List<Envio> obtenerTodos() {
        String sql = "SELECT * FROM envios ORDER BY codigo_seguimiento";
        return obtenerPorConsulta(sql);
    }

    @Override
    public List<Envio> obtenerPorEstado(EstadoEnvio estado) {
        String sql = "SELECT * FROM envios WHERE estado = ? ORDER BY codigo_seguimiento";
        List<Envio> envios = new ArrayList<>();

        try (Connection connection = obtenerConexion();
             PreparedStatement statement = preparar(connection, sql, estado);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                envios.add(mapearEnvio(resultSet));
            }
            return envios;
        } catch (SQLException ex) {
            throw new DatabaseException("No se pudieron obtener los envíos por estado.", ex);
        }
    }

    @Override
    public void actualizar(Envio envio) throws EnvioNoEncontradoException {
        String sql = """
                UPDATE envios
                SET direccion = ?, provincia = ?, ciudad = ?, codigo_postal = ?, tipo_envio = ?, estado = ?, costo = ?
                WHERE codigo_seguimiento = ?
                """;

        try (Connection connection = obtenerConexion();
             PreparedStatement statement = preparar(connection, sql,
                     envio.getDireccion(),
                     envio.getProvincia(),
                     envio.getCiudad(),
                     envio.getCodigoPostal(),
                     envio.getTipoEnvio(),
                     envio.getEstado(),
                     envio.getCosto(),
                     envio.getCodigoSeguimiento())) {

            int filas = statement.executeUpdate();
            if (filas == 0) {
                throw new EnvioNoEncontradoException("No se encontró el envío a actualizar.");
            }
        } catch (SQLException ex) {
            throw new DatabaseException("No se pudo actualizar el envío.", ex);
        }
    }

    @Override
    public void eliminar(String codigoSeguimiento) throws EnvioNoEncontradoException {
        String sqlHistorial = "DELETE FROM envio_historial_estados WHERE envio_codigo = ?";
        String sqlEnvio = "DELETE FROM envios WHERE codigo_seguimiento = ?";
        String codigo = codigoSeguimiento.trim().toUpperCase();

        try (Connection connection = obtenerConexion()) {
            connection.setAutoCommit(false);

            try (PreparedStatement historialStatement = preparar(connection, sqlHistorial, codigo)) {
                historialStatement.executeUpdate();
            }

            try (PreparedStatement envioStatement = preparar(connection, sqlEnvio, codigo)) {
                int filas = envioStatement.executeUpdate();
                if (filas == 0) {
                    connection.rollback();
                    throw new EnvioNoEncontradoException("No se encontró el envío a eliminar.");
                }
            }

            connection.commit();
        } catch (SQLException ex) {
            throw new DatabaseException("No se pudo eliminar el envío.", ex);
        }
    }

    @Override
    public void registrarHistorial(String codigoSeguimiento, EstadoEnvio estado, String descripcion) {
        String sql = """
                INSERT INTO envio_historial_estados (envio_codigo, estado, fecha, descripcion)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection connection = obtenerConexion();
             PreparedStatement statement = preparar(connection, sql,
                     codigoSeguimiento.trim().toUpperCase(),
                     estado,
                     LocalDateTime.now(),
                     descripcion)) {

            statement.executeUpdate();
        } catch (SQLException ex) {
            throw new DatabaseException("No se pudo registrar el historial del envío.", ex);
        }
    }

    @Override
    public List<EnvioHistorialEstado> obtenerHistorial(String codigoSeguimiento) throws EnvioNoEncontradoException {
        buscarPorCodigoSeguimiento(codigoSeguimiento);

        String sql = """
                SELECT *
                FROM envio_historial_estados
                WHERE envio_codigo = ?
                ORDER BY fecha ASC, id ASC
                """;
        List<EnvioHistorialEstado> historial = new ArrayList<>();

        try (Connection connection = obtenerConexion();
             PreparedStatement statement = preparar(connection, sql, codigoSeguimiento.trim().toUpperCase());
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                historial.add(mapearHistorial(resultSet));
            }
            return historial;
        } catch (SQLException ex) {
            throw new DatabaseException("No se pudo obtener el historial del envío.", ex);
        }
    }

    private List<Envio> obtenerPorConsulta(String sql) {
        List<Envio> envios = new ArrayList<>();

        try (Connection connection = obtenerConexion();
             PreparedStatement statement = preparar(connection, sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                envios.add(mapearEnvio(resultSet));
            }
            return envios;
        } catch (SQLException ex) {
            throw new DatabaseException("No se pudieron obtener los envíos.", ex);
        }
    }

    Envio mapearEnvio(ResultSet resultSet) throws SQLException {
        return new Envio(
                resultSet.getString("codigo_seguimiento"),
                resultSet.getString("direccion"),
                resultSet.getString("provincia"),
                resultSet.getString("ciudad"),
                resultSet.getString("codigo_postal"),
                TipoEnvio.valueOf(resultSet.getString("tipo_envio")),
                EstadoEnvio.valueOf(resultSet.getString("estado")),
                resultSet.getDouble("costo"));
    }

    private EnvioHistorialEstado mapearHistorial(ResultSet resultSet) throws SQLException {
        return new EnvioHistorialEstado(
                resultSet.getInt("id"),
                resultSet.getString("envio_codigo"),
                EstadoEnvio.valueOf(resultSet.getString("estado")),
                LocalDateTime.parse(resultSet.getString("fecha")),
                resultSet.getString("descripcion"));
    }
}
