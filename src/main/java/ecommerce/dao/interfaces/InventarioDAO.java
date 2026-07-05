package ecommerce.dao.interfaces;

import ecommerce.model.InventarioMovimiento;

import java.util.List;

public interface InventarioDAO {

    void guardarMovimiento(InventarioMovimiento movimiento);

    List<InventarioMovimiento> obtenerTodos();

    List<InventarioMovimiento> obtenerHistorialPorProducto(int productoId);
}
