package ecommerce.payment;

import ecommerce.interfaces.ProcesadorPago;

public class ProcesadorTransferenciaBancaria implements ProcesadorPago {

    private static final double MONTO_MINIMO = 100;
    private static final double LIMITE_OPERACION = 10000000;

    @Override
    public boolean procesarPago(double monto) {
        return monto >= MONTO_MINIMO && monto <= LIMITE_OPERACION;
    }
}
