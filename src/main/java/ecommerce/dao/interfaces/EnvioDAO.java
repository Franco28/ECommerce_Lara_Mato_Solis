package ecommerce.dao.interfaces;

import ecommerce.enums.EstadoEnvio;
import ecommerce.exception.EnvioNoEncontradoException;
import ecommerce.model.Envio;
import ecommerce.model.EnvioHistorialEstado;

import java.util.List;

public interface EnvioDAO {

    void guardar(Envio envio);

    Envio buscarPorCodigoSeguimiento(String codigoSeguimiento) throws EnvioNoEncontradoException;

    List<Envio> obtenerTodos();

    List<Envio> obtenerPorEstado(EstadoEnvio estado);

    void actualizar(Envio envio) throws EnvioNoEncontradoException;

    void eliminar(String codigoSeguimiento) throws EnvioNoEncontradoException;

    void registrarHistorial(String codigoSeguimiento, EstadoEnvio estado, String descripcion);

    List<EnvioHistorialEstado> obtenerHistorial(String codigoSeguimiento) throws EnvioNoEncontradoException;
}
