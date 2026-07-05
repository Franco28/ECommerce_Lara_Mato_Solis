package ecommerce.dao.sqlite;

import ecommerce.dao.interfaces.EnvioDAO;
import ecommerce.enums.EstadoEnvio;
import ecommerce.enums.TipoEnvio;
import ecommerce.exception.DatabaseException;
import ecommerce.exception.EnvioNoEncontradoException;
import ecommerce.model.Envio;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
        String sql = "DELETE FROM envios WHERE codigo_seguimiento = ?";

        try (Connection connection = obtenerConexion();
             PreparedStatement statement = preparar(connection, sql, codigoSeguimiento.trim().toUpperCase())) {

            int filas = statement.executeUpdate();
            if (filas == 0) {
                throw new EnvioNoEncontradoException("No se encontró el envío a eliminar.");
            }
        } catch (SQLException ex) {
            throw new DatabaseException("No se pudo eliminar el envío.", ex);
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
}
