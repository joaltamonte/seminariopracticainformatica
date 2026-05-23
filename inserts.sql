/* inserto 3 usuarios con los 3 roles distintos: adiministrador, vendedor y dueño */
INSERT INTO usuarios ( 
					username, 
					password, 
					rol, 
					nombre, 
					apellido)
VALUES ('ggarcia', 
		'scrypt:32768:8:1$cQJjLDdOXelZ3T1g$e2ed94678844c90773dc4c49f69fc88fadc2757038c2fbf50006595847be5f55e688d507b3adf713261e3e800262a7cb8f953e705c7a82065242e32186782569', 
        'vendedor', 
        'Gladis',
        'Garcia'
),
('jaltamonte', 
		'scrypt:32768:8:1$cQJjLDdOXelZ3T1g$e2ed94678844c90773dc4c49f69fc88fadc2757038c2fbf50006595847be5f55e688d507b3adf713261e3e800262a7cb8f953e705c7a82065242e32186782569', 
        'administrador', 
        'José',
        'Altamonte'
),
('pdiaz', 
		'scrypt:32768:8:1$cQJjLDdOXelZ3T1g$e2ed94678844c90773dc4c49f69fc88fadc2757038c2fbf50006595847be5f55e688d507b3adf713261e3e800262a7cb8f953e705c7a82065242e32186782569', 
        'duenio', 
        'Patricia',
        'Diaz'
);

/* inserto 2 proveedores */
INSERT INTO proveedores (
    nombre_proveedor, 
    columna_id_lista_proveedor, 
    columna_codigo_barras, 
    columna_descripcion_producto, 
    columna_importe_sin_iva, 
    incluye_iva
) 
VALUES ('Proveedor Norte','A','B','C','D',TRUE),
       ('Proveedor Este','D','C','B','A',TRUE),
	   ('Proveedor Sur','B','D','A','C',FALSE);
       
/* Inserto 5 productos a la tabla productos, previo a haber insertado los proveedores sino da error por referencia*/
INSERT INTO productos (
    id_lista_proveedor, 
    codigo_barras, 
    descripcion_producto, 
    importe_sin_iva, 
    cantidad_x_presentacion,
    id_proveedor
) 
VALUES ('a000033','8901057310420','ABROCHADORA KANGARO HP-10 PINZA',6714.23,6,1),
       ('a000583','7795156020011','ACRILICO ACRYLART   60 CC 01 BLANCO TITANIO',1296.52,1,1),
       ('r002875','7795513075852','BOLIGRAFO FILGO THE BLACK JASPER NEGRO/METAL',5801.79,1,2),
       ('f006284','','BOLIGRAFO BIC VERDE',1775,15,2),
	   ('c023750','6972537364648','BOLIGRAFO COLOURS 27043 BORRABLE ESPACIAL RETRACTIL',1754.43,12,2); 

/* inserto la utilidad*/
INSERT INTO utilidad (porcentaje) VALUES (75);

/* confirmo las inserciones */       
COMMIT;
