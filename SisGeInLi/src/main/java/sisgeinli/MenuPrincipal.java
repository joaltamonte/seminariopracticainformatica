package sisgeinli;
/*
clase principal encargada de iniciar el programa y mostrar en consola el menu
*/
import java.util.Scanner;

public class MenuPrincipal {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Autenticador auth = new Autenticador();
        Usuario usuarioLogueado = null;

        System.out.println("=========================================");
        System.out.println("   SISTEMA DE GESTIÓN DE INSUMOS         ");
        System.out.println("=========================================");

        // ── LOGIN ─────────────────────────────────────────────────
        while (usuarioLogueado == null) {
            System.out.print("👤 Usuario: ");
            String user = scanner.nextLine();
            System.out.print("🔑 Contraseña: ");
            String pass = scanner.nextLine();
            try {
                usuarioLogueado = auth.login(user, pass);
                System.out.println("\n✅ Bienvenido " + usuarioLogueado.getUsername()
                        + " (" + usuarioLogueado.getRol() + ")");
            } catch (InsumoExcepcion e) {
                System.out.println("❌ " + e.getMessage() + "\n");
            }
        }

        // ── GESTORES ──────────────────────────────────────────────
        ImportadorProducto importador   = new ImportadorProducto();
        GestorProveedor     gestorProv   = new GestorProveedor();
        ConsultadorPrecios  consultador  = new ConsultadorPrecios();
        boolean ejecutando = true;

        // ── MENÚ ──────────────────────────────────────────────────
        while (ejecutando) {
            System.out.println("\n_______________________________________________");
            System.out.println("  SISTEMA DE GESTIÓN DE INSUMOS DE LIBRERÍA    ");
            System.out.println("  Usuario: " + usuarioLogueado.getUsername()
                    + " | Rol: " + usuarioLogueado.getRol());
            System.out.println("_______________________________________________");
            System.out.println("1. Importar Excel del proveedor (.xlsx)");
            System.out.println("2. Guardar productos importados en BD");
            System.out.println("3. Alta de nuevo proveedor");
            System.out.println("4. Guardar proveedores en BD");
            System.out.println("5. Consultar precio por código de barras");
            System.out.println("6. Consultar precio por descripción");
            System.out.println("7. Salir");
            System.out.println("_______________________________________________");
            System.out.print("Opción: ");

            int opcion;
            try {
                opcion = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("❌ Ingrese un número válido.");
                continue;
            }

            switch (opcion) {

                case 1 -> {
                    System.out.print("Ruta del archivo Excel: ");
                    String ruta = scanner.nextLine().trim();
                    System.out.print("ID del proveedor: ");
                    try {
                        int idProv = Integer.parseInt(scanner.nextLine().trim());
                        importador.importar(ruta, idProv);
                    } catch (NumberFormatException e) {
                        System.out.println("❌ ID de proveedor inválido.");
                    } catch (InsumoExcepcion e) {
                        System.out.println("⚠️ " + e.getMessage());
                    }
                }

                case 2 -> {
                    if (importador.getListaProductosImportados().isEmpty()) {
                        System.out.println("❌ No hay productos en memoria. Ejecute la opción 1 primero.");
                    } else {
                        System.out.println("Productos en memoria: "
                                + importador.getListaProductosImportados().size());
                        importador.guardarProductosEnBD();
                    }
                }

                case 3 -> {
                    System.out.print("Nombre del proveedor: ");
                    String nombre = scanner.nextLine().trim();
                    System.out.print("Columna ID del proveedor (ej: A): ");
                    String colId   = scanner.nextLine().trim().toUpperCase();
                    System.out.print("Columna código de barras (ej: B): ");
                    String colCod  = scanner.nextLine().trim().toUpperCase();
                    System.out.print("Columna descripción (ej: C): ");
                    String colDesc = scanner.nextLine().trim().toUpperCase();
                    System.out.print("Columna importe sin IVA (ej: D): ");
                    String colImp  = scanner.nextLine().trim().toUpperCase();
                    System.out.print("Columna cantidad por presentación (ej: E, Enter si no aplica): ");
                    String colCant = scanner.nextLine().trim().toUpperCase();
                    if (colCant.isEmpty()) colCant = "A";

                    boolean incluyeIva = false;
                    while (true) {
                        System.out.print("¿Incluye IVA en el precio? (S/N): ");
                        String iva = scanner.nextLine().trim().toUpperCase();
                        if (iva.equals("S"))      { incluyeIva = true;  break; }
                        else if (iva.equals("N")) { incluyeIva = false; break; }
                        else System.out.println("❌ Ingrese S o N.");
                    }

                    Proveedor nuevo = new Proveedor(nombre, colId, colCod,
                            colDesc, colImp, colCant, incluyeIva);
                    gestorProv.agregarProveedor(nuevo);
                }

                case 4 -> {
                    if (gestorProv.getListaProveedoresMemoria().isEmpty()) {
                        System.out.println("❌ No hay proveedores en memoria. Use la opción 3 primero.");
                    } else {
                        gestorProv.guardarProveedoresEnBD();
                    }
                }

                case 5 -> {
                    System.out.print("Código de barras: ");
                    String cod = scanner.nextLine().trim();
                    try {
                        consultador.consultarPorCodigo(cod);
                    } catch (InsumoExcepcion e) {
                        System.out.println("⚠️ " + e.getMessage());
                    }
                }

                case 6 -> {
                    System.out.print("Descripción (o parte): ");
                    String desc = scanner.nextLine().trim();
                    try {
                        consultador.consultarPorDescripcion(desc);
                    } catch (InsumoExcepcion e) {
                        System.out.println("⚠️ " + e.getMessage());
                    }
                }

                case 7 -> {
                    System.out.println("Cerrando sesión de "
                            + usuarioLogueado.getUsername() + ". El sistema a sido cerrado correctamente");
                    ejecutando = false;
                }

                default -> System.out.println("❌ Opción inválida.");
            }
        }
        scanner.close();
    }
}
