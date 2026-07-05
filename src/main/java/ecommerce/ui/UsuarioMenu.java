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
            ConsolaUtils.imprimirTitulo("GESTION DE USUARIOS");
            ConsolaUtils.imprimirMensajeInfo("Administrador: alta, edicion, baja y consultas.");
            ConsolaUtils.imprimirMenuOpciones(
                    "1. Registrar usuario",
                    "2. Modificar usuario",
                    "3. Eliminar usuario",
                    "4. Buscar usuario por ID",
                    "5. Buscar usuario por email",
                    "6. Listar usuarios",
                    "7. Activar usuario",
                    "8. Desactivar usuario",
                    "0. Volver");

            opcion = entrada.leerEntero("Opcion: ");
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
                default -> ConsolaUtils.imprimirMensajeError("Opcion incorrecta.");
            }
        } catch (EcommerceException ex) {
            ConsolaUtils.imprimirMensajeError(ex.getMessage());
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
        String contrasenia = entrada.leerTexto("Contrasena: ");
        RolUsuario rol = rolSelector.seleccionarRol();

        Usuario usuario = usuarioService.registrarUsuario(nombre, apellido, email, contrasenia, rol);

        ConsolaUtils.imprimirMensajeExito("Usuario registrado correctamente.");
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
        usuario.setContrasenia(entrada.leerTextoOpcional("Contrasena", usuario.getContrasenia()));
        usuario.setRol(rolSelector.seleccionarRolOpcional(usuario.getRol()));

        usuarioService.modificarUsuario(usuario);
        ConsolaUtils.imprimirMensajeExito("Usuario modificado correctamente.");
    }

    private void eliminarUsuario() {
        ConsolaUtils.imprimirTitulo("ELIMINAR USUARIO");

        int id = entrada.leerEntero("ID del usuario: ");
        Usuario usuario = usuarioService.buscarPorId(id);
        ConsolaUtils.imprimirUsuario(usuario);

        String confirmacion = entrada.leerTexto("Escriba SI para confirmar la eliminacion: ");
        if ("SI".equalsIgnoreCase(confirmacion)) {
            usuarioService.eliminarUsuario(id);
            ConsolaUtils.imprimirMensajeExito("Usuario eliminado correctamente.");
        } else {
            ConsolaUtils.imprimirMensajeInfo("Eliminacion cancelada.");
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
        ConsolaUtils.imprimirMensajeExito("Usuario activado correctamente.");
    }

    private void desactivarUsuario() {
        ConsolaUtils.imprimirTitulo("DESACTIVAR USUARIO");
        int id = entrada.leerEntero("ID del usuario: ");
        usuarioService.desactivarUsuario(id);
        ConsolaUtils.imprimirMensajeExito("Usuario desactivado correctamente.");
    }
}
