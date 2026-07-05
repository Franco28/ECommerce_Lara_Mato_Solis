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
            EntradaConsola entrada) {
        this.checkoutFacade = checkoutFacade;
        this.ordenService = ordenService;
        this.carritoSesionService = carritoSesionService;
        this.entrada = entrada;
        this.clienteSelector = new ClienteSelector(usuarioService, entrada);
        this.metodoPagoSelector = new MetodoPagoSelector(entrada);
        this.tipoEnvioSelector = new TipoEnvioSelector(entrada);
        this.estadoOrdenSelector = new EstadoOrdenSelector(entrada);
    }

    public void mostrar() {
        int opcion;

        do {
            imprimirMenu();
            opcion = entrada.leerEntero("Opción: ");
            ejecutarOpcion(opcion);
        } while (opcion != 0);
    }

    private void imprimirMenu() {
        ConsolaUtils.imprimirTitulo("ÓRDENES DE COMPRA");
        System.out.println("1. Confirmar compra desde carrito");
        System.out.println("2. Buscar orden por número");
        System.out.println("3. Listar órdenes");
        System.out.println("4. Listar órdenes por cliente");
        System.out.println("5. Listar órdenes por estado");
        System.out.println("6. Actualizar estado de orden");
        System.out.println("7. Eliminar orden");
        System.out.println("0. Volver");
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
                default -> System.out.println("Opción incorrecta.");
            }
        } catch (PagoRechazadoException ex) {
            System.out.println("Pago rechazado: " + ex.getMessage());
        } catch (EcommerceException ex) {
            System.out.println("Error: " + ex.getMessage());
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

        if (!entrada.confirmar("Se generará la orden, el pago, el envío y el egreso de stock.")) {
            System.out.println("Operación cancelada.");
            return;
        }

        OrdenCompra orden = checkoutFacade.confirmarCompra(cliente, metodoPago, datosEnvio);
        System.out.println("Orden generada correctamente.");
        ConsolaUtils.imprimirOrden(orden);
    }

    private DatosEnvio leerDatosEnvio() {
        ConsolaUtils.imprimirTitulo("DATOS DE ENVÍO");
        TipoEnvio tipoEnvio = tipoEnvioSelector.seleccionarTipoEnvio();
        String direccion = entrada.leerTexto("Dirección: ");
        String provincia = entrada.leerTexto("Provincia: ");
        String ciudad = entrada.leerTexto("Ciudad: ");
        String codigoPostal = entrada.leerTexto("Código postal: ");
        return new DatosEnvio(direccion, provincia, ciudad, codigoPostal, tipoEnvio);
    }

    private void buscarOrdenPorNumero() {
        ConsolaUtils.imprimirTitulo("BUSCAR ORDEN");
        String numero = entrada.leerTexto("Número de orden: ");
        ConsolaUtils.imprimirOrden(ordenService.buscarPorNumero(numero));
    }

    private void listarOrdenes() {
        ConsolaUtils.imprimirTitulo("LISTADO DE ÓRDENES");
        ConsolaUtils.imprimirOrdenes(ordenService.listarOrdenes());
    }

    private void listarOrdenesPorCliente() {
        ConsolaUtils.imprimirTitulo("ÓRDENES POR CLIENTE");
        Usuario cliente = clienteSelector.seleccionarClienteActivo();
        ConsolaUtils.imprimirOrdenes(ordenService.listarPorCliente(cliente.getId()));
    }

    private void listarOrdenesPorEstado() {
        ConsolaUtils.imprimirTitulo("ÓRDENES POR ESTADO");
        EstadoOrden estado = estadoOrdenSelector.seleccionarEstadoOrden();
        ConsolaUtils.imprimirOrdenes(ordenService.listarPorEstado(estado));
    }

    private void actualizarEstado() {
        ConsolaUtils.imprimirTitulo("ACTUALIZAR ESTADO DE ORDEN");
        String numero = entrada.leerTexto("Número de orden: ");
        EstadoOrden estado = estadoOrdenSelector.seleccionarEstadoOrden();
        ordenService.actualizarEstado(numero, estado);
        System.out.println("Estado actualizado correctamente.");
    }

    private void eliminarOrden() {
        ConsolaUtils.imprimirTitulo("ELIMINAR ORDEN");
        String numero = entrada.leerTexto("Número de orden: ");

        if (entrada.confirmar("La orden y sus ítems serán eliminados.")) {
            ordenService.eliminarOrden(numero);
            System.out.println("Orden eliminada correctamente.");
        } else {
            System.out.println("Operación cancelada.");
        }
    }
}
