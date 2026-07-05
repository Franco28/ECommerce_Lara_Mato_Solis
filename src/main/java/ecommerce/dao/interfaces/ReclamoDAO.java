package ecommerce.dao.interfaces;

import ecommerce.enums.EstadoReclamo;
import ecommerce.model.Reclamo;

import java.util.List;

public interface ReclamoDAO {

    void guardar(Reclamo reclamo);

    Reclamo buscarPorNumero(String numeroReclamo);

    List<Reclamo> obtenerTodos();

    List<Reclamo> obtenerPorEstado(EstadoReclamo estado);

    void actualizarEstado(String numeroReclamo, EstadoReclamo estado);

    void eliminar(String numeroReclamo);
}
