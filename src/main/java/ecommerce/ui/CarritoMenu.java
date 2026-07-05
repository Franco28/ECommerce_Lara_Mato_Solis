package ecommerce.ui;

import ecommerce.exception.EcommerceException;
import ecommerce.model.Carrito;
import ecommerce.model.Producto;
import ecommerce.model.Usuario;
import ecommerce.service.CarritoService;
import ecommerce.service.CarritoSesionService;
import ecommerce.service.ProductoService;
import ecommerce.service.SesionUsuarioService;
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
            UsuarioService usuarioService, CarritoSesionService carritoSesionService,
            SesionUsuarioService sesionUsuarioService, EntradaConsola entrada) {
        this.carritoService = carritoService;
        this.productoService = productoService;
        this.entrada = entrada;
        this.clienteSelector = new ClienteSelector(usuarioService, entrada, sesionUsuarioService);
        this.carritoSesionService = carritoSesionService;
    }

    public void mostrar() {
        int opcion;

        do {
            imprimirEncabezado();
            ConsolaUtils.imprimirMensajeInfo("Trabaja solo con el cliente seleccionado.");
            ConsolaUtils.imprimirMenuOpciones(
                    "1. Seleccionar cliente",
                    "2. Crear o recuperar carrito activo",
                    "3. Agregar producto",
                    "4. Eliminar producto",
                    "5. Modificar cantidad",
                    "6. Vaciar carrito",
                    "7. Visualizar carrito",
                    "8. Calcular subtotal",
                    "9. Calcular total",
                    "10. Listar productos disponibles",
                    "0. Volver");

            opcion = entrada.leerEntero("Opcion: ");
            ejecutarOpcion(opcion);
        } while (opcion != 0);
    }

    private void imprimirEncabezado() {
        ConsolaUtils.imprimirTitulo("CARRITO DE COMPRAS");
        if (clienteActual == null) {
            ConsolaUtils.imprimirEtiquetaValor("Cliente seleccionado", "ninguno");
        } else {
            ConsolaUtils.imprimirEtiquetaValor("Cliente seleccionado",
                    clienteActual.getNombre() + " " + clienteActual.getApellido() + " - " + clienteActual.getEmail());
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
                default -> ConsolaUtils.imprimirMensajeError("Opcion incorrecta.");
            }
        } catch (EcommerceException ex) {
            ConsolaUtils.imprimirMensajeError(ex.getMessage());
        }

        if (opcion != 0) {
            entrada.pausar();
        }
    }

    private void seleccionarCliente() {
        ConsolaUtils.imprimirTitulo("SELECCIONAR CLIENTE");
        clienteActual = clienteSelector.seleccionarClienteActivo();
        obtenerCarritoActual();
        ConsolaUtils.imprimirMensajeExito("Cliente seleccionado correctamente.");
    }

    private void crearORecuperarCarrito() {
        Carrito carrito = obtenerCarritoActual();
        ConsolaUtils.imprimirMensajeInfo("Carrito activo disponible para el cliente seleccionado.");
        ConsolaUtils.imprimirCarrito(carrito);
    }

    private void agregarProducto() {
        Carrito carrito = obtenerCarritoActual();
        ConsolaUtils.imprimirTitulo("AGREGAR PRODUCTO AL CARRITO");
        listarProductosDisponiblesSinPausa();

        String codigo = entrada.leerTexto("Codigo del producto: ");
        int cantidad = entrada.leerEntero("Cantidad: ");
        carritoService.agregarProducto(carrito, codigo, cantidad);

        ConsolaUtils.imprimirMensajeExito("Producto agregado correctamente.");
        ConsolaUtils.imprimirCarrito(carrito);
    }

    private void eliminarProducto() {
        Carrito carrito = obtenerCarritoActual();
        carrito.validarNoVacio();
        ConsolaUtils.imprimirTitulo("ELIMINAR PRODUCTO DEL CARRITO");
        ConsolaUtils.imprimirCarrito(carrito);

        String codigo = entrada.leerTexto("Codigo del producto: ");
        carritoService.eliminarProducto(carrito, codigo);

        ConsolaUtils.imprimirMensajeExito("Producto eliminado del carrito.");
        ConsolaUtils.imprimirCarrito(carrito);
    }

    private void modificarCantidad() {
        Carrito carrito = obtenerCarritoActual();
        carrito.validarNoVacio();
        ConsolaUtils.imprimirTitulo("MODIFICAR CANTIDAD");
        ConsolaUtils.imprimirCarrito(carrito);

        String codigo = entrada.leerTexto("Codigo del producto: ");
        int cantidad = entrada.leerEntero("Nueva cantidad: ");
        carritoService.modificarCantidad(carrito, codigo, cantidad);

        ConsolaUtils.imprimirMensajeExito("Cantidad modificada correctamente.");
        ConsolaUtils.imprimirCarrito(carrito);
    }

    private void vaciarCarrito() {
        Carrito carrito = obtenerCarritoActual();
        carrito.validarNoVacio();
        ConsolaUtils.imprimirTitulo("VACIAR CARRITO");
        ConsolaUtils.imprimirCarrito(carrito);

        if (entrada.confirmar("El carrito quedara vacio.")) {
            carritoService.vaciarCarrito(carrito);
            ConsolaUtils.imprimirMensajeExito("Carrito vaciado correctamente.");
        } else {
            ConsolaUtils.imprimirMensajeInfo("Operacion cancelada.");
        }
    }

    private void visualizarCarrito() {
        Carrito carrito = obtenerCarritoActual();
        ConsolaUtils.imprimirTitulo("VISUALIZAR CARRITO");
        ConsolaUtils.imprimirCarrito(carrito);
    }

    private void calcularSubtotal() {
        Carrito carrito = obtenerCarritoActual();
        ConsolaUtils.imprimirMensajeInfo(String.format("Subtotal: %.2f", carritoService.calcularSubtotal(carrito)));
    }

    private void calcularTotal() {
        Carrito carrito = obtenerCarritoActual();
        ConsolaUtils.imprimirMensajeInfo(String.format("Total: %.2f", carritoService.calcularTotal(carrito)));
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
