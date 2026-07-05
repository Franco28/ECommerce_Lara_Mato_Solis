package ecommerce.payment;

import ecommerce.interfaces.ProcesadorPago;

public class ProcesadorTarjetaCredito implements ProcesadorPago {

    private static final double LIMITE_OPERACION = 2000000;

    @Override
    public boolean procesarPago(double monto) {
        return monto > 0 && monto <= LIMITE_OPERACION;
    }
}
