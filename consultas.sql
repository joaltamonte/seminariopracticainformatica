/* consulta con tablas relacionadas */ 
SELECT PROD.id_lista_proveedor AS 'Id de la lista del proveedor', 
    PROD.codigo_barras AS 'Codigo de barras', 
    PROD.descripcion_producto AS 'Descripcion del producto', 
    PROD.importe_sin_iva AS 'Importe sin iva', 
    PROD.cantidad_x_presentacion AS 'Cantidad por presentacion',
    PROV.nombre_proveedor AS 'Nombre del proveedor' 
    FROM productos PROD,
    proveedores PROV
    WHERE PROD.id_proveedor = PROV.id;
    
/* consulta con tabla relacionada con condición de que el código de barras sea '8901057310420' */
SELECT PROD.id_lista_proveedor AS 'Id de la lista del proveedor', 
    PROD.codigo_barras AS 'Codigo de barras', 
    PROD.descripcion_producto AS 'Descripcion del producto', 
    PROD.importe_sin_iva AS 'Importe sin iva', 
    PROD.cantidad_x_presentacion AS 'Cantidad por presentacion',
    PROV.nombre_proveedor AS 'Nombre del proveedor'
FROM 
    productos PROD,
    proveedores PROV
WHERE PROD.id_proveedor = PROV.id AND
	PROD.codigo_barras = '8901057310420'; 

/* consulta con tabla relacionada con condición del que la descripcion del producto contenga la palabra boligrafo */
SELECT 
    PROD.id_lista_proveedor AS 'Id de la lista del proveedor',
    PROD.codigo_barras AS 'Codigo de barras',
    PROD.descripcion_producto AS 'Descripcion del producto',
    PROD.importe_sin_iva AS 'Importe sin iva',
    PROD.cantidad_x_presentacion AS 'Cantidad por presentacion',
    PROV.nombre_proveedor AS 'Nombre del proveedor'
FROM
    productos PROD,
    proveedores PROV
WHERE
    PROD.id_proveedor = PROV.id
        AND PROD.descripcion_producto LIKE '%boligrafo%'; 

/*consulto los usuarios por tipo de rol vendedor */
SELECT 
    username
FROM
    usuarios
WHERE
    rol = 'vendedor';

/* consultas completas a tablas */
SELECT * FROM usuarios;

SELECT * FROM productos;

SELECT * FROM proveedores;

SELECT * FROM utilidad;

