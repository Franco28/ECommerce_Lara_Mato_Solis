package ecommerce.ui;

import ecommerce.model.Categoria;
import ecommerce.service.CategoriaService;

import java.util.List;

public class CategoriaSelector {

    private final CategoriaService categoriaService;
    private final EntradaConsola entrada;

    public CategoriaSelector(CategoriaService categoriaService, EntradaConsola entrada) {
        this.categoriaService = categoriaService;
        this.entrada = entrada;
    }

    public Categoria seleccionarCategoria() {
        List<Categoria> categorias = categoriaService.listarCategorias();
        ConsolaUtils.imprimirCategorias(categorias);
        int id = entrada.leerEntero("ID de categoria: ");
        return categoriaService.buscarPorId(id);
    }

    public Categoria seleccionarCategoriaOpcional(Categoria categoriaActual) {
        ConsolaUtils.imprimirEtiquetaValor("Categoria actual",
                categoriaActual.getNombre() + " (ID " + categoriaActual.getId() + ")");
        ConsolaUtils.imprimirMenuOpciones("0. Mantener categoria actual");
        ConsolaUtils.imprimirCategorias(categoriaService.listarCategorias());

        int id = entrada.leerEntero("ID de categoria: ");
        if (id == 0) {
            return categoriaActual;
        }
        return categoriaService.buscarPorId(id);
    }
}
