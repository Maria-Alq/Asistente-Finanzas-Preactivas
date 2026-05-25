import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LogicaFinanciera {

    public void registrarIngresoDiario(double montoIngresado) {
        String sqlVerificar = "SELECT id_categoria, nombre FROM Categorias_Gasto WHERE disponible_presente <= 0 LIMIT 1";
        
        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement pstmtVerificar = conn.prepareStatement(sqlVerificar);
             ResultSet rs = pstmtVerificar.executeQuery()) {

            if (rs.next()) {
                // 🚨 MODO CONTINGENCIA ACTIVADO: Se desvía el dinero para cubrir el día de hoy
                int idCatFaltante = rs.getInt("id_categoria");
                
                // 1. Sumar al presupuesto actual del mes presente
                String sqlUpdateCat = "UPDATE Categorias_Gasto SET disponible_presente = disponible_presente + ? WHERE id_categoria = ?";
                try (PreparedStatement pstmtUp = conn.prepareStatement(sqlUpdateCat)) {
                    pstmtUp.setDouble(1, montoIngresado);
                    pstmtUp.setInt(2, idCatFaltante);
                    pstmtUp.executeUpdate();
                }
                
                // 2. Sumar físicamente al Banco A (ID 1: Uso diario)
                String sqlUpdateCuenta = "UPDATE Cuentas_Bancarias SET saldo_real = saldo_real + ? WHERE id_cuenta = 1";
                try (PreparedStatement pstmtCue = conn.prepareStatement(sqlUpdateCuenta)) {
                    pstmtCue.setDouble(1, montoIngresado);
                    pstmtCue.executeUpdate();
                }

                System.out.println("🚨 Contingencia activa: Ingreso desviado a cuenta de diario para cubrir faltantes actuales.");
            } else {
                // 🟢 MODO ACUMULADOR NORMAL: Miramos hacia el futuro
                String sqlFuturo = "SELECT id_categoria, id_cuenta_asociada FROM Categorias_Gasto WHERE acumulado_futuro < meta_mensual_requerida LIMIT 1";
                
                try (PreparedStatement pstmtFuturo = conn.prepareStatement(sqlFuturo);
                     ResultSet rsFuturo = pstmtFuturo.executeQuery()) {

                    if (rsFuturo.next()) {
                        int idCatFutura = rsFuturo.getInt("id_categoria");
                        int idCuentaAsociada = rsFuturo.getInt("id_cuenta_asociada");

                        // 1. Guardar con candado para el próximo mes
                        String sqlUpFuturo = "UPDATE Categorias_Gasto SET acumulado_futuro = acumulado_futuro + ? WHERE id_categoria = ?";
                        try (PreparedStatement pstmtUpFut = conn.prepareStatement(sqlUpFuturo)) {
                            pstmtUpFut.setDouble(1, montoIngresado);
                            pstmtUpFut.setInt(2, idCatFutura);
                            pstmtUpFut.executeUpdate();
                        }

                        // 2. Sumar al banco real correspondiente
                        String sqlUpCueAsoc = "UPDATE Cuentas_Bancarias SET saldo_real = saldo_real + ? WHERE id_cuenta = ?";
                        try (PreparedStatement pstmtCueAsoc = conn.prepareStatement(sqlUpCueAsoc)) {
                            pstmtCueAsoc.setDouble(1, montoIngresado);
                            pstmtCueAsoc.setInt(2, idCuentaAsociada);
                            pstmtCueAsoc.executeUpdate();
                        }

                        System.out.println("🟢 Todo al día: Dinero acumulado y protegido para el próximo mes.");
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error en la ejecución del algoritmo: " + e.getMessage());
        }
    }

    // ==========================================
    // NUEVA FUNCIÓN 1: EL ONBOARDING (PASO CERO)
    // ==========================================
    public void configurarOnboardingInicial(double saldoBancoA, double saldoBancoB) {
        String sqlUpdateBanco = "UPDATE Cuentas_Bancarias SET saldo_real = ? WHERE id_cuenta = ?";
        
        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sqlUpdateBanco)) {

            // Guardar saldo del Banco A (Uso Diario - ID 1)
            pstmt.setDouble(1, saldoBancoA);
            pstmt.setInt(2, 1);
            pstmt.executeUpdate();

            // Guardar saldo del Banco B (Ahorros - ID 2)
            pstmt.setDouble(1, saldoBancoB);
            pstmt.setInt(2, 2);
            pstmt.executeUpdate();

            System.out.println("✅ ONBOARDING EXITOSO: Se registraron tus saldos iniciales reales.");
            System.out.println("   -> Banco A (Diario): $" + saldoBancoA);
            System.out.println("   -> Banco B (Ahorros): $" + saldoBancoB);

        } catch (SQLException e) {
            System.out.println("Error en el Onboarding: " + e.getMessage());
        }
    }

    // ==========================================
    // NUEVA FUNCIÓN 2: REGISTRAR GASTO HORMIGA
    // ==========================================
    public void registrarGastoDiario(double montoGastado, int idCategoria, int idCuentaFisica) {
        // 1. Restar del presupuesto disponible del mes presente
        String sqlUpdateCat = "UPDATE Categorias_Gasto SET disponible_presente = disponible_presente - ? WHERE id_categoria = ?";
        // 2. Restar físicamente del banco de donde salió la plata
        String sqlUpdateCuenta = "UPDATE Cuentas_Bancarias SET saldo_real = saldo_real - ? WHERE id_cuenta = ?";
        // 3. Dejar el registro en el historial
        String sqlInsertLog = "INSERT INTO Registro_Transacciones (tipo, monto, id_categoria, id_cuenta) VALUES ('GASTO_DIARIO', ?, ?, ?)";

        try (Connection conn = ConexionSQLite.conectar()) {
            
            // Restar de la Categoría
            try (PreparedStatement pstmtCat = conn.prepareStatement(sqlUpdateCat)) {
                pstmtCat.setDouble(1, montoGastado);
                pstmtCat.setInt(2, idCategoria);
                pstmtCat.executeUpdate();
            }

            // Restar de la Cuenta Bancaria
            try (PreparedStatement pstmtCuenta = conn.prepareStatement(sqlUpdateCuenta)) {
                pstmtCuenta.setDouble(1, montoGastado);
                pstmtCuenta.setInt(2, idCuentaFisica);
                pstmtCuenta.executeUpdate();
            }

            // Registrar Historial
            try (PreparedStatement pstmtLog = conn.prepareStatement(sqlInsertLog)) {
                pstmtLog.setDouble(1, montoGastado);
                pstmtLog.setInt(2, idCategoria);
                pstmtLog.setInt(3, idCuentaFisica);
                pstmtLog.executeUpdate();
            }

            System.out.println("☕ GASTO REGISTRADO: Descontaste $" + montoGastado + " de tu presupuesto actual.");

        } catch (SQLException e) {
            System.out.println("Error registrando el gasto: " + e.getMessage());
        }
    }
}