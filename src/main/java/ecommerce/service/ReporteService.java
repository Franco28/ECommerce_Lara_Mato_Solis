package ecommerce.service;

import ecommerce.dao.interfaces.EnvioDAO;
import ecommerce.dao.interfaces.OrdenDAO;
import ecommerce.dao.interfaces.ProductoDAO;
import ecommerce.dao.interfaces.ReclamoDAO;
import ecommerce.dao.interfaces.UsuarioDAO;
import ecommerce.enums.EstadoEnvio;
import ecommerce.enums.EstadoOrden;
import ecommerce.enums.EstadoPago;
import ecommerce.enums.EstadoReclamo;
import ecommerce.enums.MetodoPago;
import ecommerce.enums.RolUsuario;
import ecommerce.model.Envio;
import ecommerce.model.ItemOrden;
import ecommerce.model.OrdenCompra;
import ecommerce.model.Pago;
import ecommerce.model.Producto;
import ecommerce.model.Reclamo;
import ecommerce.model.Usuario;
import ecommerce.util.ValidadorDominio;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Servicio de reportes. Calcula métricas de gestión sobre los datos obtenidos
 * desde los DAOs. 
 */
public class ReporteService {

    private final UsuarioDAO usuarioDAO;
    private final ProductoDAO productoDAO;
    private final OrdenDAO ordenDAO;
    private final ReclamoDAO reclamoDAO;
    private final EnvioDAO envioDAO;

    public ReporteService(UsuarioDAO usuarioDAO, ProductoDAO productoDAO,
            OrdenDAO ordenDAO, ReclamoDAO reclamoDAO, EnvioDAO envioDAO) {
        this.usuarioDAO = ValidadorDominio.validarObjetoObligatorio(usuarioDAO,
                "El DAO de usuarios es obligatorio.");
        this.productoDAO = ValidadorDominio.validarObjetoObligatorio(productoDAO,
                "El DAO de productos es obligatorio.");
        this.ordenDAO = ValidadorDominio.validarObjetoObligatorio(ordenDAO,
                "El DAO de órdenes es obligatorio.");
        this.reclamoDAO = ValidadorDominio.validarObjetoObligatorio(reclamoDAO,
                "El DAO de reclamos es obligatorio.");
        this.envioDAO = ValidadorDominio.validarObjetoObligatorio(envioDAO,
                "El DAO de envíos es obligatorio.");
    }

    public int cantidadTotalUsuarios() {
        return usuarioDAO.obtenerTodos().size();
    }

    public long cantidadClientes() {
        return usuarioDAO.obtenerTodos().stream()
                .filter(usuario -> usuario.tieneRol(RolUsuario.CLIENTE))
                .count();
    }

    public int cantidadProductos() {
        return productoDAO.obtenerTodos().size();
    }

    public Map<String, Long> productosPorCategoria() {
        return productoDAO.obtenerTodos().stream()
                .collect(Collectors.groupingBy(
                        producto -> producto.getCategoria().getNombre(),
                        LinkedHashMap::new,
                        Collectors.counting()));
    }

    public List<Producto> productosSinStock() {
        return productoDAO.obtenerSinStock();
    }

    public Map<String, Integer> productosMasVendidos() {
        Map<String, Integer> acumulado = new LinkedHashMap<>();

        for (OrdenCompra orden : ordenDAO.obtenerTodos()) {
            for (ItemOrden item : orden.getProductos()) {
                acumulado.merge(item.getProducto().getCodigo(), item.getCantidad(), Integer::sum);
            }
        }

        return acumulado.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (valorAnterior, valorNuevo) -> valorAnterior,
                        LinkedHashMap::new));
    }

    public int ordenesGeneradas() {
        return ordenDAO.obtenerTodos().size();
    }

    public Map<EstadoOrden, Long> ordenesPorEstado() {
        EnumMap<EstadoOrden, Long> resultado = new EnumMap<>(EstadoOrden.class);
        ordenDAO.obtenerTodos().forEach(orden ->
                resultado.merge(orden.getEstado(), 1L, Long::sum));
        return resultado;
    }

    public double recaudacionTotal() {
        return ordenDAO.obtenerTodos().stream()
                .filter(this::tienePagoAprobado)
                .mapToDouble(OrdenCompra::getTotal)
                .sum();
    }

    public Map<MetodoPago, Double> recaudacionPorMetodoPago() {
        EnumMap<MetodoPago, Double> resultado = new EnumMap<>(MetodoPago.class);

        for (OrdenCompra orden : ordenDAO.obtenerTodos()) {
            Pago pago = orden.getPago();
            if (pago != null && pago.getEstado() == EstadoPago.APROBADO) {
                resultado.merge(pago.getMetodoPago(), orden.getTotal(), Double::sum);
            }
        }

        return resultado;
    }

    public Map<String, Long> clientesConMasCompras() {
        return ordenDAO.obtenerTodos().stream()
                .collect(Collectors.groupingBy(
                        orden -> nombreCliente(orden.getCliente()),
                        LinkedHashMap::new,
                        Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (valorAnterior, valorNuevo) -> valorAnterior,
                        LinkedHashMap::new));
    }

    public List<Reclamo> reclamosAbiertos() {
        return reclamoDAO.obtenerPorEstado(EstadoReclamo.ABIERTO);
    }

    public List<Reclamo> reclamosResueltos() {
        return reclamoDAO.obtenerPorEstado(EstadoReclamo.RESUELTO);
    }

    public List<Envio> enviosPendientes() {
        return envioDAO.obtenerTodos().stream()
                .filter(envio -> envio.getEstado() == EstadoEnvio.PENDIENTE)
                .toList();
    }

    public List<Envio> enviosEntregados() {
        return envioDAO.obtenerTodos().stream()
                .filter(envio -> envio.getEstado() == EstadoEnvio.ENTREGADO)
                .toList();
    }

    private boolean tienePagoAprobado(OrdenCompra orden) {
        return orden.getPago() != null && orden.getPago().getEstado() == EstadoPago.APROBADO;
    }

    private String nombreCliente(Usuario usuario) {
        return usuario.getNombre() + " " + usuario.getApellido() + " <" + usuario.getEmail() + ">";
    }
}
