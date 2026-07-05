package ecommerce.ui;

import java.util.Scanner;

public class EntradaConsola {

    private static final String PREFIJO_PROMPT = "> ";

    private final Scanner scanner;

    public EntradaConsola(Scanner scanner) {
        this.scanner = scanner;
    }

    public String leerTexto(String mensaje) {
        while (true) {
            System.out.print(PREFIJO_PROMPT + mensaje);
            String valor = scanner.nextLine().trim();

            if (!valor.isEmpty()) {
                return valor;
            }

            System.out.println("[ERROR] El valor ingresado no puede estar vacio.");
        }
    }

    public String leerTextoOpcional(String mensaje, String valorActual) {
        System.out.print(PREFIJO_PROMPT + mensaje + " [actual: " + valorActual + "]: ");
        String valor = scanner.nextLine().trim();
        return valor.isEmpty() ? valorActual : valor;
    }

    public int leerEntero(String mensaje) {
        while (true) {
            System.out.print(PREFIJO_PROMPT + mensaje);
            String valor = scanner.nextLine().trim();

            try {
                return Integer.parseInt(valor);
            } catch (NumberFormatException ex) {
                System.out.println("[ERROR] Debe ingresar un numero entero valido.");
            }
        }
    }

    public int leerEnteroOpcional(String mensaje, int valorActual) {
        while (true) {
            System.out.print(PREFIJO_PROMPT + mensaje + " [actual: " + valorActual + "]: ");
            String valor = scanner.nextLine().trim();

            if (valor.isEmpty()) {
                return valorActual;
            }

            try {
                return Integer.parseInt(valor);
            } catch (NumberFormatException ex) {
                System.out.println("[ERROR] Debe ingresar un numero entero valido.");
            }
        }
    }

    public double leerDecimal(String mensaje) {
        while (true) {
            System.out.print(PREFIJO_PROMPT + mensaje);
            String valor = scanner.nextLine().trim().replace(',', '.');

            try {
                return Double.parseDouble(valor);
            } catch (NumberFormatException ex) {
                System.out.println("[ERROR] Debe ingresar un numero decimal valido.");
            }
        }
    }

    public double leerDecimalOpcional(String mensaje, double valorActual) {
        while (true) {
            System.out.print(PREFIJO_PROMPT + mensaje + " [actual: " + valorActual + "]: ");
            String valor = scanner.nextLine().trim().replace(',', '.');

            if (valor.isEmpty()) {
                return valorActual;
            }

            try {
                return Double.parseDouble(valor);
            } catch (NumberFormatException ex) {
                System.out.println("[ERROR] Debe ingresar un numero decimal valido.");
            }
        }
    }

    public int leerOpcion(String mensaje, int minimo, int maximo) {
        while (true) {
            int opcion = leerEntero(mensaje);

            if (opcion >= minimo && opcion <= maximo) {
                return opcion;
            }

            System.out.println("[ERROR] La opcion ingresada no existe.");
        }
    }

    public boolean confirmar(String mensaje) {
        String confirmacion = leerTexto(mensaje + " Escriba SI para confirmar: ");
        return "SI".equalsIgnoreCase(confirmacion);
    }

    public void pausar() {
        System.out.println();
        System.out.print("[ENTER] Presione Enter para continuar...");
        scanner.nextLine();
    }
}
