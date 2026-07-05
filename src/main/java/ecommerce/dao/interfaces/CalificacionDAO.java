package ecommerce.dao.interfaces;

import ecommerce.model.Calificacion;

import java.util.List;

public interface CalificacionDAO {

    void guardar(Calificacion calificacion);

    Calificacion buscarPorId(int id);

    List<Calificacion> obtenerTodos();

    List<Calificacion> obtenerPorCliente(int clienteId);

    List<Calificacion> obtenerPorProducto(int productoId);

    double obtenerPromedioPorProducto(int productoId);

    void eliminar(int id);
}
