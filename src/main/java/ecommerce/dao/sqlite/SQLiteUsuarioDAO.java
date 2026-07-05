package ecommerce.dao.sqlite;

import ecommerce.dao.interfaces.UsuarioDAO;
import ecommerce.enums.EstadoUsuario;
import ecommerce.enums.RolUsuario;
import ecommerce.exception.DatabaseException;
import ecommerce.exception.UsuarioDuplicadoException;
import ecommerce.exception.UsuarioNoEncontradoException;
import ecommerce.model.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SQLiteUsuarioDAO extends SQLiteBaseDAO implements UsuarioDAO {

    @Override
    public void guardar(Usuario usuario) throws UsuarioDuplicadoException {
        String sql = """
                INSERT INTO usuarios (nombre, apellido, email, contrasenia, fecha_alta, estado, rol)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = obtenerConexion();
             PreparedStatement statement = prepararConGeneratedKeys(connection, sql,
                     usuario.getNombre(),
                     usuario.getApellido(),
                     usuario.getEmail(),
                     usuario.getContrasenia(),
                     usuario.getFechaAlta(),
                     usuario.getEstado(),
                     usuario.getRol())) {

            statement.executeUpdate();
            usuario.setId(obtenerIdGenerado(statement));
        } catch (SQLException ex) {
            if (esRestriccionUnica(ex)) {
                throw new UsuarioDuplicadoException("Ya existe un usuario con el email indicado.");
            }
            throw new DatabaseException("No se pudo guardar el usuario.", ex);
        }
    }

    @Override
    public Usuario buscarPorId(int id) throws UsuarioNoEncontradoException {
        String sql = "SELECT * FROM usuarios WHERE id = ?";

        try (Connection connection = obtenerConexion();
             PreparedStatement statement = preparar(connection, sql, id);
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                return mapearUsuario(resultSet);
            }
            throw new UsuarioNoEncontradoException("No se encontró un usuario con ID " + id + ".");
        } catch (SQLException ex) {
            throw new DatabaseException("No se pudo buscar el usuario por ID.", ex);
        }
    }

    @Override
    public Usuario buscarPorEmail(String email) throws UsuarioNoEncontradoException {
        return buscarOpcionalPorEmail(email)
                .orElseThrow(() -> new UsuarioNoEncontradoException(
                        "No se encontró un usuario con email " + email + "."));
    }

    @Override
    public Optional<Usuario> buscarOpcionalPorEmail(String email) {
        String sql = "SELECT * FROM usuarios WHERE email = ?";

        try (Connection connection = obtenerConexion();
             PreparedStatement statement = preparar(connection, sql, email.trim().toLowerCase());
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                return Optional.of(mapearUsuario(resultSet));
            }
            return Optional.empty();
        } catch (SQLException ex) {
            throw new DatabaseException("No se pudo buscar el usuario por email.", ex);
        }
    }

    @Override
    public List<Usuario> obtenerTodos() {
        String sql = "SELECT * FROM usuarios ORDER BY id";
        List<Usuario> usuarios = new ArrayList<>();

        try (Connection connection = obtenerConexion();
             PreparedStatement statement = preparar(connection, sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                usuarios.add(mapearUsuario(resultSet));
            }
            return usuarios;
        } catch (SQLException ex) {
            throw new DatabaseException("No se pudieron obtener los usuarios.", ex);
        }
    }

    @Override
    public void actualizar(Usuario usuario) throws UsuarioNoEncontradoException, UsuarioDuplicadoException {
        String sql = """
                UPDATE usuarios
                SET nombre = ?, apellido = ?, email = ?, contrasenia = ?, fecha_alta = ?, estado = ?, rol = ?
                WHERE id = ?
                """;

        try (Connection connection = obtenerConexion();
             PreparedStatement statement = preparar(connection, sql,
                     usuario.getNombre(),
                     usuario.getApellido(),
                     usuario.getEmail(),
                     usuario.getContrasenia(),
                     usuario.getFechaAlta(),
                     usuario.getEstado(),
                     usuario.getRol(),
                     usuario.getId())) {

            int filas = statement.executeUpdate();
            if (filas == 0) {
                throw new UsuarioNoEncontradoException("No se encontró el usuario a actualizar.");
            }
        } catch (SQLException ex) {
            if (esRestriccionUnica(ex)) {
                throw new UsuarioDuplicadoException("Ya existe un usuario con el email indicado.");
            }
            throw new DatabaseException("No se pudo actualizar el usuario.", ex);
        }
    }

    @Override
    public void eliminar(int id) throws UsuarioNoEncontradoException {
        String sql = "DELETE FROM usuarios WHERE id = ?";

        try (Connection connection = obtenerConexion();
             PreparedStatement statement = preparar(connection, sql, id)) {

            int filas = statement.executeUpdate();
            if (filas == 0) {
                throw new UsuarioNoEncontradoException("No se encontró el usuario a eliminar.");
            }
        } catch (SQLException ex) {
            throw new DatabaseException("No se pudo eliminar el usuario.", ex);
        }
    }

    private Usuario mapearUsuario(ResultSet resultSet) throws SQLException {
        return new Usuario(
                resultSet.getInt("id"),
                resultSet.getString("nombre"),
                resultSet.getString("apellido"),
                resultSet.getString("email"),
                resultSet.getString("contrasenia"),
                leerLocalDate(resultSet, "fecha_alta"),
                EstadoUsuario.valueOf(resultSet.getString("estado")),
                RolUsuario.valueOf(resultSet.getString("rol")));
    }

    private boolean esRestriccionUnica(SQLException ex) {
        return ex.getMessage() != null && ex.getMessage().toLowerCase().contains("unique");
    }
}
