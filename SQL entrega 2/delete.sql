/* Apago el modo seguro de mysql para poder borrar */
SET SQL_SAFE_UPDATES = 0;
/* Elimino todos los registros de la tabla prodctos */
DELETE FROM productos;
/* volvemos a prender el modo seguro por las dudas */
SET SQL_SAFE_UPDATES = 1;

/* Apago el modo seguro de mysql para poder borrar */
SET SQL_SAFE_UPDATES = 0;
/* eliminino el producto con el codigo de barras 7795513075852 */
DELETE FROM productos 
WHERE
    codigo_barras = '7795513075852';
/* volvemos a prender el modo seguro por las dudas */
SET SQL_SAFE_UPDATES = 1;