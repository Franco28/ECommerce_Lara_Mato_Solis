package ecommerce.service;

import ecommerce.enums.MetodoPago;
import ecommerce.model.Carrito;
import ecommerce.model.DatosEnvio;
import ecommerce.model.Envio;
import ecommerce.model.OrdenCompra;
import ecommerce.model.Pago;
import ecommerce.model.Usuario;
import ecommerce.util.ValidadorDominio;

public class CheckoutFacade {

    private final CarritoSesionService carritoSesionService;
    private final CarritoService carritoService;
    private final PagoService pagoService;
    private final EnvioService envioService;
    private final OrdenService ordenService;

    public CheckoutFacade(CarritoSesionService carritoSesionService, CarritoService carritoService,
            PagoService pagoService, EnvioService envioService, OrdenService ordenService) {
        this.carritoSesionService = ValidadorDominio.validarObjetoObligatorio(carritoSesionService,
                "El servicio de sesión de carrito es obligatorio.");
        this.carritoService = ValidadorDominio.validarObjetoObligatorio(carritoService,
                "El servicio de carrito es obligatorio.");
        this.pagoService = ValidadorDominio.validarObjetoObligatorio(pagoService,
                "El servicio de pagos es obligatorio.");
        this.envioService = ValidadorDominio.validarObjetoObligatorio(envioService,
                "El servicio de envíos es obligatorio.");
        this.ordenService = ValidadorDominio.validarObjetoObligatorio(ordenService,
                "El servicio de órdenes es obligatorio.");
    }

    public OrdenCompra confirmarCompra(Usuario cliente, MetodoPago metodoPago, DatosEnvio datosEnvio) {
        ValidadorDominio.validarObjetoObligatorio(cliente, "El cliente es obligatorio.");
        ValidadorDominio.validarObjetoObligatorio(metodoPago, "El método de pago es obligatorio.");
        ValidadorDominio.validarObjetoObligatorio(datosEnvio, "Los datos de envío son obligatorios.");

        Carrito carrito = carritoSesionService.obtenerCarritoExistente(cliente);
        carritoService.validarCarritoParaCompra(carrito);

        Pago pago = pagoService.procesarPago(metodoPago, carrito.calcularTotal());
        Envio envio = envioService.crearEnvio(
                datosEnvio.getDireccion(),
                datosEnvio.getProvincia(),
                datosEnvio.getCiudad(),
                datosEnvio.getCodigoPostal(),
                datosEnvio.getTipoEnvio());

        OrdenCompra orden = ordenService.crearOrdenDesdeCarrito(carrito, pago, envio);
        carritoSesionService.quitarCarrito(cliente);
        return orden;
    }
}
