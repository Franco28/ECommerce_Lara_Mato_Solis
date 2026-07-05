package ecommerce.dao.interfaces;

import ecommerce.enums.EstadoOrden;
import ecommerce.exception.OrdenNoEncontradaException;
import ecommerce.model.OrdenCompra;

import java.util.List;

public interface OrdenDAO {

    void guardar(OrdenCompra orden);

    OrdenCompra buscarPorNumero(String numero) throws OrdenNoEncontradaException;

    List<OrdenCompra> obtenerTodos();

    List<OrdenCompra> obtenerPorCliente(int clienteId);

    List<OrdenCompra> obtenerPorEstado(EstadoOrden estado);

    void actualizarEstado(String numero, EstadoOrden estado) throws OrdenNoEncontradaException;

    void eliminar(String numero) throws OrdenNoEncontradaException;
}
