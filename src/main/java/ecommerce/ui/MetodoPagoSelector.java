package ecommerce.ui;

import ecommerce.enums.MetodoPago;

public class MetodoPagoSelector {

    private final EntradaConsola entrada;

    public MetodoPagoSelector(EntradaConsola entrada) {
        this.entrada = entrada;
    }

    public MetodoPago seleccionarMetodoPago() {
        imprimirMetodos();
        int opcion = entrada.leerOpcion("Seleccione metodo de pago: ", 1, MetodoPago.values().length);
        return MetodoPago.values()[opcion - 1];
    }

    private void imprimirMetodos() {
        MetodoPago[] metodos = MetodoPago.values();
        String[] opciones = new String[metodos.length];
        for (int i = 0; i < metodos.length; i++) {
            opciones[i] = (i + 1) + ". " + metodos[i];
        }
        ConsolaUtils.imprimirMenuOpciones(opciones);
    }
}
