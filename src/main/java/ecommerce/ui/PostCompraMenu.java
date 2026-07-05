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

public class PostCompraMenu {

    private final ReclamoService reclamoService;
    private final DevolucionService devolucionService;
    private final CalificacionService calificacionService;
    private final EntradaConsola entrada;
    private final ClienteSelector clienteSelector;
    private final EstadoReclamoSelector estadoReclamoSelector;
    private final EstadoDevolucionSelector estadoDevolucionSelector;

    public PostCompraMenu(ReclamoService reclamoService, DevolucionService devolucionService,
            CalificacionService calificacionService, UsuarioService usuarioService, EntradaConsola entrada) {
        this.reclamoService = reclamoService;
        this.devolucionService = devolucionService;
        this.calificacionService = calificacionService;
        this.entrada = entrada;
        this.clienteSelector = new ClienteSelector(usuarioService, entrada);
        this.estadoReclamoSelector = new EstadoReclamoSelector(entrada);
        this.estadoDevolucionSelector = new EstadoDevolucionSelector(entrada);
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
        ConsolaUtils.imprimirTitulo("RECLAMOS, DEVOLUCIONES Y CALIFICACIONES");
        System.out.println("1. Generar reclamo");
        System.out.println("2. Buscar reclamo");
        System.out.println("3. Listar reclamos");
        System.out.println("4. Listar reclamos por estado");
        System.out.println("5. Cambiar estado de reclamo");
        System.out.println("6. Eliminar reclamo");
        System.out.println("7. Solicitar devolución");
        System.out.println("8. Buscar devolución");
        System.out.println("9. Listar devoluciones");
        System.out.println("10. Listar devoluciones por cliente");
        System.out.println("11. Listar devoluciones por producto");
        System.out.println("12. Listar devoluciones por estado");
        System.out.println("13. Cambiar estado de devolución");
        System.out.println("14. Eliminar devolución");
        System.out.println("15. Calificar producto");
        System.out.println("16. Buscar calificación");
        System.out.println("17. Listar calificaciones");
        System.out.println("18. Listar calificaciones por cliente");
        System.out.println("19. Listar calificaciones por producto");
        System.out.println("20. Consultar promedio de producto");
        System.out.println("21. Eliminar calificación");
        System.out.println("0. Volver");
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
                default -> System.out.println("Opción incorrecta.");
            }
        } catch (EcommerceException ex) {
            System.out.println("Error: " + ex.getMessage());
        }

        if (opcion != 0) {
            entrada.pausar();
        }
    }

    private void generarReclamo() {
        ConsolaUtils.imprimirTitulo("GENERAR RECLAMO");
        Usuario cliente = clienteSelector.seleccionarClienteActivo();
        String numeroOrden = entrada.leerTexto("Número de orden: ");
        String motivo = entrada.leerTexto("Motivo: ");

        Reclamo reclamo = reclamoService.generarReclamo(cliente, numeroOrden, motivo);
        System.out.println("Reclamo generado correctamente.");
        ConsolaUtils.imprimirReclamo(reclamo);
    }

    private void buscarReclamo() {
        ConsolaUtils.imprimirTitulo("BUSCAR RECLAMO");
        String numero = entrada.leerTexto("Número de reclamo: ");
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
        String numero = entrada.leerTexto("Número de reclamo: ");
        EstadoReclamo estado = estadoReclamoSelector.seleccionarEstadoReclamo();
        reclamoService.cambiarEstado(numero, estado);
        System.out.println("Estado actualizado correctamente.");
    }

    private void eliminarReclamo() {
        ConsolaUtils.imprimirTitulo("ELIMINAR RECLAMO");
        String numero = entrada.leerTexto("Número de reclamo: ");

        if (entrada.confirmar("El reclamo será eliminado.")) {
            reclamoService.eliminarReclamo(numero);
            System.out.println("Reclamo eliminado correctamente.");
        } else {
            System.out.println("Operación cancelada.");
        }
    }

    private void solicitarDevolucion() {
        ConsolaUtils.imprimirTitulo("SOLICITAR DEVOLUCIÓN");
        Usuario cliente = clienteSelector.seleccionarClienteActivo();
        int productoId = entrada.leerEntero("ID del producto: ");
        String motivo = entrada.leerTexto("Motivo: ");

        Devolucion devolucion = devolucionService.solicitarDevolucion(cliente, productoId, motivo);
        System.out.println("Devolución solicitada correctamente.");
        ConsolaUtils.imprimirDevolucion(devolucion);
    }

    private void buscarDevolucion() {
        ConsolaUtils.imprimirTitulo("BUSCAR DEVOLUCIÓN");
        int id = entrada.leerEntero("ID de devolución: ");
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
        ConsolaUtils.imprimirTitulo("CAMBIAR ESTADO DE DEVOLUCIÓN");
        int id = entrada.leerEntero("ID de devolución: ");
        EstadoDevolucion estado = estadoDevolucionSelector.seleccionarEstadoDevolucion();
        devolucionService.cambiarEstado(id, estado);
        System.out.println("Estado actualizado correctamente.");
    }

    private void eliminarDevolucion() {
        ConsolaUtils.imprimirTitulo("ELIMINAR DEVOLUCIÓN");
        int id = entrada.leerEntero("ID de devolución: ");

        if (entrada.confirmar("La devolución será eliminada.")) {
            devolucionService.eliminarDevolucion(id);
            System.out.println("Devolución eliminada correctamente.");
        } else {
            System.out.println("Operación cancelada.");
        }
    }

    private void calificarProducto() {
        ConsolaUtils.imprimirTitulo("CALIFICAR PRODUCTO");
        Usuario cliente = clienteSelector.seleccionarClienteActivo();
        int productoId = entrada.leerEntero("ID del producto: ");
        int puntuacion = entrada.leerOpcion("Puntuación de 1 a 5: ", 1, 5);
        String comentario = entrada.leerTexto("Comentario: ");

        Calificacion calificacion = calificacionService.calificarProducto(cliente, productoId, puntuacion, comentario);
        System.out.println("Calificación registrada correctamente.");
        ConsolaUtils.imprimirCalificacion(calificacion);
    }

    private void buscarCalificacion() {
        ConsolaUtils.imprimirTitulo("BUSCAR CALIFICACIÓN");
        int id = entrada.leerEntero("ID de calificación: ");
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
        ConsolaUtils.imprimirTitulo("ELIMINAR CALIFICACIÓN");
        int id = entrada.leerEntero("ID de calificación: ");

        if (entrada.confirmar("La calificación será eliminada.")) {
            calificacionService.eliminarCalificacion(id);
            System.out.println("Calificación eliminada correctamente.");
        } else {
            System.out.println("Operación cancelada.");
        }
    }
}
