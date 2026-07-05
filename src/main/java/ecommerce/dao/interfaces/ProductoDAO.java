package ecommerce.dao.interfaces;

import ecommerce.exception.ProductoDuplicadoException;
import ecommerce.exception.ProductoNoEncontradoException;
import ecommerce.model.Producto;

import java.util.List;
import java.util.Optional;

public interface ProductoDAO {

    void guardar(Producto producto) throws ProductoDuplicadoException;

    Producto buscarPorId(int id) throws ProductoNoEncontradoException;

    Producto buscarPorCodigo(String codigo) throws ProductoNoEncontradoException;

    Optional<Producto> buscarOpcionalPorCodigo(String codigo);

    List<Producto> obtenerTodos();

    List<Producto> obtenerPorCategoria(int categoriaId);

    List<Producto> obtenerSinStock();

    void actualizar(Producto producto) throws ProductoNoEncontradoException, ProductoDuplicadoException;

    void actualizarStock(int productoId, int nuevoStock) throws ProductoNoEncontradoException;

    void eliminar(int id) throws ProductoNoEncontradoException;
}
