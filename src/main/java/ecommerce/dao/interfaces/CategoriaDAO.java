package ecommerce.dao.interfaces;

import ecommerce.exception.CategoriaDuplicadaException;
import ecommerce.exception.CategoriaNoEncontradaException;
import ecommerce.model.Categoria;

import java.util.List;
import java.util.Optional;

public interface CategoriaDAO {

    void guardar(Categoria categoria) throws CategoriaDuplicadaException;

    Categoria buscarPorId(int id) throws CategoriaNoEncontradaException;

    Categoria buscarPorNombre(String nombre) throws CategoriaNoEncontradaException;

    Optional<Categoria> buscarOpcionalPorNombre(String nombre);

    List<Categoria> obtenerTodos();

    void actualizar(Categoria categoria) throws CategoriaNoEncontradaException, CategoriaDuplicadaException;

    void eliminar(int id) throws CategoriaNoEncontradaException;
}
