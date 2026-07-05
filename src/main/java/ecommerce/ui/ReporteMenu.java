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
            opcion = entrada.leerEntero("Opción: ");
            ejecutarOpcion(opcion);
        } while (opcion != 0);
    }

    private void imprimirMenu() {
        ConsolaUtils.imprimirTitulo("REPORTES");
        System.out.println("1. Cantidad total de usuarios");
        System.out.println("2. Cantidad de clientes");
        System.out.println("3. Cantidad de productos");
        System.out.println("4. Productos por categoría");
        System.out.println("5. Productos sin stock");
        System.out.println("6. Productos más vendidos");
        System.out.println("7. Órdenes generadas");
        System.out.println("8. Órdenes por estado");
        System.out.println("9. Recaudación total");
        System.out.println("10. Recaudación por método de pago");
        System.out.println("11. Clientes con más compras");
        System.out.println("12. Reclamos abiertos");
        System.out.println("13. Reclamos resueltos");
        System.out.println("14. Envíos pendientes");
        System.out.println("15. Envíos entregados");
        System.out.println("16. Resumen general");
        System.out.println("0. Volver");
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
            case 0 -> System.out.println("Volviendo al menú principal.");
            default -> System.out.println("Opción incorrecta.");
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
        ConsolaUtils.imprimirTitulo("PRODUCTOS POR CATEGORÍA");
        imprimirMapa("Categoría", "Cantidad", reporteService.productosPorCategoria());
        entrada.pausar();
    }

    private void imprimirProductosSinStock() {
        ConsolaUtils.imprimirTitulo("PRODUCTOS SIN STOCK");
        ConsolaUtils.imprimirProductos(reporteService.productosSinStock());
        entrada.pausar();
    }

    private void imprimirProductosMasVendidos() {
        ConsolaUtils.imprimirTitulo("PRODUCTOS MÁS VENDIDOS");
        imprimirMapa("Código de producto", "Unidades vendidas", reporteService.productosMasVendidos());
        entrada.pausar();
    }

    private void imprimirOrdenesGeneradas() {
        ConsolaUtils.imprimirTitulo("ÓRDENES GENERADAS");
        System.out.println("Total de órdenes: " + reporteService.ordenesGeneradas());
        entrada.pausar();
    }

    private void imprimirOrdenesPorEstado() {
        ConsolaUtils.imprimirTitulo("ÓRDENES POR ESTADO");
        imprimirMapa("Estado", "Cantidad", reporteService.ordenesPorEstado());
        entrada.pausar();
    }

    private void imprimirRecaudacionTotal() {
        ConsolaUtils.imprimirTitulo("RECAUDACIÓN TOTAL");
        System.out.printf("Total recaudado: %.2f%n", reporteService.recaudacionTotal());
        entrada.pausar();
    }

    private void imprimirRecaudacionPorMetodoPago() {
        ConsolaUtils.imprimirTitulo("RECAUDACIÓN POR MÉTODO DE PAGO");
        imprimirMapaDecimal("Método de pago", "Recaudación", reporteService.recaudacionPorMetodoPago());
        entrada.pausar();
    }

    private void imprimirClientesConMasCompras() {
        ConsolaUtils.imprimirTitulo("CLIENTES CON MÁS COMPRAS");
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
        ConsolaUtils.imprimirTitulo("ENVÍOS PENDIENTES");
        ConsolaUtils.imprimirEnvios(reporteService.enviosPendientes());
        entrada.pausar();
    }

    private void imprimirEnviosEntregados() {
        ConsolaUtils.imprimirTitulo("ENVÍOS ENTREGADOS");
        ConsolaUtils.imprimirEnvios(reporteService.enviosEntregados());
        entrada.pausar();
    }

    private void imprimirResumenGeneral() {
        ConsolaUtils.imprimirTitulo("RESUMEN GENERAL");
        System.out.println("Usuarios registrados: " + reporteService.cantidadTotalUsuarios());
        System.out.println("Clientes registrados: " + reporteService.cantidadClientes());
        System.out.println("Productos registrados: " + reporteService.cantidadProductos());
        System.out.println("Órdenes generadas: " + reporteService.ordenesGeneradas());
        System.out.printf("Recaudación total: %.2f%n", reporteService.recaudacionTotal());
        System.out.println("Reclamos abiertos: " + reporteService.reclamosAbiertos().size());
        System.out.println("Reclamos resueltos: " + reporteService.reclamosResueltos().size());
        System.out.println("Envíos pendientes: " + reporteService.enviosPendientes().size());
        System.out.println("Envíos entregados: " + reporteService.enviosEntregados().size());
        imprimirResumenProductosSinStock();
        imprimirResumenProductosMasVendidos();
        entrada.pausar();
    }

    private void imprimirResumenProductosSinStock() {
        System.out.println();
        System.out.println("Productos sin stock: " + reporteService.productosSinStock().size());
        for (Producto producto : reporteService.productosSinStock()) {
            System.out.println("- " + producto.getCodigo() + " - " + producto.getNombre());
        }
    }

    private void imprimirResumenProductosMasVendidos() {
        System.out.println();
        System.out.println("Productos más vendidos:");
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
