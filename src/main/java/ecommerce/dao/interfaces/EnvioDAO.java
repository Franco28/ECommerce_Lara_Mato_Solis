package ecommerce.dao.interfaces;

import ecommerce.exception.EnvioNoEncontradoException;
import ecommerce.model.Envio;

import java.util.List;

public interface EnvioDAO {

    void guardar(Envio envio);

    Envio buscarPorCodigoSeguimiento(String codigoSeguimiento) throws EnvioNoEncontradoException;

    List<Envio> obtenerTodos();

    void actualizar(Envio envio) throws EnvioNoEncontradoException;

    void eliminar(String codigoSeguimiento) throws EnvioNoEncontradoException;
}
