package ecommerce.ui;

import ecommerce.enums.RolUsuario;
import ecommerce.model.Carrito;
import ecommerce.model.Envio;
import ecommerce.model.EnvioHistorialEstado;
import ecommerce.model.Categoria;
import ecommerce.model.InventarioMovimiento;
import ecommerce.model.ItemCarrito;
import ecommerce.model.ItemOrden;
import ecommerce.model.OrdenCompra;
import ecommerce.model.Pago;
import ecommerce.model.Producto;
import ecommerce.model.ProductoDigital;
import ecommerce.model.ProductoFisico;
import ecommerce.model.ProductoImportado;
import ecommerce.model.Usuario;

import java.util.List;

public final class ConsolaUtils {

    private ConsolaUtils() {
    }

    public static void imprimirTitulo(String titulo) {
        System.out.println();
        System.out.println("=== " + titulo + " ===");
    }

    public static void imprimirUsuarios(List<Usuario> usuarios) {
        if (usuarios.isEmpty()) {
            System.out.println("No hay usuarios registrados.");
            return;
        }

        System.out.printf("%-5s %-18s %-18s %-30s %-22s %-12s%n",
                "ID", "Nombre", "Apellido", "Email", "Rol", "Estado");
        System.out.println("------------------------------------------------------------------------------------------------------");

        for (Usuario usuario : usuarios) {
            imprimirUsuarioEnTabla(usuario);
        }
    }

    public static void imprimirUsuario(Usuario usuario) {
        System.out.println("ID: " + usuario.getId());
        System.out.println("Nombre: " + usuario.getNombre());
        System.out.println("Apellido: " + usuario.getApellido());
        System.out.println("Email: " + usuario.getEmail());
        System.out.println("Fecha de alta: " + usuario.getFechaAlta());
        System.out.println("Estado: " + usuario.getEstado());
        System.out.println("Rol: " + usuario.getRol());
    }

    public static void imprimirUsuarioEnTabla(Usuario usuario) {
        System.out.printf("%-5d %-18s %-18s %-30s %-22s %-12s%n",
                usuario.getId(),
                limitar(usuario.getNombre(), 18),
                limitar(usuario.getApellido(), 18),
                limitar(usuario.getEmail(), 30),
                usuario.getRol(),
                usuario.getEstado());
    }

    public static void imprimirCategorias(List<Categoria> categorias) {
        if (categorias.isEmpty()) {
            System.out.println("No hay categorías registradas.");
            return;
        }

        System.out.printf("%-5s %-24s %-45s %-12s%n", "ID", "Nombre", "Descripción", "Estado");
        System.out.println("--------------------------------------------------------------------------------------------");

        for (Categoria categoria : categorias) {
            imprimirCategoriaEnTabla(categoria);
        }
    }

    public static void imprimirCategoria(Categoria categoria) {
        System.out.println("ID: " + categoria.getId());
        System.out.println("Nombre: " + categoria.getNombre());
        System.out.println("Descripción: " + categoria.getDescripcion());
        System.out.println("Estado: " + categoria.getEstado());
    }

    public static void imprimirCategoriaEnTabla(Categoria categoria) {
        System.out.printf("%-5d %-24s %-45s %-12s%n",
                categoria.getId(),
                limitar(categoria.getNombre(), 24),
                limitar(categoria.getDescripcion(), 45),
                categoria.getEstado());
    }

    public static void imprimirProductos(List<Producto> productos) {
        if (productos.isEmpty()) {
            System.out.println("No hay productos registrados.");
            return;
        }

        System.out.printf("%-5s %-14s %-25s %-13s %-22s %-10s %-12s %-12s%n",
                "ID", "Código", "Nombre", "Tipo", "Categoría", "Stock", "Precio", "Estado");
        System.out.println("----------------------------------------------------------------------------------------------------------------");

        for (Producto producto : productos) {
            imprimirProductoEnTabla(producto);
        }
    }

