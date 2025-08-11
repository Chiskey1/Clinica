package Vista;

import Controlador.AppContext;
import Controlador.AppController;
import Modelo.Doctor;
import Modelo.Paciente;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class FormularioCita extends JDialog {
    private final AppController controller = AppContext.get();

    // Controles búsqueda
    private JTextField txtBuscar = new JTextField(15);
    private JButton btnBuscar = new JButton("Buscar");

    // Controles formulario cita
    private JComboBox<String> comboPacientes;
    private JComboBox<String> comboDoctores;
    private JDateChooser dateChooser;
    private JComboBox<String> comboHora;
    private JComboBox<String> comboMinutos;

    // Botones principales
    private JButton btnGuardar;
    private JButton btnRefrescar;
    private JButton btnEliminar;
    private JButton btnCerrar;

    private JTable tabla;
    private DefaultTableModel modeloTabla;

    private List<Paciente> pacientes;
    private List<Doctor> doctores;

    public FormularioCita() {
        super((Frame) null, "Registrar Cita", true);
        setSize(650, 480);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));

        ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource("img/citamedica.png"));
        setIconImage(icon.getImage());

        // Panel búsqueda
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBusqueda.add(new JLabel("Buscar Paciente por nombre:"));
        panelBusqueda.add(txtBuscar);
        panelBusqueda.add(btnBuscar);

        // Panel formulario
        JPanel panelFormulario = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        comboPacientes = new JComboBox<>();
        comboDoctores = new JComboBox<>();
        dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("yyyy-MM-dd");

        comboHora = new JComboBox<>();
        comboMinutos = new JComboBox<>();
        for (int h = 0; h < 24; h++) comboHora.addItem(String.format("%02d", h));
        for (int m = 0; m < 60; m += 5) comboMinutos.addItem(String.format("%02d", m));

        btnGuardar = new JButton("Guardar");
        btnRefrescar = new JButton("Refrescar");
        btnEliminar = new JButton("Eliminar");
        btnCerrar = new JButton("Cerrar");

        // Carga datos pacientes y doctores
        try {
            pacientes = controller.getPacientes();
            doctores = controller.getDoctores();
            for (Paciente p : pacientes) comboPacientes.addItem(p.getNombre());
            for (Doctor d : doctores) comboDoctores.addItem(d.getNombre());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error cargando datos: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

        int fila = 0;
        agregarCampo(panelFormulario, gbc, fila++, "Paciente:", comboPacientes);
        agregarCampo(panelFormulario, gbc, fila++, "Doctor:", comboDoctores);
        agregarCampo(panelFormulario, gbc, fila++, "Fecha:", dateChooser);

        JPanel panelHora = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panelHora.add(comboHora);
        panelHora.add(new JLabel(":"));
        panelHora.add(comboMinutos);
        agregarCampo(panelFormulario, gbc, fila++, "Hora:", panelHora);

        // Panel arriba con búsqueda y formulario
        JPanel panelArriba = new JPanel(new BorderLayout());
        panelArriba.add(panelBusqueda, BorderLayout.NORTH);
        panelArriba.add(panelFormulario, BorderLayout.CENTER);

        add(panelArriba, BorderLayout.NORTH);

        // Tabla en centro
        modeloTabla = new DefaultTableModel(
                new Object[]{"ID", "Paciente", "Doctor", "Fecha y Hora"}, 0);
        tabla = new JTable(modeloTabla);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        // Panel botones abajo derecha
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        panelBotones.add(btnRefrescar);
        panelBotones.add(btnGuardar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnCerrar);

        add(panelBotones, BorderLayout.SOUTH);

        // Eventos
        btnBuscar.addActionListener(e -> onBuscar());
        btnGuardar.addActionListener(e -> onGuardar());
        btnRefrescar.addActionListener(e -> cargarTabla());
        btnEliminar.addActionListener(e -> onEliminar());
        btnCerrar.addActionListener(e -> dispose());

        cargarTabla();
    }

    // Buscar cita por nombre paciente
    private void onBuscar() {
        String textoBusqueda = txtBuscar.getText().trim().toLowerCase();
        if (textoBusqueda.isEmpty()) {
            cargarTabla(); // Muestra todo si vacío
            return;
        }

        try {
            List<?> citas = controller.getCitas();
            modeloTabla.setRowCount(0);
            for (Object obj : citas) {
                Modelo.Cita cita = (Modelo.Cita) obj;
                String nombrePaciente = cita.getPaciente().getNombre().toLowerCase();
                if (nombrePaciente.contains(textoBusqueda)) {
                    modeloTabla.addRow(new Object[]{
                            cita.getId(),
                            cita.getPaciente().getNombre(),
                            cita.getDoctor().getNombre(),
                            cita.getFechaHora().toString()
                    });
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error en búsqueda: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Guardar nueva cita
    private void onGuardar() {
        int indexP = comboPacientes.getSelectedIndex();
        int indexD = comboDoctores.getSelectedIndex();
        Date fecha = dateChooser.getDate();

        if (indexP == -1 || indexD == -1 || fecha == null) {
            JOptionPane.showMessageDialog(this, "Complete todos los campos.");
            return;
        }

        try {
            LocalDate ld = fecha.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            int h = Integer.parseInt(comboHora.getSelectedItem().toString());
            int m = Integer.parseInt(comboMinutos.getSelectedItem().toString());
            LocalTime lt = LocalTime.of(h, m);
            LocalDateTime fh = LocalDateTime.of(ld, lt);

            Paciente paciente = pacientes.get(indexP);
            Doctor doctor = doctores.get(indexD);

            controller.addCita(paciente, doctor, fh);
            JOptionPane.showMessageDialog(this, "Cita registrada.");
            cargarTabla();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al guardar: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Eliminar cita seleccionada
    private void onEliminar() {
        int filaSeleccionada = tabla.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una cita para eliminar.");
            return;
        }

        int idCita = (int) modeloTabla.getValueAt(filaSeleccionada, 0);

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro que desea eliminar la cita seleccionada?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                controller.eliminarCita(idCita);
                JOptionPane.showMessageDialog(this, "Cita eliminada.");
                cargarTabla();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al eliminar cita: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Cargar todas las citas en la tabla
    private void cargarTabla() {
        try {
            List<?> citas = controller.getCitas();
            modeloTabla.setRowCount(0);
            for (Object obj : citas) {
                Modelo.Cita cita = (Modelo.Cita) obj;

                modeloTabla.addRow(new Object[]{
                        cita.getId(),
                        cita.getPaciente().getNombre(),
                        cita.getDoctor().getNombre(),
                        cita.getFechaHora().toString()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error cargando citas: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Agrega etiqueta y campo al formulario
    private void agregarCampo(JPanel panel, GridBagConstraints gbc, int fila, String etiqueta, JComponent campo) {
        gbc.gridx = 0;
        gbc.gridy = fila;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel(etiqueta), gbc);
        gbc.gridx = 1;
        gbc.gridy = fila;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(campo, gbc);
    }
}
