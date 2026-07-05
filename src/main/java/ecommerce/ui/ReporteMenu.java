package ecommerce.ui;

import ecommerce.model.Producto;
import ecommerce.service.ReporteService;

import java.util.Map;

public class ReporteMenu {

    private final ReporteService reporteService;
    private final EntradaConsola entrada;

    public ReporteMenu(ReporteService reporteService, EntradaConsola entrada) {
        this.reporteService = reporteService;
        this.entrada = entrada;
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
        ConsolaUtils.imprimirTitulo("REPORTES");
        ConsolaUtils.imprimirMensajeInfo("Reportes de gestion y resumen del sistema.");
        ConsolaUtils.imprimirMenuOpciones(
                "1. Cantidad total de usuarios",
                "2. Cantidad de clientes",
                "3. Cantidad de productos",
                "4. Productos por categoria",
                "5. Productos sin stock",
                "6. Productos mas vendidos",
                "7. Ordenes generadas",
                "8. Ordenes por estado",
                "9. Recaudacion total",
                "10. Recaudacion por metodo de pago",
                "11. Clientes con mas compras",
                "12. Reclamos abiertos",
                "13. Reclamos resueltos",
                "14. Envios pendientes",
                "15. Envios entregados",
                "16. Resumen general",
                "0. Volver");
    }

    private void ejecutarOpcion(int opcion) {
        switch (opcion) {
            case 1 -> imprimirCantidadTotalUsuarios();
            case 2 -> imprimirCantidadClientes();
            case 3 -> imprimirCantidadProductos();
            case 4 -> imprimirProductosPorCategoria();
            case 5 -> imprimirProductosSinStock();
            case 6 -> imprimirProductosMasVendidos();
            case 7 -> imprimirOrdenesGeneradas();
            case 8 -> imprimirOrdenesPorEstado();
            case 9 -> imprimirRecaudacionTotal();
            case 10 -> imprimirRecaudacionPorMetodoPago();
            case 11 -> imprimirClientesConMasCompras();
            case 12 -> imprimirReclamosAbiertos();
            case 13 -> imprimirReclamosResueltos();
            case 14 -> imprimirEnviosPendientes();
            case 15 -> imprimirEnviosEntregados();
            case 16 -> imprimirResumenGeneral();
            case 0 -> ConsolaUtils.imprimirMensajeInfo("Volviendo al menu principal.");
            default -> ConsolaUtils.imprimirMensajeError("Opcion incorrecta.");
        }
    }

    private void imprimirCantidadTotalUsuarios() {
        ConsolaUtils.imprimirTitulo("CANTIDAD TOTAL DE USUARIOS");
        System.out.println("Total de usuarios: " + reporteService.cantidadTotalUsuarios());
        entrada.pausar();
    }

    private void imprimirCantidadClientes() {
        ConsolaUtils.imprimirTitulo("CANTIDAD DE CLIENTES");
        System.out.println("Total de clientes: " + reporteService.cantidadClientes());
        entrada.pausar();
    }

    private void imprimirCantidadProductos() {
        ConsolaUtils.imprimirTitulo("CANTIDAD DE PRODUCTOS");
        System.out.println("Total de productos: " + reporteService.cantidadProductos());
        entrada.pausar();
    }

    private void imprimirProductosPorCategoria() {
        ConsolaUtils.imprimirTitulo("PRODUCTOS POR CATEGORIA");
        imprimirMapa("Categoria", "Cantidad", reporteService.productosPorCategoria());
        entrada.pausar();
    }

    private void imprimirProductosSinStock() {
        ConsolaUtils.imprimirTitulo("PRODUCTOS SIN STOCK");
        ConsolaUtils.imprimirProductos(reporteService.productosSinStock());
        entrada.pausar();
    }

    private void imprimirProductosMasVendidos() {
        ConsolaUtils.imprimirTitulo("PRODUCTOS MAS VENDIDOS");
        imprimirMapa("Codigo de producto", "Unidades vendidas", reporteService.productosMasVendidos());
        entrada.pausar();
    }

    private void imprimirOrdenesGeneradas() {
        ConsolaUtils.imprimirTitulo("ORDENES GENERADAS");
        System.out.println("Total de ordenes: " + reporteService.ordenesGeneradas());
        entrada.pausar();
    }

    private void imprimirOrdenesPorEstado() {
        ConsolaUtils.imprimirTitulo("ORDENES POR ESTADO");
        imprimirMapa("Estado", "Cantidad", reporteService.ordenesPorEstado());
        entrada.pausar();
    }

    private void imprimirRecaudacionTotal() {
        ConsolaUtils.imprimirTitulo("RECAUDACION TOTAL");
        System.out.printf("Total recaudado: %.2f%n", reporteService.recaudacionTotal());
        entrada.pausar();
    }

