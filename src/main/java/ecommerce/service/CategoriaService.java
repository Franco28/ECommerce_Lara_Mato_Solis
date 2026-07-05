package ecommerce.service;

import ecommerce.dao.interfaces.CategoriaDAO;
import ecommerce.enums.EstadoCategoria;
import ecommerce.exception.CategoriaDuplicadaException;
import ecommerce.model.Categoria;
import ecommerce.util.ValidadorDominio;

import java.util.List;

/**
 * Servicio de categorías. Mantiene las reglas de duplicidad y estado
 * fuera del menú por consola.
 */
public class CategoriaService {

    private final CategoriaDAO categoriaDAO;

    public CategoriaService(CategoriaDAO categoriaDAO) {
        this.categoriaDAO = ValidadorDominio.validarObjetoObligatorio(categoriaDAO,
                "El DAO de categorías es obligatorio.");
    }

    public Categoria crearCategoria(String nombre, String descripcion) {
        validarNombreDisponible(nombre);
        Categoria categoria = new Categoria(0, nombre, descripcion, EstadoCategoria.ACTIVA);
        categoriaDAO.guardar(categoria);
        return categoria;
    }

    public void modificarCategoria(Categoria categoria) {
        ValidadorDominio.validarObjetoObligatorio(categoria, "La categoría es obligatoria.");
        validarNombreDisponibleParaOtraCategoria(categoria.getNombre(), categoria.getId());
        categoriaDAO.actualizar(categoria);
    }

    public Categoria buscarPorId(int id) {
        ValidadorDominio.validarEnteroMayorACero(id, "El ID de la categoría debe ser mayor a cero.");
        return categoriaDAO.buscarPorId(id);
    }

    public Categoria buscarPorNombre(String nombre) {
        ValidadorDominio.validarTextoObligatorio(nombre, "El nombre de la categoría es obligatorio.");
        return categoriaDAO.buscarPorNombre(nombre);
    }

    public List<Categoria> listarCategorias() {
        return categoriaDAO.obtenerTodos();
    }

    public void eliminarCategoria(int id) {
        ValidadorDominio.validarEnteroMayorACero(id, "El ID de la categoría debe ser mayor a cero.");
        categoriaDAO.eliminar(id);
    }

    public void activarCategoria(int id) {
        Categoria categoria = buscarPorId(id);
        categoria.activar();
        categoriaDAO.actualizar(categoria);
    }

    public void desactivarCategoria(int id) {
        Categoria categoria = buscarPorId(id);
        categoria.desactivar();
        categoriaDAO.actualizar(categoria);
    }

    private void validarNombreDisponible(String nombre) {
        ValidadorDominio.validarTextoObligatorio(nombre, "El nombre de la categoría es obligatorio.");
        if (categoriaDAO.buscarOpcionalPorNombre(nombre).isPresent()) {
            throw new CategoriaDuplicadaException("Ya existe una categoría con el nombre indicado.");
        }
    }

    private void validarNombreDisponibleParaOtraCategoria(String nombre, int categoriaId) {
        ValidadorDominio.validarTextoObligatorio(nombre, "El nombre de la categoría es obligatorio.");
        categoriaDAO.buscarOpcionalPorNombre(nombre)
                .filter(categoriaExistente -> categoriaExistente.getId() != categoriaId)
                .ifPresent(categoriaExistente -> {
                    throw new CategoriaDuplicadaException("Ya existe otra categoría con el nombre indicado.");
                });
    }
}
