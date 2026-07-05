package ecommerce.database;

import ecommerce.exception.DatabaseException;

/**
 * Punto de entrada auxiliar para probar solamente la Etapa 3.
 */
public class DatabaseInitializerApp {

    public static void main(String[] args) {
        try {
            DatabaseInitializer.inicializarBaseDatos();
            System.out.println("Base de datos inicializada correctamente.");
            System.out.println("Ruta: " + DatabaseConfig.obtenerRutaBaseDatos());
        } catch (DatabaseException ex) {
            System.out.println("Error al inicializar la base de datos: " + ex.getMessage());
            if (ex.getCause() != null) {
                System.out.println("Causa: " + ex.getCause().getMessage());
            }
        }
    }
}
