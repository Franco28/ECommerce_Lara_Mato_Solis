package ecommerce.exception;

/**
 * Excepción específica para errores de infraestructura de base de datos.
 * Se separa de las excepciones de negocio para distinguir fallas técnicas
 * de validaciones propias del dominio.
 */
public class DatabaseException extends EcommerceException {

    public DatabaseException(String mensaje) {
        super(mensaje);
    }

    public DatabaseException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
