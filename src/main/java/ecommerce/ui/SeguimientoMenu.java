package ecommerce.ui;

import ecommerce.exception.EcommerceException;
import ecommerce.model.Envio;
import ecommerce.model.OrdenCompra;
import ecommerce.service.SeguimientoService;

public class SeguimientoMenu {

    private final SeguimientoService seguimientoService;
    private final EntradaConsola entrada;

    public SeguimientoMenu(SeguimientoService seguimientoService, EntradaConsola entrada) {
        this.seguimientoService = seguimientoService;
        this.entrada = entrada;
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
        ConsolaUtils.imprimirTitulo("SEGUIMIENTO DE PEDIDOS");
        ConsolaUtils.imprimirMensajeInfo("Consulta de pedidos y envios desde una sola pantalla.");
        ConsolaUtils.imprimirMenuOpciones(
                "1. Consultar pedido por numero de orden",
                "2. Consultar envio por codigo de seguimiento",
                "3. Consultar envio asociado a una orden",
                "4. Consultar historial de envio por codigo",
                "5. Consultar historial de envio por orden",
                "6. Consultar fecha estimada por codigo",
                "7. Consultar fecha estimada por orden",
                "0. Volver");
    }

    private void ejecutarOpcion(int opcion) {
        try {
            switch (opcion) {
                case 1 -> consultarPedido();
                case 2 -> consultarEnvioPorCodigo();
                case 3 -> consultarEnvioPorOrden();
                case 4 -> consultarHistorialPorCodigo();
                case 5 -> consultarHistorialPorOrden();
                case 6 -> consultarFechaEstimadaPorCodigo();
                case 7 -> consultarFechaEstimadaPorOrden();
                case 0 -> { }
                default -> ConsolaUtils.imprimirMensajeError("Opcion incorrecta.");
            }
        } catch (EcommerceException ex) {
            ConsolaUtils.imprimirMensajeError(ex.getMessage());
        }

        if (opcion != 0) {
            entrada.pausar();
        }
    }

    private void consultarPedido() {
        ConsolaUtils.imprimirTitulo("CONSULTAR PEDIDO");
        String numero = entrada.leerTexto("Numero de orden: ");
        OrdenCompra orden = seguimientoService.consultarPedido(numero);
        ConsolaUtils.imprimirOrden(orden);
    }

    private void consultarEnvioPorCodigo() {
        ConsolaUtils.imprimirTitulo("CONSULTAR ENVIO");
        String codigo = entrada.leerTexto("Codigo de seguimiento: ");
        Envio envio = seguimientoService.consultarEnvio(codigo);
        ConsolaUtils.imprimirEnvio(envio);
    }

    private void consultarEnvioPorOrden() {
        ConsolaUtils.imprimirTitulo("CONSULTAR ENVIO POR ORDEN");
        String numero = entrada.leerTexto("Numero de orden: ");
        Envio envio = seguimientoService.consultarEnvioPorOrden(numero);
        ConsolaUtils.imprimirEnvio(envio);
    }

    private void consultarHistorialPorCodigo() {
        ConsolaUtils.imprimirTitulo("HISTORIAL DE ENVIO");
        String codigo = entrada.leerTexto("Codigo de seguimiento: ");
        ConsolaUtils.imprimirHistorialEnvio(seguimientoService.consultarHistorialPorCodigo(codigo));
    }

    private void consultarHistorialPorOrden() {
        ConsolaUtils.imprimirTitulo("HISTORIAL DE ENVIO POR ORDEN");
        String numero = entrada.leerTexto("Numero de orden: ");
        ConsolaUtils.imprimirHistorialEnvio(seguimientoService.consultarHistorialPorOrden(numero));
    }

    private void consultarFechaEstimadaPorCodigo() {
        ConsolaUtils.imprimirTitulo("FECHA ESTIMADA POR CODIGO");
        String codigo = entrada.leerTexto("Codigo de seguimiento: ");
        System.out.println("Fecha estimada: " + seguimientoService.consultarFechaEstimadaPorCodigo(codigo));
    }

    private void consultarFechaEstimadaPorOrden() {
        ConsolaUtils.imprimirTitulo("FECHA ESTIMADA POR ORDEN");
        String numero = entrada.leerTexto("Numero de orden: ");
        System.out.println("Fecha estimada: " + seguimientoService.consultarFechaEstimadaPorOrden(numero));
    }
}
