package ecommerce.dao.factory;

import ecommerce.dao.interfaces.CalificacionDAO;
import ecommerce.dao.interfaces.CategoriaDAO;
import ecommerce.dao.interfaces.DevolucionDAO;
import ecommerce.dao.interfaces.EnvioDAO;
import ecommerce.dao.interfaces.InventarioDAO;
import ecommerce.dao.interfaces.OrdenDAO;
import ecommerce.dao.interfaces.PagoDAO;
import ecommerce.dao.interfaces.ProductoDAO;
import ecommerce.dao.interfaces.ReclamoDAO;
import ecommerce.dao.interfaces.UsuarioDAO;

public abstract class DAOFactory {

    public abstract UsuarioDAO crearUsuarioDAO();

    public abstract CategoriaDAO crearCategoriaDAO();

    public abstract ProductoDAO crearProductoDAO();

    public abstract InventarioDAO crearInventarioDAO();

    public abstract PagoDAO crearPagoDAO();

    public abstract EnvioDAO crearEnvioDAO();

    public abstract OrdenDAO crearOrdenDAO();

    public abstract ReclamoDAO crearReclamoDAO();

    public abstract DevolucionDAO crearDevolucionDAO();

    public abstract CalificacionDAO crearCalificacionDAO();

    public static DAOFactory obtenerFactory() {
        return new SQLiteDAOFactory();
    }
}
