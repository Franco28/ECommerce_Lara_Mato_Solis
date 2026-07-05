package ecommerce.ui;

import ecommerce.enums.MetodoPago;

public class MetodoPagoSelector {

    private final EntradaConsola entrada;

    public MetodoPagoSelector(EntradaConsola entrada) {
        this.entrada = entrada;
    }

    public MetodoPago seleccionarMetodoPago() {
        imprimirMetodos();
        int opcion = entrada.leerOpcion("Seleccione método de pago: ", 1, MetodoPago.values().length);
        return MetodoPago.values()[opcion - 1];
    }

    private void imprimirMetodos() {
        MetodoPago[] metodos = MetodoPago.values();
        for (int i = 0; i < metodos.length; i++) {
            System.out.println((i + 1) + ". " + metodos[i]);
        }
    }
}
