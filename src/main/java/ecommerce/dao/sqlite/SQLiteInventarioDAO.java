package ecommerce.dao.sqlite;

import ecommerce.dao.interfaces.InventarioDAO;
import ecommerce.enums.TipoMovimientoInventario;
import ecommerce.exception.DatabaseException;
import ecommerce.exception.ProductoNoEncontradoException;
import ecommerce.model.InventarioMovimiento;
import ecommerce.model.Producto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SQLiteInventarioDAO extends SQLiteBaseDAO implements InventarioDAO {

    private final SQLiteProductoDAO productoDAO;

    public SQLiteInventarioDAO() {
        this(new SQLiteProductoDAO());
    }

    SQLiteInventarioDAO(SQLiteProductoDAO productoDAO) {
        this.productoDAO = productoDAO;
    }

    @Override
    public void guardarMovimiento(InventarioMovimiento movimiento) {
        String sql = """
                INSERT INTO inventario_movimientos (producto_id, cantidad, tipo, fecha, observacion, stock_resultante)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = obtenerConexion();
             PreparedStatement statement = prepararConGeneratedKeys(connection, sql,
                     movimiento.getProducto().getId(),
                     movimiento.getCantidad(),
                     movimiento.getTipo(),
                     movimiento.getFecha(),
                     movimiento.getMotivo(),
                     movimiento.getStockResultante())) {

            statement.executeUpdate();
            movimiento.setId(obtenerIdGenerado(statement));
        } catch (SQLException ex) {
            throw new DatabaseException("No se pudo guardar el movimiento de inventario.", ex);
        }
    }

    @Override
    public List<InventarioMovimiento> obtenerTodos() {
        return obtenerPorConsulta("SELECT * FROM inventario_movimientos ORDER BY fecha DESC, id DESC");
    }

    @Override
    public List<InventarioMovimiento> obtenerHistorialPorProducto(int productoId) {
        String sql = "SELECT * FROM inventario_movimientos WHERE producto_id = ? ORDER BY fecha DESC, id DESC";
        List<InventarioMovimiento> movimientos = new ArrayList<>();

        try (Connection connection = obtenerConexion();
             PreparedStatement statement = preparar(connection, sql, productoId);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                movimientos.add(mapearMovimiento(resultSet));
            }
            return movimientos;
        } catch (SQLException ex) {
            throw new DatabaseException("No se pudo obtener el historial de inventario por producto.", ex);
        }
    }

    private List<InventarioMovimiento> obtenerPorConsulta(String sql) {
        List<InventarioMovimiento> movimientos = new ArrayList<>();

        try (Connection connection = obtenerConexion();
             PreparedStatement statement = preparar(connection, sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                movimientos.add(mapearMovimiento(resultSet));
            }
            return movimientos;
        } catch (SQLException ex) {
            throw new DatabaseException("No se pudieron obtener los movimientos de inventario.", ex);
        }
    }

    private InventarioMovimiento mapearMovimiento(ResultSet resultSet) throws SQLException {
        Producto producto;
        try {
            producto = productoDAO.buscarPorId(resultSet.getInt("producto_id"));
        } catch (ProductoNoEncontradoException ex) {
            throw new DatabaseException("El movimiento referencia un producto inexistente.", ex);
        }

        return new InventarioMovimiento(
                resultSet.getInt("id"),
                producto,
                resultSet.getInt("cantidad"),
                TipoMovimientoInventario.valueOf(resultSet.getString("tipo")),
                leerLocalDateTime(resultSet, "fecha"),
                resultSet.getString("observacion"),
                resultSet.getInt("stock_resultante"));
    }
}
