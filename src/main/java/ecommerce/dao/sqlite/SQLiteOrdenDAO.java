package ecommerce.dao.sqlite;

import ecommerce.dao.interfaces.OrdenDAO;
import ecommerce.enums.EstadoOrden;
import ecommerce.exception.DatabaseException;
import ecommerce.exception.EnvioNoEncontradoException;
import ecommerce.exception.OrdenNoEncontradaException;
import ecommerce.exception.ProductoNoEncontradoException;
import ecommerce.exception.UsuarioNoEncontradoException;
import ecommerce.model.Envio;
import ecommerce.model.ItemOrden;
import ecommerce.model.OrdenCompra;
import ecommerce.model.Pago;
import ecommerce.model.Producto;
import ecommerce.model.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SQLiteOrdenDAO extends SQLiteBaseDAO implements OrdenDAO {

    private final SQLiteUsuarioDAO usuarioDAO;
    private final SQLiteProductoDAO productoDAO;
    private final SQLitePagoDAO pagoDAO;
    private final SQLiteEnvioDAO envioDAO;

    public SQLiteOrdenDAO() {
        this(new SQLiteUsuarioDAO(), new SQLiteProductoDAO(), new SQLitePagoDAO(), new SQLiteEnvioDAO());
    }

    SQLiteOrdenDAO(SQLiteUsuarioDAO usuarioDAO, SQLiteProductoDAO productoDAO,
            SQLitePagoDAO pagoDAO, SQLiteEnvioDAO envioDAO) {
        this.usuarioDAO = usuarioDAO;
        this.productoDAO = productoDAO;
        this.pagoDAO = pagoDAO;
        this.envioDAO = envioDAO;
    }

    @Override
    public void guardar(OrdenCompra orden) {
        String sqlOrden = """
                INSERT INTO ordenes (numero, cliente_id, fecha, total, estado, pago_id, envio_codigo)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;
        String sqlItem = """
                INSERT INTO orden_items (orden_numero, producto_id, cantidad, precio_unitario, subtotal)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection connection = obtenerConexion()) {
            connection.setAutoCommit(false);

            try (PreparedStatement ordenStatement = preparar(connection, sqlOrden,
                    orden.getNumero(),
                    orden.getCliente().getId(),
                    orden.getFecha(),
                    orden.getTotal(),
                    orden.getEstado(),
                    obtenerPagoId(orden.getPago()),
                    obtenerCodigoEnvio(orden.getEnvio()))) {

                ordenStatement.executeUpdate();
            }

            for (ItemOrden item : orden.getProductos()) {
                try (PreparedStatement itemStatement = preparar(connection, sqlItem,
                        orden.getNumero(),
                        item.getProducto().getId(),
                        item.getCantidad(),
                        item.getPrecioUnitario(),
                        item.getSubtotal())) {

                    itemStatement.executeUpdate();
                }
            }

            connection.commit();
        } catch (SQLException ex) {
            throw new DatabaseException("No se pudo guardar la orden.", ex);
        }
    }

    @Override
    public OrdenCompra buscarPorNumero(String numero) throws OrdenNoEncontradaException {
        String sql = "SELECT * FROM ordenes WHERE numero = ?";

        try (Connection connection = obtenerConexion();
             PreparedStatement statement = preparar(connection, sql, numero.trim().toUpperCase());
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                return mapearOrden(resultSet);
            }
            throw new OrdenNoEncontradaException("No se encontró una orden con número " + numero + ".");
        } catch (SQLException ex) {
            throw new DatabaseException("No se pudo buscar la orden por número.", ex);
        }
    }

    @Override
    public List<OrdenCompra> obtenerTodos() {
        return obtenerPorConsulta("SELECT * FROM ordenes ORDER BY fecha DESC");
    }

    @Override
    public List<OrdenCompra> obtenerPorCliente(int clienteId) {
        String sql = "SELECT * FROM ordenes WHERE cliente_id = ? ORDER BY fecha DESC";
        List<OrdenCompra> ordenes = new ArrayList<>();

        try (Connection connection = obtenerConexion();
             PreparedStatement statement = preparar(connection, sql, clienteId);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                ordenes.add(mapearOrden(resultSet));
            }
            return ordenes;
        } catch (SQLException ex) {
            throw new DatabaseException("No se pudieron obtener las órdenes por cliente.", ex);
        }
    }

    @Override
    public List<OrdenCompra> obtenerPorEstado(EstadoOrden estado) {
        String sql = "SELECT * FROM ordenes WHERE estado = ? ORDER BY fecha DESC";
        List<OrdenCompra> ordenes = new ArrayList<>();

        try (Connection connection = obtenerConexion();
             PreparedStatement statement = preparar(connection, sql, estado.name());
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                ordenes.add(mapearOrden(resultSet));
            }
            return ordenes;
        } catch (SQLException ex) {
            throw new DatabaseException("No se pudieron obtener las órdenes por estado.", ex);
        }
    }

    @Override
    public void actualizarEstado(String numero, EstadoOrden estado) throws OrdenNoEncontradaException {
        String sql = "UPDATE ordenes SET estado = ? WHERE numero = ?";

        try (Connection connection = obtenerConexion();
             PreparedStatement statement = preparar(connection, sql, estado, numero.trim().toUpperCase())) {

            int filas = statement.executeUpdate();
            if (filas == 0) {
                throw new OrdenNoEncontradaException("No se encontró la orden a actualizar.");
            }
        } catch (SQLException ex) {
            throw new DatabaseException("No se pudo actualizar el estado de la orden.", ex);
        }
    }

    @Override
    public void eliminar(String numero) throws OrdenNoEncontradaException {
        String sqlItems = "DELETE FROM orden_items WHERE orden_numero = ?";
        String sqlOrden = "DELETE FROM ordenes WHERE numero = ?";
        String numeroNormalizado = numero.trim().toUpperCase();

        try (Connection connection = obtenerConexion()) {
            connection.setAutoCommit(false);

            try (PreparedStatement itemStatement = preparar(connection, sqlItems, numeroNormalizado)) {
                itemStatement.executeUpdate();
            }

            try (PreparedStatement ordenStatement = preparar(connection, sqlOrden, numeroNormalizado)) {
                int filas = ordenStatement.executeUpdate();
                if (filas == 0) {
                    throw new OrdenNoEncontradaException("No se encontró la orden a eliminar.");
                }
            }

            connection.commit();
        } catch (SQLException ex) {
            throw new DatabaseException("No se pudo eliminar la orden.", ex);
        }
    }

    private List<OrdenCompra> obtenerPorConsulta(String sql) {
        List<OrdenCompra> ordenes = new ArrayList<>();

        try (Connection connection = obtenerConexion();
             PreparedStatement statement = preparar(connection, sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                ordenes.add(mapearOrden(resultSet));
            }
            return ordenes;
        } catch (SQLException ex) {
            throw new DatabaseException("No se pudieron obtener las órdenes.", ex);
        }
    }

    private OrdenCompra mapearOrden(ResultSet resultSet) throws SQLException {
        Usuario cliente;
        try {
            cliente = usuarioDAO.buscarPorId(resultSet.getInt("cliente_id"));
        } catch (UsuarioNoEncontradoException ex) {
            throw new DatabaseException("La orden referencia un cliente inexistente.", ex);
        }

        OrdenCompra orden = new OrdenCompra(
                resultSet.getString("numero"),
                cliente,
                leerLocalDateTime(resultSet, "fecha"),
                EstadoOrden.valueOf(resultSet.getString("estado")));

        Integer pagoId = resultSet.getObject("pago_id", Integer.class);
        if (pagoId != null) {
            Pago pago = pagoDAO.buscarPorId(pagoId);
            orden.asociarPago(pago);
        }

        String envioCodigo = resultSet.getString("envio_codigo");
        if (envioCodigo != null) {
            try {
                Envio envio = envioDAO.buscarPorCodigoSeguimiento(envioCodigo);
                orden.asociarEnvio(envio);
            } catch (EnvioNoEncontradoException ex) {
                throw new DatabaseException("La orden referencia un envío inexistente.", ex);
            }
        }

        cargarItems(orden);
        return orden;
    }

    private void cargarItems(OrdenCompra orden) {
        String sql = "SELECT * FROM orden_items WHERE orden_numero = ? ORDER BY id";

        try (Connection connection = obtenerConexion();
             PreparedStatement statement = preparar(connection, sql, orden.getNumero());
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Producto producto;
                try {
                    producto = productoDAO.buscarPorId(resultSet.getInt("producto_id"));
                } catch (ProductoNoEncontradoException ex) {
                    throw new DatabaseException("La orden referencia un producto inexistente.", ex);
                }

                orden.agregarItem(new ItemOrden(
                        producto,
                        resultSet.getInt("cantidad"),
                        resultSet.getDouble("precio_unitario")));
            }
        } catch (SQLException ex) {
            throw new DatabaseException("No se pudieron cargar los items de la orden.", ex);
        }
    }

    private Integer obtenerPagoId(Pago pago) {
        if (pago == null || pago.getId() == 0) {
            return null;
        }
        return pago.getId();
    }

    private String obtenerCodigoEnvio(Envio envio) {
        return envio == null ? null : envio.getCodigoSeguimiento();
    }
}
