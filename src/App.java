import java.sql.Connection;
import java.sql.Statement;

public class App {
    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println(" INICIANDO ASISTENTE FINANCIERO PREACTIVO");
        System.out.println("=================================================\n");

        // 1. Inicializar el archivo de base de datos y crear las tablas
        ConexionSQLite.crearTablas();

        // 2. Configurar el escenario de prueba (Simulamos un mes con problemas)
        prepararEscenarioDePrueba();

        // 3. Instanciar el cerebro de nuestra app
        LogicaFinanciera logica = new LogicaFinanciera();

        System.out.println("\n[SIMULACIÓN] -> Ganaste $20.00 en tu trabajo diario.");
        System.out.println("Presionando el botón [+] para registrar el ingreso...\n");

        // 4. Ejecutar el registro
        logica.registrarIngresoDiario(20.00);

        System.out.println("\n=================================================");
        System.out.println(" PRUEBA FINALIZADA");
        System.out.println("=================================================");
    }

    /**
     * Este método inyecta datos falsos a la base de datos para simular
     * que el usuario se quedó con $0.00 en la comida del mes presente.
     */
    private static void prepararEscenarioDePrueba() {
        // Consultas SQL para limpiar pruebas anteriores y crear datos nuevos
        String limpiarCategorias = "DELETE FROM Categorias_Gasto;";
        String limpiarCuentas = "DELETE FROM Cuentas_Bancarias;";

        // Insertamos dos cuentas bancarias reales
        String insertCuentas = "INSERT INTO Cuentas_Bancarias (id_cuenta, nombre_banco, saldo_real) VALUES " +
                               "(1, 'Banco A - Uso Diario', 50.00), " +
                               "(2, 'Banco B - Ahorros', 100.00);";

        // CASO CRÍTICO: La comida del presente está en 0.00 (Falta dinero hoy)
        String insertComida = "INSERT INTO Categorias_Gasto (id_categoria, nombre, meta_mensual_requerida, acumulado_futuro, disponible_presente, id_cuenta_asociada) VALUES " +
                              "(1, 'Comida', 150.00, 50.00, 0.00, 1);";

        // CASO NORMAL: La luz está al día en el presente
        String insertLuz = "INSERT INTO Categorias_Gasto (id_categoria, nombre, meta_mensual_requerida, acumulado_futuro, disponible_presente, id_cuenta_asociada) VALUES " +
                           "(2, 'Luz', 40.00, 10.00, 40.00, 1);";

        try (Connection conn = ConexionSQLite.conectar();
             Statement stmt = conn.createStatement()) {
            
            stmt.execute(limpiarCategorias);
            stmt.execute(limpiarCuentas);
            stmt.execute(insertCuentas);
            stmt.execute(insertComida);
            stmt.execute(insertLuz);
            
            System.out.println(">> Escenario configurado: Tu presupuesto de 'Comida' actual está en $0.00.");
            
        } catch (Exception e) {
            System.out.println("Error configurando la prueba: " + e.getMessage());
        }
    }
}
