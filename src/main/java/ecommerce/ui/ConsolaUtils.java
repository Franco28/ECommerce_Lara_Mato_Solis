package ecommerce.ui;

import ecommerce.enums.RolUsuario;
import ecommerce.model.Usuario;

import java.util.List;

public final class ConsolaUtils {

    private ConsolaUtils() {
    }

    public static void imprimirTitulo(String titulo) {
        System.out.println();
        System.out.println("=== " + titulo + " ===");
    }

    public static void imprimirUsuarios(List<Usuario> usuarios) {
        if (usuarios.isEmpty()) {
            System.out.println("No hay usuarios registrados.");
            return;
        }

        System.out.printf("%-5s %-18s %-18s %-30s %-22s %-12s%n",
                "ID", "Nombre", "Apellido", "Email", "Rol", "Estado");
        System.out.println("------------------------------------------------------------------------------------------------------");

        for (Usuario usuario : usuarios) {
            imprimirUsuarioEnTabla(usuario);
        }
    }

    public static void imprimirUsuario(Usuario usuario) {
        System.out.println("ID: " + usuario.getId());
        System.out.println("Nombre: " + usuario.getNombre());
        System.out.println("Apellido: " + usuario.getApellido());
        System.out.println("Email: " + usuario.getEmail());
        System.out.println("Fecha de alta: " + usuario.getFechaAlta());
        System.out.println("Estado: " + usuario.getEstado());
        System.out.println("Rol: " + usuario.getRol());
    }

    public static void imprimirUsuarioEnTabla(Usuario usuario) {
        System.out.printf("%-5d %-18s %-18s %-30s %-22s %-12s%n",
                usuario.getId(),
                limitar(usuario.getNombre(), 18),
                limitar(usuario.getApellido(), 18),
                limitar(usuario.getEmail(), 30),
                usuario.getRol(),
                usuario.getEstado());
    }

    public static void imprimirRoles() {
        RolUsuario[] roles = RolUsuario.values();

        for (int i = 0; i < roles.length; i++) {
            RolUsuario rol = roles[i];
            System.out.println((i + 1) + ". " + rol + " - " + describirRol(rol));
        }
    }

    public static String describirRol(RolUsuario rol) {
        return switch (rol) {
            case CLIENTE -> "consulta productos, administra carrito, realiza compras y genera reclamos";
            case ADMINISTRADOR -> "gestiona usuarios, productos, categorías, inventario y reportes";
            case OPERADOR_VENTAS -> "administra órdenes, confirma pagos y gestiona estados de pedidos";
            case RESPONSABLE_LOGISTICA -> "gestiona envíos y actualiza estados de entrega";
        };
    }

    private static String limitar(String valor, int maximo) {
        if (valor == null) {
            return "";
        }
        if (valor.length() <= maximo) {
            return valor;
        }
        return valor.substring(0, maximo - 3) + "...";
    }
}
