package ecommerce.service;

import ecommerce.dao.interfaces.EnvioDAO;
import ecommerce.enums.EstadoEnvio;
import ecommerce.enums.TipoEnvio;
import ecommerce.model.Envio;
import ecommerce.model.EnvioHistorialEstado;
import ecommerce.util.ValidadorDominio;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

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
        envioDAO.registrarHistorial(envio.getCodigoSeguimiento(), envio.getEstado(),
                "Envío creado y pendiente de preparación.");
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

    public List<Envio> listarPorEstado(EstadoEnvio estado) {
        ValidadorDominio.validarObjetoObligatorio(estado, "El estado de envío es obligatorio.");
        return envioDAO.obtenerPorEstado(estado);
    }

    public void actualizarEstado(String codigoSeguimiento, EstadoEnvio nuevoEstado) {
        ValidadorDominio.validarTextoObligatorio(codigoSeguimiento,
                "El código de seguimiento es obligatorio.");
        ValidadorDominio.validarObjetoObligatorio(nuevoEstado, "El nuevo estado es obligatorio.");

        Envio envio = buscarPorCodigoSeguimiento(codigoSeguimiento);
        validarCambioEstado(envio, nuevoEstado);
        envio.actualizarEstado(nuevoEstado);
        envioDAO.actualizar(envio);
        envioDAO.registrarHistorial(envio.getCodigoSeguimiento(), nuevoEstado,
                "Estado actualizado a " + nuevoEstado + ".");
    }

    public List<EnvioHistorialEstado> consultarHistorial(String codigoSeguimiento) {
        ValidadorDominio.validarTextoObligatorio(codigoSeguimiento,
                "El código de seguimiento es obligatorio.");
        return envioDAO.obtenerHistorial(codigoSeguimiento);
    }

    public LocalDate calcularFechaEstimada(String codigoSeguimiento) {
        Envio envio = buscarPorCodigoSeguimiento(codigoSeguimiento);
        LocalDate fechaBase = consultarHistorial(codigoSeguimiento).stream()
                .findFirst()
                .map(historial -> historial.getFecha().toLocalDate())
                .orElse(LocalDate.now());
        return fechaBase.plusDays(diasEstimadosPorTipo(envio.getTipoEnvio()));
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

    private void validarCambioEstado(Envio envio, EstadoEnvio nuevoEstado) {
        EstadoEnvio estadoActual = envio.getEstado();
        if (estadoActual == EstadoEnvio.ENTREGADO && nuevoEstado != EstadoEnvio.ENTREGADO) {
            throw new ecommerce.exception.DatosInvalidosException(
                    "Un envío entregado no puede volver a otro estado.");
        }
        if (estadoActual == EstadoEnvio.CANCELADO && nuevoEstado != EstadoEnvio.CANCELADO) {
            throw new ecommerce.exception.DatosInvalidosException(
                    "Un envío cancelado no puede volver a otro estado.");
        }
    }

    private long diasEstimadosPorTipo(TipoEnvio tipoEnvio) {
        return switch (tipoEnvio) {
            case RETIRO_EN_SUCURSAL -> 0;
            case EXPRESS -> 2;
            case ESTANDAR -> 5;
            case INTERNACIONAL -> 15;
        };
    }

    private String generarCodigoSeguimiento() {
        return "ENV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
