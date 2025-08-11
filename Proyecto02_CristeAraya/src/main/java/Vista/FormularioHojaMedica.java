package Vista;

import Controlador.AppContext;
import Controlador.AppController;
import Modelo.Doctor;
import Modelo.HojaMedica;
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

public class FormularioHojaMedica extends JDialog {
    private final AppController controller = AppContext.get();

    // Búsqueda por ID
    private JTextField txtBuscarId = new JTextField(10);
    private JButton btnBuscar = new JButton("Buscar");

    // Campos formulario
    private JComboBox<String> cbPaciente = new JComboBox<>();
    private JComboBox<String> cbDoctor   = new JComboBox<>();
    private JDateChooser dateChooser = new JDateChooser();
    private JComboBox<String> comboHora = new JComboBox<>();
    private JComboBox<String> comboMin  = new JComboBox<>();
    private JTextField txtMotivo = new JTextField(20);
    private JTextArea  txtDiag   = new JTextArea(3, 20);
    private JTextArea  txtTrat   = new JTextArea(3, 20);

    private JTable tabla = new JTable(new DefaultTableModel(
            new Object[]{"ID","Paciente","Doctor","Fecha/Hora","Motivo"}, 0));

    private List<Paciente> pacientes;
    private List<Doctor>   doctores;

