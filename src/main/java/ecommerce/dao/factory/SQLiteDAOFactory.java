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
import ecommerce.dao.sqlite.SQLiteCalificacionDAO;
import ecommerce.dao.sqlite.SQLiteCategoriaDAO;
import ecommerce.dao.sqlite.SQLiteDevolucionDAO;
import ecommerce.dao.sqlite.SQLiteEnvioDAO;
import ecommerce.dao.sqlite.SQLiteInventarioDAO;
import ecommerce.dao.sqlite.SQLiteOrdenDAO;
import ecommerce.dao.sqlite.SQLitePagoDAO;
import ecommerce.dao.sqlite.SQLiteProductoDAO;
import ecommerce.dao.sqlite.SQLiteReclamoDAO;
import ecommerce.dao.sqlite.SQLiteUsuarioDAO;

public class SQLiteDAOFactory extends DAOFactory {

    @Override
    public UsuarioDAO crearUsuarioDAO() {
        return new SQLiteUsuarioDAO();
    }

    @Override
    public CategoriaDAO crearCategoriaDAO() {
        return new SQLiteCategoriaDAO();
    }

    @Override
    public ProductoDAO crearProductoDAO() {
        return new SQLiteProductoDAO();
    }

    @Override
    public InventarioDAO crearInventarioDAO() {
        return new SQLiteInventarioDAO();
    }

    @Override
    public PagoDAO crearPagoDAO() {
        return new SQLitePagoDAO();
    }

    @Override
    public EnvioDAO crearEnvioDAO() {
        return new SQLiteEnvioDAO();
    }

    @Override
    public OrdenDAO crearOrdenDAO() {
        return new SQLiteOrdenDAO();
    }

    @Override
    public ReclamoDAO crearReclamoDAO() {
        return new SQLiteReclamoDAO();
    }

    @Override
    public DevolucionDAO crearDevolucionDAO() {
        return new SQLiteDevolucionDAO();
    }

    @Override
    public CalificacionDAO crearCalificacionDAO() {
        return new SQLiteCalificacionDAO();
    }
}
