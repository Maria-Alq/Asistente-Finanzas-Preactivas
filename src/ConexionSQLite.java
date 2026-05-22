import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexionSQLite {
    // Nombre del archivo de base de datos local
    private static final String URL_BD = "jdbc:sqlite:FinanzasPreactivas.db";

    public static Connection conectar() {
        Connection conexion = null;
        try {
            conexion = DriverManager.getConnection(URL_BD);
        } catch (SQLException e) {
            System.out.println("Error al conectar a SQLite: " + e.getMessage());
        }
        return conexion;
    }

    public static void crearTablas() {
        String tablaCuentas = "CREATE TABLE IF NOT EXISTS Cuentas_Bancarias ("
                + "id_cuenta INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "nombre_banco TEXT NOT NULL,"
                + "saldo_real REAL NOT NULL DEFAULT 0.00);";

        String tablaCategorias = "CREATE TABLE IF NOT EXISTS Categorias_Gasto ("
                + "id_categoria INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "nombre TEXT NOT NULL,"
                + "meta_mensual_requerida REAL NOT NULL,"
                + "acumulado_futuro REAL NOT NULL DEFAULT 0.00,"
                + "disponible_presente REAL NOT NULL DEFAULT 0.00,"
                + "id_cuenta_asociada INTEGER,"
                + "FOREIGN KEY (id_cuenta_asociada) REFERENCES Cuentas_Bancarias(id_cuenta));";

        String tablaTransacciones = "CREATE TABLE IF NOT EXISTS Registro_Transacciones ("
                + "id_transaccion INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "tipo TEXT NOT NULL,"
                + "monto REAL NOT NULL,"
                + "id_categoria INTEGER,"
                + "id_cuenta INTEGER,"
                + "fecha_registro TEXT DEFAULT CURRENT_TIMESTAMP,"
                + "FOREIGN KEY (id_categoria) REFERENCES Categorias_Gasto(id_categoria),"
                + "FOREIGN KEY (id_cuenta) REFERENCES Cuentas_Bancarias(id_cuenta));";

        try (Connection conn = conectar(); Statement stmt = conn.createStatement()) {
            stmt.execute(tablaCuentas);
            stmt.execute(tablaCategorias);
            stmt.execute(tablaTransacciones);
            System.out.println("¡Estructura de base de datos SQLite creada con éxito!");
        } catch (SQLException e) {
            System.out.println("Error al crear las tablas: " + e.getMessage());
        }
    }
}
