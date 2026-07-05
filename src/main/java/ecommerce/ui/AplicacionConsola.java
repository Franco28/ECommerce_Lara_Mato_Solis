package ecommerce.ui;

import ecommerce.database.DatabaseConfig;
import ecommerce.database.DatabaseInitializer;
import ecommerce.service.ServiceFactory;

import java.util.Scanner;

public class AplicacionConsola {

    public void iniciar() {
        DatabaseInitializer.inicializarBaseDatos();

        try (Scanner scanner = new Scanner(System.in)) {
            EntradaConsola entrada = new EntradaConsola(scanner);
            ServiceFactory serviceFactory = ServiceFactory.crearConSQLite();

            System.out.println("Base de datos: " + DatabaseConfig.obtenerRutaBaseDatos());
            new MenuPrincipal(serviceFactory, entrada).mostrar();
        }
    }
}
