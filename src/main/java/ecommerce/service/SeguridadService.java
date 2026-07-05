package ecommerce.service;

import ecommerce.enums.RolUsuario;
import ecommerce.exception.PermisoDenegadoException;
import ecommerce.model.Usuario;
import ecommerce.util.ValidadorDominio;

import java.util.Arrays;

/**
 * Centraliza las reglas de permisos para evitar que el menú por consola
 * duplique validaciones en cada opción.
 */
public class SeguridadService {

    public void validarUsuarioActivo(Usuario usuario) {
        ValidadorDominio.validarObjetoObligatorio(usuario, "El usuario es obligatorio.");
        if (!usuario.estaActivo()) {
            throw new PermisoDenegadoException("El usuario se encuentra inactivo.");
        }
    }

    public void validarRol(Usuario usuario, RolUsuario rolRequerido) {
        validarUsuarioActivo(usuario);
        ValidadorDominio.validarObjetoObligatorio(rolRequerido, "El rol requerido es obligatorio.");

        if (!usuario.tieneRol(rolRequerido)) {
            throw new PermisoDenegadoException(
                    "La acción requiere el rol " + rolRequerido + ".");
        }
    }

    public void validarAlgunRol(Usuario usuario, RolUsuario... rolesPermitidos) {
        validarUsuarioActivo(usuario);
        if (rolesPermitidos == null || rolesPermitidos.length == 0) {
            throw new PermisoDenegadoException("No se configuraron roles permitidos para la acción.");
        }

        boolean permitido = Arrays.stream(rolesPermitidos)
                .anyMatch(usuario::tieneRol);

        if (!permitido) {
            throw new PermisoDenegadoException("El usuario no tiene permisos para realizar esta acción.");
        }
    }

    public void validarClientePropietario(Usuario clienteSesion, Usuario clienteRecurso) {
        validarRol(clienteSesion, RolUsuario.CLIENTE);
        ValidadorDominio.validarObjetoObligatorio(clienteRecurso,
                "El cliente asociado al recurso es obligatorio.");

        if (clienteSesion.getId() != clienteRecurso.getId()) {
            throw new PermisoDenegadoException("El cliente no puede operar recursos de otro usuario.");
        }
    }
}
