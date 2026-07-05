package ecommerce.ui;

import ecommerce.exception.EcommerceException;
import ecommerce.model.Categoria;
import ecommerce.service.CategoriaService;

public class CategoriaMenu {

    private final CategoriaService categoriaService;
    private final EntradaConsola entrada;

    public CategoriaMenu(CategoriaService categoriaService, EntradaConsola entrada) {
        this.categoriaService = categoriaService;
        this.entrada = entrada;
    }

    public void mostrar() {
        int opcion;

        do {
            ConsolaUtils.imprimirTitulo("GESTION DE CATEGORIAS");
            ConsolaUtils.imprimirMensajeInfo("Administracion de categorias y estados.");
            ConsolaUtils.imprimirMenuOpciones(
                    "1. Alta de categoria",
                    "2. Modificar categoria",
                    "3. Eliminar categoria",
                    "4. Buscar categoria por ID",
                    "5. Buscar categoria por nombre",
                    "6. Listar categorias",
                    "7. Activar categoria",
                    "8. Desactivar categoria",
                    "0. Volver");

            opcion = entrada.leerEntero("Opcion: ");
            ejecutarOpcion(opcion);
        } while (opcion != 0);
    }

    private void ejecutarOpcion(int opcion) {
        try {
            switch (opcion) {
                case 1 -> crearCategoria();
                case 2 -> modificarCategoria();
                case 3 -> eliminarCategoria();
                case 4 -> buscarPorId();
                case 5 -> buscarPorNombre();
                case 6 -> listarCategorias();
                case 7 -> activarCategoria();
                case 8 -> desactivarCategoria();
                case 0 -> { }
                default -> ConsolaUtils.imprimirMensajeError("Opcion incorrecta.");
            }
        } catch (EcommerceException ex) {
            ConsolaUtils.imprimirMensajeError(ex.getMessage());
        }

        if (opcion != 0) {
            entrada.pausar();
        }
    }

    private void crearCategoria() {
        ConsolaUtils.imprimirTitulo("ALTA DE CATEGORIA");
        String nombre = entrada.leerTexto("Nombre: ");
        String descripcion = entrada.leerTexto("Descripcion: ");

        Categoria categoria = categoriaService.crearCategoria(nombre, descripcion);

        ConsolaUtils.imprimirMensajeExito("Categoria creada correctamente.");
        ConsolaUtils.imprimirCategoria(categoria);
    }

    private void modificarCategoria() {
        ConsolaUtils.imprimirTitulo("MODIFICAR CATEGORIA");
        int id = entrada.leerEntero("ID de categoria: ");
        Categoria categoria = categoriaService.buscarPorId(id);

        ConsolaUtils.imprimirCategoria(categoria);
        System.out.println();

        categoria.setNombre(entrada.leerTextoOpcional("Nombre", categoria.getNombre()));
        categoria.setDescripcion(entrada.leerTextoOpcional("Descripcion", categoria.getDescripcion()));

        categoriaService.modificarCategoria(categoria);
        ConsolaUtils.imprimirMensajeExito("Categoria modificada correctamente.");
    }

    private void eliminarCategoria() {
        ConsolaUtils.imprimirTitulo("ELIMINAR CATEGORIA");
        int id = entrada.leerEntero("ID de categoria: ");
        Categoria categoria = categoriaService.buscarPorId(id);
        ConsolaUtils.imprimirCategoria(categoria);

        if (entrada.confirmar("La categoria se eliminara definitivamente.")) {
            categoriaService.eliminarCategoria(id);
            ConsolaUtils.imprimirMensajeExito("Categoria eliminada correctamente.");
        } else {
            ConsolaUtils.imprimirMensajeInfo("Eliminacion cancelada.");
        }
    }

    private void buscarPorId() {
        ConsolaUtils.imprimirTitulo("BUSCAR CATEGORIA POR ID");
        int id = entrada.leerEntero("ID de categoria: ");
        ConsolaUtils.imprimirCategoria(categoriaService.buscarPorId(id));
    }

    private void buscarPorNombre() {
        ConsolaUtils.imprimirTitulo("BUSCAR CATEGORIA POR NOMBRE");
        String nombre = entrada.leerTexto("Nombre: ");
        ConsolaUtils.imprimirCategoria(categoriaService.buscarPorNombre(nombre));
    }

    private void listarCategorias() {
        ConsolaUtils.imprimirTitulo("LISTADO DE CATEGORIAS");
        ConsolaUtils.imprimirCategorias(categoriaService.listarCategorias());
    }

    private void activarCategoria() {
        ConsolaUtils.imprimirTitulo("ACTIVAR CATEGORIA");
        int id = entrada.leerEntero("ID de categoria: ");
        categoriaService.activarCategoria(id);
        ConsolaUtils.imprimirMensajeExito("Categoria activada correctamente.");
    }

    private void desactivarCategoria() {
        ConsolaUtils.imprimirTitulo("DESACTIVAR CATEGORIA");
        int id = entrada.leerEntero("ID de categoria: ");
        categoriaService.desactivarCategoria(id);
        ConsolaUtils.imprimirMensajeExito("Categoria desactivada correctamente.");
    }
}
