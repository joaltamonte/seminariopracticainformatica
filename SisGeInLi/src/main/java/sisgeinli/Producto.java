package sisgeinli;

import java.math.BigDecimal;

public abstract class Producto {
    private String idListaProveedor;
    private String codigoBarras;
    private String descripcionProducto;
    private BigDecimal importeSinIva;
    private int cantidadXPresentacion;
    private int idProveedor;

    public Producto(String idListaProveedor, String codigoBarras, String descripcionProducto, BigDecimal importeSinIva, int cantidadXPresentacion, int idProveedor) {
        this.idListaProveedor = idListaProveedor;
        this.codigoBarras = codigoBarras;
        this.descripcionProducto = descripcionProducto;
        this.importeSinIva = importeSinIva;
        this.cantidadXPresentacion = cantidadXPresentacion;
        this.idProveedor = idProveedor;
    }

    public abstract BigDecimal calcularPrecioFinal(BigDecimal porcentajeUtilidad);

    
    public String getCodigoBarras() { return codigoBarras; }
    public String getDescripcionProducto() { return descripcionProducto; }
    public BigDecimal getImporteSinIva() { return importeSinIva; }
    public void setImporteSinIva(BigDecimal importeSinIva) { this.importeSinIva = importeSinIva; }
    public int getIdProveedor() { return idProveedor; }
    public String getIdListaProveedor() { return idListaProveedor; }
    public int getCantidadXPresentacion() { return cantidadXPresentacion; }
}