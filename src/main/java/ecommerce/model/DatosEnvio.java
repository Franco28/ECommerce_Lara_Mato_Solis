package ecommerce.model;

import ecommerce.enums.TipoEnvio;
import ecommerce.util.ValidadorDominio;

public class DatosEnvio {

    private final String direccion;
    private final String provincia;
    private final String ciudad;
    private final String codigoPostal;
    private final TipoEnvio tipoEnvio;

    public DatosEnvio(String direccion, String provincia, String ciudad,
            String codigoPostal, TipoEnvio tipoEnvio) {
        ValidadorDominio.validarTextoObligatorio(direccion, "La dirección es obligatoria.");
        ValidadorDominio.validarTextoObligatorio(provincia, "La provincia es obligatoria.");
        ValidadorDominio.validarTextoObligatorio(ciudad, "La ciudad es obligatoria.");
        ValidadorDominio.validarTextoObligatorio(codigoPostal, "El código postal es obligatorio.");

        this.direccion = direccion.trim();
        this.provincia = provincia.trim();
        this.ciudad = ciudad.trim();
        this.codigoPostal = codigoPostal.trim();
        this.tipoEnvio = ValidadorDominio.validarObjetoObligatorio(tipoEnvio,
                "El tipo de envío es obligatorio.");
    }

    public String getDireccion() {
        return direccion;
    }

    public String getProvincia() {
        return provincia;
    }

    public String getCiudad() {
        return ciudad;
    }

    public String getCodigoPostal() {
        return codigoPostal;
    }

    public TipoEnvio getTipoEnvio() {
        return tipoEnvio;
    }
}
