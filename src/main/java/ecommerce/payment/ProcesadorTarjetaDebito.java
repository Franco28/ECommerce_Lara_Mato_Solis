package ecommerce.payment;

import ecommerce.interfaces.ProcesadorPago;

public class ProcesadorTarjetaDebito implements ProcesadorPago {

    private static final double LIMITE_OPERACION = 1000000;

    @Override
    public boolean procesarPago(double monto) {
        return monto > 0 && monto <= LIMITE_OPERACION;
    }
}
