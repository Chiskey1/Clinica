package Vista;

import Controlador.AppController;
import Controlador.AppContext;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class FormularioMenuPrincipal extends JFrame {

    // ÚNICO controller para toda la app
    private final AppController controller = new AppController();

    private JPanel panelBotones;
    private JLabel lblMensaje;
    private String[] textos = {
            "1. Paciente", "2. Doctor",
            "3. Enfermero", "4. Cita",
            "5. Vacuna", "6. Hoja Médica",
            "7. Hoja Enfermería", "8. Salir"
    };

    private Runnable[] acciones = {
            () -> new FormularioPaciente().setVisible(true),
            () -> new FormularioDoctor().setVisible(true),
            () -> new FormularioEnfermero().setVisible(true),
            () -> new FormularioCita().setVisible(true),
            () -> new Vista.FormularioVacuna().setVisible(true),
            () -> new Vista.FormularioHojaMedica().setVisible(true),
            () -> new Vista.FormularioHojaEnfermeria().setVisible(true),
            () -> System.exit(0)
    };

    private Color btnColor = new Color(3, 140, 140);   // #038C8C
    private Color btnHover = new Color(4, 157, 217);   // #049DD9
    private Color btnSalir = new Color(220, 53, 69);
    private Color btnSalirHover = new Color(180, 40, 50);

    public FormularioMenuPrincipal() {
        setTitle("Clínica Vida Sana | Menú Principal");
        setSize(800, 650);
        setMinimumSize(new Dimension(400, 500));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Inicializa el contexto de la app (para que las vistas usen el mismo controller)
        AppContext.init(controller);

        ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource("img/logo.png"));
        setIconImage(icon.getImage());

        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(new Color(220, 235, 255));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setContentPane(panelPrincipal);

        // Banner arriba
        ImageIcon bannerIcon = new ImageIcon(getClass().getClassLoader().getResource("img/banner.png"));
        JLabel bannerLabel = new JLabel(bannerIcon);
        bannerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panelPrincipal.add(bannerLabel, BorderLayout.NORTH);

        // Panel central con mensaje y botones verticales
        JPanel panelCentro = new JPanel(new BorderLayout());
        panelCentro.setBackground(new Color(220, 235, 255));
        panelCentro.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        panelPrincipal.add(panelCentro, BorderLayout.CENTER);

        // Mensaje debajo del banner
        lblMensaje = new JLabel("Seleccione la opción que desea realizar", SwingConstants.CENTER);
        lblMensaje.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblMensaje.setForeground(new Color(10, 30, 80));
        lblMensaje.setBorder(BorderFactory.createEmptyBorder(10, 0, 25, 0));
        panelCentro.add(lblMensaje, BorderLayout.NORTH);

        // Panel botones
        panelBotones = new JPanel();
        panelBotones.setBackground(new Color(220, 235, 255));
        panelCentro.add(panelBotones, BorderLayout.CENTER);

        crearBotonesGrid(2);

        // Ajustar columnas según tamaño ventana
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int ancho = getWidth();
                if (ancho < 600) crearBotonesGrid(1);
                else crearBotonesGrid(2);
            }
        });

        // Footer
        JLabel footer = new JLabel("© 2025 Clínica Vida Sana. Todos los derechos reservados.", SwingConstants.CENTER);
        footer.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        footer.setForeground(new Color(70, 70, 70));
        footer.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        panelPrincipal.add(footer, BorderLayout.SOUTH);
    }

    private void crearBotonesGrid(int columnas) {
        panelBotones.removeAll();
        panelBotones.setLayout(new GridLayout(0, columnas, 20, 20));

        Dimension tamBoton = new Dimension(180, 50);

        for (int i = 0; i < textos.length; i++) {
            Color bgNormal = (i == textos.length - 1) ? btnSalir : btnColor;
            Color bgHover = (i == textos.length - 1) ? btnSalirHover : btnHover;
            JButton btn = crearBoton(textos[i], bgNormal, bgHover, acciones[i]);
            btn.setPreferredSize(tamBoton);
            panelBotones.add(btn);
        }

        panelBotones.revalidate();
        panelBotones.repaint();
    }

    private JButton crearBoton(String texto, Color colorNormal, Color colorHover, Runnable accion) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        boton.setForeground(Color.WHITE);
        boton.setBackground(colorNormal);
        boton.setFocusPainted(false);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.setOpaque(true);
        boton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(colorNormal.darker(), 2),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        boton.addActionListener(e -> accion.run());

        boton.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                boton.setBackground(colorHover);
                boton.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(colorHover.darker(), 2),
                        BorderFactory.createEmptyBorder(10, 15, 10, 15)
                ));
            }
            @Override public void mouseExited(MouseEvent e) {
                boton.setBackground(colorNormal);
                boton.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(colorNormal.darker(), 2),
                        BorderFactory.createEmptyBorder(10, 15, 10, 15)
                ));
            }
        });

        return boton;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FormularioMenuPrincipal().setVisible(true));
    }
}


