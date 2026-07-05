package ecommerce.dao.sqlite;

import ecommerce.dao.interfaces.ProductoDAO;
import ecommerce.enums.EstadoProducto;
import ecommerce.exception.CategoriaNoEncontradaException;
import ecommerce.exception.DatabaseException;
import ecommerce.exception.ProductoDuplicadoException;
import ecommerce.exception.ProductoNoEncontradoException;
import ecommerce.model.Categoria;
import ecommerce.model.Producto;
import ecommerce.model.ProductoDigital;
import ecommerce.model.ProductoFisico;
import ecommerce.model.ProductoImportado;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SQLiteProductoDAO extends SQLiteBaseDAO implements ProductoDAO {

    private static final String TIPO_FISICO = "FISICO";
    private static final String TIPO_DIGITAL = "DIGITAL";
    private static final String TIPO_IMPORTADO = "IMPORTADO";

    private final SQLiteCategoriaDAO categoriaDAO;

    public SQLiteProductoDAO() {
        this(new SQLiteCategoriaDAO());
    }

    SQLiteProductoDAO(SQLiteCategoriaDAO categoriaDAO) {
        this.categoriaDAO = categoriaDAO;
    }

    @Override
    public void guardar(Producto producto) throws ProductoDuplicadoException {
        String sql = """
                INSERT INTO productos (
                    codigo, nombre, descripcion, precio, categoria_id, stock, peso,
                    estado, tipo_producto, porcentaje_impuesto_importacion, url_descarga
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = obtenerConexion();
             PreparedStatement statement = prepararConGeneratedKeys(connection, sql,
                     producto.getCodigo(),
                     producto.getNombre(),
                     producto.getDescripcion(),
                     producto.getPrecio(),
                     producto.getCategoria().getId(),
                     producto.getStock(),
                     producto.getPeso(),
                     producto.getEstado(),
                     obtenerTipoProducto(producto),
                     obtenerPorcentajeImpuesto(producto),
                     obtenerUrlDescarga(producto))) {

            statement.executeUpdate();
            producto.setId(obtenerIdGenerado(statement));
        } catch (SQLException ex) {
            if (esRestriccionUnica(ex)) {
                throw new ProductoDuplicadoException("Ya existe un producto con el código indicado.");
            }
            throw new DatabaseException("No se pudo guardar el producto.", ex);
        }
    }

    @Override
    public Producto buscarPorId(int id) throws ProductoNoEncontradoException {
        String sql = "SELECT * FROM productos WHERE id = ?";

        try (Connection connection = obtenerConexion();
             PreparedStatement statement = preparar(connection, sql, id);
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                return mapearProducto(resultSet);
            }
            throw new ProductoNoEncontradoException("No se encontró un producto con ID " + id + ".");
        } catch (SQLException ex) {
            throw new DatabaseException("No se pudo buscar el producto por ID.", ex);
        }
    }

    @Override
    public Producto buscarPorCodigo(String codigo) throws ProductoNoEncontradoException {
        return buscarOpcionalPorCodigo(codigo)
                .orElseThrow(() -> new ProductoNoEncontradoException(
                        "No se encontró un producto con código " + codigo + "."));
    }

    @Override
    public Optional<Producto> buscarOpcionalPorCodigo(String codigo) {
        String sql = "SELECT * FROM productos WHERE codigo = ?";

        try (Connection connection = obtenerConexion();
             PreparedStatement statement = preparar(connection, sql, codigo.trim().toUpperCase());
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                return Optional.of(mapearProducto(resultSet));
            }
            return Optional.empty();
        } catch (SQLException ex) {
            throw new DatabaseException("No se pudo buscar el producto por código.", ex);
        }
    }

    @Override
    public List<Producto> obtenerTodos() {
        return obtenerPorConsulta("SELECT * FROM productos ORDER BY id");
    }

    @Override
    public List<Producto> obtenerPorCategoria(int categoriaId) {
        String sql = "SELECT * FROM productos WHERE categoria_id = ? ORDER BY id";
        List<Producto> productos = new ArrayList<>();

        try (Connection connection = obtenerConexion();
             PreparedStatement statement = preparar(connection, sql, categoriaId);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                productos.add(mapearProducto(resultSet));
            }
            return productos;
        } catch (SQLException ex) {
            throw new DatabaseException("No se pudieron obtener los productos por categoría.", ex);
        }
    }

    @Override
    public List<Producto> obtenerSinStock() {
        return obtenerPorConsulta("SELECT * FROM productos WHERE stock = 0 OR estado = 'SIN_STOCK' ORDER BY id");
    }

    @Override
    public void actualizar(Producto producto) throws ProductoNoEncontradoException, ProductoDuplicadoException {
        String sql = """
                UPDATE productos
                SET codigo = ?, nombre = ?, descripcion = ?, precio = ?, categoria_id = ?, stock = ?, peso = ?,
                    estado = ?, tipo_producto = ?, porcentaje_impuesto_importacion = ?, url_descarga = ?
                WHERE id = ?
                """;

        try (Connection connection = obtenerConexion();
             PreparedStatement statement = preparar(connection, sql,
                     producto.getCodigo(),
                     producto.getNombre(),
                     producto.getDescripcion(),
                     producto.getPrecio(),
                     producto.getCategoria().getId(),
                     producto.getStock(),
                     producto.getPeso(),
                     producto.getEstado(),
                     obtenerTipoProducto(producto),
                     obtenerPorcentajeImpuesto(producto),
                     obtenerUrlDescarga(producto),
                     producto.getId())) {

            int filas = statement.executeUpdate();
            if (filas == 0) {
                throw new ProductoNoEncontradoException("No se encontró el producto a actualizar.");
            }
        } catch (SQLException ex) {
            if (esRestriccionUnica(ex)) {
                throw new ProductoDuplicadoException("Ya existe un producto con el código indicado.");
            }
            throw new DatabaseException("No se pudo actualizar el producto.", ex);
        }
    }

    @Override
    public void actualizarStock(int productoId, int nuevoStock) throws ProductoNoEncontradoException {
        String estado = nuevoStock == 0 ? EstadoProducto.SIN_STOCK.name() : EstadoProducto.ACTIVO.name();
        String sql = "UPDATE productos SET stock = ?, estado = ? WHERE id = ?";

        try (Connection connection = obtenerConexion();
             PreparedStatement statement = preparar(connection, sql, nuevoStock, estado, productoId)) {

            int filas = statement.executeUpdate();
            if (filas == 0) {
                throw new ProductoNoEncontradoException("No se encontró el producto para actualizar stock.");
            }
        } catch (SQLException ex) {
            throw new DatabaseException("No se pudo actualizar el stock del producto.", ex);
        }
    }

    @Override
    public void eliminar(int id) throws ProductoNoEncontradoException {
        String sql = "DELETE FROM productos WHERE id = ?";

        try (Connection connection = obtenerConexion();
             PreparedStatement statement = preparar(connection, sql, id)) {

            int filas = statement.executeUpdate();
            if (filas == 0) {
                throw new ProductoNoEncontradoException("No se encontró el producto a eliminar.");
            }
        } catch (SQLException ex) {
            throw new DatabaseException("No se pudo eliminar el producto.", ex);
        }
    }

    Producto mapearProducto(ResultSet resultSet) throws SQLException {
        Categoria categoria;
        try {
            categoria = categoriaDAO.buscarPorId(resultSet.getInt("categoria_id"));
        } catch (CategoriaNoEncontradaException ex) {
            throw new DatabaseException("El producto referencia una categoría inexistente.", ex);
        }

        String tipoProducto = resultSet.getString("tipo_producto");
        EstadoProducto estado = EstadoProducto.valueOf(resultSet.getString("estado"));

        return switch (tipoProducto) {
            case TIPO_DIGITAL -> new ProductoDigital(
                    resultSet.getInt("id"),
                    resultSet.getString("codigo"),
                    resultSet.getString("nombre"),
                    resultSet.getString("descripcion"),
                    resultSet.getDouble("precio"),
                    categoria,
                    resultSet.getInt("stock"),
                    estado,
                    resultSet.getString("url_descarga"));
            case TIPO_IMPORTADO -> new ProductoImportado(
                    resultSet.getInt("id"),
                    resultSet.getString("codigo"),
                    resultSet.getString("nombre"),
                    resultSet.getString("descripcion"),
                    resultSet.getDouble("precio"),
                    categoria,
                    resultSet.getInt("stock"),
                    resultSet.getDouble("peso"),
                    estado,
                    resultSet.getDouble("porcentaje_impuesto_importacion"));
            case TIPO_FISICO -> new ProductoFisico(
                    resultSet.getInt("id"),
                    resultSet.getString("codigo"),
                    resultSet.getString("nombre"),
                    resultSet.getString("descripcion"),
                    resultSet.getDouble("precio"),
                    categoria,
                    resultSet.getInt("stock"),
                    resultSet.getDouble("peso"),
                    estado);
            default -> throw new DatabaseException("Tipo de producto no soportado: " + tipoProducto);
        };
    }

    private List<Producto> obtenerPorConsulta(String sql) {
        List<Producto> productos = new ArrayList<>();

        try (Connection connection = obtenerConexion();
             PreparedStatement statement = preparar(connection, sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                productos.add(mapearProducto(resultSet));
            }
            return productos;
        } catch (SQLException ex) {
            throw new DatabaseException("No se pudieron obtener los productos.", ex);
        }
    }

    private String obtenerTipoProducto(Producto producto) {
        if (producto instanceof ProductoDigital) {
            return TIPO_DIGITAL;
        }
        if (producto instanceof ProductoImportado) {
            return TIPO_IMPORTADO;
        }
        if (producto instanceof ProductoFisico) {
            return TIPO_FISICO;
        }
        throw new DatabaseException("Tipo de producto no soportado: " + producto.getClass().getSimpleName());
    }

    private double obtenerPorcentajeImpuesto(Producto producto) {
        if (producto instanceof ProductoImportado productoImportado) {
            return productoImportado.getPorcentajeImpuestoImportacion();
        }
        return 0;
    }

    private String obtenerUrlDescarga(Producto producto) {
        if (producto instanceof ProductoDigital productoDigital) {
            return productoDigital.getUrlDescarga();
        }
        return null;
    }

    private boolean esRestriccionUnica(SQLException ex) {
        return ex.getMessage() != null && ex.getMessage().toLowerCase().contains("unique");
    }
}
