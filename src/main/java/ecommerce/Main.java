package ecommerce;

import ecommerce.database.DatabaseConfig;
import ecommerce.dao.factory.DAOFactory;
import ecommerce.enums.EstadoCategoria;
import ecommerce.enums.EstadoProducto;
import ecommerce.enums.EstadoUsuario;
import ecommerce.enums.RolUsuario;
import ecommerce.exception.EcommerceException;
import ecommerce.model.Carrito;
import ecommerce.model.Categoria;
import ecommerce.model.Producto;
import ecommerce.model.ProductoFisico;
import ecommerce.model.Usuario;

import java.time.LocalDate;

public class Main {

    public static void main(String[] args) {
        try {
            Categoria tecnologia = new Categoria(
                    1,
                    "Tecnología",
                    "Productos electrónicos y accesorios",
                    EstadoCategoria.ACTIVA);

            Producto teclado = new ProductoFisico(
                    1,
                    "TEC-001",
                    "Teclado mecánico",
                    "Teclado mecánico retroiluminado",
                    75000,
                    tecnologia,
                    10,
                    0.8,
                    EstadoProducto.ACTIVO);

            Usuario cliente = new Usuario(
                    1,
                    "Franco",
                    "Mato",
                    "franco@email.com",
                    "123456",
                    LocalDate.now(),
                    EstadoUsuario.ACTIVO,
                    RolUsuario.CLIENTE);

            Carrito carrito = new Carrito(1, cliente);
            carrito.agregarProducto(teclado, 2);

            teclado.mostrarInformacion();
            System.out.printf("Total del carrito: $%.2f%n", carrito.calcularTotal());

            System.out.println();
            DAOFactory factory = DAOFactory.obtenerFactory();

            System.out.println("Etapa 4 preparada: DAO, SQLiteDAOFactory e interfaces de persistencia agregadas.");
            System.out.println("Factory configurada: " + factory.getClass().getSimpleName());
            System.out.println("Ruta configurada de base de datos: " + DatabaseConfig.obtenerRutaBaseDatos());
            System.out.println("Para inicializar las tablas ejecutar: mvn clean compile exec:java -Dexec.mainClass=ecommerce.database.DatabaseInitializerApp");

        } catch (EcommerceException ex) {
            System.out.println("Error de negocio: " + ex.getMessage());
        }
    }
}
