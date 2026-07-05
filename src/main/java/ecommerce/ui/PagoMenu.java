package ecommerce.ui;

import ecommerce.enums.MetodoPago;
import ecommerce.exception.EcommerceException;
import ecommerce.exception.PagoRechazadoException;
import ecommerce.model.Pago;
import ecommerce.service.PagoService;

public class PagoMenu {

    private final PagoService pagoService;
    private final EntradaConsola entrada;
    private final MetodoPagoSelector metodoPagoSelector;

    public PagoMenu(PagoService pagoService, EntradaConsola entrada) {
        this.pagoService = pagoService;
        this.entrada = entrada;
        this.metodoPagoSelector = new MetodoPagoSelector(entrada);
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
        ConsolaUtils.imprimirTitulo("PROCESAMIENTO DE PAGOS");
        System.out.println("1. Procesar pago");
        System.out.println("2. Registrar pago pendiente");
        System.out.println("3. Buscar pago por ID");
        System.out.println("4. Listar pagos");
        System.out.println("5. Aprobar pago pendiente");
        System.out.println("6. Rechazar pago");
        System.out.println("7. Cancelar pago");
        System.out.println("8. Eliminar pago");
        System.out.println("0. Volver");
    }

    private void ejecutarOpcion(int opcion) {
        try {
            switch (opcion) {
                case 1 -> procesarPago();
                case 2 -> registrarPagoPendiente();
                case 3 -> buscarPagoPorId();
                case 4 -> listarPagos();
                case 5 -> aprobarPago();
                case 6 -> rechazarPago();
                case 7 -> cancelarPago();
                case 8 -> eliminarPago();
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

    private void procesarPago() {
        ConsolaUtils.imprimirTitulo("PROCESAR PAGO");
        MetodoPago metodoPago = metodoPagoSelector.seleccionarMetodoPago();
        double monto = entrada.leerDecimal("Monto: ");

        Pago pago = pagoService.procesarPago(metodoPago, monto);

        System.out.println("Pago procesado correctamente.");
        ConsolaUtils.imprimirPago(pago);
    }

    private void registrarPagoPendiente() {
        ConsolaUtils.imprimirTitulo("REGISTRAR PAGO PENDIENTE");
        MetodoPago metodoPago = metodoPagoSelector.seleccionarMetodoPago();
        double monto = entrada.leerDecimal("Monto: ");

        Pago pago = pagoService.registrarPagoPendiente(metodoPago, monto);

        System.out.println("Pago pendiente registrado correctamente.");
        ConsolaUtils.imprimirPago(pago);
    }

    private void buscarPagoPorId() {
        ConsolaUtils.imprimirTitulo("BUSCAR PAGO POR ID");
        int id = entrada.leerEntero("ID del pago: ");
        ConsolaUtils.imprimirPago(pagoService.buscarPorId(id));
    }

    private void listarPagos() {
        ConsolaUtils.imprimirTitulo("LISTADO DE PAGOS");
        ConsolaUtils.imprimirPagos(pagoService.listarPagos());
    }

    private void aprobarPago() {
        ConsolaUtils.imprimirTitulo("APROBAR PAGO");
        int id = entrada.leerEntero("ID del pago: ");
        pagoService.aprobarPago(id);
        System.out.println("Pago aprobado correctamente.");
    }

    private void rechazarPago() {
        ConsolaUtils.imprimirTitulo("RECHAZAR PAGO");
        int id = entrada.leerEntero("ID del pago: ");
        pagoService.rechazarPago(id);
        System.out.println("Pago rechazado correctamente.");
    }

    private void cancelarPago() {
        ConsolaUtils.imprimirTitulo("CANCELAR PAGO");
        int id = entrada.leerEntero("ID del pago: ");
        pagoService.cancelarPago(id);
        System.out.println("Pago cancelado correctamente.");
    }

    private void eliminarPago() {
        ConsolaUtils.imprimirTitulo("ELIMINAR PAGO");
        int id = entrada.leerEntero("ID del pago: ");

        if (entrada.confirmar("El pago será eliminado.")) {
            pagoService.eliminarPago(id);
            System.out.println("Pago eliminado correctamente.");
        } else {
            System.out.println("Operación cancelada.");
        }
    }
}
