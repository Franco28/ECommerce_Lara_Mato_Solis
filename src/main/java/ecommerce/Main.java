package ecommerce;

import ecommerce.exception.EcommerceException;
import ecommerce.ui.AplicacionConsola;

public class Main {

    public static void main(String[] args) {
        try {
            new AplicacionConsola().iniciar();
        } catch (EcommerceException ex) {
            System.out.println("Error: " + ex.getMessage());
        } catch (Exception ex) {
            System.out.println("Error inesperado al ejecutar el sistema.");
            System.out.println(ex.getMessage());
        }
    }
}
