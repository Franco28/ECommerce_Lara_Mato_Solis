package ecommerce.service;

import ecommerce.model.Envio;
import ecommerce.model.EnvioHistorialEstado;
import ecommerce.model.OrdenCompra;
import ecommerce.util.ValidadorDominio;

import java.time.LocalDate;
import java.util.List;

public class SeguimientoService {

    private final OrdenService ordenService;
    private final EnvioService envioService;

    public SeguimientoService(OrdenService ordenService, EnvioService envioService) {
        this.ordenService = ValidadorDominio.validarObjetoObligatorio(ordenService,
                "El servicio de órdenes es obligatorio.");
        this.envioService = ValidadorDominio.validarObjetoObligatorio(envioService,
                "El servicio de envíos es obligatorio.");
    }

    public OrdenCompra consultarPedido(String numeroOrden) {
        ValidadorDominio.validarTextoObligatorio(numeroOrden,
                "El número de orden es obligatorio.");
        return ordenService.buscarPorNumero(numeroOrden);
    }

    public Envio consultarEnvio(String codigoSeguimiento) {
        ValidadorDominio.validarTextoObligatorio(codigoSeguimiento,
                "El código de seguimiento es obligatorio.");
        return envioService.buscarPorCodigoSeguimiento(codigoSeguimiento);
    }

    public Envio consultarEnvioPorOrden(String numeroOrden) {
        OrdenCompra orden = consultarPedido(numeroOrden);
        if (orden.getEnvio() == null) {
            throw new ecommerce.exception.EnvioNoEncontradoException(
                    "La orden no tiene un envío asociado.");
        }
        return orden.getEnvio();
    }

    public List<EnvioHistorialEstado> consultarHistorialPorCodigo(String codigoSeguimiento) {
        ValidadorDominio.validarTextoObligatorio(codigoSeguimiento,
                "El código de seguimiento es obligatorio.");
        return envioService.consultarHistorial(codigoSeguimiento);
    }

    public List<EnvioHistorialEstado> consultarHistorialPorOrden(String numeroOrden) {
        Envio envio = consultarEnvioPorOrden(numeroOrden);
        return envioService.consultarHistorial(envio.getCodigoSeguimiento());
    }

    public LocalDate consultarFechaEstimadaPorCodigo(String codigoSeguimiento) {
        ValidadorDominio.validarTextoObligatorio(codigoSeguimiento,
                "El código de seguimiento es obligatorio.");
        return envioService.calcularFechaEstimada(codigoSeguimiento);
    }

    public LocalDate consultarFechaEstimadaPorOrden(String numeroOrden) {
        Envio envio = consultarEnvioPorOrden(numeroOrden);
        return envioService.calcularFechaEstimada(envio.getCodigoSeguimiento());
    }
}
