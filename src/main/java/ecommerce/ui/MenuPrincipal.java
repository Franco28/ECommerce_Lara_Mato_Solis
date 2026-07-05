package ecommerce.ui;

import ecommerce.exception.EcommerceException;
import ecommerce.service.ServiceFactory;

public class MenuPrincipal {

    private final EntradaConsola entrada;
    private final UsuarioMenu usuarioMenu;
    private final RolMenu rolMenu;
    private final ProductoMenu productoMenu;
    private final CategoriaMenu categoriaMenu;
    private final InventarioMenu inventarioMenu;
    private final CarritoMenu carritoMenu;
    private final PagoMenu pagoMenu;

    public MenuPrincipal(ServiceFactory serviceFactory, EntradaConsola entrada) {
        this.entrada = entrada;
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
                entrada);
        this.pagoMenu = new PagoMenu(serviceFactory.pagoService(), entrada);
    }

    public void mostrar() {
        int opcion;

        do {
            imprimirMenu();
            opcion = entrada.leerEntero("Opción: ");
            ejecutarOpcion(opcion);
        } while (opcion != 13);
    }

    private void imprimirMenu() {
        ConsolaUtils.imprimirTitulo("SISTEMA E-COMMERCE");
        System.out.println("1. Gestión de Usuarios");
        System.out.println("2. Gestión de Roles");
        System.out.println("3. Gestión de Productos");
        System.out.println("4. Gestión de Categorías");
        System.out.println("5. Gestión de Inventario");
        System.out.println("6. Carrito de Compras");
        System.out.println("7. Órdenes de Compra");
        System.out.println("8. Procesamiento de Pagos");
        System.out.println("9. Gestión de Envíos");
        System.out.println("10. Seguimiento de Pedidos");
        System.out.println("11. Reclamos y Devoluciones");
        System.out.println("12. Reportes");
        System.out.println("13. Salir");
    }

    private void ejecutarOpcion(int opcion) {
        try {
            switch (opcion) {
                case 1 -> usuarioMenu.mostrar();
                case 2 -> rolMenu.mostrar();
                case 3 -> productoMenu.mostrar();
                case 4 -> categoriaMenu.mostrar();
                case 5 -> inventarioMenu.mostrar();
                case 6 -> carritoMenu.mostrar();
                case 8 -> pagoMenu.mostrar();
                case 7, 9, 10, 11, 12 -> moduloNoDisponible();
                case 13 -> System.out.println("Saliendo del sistema.");
                default -> System.out.println("Opción incorrecta.");
            }
        } catch (EcommerceException ex) {
            System.out.println("Error: " + ex.getMessage());
            entrada.pausar();
        }
    }

    private void moduloNoDisponible() {
        System.out.println("El módulo seleccionado todavía no está disponible desde el menú principal.");
        entrada.pausar();
    }
}
