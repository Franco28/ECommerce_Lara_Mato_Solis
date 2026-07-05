package ecommerce.ui;

import ecommerce.enums.RolUsuario;
import ecommerce.exception.EcommerceException;
import ecommerce.model.Usuario;
import ecommerce.service.ServiceFactory;
import ecommerce.service.SesionUsuarioService;

public class MenuPrincipal {

    private final EntradaConsola entrada;
    private final SesionUsuarioService sesionUsuarioService;
    private final UsuarioMenu usuarioMenu;
    private final RolMenu rolMenu;
    private final ProductoMenu productoMenu;
    private final CategoriaMenu categoriaMenu;
    private final InventarioMenu inventarioMenu;
    private final CarritoMenu carritoMenu;
    private final PagoMenu pagoMenu;
    private final OrdenMenu ordenMenu;
    private final EnvioMenu envioMenu;
    private final SeguimientoMenu seguimientoMenu;
    private final PostCompraMenu postCompraMenu;
    private final ReporteMenu reporteMenu;

    public MenuPrincipal(ServiceFactory serviceFactory, EntradaConsola entrada) {
        this.entrada = entrada;
        this.sesionUsuarioService = serviceFactory.sesionUsuarioService();
        this.usuarioMenu = new UsuarioMenu(serviceFactory.usuarioService(), entrada);
        this.rolMenu = new RolMenu(entrada);
        this.productoMenu = new ProductoMenu(
                serviceFactory.productoService(),
                serviceFactory.categoriaService(),
                entrada);
        this.categoriaMenu = new CategoriaMenu(serviceFactory.categoriaService(), entrada);
        this.inventarioMenu = new InventarioMenu(
                serviceFactory.inventarioService(),
                serviceFactory.productoService(),
                entrada);
        this.carritoMenu = new CarritoMenu(
                serviceFactory.carritoService(),
                serviceFactory.productoService(),
                serviceFactory.usuarioService(),
                serviceFactory.carritoSesionService(),
                serviceFactory.sesionUsuarioService(),
                entrada);
        this.pagoMenu = new PagoMenu(serviceFactory.pagoService(), entrada);
        this.ordenMenu = new OrdenMenu(
                serviceFactory.checkoutFacade(),
                serviceFactory.ordenService(),
                serviceFactory.usuarioService(),
                serviceFactory.carritoSesionService(),
                serviceFactory.sesionUsuarioService(),
                entrada);
        this.envioMenu = new EnvioMenu(serviceFactory.envioService(), entrada);
        this.seguimientoMenu = new SeguimientoMenu(serviceFactory.seguimientoService(), entrada);
        this.postCompraMenu = new PostCompraMenu(
                serviceFactory.reclamoService(),
                serviceFactory.devolucionService(),
                serviceFactory.calificacionService(),
                serviceFactory.usuarioService(),
                serviceFactory.sesionUsuarioService(),
                entrada);
        this.reporteMenu = new ReporteMenu(serviceFactory.reporteService(), entrada);
    }

    public void mostrar() {
        int opcion;

        do {
            imprimirMenu();
            opcion = entrada.leerEntero("Opcion: ");
            ejecutarOpcion(opcion);
        } while (opcion != 13 && sesionUsuarioService.haySesionActiva());
    }

    private void imprimirMenu() {
        ConsolaUtils.imprimirTitulo("SISTEMA E-COMMERCE");
        imprimirSesionActual();
        ConsolaUtils.imprimirMensajeInfo("Seleccione un modulo y presione Enter.");
        ConsolaUtils.imprimirMenuOpciones(
                "1. Gestion de Usuarios",
                "2. Gestion de Roles",
                "3. Gestion de Productos",
                "4. Gestion de Categorias",
                "5. Gestion de Inventario",
                "6. Carrito de Compras",
                "7. Ordenes de Compra",
                "8. Procesamiento de Pagos",
                "9. Gestion de Envios",
                "10. Seguimiento de Pedidos",
                "11. Reclamos y Devoluciones",
                "12. Reportes",
                "13. Salir");
    }

    private void imprimirSesionActual() {
        Usuario usuario = sesionUsuarioService.requerirUsuarioActual();
        ConsolaUtils.imprimirEtiquetaValor("Usuario", usuario.getNombre() + " " + usuario.getApellido());
        ConsolaUtils.imprimirEtiquetaValor("Rol", usuario.getRol());
        System.out.println();
    }

    private void ejecutarOpcion(int opcion) {
        try {
            validarAcceso(opcion);
            switch (opcion) {
                case 1 -> usuarioMenu.mostrar();
                case 2 -> rolMenu.mostrar();
                case 3 -> productoMenu.mostrar();
                case 4 -> categoriaMenu.mostrar();
                case 5 -> inventarioMenu.mostrar();
                case 6 -> carritoMenu.mostrar();
                case 7 -> ordenMenu.mostrar();
                case 8 -> pagoMenu.mostrar();
                case 9 -> envioMenu.mostrar();
                case 10 -> seguimientoMenu.mostrar();
                case 11 -> postCompraMenu.mostrar();
                case 12 -> reporteMenu.mostrar();
                case 13 -> ConsolaUtils.imprimirMensajeInfo("Saliendo del menu principal.");
                default -> ConsolaUtils.imprimirMensajeError("Opcion incorrecta.");
            }
        } catch (EcommerceException ex) {
            ConsolaUtils.imprimirMensajeError(ex.getMessage());
            entrada.pausar();
        }
    }

    private void validarAcceso(int opcion) {
        switch (opcion) {
            case 1, 2, 3, 4, 5, 12 -> sesionUsuarioService.validarRol(RolUsuario.ADMINISTRADOR);
            case 6 -> sesionUsuarioService.validarRol(RolUsuario.CLIENTE);
            case 7 -> sesionUsuarioService.validarAlgunRol(
                    RolUsuario.ADMINISTRADOR,
                    RolUsuario.OPERADOR_VENTAS,
                    RolUsuario.CLIENTE);
            case 8 -> sesionUsuarioService.validarAlgunRol(
                    RolUsuario.ADMINISTRADOR,
                    RolUsuario.OPERADOR_VENTAS);
            case 9 -> sesionUsuarioService.validarAlgunRol(
                    RolUsuario.ADMINISTRADOR,
                    RolUsuario.RESPONSABLE_LOGISTICA);
            case 10 -> sesionUsuarioService.validarAlgunRol(
                    RolUsuario.ADMINISTRADOR,
                    RolUsuario.OPERADOR_VENTAS,
                    RolUsuario.RESPONSABLE_LOGISTICA,
                    RolUsuario.CLIENTE);
            case 11 -> sesionUsuarioService.validarAlgunRol(
                    RolUsuario.ADMINISTRADOR,
                    RolUsuario.OPERADOR_VENTAS,
                    RolUsuario.CLIENTE);
            case 13 -> {
            }
            default -> {
            }
        }
    }

}