    public static void imprimirProducto(Producto producto) {
        System.out.println("ID: " + producto.getId());
        System.out.println("Código: " + producto.getCodigo());
        System.out.println("Nombre: " + producto.getNombre());
        System.out.println("Descripción: " + producto.getDescripcion());
        System.out.println("Tipo: " + obtenerTipoProducto(producto));
        System.out.println("Categoría: " + producto.getCategoria().getNombre() + " (ID " + producto.getCategoria().getId() + ")");
        System.out.printf("Precio base: %.2f%n", producto.getPrecio());
        System.out.printf("Precio final: %.2f%n", producto.calcularPrecioFinal());
        System.out.println("Stock: " + producto.getStock());
        System.out.println("Peso: " + producto.getPeso());
        System.out.println("Estado: " + producto.getEstado());

        if (producto instanceof ProductoDigital productoDigital) {
            System.out.println("URL de descarga: " + productoDigital.getUrlDescarga());
        } else if (producto instanceof ProductoImportado productoImportado) {
            System.out.println("Impuesto de importación: " + productoImportado.getPorcentajeImpuestoImportacion() + "%");
        }
    }

    public static void imprimirProductoEnTabla(Producto producto) {
        System.out.printf("%-5d %-14s %-25s %-13s %-22s %-10d %-12.2f %-12s%n",
                producto.getId(),
                limitar(producto.getCodigo(), 14),
                limitar(producto.getNombre(), 25),
                obtenerTipoProducto(producto),
                limitar(producto.getCategoria().getNombre(), 22),
                producto.getStock(),
                producto.calcularPrecioFinal(),
                producto.getEstado());
    }


    public static void imprimirCarrito(Carrito carrito) {
        System.out.println("Cliente: " + carrito.getCliente().getNombre() + " "
                + carrito.getCliente().getApellido() + " - " + carrito.getCliente().getEmail());
        System.out.println("Fecha de creación: " + carrito.getFechaCreacion());

        if (carrito.estaVacio()) {
            System.out.println("El carrito está vacío.");
            return;
        }

        System.out.printf("%-14s %-28s %-10s %-15s %-15s%n",
                "Código", "Producto", "Cantidad", "Precio unit.", "Subtotal");
        System.out.println("--------------------------------------------------------------------------------------");

        for (ItemCarrito item : carrito.getItems()) {
            imprimirItemCarritoEnTabla(item);
        }

        System.out.println("--------------------------------------------------------------------------------------");
        System.out.printf("Total: %.2f%n", carrito.calcularTotal());
    }

    public static void imprimirItemCarritoEnTabla(ItemCarrito item) {
        System.out.printf("%-14s %-28s %-10d %-15.2f %-15.2f%n",
                limitar(item.getProducto().getCodigo(), 14),
                limitar(item.getProducto().getNombre(), 28),
                item.getCantidad(),
                item.getPrecioUnitario(),
                item.calcularSubtotal());
    }

    public static void imprimirMovimientosInventario(List<InventarioMovimiento> movimientos) {
        if (movimientos.isEmpty()) {
            System.out.println("No hay movimientos de inventario registrados.");
            return;
        }

        System.out.printf("%-5s %-20s %-13s %-10s %-18s %-16s %-35s%n",
                "ID", "Producto", "Tipo", "Cantidad", "Stock result.", "Fecha", "Motivo");
        System.out.println("----------------------------------------------------------------------------------------------------------------------");

        for (InventarioMovimiento movimiento : movimientos) {
            imprimirMovimientoInventarioEnTabla(movimiento);
        }
    }

    public static void imprimirMovimientoInventario(InventarioMovimiento movimiento) {
        System.out.println("ID: " + movimiento.getId());
        System.out.println("Producto: " + movimiento.getProducto().getNombre()
                + " (" + movimiento.getProducto().getCodigo() + ")");
        System.out.println("Tipo: " + movimiento.getTipo());
        System.out.println("Cantidad: " + movimiento.getCantidad());
        System.out.println("Stock resultante: " + movimiento.getStockResultante());
        System.out.println("Fecha: " + movimiento.getFecha());
        System.out.println("Motivo: " + movimiento.getMotivo());
    }

