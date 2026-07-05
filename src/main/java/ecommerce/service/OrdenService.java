package ecommerce.service;

import ecommerce.dao.interfaces.OrdenDAO;
import ecommerce.enums.EstadoOrden;
import ecommerce.enums.EstadoPago;
import ecommerce.enums.RolUsuario;
import ecommerce.exception.CarritoVacioException;
import ecommerce.exception.DatosInvalidosException;
import ecommerce.model.Carrito;
import ecommerce.model.Envio;
import ecommerce.model.OrdenCompra;
import ecommerce.model.Pago;
import ecommerce.model.Usuario;
import ecommerce.model.builder.OrdenCompraBuilder;
import ecommerce.util.ValidadorDominio;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * Servicio de órdenes. Convierte un carrito en una orden persistida y
 * coordina la salida de stock a través de InventarioService.
 */
public class OrdenService {

    private static final DateTimeFormatter ORDEN_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final OrdenDAO ordenDAO;
    private final InventarioService inventarioService;
    private final CarritoService carritoService;
    private final SeguridadService seguridadService;

    public OrdenService(OrdenDAO ordenDAO, InventarioService inventarioService,
            CarritoService carritoService) {
        this(ordenDAO, inventarioService, carritoService, new SeguridadService());
    }

    public OrdenService(OrdenDAO ordenDAO, InventarioService inventarioService,
            CarritoService carritoService, SeguridadService seguridadService) {
        this.ordenDAO = ValidadorDominio.validarObjetoObligatorio(ordenDAO,
                "El DAO de órdenes es obligatorio.");
        this.inventarioService = ValidadorDominio.validarObjetoObligatorio(inventarioService,
                "El servicio de inventario es obligatorio.");
        this.carritoService = ValidadorDominio.validarObjetoObligatorio(carritoService,
                "El servicio de carrito es obligatorio.");
        this.seguridadService = ValidadorDominio.validarObjetoObligatorio(seguridadService,
                "El servicio de seguridad es obligatorio.");
    }

    public OrdenCompra crearOrdenDesdeCarrito(Carrito carrito, Pago pago, Envio envio) {
        validarDatosDeCompra(carrito, pago, envio);

        OrdenCompra orden = new OrdenCompraBuilder()
                .conNumero(generarNumeroOrden())
                .conCliente(carrito.getCliente())
                .conFecha(LocalDateTime.now())
                .conEstado(EstadoOrden.PAGADA)
                .conPago(pago)
                .conEnvio(envio)
                .desdeCarrito(carrito)
                .build();

        ordenDAO.guardar(orden);

        descontarStockPorOrden(orden);
        carrito.vaciar();

        return orden;
    }

    public OrdenCompra buscarPorNumero(String numero) {
        ValidadorDominio.validarTextoObligatorio(numero, "El número de orden es obligatorio.");
        return ordenDAO.buscarPorNumero(numero);
    }

    public List<OrdenCompra> listarOrdenes() {
        return ordenDAO.obtenerTodos();
    }

    public List<OrdenCompra> listarPorCliente(int clienteId) {
        ValidadorDominio.validarEnteroMayorACero(clienteId,
                "El ID del cliente debe ser mayor a cero.");
        return ordenDAO.obtenerPorCliente(clienteId);
    }

    public List<OrdenCompra> listarPorEstado(EstadoOrden estado) {
        ValidadorDominio.validarObjetoObligatorio(estado, "El estado de orden es obligatorio.");
        return ordenDAO.obtenerPorEstado(estado);
    }

    public void actualizarEstado(String numero, EstadoOrden nuevoEstado) {
        ValidadorDominio.validarTextoObligatorio(numero, "El número de orden es obligatorio.");
        ValidadorDominio.validarObjetoObligatorio(nuevoEstado, "El nuevo estado es obligatorio.");
        ordenDAO.actualizarEstado(numero, nuevoEstado);
    }

    public void eliminarOrden(String numero) {
        ValidadorDominio.validarTextoObligatorio(numero, "El número de orden es obligatorio.");
        ordenDAO.eliminar(numero);
    }

    public void validarPuedeConsultarOrden(Usuario usuarioSesion, OrdenCompra orden) {
        ValidadorDominio.validarObjetoObligatorio(orden, "La orden es obligatoria.");
        seguridadService.validarUsuarioActivo(usuarioSesion);

        boolean esClientePropietario = usuarioSesion.tieneRol(RolUsuario.CLIENTE)
                && usuarioSesion.getId() == orden.getCliente().getId();
        boolean esOperador = usuarioSesion.tieneRol(RolUsuario.OPERADOR_VENTAS);
        boolean esAdministrador = usuarioSesion.tieneRol(RolUsuario.ADMINISTRADOR);
        boolean esLogistica = usuarioSesion.tieneRol(RolUsuario.RESPONSABLE_LOGISTICA);

        if (!esClientePropietario && !esOperador && !esAdministrador && !esLogistica) {
            throw new ecommerce.exception.PermisoDenegadoException(
                    "El usuario no puede consultar esta orden.");
        }
    }

    private void validarDatosDeCompra(Carrito carrito, Pago pago, Envio envio) {
        carritoService.validarCarritoParaCompra(carrito);
        ValidadorDominio.validarObjetoObligatorio(pago, "El pago es obligatorio.");
        ValidadorDominio.validarObjetoObligatorio(envio, "El envío es obligatorio.");

        if (carrito.estaVacio()) {
            throw new CarritoVacioException("No se puede crear una orden con un carrito vacío.");
        }
        if (pago.getEstado() != EstadoPago.APROBADO) {
            throw new DatosInvalidosException("La orden requiere un pago aprobado.");
        }
        if (Double.compare(pago.getMonto(), carrito.calcularTotal()) != 0) {
            throw new DatosInvalidosException("El monto del pago debe coincidir con el total del carrito.");
        }
    }

    private void descontarStockPorOrden(OrdenCompra orden) {
        orden.getProductos().forEach(item -> inventarioService.egresarStock(
                item.getProducto().getId(),
                item.getCantidad(),
                "Venta asociada a orden " + orden.getNumero()));
    }

    private String generarNumeroOrden() {
        return "ORD-" + LocalDateTime.now().format(ORDEN_FORMATTER)
                + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
}
