package ecommerce.payment;

import ecommerce.enums.MetodoPago;
import ecommerce.exception.DatosInvalidosException;
import ecommerce.interfaces.ProcesadorPago;
import ecommerce.util.ValidadorDominio;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

public final class ProcesadorPagoFactory {

    private static final Map<MetodoPago, Supplier<ProcesadorPago>> PROCESADORES = new EnumMap<>(MetodoPago.class);

    static {
        PROCESADORES.put(MetodoPago.TARJETA_CREDITO, ProcesadorTarjetaCredito::new);
        PROCESADORES.put(MetodoPago.TARJETA_DEBITO, ProcesadorTarjetaDebito::new);
        PROCESADORES.put(MetodoPago.TRANSFERENCIA_BANCARIA, ProcesadorTransferenciaBancaria::new);
        PROCESADORES.put(MetodoPago.BILLETERA_VIRTUAL, ProcesadorBilleteraVirtual::new);
        PROCESADORES.put(MetodoPago.PAGO_CONTRA_ENTREGA, ProcesadorPagoContraEntrega::new);
    }

    private ProcesadorPagoFactory() {
    }

    public static ProcesadorPago crearProcesador(MetodoPago metodoPago) {
        ValidadorDominio.validarObjetoObligatorio(metodoPago, "El método de pago es obligatorio.");
        Supplier<ProcesadorPago> supplier = PROCESADORES.get(metodoPago);

        if (supplier == null) {
            throw new DatosInvalidosException("No existe un procesador para el método de pago seleccionado.");
        }

        return supplier.get();
    }
}
