package ecommerce.dao.sqlite;

import ecommerce.dao.interfaces.DevolucionDAO;
import ecommerce.enums.EstadoDevolucion;
import ecommerce.exception.DatabaseException;
import ecommerce.exception.ProductoNoEncontradoException;
import ecommerce.exception.UsuarioNoEncontradoException;
import ecommerce.model.Devolucion;
import ecommerce.model.Producto;
import ecommerce.model.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SQLiteDevolucionDAO extends SQLiteBaseDAO implements DevolucionDAO {

    private final SQLiteUsuarioDAO usuarioDAO;
    private final SQLiteProductoDAO productoDAO;

    public SQLiteDevolucionDAO() {
        this(new SQLiteUsuarioDAO(), new SQLiteProductoDAO());
    }

    SQLiteDevolucionDAO(SQLiteUsuarioDAO usuarioDAO, SQLiteProductoDAO productoDAO) {
        this.usuarioDAO = usuarioDAO;
        this.productoDAO = productoDAO;
    }

    @Override
    public void guardar(Devolucion devolucion) {
        String sql = """
                INSERT INTO devoluciones (cliente_id, producto_id, motivo, fecha, estado)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection connection = obtenerConexion();
             PreparedStatement statement = prepararConGeneratedKeys(connection, sql,
                     devolucion.getCliente().getId(),
                     devolucion.getProducto().getId(),
                     devolucion.getMotivo(),
                     devolucion.getFecha(),
                     devolucion.getEstado())) {

            statement.executeUpdate();
            devolucion.setId(obtenerIdGenerado(statement));
        } catch (SQLException ex) {
            throw new DatabaseException("No se pudo guardar la devolución.", ex);
        }
    }

    @Override
    public Devolucion buscarPorId(int id) {
        String sql = "SELECT * FROM devoluciones WHERE id = ?";

        try (Connection connection = obtenerConexion();
             PreparedStatement statement = preparar(connection, sql, id);
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                return mapearDevolucion(resultSet);
            }
            throw new DatabaseException("No se encontró una devolución con ID " + id + ".");
        } catch (SQLException ex) {
            throw new DatabaseException("No se pudo buscar la devolución por ID.", ex);
        }
    }

    @Override
    public List<Devolucion> obtenerTodos() {
        return obtenerPorConsulta("SELECT * FROM devoluciones ORDER BY fecha DESC");
    }

    @Override
    public List<Devolucion> obtenerPorCliente(int clienteId) {
        String sql = "SELECT * FROM devoluciones WHERE cliente_id = ? ORDER BY fecha DESC";
        return obtenerPorConsulta(sql, clienteId);
    }

    @Override
    public List<Devolucion> obtenerPorProducto(int productoId) {
        String sql = "SELECT * FROM devoluciones WHERE producto_id = ? ORDER BY fecha DESC";
        return obtenerPorConsulta(sql, productoId);
    }

    @Override
    public List<Devolucion> obtenerPorEstado(EstadoDevolucion estado) {
        String sql = "SELECT * FROM devoluciones WHERE estado = ? ORDER BY fecha DESC";
        return obtenerPorConsulta(sql, estado.name());
    }

    @Override
    public void actualizarEstado(int id, EstadoDevolucion estado) {
        String sql = "UPDATE devoluciones SET estado = ? WHERE id = ?";

        try (Connection connection = obtenerConexion();
             PreparedStatement statement = preparar(connection, sql, estado, id)) {

            validarFilasAfectadas(statement.executeUpdate(), "No se encontró la devolución a actualizar.");
        } catch (SQLException ex) {
            throw new DatabaseException("No se pudo actualizar el estado de la devolución.", ex);
        }
    }

    @Override
    public void eliminar(int id) {
        String sql = "DELETE FROM devoluciones WHERE id = ?";

        try (Connection connection = obtenerConexion();
             PreparedStatement statement = preparar(connection, sql, id)) {

            validarFilasAfectadas(statement.executeUpdate(), "No se encontró la devolución a eliminar.");
        } catch (SQLException ex) {
            throw new DatabaseException("No se pudo eliminar la devolución.", ex);
        }
    }

    private List<Devolucion> obtenerPorConsulta(String sql, Object... parametros) {
        List<Devolucion> devoluciones = new ArrayList<>();

        try (Connection connection = obtenerConexion();
             PreparedStatement statement = preparar(connection, sql, parametros);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                devoluciones.add(mapearDevolucion(resultSet));
            }
            return devoluciones;
        } catch (SQLException ex) {
            throw new DatabaseException("No se pudieron obtener las devoluciones.", ex);
        }
    }

    private Devolucion mapearDevolucion(ResultSet resultSet) throws SQLException {
        Usuario cliente;
        Producto producto;

        try {
            cliente = usuarioDAO.buscarPorId(resultSet.getInt("cliente_id"));
            producto = productoDAO.buscarPorId(resultSet.getInt("producto_id"));
        } catch (UsuarioNoEncontradoException | ProductoNoEncontradoException ex) {
            throw new DatabaseException("La devolución referencia datos inexistentes.", ex);
        }

        return new Devolucion(
                resultSet.getInt("id"),
                cliente,
                producto,
                resultSet.getString("motivo"),
                leerLocalDateTime(resultSet, "fecha"),
                EstadoDevolucion.valueOf(resultSet.getString("estado")));
    }
}
