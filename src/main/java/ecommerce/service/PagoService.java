package ecommerce.service;

import ecommerce.dao.interfaces.PagoDAO;
import ecommerce.enums.EstadoPago;
import ecommerce.enums.MetodoPago;
import ecommerce.exception.DatosInvalidosException;
import ecommerce.model.Pago;
import ecommerce.util.ValidadorDominio;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio de pagos. Registra y cambia estados.
 */
public class PagoService {

    private final PagoDAO pagoDAO;

    public PagoService(PagoDAO pagoDAO) {
        this.pagoDAO = ValidadorDominio.validarObjetoObligatorio(pagoDAO,
                "El DAO de pagos es obligatorio.");
    }

    public Pago registrarPagoPendiente(MetodoPago metodoPago, double monto) {
        validarPago(metodoPago, monto);
        Pago pago = new Pago(0, metodoPago, monto, EstadoPago.PENDIENTE, LocalDateTime.now());
        pagoDAO.guardar(pago);
        return pago;
    }

    public Pago registrarPagoAprobado(MetodoPago metodoPago, double monto) {
        validarPago(metodoPago, monto);
        Pago pago = new Pago(0, metodoPago, monto, EstadoPago.APROBADO, LocalDateTime.now());
        pagoDAO.guardar(pago);
        return pago;
    }

    public Pago buscarPorId(int id) {
        ValidadorDominio.validarEnteroMayorACero(id, "El ID del pago debe ser mayor a cero.");
        return pagoDAO.buscarPorId(id);
    }

    public List<Pago> listarPagos() {
        return pagoDAO.obtenerTodos();
    }

    public void aprobarPago(int id) {
        Pago pago = buscarPorId(id);
        pago.aprobar();
        pagoDAO.actualizar(pago);
    }

    public void rechazarPago(int id) {
        Pago pago = buscarPorId(id);
        pago.rechazar();
        pagoDAO.actualizar(pago);
    }

    public void cancelarPago(int id) {
        Pago pago = buscarPorId(id);
        pago.cancelar();
        pagoDAO.actualizar(pago);
    }

    public void validarPagoAprobado(Pago pago) {
        ValidadorDominio.validarObjetoObligatorio(pago, "El pago es obligatorio.");
        if (pago.getEstado() != EstadoPago.APROBADO) {
            throw new DatosInvalidosException("La orden solo puede confirmarse con un pago aprobado.");
        }
    }

    private void validarPago(MetodoPago metodoPago, double monto) {
        ValidadorDominio.validarObjetoObligatorio(metodoPago, "El método de pago es obligatorio.");
        ValidadorDominio.validarDecimalMayorACero(monto, "El monto del pago debe ser mayor a cero.");
    }
}
