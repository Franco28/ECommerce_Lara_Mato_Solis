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
            opcion = entrada.leerEntero("Opción: ");
            ejecutarOpcion(opcion);
        } while (opcion != 0);
    }

    private void imprimirMenu() {
        ConsolaUtils.imprimirTitulo("SEGUIMIENTO DE PEDIDOS");
        System.out.println("1. Consultar pedido por número de orden");
        System.out.println("2. Consultar envío por código de seguimiento");
        System.out.println("3. Consultar envío asociado a una orden");
        System.out.println("4. Consultar historial de envío por código");
        System.out.println("5. Consultar historial de envío por orden");
        System.out.println("6. Consultar fecha estimada por código");
        System.out.println("7. Consultar fecha estimada por orden");
        System.out.println("0. Volver");
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
                default -> System.out.println("Opción incorrecta.");
            }
        } catch (EcommerceException ex) {
            System.out.println("Error: " + ex.getMessage());
        }

        if (opcion != 0) {
            entrada.pausar();
        }
    }

    private void consultarPedido() {
        ConsolaUtils.imprimirTitulo("CONSULTAR PEDIDO");
        String numero = entrada.leerTexto("Número de orden: ");
        OrdenCompra orden = seguimientoService.consultarPedido(numero);
        ConsolaUtils.imprimirOrden(orden);
    }

    private void consultarEnvioPorCodigo() {
        ConsolaUtils.imprimirTitulo("CONSULTAR ENVÍO");
        String codigo = entrada.leerTexto("Código de seguimiento: ");
        Envio envio = seguimientoService.consultarEnvio(codigo);
        ConsolaUtils.imprimirEnvio(envio);
    }

    private void consultarEnvioPorOrden() {
        ConsolaUtils.imprimirTitulo("CONSULTAR ENVÍO POR ORDEN");
        String numero = entrada.leerTexto("Número de orden: ");
        Envio envio = seguimientoService.consultarEnvioPorOrden(numero);
        ConsolaUtils.imprimirEnvio(envio);
    }

    private void consultarHistorialPorCodigo() {
        ConsolaUtils.imprimirTitulo("HISTORIAL DE ENVÍO");
        String codigo = entrada.leerTexto("Código de seguimiento: ");
        ConsolaUtils.imprimirHistorialEnvio(seguimientoService.consultarHistorialPorCodigo(codigo));
    }

    private void consultarHistorialPorOrden() {
        ConsolaUtils.imprimirTitulo("HISTORIAL DE ENVÍO POR ORDEN");
        String numero = entrada.leerTexto("Número de orden: ");
        ConsolaUtils.imprimirHistorialEnvio(seguimientoService.consultarHistorialPorOrden(numero));
    }

    private void consultarFechaEstimadaPorCodigo() {
        ConsolaUtils.imprimirTitulo("FECHA ESTIMADA POR CÓDIGO");
        String codigo = entrada.leerTexto("Código de seguimiento: ");
        System.out.println("Fecha estimada: " + seguimientoService.consultarFechaEstimadaPorCodigo(codigo));
    }

    private void consultarFechaEstimadaPorOrden() {
        ConsolaUtils.imprimirTitulo("FECHA ESTIMADA POR ORDEN");
        String numero = entrada.leerTexto("Número de orden: ");
        System.out.println("Fecha estimada: " + seguimientoService.consultarFechaEstimadaPorOrden(numero));
    }
}
