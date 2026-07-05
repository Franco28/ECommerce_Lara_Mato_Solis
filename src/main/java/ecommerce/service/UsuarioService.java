package ecommerce.service;

import ecommerce.dao.interfaces.UsuarioDAO;
import ecommerce.enums.EstadoUsuario;
import ecommerce.enums.RolUsuario;
import ecommerce.exception.UsuarioDuplicadoException;
import ecommerce.exception.UsuarioNoEncontradoException;
import ecommerce.model.Usuario;
import ecommerce.util.ValidadorDominio;

import java.time.LocalDate;
import java.util.List;

/**
 * Servicio de aplicación para el módulo de usuarios.
 * Valida reglas de negocio antes de delegar la persistencia al DAO.
 */
public class UsuarioService {

    private final UsuarioDAO usuarioDAO;

    public UsuarioService(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = ValidadorDominio.validarObjetoObligatorio(usuarioDAO,
                "El DAO de usuarios es obligatorio.");
    }

    public Usuario registrarUsuario(String nombre, String apellido, String email,
            String contrasenia, RolUsuario rol) {
        ValidadorDominio.validarEmail(email);
        validarEmailDisponible(email);

        Usuario usuario = new Usuario(
                0,
                nombre,
                apellido,
                email,
                contrasenia,
                LocalDate.now(),
                EstadoUsuario.ACTIVO,
                rol);

        usuarioDAO.guardar(usuario);
        return usuario;
    }

    public void modificarUsuario(Usuario usuario) {
        ValidadorDominio.validarObjetoObligatorio(usuario, "El usuario es obligatorio.");
        ValidadorDominio.validarEmail(usuario.getEmail());
        validarEmailDisponibleParaOtroUsuario(usuario.getEmail(), usuario.getId());
        usuarioDAO.actualizar(usuario);
    }

    public Usuario buscarPorId(int id) {
        ValidadorDominio.validarEnteroMayorACero(id, "El ID del usuario debe ser mayor a cero.");
        return usuarioDAO.buscarPorId(id);
    }

    public Usuario buscarPorEmail(String email) {
        ValidadorDominio.validarEmail(email);
        return usuarioDAO.buscarPorEmail(email);
    }

    public List<Usuario> listarUsuarios() {
        return usuarioDAO.obtenerTodos();
    }

    public void eliminarUsuario(int id) throws UsuarioNoEncontradoException {
        ValidadorDominio.validarEnteroMayorACero(id, "El ID del usuario debe ser mayor a cero.");
        usuarioDAO.eliminar(id);
    }

    public void activarUsuario(int id) {
        Usuario usuario = buscarPorId(id);
        usuario.activar();
        usuarioDAO.actualizar(usuario);
    }

    public void desactivarUsuario(int id) {
        Usuario usuario = buscarPorId(id);
        usuario.desactivar();
        usuarioDAO.actualizar(usuario);
    }

    public long contarClientes() {
        return listarUsuarios().stream()
                .filter(usuario -> usuario.tieneRol(RolUsuario.CLIENTE))
                .count();
    }

    private void validarEmailDisponible(String email) {
        if (usuarioDAO.buscarOpcionalPorEmail(email).isPresent()) {
            throw new UsuarioDuplicadoException("Ya existe un usuario con el email indicado.");
        }
    }

    private void validarEmailDisponibleParaOtroUsuario(String email, int usuarioId) {
        usuarioDAO.buscarOpcionalPorEmail(email)
                .filter(usuarioExistente -> usuarioExistente.getId() != usuarioId)
                .ifPresent(usuarioExistente -> {
                    throw new UsuarioDuplicadoException("Ya existe otro usuario con el email indicado.");
                });
    }
}
