package ecommerce.service;

import ecommerce.dao.interfaces.InventarioDAO;
import ecommerce.dao.interfaces.ProductoDAO;
import ecommerce.enums.TipoMovimientoInventario;
import ecommerce.exception.StockInsuficienteException;
import ecommerce.model.InventarioMovimiento;
import ecommerce.model.Producto;
import ecommerce.util.ValidadorDominio;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio responsable de controlar stock y registrar historial de movimientos.
 */
public class InventarioService {

    private final ProductoDAO productoDAO;
    private final InventarioDAO inventarioDAO;

    public InventarioService(ProductoDAO productoDAO, InventarioDAO inventarioDAO) {
        this.productoDAO = ValidadorDominio.validarObjetoObligatorio(productoDAO,
                "El DAO de productos es obligatorio.");
        this.inventarioDAO = ValidadorDominio.validarObjetoObligatorio(inventarioDAO,
                "El DAO de inventario es obligatorio.");
    }

    public InventarioMovimiento ingresarStock(int productoId, int cantidad, String motivo) {
        Producto producto = obtenerProducto(productoId);
        producto.ingresarStock(cantidad);
        productoDAO.actualizarStock(producto.getId(), producto.getStock());
        return registrarMovimiento(producto, cantidad, TipoMovimientoInventario.INGRESO, motivo);
    }

    public InventarioMovimiento egresarStock(int productoId, int cantidad, String motivo) {
        Producto producto = obtenerProducto(productoId);
        if (cantidad > producto.getStock()) {
            throw new StockInsuficienteException("No hay stock suficiente para realizar el egreso.");
        }

        producto.egresarStock(cantidad);
        productoDAO.actualizarStock(producto.getId(), producto.getStock());
        return registrarMovimiento(producto, cantidad, TipoMovimientoInventario.EGRESO, motivo);
    }

    public InventarioMovimiento ajustarStock(int productoId, int nuevoStock, String motivo) {
        ValidadorDominio.validarEnteroNoNegativo(nuevoStock,
                "El nuevo stock no puede ser negativo.");

        Producto producto = obtenerProducto(productoId);
        int stockAnterior = producto.getStock();
        producto.setStock(nuevoStock);
        productoDAO.actualizarStock(producto.getId(), nuevoStock);

        int diferencia = Math.abs(nuevoStock - stockAnterior);
        int cantidadMovimiento = diferencia == 0 ? 1 : diferencia;
        return registrarMovimiento(producto, cantidadMovimiento, TipoMovimientoInventario.AJUSTE, motivo);
    }

    public int consultarStock(int productoId) {
        return obtenerProducto(productoId).getStock();
    }

    public void validarStockDisponible(int productoId, int cantidad) {
        Producto producto = obtenerProducto(productoId);
        ValidadorDominio.validarEnteroMayorACero(cantidad, "La cantidad debe ser mayor a cero.");

        if (!producto.validarDisponibilidad(cantidad)) {
            throw new StockInsuficienteException(
                    "No hay stock disponible suficiente para el producto " + producto.getCodigo() + ".");
        }
    }

    public List<InventarioMovimiento> listarMovimientos() {
        return inventarioDAO.obtenerTodos();
    }

    public List<InventarioMovimiento> obtenerHistorialPorProducto(int productoId) {
        ValidadorDominio.validarEnteroMayorACero(productoId,
                "El ID del producto debe ser mayor a cero.");
        return inventarioDAO.obtenerHistorialPorProducto(productoId);
    }

    private Producto obtenerProducto(int productoId) {
        ValidadorDominio.validarEnteroMayorACero(productoId,
                "El ID del producto debe ser mayor a cero.");
        return productoDAO.buscarPorId(productoId);
    }

    private InventarioMovimiento registrarMovimiento(Producto producto, int cantidad,
            TipoMovimientoInventario tipo, String motivo) {
        InventarioMovimiento movimiento = new InventarioMovimiento(
                0,
                producto,
                cantidad,
                tipo,
                LocalDateTime.now(),
                motivo,
                producto.getStock());

        inventarioDAO.guardarMovimiento(movimiento);
        return movimiento;
    }
}