    public static void imprimirMovimientoInventarioEnTabla(InventarioMovimiento movimiento) {
        System.out.printf("%-5d %-20s %-13s %-10d %-18d %-16s %-35s%n",
                movimiento.getId(),
                limitar(movimiento.getProducto().getCodigo(), 20),
                movimiento.getTipo(),
                movimiento.getCantidad(),
                movimiento.getStockResultante(),
                movimiento.getFecha().toLocalDate(),
                limitar(movimiento.getMotivo(), 35));
    }


    public static void imprimirPagos(List<Pago> pagos) {
        if (pagos.isEmpty()) {
            System.out.println("No hay pagos registrados.");
            return;
        }

        System.out.printf("%-5s %-28s %-12s %-14s %-18s%n",
                "ID", "Método", "Monto", "Estado", "Fecha");
        System.out.println("--------------------------------------------------------------------------------");

        for (Pago pago : pagos) {
            imprimirPagoEnTabla(pago);
        }
    }

    public static void imprimirPago(Pago pago) {
        System.out.println("ID: " + pago.getId());
        System.out.println("Método de pago: " + pago.getMetodoPago());
        System.out.printf("Monto: %.2f%n", pago.getMonto());
        System.out.println("Estado: " + pago.getEstado());
        System.out.println("Fecha: " + pago.getFecha());
    }

    public static void imprimirPagoEnTabla(Pago pago) {
        System.out.printf("%-5d %-28s %-12.2f %-14s %-18s%n",
                pago.getId(),
                pago.getMetodoPago(),
                pago.getMonto(),
                pago.getEstado(),
                pago.getFecha().toLocalDate());
    }


    public static void imprimirOrdenes(List<OrdenCompra> ordenes) {
        if (ordenes.isEmpty()) {
            System.out.println("No hay órdenes registradas.");
            return;
        }

        System.out.printf("%-25s %-28s %-14s %-15s %-18s %-18s%n",
                "Número", "Cliente", "Total", "Estado", "Pago", "Fecha");
        System.out.println("----------------------------------------------------------------------------------------------------------------");

        for (OrdenCompra orden : ordenes) {
            imprimirOrdenEnTabla(orden);
        }
    }

    public static void imprimirOrden(OrdenCompra orden) {
        System.out.println("Número: " + orden.getNumero());
        System.out.println("Cliente: " + orden.getCliente().getNombre() + " "
                + orden.getCliente().getApellido() + " - " + orden.getCliente().getEmail());
        System.out.println("Fecha: " + orden.getFecha());
        System.out.println("Estado: " + orden.getEstado());
        System.out.printf("Total productos: %.2f%n", orden.getTotal());

        if (orden.getPago() != null) {
            System.out.println();
            System.out.println("Pago asociado:");
            imprimirPago(orden.getPago());
        }

        if (orden.getEnvio() != null) {
            System.out.println();
            System.out.println("Envío asociado:");
            imprimirEnvio(orden.getEnvio());
        }

        if (orden.getProductos().isEmpty()) {
            System.out.println("La orden no tiene productos asociados.");
            return;
        }

        System.out.println();
        System.out.printf("%-14s %-28s %-10s %-15s %-15s%n",
                "Código", "Producto", "Cantidad", "Precio unit.", "Subtotal");
        System.out.println("--------------------------------------------------------------------------------------");

        for (ItemOrden item : orden.getProductos()) {
            imprimirItemOrdenEnTabla(item);
        }
    }

    public static void imprimirOrdenEnTabla(OrdenCompra orden) {
        String cliente = orden.getCliente().getNombre() + " " + orden.getCliente().getApellido();
        String pago = orden.getPago() == null ? "Sin pago" : orden.getPago().getEstado().toString();
        System.out.printf("%-25s %-28s %-14.2f %-15s %-18s %-18s%n",
                limitar(orden.getNumero(), 25),
                limitar(cliente, 28),
                orden.getTotal(),
                orden.getEstado(),
                pago,
                orden.getFecha().toLocalDate());
    }

    public static void imprimirItemOrdenEnTabla(ItemOrden item) {
        System.out.printf("%-14s %-28s %-10d %-15.2f %-15.2f%n",
                limitar(item.getProducto().getCodigo(), 14),
                limitar(item.getProducto().getNombre(), 28),
                item.getCantidad(),
                item.getPrecioUnitario(),
                item.getSubtotal());
    }

