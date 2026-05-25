import java.sql.Connection;
import java.sql.Statement;

public class App {
    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println(" INICIANDO ASISTENTE FINANCIERO PREACTIVO");
        System.out.println("=================================================\n");

        // 1. Inicializar la base de datos vacía
        ConexionSQLite.crearTablas();
        prepararEscenarioVacio(); // Limpiamos la base de datos
        
        LogicaFinanciera logica = new LogicaFinanciera();

        // ---------------------------------------------------------
        // SIMULACIÓN 1: EL DÍA QUE SE DESCARGA LA APP (ONBOARDING)
        // ---------------------------------------------------------
        System.out.println("[SIMULACIÓN 1] -> Día de instalación de la app.");
        // Digamos que el usuario tiene $150 en su tarjeta de débito y $300 en ahorros
        logica.configurarOnboardingInicial(150.00, 300.00);

        // ---------------------------------------------------------
        // SIMULACIÓN 2: UN GASTO HORMIGA EN EL PRESENTE
        // ---------------------------------------------------------
        System.out.println("\n[SIMULACIÓN 2] -> Te compraste un café y unas galletas ($5.50).");
        System.out.println("Presionando el botón [-] para registrar el gasto...");
        
        // Parámetros: Monto ($5.50), Categoría (1 = Comida/Colación), Cuenta (1 = Banco A)
        logica.registrarGastoDiario(5.50, 1, 1);

        // ---------------------------------------------------------
        // SIMULACIÓN 3: INGRESO DIARIO (MODO ACUMULADOR O CONTINGENCIA)
        // ---------------------------------------------------------
        System.out.println("\n[SIMULACIÓN 3] -> Recibiste tu ganancia diaria de $20.00.");
        logica.registrarIngresoDiario(20.00);

        System.out.println("\n=================================================");
        System.out.println(" PRUEBAS DEL CEREBRO FINALIZADAS");
        System.out.println("=================================================");
    }

    private static void prepararEscenarioVacio() {
        String limpiarCategorias = "DELETE FROM Categorias_Gasto;";
        String limpiarCuentas = "DELETE FROM Cuentas_Bancarias;";
        
        // Creamos las estructuras básicas vacías
        String insertCuentas = "INSERT INTO Cuentas_Bancarias (id_cuenta, nombre_banco, saldo_real) VALUES " +
                               "(1, 'Banco A - Uso Diario', 0.00), " +
                               "(2, 'Banco B - Ahorros', 0.00);";

        // Dejamos la comida lista para probar (Con $100 de presupuesto para hoy)
        String insertComida = "INSERT INTO Categorias_Gasto (id_categoria, nombre, meta_mensual_requerida, acumulado_futuro, disponible_presente, id_cuenta_asociada) VALUES " +
                              "(1, 'Comida y Colaciones', 150.00, 50.00, 100.00, 1);";

        try (Connection conn = ConexionSQLite.conectar(); Statement stmt = conn.createStatement()) {
            stmt.execute(limpiarCategorias);
            stmt.execute(limpiarCuentas);
            stmt.execute(insertCuentas);
            stmt.execute(insertComida);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}