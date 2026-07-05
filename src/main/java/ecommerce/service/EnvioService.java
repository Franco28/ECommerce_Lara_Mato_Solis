package ecommerce.service;

import ecommerce.dao.interfaces.EnvioDAO;
import ecommerce.enums.EstadoEnvio;
import ecommerce.enums.TipoEnvio;
import ecommerce.model.Envio;
import ecommerce.util.ValidadorDominio;

import java.util.List;
import java.util.UUID;

/**
 * Servicio de envíos. Crea envíos con costo calculado por tipo y permite
 * actualizar estados de entrega.
 */
public class EnvioService {

    private final EnvioDAO envioDAO;

    public EnvioService(EnvioDAO envioDAO) {
        this.envioDAO = ValidadorDominio.validarObjetoObligatorio(envioDAO,
                "El DAO de envíos es obligatorio.");
    }

    public Envio crearEnvio(String direccion, String provincia, String ciudad,
            String codigoPostal, TipoEnvio tipoEnvio) {
        ValidadorDominio.validarObjetoObligatorio(tipoEnvio, "El tipo de envío es obligatorio.");

        Envio envio = new Envio(
                generarCodigoSeguimiento(),
                direccion,
                provincia,
                ciudad,
                codigoPostal,
                tipoEnvio,
                EstadoEnvio.PENDIENTE,
                calcularCostoPorTipo(tipoEnvio));

        envioDAO.guardar(envio);
        return envio;
    }

    public Envio buscarPorCodigoSeguimiento(String codigoSeguimiento) {
        ValidadorDominio.validarTextoObligatorio(codigoSeguimiento,
                "El código de seguimiento es obligatorio.");
        return envioDAO.buscarPorCodigoSeguimiento(codigoSeguimiento);
    }

    public List<Envio> listarEnvios() {
        return envioDAO.obtenerTodos();
    }

    public void actualizarEstado(String codigoSeguimiento, EstadoEnvio nuevoEstado) {
        Envio envio = buscarPorCodigoSeguimiento(codigoSeguimiento);
        envio.actualizarEstado(nuevoEstado);
        envioDAO.actualizar(envio);
    }

    public void eliminarEnvio(String codigoSeguimiento) {
        ValidadorDominio.validarTextoObligatorio(codigoSeguimiento,
                "El código de seguimiento es obligatorio.");
        envioDAO.eliminar(codigoSeguimiento);
    }

    public double calcularCostoPorTipo(TipoEnvio tipoEnvio) {
        ValidadorDominio.validarObjetoObligatorio(tipoEnvio, "El tipo de envío es obligatorio.");
        return switch (tipoEnvio) {
            case RETIRO_EN_SUCURSAL -> 0;
            case ESTANDAR -> 3500;
            case EXPRESS -> 6500;
            case INTERNACIONAL -> 25000;
        };
    }

    private String generarCodigoSeguimiento() {
        return "ENV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
