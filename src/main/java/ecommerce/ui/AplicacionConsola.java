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
            AutenticacionMenu autenticacionMenu = new AutenticacionMenu(serviceFactory.autenticacionService(), entrada);
            MenuPrincipal menuPrincipal = new MenuPrincipal(serviceFactory, entrada);

            ConsolaUtils.imprimirMensajeInfo("Base de datos: " + DatabaseConfig.obtenerRutaBaseDatos());

            boolean continuar;
            do {
                continuar = autenticacionMenu.mostrar();
                if (continuar) {
                    menuPrincipal.mostrar();
                    serviceFactory.autenticacionService().cerrarSesion();
                }
            } while (continuar);
        }
    }
}
