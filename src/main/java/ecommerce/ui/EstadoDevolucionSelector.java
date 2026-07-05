package ecommerce.ui;

import ecommerce.enums.EstadoDevolucion;

public class EstadoDevolucionSelector {

    private final EntradaConsola entrada;

    public EstadoDevolucionSelector(EntradaConsola entrada) {
        this.entrada = entrada;
    }

    public EstadoDevolucion seleccionarEstadoDevolucion() {
        imprimirEstados();
        int opcion = entrada.leerOpcion("Seleccione estado de devolucion: ", 1, EstadoDevolucion.values().length);
        return EstadoDevolucion.values()[opcion - 1];
    }

    private void imprimirEstados() {
        EstadoDevolucion[] estados = EstadoDevolucion.values();
        String[] opciones = new String[estados.length];
        for (int i = 0; i < estados.length; i++) {
            opciones[i] = (i + 1) + ". " + estados[i];
        }
        ConsolaUtils.imprimirMenuOpciones(opciones);
    }
}
