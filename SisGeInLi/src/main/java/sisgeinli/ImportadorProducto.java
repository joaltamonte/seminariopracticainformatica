package sisgeinli;
/*
 * Esta clase se encarga de importar productos desde un archivo Excel (.xlsx).
 *
 * El proceso es:
 * 1. Busca la parametría del proveedor en la base de datos.
 * 2. Determina en qué columnas del Excel están los datos necesarios.
 * 3. Recorre el archivo Excel y genera objetos Producto.
 * 4. Guarda los productos importados en memoria.
 * 5. Permite persistirlos posteriormente en la base de datos.
 *
 * Se decidió leer directamente la estructura interna del archivo XLSX
 * utilizando ZIP y XML para evitar depender de librerías externas.
 */
import java.io.*;
import java.util.*;
import java.util.zip.*;
import java.math.BigDecimal;
import java.util.regex.*;
import java.sql.*;

public class ImportadorProducto {
    /*
     * Lista temporal donde se almacenan los productos importados
     * antes de guardarlos definitivamente en la base de datos.
     */
    private ArrayList<Producto> listaProductosImportados = new ArrayList<>();
    private ArrayList<String> sharedStrings = new ArrayList<>();
    /*
     * Método principal de importación.
     *
     * Recibe la ruta del archivo Excel y el proveedor al que
     * pertenece la lista de precios.
     *
     * Primero obtiene la configuración del proveedor y luego
     * ejecuta la lectura del archivo.
     */
    public void importar(String rutaArchivo, int idProveedor) throws InsumoExcepcion {
        Proveedor proveedor = buscarProveedorEnBD(idProveedor);
        
        int idxId       = letraAIndice(proveedor.getColumnaIdListaProveedor());
        int idxCodigo   = letraAIndice(proveedor.getColumnaCodigoBarras());
        int idxDesc     = letraAIndice(proveedor.getcolumnaDescripcionProducto());
        int idxImporte  = letraAIndice(proveedor.getColumnaImporteSinIva());
        int idxCantidad = letraAIndice(proveedor.getColumnaCantidadXPresentacion());
        boolean incluyeIva = proveedor.getIncluyeIva();

        System.out.println("📋 Parametría del proveedor '" + proveedor.getNombreProveedor() + "':");
        System.out.println("   ID=" + proveedor.getColumnaIdListaProveedor()
                + " | CodBarras=" + proveedor.getColumnaCodigoBarras()
                + " | Desc=" + proveedor.getcolumnaDescripcionProducto()
                + " | Importe=" + proveedor.getColumnaImporteSinIva()
                + " | Cant=" + proveedor.getColumnaCantidadXPresentacion()
                + " | IVA incluido=" + incluyeIva);

        leerXLSX(rutaArchivo, idProveedor, incluyeIva,
                idxId, idxCodigo, idxDesc, idxImporte, idxCantidad);
    }
/*busca el proveedor al cual le voy a cargar una lista de productos, para traer la parametria*/
    private Proveedor buscarProveedorEnBD(int idProveedor) throws InsumoExcepcion {
        String sql = "SELECT nombre_proveedor, columna_id_lista_proveedor, columna_codigo_barras, "
                   + "columna_descripcion_producto, columna_importe_sin_iva, "
                   + "columna_cantidad_x_presentacion, incluye_iva "
                   + "FROM proveedores WHERE id = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idProveedor);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Proveedor(
                        rs.getString("nombre_proveedor"),
                        rs.getString("columna_id_lista_proveedor"),
                        rs.getString("columna_codigo_barras"),
                        rs.getString("columna_descripcion_producto"),
                        rs.getString("columna_importe_sin_iva"),
                        rs.getString("columna_cantidad_x_presentacion"),
                        rs.getBoolean("incluye_iva")
                    );
                } else {
                    throw new InsumoExcepcion("No se encontró el proveedor con ID " + idProveedor
                            + ". Registrelo primero con la opción 3.");
                }
            }
        } catch (SQLException e) {
            throw new InsumoExcepcion("Error al buscar proveedor en BD: " + e.getMessage());
        }
    }
/*
 * Convierte una letra de columna de Excel a un índice numérico.
 *
 * Ejemplos:
 * A -> 0
 * B -> 1
 * Z -> 25
 * AA -> 26
 *
 * Esto permite trabajar internamente con posiciones numéricas.
 */
    private int letraAIndice(String letra) throws InsumoExcepcion {
        if (letra == null || letra.trim().isEmpty()) {
            throw new InsumoExcepcion("Columna no configurada para este proveedor.");
        }
        letra = letra.trim().toUpperCase();
        int resultado = 0;
        for (char c : letra.toCharArray()) {
            if (c < 'A' || c > 'Z') {
                throw new InsumoExcepcion("Letra de columna inválida: '" + letra + "'");
            }
            resultado = resultado * 26 + (c - 'A' + 1);
        }
        return resultado - 1;
    }
