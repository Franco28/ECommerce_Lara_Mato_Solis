package ecommerce.service;

import ecommerce.dao.interfaces.DevolucionDAO;
import ecommerce.enums.EstadoDevolucion;
import ecommerce.enums.RolUsuario;
import ecommerce.exception.PermisoDenegadoException;
import ecommerce.model.Devolucion;
import ecommerce.model.ItemOrden;
import ecommerce.model.OrdenCompra;
import ecommerce.model.Producto;
import ecommerce.model.Usuario;
import ecommerce.util.ValidadorDominio;

import java.time.LocalDateTime;
import java.util.List;

public class DevolucionService {

    private final DevolucionDAO devolucionDAO;
    private final OrdenService ordenService;
    private final ProductoService productoService;
    private final SeguridadService seguridadService;

    public DevolucionService(DevolucionDAO devolucionDAO, OrdenService ordenService,
            ProductoService productoService, SeguridadService seguridadService) {
        this.devolucionDAO = ValidadorDominio.validarObjetoObligatorio(devolucionDAO,
                "El DAO de devoluciones es obligatorio.");
        this.ordenService = ValidadorDominio.validarObjetoObligatorio(ordenService,
                "El servicio de órdenes es obligatorio.");
        this.productoService = ValidadorDominio.validarObjetoObligatorio(productoService,
                "El servicio de productos es obligatorio.");
        this.seguridadService = ValidadorDominio.validarObjetoObligatorio(seguridadService,
                "El servicio de seguridad es obligatorio.");
    }

    public Devolucion solicitarDevolucion(Usuario cliente, int productoId, String motivo) {
        seguridadService.validarRol(cliente, RolUsuario.CLIENTE);
        Producto producto = productoService.buscarPorId(productoId);
        validarProductoComprado(cliente, producto.getId());

        Devolucion devolucion = new Devolucion(
                0,
                cliente,
                producto,
                motivo,
                LocalDateTime.now(),
                EstadoDevolucion.SOLICITADA);

        devolucionDAO.guardar(devolucion);
        return devolucion;
    }

    public Devolucion buscarPorId(int id) {
        ValidadorDominio.validarEnteroMayorACero(id, "El ID de devolución debe ser mayor a cero.");
        return devolucionDAO.buscarPorId(id);
    }

    public List<Devolucion> listarDevoluciones() {
        return devolucionDAO.obtenerTodos();
    }

    public List<Devolucion> listarPorCliente(int clienteId) {
        ValidadorDominio.validarEnteroMayorACero(clienteId,
                "El ID del cliente debe ser mayor a cero.");
        return devolucionDAO.obtenerPorCliente(clienteId);
    }

    public List<Devolucion> listarPorProducto(int productoId) {
        ValidadorDominio.validarEnteroMayorACero(productoId,
                "El ID del producto debe ser mayor a cero.");
        return devolucionDAO.obtenerPorProducto(productoId);
    }

    public List<Devolucion> listarPorEstado(EstadoDevolucion estado) {
        ValidadorDominio.validarObjetoObligatorio(estado,
                "El estado de devolución es obligatorio.");
        return devolucionDAO.obtenerPorEstado(estado);
    }

    public void cambiarEstado(int id, EstadoDevolucion estado) {
        ValidadorDominio.validarEnteroMayorACero(id, "El ID de devolución debe ser mayor a cero.");
        ValidadorDominio.validarObjetoObligatorio(estado,
                "El estado de devolución es obligatorio.");
        devolucionDAO.actualizarEstado(id, estado);
    }

    public void eliminarDevolucion(int id) {
        ValidadorDominio.validarEnteroMayorACero(id, "El ID de devolución debe ser mayor a cero.");
        devolucionDAO.eliminar(id);
    }

    private void validarProductoComprado(Usuario cliente, int productoId) {
        List<OrdenCompra> ordenes = ordenService.listarPorCliente(cliente.getId());

        boolean productoComprado = ordenes.stream()
                .flatMap(orden -> orden.getProductos().stream())
                .map(ItemOrden::getProducto)
                .anyMatch(producto -> producto.getId() == productoId);

        if (!productoComprado) {
            throw new PermisoDenegadoException(
                    "El cliente solo puede solicitar devoluciones de productos comprados.");
        }
    }
}
