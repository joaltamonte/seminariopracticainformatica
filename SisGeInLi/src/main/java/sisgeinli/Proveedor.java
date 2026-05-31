package sisgeinli;

public class Proveedor {
    private String nombreProveedor;
    private String columnaIdListaProveedor;
    private String columnaCodigoBarras;
    private String columnaDescripcionProducto;
    private String columnaImporteSinIva;
    private String columnaCantidadXPresentacion;
    private Boolean incluyeIva;

    public Proveedor(String nombreProveedor, String columnaIdListaProveedor, String columnaCodigoBarras, 
                     String columnaDescripcionProducto, String columnaImporteSinIva, String columnaCantidadXPresentacion, Boolean incluyeIva) {
        this.nombreProveedor = nombreProveedor;
        this.columnaIdListaProveedor = columnaIdListaProveedor;
        this.columnaCodigoBarras = columnaCodigoBarras;
        this.columnaDescripcionProducto = columnaDescripcionProducto; // 👈 CORREGIDO ACÁ
        this.columnaImporteSinIva = columnaImporteSinIva;
        this.columnaCantidadXPresentacion = columnaCantidadXPresentacion;
        this.incluyeIva = incluyeIva;
    }

    public String getNombreProveedor() { return nombreProveedor; }
    public String getColumnaIdListaProveedor() { return columnaIdListaProveedor; }
    public String getColumnaCodigoBarras() { return columnaCodigoBarras; }
    public String getcolumnaDescripcionProducto() { return columnaDescripcionProducto; }
    public String getColumnaImporteSinIva() { return columnaImporteSinIva; }
    public String getColumnaCantidadXPresentacion() { return columnaCantidadXPresentacion; }
    public Boolean getIncluyeIva() { return incluyeIva; }
}