use prestamos;

create table clientes(
	dni varchar(18),
    nombre varchar(30),
    apellido varchar(30),
    telefono varchar(20),
    correo varchar(100),
    sueldo decimal(14,2),
    primary key(dni)
    );
    
create table direcciones(
	idDireccion int auto_increment primary key,
	pais varchar(50),
    departamento varchar(50),
    ciudad varchar(50),
    colonia varchar(50),
    referencia varchar(100),
    dni varchar(18),
    foreign key(dni) references cliente(dni)
    );
    
create table prestamos(
	idPrestamo int auto_increment primary key,
    monto decimal(14,2),
    plazo int,
    tasa_interes decimal(14,2),
    cuota decimal(14,2),
    estado char(1),
    tipo_prestamo char(1)
	);
    
create table cliente_prestamos(
	dni varchar(18),
    idPrestamo int,
    foreign key(dni) references cliente(dni),
    foreign key(idPrestamo) references prestamos(idPrestamo)
	);
    
create table amortizacion(
	idPrestamo int,
	numeroCuota int,
    interes decimal(14,2),
    capital decimal(14,2),
    saldo decimal(14,2),
    estado char(1),
    fechaVencimiento date,
    primary key(idPrestamo, numeroCuota),
    foreign key(idPrestamo) references prestamos(idPrestamo)
	);
    
select *from clientes;
select *from direcciones;
select *from prestamos;
select *from amortizacion;
select *from cliente_prestamos;
    