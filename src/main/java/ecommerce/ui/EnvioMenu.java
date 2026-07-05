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
            opcion = entrada.leerEntero("Opción: ");
            ejecutarOpcion(opcion);
        } while (opcion != 0);
    }

    private void imprimirMenu() {
        ConsolaUtils.imprimirTitulo("GESTIÓN DE ENVÍOS");
        System.out.println("1. Crear envío manual");
        System.out.println("2. Buscar envío por código de seguimiento");
        System.out.println("3. Listar envíos");
        System.out.println("4. Listar envíos por estado");
        System.out.println("5. Actualizar estado de envío");
        System.out.println("6. Consultar historial de envío");
        System.out.println("7. Consultar fecha estimada de entrega");
        System.out.println("8. Eliminar envío");
        System.out.println("0. Volver");
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
                default -> System.out.println("Opción incorrecta.");
            }
        } catch (EcommerceException ex) {
            System.out.println("Error: " + ex.getMessage());
        }

        if (opcion != 0) {
            entrada.pausar();
        }
    }

    private void crearEnvio() {
        ConsolaUtils.imprimirTitulo("CREAR ENVÍO");
        TipoEnvio tipoEnvio = tipoEnvioSelector.seleccionarTipoEnvio();
        String direccion = entrada.leerTexto("Dirección: ");
        String provincia = entrada.leerTexto("Provincia: ");
        String ciudad = entrada.leerTexto("Ciudad: ");
        String codigoPostal = entrada.leerTexto("Código postal: ");

        Envio envio = envioService.crearEnvio(direccion, provincia, ciudad, codigoPostal, tipoEnvio);
        System.out.println("Envío creado correctamente.");
        ConsolaUtils.imprimirEnvio(envio);
    }

    private void buscarEnvio() {
        ConsolaUtils.imprimirTitulo("BUSCAR ENVÍO");
        String codigo = entrada.leerTexto("Código de seguimiento: ");
        ConsolaUtils.imprimirEnvio(envioService.buscarPorCodigoSeguimiento(codigo));
    }

    private void listarEnvios() {
        ConsolaUtils.imprimirTitulo("LISTADO DE ENVÍOS");
        ConsolaUtils.imprimirEnvios(envioService.listarEnvios());
    }

    private void listarEnviosPorEstado() {
        ConsolaUtils.imprimirTitulo("ENVÍOS POR ESTADO");
        EstadoEnvio estado = estadoEnvioSelector.seleccionarEstadoEnvio();
        ConsolaUtils.imprimirEnvios(envioService.listarPorEstado(estado));
    }

    private void actualizarEstado() {
        ConsolaUtils.imprimirTitulo("ACTUALIZAR ESTADO DE ENVÍO");
        String codigo = entrada.leerTexto("Código de seguimiento: ");
        EstadoEnvio estado = estadoEnvioSelector.seleccionarEstadoEnvio();
        envioService.actualizarEstado(codigo, estado);
        System.out.println("Estado actualizado correctamente.");
    }

    private void consultarHistorial() {
        ConsolaUtils.imprimirTitulo("HISTORIAL DE ENVÍO");
        String codigo = entrada.leerTexto("Código de seguimiento: ");
        ConsolaUtils.imprimirHistorialEnvio(envioService.consultarHistorial(codigo));
    }

    private void consultarFechaEstimada() {
        ConsolaUtils.imprimirTitulo("FECHA ESTIMADA DE ENTREGA");
        String codigo = entrada.leerTexto("Código de seguimiento: ");
        System.out.println("Fecha estimada: " + envioService.calcularFechaEstimada(codigo));
    }

    private void eliminarEnvio() {
        ConsolaUtils.imprimirTitulo("ELIMINAR ENVÍO");
        String codigo = entrada.leerTexto("Código de seguimiento: ");

        if (entrada.confirmar("El envío será eliminado.")) {
            envioService.eliminarEnvio(codigo);
            System.out.println("Envío eliminado correctamente.");
        } else {
            System.out.println("Operación cancelada.");
        }
    }
}
