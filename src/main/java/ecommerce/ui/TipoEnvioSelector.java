package ecommerce.ui;

import ecommerce.enums.TipoEnvio;

public class TipoEnvioSelector {

    private final EntradaConsola entrada;

    public TipoEnvioSelector(EntradaConsola entrada) {
        this.entrada = entrada;
    }

    public TipoEnvio seleccionarTipoEnvio() {
        imprimirTipos();
        int opcion = entrada.leerOpcion("Seleccione tipo de envio: ", 1, TipoEnvio.values().length);
        return TipoEnvio.values()[opcion - 1];
    }

    private void imprimirTipos() {
        TipoEnvio[] tipos = TipoEnvio.values();
        String[] opciones = new String[tipos.length];
        for (int i = 0; i < tipos.length; i++) {
            opciones[i] = (i + 1) + ". " + tipos[i];
        }
        ConsolaUtils.imprimirMenuOpciones(opciones);
    }
}
