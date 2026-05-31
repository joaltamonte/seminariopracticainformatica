package sisgeinli;

import java.math.BigDecimal;
/*
Clase encargada de calcular el precio del producto tomando el margen parametrizado
y aplicandole el 21% del iva
*/
public class GestorMargen extends Producto {
    
    public GestorMargen(String idListaProveedor, String codigoBarras, String descripcionProducto, 
                            BigDecimal importeSinIva, int cantidadXPresentacion, int idProveedor) {
        super(idListaProveedor, codigoBarras, descripcionProducto, importeSinIva, cantidadXPresentacion, idProveedor);
    }

    
    @Override
    public BigDecimal calcularPrecioFinal(BigDecimal porcentajeUtilidad) {
        BigDecimal costo = this.getImporteSinIva();
        BigDecimal margen = costo.multiply(porcentajeUtilidad.divide(new BigDecimal("100")));
        BigDecimal netoConMargen = costo.add(margen);
        // Retorna el precio final aplicando el IVA (21%)
        return netoConMargen.multiply(new BigDecimal("1.21"));
    }
}