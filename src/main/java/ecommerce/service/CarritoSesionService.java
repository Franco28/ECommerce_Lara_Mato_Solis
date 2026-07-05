package ecommerce.service;

import ecommerce.exception.CarritoVacioException;
import ecommerce.model.Carrito;
import ecommerce.model.Usuario;
import ecommerce.util.ValidadorDominio;

import java.util.HashMap;
import java.util.Map;

public class CarritoSesionService {

    private final CarritoService carritoService;
    private final Map<Integer, Carrito> carritosActivos;

    public CarritoSesionService(CarritoService carritoService) {
        this.carritoService = ValidadorDominio.validarObjetoObligatorio(carritoService,
                "El servicio de carrito es obligatorio.");
        this.carritosActivos = new HashMap<>();
    }

    public Carrito obtenerOCrearCarrito(Usuario cliente) {
        ValidadorDominio.validarObjetoObligatorio(cliente, "El cliente es obligatorio.");
        return carritosActivos.computeIfAbsent(cliente.getId(), id -> carritoService.crearCarrito(cliente));
    }

    public Carrito obtenerCarritoExistente(Usuario cliente) {
        ValidadorDominio.validarObjetoObligatorio(cliente, "El cliente es obligatorio.");
        Carrito carrito = carritosActivos.get(cliente.getId());
        if (carrito == null) {
            throw new CarritoVacioException("El cliente no tiene un carrito activo cargado.");
        }
        return carrito;
    }

    public void quitarCarrito(Usuario cliente) {
        ValidadorDominio.validarObjetoObligatorio(cliente, "El cliente es obligatorio.");
        carritosActivos.remove(cliente.getId());
    }
}
