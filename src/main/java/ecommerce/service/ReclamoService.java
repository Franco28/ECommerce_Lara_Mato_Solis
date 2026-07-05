package ecommerce.service;

import ecommerce.dao.interfaces.ReclamoDAO;
import ecommerce.enums.EstadoReclamo;
import ecommerce.enums.RolUsuario;
import ecommerce.exception.PermisoDenegadoException;
import ecommerce.model.OrdenCompra;
import ecommerce.model.Reclamo;
import ecommerce.model.Usuario;
import ecommerce.util.ValidadorDominio;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * Servicio de reclamos. Asegura que el reclamo se genere sobre una orden existente
 * y que el cliente corresponda con el pedido asociado.
 */
public class ReclamoService {

    private static final DateTimeFormatter RECLAMO_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final ReclamoDAO reclamoDAO;
    private final OrdenService ordenService;
    private final SeguridadService seguridadService;

    public ReclamoService(ReclamoDAO reclamoDAO, OrdenService ordenService) {
        this(reclamoDAO, ordenService, new SeguridadService());
    }

    public ReclamoService(ReclamoDAO reclamoDAO, OrdenService ordenService,
            SeguridadService seguridadService) {
        this.reclamoDAO = ValidadorDominio.validarObjetoObligatorio(reclamoDAO,
                "El DAO de reclamos es obligatorio.");
        this.ordenService = ValidadorDominio.validarObjetoObligatorio(ordenService,
                "El servicio de órdenes es obligatorio.");
        this.seguridadService = ValidadorDominio.validarObjetoObligatorio(seguridadService,
                "El servicio de seguridad es obligatorio.");
    }

    public Reclamo generarReclamo(Usuario cliente, String numeroOrden, String motivo) {
        seguridadService.validarRol(cliente, RolUsuario.CLIENTE);
        OrdenCompra orden = ordenService.buscarPorNumero(numeroOrden);

        if (orden.getCliente().getId() != cliente.getId()) {
            throw new PermisoDenegadoException("El cliente solo puede reclamar sus propias órdenes.");
        }

        Reclamo reclamo = new Reclamo(
                generarNumeroReclamo(),
                cliente,
                orden,
                motivo,
                LocalDateTime.now(),
                EstadoReclamo.ABIERTO);

        reclamoDAO.guardar(reclamo);
        return reclamo;
    }

    public Reclamo buscarPorNumero(String numeroReclamo) {
        ValidadorDominio.validarTextoObligatorio(numeroReclamo,
                "El número de reclamo es obligatorio.");
        return reclamoDAO.buscarPorNumero(numeroReclamo);
    }

    public List<Reclamo> listarReclamos() {
        return reclamoDAO.obtenerTodos();
    }

    public List<Reclamo> listarPorEstado(EstadoReclamo estado) {
        ValidadorDominio.validarObjetoObligatorio(estado, "El estado de reclamo es obligatorio.");
        return reclamoDAO.obtenerPorEstado(estado);
    }

    public void cambiarEstado(String numeroReclamo, EstadoReclamo nuevoEstado) {
        ValidadorDominio.validarTextoObligatorio(numeroReclamo,
                "El número de reclamo es obligatorio.");
        ValidadorDominio.validarObjetoObligatorio(nuevoEstado,
                "El nuevo estado del reclamo es obligatorio.");
        reclamoDAO.actualizarEstado(numeroReclamo, nuevoEstado);
    }

    public void eliminarReclamo(String numeroReclamo) {
        ValidadorDominio.validarTextoObligatorio(numeroReclamo,
                "El número de reclamo es obligatorio.");
        reclamoDAO.eliminar(numeroReclamo);
    }

    private String generarNumeroReclamo() {
        return "REC-" + LocalDateTime.now().format(RECLAMO_FORMATTER)
                + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
}
