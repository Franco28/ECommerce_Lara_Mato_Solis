package ecommerce.ui;

import ecommerce.exception.DatosInvalidosException;
import ecommerce.exception.EcommerceException;
import ecommerce.model.InventarioMovimiento;
import ecommerce.model.Producto;
import ecommerce.service.InventarioService;
import ecommerce.service.ProductoService;

public class InventarioMenu {

    private final InventarioService inventarioService;
    private final ProductoService productoService;
    private final EntradaConsola entrada;

    public InventarioMenu(InventarioService inventarioService, ProductoService productoService, EntradaConsola entrada) {
        this.inventarioService = inventarioService;
        this.productoService = productoService;
        this.entrada = entrada;
    }

    public void mostrar() {
        int opcion;

        do {
            ConsolaUtils.imprimirTitulo("GESTION DE INVENTARIO");
            ConsolaUtils.imprimirMensajeInfo("Control de stock y movimientos por producto.");
            ConsolaUtils.imprimirMenuOpciones(
                    "1. Ingreso de stock",
                    "2. Egreso de stock",
                    "3. Ajuste de stock",
                    "4. Consultar stock",
                    "5. Historial por producto",
                    "6. Listar movimientos",
                    "0. Volver");

            opcion = entrada.leerEntero("Opcion: ");
            ejecutarOpcion(opcion);
        } while (opcion != 0);
    }

    private void ejecutarOpcion(int opcion) {
        try {
            switch (opcion) {
                case 1 -> ingresarStock();
                case 2 -> egresarStock();
                case 3 -> ajustarStock();
                case 4 -> consultarStock();
                case 5 -> historialPorProducto();
                case 6 -> listarMovimientos();
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

    private void ingresarStock() {
        ConsolaUtils.imprimirTitulo("INGRESO DE STOCK");
        Producto producto = seleccionarProducto();
        int cantidad = entrada.leerEntero("Cantidad a ingresar: ");
        String motivo = entrada.leerTexto("Motivo: ");

        InventarioMovimiento movimiento = inventarioService.ingresarStock(producto.getId(), cantidad, motivo);

        ConsolaUtils.imprimirMensajeExito("Stock ingresado correctamente.");
        ConsolaUtils.imprimirMovimientoInventario(movimiento);
    }

    private void egresarStock() {
        ConsolaUtils.imprimirTitulo("EGRESO DE STOCK");
        Producto producto = seleccionarProducto();
        int cantidad = entrada.leerEntero("Cantidad a egresar: ");
        String motivo = entrada.leerTexto("Motivo: ");

        InventarioMovimiento movimiento = inventarioService.egresarStock(producto.getId(), cantidad, motivo);

        ConsolaUtils.imprimirMensajeExito("Stock egresado correctamente.");
        ConsolaUtils.imprimirMovimientoInventario(movimiento);
    }

    private void ajustarStock() {
        ConsolaUtils.imprimirTitulo("AJUSTE DE STOCK");
        Producto producto = seleccionarProducto();
        int nuevoStock = entrada.leerEntero("Nuevo stock: ");
        String motivo = entrada.leerTexto("Motivo: ");

        InventarioMovimiento movimiento = inventarioService.ajustarStock(producto.getId(), nuevoStock, motivo);

        ConsolaUtils.imprimirMensajeExito("Stock ajustado correctamente.");
        ConsolaUtils.imprimirMovimientoInventario(movimiento);
    }

    private void consultarStock() {
        ConsolaUtils.imprimirTitulo("CONSULTAR STOCK");
        Producto producto = seleccionarProducto();
        int stock = inventarioService.consultarStock(producto.getId());

        ConsolaUtils.imprimirEtiquetaValor("Producto", producto.getCodigo() + " - " + producto.getNombre());
        ConsolaUtils.imprimirEtiquetaValor("Stock disponible", stock);
    }

    private void historialPorProducto() {
        ConsolaUtils.imprimirTitulo("HISTORIAL DE INVENTARIO POR PRODUCTO");
        Producto producto = seleccionarProducto();
        ConsolaUtils.imprimirMovimientosInventario(
                inventarioService.obtenerHistorialPorProducto(producto.getId()));
    }

    private void listarMovimientos() {
        ConsolaUtils.imprimirTitulo("LISTADO DE MOVIMIENTOS DE INVENTARIO");
        ConsolaUtils.imprimirMovimientosInventario(inventarioService.listarMovimientos());
    }

    private Producto seleccionarProducto() {
        var productos = productoService.listarProductos();
        if (productos.isEmpty()) {
            throw new DatosInvalidosException("Debe registrar al menos un producto antes de operar inventario.");
        }

        ConsolaUtils.imprimirProductos(productos);
        int id = entrada.leerEntero("ID del producto: ");
        return productoService.buscarPorId(id);
    }
}
