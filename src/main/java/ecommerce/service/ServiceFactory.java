package ecommerce.service;

import ecommerce.dao.factory.DAOFactory;
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
import ecommerce.util.ValidadorDominio;

/**
 * Fábrica simple de servicios de aplicación.
 * Reutiliza la DAOFactory para mantener la creación de dependencias en un solo lugar.
 */
public class ServiceFactory {

    private final UsuarioDAO usuarioDAO;
    private final CategoriaDAO categoriaDAO;
    private final ProductoDAO productoDAO;
    private final InventarioDAO inventarioDAO;
    private final PagoDAO pagoDAO;
    private final EnvioDAO envioDAO;
    private final OrdenDAO ordenDAO;
    private final ReclamoDAO reclamoDAO;
    private final DevolucionDAO devolucionDAO;
    private final CalificacionDAO calificacionDAO;

    private final SeguridadService seguridadService;
    private final UsuarioService usuarioService;
    private final CategoriaService categoriaService;
    private final ProductoService productoService;
    private final InventarioService inventarioService;
    private final CarritoService carritoService;
    private final CarritoSesionService carritoSesionService;
    private final PagoService pagoService;
    private final EnvioService envioService;
    private final OrdenService ordenService;
    private final ReclamoService reclamoService;
    private final DevolucionService devolucionService;
    private final CalificacionService calificacionService;
    private final SeguimientoService seguimientoService;
    private final ReporteService reporteService;
    private final CheckoutFacade checkoutFacade;

    public ServiceFactory(DAOFactory daoFactory) {
        ValidadorDominio.validarObjetoObligatorio(daoFactory, "La fábrica de DAOs es obligatoria.");

        this.usuarioDAO = daoFactory.crearUsuarioDAO();
        this.categoriaDAO = daoFactory.crearCategoriaDAO();
        this.productoDAO = daoFactory.crearProductoDAO();
        this.inventarioDAO = daoFactory.crearInventarioDAO();
        this.pagoDAO = daoFactory.crearPagoDAO();
        this.envioDAO = daoFactory.crearEnvioDAO();
        this.ordenDAO = daoFactory.crearOrdenDAO();
        this.reclamoDAO = daoFactory.crearReclamoDAO();
        this.devolucionDAO = daoFactory.crearDevolucionDAO();
        this.calificacionDAO = daoFactory.crearCalificacionDAO();

        this.seguridadService = new SeguridadService();
        this.usuarioService = new UsuarioService(usuarioDAO);
        this.categoriaService = new CategoriaService(categoriaDAO);
        this.productoService = new ProductoService(productoDAO, categoriaDAO);
        this.inventarioService = new InventarioService(productoDAO, inventarioDAO);
        this.carritoService = new CarritoService(productoDAO, seguridadService);
        this.carritoSesionService = new CarritoSesionService(carritoService);
        this.pagoService = new PagoService(pagoDAO);
        this.envioService = new EnvioService(envioDAO);
        this.ordenService = new OrdenService(ordenDAO, inventarioService, carritoService, seguridadService);
        this.reclamoService = new ReclamoService(reclamoDAO, ordenService, seguridadService);
        this.devolucionService = new DevolucionService(devolucionDAO, ordenService, productoService, seguridadService);
        this.calificacionService = new CalificacionService(calificacionDAO, ordenService, productoService, seguridadService);
        this.seguimientoService = new SeguimientoService(ordenService, envioService);
        this.reporteService = new ReporteService(usuarioDAO, productoDAO, ordenDAO, reclamoDAO, envioDAO);
        this.checkoutFacade = new CheckoutFacade(carritoSesionService, carritoService, pagoService, envioService, ordenService);
    }

    public static ServiceFactory crearConSQLite() {
        return new ServiceFactory(DAOFactory.obtenerFactory());
    }

    public SeguridadService seguridadService() {
        return seguridadService;
    }

    public UsuarioService usuarioService() {
        return usuarioService;
    }

    public CategoriaService categoriaService() {
        return categoriaService;
    }

    public ProductoService productoService() {
        return productoService;
    }

    public InventarioService inventarioService() {
        return inventarioService;
    }

    public CarritoService carritoService() {
        return carritoService;
    }

    public CarritoSesionService carritoSesionService() {
        return carritoSesionService;
    }

    public PagoService pagoService() {
        return pagoService;
    }

    public EnvioService envioService() {
        return envioService;
    }

    public OrdenService ordenService() {
        return ordenService;
    }

    public ReclamoService reclamoService() {
        return reclamoService;
    }

    public DevolucionService devolucionService() {
        return devolucionService;
    }

    public CalificacionService calificacionService() {
        return calificacionService;
    }

    public SeguimientoService seguimientoService() {
        return seguimientoService;
    }

    public ReporteService reporteService() {
        return reporteService;
    }

    public CheckoutFacade checkoutFacade() {
        return checkoutFacade;
    }
}
