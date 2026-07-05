# E-Commerce Final - Programación Avanzada

## Etapa 1 - Modelo de dominio

Incluye:

- Enums principales del negocio.
- Interfaces obligatorias y complementarias.
- Clase abstracta `Producto`.
- Subclases `ProductoFisico`, `ProductoDigital` y `ProductoImportado`.
- Entidades principales del ciclo de compra:
  - Usuario
  - Categoria
  - Producto
  - InventarioMovimiento
  - Carrito
  - ItemCarrito
  - OrdenCompra
  - ItemOrden
  - Pago
  - Envio
  - Reclamo
  - Devolucion
  - Calificacion

## Ejecutar / Compilar Proyecto

`$files = Get-ChildItem -Recurse -Filter *.java | ForEach-Object FullName`
`javac -d out $files`
`java -cp out ecommerce.Main`