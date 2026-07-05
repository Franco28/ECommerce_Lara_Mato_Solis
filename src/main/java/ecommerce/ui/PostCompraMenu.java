package ecommerce.ui;

import ecommerce.enums.EstadoDevolucion;
import ecommerce.enums.EstadoReclamo;
import ecommerce.exception.EcommerceException;
import ecommerce.model.Calificacion;
import ecommerce.model.Devolucion;
import ecommerce.model.Reclamo;
import ecommerce.model.Usuario;
import ecommerce.service.CalificacionService;
import ecommerce.service.DevolucionService;
import ecommerce.service.ReclamoService;
import ecommerce.service.UsuarioService;
import ecommerce.service.SesionUsuarioService;

public class PostCompraMenu {

    private final ReclamoService reclamoService;
    private final DevolucionService devolucionService;
    private final CalificacionService calificacionService;
    private final EntradaConsola entrada;
    private final ClienteSelector clienteSelector;
    private final EstadoReclamoSelector estadoReclamoSelector;
    private final EstadoDevolucionSelector estadoDevolucionSelector;

    public PostCompraMenu(ReclamoService reclamoService, DevolucionService devolucionService,
            CalificacionService calificacionService, UsuarioService usuarioService,
            SesionUsuarioService sesionUsuarioService, EntradaConsola entrada) {
        this.reclamoService = reclamoService;
        this.devolucionService = devolucionService;
        this.calificacionService = calificacionService;
        this.entrada = entrada;
        this.clienteSelector = new ClienteSelector(usuarioService, entrada, sesionUsuarioService);
        this.estadoReclamoSelector = new EstadoReclamoSelector(entrada);
        this.estadoDevolucionSelector = new EstadoDevolucionSelector(entrada);
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
        ConsolaUtils.imprimirTitulo("RECLAMOS, DEVOLUCIONES Y CALIFICACIONES");
        ConsolaUtils.imprimirMensajeInfo("Gestion de post compra: reclamos, devoluciones y valoraciones.");
        ConsolaUtils.imprimirMenuOpciones(
                "1. Generar reclamo",
                "2. Buscar reclamo",
                "3. Listar reclamos",
                "4. Listar reclamos por estado",
                "5. Cambiar estado de reclamo",
                "6. Eliminar reclamo",
                "7. Solicitar devolucion",
                "8. Buscar devolucion",
                "9. Listar devoluciones",
                "10. Listar devoluciones por cliente",
                "11. Listar devoluciones por producto",
                "12. Listar devoluciones por estado",
                "13. Cambiar estado de devolucion",
                "14. Eliminar devolucion",
                "15. Calificar producto",
                "16. Buscar calificacion",
                "17. Listar calificaciones",
                "18. Listar calificaciones por cliente",
                "19. Listar calificaciones por producto",
                "20. Consultar promedio de producto",
                "21. Eliminar calificacion",
                "0. Volver");
    }