    public FormularioHojaMedica() {
        super((Frame) null, "Hoja Médica", true);

        setIconImage(new ImageIcon(getClass().getResource("/img/hojamedica.png")).getImage());

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(880, 520);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8,8));

        // Inicializar combo de hora y minutos
        dateChooser.setDateFormatString("yyyy-MM-dd");
        for (int h=0; h<24; h++) comboHora.addItem(String.format("%02d", h));
        for (int m=0; m<60; m+=5) comboMin.addItem(String.format("%02d", m));

        // Panel de búsqueda
        JPanel panelBuscar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBuscar.add(new JLabel("Buscar Hoja Médica por ID:"));
        panelBuscar.add(txtBuscarId);
        panelBuscar.add(btnBuscar);

        // Formulario con GridBagLayout
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(12, 12, 0, 12));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Campos: paciente, doctor, fecha, hora, motivo, diagnóstico, tratamiento
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0; form.add(new JLabel("Paciente:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; form.add(cbPaciente, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0; form.add(new JLabel("Doctor:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; form.add(cbDoctor, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0; form.add(new JLabel("Fecha:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; form.add(dateChooser, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0; form.add(new JLabel("Hora:"), gbc);
        JPanel horaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        horaPanel.add(comboHora); horaPanel.add(new JLabel(":")); horaPanel.add(comboMin);
        gbc.gridx = 1; gbc.weightx = 1; form.add(horaPanel, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0; form.add(new JLabel("Motivo:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; form.add(txtMotivo, gbc);

        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0; gbc.anchor = GridBagConstraints.NORTHWEST;
        form.add(new JLabel("Diagnósticos:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; form.add(new JScrollPane(txtDiag), gbc);

        gbc.gridx = 0; gbc.gridy = 6; gbc.weightx = 0; gbc.anchor = GridBagConstraints.NORTHWEST;
        form.add(new JLabel("Tratamiento:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; form.add(new JScrollPane(txtTrat), gbc);

        // Panel arriba con búsqueda y formulario
        JPanel panelArriba = new JPanel(new BorderLayout());
        panelArriba.add(panelBuscar, BorderLayout.NORTH);
        panelArriba.add(form, BorderLayout.CENTER);

        add(panelArriba, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        // Panel botones abajo
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnGuardar = new JButton("Guardar");
        JButton btnRefrescar = new JButton("Refrescar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnCerrar = new JButton("Cerrar");

        south.add(btnCerrar);
        south.add(btnRefrescar);
        south.add(btnGuardar);
        south.add(btnEliminar);

        add(south, BorderLayout.SOUTH);

        // Listeners
        btnGuardar.addActionListener(e -> onGuardar());
        btnRefrescar.addActionListener(e -> { cargarCombos(); cargarTabla(); });
        btnCerrar.addActionListener(e -> dispose());
        btnEliminar.addActionListener(e -> onEliminar());
        btnBuscar.addActionListener(e -> buscarHojaMedicaPorId());

        // Cargar datos iniciales
        cargarCombos();
        cargarTabla();
    }

    // Buscar hoja médica por ID y mostrar
    private void buscarHojaMedicaPorId() {
        String texto = txtBuscarId.getText().trim();
        if (texto.isEmpty()) {
            cargarTabla(); // mostrar todo si está vacío
            return;
        }
        try {
            int id = Integer.parseInt(texto);
            List<HojaMedica> data = controller.getHojasMedicas();
            DefaultTableModel model = (DefaultTableModel) tabla.getModel();
            model.setRowCount(0);
            boolean encontrado = false;
            for (HojaMedica h : data) {
                if (h.getId() == id) {
                    model.addRow(new Object[]{
                            h.getId(),
                            h.getPaciente().getNombre(),
                            h.getDoctor().getNombre(),
                            h.getFechaHora().toString(),
                            h.getMotivoConsulta()
                    });
                    encontrado = true;
                    break;
                }
            }
            if (!encontrado) {
                JOptionPane.showMessageDialog(this, "No se encontró hoja médica con ID: " + id);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "ID inválido, debe ser un número.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error buscando hoja médica: " + ex.getMessage());
        }
    }

    // Cargar combos de pacientes y doctores
    private void cargarCombos() {
        try {
            cbPaciente.removeAllItems();
            cbDoctor.removeAllItems();
            pacientes = controller.getPacientes();
            doctores  = controller.getDoctores();
            for (Paciente p : pacientes) cbPaciente.addItem(p.getNombre());
            for (Doctor d : doctores)    cbDoctor.addItem(d.getNombre());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error cargando combos: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Guardar hoja médica
    private void onGuardar() {
        try {
            int ip = cbPaciente.getSelectedIndex();
            int id = cbDoctor.getSelectedIndex();
            Date fecha = dateChooser.getDate();
            if (ip == -1 || id == -1 || fecha == null)
                throw new IllegalArgumentException("Complete todos los campos");

            LocalDate ld = fecha.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalTime lt = LocalTime.of(
                    Integer.parseInt(comboHora.getSelectedItem().toString()),
                    Integer.parseInt(comboMin.getSelectedItem().toString())
            );
            LocalDateTime fh = LocalDateTime.of(ld, lt);

            controller.addHojaMedica(
                    fh,
                    txtMotivo.getText().trim(),
                    txtDiag.getText(),
                    txtTrat.getText(),
                    doctores.get(id),
                    pacientes.get(ip)
            );
            JOptionPane.showMessageDialog(this, "Hoja médica guardada");
            cargarTabla();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al guardar: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Cargar tabla con hojas médicas
    private void cargarTabla() {
        try {
            List<HojaMedica> data = controller.getHojasMedicas();
            DefaultTableModel model = (DefaultTableModel) tabla.getModel();
            model.setRowCount(0);
            for (HojaMedica h : data) {
                model.addRow(new Object[]{
                        h.getId(),
                        h.getPaciente().getNombre(),
                        h.getDoctor().getNombre(),
                        h.getFechaHora().toString(),
                        h.getMotivoConsulta()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error cargando hojas médicas: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Eliminar hoja médica seleccionada
    private void onEliminar() {
        int filaSeleccionada = tabla.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una hoja médica para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) tabla.getValueAt(filaSeleccionada, 0);
        int confirmar = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar la hoja médica con ID " + id + "?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION);
        if (confirmar == JOptionPane.YES_OPTION) {
            try {
                controller.eliminarHojaMedica(id);
                JOptionPane.showMessageDialog(this, "Hoja médica eliminada.");
                cargarTabla();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al eliminar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
