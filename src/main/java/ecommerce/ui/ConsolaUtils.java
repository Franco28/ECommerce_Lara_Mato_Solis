package ecommerce.ui;

import ecommerce.enums.RolUsuario;
import ecommerce.model.Calificacion;
import ecommerce.model.Carrito;
import ecommerce.model.Devolucion;
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

import java.util.ArrayList;
import java.util.List;

public final class ConsolaUtils {

    private static final int ANCHO_TERMINAL = 72;

    private ConsolaUtils() {
    }

    public static void imprimirTitulo(String titulo) {
        System.out.println();
        String texto = titulo == null ? "" : titulo.trim().toUpperCase();
        int ancho = Math.max(ANCHO_TERMINAL, texto.length() + 8);
        imprimirCaja(texto, ancho, '=');
    }

    public static void imprimirSubtitulo(String texto) {
        if (texto == null || texto.isBlank()) {
            return;
        }

        System.out.println();
        String contenido = " " + texto.trim().toUpperCase() + " ";
        System.out.println("+" + repetir('-', Math.max(ANCHO_TERMINAL - 2, contenido.length())) + "+");
        System.out.println("|" + centrar(contenido, Math.max(ANCHO_TERMINAL - 2, contenido.length())) + "|");
        System.out.println("+" + repetir('-', Math.max(ANCHO_TERMINAL - 2, contenido.length())) + "+");
    }

    public static void imprimirMenuOpciones(String... opciones) {
        if (opciones == null || opciones.length == 0) {
            return;
        }

        int contenidoAncho = 0;
        for (String opcion : opciones) {
            if (opcion != null) {
                contenidoAncho = Math.max(contenidoAncho, opcion.length());
            }
        }

        int ancho = Math.max(ANCHO_TERMINAL - 4, contenidoAncho + 2);
        String borde = "+" + repetir('-', ancho + 2) + "+";

        System.out.println(borde);
        for (String opcion : opciones) {
            String texto = opcion == null ? "" : opcion;
            System.out.println("| " + padRight(texto, ancho) + " |");
        }
        System.out.println(borde);
    }

    public static void imprimirMensajeInfo(String mensaje) {
        imprimirBloqueMensaje("INFO", mensaje);
    }

    public static void imprimirMensajeExito(String mensaje) {
        imprimirBloqueMensaje("OK", mensaje);
    }

    public static void imprimirMensajeError(String mensaje) {
        imprimirBloqueMensaje("ERROR", mensaje);
    }

    public static void imprimirEtiquetaValor(String etiqueta, Object valor) {
        String izquierda = etiqueta == null ? "" : etiqueta.trim();
        String derecha = String.valueOf(valor);
        int anchoEtiqueta = Math.max(18, izquierda.length());
        System.out.printf("| %-18s : %s%n", limitar(izquierda, anchoEtiqueta), derecha);
    }

    public static void imprimirSeparador() {
        System.out.println("+" + repetir('-', ANCHO_TERMINAL - 2) + "+");
    }

    public static void imprimirUsuarios(List<Usuario> usuarios) {
        if (usuarios.isEmpty()) {
            System.out.println("No hay usuarios registrados.");
            return;
        }

        List<String[]> filas = new ArrayList<>();
        for (Usuario usuario : usuarios) {
            filas.add(new String[] {
                    String.valueOf(usuario.getId()),
                    limitar(usuario.getNombre(), 18),
                    limitar(usuario.getApellido(), 18),
                    limitar(usuario.getEmail(), 30),
                    String.valueOf(usuario.getRol()),
                    String.valueOf(usuario.getEstado())
            });
        }

        imprimirTabla(
                new String[] {"ID", "Nombre", "Apellido", "Email", "Rol", "Estado"},
                filas);
    }