    private void ejecutarOpcion(int opcion) {
        try {
            switch (opcion) {
                case 1 -> generarReclamo();
                case 2 -> buscarReclamo();
                case 3 -> listarReclamos();
                case 4 -> listarReclamosPorEstado();
                case 5 -> cambiarEstadoReclamo();
                case 6 -> eliminarReclamo();
                case 7 -> solicitarDevolucion();
                case 8 -> buscarDevolucion();
                case 9 -> listarDevoluciones();
                case 10 -> listarDevolucionesPorCliente();
                case 11 -> listarDevolucionesPorProducto();
                case 12 -> listarDevolucionesPorEstado();
                case 13 -> cambiarEstadoDevolucion();
                case 14 -> eliminarDevolucion();
                case 15 -> calificarProducto();
                case 16 -> buscarCalificacion();
                case 17 -> listarCalificaciones();
                case 18 -> listarCalificacionesPorCliente();
                case 19 -> listarCalificacionesPorProducto();
                case 20 -> consultarPromedioProducto();
                case 21 -> eliminarCalificacion();
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

    private void generarReclamo() {
        ConsolaUtils.imprimirTitulo("GENERAR RECLAMO");
        Usuario cliente = clienteSelector.seleccionarClienteActivo();
        String numeroOrden = entrada.leerTexto("Numero de orden: ");
        String motivo = entrada.leerTexto("Motivo: ");

        Reclamo reclamo = reclamoService.generarReclamo(cliente, numeroOrden, motivo);
        ConsolaUtils.imprimirMensajeExito("Reclamo generado correctamente.");
        ConsolaUtils.imprimirReclamo(reclamo);
    }

    private void buscarReclamo() {
        ConsolaUtils.imprimirTitulo("BUSCAR RECLAMO");
        String numero = entrada.leerTexto("Numero de reclamo: ");
        ConsolaUtils.imprimirReclamo(reclamoService.buscarPorNumero(numero));
    }

    private void listarReclamos() {
        ConsolaUtils.imprimirTitulo("LISTADO DE RECLAMOS");
        ConsolaUtils.imprimirReclamos(reclamoService.listarReclamos());
    }

    private void listarReclamosPorEstado() {
        ConsolaUtils.imprimirTitulo("RECLAMOS POR ESTADO");
        EstadoReclamo estado = estadoReclamoSelector.seleccionarEstadoReclamo();
        ConsolaUtils.imprimirReclamos(reclamoService.listarPorEstado(estado));
    }

    private void cambiarEstadoReclamo() {
        ConsolaUtils.imprimirTitulo("CAMBIAR ESTADO DE RECLAMO");
        String numero = entrada.leerTexto("Numero de reclamo: ");
        EstadoReclamo estado = estadoReclamoSelector.seleccionarEstadoReclamo();
        reclamoService.cambiarEstado(numero, estado);
        ConsolaUtils.imprimirMensajeExito("Estado actualizado correctamente.");
    }

    private void eliminarReclamo() {
        ConsolaUtils.imprimirTitulo("ELIMINAR RECLAMO");
        String numero = entrada.leerTexto("Numero de reclamo: ");

        if (entrada.confirmar("El reclamo sera eliminado.")) {
            reclamoService.eliminarReclamo(numero);
            ConsolaUtils.imprimirMensajeExito("Reclamo eliminado correctamente.");
        } else {
            ConsolaUtils.imprimirMensajeInfo("Operacion cancelada.");
        }
    }

    private void solicitarDevolucion() {
        ConsolaUtils.imprimirTitulo("SOLICITAR DEVOLUCION");
        Usuario cliente = clienteSelector.seleccionarClienteActivo();
        int productoId = entrada.leerEntero("ID del producto: ");
        String motivo = entrada.leerTexto("Motivo: ");

        Devolucion devolucion = devolucionService.solicitarDevolucion(cliente, productoId, motivo);
        ConsolaUtils.imprimirMensajeExito("Devolucion solicitada correctamente.");
        ConsolaUtils.imprimirDevolucion(devolucion);
    }

    private void buscarDevolucion() {
        ConsolaUtils.imprimirTitulo("BUSCAR DEVOLUCION");
        int id = entrada.leerEntero("ID de devolucion: ");
        ConsolaUtils.imprimirDevolucion(devolucionService.buscarPorId(id));
    }

    private void listarDevoluciones() {
        ConsolaUtils.imprimirTitulo("LISTADO DE DEVOLUCIONES");
        ConsolaUtils.imprimirDevoluciones(devolucionService.listarDevoluciones());
    }

    private void listarDevolucionesPorCliente() {
        ConsolaUtils.imprimirTitulo("DEVOLUCIONES POR CLIENTE");
        Usuario cliente = clienteSelector.seleccionarClienteActivo();
        ConsolaUtils.imprimirDevoluciones(devolucionService.listarPorCliente(cliente.getId()));
    }

    private void listarDevolucionesPorProducto() {
        ConsolaUtils.imprimirTitulo("DEVOLUCIONES POR PRODUCTO");
        int productoId = entrada.leerEntero("ID del producto: ");
        ConsolaUtils.imprimirDevoluciones(devolucionService.listarPorProducto(productoId));
    }

    private void listarDevolucionesPorEstado() {
        ConsolaUtils.imprimirTitulo("DEVOLUCIONES POR ESTADO");
        EstadoDevolucion estado = estadoDevolucionSelector.seleccionarEstadoDevolucion();
        ConsolaUtils.imprimirDevoluciones(devolucionService.listarPorEstado(estado));
    }

    private void cambiarEstadoDevolucion() {
        ConsolaUtils.imprimirTitulo("CAMBIAR ESTADO DE DEVOLUCION");
        int id = entrada.leerEntero("ID de devolucion: ");
        EstadoDevolucion estado = estadoDevolucionSelector.seleccionarEstadoDevolucion();
        devolucionService.cambiarEstado(id, estado);
        ConsolaUtils.imprimirMensajeExito("Estado actualizado correctamente.");
    }

    private void eliminarDevolucion() {
        ConsolaUtils.imprimirTitulo("ELIMINAR DEVOLUCION");
        int id = entrada.leerEntero("ID de devolucion: ");

        if (entrada.confirmar("La devolucion sera eliminada.")) {
            devolucionService.eliminarDevolucion(id);
            ConsolaUtils.imprimirMensajeExito("Devolucion eliminada correctamente.");
        } else {
            ConsolaUtils.imprimirMensajeInfo("Operacion cancelada.");
        }
    }

    private void calificarProducto() {
        ConsolaUtils.imprimirTitulo("CALIFICAR PRODUCTO");
        Usuario cliente = clienteSelector.seleccionarClienteActivo();
        int productoId = entrada.leerEntero("ID del producto: ");
        int puntuacion = entrada.leerOpcion("Puntuacion de 1 a 5: ", 1, 5);
        String comentario = entrada.leerTexto("Comentario: ");

        Calificacion calificacion = calificacionService.calificarProducto(cliente, productoId, puntuacion, comentario);
        ConsolaUtils.imprimirMensajeExito("Calificacion registrada correctamente.");
        ConsolaUtils.imprimirCalificacion(calificacion);
    }

    private void buscarCalificacion() {
        ConsolaUtils.imprimirTitulo("BUSCAR CALIFICACION");
        int id = entrada.leerEntero("ID de calificacion: ");
        ConsolaUtils.imprimirCalificacion(calificacionService.buscarPorId(id));
    }

    private void listarCalificaciones() {
        ConsolaUtils.imprimirTitulo("LISTADO DE CALIFICACIONES");
        ConsolaUtils.imprimirCalificaciones(calificacionService.listarCalificaciones());
    }

    private void listarCalificacionesPorCliente() {
        ConsolaUtils.imprimirTitulo("CALIFICACIONES POR CLIENTE");
        Usuario cliente = clienteSelector.seleccionarClienteActivo();
        ConsolaUtils.imprimirCalificaciones(calificacionService.listarPorCliente(cliente.getId()));
    }

    private void listarCalificacionesPorProducto() {
        ConsolaUtils.imprimirTitulo("CALIFICACIONES POR PRODUCTO");
        int productoId = entrada.leerEntero("ID del producto: ");
        ConsolaUtils.imprimirCalificaciones(calificacionService.listarPorProducto(productoId));
    }

    private void consultarPromedioProducto() {
        ConsolaUtils.imprimirTitulo("PROMEDIO DE CALIFICACIONES");
        int productoId = entrada.leerEntero("ID del producto: ");
        double promedio = calificacionService.obtenerPromedioPorProducto(productoId);
        System.out.printf("Promedio: %.2f%n", promedio);
    }

    private void eliminarCalificacion() {
        ConsolaUtils.imprimirTitulo("ELIMINAR CALIFICACION");
        int id = entrada.leerEntero("ID de calificacion: ");

        if (entrada.confirmar("La calificacion sera eliminada.")) {
            calificacionService.eliminarCalificacion(id);
            ConsolaUtils.imprimirMensajeExito("Calificacion eliminada correctamente.");
        } else {
            ConsolaUtils.imprimirMensajeInfo("Operacion cancelada.");
        }
    }
}
