/*creacion de la base de datos*/

CREATE database insumos_db;

/*selecciono la base de datos*/

USE insumos_db;

/*creo las tablas*/

CREATE TABLE usuarios (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(30) NOT NULL,
    password VARCHAR(162) NOT NULL,
    rol VARCHAR(20),
    nombre VARCHAR(50),
    apellido VARCHAR(50)
);

CREATE TABLE utilidad (
    id INT PRIMARY KEY AUTO_INCREMENT,
    porcentaje DECIMAL(15 , 2 ) NOT NULL,
    fecha_actualizacion DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE proveedores (
    id INT PRIMARY KEY AUTO_INCREMENT,
    fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP,
    nombre_proveedor VARCHAR(50) NOT NULL,
    columna_id_lista_proveedor VARCHAR(2),
    columna_codigo_barras VARCHAR(2),
    columna_descripcion_producto VARCHAR(2),
    columna_importe_sin_iva VARCHAR(2),
    columna_cantidad_x_presentacion VARCHAR(2),
    incluye_iva BOOLEAN
);

CREATE TABLE productos (
    id INT PRIMARY KEY AUTO_INCREMENT,
    fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP,
    id_lista_proveedor VARCHAR(20),
    codigo_barras VARCHAR(30),
    descripcion_producto VARCHAR(100),
    importe_sin_iva DECIMAL(15 , 2 ),
    cantidad_x_presentacion INT,
    id_proveedor INT,
    FOREIGN KEY (id_proveedor)
        REFERENCES proveedores (id)
);

/*muestro las tablas*/
SHOW TABLES;

