package ecommerce.ui;

import ecommerce.enums.RolUsuario;

public class RolMenu {

    private final EntradaConsola entrada;

    public RolMenu(EntradaConsola entrada) {
        this.entrada = entrada;
    }

    public void mostrar() {
        int opcion;

        do {
            ConsolaUtils.imprimirTitulo("GESTION DE ROLES");
            ConsolaUtils.imprimirMensajeInfo("Consulta rapida de roles y permisos disponibles.");
            ConsolaUtils.imprimirMenuOpciones(
                    "1. Listar roles",
                    "2. Consultar permisos de un rol",
                    "0. Volver");

            opcion = entrada.leerEntero("Opcion: ");
            ejecutarOpcion(opcion);
        } while (opcion != 0);
    }

    private void ejecutarOpcion(int opcion) {
        switch (opcion) {
            case 1 -> listarRoles();
            case 2 -> consultarRol();
            case 0 -> { }
            default -> ConsolaUtils.imprimirMensajeError("Opcion incorrecta.");
        }

        if (opcion != 0) {
            entrada.pausar();
        }
    }

    private void listarRoles() {
        ConsolaUtils.imprimirTitulo("ROLES DISPONIBLES");
        ConsolaUtils.imprimirRoles();
    }

    private void consultarRol() {
        ConsolaUtils.imprimirTitulo("CONSULTAR ROL");
        RolSelector selector = new RolSelector(entrada);
        RolUsuario rol = selector.seleccionarRol();

        ConsolaUtils.imprimirEtiquetaValor("Rol", rol);
        ConsolaUtils.imprimirEtiquetaValor("Permisos", ConsolaUtils.describirRol(rol));
    }
}
