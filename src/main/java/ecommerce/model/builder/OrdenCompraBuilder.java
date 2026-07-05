package ecommerce.model.builder;

import ecommerce.enums.EstadoOrden;
import ecommerce.model.Carrito;
import ecommerce.model.Envio;
import ecommerce.model.ItemCarrito;
import ecommerce.model.ItemOrden;
import ecommerce.model.OrdenCompra;
import ecommerce.model.Pago;
import ecommerce.model.Usuario;
import ecommerce.util.ValidadorDominio;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrdenCompraBuilder {

    private String numero;
    private Usuario cliente;
    private LocalDateTime fecha;
    private EstadoOrden estado;
    private final List<ItemOrden> items;
    private Pago pago;
    private Envio envio;

    public OrdenCompraBuilder() {
        this.fecha = LocalDateTime.now();
        this.estado = EstadoOrden.CREADA;
        this.items = new ArrayList<>();
    }

    public OrdenCompraBuilder conNumero(String numero) {
        this.numero = numero;
        return this;
    }

    public OrdenCompraBuilder conCliente(Usuario cliente) {
        this.cliente = cliente;
        return this;
    }

    public OrdenCompraBuilder conFecha(LocalDateTime fecha) {
        this.fecha = fecha;
        return this;
    }

    public OrdenCompraBuilder conEstado(EstadoOrden estado) {
        this.estado = estado;
        return this;
    }

    public OrdenCompraBuilder conPago(Pago pago) {
        this.pago = pago;
        return this;
    }

    public OrdenCompraBuilder conEnvio(Envio envio) {
        this.envio = envio;
        return this;
    }

    public OrdenCompraBuilder conItem(ItemOrden item) {
        this.items.add(ValidadorDominio.validarObjetoObligatorio(item,
                "El item de la orden es obligatorio."));
        return this;
    }

    public OrdenCompraBuilder desdeCarrito(Carrito carrito) {
        ValidadorDominio.validarObjetoObligatorio(carrito, "El carrito es obligatorio.");
        this.cliente = carrito.getCliente();
        this.items.clear();
        for (ItemCarrito itemCarrito : carrito.getItems()) {
            this.items.add(new ItemOrden(
                    itemCarrito.getProducto(),
                    itemCarrito.getCantidad(),
                    itemCarrito.getPrecioUnitario()));
        }
        return this;
    }

    public OrdenCompra build() {
        OrdenCompra orden = new OrdenCompra(numero, cliente, fecha, estado);
        for (ItemOrden item : items) {
            orden.agregarItem(item);
        }
        if (pago != null) {
            orden.asociarPago(pago);
        }
        if (envio != null) {
            orden.asociarEnvio(envio);
        }
        return orden;
    }
}
