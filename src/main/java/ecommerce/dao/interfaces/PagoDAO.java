package ecommerce.dao.interfaces;

import ecommerce.model.Pago;

import java.util.List;

public interface PagoDAO {

    void guardar(Pago pago);

    Pago buscarPorId(int id);

    List<Pago> obtenerTodos();

    void actualizar(Pago pago);

    void eliminar(int id);
}
