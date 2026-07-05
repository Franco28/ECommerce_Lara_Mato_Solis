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
            opcion = entrada.leerEntero("Opcion: ");
            ejecutarOpcion(opcion);
        } while (opcion != 0);
    }

    private void imprimirMenu() {
        ConsolaUtils.imprimirTitulo("PROCESAMIENTO DE PAGOS");
        ConsolaUtils.imprimirMensajeInfo("Procesamiento y administracion de pagos.");
        ConsolaUtils.imprimirMenuOpciones(
                "1. Procesar pago",
                "2. Registrar pago pendiente",
                "3. Buscar pago por ID",
                "4. Listar pagos",
                "5. Aprobar pago pendiente",
                "6. Rechazar pago",
                "7. Cancelar pago",
                "8. Eliminar pago",
                "0. Volver");
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

    private void procesarPago() {
        ConsolaUtils.imprimirTitulo("PROCESAR PAGO");
        MetodoPago metodoPago = metodoPagoSelector.seleccionarMetodoPago();
        double monto = entrada.leerDecimal("Monto: ");

        Pago pago = pagoService.procesarPago(metodoPago, monto);

        ConsolaUtils.imprimirMensajeExito("Pago procesado correctamente.");
        ConsolaUtils.imprimirPago(pago);
    }

    private void registrarPagoPendiente() {
        ConsolaUtils.imprimirTitulo("REGISTRAR PAGO PENDIENTE");
        MetodoPago metodoPago = metodoPagoSelector.seleccionarMetodoPago();
        double monto = entrada.leerDecimal("Monto: ");

        Pago pago = pagoService.registrarPagoPendiente(metodoPago, monto);

        ConsolaUtils.imprimirMensajeExito("Pago pendiente registrado correctamente.");
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
        ConsolaUtils.imprimirMensajeExito("Pago aprobado correctamente.");
    }

    private void rechazarPago() {
        ConsolaUtils.imprimirTitulo("RECHAZAR PAGO");
        int id = entrada.leerEntero("ID del pago: ");
        pagoService.rechazarPago(id);
        ConsolaUtils.imprimirMensajeExito("Pago rechazado correctamente.");
    }

    private void cancelarPago() {
        ConsolaUtils.imprimirTitulo("CANCELAR PAGO");
        int id = entrada.leerEntero("ID del pago: ");
        pagoService.cancelarPago(id);
        ConsolaUtils.imprimirMensajeExito("Pago cancelado correctamente.");
    }

    private void eliminarPago() {
        ConsolaUtils.imprimirTitulo("ELIMINAR PAGO");
        int id = entrada.leerEntero("ID del pago: ");

        if (entrada.confirmar("El pago sera eliminado.")) {
            pagoService.eliminarPago(id);
            ConsolaUtils.imprimirMensajeExito("Pago eliminado correctamente.");
        } else {
            ConsolaUtils.imprimirMensajeInfo("Operacion cancelada.");
        }
    }
}
