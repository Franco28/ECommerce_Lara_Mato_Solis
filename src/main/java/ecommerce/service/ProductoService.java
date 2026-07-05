package ecommerce.service;

import ecommerce.dao.interfaces.CategoriaDAO;
import ecommerce.dao.interfaces.ProductoDAO;
import ecommerce.enums.EstadoProducto;
import ecommerce.exception.DatosInvalidosException;
import ecommerce.exception.ProductoDuplicadoException;
import ecommerce.model.Categoria;
import ecommerce.model.Producto;
import ecommerce.util.ValidadorDominio;

import java.util.List;

/**
 * Servicio de productos. Verifica duplicados, categoría válida y reglas
 * mínimas antes de persistir.
 */
public class ProductoService {

    private final ProductoDAO productoDAO;
    private final CategoriaDAO categoriaDAO;

    public ProductoService(ProductoDAO productoDAO, CategoriaDAO categoriaDAO) {
        this.productoDAO = ValidadorDominio.validarObjetoObligatorio(productoDAO,
                "El DAO de productos es obligatorio.");
        this.categoriaDAO = ValidadorDominio.validarObjetoObligatorio(categoriaDAO,
                "El DAO de categorías es obligatorio.");
    }

    public Producto registrarProducto(Producto producto) {
        ValidadorDominio.validarObjetoObligatorio(producto, "El producto es obligatorio.");
        validarCodigoDisponible(producto.getCodigo());
        validarCategoriaExistenteYActiva(producto.getCategoria());
        validarProductoComercializable(producto);
        productoDAO.guardar(producto);
        return producto;
    }

    public void modificarProducto(Producto producto) {
        ValidadorDominio.validarObjetoObligatorio(producto, "El producto es obligatorio.");
        validarCodigoDisponibleParaOtroProducto(producto.getCodigo(), producto.getId());
        validarCategoriaExistenteYActiva(producto.getCategoria());
        validarProductoComercializable(producto);
        productoDAO.actualizar(producto);
    }

    public Producto buscarPorId(int id) {
        ValidadorDominio.validarEnteroMayorACero(id, "El ID del producto debe ser mayor a cero.");
        return productoDAO.buscarPorId(id);
    }

    public Producto buscarPorCodigo(String codigo) {
        ValidadorDominio.validarTextoObligatorio(codigo, "El código del producto es obligatorio.");
        return productoDAO.buscarPorCodigo(codigo);
    }

    public List<Producto> listarProductos() {
        return productoDAO.obtenerTodos();
    }

    public List<Producto> listarPorCategoria(int categoriaId) {
        ValidadorDominio.validarEnteroMayorACero(categoriaId,
                "El ID de la categoría debe ser mayor a cero.");
        return productoDAO.obtenerPorCategoria(categoriaId);
    }

    public List<Producto> listarSinStock() {
        return productoDAO.obtenerSinStock();
    }

    public void eliminarProducto(int id) {
        ValidadorDominio.validarEnteroMayorACero(id, "El ID del producto debe ser mayor a cero.");
        productoDAO.eliminar(id);
    }

    public void activarProducto(int id) {
        Producto producto = buscarPorId(id);
        producto.activar();
        productoDAO.actualizar(producto);
    }

    public void inactivarProducto(int id) {
        Producto producto = buscarPorId(id);
        producto.inactivar();
        productoDAO.actualizar(producto);
    }

    public void suspenderProducto(int id) {
        Producto producto = buscarPorId(id);
        producto.suspender();
        productoDAO.actualizar(producto);
    }

    private void validarCodigoDisponible(String codigo) {
        ValidadorDominio.validarTextoObligatorio(codigo, "El código del producto es obligatorio.");
        if (productoDAO.buscarOpcionalPorCodigo(codigo).isPresent()) {
            throw new ProductoDuplicadoException("Ya existe un producto con el código indicado.");
        }
    }

    private void validarCodigoDisponibleParaOtroProducto(String codigo, int productoId) {
        ValidadorDominio.validarTextoObligatorio(codigo, "El código del producto es obligatorio.");
        productoDAO.buscarOpcionalPorCodigo(codigo)
                .filter(productoExistente -> productoExistente.getId() != productoId)
                .ifPresent(productoExistente -> {
                    throw new ProductoDuplicadoException("Ya existe otro producto con el código indicado.");
                });
    }

    private void validarCategoriaExistenteYActiva(Categoria categoria) {
        ValidadorDominio.validarObjetoObligatorio(categoria, "La categoría es obligatoria.");
        Categoria categoriaPersistida = categoriaDAO.buscarPorId(categoria.getId());
        if (!categoriaPersistida.estaActiva()) {
            throw new DatosInvalidosException("No se pueden asociar productos a una categoría inactiva.");
        }
    }

    private void validarProductoComercializable(Producto producto) {
        if (producto.getEstado() == EstadoProducto.ACTIVO && producto.getStock() == 0) {
            throw new DatosInvalidosException("Un producto activo debe tener stock disponible.");
        }
        if (producto.getEstado() == EstadoProducto.SIN_STOCK && producto.getStock() > 0) {
            throw new DatosInvalidosException("Un producto con stock disponible no debería quedar marcado como SIN_STOCK.");
        }
    }
}