    public static void imprimirUsuario(Usuario usuario) {
        imprimirFicha("USUARIO", new String[][] {
                {"ID", String.valueOf(usuario.getId())},
                {"Nombre", usuario.getNombre()},
                {"Apellido", usuario.getApellido()},
                {"Email", usuario.getEmail()},
                {"Fecha de alta", String.valueOf(usuario.getFechaAlta())},
                {"Estado", String.valueOf(usuario.getEstado())},
                {"Rol", String.valueOf(usuario.getRol())}
        });
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
            System.out.println("No hay categorias registradas.");
            return;
        }

        List<String[]> filas = new ArrayList<>();
        for (Categoria categoria : categorias) {
            filas.add(new String[] {
                    String.valueOf(categoria.getId()),
                    limitar(categoria.getNombre(), 24),
                    limitar(categoria.getDescripcion(), 45),
                    String.valueOf(categoria.getEstado())
            });
        }

        imprimirTabla(
                new String[] {"ID", "Nombre", "Descripcion", "Estado"},
                filas);
    }

    public static void imprimirCategoria(Categoria categoria) {
        imprimirFicha("CATEGORIA", new String[][] {
                {"ID", String.valueOf(categoria.getId())},
                {"Nombre", categoria.getNombre()},
                {"Descripcion", categoria.getDescripcion()},
                {"Estado", String.valueOf(categoria.getEstado())}
        });
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

        List<String[]> filas = new ArrayList<>();
        for (Producto producto : productos) {
            filas.add(new String[] {
                    String.valueOf(producto.getId()),
                    limitar(producto.getCodigo(), 14),
                    limitar(producto.getNombre(), 25),
                    obtenerTipoProducto(producto),
                    limitar(producto.getCategoria().getNombre(), 22),
                    String.valueOf(producto.getStock()),
                    String.format("%.2f", producto.calcularPrecioFinal()),
                    String.valueOf(producto.getEstado())
            });
        }

        imprimirTabla(
                new String[] {"ID", "Codigo", "Nombre", "Tipo", "Categoria", "Stock", "Precio", "Estado"},
                filas);
    }

    public static void imprimirProducto(Producto producto) {
        List<String[]> campos = new ArrayList<>();
        campos.add(new String[] {"ID", String.valueOf(producto.getId())});
        campos.add(new String[] {"Codigo", producto.getCodigo()});
        campos.add(new String[] {"Nombre", producto.getNombre()});
        campos.add(new String[] {"Descripcion", producto.getDescripcion()});
        campos.add(new String[] {"Tipo", obtenerTipoProducto(producto)});
        campos.add(new String[] {"Categoria", producto.getCategoria().getNombre() + " (ID " + producto.getCategoria().getId() + ")"});
        campos.add(new String[] {"Precio base", String.format("%.2f", producto.getPrecio())});
        campos.add(new String[] {"Precio final", String.format("%.2f", producto.calcularPrecioFinal())});
        campos.add(new String[] {"Stock", String.valueOf(producto.getStock())});
        campos.add(new String[] {"Peso", String.valueOf(producto.getPeso())});
        campos.add(new String[] {"Estado", String.valueOf(producto.getEstado())});

        if (producto instanceof ProductoDigital productoDigital) {
            campos.add(new String[] {"URL de descarga", productoDigital.getUrlDescarga()});
        } else if (producto instanceof ProductoImportado productoImportado) {
            campos.add(new String[] {"Impuesto importacion", productoImportado.getPorcentajeImpuestoImportacion() + "%"});
        }

        imprimirFicha("PRODUCTO", campos.toArray(new String[0][]));
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
        imprimirEtiquetaValor("Cliente", carrito.getCliente().getNombre() + " "
                + carrito.getCliente().getApellido() + " - " + carrito.getCliente().getEmail());
        imprimirEtiquetaValor("Fecha de creacion", carrito.getFechaCreacion());

        if (carrito.estaVacio()) {
            System.out.println("El carrito esta vacio.");
            return;
        }

        List<String[]> filas = new ArrayList<>();
        for (ItemCarrito item : carrito.getItems()) {
            filas.add(new String[] {
                    limitar(item.getProducto().getCodigo(), 14),
                    limitar(item.getProducto().getNombre(), 28),
                    String.valueOf(item.getCantidad()),
                    String.format("%.2f", item.getPrecioUnitario()),
                    String.format("%.2f", item.calcularSubtotal())
            });
        }

        imprimirTabla(
                new String[] {"Codigo", "Producto", "Cantidad", "Precio unit.", "Subtotal"},
                filas);
        imprimirMensajeExito(String.format("Total: %.2f", carrito.calcularTotal()));
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

        List<String[]> filas = new ArrayList<>();
        for (InventarioMovimiento movimiento : movimientos) {
            filas.add(new String[] {
                    String.valueOf(movimiento.getId()),
                    limitar(movimiento.getProducto().getCodigo(), 20),
                    String.valueOf(movimiento.getTipo()),
                    String.valueOf(movimiento.getCantidad()),
                    String.valueOf(movimiento.getStockResultante()),
                    String.valueOf(movimiento.getFecha().toLocalDate()),
                    limitar(movimiento.getMotivo(), 35)
            });
        }

        imprimirTabla(
                new String[] {"ID", "Producto", "Tipo", "Cantidad", "Stock result.", "Fecha", "Motivo"},
                filas);
    }

    public static void imprimirMovimientoInventario(InventarioMovimiento movimiento) {
        imprimirFicha("MOVIMIENTO DE INVENTARIO", new String[][] {
                {"ID", String.valueOf(movimiento.getId())},
                {"Producto", movimiento.getProducto().getNombre() + " (" + movimiento.getProducto().getCodigo() + ")"},
                {"Tipo", String.valueOf(movimiento.getTipo())},
                {"Cantidad", String.valueOf(movimiento.getCantidad())},
                {"Stock resultante", String.valueOf(movimiento.getStockResultante())},
                {"Fecha", String.valueOf(movimiento.getFecha())},
                {"Motivo", movimiento.getMotivo()}
        });
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

        List<String[]> filas = new ArrayList<>();
        for (Pago pago : pagos) {
            filas.add(new String[] {
                    String.valueOf(pago.getId()),
                    String.valueOf(pago.getMetodoPago()),
                    String.format("%.2f", pago.getMonto()),
                    String.valueOf(pago.getEstado()),
                    String.valueOf(pago.getFecha().toLocalDate())
            });
        }

        imprimirTabla(
                new String[] {"ID", "Metodo", "Monto", "Estado", "Fecha"},
                filas);
    }

    public static void imprimirPago(Pago pago) {
        imprimirFicha("PAGO", new String[][] {
                {"ID", String.valueOf(pago.getId())},
                {"Metodo de pago", String.valueOf(pago.getMetodoPago())},
                {"Monto", String.format("%.2f", pago.getMonto())},
                {"Estado", String.valueOf(pago.getEstado())},
                {"Fecha", String.valueOf(pago.getFecha())}
        });
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
            System.out.println("No hay ordenes registradas.");
            return;
        }

        List<String[]> filas = new ArrayList<>();
        for (OrdenCompra orden : ordenes) {
            String cliente = orden.getCliente().getNombre() + " " + orden.getCliente().getApellido();
            String pago = orden.getPago() == null ? "Sin pago" : orden.getPago().getEstado().toString();
            filas.add(new String[] {
                    limitar(orden.getNumero(), 25),
                    limitar(cliente, 28),
                    String.format("%.2f", orden.getTotal()),
                    String.valueOf(orden.getEstado()),
                    pago,
                    String.valueOf(orden.getFecha().toLocalDate())
            });
        }

        imprimirTabla(
                new String[] {"Numero", "Cliente", "Total", "Estado", "Pago", "Fecha"},
                filas);
    }

    public static void imprimirOrden(OrdenCompra orden) {
        List<String[]> campos = new ArrayList<>();
        campos.add(new String[] {"Numero", orden.getNumero()});
        campos.add(new String[] {"Cliente", orden.getCliente().getNombre() + " "
                + orden.getCliente().getApellido() + " - " + orden.getCliente().getEmail()});
        campos.add(new String[] {"Fecha", String.valueOf(orden.getFecha())});
        campos.add(new String[] {"Estado", String.valueOf(orden.getEstado())});
        campos.add(new String[] {"Total productos", String.format("%.2f", orden.getTotal())});

        if (orden.getPago() != null) {
            campos.add(new String[] {"Pago", orden.getPago().getMetodoPago() + " / " + orden.getPago().getEstado()});
        }

        if (orden.getEnvio() != null) {
            campos.add(new String[] {"Envio", orden.getEnvio().getCodigoSeguimiento() + " / " + orden.getEnvio().getEstado()});
        }

        imprimirFicha("ORDEN", campos.toArray(new String[0][]));

        if (orden.getProductos().isEmpty()) {
            System.out.println("La orden no tiene productos asociados.");
            return;
        }

        System.out.println();
        System.out.printf("%-14s %-28s %-10s %-15s %-15s%n",
                "Codigo", "Producto", "Cantidad", "Precio unit.", "Subtotal");
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
        imprimirFicha("ENVIO", new String[][] {
                {"Codigo de seguimiento", envio.getCodigoSeguimiento()},
                {"Tipo", String.valueOf(envio.getTipoEnvio())},
                {"Estado", String.valueOf(envio.getEstado())},
                {"Direccion", envio.getDireccion()},
                {"Provincia", envio.getProvincia()},
                {"Ciudad", envio.getCiudad()},
                {"Codigo postal", envio.getCodigoPostal()},
                {"Costo", String.format("%.2f", envio.getCosto())}
        });
    }


    public static void imprimirEnvios(List<Envio> envios) {
        if (envios.isEmpty()) {
            System.out.println("No hay envios registrados.");
            return;
        }

        List<String[]> filas = new ArrayList<>();
        for (Envio envio : envios) {
            filas.add(new String[] {
                    limitar(envio.getCodigoSeguimiento(), 15),
                    String.valueOf(envio.getTipoEnvio()),
                    String.valueOf(envio.getEstado()),
                    limitar(envio.getProvincia(), 18),
                    limitar(envio.getCiudad(), 18),
                    limitar(envio.getCodigoPostal(), 18),
                    String.format("%.2f", envio.getCosto())
            });
        }

        imprimirTabla(
                new String[] {"Codigo", "Tipo", "Estado", "Provincia", "Ciudad", "Codigo postal", "Costo"},
                filas);
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
            System.out.println("No hay historial registrado para el envio.");
            return;
        }

        List<String[]> filas = new ArrayList<>();
        for (EnvioHistorialEstado registro : historial) {
            filas.add(new String[] {
                    String.valueOf(registro.getId()),
                    limitar(registro.getCodigoSeguimiento(), 15),
                    String.valueOf(registro.getEstado()),
                    String.valueOf(registro.getFecha()),
                    limitar(registro.getDescripcion(), 45)
            });
        }

        imprimirTabla(
                new String[] {"ID", "Codigo", "Estado", "Fecha", "Descripcion"},
                filas);
    }

    public static void imprimirHistorialEnvioEnTabla(EnvioHistorialEstado registro) {
        System.out.printf("%-5d %-15s %-14s %-20s %-45s%n",
                registro.getId(),
                limitar(registro.getCodigoSeguimiento(), 15),
                registro.getEstado(),
                registro.getFecha(),
                limitar(registro.getDescripcion(), 45));
    }


    public static void imprimirReclamos(List<ecommerce.model.Reclamo> reclamos) {
        if (reclamos.isEmpty()) {
            System.out.println("No hay reclamos registrados.");
            return;
        }

        List<String[]> filas = new ArrayList<>();
        for (ecommerce.model.Reclamo reclamo : reclamos) {
            String cliente = reclamo.getCliente().getNombre() + " " + reclamo.getCliente().getApellido();
            filas.add(new String[] {
                    limitar(reclamo.getNumeroReclamo(), 24),
                    limitar(cliente, 28),
                    limitar(reclamo.getPedidoAsociado().getNumero(), 25),
                    String.valueOf(reclamo.getEstado()),
                    String.valueOf(reclamo.getFecha().toLocalDate()),
                    limitar(reclamo.getMotivo(), 35)
            });
        }

        imprimirTabla(
                new String[] {"Numero", "Cliente", "Orden", "Estado", "Fecha", "Motivo"},
                filas);
    }

    public static void imprimirReclamo(ecommerce.model.Reclamo reclamo) {
        imprimirFicha("RECLAMO", new String[][] {
                {"Numero", reclamo.getNumeroReclamo()},
                {"Cliente", reclamo.getCliente().getNombre() + " "
                        + reclamo.getCliente().getApellido() + " - " + reclamo.getCliente().getEmail()},
                {"Orden asociada", reclamo.getPedidoAsociado().getNumero()},
                {"Motivo", reclamo.getMotivo()},
                {"Fecha", String.valueOf(reclamo.getFecha())},
                {"Estado", String.valueOf(reclamo.getEstado())}
        });
    }

    public static void imprimirReclamoEnTabla(ecommerce.model.Reclamo reclamo) {
        String cliente = reclamo.getCliente().getNombre() + " " + reclamo.getCliente().getApellido();
        System.out.printf("%-24s %-28s %-25s %-14s %-18s %-35s%n",
                limitar(reclamo.getNumeroReclamo(), 24),
                limitar(cliente, 28),
                limitar(reclamo.getPedidoAsociado().getNumero(), 25),
                reclamo.getEstado(),
                reclamo.getFecha().toLocalDate(),
                limitar(reclamo.getMotivo(), 35));
    }

    public static void imprimirDevoluciones(List<Devolucion> devoluciones) {
        if (devoluciones.isEmpty()) {
            System.out.println("No hay devoluciones registradas.");
            return;
        }

        List<String[]> filas = new ArrayList<>();
        for (Devolucion devolucion : devoluciones) {
            String cliente = devolucion.getCliente().getNombre() + " " + devolucion.getCliente().getApellido();
            filas.add(new String[] {
                    String.valueOf(devolucion.getId()),
                    limitar(cliente, 28),
                    limitar(devolucion.getProducto().getNombre(), 25),
                    String.valueOf(devolucion.getEstado()),
                    String.valueOf(devolucion.getFecha().toLocalDate()),
                    limitar(devolucion.getMotivo(), 35)
            });
        }

        imprimirTabla(
                new String[] {"ID", "Cliente", "Producto", "Estado", "Fecha", "Motivo"},
                filas);
    }

    public static void imprimirDevolucion(Devolucion devolucion) {
        imprimirFicha("DEVOLUCION", new String[][] {
                {"ID", String.valueOf(devolucion.getId())},
                {"Cliente", devolucion.getCliente().getNombre() + " "
                        + devolucion.getCliente().getApellido() + " - " + devolucion.getCliente().getEmail()},
                {"Producto", devolucion.getProducto().getNombre()
                        + " (" + devolucion.getProducto().getCodigo() + ")"},
                {"Motivo", devolucion.getMotivo()},
                {"Fecha", String.valueOf(devolucion.getFecha())},
                {"Estado", String.valueOf(devolucion.getEstado())}
        });
    }

    public static void imprimirDevolucionEnTabla(Devolucion devolucion) {
        String cliente = devolucion.getCliente().getNombre() + " " + devolucion.getCliente().getApellido();
        System.out.printf("%-5d %-28s %-25s %-14s %-18s %-35s%n",
                devolucion.getId(),
                limitar(cliente, 28),
                limitar(devolucion.getProducto().getNombre(), 25),
                devolucion.getEstado(),
                devolucion.getFecha().toLocalDate(),
                limitar(devolucion.getMotivo(), 35));
    }

    public static void imprimirCalificaciones(List<Calificacion> calificaciones) {
        if (calificaciones.isEmpty()) {
            System.out.println("No hay calificaciones registradas.");
            return;
        }

        List<String[]> filas = new ArrayList<>();
        for (Calificacion calificacion : calificaciones) {
            String cliente = calificacion.getCliente().getNombre() + " " + calificacion.getCliente().getApellido();
            filas.add(new String[] {
                    String.valueOf(calificacion.getId()),
                    limitar(cliente, 28),
                    limitar(calificacion.getProducto().getNombre(), 25),
                    String.valueOf(calificacion.getPuntuacion()),
                    String.valueOf(calificacion.getFecha().toLocalDate()),
                    limitar(calificacion.getComentario(), 40)
            });
        }

        imprimirTabla(
                new String[] {"ID", "Cliente", "Producto", "Puntos", "Fecha", "Comentario"},
                filas);
    }

    public static void imprimirCalificacion(Calificacion calificacion) {
        imprimirFicha("CALIFICACION", new String[][] {
                {"ID", String.valueOf(calificacion.getId())},
                {"Cliente", calificacion.getCliente().getNombre() + " "
                        + calificacion.getCliente().getApellido() + " - " + calificacion.getCliente().getEmail()},
                {"Producto", calificacion.getProducto().getNombre()
                        + " (" + calificacion.getProducto().getCodigo() + ")"},
                {"Puntuacion", String.valueOf(calificacion.getPuntuacion())},
                {"Comentario", calificacion.getComentario()},
                {"Fecha", String.valueOf(calificacion.getFecha())}
        });
    }

    public static void imprimirCalificacionEnTabla(Calificacion calificacion) {
        String cliente = calificacion.getCliente().getNombre() + " " + calificacion.getCliente().getApellido();
        System.out.printf("%-5d %-28s %-25s %-10d %-18s %-40s%n",
                calificacion.getId(),
                limitar(cliente, 28),
                limitar(calificacion.getProducto().getNombre(), 25),
                calificacion.getPuntuacion(),
                calificacion.getFecha().toLocalDate(),
                limitar(calificacion.getComentario(), 40));
    }

    public static void imprimirRoles() {
        RolUsuario[] roles = RolUsuario.values();

        String[] opciones = new String[roles.length];
        for (int i = 0; i < roles.length; i++) {
            RolUsuario rol = roles[i];
            opciones[i] = (i + 1) + ". " + rol + " - " + describirRol(rol);
        }
        imprimirMenuOpciones(opciones);
    }

    public static String describirRol(RolUsuario rol) {
        return switch (rol) {
            case CLIENTE -> "consulta productos, administra carrito, realiza compras y genera reclamos";
            case ADMINISTRADOR -> "gestiona usuarios, productos, categorias, inventario y reportes";
            case OPERADOR_VENTAS -> "administra ordenes, confirma pagos y gestiona estados de pedidos";
            case RESPONSABLE_LOGISTICA -> "gestiona envios y actualiza estados de entrega";
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

    private static String centrar(String texto, int ancho) {
        if (texto.length() >= ancho) {
            return texto.substring(0, ancho);
        }

        int espaciosIzquierda = (ancho - texto.length()) / 2;
        int espaciosDerecha = ancho - texto.length() - espaciosIzquierda;
        return repetir(' ', espaciosIzquierda) + texto + repetir(' ', espaciosDerecha);
    }

    private static void imprimirCaja(String texto, int ancho, char bordeInterno) {
        String borde = "+" + repetir(bordeInterno, ancho - 2) + "+";
        System.out.println(borde);
        System.out.println("|" + centrar(texto, ancho - 2) + "|");
        System.out.println(borde);
    }

    private static void imprimirBloqueMensaje(String tipo, String mensaje) {
        String contenido = "[" + tipo + "] " + (mensaje == null ? "" : mensaje);
        int ancho = Math.max(ANCHO_TERMINAL, contenido.length() + 4);
        String borde = "+" + repetir('-', ancho - 2) + "+";
        System.out.println(borde);
        System.out.println("| " + padRight(contenido, ancho - 4) + " |");
        System.out.println(borde);
    }

    private static String repetir(char caracter, int cantidad) {
        return String.valueOf(caracter).repeat(Math.max(0, cantidad));
    }

    private static String padRight(String texto, int ancho) {
        if (texto.length() >= ancho) {
            return texto.substring(0, ancho);
        }

        return texto + repetir(' ', ancho - texto.length());
    }

    private static void imprimirTabla(String[] encabezados, List<String[]> filas) {
        if (encabezados == null || encabezados.length == 0) {
            return;
        }

        int columnas = encabezados.length;
        int[] anchos = new int[columnas];

        for (int i = 0; i < columnas; i++) {
            anchos[i] = encabezados[i] == null ? 0 : encabezados[i].length();
        }

        for (String[] fila : filas) {
            for (int i = 0; i < columnas && i < fila.length; i++) {
                String valor = fila[i] == null ? "" : fila[i];
                anchos[i] = Math.max(anchos[i], valor.length());
            }
        }

        String borde = construirBorde(anchos);
        System.out.println(borde);
        imprimirFilaTabla(encabezados, anchos);
        System.out.println(borde);
        for (String[] fila : filas) {
            imprimirFilaTabla(fila, anchos);
        }
        System.out.println(borde);
    }

    private static void imprimirFicha(String titulo, String[][] campos) {
        String texto = titulo == null ? "" : titulo.trim().toUpperCase();
        int anchoTitulo = Math.max(ANCHO_TERMINAL, texto.length() + 8);
        String bordeTitulo = "+" + repetir('=', anchoTitulo - 2) + "+";
        System.out.println();
        System.out.println(bordeTitulo);
        System.out.println("|" + centrar(texto, anchoTitulo - 2) + "|");
        System.out.println(bordeTitulo);

        int anchoEtiqueta = 0;
        int anchoValor = 0;
        for (String[] campo : campos) {
            if (campo == null || campo.length < 2) {
                continue;
            }
            anchoEtiqueta = Math.max(anchoEtiqueta, campo[0] == null ? 0 : campo[0].length());
            anchoValor = Math.max(anchoValor, campo[1] == null ? 0 : campo[1].length());
        }

        anchoEtiqueta = Math.min(Math.max(anchoEtiqueta, 12), 26);
        anchoValor = Math.min(Math.max(anchoValor, 20), 44);

        String borde = "+-" + repetir('-', anchoEtiqueta) + "-+-" + repetir('-', anchoValor) + "-+";
        System.out.println(borde);
        for (String[] campo : campos) {
            String etiqueta = campo != null && campo.length > 0 && campo[0] != null ? campo[0] : "";
            String valor = campo != null && campo.length > 1 && campo[1] != null ? campo[1] : "";
            System.out.println("| " + padRight(limitar(etiqueta, anchoEtiqueta), anchoEtiqueta)
                    + " | " + padRight(limitar(valor, anchoValor), anchoValor) + " |");
        }
        System.out.println(borde);
    }

    private static void imprimirFilaTabla(String[] valores, int[] anchos) {
        StringBuilder fila = new StringBuilder("|");
        for (int i = 0; i < anchos.length; i++) {
            String valor = i < valores.length && valores[i] != null ? valores[i] : "";
            fila.append(' ').append(padRight(valor, anchos[i])).append(" |");
        }
        System.out.println(fila);
    }

    private static String construirBorde(int[] anchos) {
        StringBuilder borde = new StringBuilder("+");
        for (int ancho : anchos) {
            borde.append(repetir('-', ancho + 2)).append('+');
        }
        return borde.toString();
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
