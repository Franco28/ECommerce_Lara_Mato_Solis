package ecommerce.dao.interfaces;

import ecommerce.enums.EstadoDevolucion;
import ecommerce.model.Devolucion;

import java.util.List;

public interface DevolucionDAO {

    void guardar(Devolucion devolucion);

    Devolucion buscarPorId(int id);

    List<Devolucion> obtenerTodos();

    List<Devolucion> obtenerPorCliente(int clienteId);

    List<Devolucion> obtenerPorProducto(int productoId);

    List<Devolucion> obtenerPorEstado(EstadoDevolucion estado);

    void actualizarEstado(int id, EstadoDevolucion estado);

    void eliminar(int id);
}
