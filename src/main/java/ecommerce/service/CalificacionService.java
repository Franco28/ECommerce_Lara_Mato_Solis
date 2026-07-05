package ecommerce.service;

import ecommerce.dao.interfaces.CalificacionDAO;
import ecommerce.enums.RolUsuario;
import ecommerce.exception.PermisoDenegadoException;
import ecommerce.model.Calificacion;
import ecommerce.model.ItemOrden;
import ecommerce.model.OrdenCompra;
import ecommerce.model.Producto;
import ecommerce.model.Usuario;
import ecommerce.util.ValidadorDominio;

import java.time.LocalDateTime;
import java.util.List;

public class CalificacionService {

    private final CalificacionDAO calificacionDAO;
    private final OrdenService ordenService;
    private final ProductoService productoService;
    private final SeguridadService seguridadService;

    public CalificacionService(CalificacionDAO calificacionDAO, OrdenService ordenService,
            ProductoService productoService, SeguridadService seguridadService) {
        this.calificacionDAO = ValidadorDominio.validarObjetoObligatorio(calificacionDAO,
                "El DAO de calificaciones es obligatorio.");
        this.ordenService = ValidadorDominio.validarObjetoObligatorio(ordenService,
                "El servicio de órdenes es obligatorio.");
        this.productoService = ValidadorDominio.validarObjetoObligatorio(productoService,
                "El servicio de productos es obligatorio.");
        this.seguridadService = ValidadorDominio.validarObjetoObligatorio(seguridadService,
                "El servicio de seguridad es obligatorio.");
    }

    public Calificacion calificarProducto(Usuario cliente, int productoId, int puntuacion, String comentario) {
        seguridadService.validarRol(cliente, RolUsuario.CLIENTE);
        Producto producto = productoService.buscarPorId(productoId);
        validarProductoComprado(cliente, producto.getId());

        Calificacion calificacion = new Calificacion(
                0,
                cliente,
                producto,
                puntuacion,
                comentario,
                LocalDateTime.now());

        calificacionDAO.guardar(calificacion);
        return calificacion;
    }

    public Calificacion buscarPorId(int id) {
        ValidadorDominio.validarEnteroMayorACero(id, "El ID de calificación debe ser mayor a cero.");
        return calificacionDAO.buscarPorId(id);
    }

    public List<Calificacion> listarCalificaciones() {
        return calificacionDAO.obtenerTodos();
    }

    public List<Calificacion> listarPorCliente(int clienteId) {
        ValidadorDominio.validarEnteroMayorACero(clienteId,
                "El ID del cliente debe ser mayor a cero.");
        return calificacionDAO.obtenerPorCliente(clienteId);
    }

    public List<Calificacion> listarPorProducto(int productoId) {
        ValidadorDominio.validarEnteroMayorACero(productoId,
                "El ID del producto debe ser mayor a cero.");
        return calificacionDAO.obtenerPorProducto(productoId);
    }

    public double obtenerPromedioPorProducto(int productoId) {
        ValidadorDominio.validarEnteroMayorACero(productoId,
                "El ID del producto debe ser mayor a cero.");
        return calificacionDAO.obtenerPromedioPorProducto(productoId);
    }

    public void eliminarCalificacion(int id) {
        ValidadorDominio.validarEnteroMayorACero(id, "El ID de calificación debe ser mayor a cero.");
        calificacionDAO.eliminar(id);
    }

    private void validarProductoComprado(Usuario cliente, int productoId) {
        List<OrdenCompra> ordenes = ordenService.listarPorCliente(cliente.getId());

        boolean productoComprado = ordenes.stream()
                .flatMap(orden -> orden.getProductos().stream())
                .map(ItemOrden::getProducto)
                .anyMatch(producto -> producto.getId() == productoId);

        if (!productoComprado) {
            throw new PermisoDenegadoException(
                    "El cliente solo puede calificar productos comprados.");
        }
    }
}
