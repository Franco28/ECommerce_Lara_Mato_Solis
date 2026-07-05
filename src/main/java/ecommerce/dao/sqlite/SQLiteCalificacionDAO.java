package ecommerce.dao.sqlite;

import ecommerce.dao.interfaces.CalificacionDAO;
import ecommerce.exception.DatabaseException;
import ecommerce.exception.ProductoNoEncontradoException;
import ecommerce.exception.UsuarioNoEncontradoException;
import ecommerce.model.Calificacion;
import ecommerce.model.Producto;
import ecommerce.model.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SQLiteCalificacionDAO extends SQLiteBaseDAO implements CalificacionDAO {

    private final SQLiteUsuarioDAO usuarioDAO;
    private final SQLiteProductoDAO productoDAO;

    public SQLiteCalificacionDAO() {
        this(new SQLiteUsuarioDAO(), new SQLiteProductoDAO());
    }

    SQLiteCalificacionDAO(SQLiteUsuarioDAO usuarioDAO, SQLiteProductoDAO productoDAO) {
        this.usuarioDAO = usuarioDAO;
        this.productoDAO = productoDAO;
    }

    @Override
    public void guardar(Calificacion calificacion) {
        String sql = """
                INSERT INTO calificaciones (cliente_id, producto_id, puntuacion, comentario, fecha)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection connection = obtenerConexion();
             PreparedStatement statement = prepararConGeneratedKeys(connection, sql,
                     calificacion.getCliente().getId(),
                     calificacion.getProducto().getId(),
                     calificacion.getPuntuacion(),
                     calificacion.getComentario(),
                     calificacion.getFecha())) {

            statement.executeUpdate();
            calificacion.setId(obtenerIdGenerado(statement));
        } catch (SQLException ex) {
            throw new DatabaseException("No se pudo guardar la calificación.", ex);
        }
    }

    @Override
    public Calificacion buscarPorId(int id) {
        String sql = "SELECT * FROM calificaciones WHERE id = ?";

        try (Connection connection = obtenerConexion();
             PreparedStatement statement = preparar(connection, sql, id);
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                return mapearCalificacion(resultSet);
            }
            throw new DatabaseException("No se encontró una calificación con ID " + id + ".");
        } catch (SQLException ex) {
            throw new DatabaseException("No se pudo buscar la calificación por ID.", ex);
        }
    }

    @Override
    public List<Calificacion> obtenerTodos() {
        return obtenerPorConsulta("SELECT * FROM calificaciones ORDER BY fecha DESC");
    }

    @Override
    public List<Calificacion> obtenerPorCliente(int clienteId) {
        String sql = "SELECT * FROM calificaciones WHERE cliente_id = ? ORDER BY fecha DESC";
        return obtenerPorConsulta(sql, clienteId);
    }

    @Override
    public List<Calificacion> obtenerPorProducto(int productoId) {
        String sql = "SELECT * FROM calificaciones WHERE producto_id = ? ORDER BY fecha DESC";
        return obtenerPorConsulta(sql, productoId);
    }

    @Override
    public double obtenerPromedioPorProducto(int productoId) {
        String sql = "SELECT AVG(puntuacion) AS promedio FROM calificaciones WHERE producto_id = ?";

        try (Connection connection = obtenerConexion();
             PreparedStatement statement = preparar(connection, sql, productoId);
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                return resultSet.getDouble("promedio");
            }
            return 0;
        } catch (SQLException ex) {
            throw new DatabaseException("No se pudo calcular el promedio de calificaciones.", ex);
        }
    }

    @Override
    public void eliminar(int id) {
        String sql = "DELETE FROM calificaciones WHERE id = ?";

        try (Connection connection = obtenerConexion();
             PreparedStatement statement = preparar(connection, sql, id)) {

            validarFilasAfectadas(statement.executeUpdate(), "No se encontró la calificación a eliminar.");
        } catch (SQLException ex) {
            throw new DatabaseException("No se pudo eliminar la calificación.", ex);
        }
    }

    private List<Calificacion> obtenerPorConsulta(String sql, Object... parametros) {
        List<Calificacion> calificaciones = new ArrayList<>();

        try (Connection connection = obtenerConexion();
             PreparedStatement statement = preparar(connection, sql, parametros);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                calificaciones.add(mapearCalificacion(resultSet));
            }
            return calificaciones;
        } catch (SQLException ex) {
            throw new DatabaseException("No se pudieron obtener las calificaciones.", ex);
        }
    }

    private Calificacion mapearCalificacion(ResultSet resultSet) throws SQLException {
        Usuario cliente;
        Producto producto;

        try {
            cliente = usuarioDAO.buscarPorId(resultSet.getInt("cliente_id"));
            producto = productoDAO.buscarPorId(resultSet.getInt("producto_id"));
        } catch (UsuarioNoEncontradoException | ProductoNoEncontradoException ex) {
            throw new DatabaseException("La calificación referencia datos inexistentes.", ex);
        }

        return new Calificacion(
                resultSet.getInt("id"),
                cliente,
                producto,
                resultSet.getInt("puntuacion"),
                resultSet.getString("comentario"),
                leerLocalDateTime(resultSet, "fecha"));
    }
}
