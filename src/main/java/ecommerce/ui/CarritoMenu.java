package ecommerce.ui;

import ecommerce.exception.EcommerceException;
import ecommerce.model.Carrito;
import ecommerce.model.Producto;
import ecommerce.model.Usuario;
import ecommerce.service.CarritoService;
import ecommerce.service.CarritoSesionService;
import ecommerce.service.ProductoService;
import ecommerce.service.UsuarioService;

import java.util.List;

public class CarritoMenu {

    private final CarritoService carritoService;
    private final ProductoService productoService;
    private final EntradaConsola entrada;
    private final ClienteSelector clienteSelector;
    private final CarritoSesionService carritoSesionService;
    private Usuario clienteActual;

    public CarritoMenu(CarritoService carritoService, ProductoService productoService,
            UsuarioService usuarioService, CarritoSesionService carritoSesionService, EntradaConsola entrada) {
        this.carritoService = carritoService;
        this.productoService = productoService;
        this.entrada = entrada;
        this.clienteSelector = new ClienteSelector(usuarioService, entrada);
        this.carritoSesionService = carritoSesionService;
    }

    public void mostrar() {
        int opcion;

        do {
            imprimirEncabezado();
            System.out.println("1. Seleccionar cliente");
            System.out.println("2. Crear o recuperar carrito activo");
            System.out.println("3. Agregar producto");
            System.out.println("4. Eliminar producto");
            System.out.println("5. Modificar cantidad");
            System.out.println("6. Vaciar carrito");
            System.out.println("7. Visualizar carrito");
            System.out.println("8. Calcular subtotal");
            System.out.println("9. Calcular total");
            System.out.println("10. Listar productos disponibles");
            System.out.println("0. Volver");

            opcion = entrada.leerEntero("Opción: ");
            ejecutarOpcion(opcion);
        } while (opcion != 0);
    }

    private void imprimirEncabezado() {
        ConsolaUtils.imprimirTitulo("CARRITO DE COMPRAS");
        if (clienteActual == null) {
            System.out.println("Cliente seleccionado: ninguno");
        } else {
            System.out.println("Cliente seleccionado: " + clienteActual.getNombre() + " "
                    + clienteActual.getApellido() + " - " + clienteActual.getEmail());
        }
        System.out.println();
    }

    private void ejecutarOpcion(int opcion) {
        try {
            switch (opcion) {
                case 1 -> seleccionarCliente();
                case 2 -> crearORecuperarCarrito();
                case 3 -> agregarProducto();
                case 4 -> eliminarProducto();
                case 5 -> modificarCantidad();
                case 6 -> vaciarCarrito();
                case 7 -> visualizarCarrito();
                case 8 -> calcularSubtotal();
                case 9 -> calcularTotal();
                case 10 -> listarProductosDisponibles();
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

    private void seleccionarCliente() {
        ConsolaUtils.imprimirTitulo("SELECCIONAR CLIENTE");
        clienteActual = clienteSelector.seleccionarClienteActivo();
        obtenerCarritoActual();
        System.out.println("Cliente seleccionado correctamente.");
    }

    private void crearORecuperarCarrito() {
        Carrito carrito = obtenerCarritoActual();
        System.out.println("Carrito activo disponible para el cliente seleccionado.");
        ConsolaUtils.imprimirCarrito(carrito);
    }

    private void agregarProducto() {
        Carrito carrito = obtenerCarritoActual();
        ConsolaUtils.imprimirTitulo("AGREGAR PRODUCTO AL CARRITO");
        listarProductosDisponiblesSinPausa();

        String codigo = entrada.leerTexto("Código del producto: ");
        int cantidad = entrada.leerEntero("Cantidad: ");
        carritoService.agregarProducto(carrito, codigo, cantidad);

        System.out.println("Producto agregado correctamente.");
        ConsolaUtils.imprimirCarrito(carrito);
    }

    private void eliminarProducto() {
        Carrito carrito = obtenerCarritoActual();
        carrito.validarNoVacio();
        ConsolaUtils.imprimirTitulo("ELIMINAR PRODUCTO DEL CARRITO");
        ConsolaUtils.imprimirCarrito(carrito);

        String codigo = entrada.leerTexto("Código del producto: ");
        carritoService.eliminarProducto(carrito, codigo);

        System.out.println("Producto eliminado del carrito.");
        ConsolaUtils.imprimirCarrito(carrito);
    }

    private void modificarCantidad() {
        Carrito carrito = obtenerCarritoActual();
        carrito.validarNoVacio();
        ConsolaUtils.imprimirTitulo("MODIFICAR CANTIDAD");
        ConsolaUtils.imprimirCarrito(carrito);

        String codigo = entrada.leerTexto("Código del producto: ");
        int cantidad = entrada.leerEntero("Nueva cantidad: ");
        carritoService.modificarCantidad(carrito, codigo, cantidad);

        System.out.println("Cantidad modificada correctamente.");
        ConsolaUtils.imprimirCarrito(carrito);
    }

    private void vaciarCarrito() {
        Carrito carrito = obtenerCarritoActual();
        carrito.validarNoVacio();
        ConsolaUtils.imprimirTitulo("VACIAR CARRITO");
        ConsolaUtils.imprimirCarrito(carrito);

        if (entrada.confirmar("El carrito quedará vacío.")) {
            carritoService.vaciarCarrito(carrito);
            System.out.println("Carrito vaciado correctamente.");
        } else {
            System.out.println("Operación cancelada.");
        }
    }

    private void visualizarCarrito() {
        Carrito carrito = obtenerCarritoActual();
        ConsolaUtils.imprimirTitulo("VISUALIZAR CARRITO");
        ConsolaUtils.imprimirCarrito(carrito);
    }

    private void calcularSubtotal() {
        Carrito carrito = obtenerCarritoActual();
        System.out.printf("Subtotal: %.2f%n", carritoService.calcularSubtotal(carrito));
    }

    private void calcularTotal() {
        Carrito carrito = obtenerCarritoActual();
        System.out.printf("Total: %.2f%n", carritoService.calcularTotal(carrito));
    }

    private void listarProductosDisponibles() {
        ConsolaUtils.imprimirTitulo("PRODUCTOS DISPONIBLES");
        listarProductosDisponiblesSinPausa();
    }

    private void listarProductosDisponiblesSinPausa() {
        List<Producto> productos = productoService.listarProductos().stream()
                .filter(producto -> producto.validarDisponibilidad(1))
                .toList();
        ConsolaUtils.imprimirProductos(productos);
    }

    private Carrito obtenerCarritoActual() {
        if (clienteActual == null) {
            clienteActual = clienteSelector.seleccionarClienteActivo();
        }

        return carritoSesionService.obtenerOCrearCarrito(clienteActual);
    }
}
