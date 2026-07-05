package ecommerce.ui;

import ecommerce.enums.EstadoOrden;

public class EstadoOrdenSelector {

    private final EntradaConsola entrada;

    public EstadoOrdenSelector(EntradaConsola entrada) {
        this.entrada = entrada;
    }

    public EstadoOrden seleccionarEstadoOrden() {
        imprimirEstados();
        int opcion = entrada.leerOpcion("Seleccione estado de orden: ", 1, EstadoOrden.values().length);
        return EstadoOrden.values()[opcion - 1];
    }

    private void imprimirEstados() {
        EstadoOrden[] estados = EstadoOrden.values();
        for (int i = 0; i < estados.length; i++) {
            System.out.println((i + 1) + ". " + estados[i]);
        }
    }
}
