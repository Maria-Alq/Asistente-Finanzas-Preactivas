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
}