/*
 * Recorre el contenido del archivo Excel y genera los objetos Producto.
 *
 * Durante la lectura:
 * - Se ignora la fila de encabezado.
 * - Se validan los datos obligatorios.
 * - Se ajusta el precio si el proveedor informa valores con IVA.
 * - Se registran las filas con error para continuar procesando
 *   el resto del archivo.
 *
 * Los productos válidos se almacenan en memoria para su posterior
 * grabación en la base de datos.
 */
    private void leerXLSX(String rutaArchivo, int idProveedor, boolean incluyeIva,
                           int idxId, int idxCodigo, int idxDesc,
                           int idxImporte, int idxCantidad) throws InsumoExcepcion {
        listaProductosImportados.clear();
        int filasOk = 0;
        int filasError = 0;

        try (ZipFile zip = new ZipFile(rutaArchivo)) {
            cargarSharedStrings(zip);

            ZipEntry hoja = zip.getEntry("xl/worksheets/sheet1.xml");
            if (hoja == null) throw new InsumoExcepcion("No se encontró la hoja de cálculo.");

            String xmlCompleto;
            try (Scanner sc = new Scanner(zip.getInputStream(hoja), "UTF-8").useDelimiter("\\A")) {
                xmlCompleto = sc.hasNext() ? sc.next() : "";
            }

            Matcher matcherRow = Pattern.compile("<row[^>]*>(.*?)</row>", Pattern.DOTALL)
                                        .matcher(xmlCompleto);

            boolean primeraFila = true;
            while (matcherRow.find()) {
                if (primeraFila) { primeraFila = false; continue; }

                Map<Integer, String> celdas = extraerCeldas(matcherRow.group(1));

                try {
                    String idLista = obtenerCelda(celdas, idxId, "ID lista");
                    String codigo  = celdas.getOrDefault(idxCodigo, "");
                    String desc    = obtenerCelda(celdas, idxDesc, "descripción");
                    String impStr  = obtenerCelda(celdas, idxImporte, "importe");

                    double importe = Double.parseDouble(impStr.replace(",", ".").trim());
                    if (incluyeIva) importe = importe / 1.21;

                    int cantidad = 1;
                    if (idxCantidad >= 0 && celdas.containsKey(idxCantidad)) {
                        try { cantidad = Integer.parseInt(celdas.get(idxCantidad).trim()); }
                        catch (NumberFormatException ignored) {}
                    }

                    listaProductosImportados.add(new GestorMargen(
                        idLista, codigo, desc,
                        BigDecimal.valueOf(importe).setScale(2, java.math.RoundingMode.HALF_UP),
                        cantidad, idProveedor
                    ));
                    filasOk++;

                } catch (Exception e) {
                    filasError++;
                }
            }

        } catch (InsumoExcepcion e) {
            throw e;
        } catch (Exception e) {
            throw new InsumoExcepcion("Error al leer el archivo Excel: " + e.getMessage());
        }

        System.out.println("✔ Lectura completada: " + filasOk + " productos en memoria."
                + (filasError > 0 ? " (" + filasError + " filas con error ignoradas)" : ""));
    }

    private Map<Integer, String> extraerCeldas(String filaXml) {
        Map<Integer, String> celdas = new HashMap<>();
        Matcher matcherCell = Pattern.compile("<c\\s+r=\"([A-Z]+)\\d+\"[^>]*>(.*?)</c>", Pattern.DOTALL)
                                     .matcher(filaXml);
        while (matcherCell.find()) {
            String refCol = matcherCell.group(1);
            String contenidoCelda = matcherCell.group(2);
            Matcher matcherV = Pattern.compile("<v>(.*?)</v>").matcher(contenidoCelda);
            if (matcherV.find()) {
                String valor = matcherV.group(1);
                boolean esSharedString = matcherCell.group(0).contains("t=\"s\"");
                if (esSharedString) {
                    try {
                        int idx = Integer.parseInt(valor);
                        if (idx >= 0 && idx < sharedStrings.size()) valor = sharedStrings.get(idx);
                    } catch (NumberFormatException ignored) {}
                }
                try { celdas.put(letraRefAIndice(refCol), valor); }
                catch (Exception ignored) {}
            }
        }
        return celdas;
    }

    private int letraRefAIndice(String letra) {
        int resultado = 0;
        for (char c : letra.toUpperCase().toCharArray()) resultado = resultado * 26 + (c - 'A' + 1);
        return resultado - 1;
    }

    private String obtenerCelda(Map<Integer, String> celdas, int idx, String nombreCampo) throws InsumoExcepcion {
        String valor = celdas.get(idx);
        if (valor == null || valor.trim().isEmpty())
            throw new InsumoExcepcion("Fila sin valor en columna '" + nombreCampo + "'");
        return valor.trim();
    }

    private void cargarSharedStrings(ZipFile zip) throws IOException {
        sharedStrings.clear();
        ZipEntry entry = zip.getEntry("xl/sharedStrings.xml");
        if (entry == null) return;
        String xml;
        try (Scanner sc = new Scanner(zip.getInputStream(entry), "UTF-8").useDelimiter("\\A")) {
            xml = sc.hasNext() ? sc.next() : "";
        }
        Matcher m = Pattern.compile("<t[^>]*>(.*?)</t>").matcher(xml);
        while (m.find()) sharedStrings.add(m.group(1));
    }

    /*
 * Guarda los productos importados en la base de datos.
 *
 * La lógica utilizada es:
 *
 * 1. Si el producto no existe:
 *      -> INSERT
 *
 * 2. Si existe y cambió el precio:
 *      -> UPDATE
 *
 * 3. Si existe y el precio es igual:
 *      -> No se realiza ninguna acción
 *
 * De esta manera se evitan registros duplicados y se actualizan
 * únicamente los productos que realmente tuvieron modificaciones.
 */
    public void guardarProductosEnBD() {
        if (listaProductosImportados.isEmpty()) {
            System.out.println("⚠️ No hay productos en memoria para guardar.");
            return;
        }

        // Busco si ya existe el producto para este proveedor por id_lista_proveedor + id_proveedor
        String sqlSelect = "SELECT id, importe_sin_iva FROM productos "
                         + "WHERE id_lista_proveedor = ? AND id_proveedor = ?";

        String sqlInsert = "INSERT INTO productos "
                         + "(id_lista_proveedor, codigo_barras, descripcion_producto, "
                         + "importe_sin_iva, cantidad_x_presentacion, id_proveedor) "
                         + "VALUES (?, ?, ?, ?, ?, ?)";

        String sqlUpdate = "UPDATE productos SET "
                         + "codigo_barras = ?, descripcion_producto = ?, "
                         + "importe_sin_iva = ?, cantidad_x_presentacion = ? "
                         + "WHERE id_lista_proveedor = ? AND id_proveedor = ?";

        int insertados   = 0;
        int actualizados = 0;
        int ignorados    = 0;

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement psSelect = con.prepareStatement(sqlSelect);
             PreparedStatement psInsert = con.prepareStatement(sqlInsert);
             PreparedStatement psUpdate = con.prepareStatement(sqlUpdate)) {

            for (Producto p : listaProductosImportados) {

                // 1. Busco si existe
                psSelect.setString(1, p.getIdListaProveedor());
                psSelect.setInt(2, p.getIdProveedor());

                try (ResultSet rs = psSelect.executeQuery()) {
                    if (rs.next()) {
                        // Ya existe — comparo el importe
                        BigDecimal importeActual = rs.getBigDecimal("importe_sin_iva").setScale(2, java.math.RoundingMode.HALF_UP);
                        BigDecimal importeNuevo = p.getImporteSinIva().setScale(2, java.math.RoundingMode.HALF_UP);
                        if (importeActual.compareTo(importeNuevo) != 0) {
                            // El importe cambió → UPDATE
                            psUpdate.setString(1, p.getCodigoBarras());
                            psUpdate.setString(2, p.getDescripcionProducto());
                            psUpdate.setBigDecimal(3, p.getImporteSinIva());
                            psUpdate.setInt(4, p.getCantidadXPresentacion());
                            psUpdate.setString(5, p.getIdListaProveedor());
                            psUpdate.setInt(6, p.getIdProveedor());
                            psUpdate.executeUpdate();
                            actualizados++;
                        } else {
                            // El importe es igual → ignoro
                            ignorados++;
                        }
                    } else {
                        // No existe → INSERT
                        psInsert.setString(1, p.getIdListaProveedor());
                        psInsert.setString(2, p.getCodigoBarras());
                        psInsert.setString(3, p.getDescripcionProducto());
                        psInsert.setBigDecimal(4, p.getImporteSinIva());
                        psInsert.setInt(5, p.getCantidadXPresentacion());
                        psInsert.setInt(6, p.getIdProveedor());
                        psInsert.executeUpdate();
                        insertados++;
                    }
                }
            }

            System.out.println("💾 [MySQL] Insertados: " + insertados
                    + " | Actualizados: " + actualizados
                    + " | Sin cambios: " + ignorados);
            listaProductosImportados.clear();

        } catch (SQLException e) {
            System.out.println("❌ Error al guardar en BD: " + e.getMessage());
        }
    }

    public ArrayList<Producto> getListaProductosImportados() {
        return listaProductosImportados;
    }
}
