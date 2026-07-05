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
            ConsolaUtils.imprimirTitulo("GESTIÓN DE ROLES");
            System.out.println("1. Listar roles");
            System.out.println("2. Consultar permisos de un rol");
            System.out.println("0. Volver");

            opcion = entrada.leerEntero("Opción: ");
            ejecutarOpcion(opcion);
        } while (opcion != 0);
    }

    private void ejecutarOpcion(int opcion) {
        switch (opcion) {
            case 1 -> listarRoles();
            case 2 -> consultarRol();
            case 0 -> { }
            default -> System.out.println("Opción incorrecta.");
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

        System.out.println("Rol: " + rol);
        System.out.println("Permisos: " + ConsolaUtils.describirRol(rol));
    }
}
