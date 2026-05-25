import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InterfazApp extends JFrame {
    
    // Instanciamos el cerebro de tu app
    private LogicaFinanciera logica;

    public InterfazApp() {
        logica = new LogicaFinanciera();

        // 1. Configuración básica de la ventana
        setTitle("Asistente Financiero Preactivo");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centrar en la pantalla
        setLayout(null); // Diseño libre

        // 2. Crear los elementos visuales (Etiquetas, Cajas de texto, Botones)
        JLabel lblTitulo = new JLabel("¡Hola! ¿Qué movimiento harás hoy?");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 14));
        lblTitulo.setBounds(70, 20, 300, 30);
        add(lblTitulo);

        JLabel lblMonto = new JLabel("Monto ($):");
        lblMonto.setBounds(50, 80, 100, 30);
        add(lblMonto);

        JTextField txtMonto = new JTextField();
        txtMonto.setBounds(130, 80, 150, 30);
        add(txtMonto);

        JButton btnIngreso = new JButton("+ Ingreso Diario");
        btnIngreso.setBounds(40, 140, 140, 40);
        btnIngreso.setBackground(new Color(144, 238, 144)); // Verde claro
        add(btnIngreso);

        JButton btnGasto = new JButton("- Gasto Hormiga");
        btnGasto.setBounds(200, 140, 140, 40);
        btnGasto.setBackground(new Color(255, 182, 193)); // Rojo/Rosa claro
        add(btnGasto);

        // 3. Darle vida al botón de "Ingreso Diario"
        btnIngreso.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    double monto = Double.parseDouble(txtMonto.getText());
                    logica.registrarIngresoDiario(monto);
                    JOptionPane.showMessageDialog(null, "¡Se registraron $" + monto + " exitosamente!\n(Revisa la consola para ver a qué cuenta fue)");
                    txtMonto.setText(""); // Limpiar la caja de texto
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Por favor, ingresa un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // 4. Darle vida al botón de "Gasto Hormiga"
        btnGasto.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    double monto = Double.parseDouble(txtMonto.getText());
                    // Parámetros: monto, idCategoria=1 (Comida), idCuenta=1 (Banco A)
                    logica.registrarGastoDiario(monto, 1, 1);
                    JOptionPane.showMessageDialog(null, "¡Descontaste $" + monto + " de tu presupuesto actual!");
                    txtMonto.setText(""); 
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Por favor, ingresa un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    // El método Main exclusivo para arrancar la ventana visual
    public static void main(String[] args) {
        // Aseguramos que la base de datos exista antes de abrir la ventana
        ConexionSQLite.crearTablas();
        
        // Abrimos la ventana
        InterfazApp ventana = new InterfazApp();
        ventana.setVisible(true);
    }
}
