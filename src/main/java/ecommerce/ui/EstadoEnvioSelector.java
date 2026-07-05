package ecommerce.ui;

import ecommerce.enums.EstadoEnvio;

public class EstadoEnvioSelector {

    private final EntradaConsola entrada;

    public EstadoEnvioSelector(EntradaConsola entrada) {
        this.entrada = entrada;
    }

    public EstadoEnvio seleccionarEstadoEnvio() {
        imprimirEstados();
        int opcion = entrada.leerOpcion("Seleccione estado de envio: ", 1, EstadoEnvio.values().length);
        return EstadoEnvio.values()[opcion - 1];
    }

    private void imprimirEstados() {
        EstadoEnvio[] estados = EstadoEnvio.values();
        String[] opciones = new String[estados.length];
        for (int i = 0; i < estados.length; i++) {
            opciones[i] = (i + 1) + ". " + estados[i];
        }
        ConsolaUtils.imprimirMenuOpciones(opciones);
    }
}
