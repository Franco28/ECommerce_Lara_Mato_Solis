package ecommerce.ui;

import ecommerce.enums.EstadoOrden;
import ecommerce.enums.MetodoPago;
import ecommerce.enums.TipoEnvio;
import ecommerce.exception.EcommerceException;
import ecommerce.exception.PagoRechazadoException;
import ecommerce.model.Carrito;
import ecommerce.model.DatosEnvio;
import ecommerce.model.OrdenCompra;
import ecommerce.model.Usuario;
import ecommerce.service.CarritoSesionService;
import ecommerce.service.CheckoutFacade;
import ecommerce.service.OrdenService;
import ecommerce.service.UsuarioService;
import ecommerce.service.SesionUsuarioService;

public class OrdenMenu {

    private final CheckoutFacade checkoutFacade;
    private final OrdenService ordenService;
    private final CarritoSesionService carritoSesionService;
    private final EntradaConsola entrada;
    private final ClienteSelector clienteSelector;
    private final MetodoPagoSelector metodoPagoSelector;
    private final TipoEnvioSelector tipoEnvioSelector;
    private final EstadoOrdenSelector estadoOrdenSelector;

    public OrdenMenu(CheckoutFacade checkoutFacade, OrdenService ordenService,
            UsuarioService usuarioService, CarritoSesionService carritoSesionService,
            SesionUsuarioService sesionUsuarioService, EntradaConsola entrada) {
        this.checkoutFacade = checkoutFacade;
        this.ordenService = ordenService;
        this.carritoSesionService = carritoSesionService;
        this.entrada = entrada;
        this.clienteSelector = new ClienteSelector(usuarioService, entrada, sesionUsuarioService);
        this.metodoPagoSelector = new MetodoPagoSelector(entrada);
        this.tipoEnvioSelector = new TipoEnvioSelector(entrada);
        this.estadoOrdenSelector = new EstadoOrdenSelector(entrada);
    }

    public void mostrar() {
        int opcion;

        do {
            imprimirMenu();
            opcion = entrada.leerEntero("Opcion: ");
            ejecutarOpcion(opcion);
        } while (opcion != 0);
    }

    private void imprimirMenu() {
        ConsolaUtils.imprimirTitulo("ORDENES DE COMPRA");
        ConsolaUtils.imprimirMensajeInfo("Checkout, consulta y administracion de ordenes.");
        ConsolaUtils.imprimirMenuOpciones(
                "1. Confirmar compra desde carrito",
                "2. Buscar orden por numero",
                "3. Listar ordenes",
                "4. Listar ordenes por cliente",
                "5. Listar ordenes por estado",
                "6. Actualizar estado de orden",
                "7. Eliminar orden",
                "0. Volver");
    }

    private void ejecutarOpcion(int opcion) {
        try {
            switch (opcion) {
                case 1 -> confirmarCompra();
                case 2 -> buscarOrdenPorNumero();
                case 3 -> listarOrdenes();
                case 4 -> listarOrdenesPorCliente();
                case 5 -> listarOrdenesPorEstado();
                case 6 -> actualizarEstado();
                case 7 -> eliminarOrden();
                case 0 -> { }
                default -> ConsolaUtils.imprimirMensajeError("Opcion incorrecta.");
            }
        } catch (PagoRechazadoException ex) {
            ConsolaUtils.imprimirMensajeError("Pago rechazado: " + ex.getMessage());
        } catch (EcommerceException ex) {
            ConsolaUtils.imprimirMensajeError(ex.getMessage());
        }

        if (opcion != 0) {
            entrada.pausar();
        }
    }

    private void confirmarCompra() {
        ConsolaUtils.imprimirTitulo("CONFIRMAR COMPRA");
        Usuario cliente = clienteSelector.seleccionarClienteActivo();
        Carrito carrito = carritoSesionService.obtenerCarritoExistente(cliente);
        ConsolaUtils.imprimirCarrito(carrito);

        MetodoPago metodoPago = metodoPagoSelector.seleccionarMetodoPago();
        DatosEnvio datosEnvio = leerDatosEnvio();

        if (!entrada.confirmar("Se generara la orden, el pago, el envio y el egreso de stock.")) {
            ConsolaUtils.imprimirMensajeInfo("Operacion cancelada.");
            return;
        }

        OrdenCompra orden = checkoutFacade.confirmarCompra(cliente, metodoPago, datosEnvio);
        ConsolaUtils.imprimirMensajeExito("Orden generada correctamente.");
        ConsolaUtils.imprimirOrden(orden);
    }

    private DatosEnvio leerDatosEnvio() {
        ConsolaUtils.imprimirTitulo("DATOS DE ENVIO");
        TipoEnvio tipoEnvio = tipoEnvioSelector.seleccionarTipoEnvio();
        String direccion = entrada.leerTexto("Direccion: ");
        String provincia = entrada.leerTexto("Provincia: ");
        String ciudad = entrada.leerTexto("Ciudad: ");
        String codigoPostal = entrada.leerTexto("Codigo postal: ");
        return new DatosEnvio(direccion, provincia, ciudad, codigoPostal, tipoEnvio);
    }

    private void buscarOrdenPorNumero() {
        ConsolaUtils.imprimirTitulo("BUSCAR ORDEN");
        String numero = entrada.leerTexto("Numero de orden: ");
        ConsolaUtils.imprimirOrden(ordenService.buscarPorNumero(numero));
    }

    private void listarOrdenes() {
        ConsolaUtils.imprimirTitulo("LISTADO DE ORDENES");
        ConsolaUtils.imprimirOrdenes(ordenService.listarOrdenes());
    }

    private void listarOrdenesPorCliente() {
        ConsolaUtils.imprimirTitulo("ORDENES POR CLIENTE");
        Usuario cliente = clienteSelector.seleccionarClienteActivo();
        ConsolaUtils.imprimirOrdenes(ordenService.listarPorCliente(cliente.getId()));
    }

    private void listarOrdenesPorEstado() {
        ConsolaUtils.imprimirTitulo("ORDENES POR ESTADO");
        EstadoOrden estado = estadoOrdenSelector.seleccionarEstadoOrden();
        ConsolaUtils.imprimirOrdenes(ordenService.listarPorEstado(estado));
    }

    private void actualizarEstado() {
        ConsolaUtils.imprimirTitulo("ACTUALIZAR ESTADO DE ORDEN");
        String numero = entrada.leerTexto("Numero de orden: ");
        EstadoOrden estado = estadoOrdenSelector.seleccionarEstadoOrden();
        ordenService.actualizarEstado(numero, estado);
        ConsolaUtils.imprimirMensajeExito("Estado actualizado correctamente.");
    }

    private void eliminarOrden() {
        ConsolaUtils.imprimirTitulo("ELIMINAR ORDEN");
        String numero = entrada.leerTexto("Numero de orden: ");

        if (entrada.confirmar("La orden y sus items seran eliminados.")) {
            ordenService.eliminarOrden(numero);
            ConsolaUtils.imprimirMensajeExito("Orden eliminada correctamente.");
        } else {
            ConsolaUtils.imprimirMensajeInfo("Operacion cancelada.");
        }
    }
}