    private void imprimirRecaudacionPorMetodoPago() {
        ConsolaUtils.imprimirTitulo("RECAUDACION POR METODO DE PAGO");
        imprimirMapaDecimal("Metodo de pago", "Recaudacion", reporteService.recaudacionPorMetodoPago());
        entrada.pausar();
    }

    private void imprimirClientesConMasCompras() {
        ConsolaUtils.imprimirTitulo("CLIENTES CON MAS COMPRAS");
        imprimirMapa("Cliente", "Compras", reporteService.clientesConMasCompras());
        entrada.pausar();
    }

    private void imprimirReclamosAbiertos() {
        ConsolaUtils.imprimirTitulo("RECLAMOS ABIERTOS");
        ConsolaUtils.imprimirReclamos(reporteService.reclamosAbiertos());
        entrada.pausar();
    }

    private void imprimirReclamosResueltos() {
        ConsolaUtils.imprimirTitulo("RECLAMOS RESUELTOS");
        ConsolaUtils.imprimirReclamos(reporteService.reclamosResueltos());
        entrada.pausar();
    }

    private void imprimirEnviosPendientes() {
        ConsolaUtils.imprimirTitulo("ENVIOS PENDIENTES");
        ConsolaUtils.imprimirEnvios(reporteService.enviosPendientes());
        entrada.pausar();
    }

    private void imprimirEnviosEntregados() {
        ConsolaUtils.imprimirTitulo("ENVIOS ENTREGADOS");
        ConsolaUtils.imprimirEnvios(reporteService.enviosEntregados());
        entrada.pausar();
    }

    private void imprimirResumenGeneral() {
        ConsolaUtils.imprimirTitulo("RESUMEN GENERAL");
        ConsolaUtils.imprimirEtiquetaValor("Usuarios registrados", reporteService.cantidadTotalUsuarios());
        ConsolaUtils.imprimirEtiquetaValor("Clientes registrados", reporteService.cantidadClientes());
        ConsolaUtils.imprimirEtiquetaValor("Productos registrados", reporteService.cantidadProductos());
        ConsolaUtils.imprimirEtiquetaValor("Ordenes generadas", reporteService.ordenesGeneradas());
        ConsolaUtils.imprimirEtiquetaValor("Recaudacion total", String.format("%.2f", reporteService.recaudacionTotal()));
        ConsolaUtils.imprimirEtiquetaValor("Reclamos abiertos", reporteService.reclamosAbiertos().size());
        ConsolaUtils.imprimirEtiquetaValor("Reclamos resueltos", reporteService.reclamosResueltos().size());
        ConsolaUtils.imprimirEtiquetaValor("Envios pendientes", reporteService.enviosPendientes().size());
        ConsolaUtils.imprimirEtiquetaValor("Envios entregados", reporteService.enviosEntregados().size());
        imprimirResumenProductosSinStock();
        imprimirResumenProductosMasVendidos();
        entrada.pausar();
    }

    private void imprimirResumenProductosSinStock() {
        System.out.println();
        ConsolaUtils.imprimirSubtitulo("Productos sin stock");
        System.out.println("Total: " + reporteService.productosSinStock().size());
        for (Producto producto : reporteService.productosSinStock()) {
            System.out.println("- " + producto.getCodigo() + " - " + producto.getNombre());
        }
    }

    private void imprimirResumenProductosMasVendidos() {
        System.out.println();
        ConsolaUtils.imprimirSubtitulo("Productos mas vendidos");
        Map<String, Integer> productosMasVendidos = reporteService.productosMasVendidos();
        if (productosMasVendidos.isEmpty()) {
            System.out.println("No hay ventas registradas.");
            return;
        }

        int contador = 0;
        for (Map.Entry<String, Integer> entry : productosMasVendidos.entrySet()) {
            if (contador == 5) {
                break;
            }
            System.out.println("- " + entry.getKey() + ": " + entry.getValue() + " unidades");
            contador++;
        }
    }

    private void imprimirMapa(String encabezadoClave, String encabezadoValor, Map<?, ?> valores) {
        if (valores.isEmpty()) {
            System.out.println("No hay datos para mostrar.");
            return;
        }

        System.out.printf("%-45s %-18s%n", encabezadoClave, encabezadoValor);
        System.out.println("----------------------------------------------------------------");

        for (Map.Entry<?, ?> entry : valores.entrySet()) {
            System.out.printf("%-45s %-18s%n", entry.getKey(), entry.getValue());
        }
    }

    private void imprimirMapaDecimal(String encabezadoClave, String encabezadoValor, Map<?, Double> valores) {
        if (valores.isEmpty()) {
            System.out.println("No hay datos para mostrar.");
            return;
        }

        System.out.printf("%-45s %-18s%n", encabezadoClave, encabezadoValor);
        System.out.println("----------------------------------------------------------------");

        for (Map.Entry<?, Double> entry : valores.entrySet()) {
            System.out.printf("%-45s %-18.2f%n", entry.getKey(), entry.getValue());
        }
    }
}
