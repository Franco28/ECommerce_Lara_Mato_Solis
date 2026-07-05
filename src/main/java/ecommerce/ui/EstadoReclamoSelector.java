package ecommerce.ui;

import ecommerce.enums.EstadoReclamo;

public class EstadoReclamoSelector {

    private final EntradaConsola entrada;

    public EstadoReclamoSelector(EntradaConsola entrada) {
        this.entrada = entrada;
    }

    public EstadoReclamo seleccionarEstadoReclamo() {
        imprimirEstados();
        int opcion = entrada.leerOpcion("Seleccione estado de reclamo: ", 1, EstadoReclamo.values().length);
        return EstadoReclamo.values()[opcion - 1];
    }

    private void imprimirEstados() {
        EstadoReclamo[] estados = EstadoReclamo.values();
        for (int i = 0; i < estados.length; i++) {
            System.out.println((i + 1) + ". " + estados[i]);
        }
    }
}
