package ecommerce.ui;

import ecommerce.enums.RolUsuario;

public class RolSelector {

    private final EntradaConsola entrada;

    public RolSelector(EntradaConsola entrada) {
        this.entrada = entrada;
    }

    public RolUsuario seleccionarRol() {
        ConsolaUtils.imprimirRoles();

        while (true) {
            int opcion = entrada.leerEntero("Seleccione rol: ");
            RolUsuario[] roles = RolUsuario.values();

            if (opcion >= 1 && opcion <= roles.length) {
                return roles[opcion - 1];
            }

            ConsolaUtils.imprimirMensajeError("Rol invalido.");
        }
    }

    public RolUsuario seleccionarRolOpcional(RolUsuario rolActual) {
        ConsolaUtils.imprimirEtiquetaValor("Rol actual", rolActual);
        ConsolaUtils.imprimirMenuOpciones("0. Mantener rol actual");
        ConsolaUtils.imprimirRoles();

        while (true) {
            int opcion = entrada.leerEntero("Seleccione rol: ");
            RolUsuario[] roles = RolUsuario.values();

            if (opcion == 0) {
                return rolActual;
            }
            if (opcion >= 1 && opcion <= roles.length) {
                return roles[opcion - 1];
            }

            ConsolaUtils.imprimirMensajeError("Rol invalido.");
        }
    }
}
