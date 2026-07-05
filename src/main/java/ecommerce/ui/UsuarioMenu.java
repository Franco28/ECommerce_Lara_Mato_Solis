package ecommerce.ui;

import ecommerce.enums.RolUsuario;
import ecommerce.exception.EcommerceException;
import ecommerce.model.Usuario;
import ecommerce.service.UsuarioService;

public class UsuarioMenu {

    private final UsuarioService usuarioService;
    private final EntradaConsola entrada;
    private final RolSelector rolSelector;

    public UsuarioMenu(UsuarioService usuarioService, EntradaConsola entrada) {
        this.usuarioService = usuarioService;
        this.entrada = entrada;
        this.rolSelector = new RolSelector(entrada);
    }

    public void mostrar() {
        int opcion;

        do {
            ConsolaUtils.imprimirTitulo("GESTIÓN DE USUARIOS");
            System.out.println("1. Registrar usuario");
            System.out.println("2. Modificar usuario");
            System.out.println("3. Eliminar usuario");
            System.out.println("4. Buscar usuario por ID");
            System.out.println("5. Buscar usuario por email");
            System.out.println("6. Listar usuarios");
            System.out.println("7. Activar usuario");
            System.out.println("8. Desactivar usuario");
            System.out.println("0. Volver");

            opcion = entrada.leerEntero("Opción: ");
            ejecutarOpcion(opcion);
        } while (opcion != 0);
    }

    private void ejecutarOpcion(int opcion) {
        try {
            switch (opcion) {
                case 1 -> registrarUsuario();
                case 2 -> modificarUsuario();
                case 3 -> eliminarUsuario();
                case 4 -> buscarPorId();
                case 5 -> buscarPorEmail();
                case 6 -> listarUsuarios();
                case 7 -> activarUsuario();
                case 8 -> desactivarUsuario();
                case 0 -> { }
                default -> System.out.println("Opción incorrecta.");
            }
        } catch (EcommerceException ex) {
            System.out.println("Error: " + ex.getMessage());
        }

        if (opcion != 0) {
            entrada.pausar();
        }
    }

    private void registrarUsuario() {
        ConsolaUtils.imprimirTitulo("REGISTRAR USUARIO");

        String nombre = entrada.leerTexto("Nombre: ");
        String apellido = entrada.leerTexto("Apellido: ");
        String email = entrada.leerTexto("Email: ");
        String contrasenia = entrada.leerTexto("Contraseña: ");
        RolUsuario rol = rolSelector.seleccionarRol();

        Usuario usuario = usuarioService.registrarUsuario(nombre, apellido, email, contrasenia, rol);

        System.out.println("Usuario registrado correctamente.");
        ConsolaUtils.imprimirUsuario(usuario);
    }

    private void modificarUsuario() {
        ConsolaUtils.imprimirTitulo("MODIFICAR USUARIO");

        int id = entrada.leerEntero("ID del usuario: ");
        Usuario usuario = usuarioService.buscarPorId(id);

        ConsolaUtils.imprimirUsuario(usuario);
        System.out.println();

        usuario.setNombre(entrada.leerTextoOpcional("Nombre", usuario.getNombre()));
        usuario.setApellido(entrada.leerTextoOpcional("Apellido", usuario.getApellido()));
        usuario.setEmail(entrada.leerTextoOpcional("Email", usuario.getEmail()));
        usuario.setContrasenia(entrada.leerTextoOpcional("Contraseña", usuario.getContrasenia()));
        usuario.setRol(rolSelector.seleccionarRolOpcional(usuario.getRol()));

        usuarioService.modificarUsuario(usuario);
        System.out.println("Usuario modificado correctamente.");
    }

    private void eliminarUsuario() {
        ConsolaUtils.imprimirTitulo("ELIMINAR USUARIO");

        int id = entrada.leerEntero("ID del usuario: ");
        Usuario usuario = usuarioService.buscarPorId(id);
        ConsolaUtils.imprimirUsuario(usuario);

        String confirmacion = entrada.leerTexto("Escriba SI para confirmar la eliminación: ");
        if ("SI".equalsIgnoreCase(confirmacion)) {
            usuarioService.eliminarUsuario(id);
            System.out.println("Usuario eliminado correctamente.");
        } else {
            System.out.println("Eliminación cancelada.");
        }
    }

    private void buscarPorId() {
        ConsolaUtils.imprimirTitulo("BUSCAR USUARIO POR ID");
        int id = entrada.leerEntero("ID del usuario: ");
        ConsolaUtils.imprimirUsuario(usuarioService.buscarPorId(id));
    }

    private void buscarPorEmail() {
        ConsolaUtils.imprimirTitulo("BUSCAR USUARIO POR EMAIL");
        String email = entrada.leerTexto("Email: ");
        ConsolaUtils.imprimirUsuario(usuarioService.buscarPorEmail(email));
    }

    private void listarUsuarios() {
        ConsolaUtils.imprimirTitulo("LISTADO DE USUARIOS");
        ConsolaUtils.imprimirUsuarios(usuarioService.listarUsuarios());
    }

    private void activarUsuario() {
        ConsolaUtils.imprimirTitulo("ACTIVAR USUARIO");
        int id = entrada.leerEntero("ID del usuario: ");
        usuarioService.activarUsuario(id);
        System.out.println("Usuario activado correctamente.");
    }

    private void desactivarUsuario() {
        ConsolaUtils.imprimirTitulo("DESACTIVAR USUARIO");
        int id = entrada.leerEntero("ID del usuario: ");
        usuarioService.desactivarUsuario(id);
        System.out.println("Usuario desactivado correctamente.");
    }
}
