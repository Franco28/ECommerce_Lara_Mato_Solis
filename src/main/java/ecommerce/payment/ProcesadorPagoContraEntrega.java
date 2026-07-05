package ecommerce.payment;

import ecommerce.interfaces.ProcesadorPago;

public class ProcesadorPagoContraEntrega implements ProcesadorPago {

    private static final double LIMITE_OPERACION = 300000;

    @Override
    public boolean procesarPago(double monto) {
        return monto > 0 && monto <= LIMITE_OPERACION;
    }
}
