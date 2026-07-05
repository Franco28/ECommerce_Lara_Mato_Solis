package ecommerce.ui;

import ecommerce.enums.EstadoProducto;

public class EstadoProductoSelector {

    private final EntradaConsola entrada;

    public EstadoProductoSelector(EntradaConsola entrada) {
        this.entrada = entrada;
    }

    public EstadoProducto seleccionarEstado() {
        imprimirEstados(false);
        int opcion = entrada.leerOpcion("Seleccione estado: ", 1, EstadoProducto.values().length);
        return EstadoProducto.values()[opcion - 1];
    }

    public EstadoProducto seleccionarEstadoOpcional(EstadoProducto estadoActual) {
        ConsolaUtils.imprimirEtiquetaValor("Estado actual", estadoActual);
        imprimirEstados(true);
        int opcion = entrada.leerOpcion("Seleccione estado: ", 0, EstadoProducto.values().length);
        return opcion == 0 ? estadoActual : EstadoProducto.values()[opcion - 1];
    }

    private void imprimirEstados(boolean incluirMantener) {
        if (incluirMantener) {
            ConsolaUtils.imprimirMenuOpciones("0. Mantener estado actual");
        }

        EstadoProducto[] estados = EstadoProducto.values();
        String[] opciones = new String[estados.length];
        for (int i = 0; i < estados.length; i++) {
            opciones[i] = (i + 1) + ". " + estados[i];
        }
        ConsolaUtils.imprimirMenuOpciones(opciones);
    }
}
