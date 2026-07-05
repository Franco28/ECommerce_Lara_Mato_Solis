package ecommerce.model;

import ecommerce.enums.EstadoEnvio;
import ecommerce.util.ValidadorDominio;

import java.time.LocalDateTime;

public class EnvioHistorialEstado {

    private int id;
    private String codigoSeguimiento;
    private EstadoEnvio estado;
    private LocalDateTime fecha;
    private String descripcion;

    public EnvioHistorialEstado(int id, String codigoSeguimiento, EstadoEnvio estado,
            LocalDateTime fecha, String descripcion) {
        this.id = id;
        setCodigoSeguimiento(codigoSeguimiento);
        this.estado = ValidadorDominio.validarObjetoObligatorio(estado,
                "El estado del envío es obligatorio.");
        this.fecha = ValidadorDominio.validarObjetoObligatorio(fecha,
                "La fecha del historial es obligatoria.");
        setDescripcion(descripcion);
    }

    public int getId() {
        return id;
    }

    public String getCodigoSeguimiento() {
        return codigoSeguimiento;
    }

    public void setCodigoSeguimiento(String codigoSeguimiento) {
        ValidadorDominio.validarTextoObligatorio(codigoSeguimiento,
                "El código de seguimiento es obligatorio.");
        this.codigoSeguimiento = codigoSeguimiento.trim().toUpperCase();
    }

    public EstadoEnvio getEstado() {
        return estado;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        ValidadorDominio.validarTextoObligatorio(descripcion,
                "La descripción del historial es obligatoria.");
        this.descripcion = descripcion.trim();
    }
}
