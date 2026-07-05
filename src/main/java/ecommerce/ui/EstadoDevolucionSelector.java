package ecommerce.ui;

import ecommerce.enums.EstadoDevolucion;

public class EstadoDevolucionSelector {

    private final EntradaConsola entrada;

    public EstadoDevolucionSelector(EntradaConsola entrada) {
        this.entrada = entrada;
    }

    public EstadoDevolucion seleccionarEstadoDevolucion() {
        imprimirEstados();
        int opcion = entrada.leerOpcion("Seleccione estado de devolución: ", 1, EstadoDevolucion.values().length);
        return EstadoDevolucion.values()[opcion - 1];
    }

    private void imprimirEstados() {
        EstadoDevolucion[] estados = EstadoDevolucion.values();
        for (int i = 0; i < estados.length; i++) {
            System.out.println((i + 1) + ". " + estados[i]);
        }
    }
}
