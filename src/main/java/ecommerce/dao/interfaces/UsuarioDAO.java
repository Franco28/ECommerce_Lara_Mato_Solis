package ecommerce.dao.interfaces;

import ecommerce.exception.UsuarioDuplicadoException;
import ecommerce.exception.UsuarioNoEncontradoException;
import ecommerce.model.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioDAO {

    void guardar(Usuario usuario) throws UsuarioDuplicadoException;

    Usuario buscarPorId(int id) throws UsuarioNoEncontradoException;

    Usuario buscarPorEmail(String email) throws UsuarioNoEncontradoException;

    Optional<Usuario> buscarOpcionalPorEmail(String email);

    List<Usuario> obtenerTodos();

    void actualizar(Usuario usuario) throws UsuarioNoEncontradoException, UsuarioDuplicadoException;

    void eliminar(int id) throws UsuarioNoEncontradoException;
}
