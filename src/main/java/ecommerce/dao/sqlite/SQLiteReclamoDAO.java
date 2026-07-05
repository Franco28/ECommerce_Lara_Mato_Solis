package ecommerce.dao.sqlite;

import ecommerce.dao.interfaces.ReclamoDAO;
import ecommerce.enums.EstadoReclamo;
import ecommerce.exception.DatabaseException;
import ecommerce.exception.OrdenNoEncontradaException;
import ecommerce.exception.UsuarioNoEncontradoException;
import ecommerce.model.OrdenCompra;
import ecommerce.model.Reclamo;
import ecommerce.model.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SQLiteReclamoDAO extends SQLiteBaseDAO implements ReclamoDAO {

    private final SQLiteUsuarioDAO usuarioDAO;
    private final SQLiteOrdenDAO ordenDAO;

    public SQLiteReclamoDAO() {
        this(new SQLiteUsuarioDAO(), new SQLiteOrdenDAO());
    }

    SQLiteReclamoDAO(SQLiteUsuarioDAO usuarioDAO, SQLiteOrdenDAO ordenDAO) {
        this.usuarioDAO = usuarioDAO;
        this.ordenDAO = ordenDAO;
    }

    @Override
    public void guardar(Reclamo reclamo) {
        String sql = """
                INSERT INTO reclamos (numero_reclamo, cliente_id, orden_numero, motivo, fecha, estado)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = obtenerConexion();
             PreparedStatement statement = preparar(connection, sql,
                     reclamo.getNumeroReclamo(),
                     reclamo.getCliente().getId(),
                     reclamo.getPedidoAsociado().getNumero(),
                     reclamo.getMotivo(),
                     reclamo.getFecha(),
                     reclamo.getEstado())) {

            statement.executeUpdate();
        } catch (SQLException ex) {
            throw new DatabaseException("No se pudo guardar el reclamo.", ex);
        }
    }

    @Override
    public Reclamo buscarPorNumero(String numeroReclamo) {
        String sql = "SELECT * FROM reclamos WHERE numero_reclamo = ?";

        try (Connection connection = obtenerConexion();
             PreparedStatement statement = preparar(connection, sql, numeroReclamo.trim().toUpperCase());
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                return mapearReclamo(resultSet);
            }
            throw new DatabaseException("No se encontró un reclamo con número " + numeroReclamo + ".");
        } catch (SQLException ex) {
            throw new DatabaseException("No se pudo buscar el reclamo por número.", ex);
        }
    }

    @Override
    public List<Reclamo> obtenerTodos() {
        return obtenerPorConsulta("SELECT * FROM reclamos ORDER BY fecha DESC");
    }

    @Override
    public List<Reclamo> obtenerPorEstado(EstadoReclamo estado) {
        String sql = "SELECT * FROM reclamos WHERE estado = ? ORDER BY fecha DESC";
        List<Reclamo> reclamos = new ArrayList<>();

        try (Connection connection = obtenerConexion();
             PreparedStatement statement = preparar(connection, sql, estado.name());
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                reclamos.add(mapearReclamo(resultSet));
            }
            return reclamos;
        } catch (SQLException ex) {
            throw new DatabaseException("No se pudieron obtener los reclamos por estado.", ex);
        }
    }

    @Override
    public void actualizarEstado(String numeroReclamo, EstadoReclamo estado) {
        String sql = "UPDATE reclamos SET estado = ? WHERE numero_reclamo = ?";

        try (Connection connection = obtenerConexion();
             PreparedStatement statement = preparar(connection, sql, estado, numeroReclamo.trim().toUpperCase())) {

            validarFilasAfectadas(statement.executeUpdate(), "No se encontró el reclamo a actualizar.");
        } catch (SQLException ex) {
            throw new DatabaseException("No se pudo actualizar el estado del reclamo.", ex);
        }
    }

    @Override
    public void eliminar(String numeroReclamo) {
        String sql = "DELETE FROM reclamos WHERE numero_reclamo = ?";

        try (Connection connection = obtenerConexion();
             PreparedStatement statement = preparar(connection, sql, numeroReclamo.trim().toUpperCase())) {

            validarFilasAfectadas(statement.executeUpdate(), "No se encontró el reclamo a eliminar.");
        } catch (SQLException ex) {
            throw new DatabaseException("No se pudo eliminar el reclamo.", ex);
        }
    }

    private List<Reclamo> obtenerPorConsulta(String sql) {
        List<Reclamo> reclamos = new ArrayList<>();

        try (Connection connection = obtenerConexion();
             PreparedStatement statement = preparar(connection, sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                reclamos.add(mapearReclamo(resultSet));
            }
            return reclamos;
        } catch (SQLException ex) {
            throw new DatabaseException("No se pudieron obtener los reclamos.", ex);
        }
    }

    private Reclamo mapearReclamo(ResultSet resultSet) throws SQLException {
        Usuario cliente;
        OrdenCompra orden;

        try {
            cliente = usuarioDAO.buscarPorId(resultSet.getInt("cliente_id"));
            orden = ordenDAO.buscarPorNumero(resultSet.getString("orden_numero"));
        } catch (UsuarioNoEncontradoException | OrdenNoEncontradaException ex) {
            throw new DatabaseException("El reclamo referencia datos inexistentes.", ex);
        }

        return new Reclamo(
                resultSet.getString("numero_reclamo"),
                cliente,
                orden,
                resultSet.getString("motivo"),
                leerLocalDateTime(resultSet, "fecha"),
                EstadoReclamo.valueOf(resultSet.getString("estado")));
    }
}