    public static void imprimirEnvio(Envio envio) {
        System.out.println("Código de seguimiento: " + envio.getCodigoSeguimiento());
        System.out.println("Tipo: " + envio.getTipoEnvio());
        System.out.println("Estado: " + envio.getEstado());
        System.out.println("Dirección: " + envio.getDireccion());
        System.out.println("Provincia: " + envio.getProvincia());
        System.out.println("Ciudad: " + envio.getCiudad());
        System.out.println("Código postal: " + envio.getCodigoPostal());
        System.out.printf("Costo: %.2f%n", envio.getCosto());
    }


    public static void imprimirEnvios(List<Envio> envios) {
        if (envios.isEmpty()) {
            System.out.println("No hay envíos registrados.");
            return;
        }

        System.out.printf("%-15s %-18s %-14s %-18s %-18s %-18s %-12s%n",
                "Código", "Tipo", "Estado", "Provincia", "Ciudad", "Código postal", "Costo");
        System.out.println("----------------------------------------------------------------------------------------------------------------");

        for (Envio envio : envios) {
            imprimirEnvioEnTabla(envio);
        }
    }

    public static void imprimirEnvioEnTabla(Envio envio) {
        System.out.printf("%-15s %-18s %-14s %-18s %-18s %-18s %-12.2f%n",
                limitar(envio.getCodigoSeguimiento(), 15),
                envio.getTipoEnvio(),
                envio.getEstado(),
                limitar(envio.getProvincia(), 18),
                limitar(envio.getCiudad(), 18),
                limitar(envio.getCodigoPostal(), 18),
                envio.getCosto());
    }

    public static void imprimirHistorialEnvio(List<EnvioHistorialEstado> historial) {
        if (historial.isEmpty()) {
            System.out.println("No hay historial registrado para el envío.");
            return;
        }

        System.out.printf("%-5s %-15s %-14s %-20s %-45s%n",
                "ID", "Código", "Estado", "Fecha", "Descripción");
        System.out.println("---------------------------------------------------------------------------------------------------------------");

        for (EnvioHistorialEstado registro : historial) {
            imprimirHistorialEnvioEnTabla(registro);
        }
    }

    public static void imprimirHistorialEnvioEnTabla(EnvioHistorialEstado registro) {
        System.out.printf("%-5d %-15s %-14s %-20s %-45s%n",
                registro.getId(),
                limitar(registro.getCodigoSeguimiento(), 15),
                registro.getEstado(),
                registro.getFecha(),
                limitar(registro.getDescripcion(), 45));
    }

    public static void imprimirRoles() {
        RolUsuario[] roles = RolUsuario.values();

        for (int i = 0; i < roles.length; i++) {
            RolUsuario rol = roles[i];
            System.out.println((i + 1) + ". " + rol + " - " + describirRol(rol));
        }
    }

    public static String describirRol(RolUsuario rol) {
        return switch (rol) {
            case CLIENTE -> "consulta productos, administra carrito, realiza compras y genera reclamos";
            case ADMINISTRADOR -> "gestiona usuarios, productos, categorías, inventario y reportes";
            case OPERADOR_VENTAS -> "administra órdenes, confirma pagos y gestiona estados de pedidos";
            case RESPONSABLE_LOGISTICA -> "gestiona envíos y actualiza estados de entrega";
        };
    }

    public static String obtenerTipoProducto(Producto producto) {
        if (producto instanceof ProductoDigital) {
            return "DIGITAL";
        }
        if (producto instanceof ProductoImportado) {
            return "IMPORTADO";
        }
        if (producto instanceof ProductoFisico) {
            return "FISICO";
        }
        return producto.getClass().getSimpleName().toUpperCase();
    }

    private static String limitar(String valor, int maximo) {
        if (valor == null) {
            return "";
        }
        if (valor.length() <= maximo) {
            return valor;
        }
        return valor.substring(0, maximo - 3) + "...";
    }
}
