package ecommerce.ui;

import ecommerce.enums.EstadoEnvio;
import ecommerce.enums.TipoEnvio;
import ecommerce.exception.EcommerceException;
import ecommerce.model.Envio;
import ecommerce.service.EnvioService;

public class EnvioMenu {

    private final EnvioService envioService;
    private final EntradaConsola entrada;
    private final TipoEnvioSelector tipoEnvioSelector;
    private final EstadoEnvioSelector estadoEnvioSelector;

    public EnvioMenu(EnvioService envioService, EntradaConsola entrada) {
        this.envioService = envioService;
        this.entrada = entrada;
        this.tipoEnvioSelector = new TipoEnvioSelector(entrada);
        this.estadoEnvioSelector = new EstadoEnvioSelector(entrada);
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
        ConsolaUtils.imprimirTitulo("GESTION DE ENVIOS");
        ConsolaUtils.imprimirMensajeInfo("Gestion de envios y seguimiento logistico.");
        ConsolaUtils.imprimirMenuOpciones(
                "1. Crear envio manual",
                "2. Buscar envio por codigo de seguimiento",
                "3. Listar envios",
                "4. Listar envios por estado",
                "5. Actualizar estado de envio",
                "6. Consultar historial de envio",
                "7. Consultar fecha estimada de entrega",
                "8. Eliminar envio",
                "0. Volver");
    }

    private void ejecutarOpcion(int opcion) {
        try {
            switch (opcion) {
                case 1 -> crearEnvio();
                case 2 -> buscarEnvio();
                case 3 -> listarEnvios();
                case 4 -> listarEnviosPorEstado();
                case 5 -> actualizarEstado();
                case 6 -> consultarHistorial();
                case 7 -> consultarFechaEstimada();
                case 8 -> eliminarEnvio();
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

    private void crearEnvio() {
        ConsolaUtils.imprimirTitulo("CREAR ENVIO");
        TipoEnvio tipoEnvio = tipoEnvioSelector.seleccionarTipoEnvio();
        String direccion = entrada.leerTexto("Direccion: ");
        String provincia = entrada.leerTexto("Provincia: ");
        String ciudad = entrada.leerTexto("Ciudad: ");
        String codigoPostal = entrada.leerTexto("Codigo postal: ");

        Envio envio = envioService.crearEnvio(direccion, provincia, ciudad, codigoPostal, tipoEnvio);
        ConsolaUtils.imprimirMensajeExito("Envio creado correctamente.");
        ConsolaUtils.imprimirEnvio(envio);
    }

    private void buscarEnvio() {
        ConsolaUtils.imprimirTitulo("BUSCAR ENVIO");
        String codigo = entrada.leerTexto("Codigo de seguimiento: ");
        ConsolaUtils.imprimirEnvio(envioService.buscarPorCodigoSeguimiento(codigo));
    }

    private void listarEnvios() {
        ConsolaUtils.imprimirTitulo("LISTADO DE ENVIOS");
        ConsolaUtils.imprimirEnvios(envioService.listarEnvios());
    }

    private void listarEnviosPorEstado() {
        ConsolaUtils.imprimirTitulo("ENVIOS POR ESTADO");
        EstadoEnvio estado = estadoEnvioSelector.seleccionarEstadoEnvio();
        ConsolaUtils.imprimirEnvios(envioService.listarPorEstado(estado));
    }

    private void actualizarEstado() {
        ConsolaUtils.imprimirTitulo("ACTUALIZAR ESTADO DE ENVIO");
        String codigo = entrada.leerTexto("Codigo de seguimiento: ");
        EstadoEnvio estado = estadoEnvioSelector.seleccionarEstadoEnvio();
        envioService.actualizarEstado(codigo, estado);
        ConsolaUtils.imprimirMensajeExito("Estado actualizado correctamente.");
    }

    private void consultarHistorial() {
        ConsolaUtils.imprimirTitulo("HISTORIAL DE ENVIO");
        String codigo = entrada.leerTexto("Codigo de seguimiento: ");
        ConsolaUtils.imprimirHistorialEnvio(envioService.consultarHistorial(codigo));
    }

    private void consultarFechaEstimada() {
        ConsolaUtils.imprimirTitulo("FECHA ESTIMADA DE ENTREGA");
        String codigo = entrada.leerTexto("Codigo de seguimiento: ");
        System.out.println("Fecha estimada: " + envioService.calcularFechaEstimada(codigo));
    }

    private void eliminarEnvio() {
        ConsolaUtils.imprimirTitulo("ELIMINAR ENVIO");
        String codigo = entrada.leerTexto("Codigo de seguimiento: ");

        if (entrada.confirmar("El envio sera eliminado.")) {
            envioService.eliminarEnvio(codigo);
            ConsolaUtils.imprimirMensajeExito("Envio eliminado correctamente.");
        } else {
            ConsolaUtils.imprimirMensajeInfo("Operacion cancelada.");
        }
    }
}
