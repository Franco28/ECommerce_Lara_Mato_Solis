package ecommerce.dao.sqlite;

import ecommerce.dao.interfaces.CategoriaDAO;
import ecommerce.enums.EstadoCategoria;
import ecommerce.exception.CategoriaDuplicadaException;
import ecommerce.exception.CategoriaNoEncontradaException;
import ecommerce.exception.DatabaseException;
import ecommerce.model.Categoria;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SQLiteCategoriaDAO extends SQLiteBaseDAO implements CategoriaDAO {

    @Override
    public void guardar(Categoria categoria) throws CategoriaDuplicadaException {
        String sql = """
                INSERT INTO categorias (nombre, descripcion, estado)
                VALUES (?, ?, ?)
                """;

        try (Connection connection = obtenerConexion();
             PreparedStatement statement = prepararConGeneratedKeys(connection, sql,
                     categoria.getNombre(),
                     categoria.getDescripcion(),
                     categoria.getEstado())) {

            statement.executeUpdate();
            categoria.setId(obtenerIdGenerado(statement));
        } catch (SQLException ex) {
            if (esRestriccionUnica(ex)) {
                throw new CategoriaDuplicadaException("Ya existe una categoría con el nombre indicado.");
            }
            throw new DatabaseException("No se pudo guardar la categoría.", ex);
        }
    }

    @Override
    public Categoria buscarPorId(int id) throws CategoriaNoEncontradaException {
        String sql = "SELECT * FROM categorias WHERE id = ?";

        try (Connection connection = obtenerConexion();
             PreparedStatement statement = preparar(connection, sql, id);
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                return mapearCategoria(resultSet);
            }
            throw new CategoriaNoEncontradaException("No se encontró una categoría con ID " + id + ".");
        } catch (SQLException ex) {
            throw new DatabaseException("No se pudo buscar la categoría por ID.", ex);
        }
    }

    @Override
    public Categoria buscarPorNombre(String nombre) throws CategoriaNoEncontradaException {
        return buscarOpcionalPorNombre(nombre)
                .orElseThrow(() -> new CategoriaNoEncontradaException(
                        "No se encontró una categoría con nombre " + nombre + "."));
    }

    @Override
    public Optional<Categoria> buscarOpcionalPorNombre(String nombre) {
        String sql = "SELECT * FROM categorias WHERE LOWER(nombre) = LOWER(?)";

        try (Connection connection = obtenerConexion();
             PreparedStatement statement = preparar(connection, sql, nombre.trim());
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                return Optional.of(mapearCategoria(resultSet));
            }
            return Optional.empty();
        } catch (SQLException ex) {
            throw new DatabaseException("No se pudo buscar la categoría por nombre.", ex);
        }
    }

    @Override
    public List<Categoria> obtenerTodos() {
        String sql = "SELECT * FROM categorias ORDER BY id";
        List<Categoria> categorias = new ArrayList<>();

        try (Connection connection = obtenerConexion();
             PreparedStatement statement = preparar(connection, sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                categorias.add(mapearCategoria(resultSet));
            }
            return categorias;
        } catch (SQLException ex) {
            throw new DatabaseException("No se pudieron obtener las categorías.", ex);
        }
    }

    @Override
    public void actualizar(Categoria categoria) throws CategoriaNoEncontradaException, CategoriaDuplicadaException {
        String sql = """
                UPDATE categorias
                SET nombre = ?, descripcion = ?, estado = ?
                WHERE id = ?
                """;

        try (Connection connection = obtenerConexion();
             PreparedStatement statement = preparar(connection, sql,
                     categoria.getNombre(),
                     categoria.getDescripcion(),
                     categoria.getEstado(),
                     categoria.getId())) {

            int filas = statement.executeUpdate();
            if (filas == 0) {
                throw new CategoriaNoEncontradaException("No se encontró la categoría a actualizar.");
            }
        } catch (SQLException ex) {
            if (esRestriccionUnica(ex)) {
                throw new CategoriaDuplicadaException("Ya existe una categoría con el nombre indicado.");
            }
            throw new DatabaseException("No se pudo actualizar la categoría.", ex);
        }
    }

    @Override
    public void eliminar(int id) throws CategoriaNoEncontradaException {
        String sql = "DELETE FROM categorias WHERE id = ?";

        try (Connection connection = obtenerConexion();
             PreparedStatement statement = preparar(connection, sql, id)) {

            int filas = statement.executeUpdate();
            if (filas == 0) {
                throw new CategoriaNoEncontradaException("No se encontró la categoría a eliminar.");
            }
        } catch (SQLException ex) {
            throw new DatabaseException("No se pudo eliminar la categoría.", ex);
        }
    }

    Categoria mapearCategoria(ResultSet resultSet) throws SQLException {
        return new Categoria(
                resultSet.getInt("id"),
                resultSet.getString("nombre"),
                resultSet.getString("descripcion"),
                EstadoCategoria.valueOf(resultSet.getString("estado")));
    }

    private boolean esRestriccionUnica(SQLException ex) {
        return ex.getMessage() != null && ex.getMessage().toLowerCase().contains("unique");
    }
}
