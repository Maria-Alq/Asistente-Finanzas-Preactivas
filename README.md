## Getting Started

Welcome to the VS Code Java world. Here is a guideline to help you get started to write Java code in Visual Studio Code.

## Folder Structure

The workspace contains two folders by default, where:

- `src`: the folder to maintain sources
- `lib`: the folder to maintain dependencies

Meanwhile, the compiled output files will be generated in the `bin` folder by default.

> If you want to customize the folder structure, open `.vscode/settings.json` and update the related settings there.

## Dependency Management

The `JAVA PROJECTS` view allows you to manage your dependencies. More details can be found [here](https://github.com/microsoft/vscode-java-dependency#manage-dependencies).

# Arquitectura del Asistente Financiero Preactivo

Este proyecto es una aplicación móvil nativa diseñada en Java que implementa un modelo de planificación financiera inversa. Utiliza SQLite como motor de base de datos local e independiente.

## Modelo de Datos (Esquema de Tablas SQLite)

1. **Cuentas_Bancarias**: Almacena los saldos físicos reales declarados por el usuario.
   - `id_cuenta` (INTEGER, Clave Primaria)
   - `nombre_banco` (TEXT, Ej: 'Banco A - Uso Diario', 'Banco B - Ahorros')
   - `saldo_real` (REAL, Balance físico actual)

2. **Categorias_Gasto**: Gestiona el presupuesto bajo el "Sistema de Dos Caras".
   - `id_categoria` (INTEGER, Clave Primaria)
   - `nombre` (TEXT, Ej: 'Comida', 'Luz')
   - `meta_mensual_requerida` (REAL, Fondo necesario para el siguiente mes)
   - `acumulado_futuro` (REAL, Dinero congelado para el próximo mes)
   - `disponible_presente` (REAL, Presupuesto activo para el mes en curso)
   - `id_cuenta_asociada` (INTEGER, Llave foránea hacia Cuentas_Bancarias)

3. **Registro_Transacciones**: Historial de auditoría para flujos financieros.
   - `id_transaccion` (INTEGER, Clave Primaria)
   - `tipo` (TEXT, Ej: 'CONTINGENCIA_PRESENTE', 'ACUMULADO_FUTURO')
   - `monto` (REAL)
   - `fecha_registro` (TEXT, Marca de tiempo automática)