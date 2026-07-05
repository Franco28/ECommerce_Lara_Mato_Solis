PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS usuarios (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL,
    apellido TEXT NOT NULL,
    email TEXT NOT NULL UNIQUE,
    contrasenia TEXT NOT NULL,
    fecha_alta TEXT NOT NULL,
    estado TEXT NOT NULL,
    rol TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS categorias (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL UNIQUE,
    descripcion TEXT NOT NULL,
    estado TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS productos (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    codigo TEXT NOT NULL UNIQUE,
    nombre TEXT NOT NULL,
    descripcion TEXT NOT NULL,
    precio REAL NOT NULL CHECK (precio > 0),
    categoria_id INTEGER NOT NULL,
    stock INTEGER NOT NULL CHECK (stock >= 0),
    peso REAL NOT NULL DEFAULT 0 CHECK (peso >= 0),
    estado TEXT NOT NULL,
    tipo_producto TEXT NOT NULL,
    porcentaje_impuesto_importacion REAL NOT NULL DEFAULT 0 CHECK (porcentaje_impuesto_importacion >= 0),
    url_descarga TEXT,
    FOREIGN KEY (categoria_id) REFERENCES categorias(id)
);

CREATE TABLE IF NOT EXISTS inventario_movimientos (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    producto_id INTEGER NOT NULL,
    cantidad INTEGER NOT NULL,
    tipo TEXT NOT NULL,
    fecha TEXT NOT NULL,
    observacion TEXT NOT NULL,
    stock_resultante INTEGER NOT NULL DEFAULT 0 CHECK (stock_resultante >= 0),
    FOREIGN KEY (producto_id) REFERENCES productos(id)
);

CREATE TABLE IF NOT EXISTS carritos (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    usuario_id INTEGER NOT NULL,
    fecha_creacion TEXT NOT NULL,
    activo INTEGER NOT NULL DEFAULT 1,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

CREATE TABLE IF NOT EXISTS carrito_items (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    carrito_id INTEGER NOT NULL,
    producto_id INTEGER NOT NULL,
    cantidad INTEGER NOT NULL CHECK (cantidad > 0),
    precio_unitario REAL NOT NULL CHECK (precio_unitario > 0),
    FOREIGN KEY (carrito_id) REFERENCES carritos(id),
    FOREIGN KEY (producto_id) REFERENCES productos(id),
    UNIQUE (carrito_id, producto_id)
);

CREATE TABLE IF NOT EXISTS pagos (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    metodo_pago TEXT NOT NULL,
    monto REAL NOT NULL CHECK (monto > 0),
    estado TEXT NOT NULL,
    fecha TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS envios (
    codigo_seguimiento TEXT PRIMARY KEY,
    direccion TEXT NOT NULL,
    provincia TEXT NOT NULL,
    ciudad TEXT NOT NULL,
    codigo_postal TEXT NOT NULL,
    tipo_envio TEXT NOT NULL,
    estado TEXT NOT NULL,
    costo REAL NOT NULL CHECK (costo >= 0)
);

CREATE TABLE IF NOT EXISTS ordenes (
    numero TEXT PRIMARY KEY,
    cliente_id INTEGER NOT NULL,
    fecha TEXT NOT NULL,
    total REAL NOT NULL DEFAULT 0 CHECK (total >= 0),
    estado TEXT NOT NULL,
    pago_id INTEGER,
    envio_codigo TEXT,
    FOREIGN KEY (cliente_id) REFERENCES usuarios(id),
    FOREIGN KEY (pago_id) REFERENCES pagos(id),
    FOREIGN KEY (envio_codigo) REFERENCES envios(codigo_seguimiento)
);

CREATE TABLE IF NOT EXISTS orden_items (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    orden_numero TEXT NOT NULL,
    producto_id INTEGER NOT NULL,
    cantidad INTEGER NOT NULL CHECK (cantidad > 0),
    precio_unitario REAL NOT NULL CHECK (precio_unitario > 0),
    subtotal REAL NOT NULL CHECK (subtotal >= 0),
    FOREIGN KEY (orden_numero) REFERENCES ordenes(numero),
    FOREIGN KEY (producto_id) REFERENCES productos(id)
);

CREATE TABLE IF NOT EXISTS reclamos (
    numero_reclamo TEXT PRIMARY KEY,
    cliente_id INTEGER NOT NULL,
    orden_numero TEXT NOT NULL,
    motivo TEXT NOT NULL,
    fecha TEXT NOT NULL,
    estado TEXT NOT NULL,
    FOREIGN KEY (cliente_id) REFERENCES usuarios(id),
    FOREIGN KEY (orden_numero) REFERENCES ordenes(numero)
);

CREATE TABLE IF NOT EXISTS devoluciones (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    cliente_id INTEGER NOT NULL,
    producto_id INTEGER NOT NULL,
    motivo TEXT NOT NULL,
    fecha TEXT NOT NULL,
    estado TEXT NOT NULL,
    FOREIGN KEY (cliente_id) REFERENCES usuarios(id),
    FOREIGN KEY (producto_id) REFERENCES productos(id)
);

CREATE TABLE IF NOT EXISTS calificaciones (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    cliente_id INTEGER NOT NULL,
    producto_id INTEGER NOT NULL,
    puntuacion INTEGER NOT NULL CHECK (puntuacion BETWEEN 1 AND 5),
    comentario TEXT NOT NULL,
    fecha TEXT NOT NULL,
    FOREIGN KEY (cliente_id) REFERENCES usuarios(id),
    FOREIGN KEY (producto_id) REFERENCES productos(id)
);

CREATE INDEX IF NOT EXISTS idx_productos_categoria ON productos(categoria_id);
CREATE INDEX IF NOT EXISTS idx_productos_codigo ON productos(codigo);
CREATE INDEX IF NOT EXISTS idx_usuarios_email ON usuarios(email);
CREATE INDEX IF NOT EXISTS idx_ordenes_cliente ON ordenes(cliente_id);
CREATE INDEX IF NOT EXISTS idx_ordenes_estado ON ordenes(estado);
CREATE INDEX IF NOT EXISTS idx_reclamos_estado ON reclamos(estado);
CREATE INDEX IF NOT EXISTS idx_envios_estado ON envios(estado);

CREATE TABLE IF NOT EXISTS envio_historial_estados (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    envio_codigo TEXT NOT NULL,
    estado TEXT NOT NULL,
    fecha TEXT NOT NULL,
    descripcion TEXT NOT NULL,
    FOREIGN KEY (envio_codigo) REFERENCES envios(codigo_seguimiento)
);

CREATE INDEX IF NOT EXISTS idx_envio_historial_codigo ON envio_historial_estados(envio_codigo);
CREATE INDEX IF NOT EXISTS idx_devoluciones_cliente ON devoluciones(cliente_id);
CREATE INDEX IF NOT EXISTS idx_devoluciones_producto ON devoluciones(producto_id);
CREATE INDEX IF NOT EXISTS idx_devoluciones_estado ON devoluciones(estado);
CREATE INDEX IF NOT EXISTS idx_calificaciones_cliente ON calificaciones(cliente_id);
CREATE INDEX IF NOT EXISTS idx_calificaciones_producto ON calificaciones(producto_id);
