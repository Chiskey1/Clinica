package Vista;

import Controlador.AppContext;
import Controlador.AppController;
import Modelo.Enfermero;
import Modelo.HojaEnfermeria;
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

public class FormularioHojaEnfermeria extends JDialog {
    private final AppController controller = AppContext.get();

    // Controles búsqueda por ID
    private JTextField txtBuscarId = new JTextField(10);
    private JButton btnBuscar = new JButton("Buscar");

    // Campos del formulario
    private JComboBox<String> cbPaciente  = new JComboBox<>();
    private JComboBox<String> cbEnfermero = new JComboBox<>();
    private JDateChooser dateChooser = new JDateChooser();
    private JComboBox<String> comboHora = new JComboBox<>();
    private JComboBox<String> comboMin  = new JComboBox<>();
    private JTextArea  txtSignos    = new JTextArea(3, 20);
    private JTextArea  txtObs       = new JTextArea(3, 20);

    // Tabla para mostrar datos
    private JTable tabla = new JTable(new DefaultTableModel(
            new Object[]{"ID","Paciente","Enfermero","Fecha/Hora"}, 0));

    // Listas auxiliares
    private List<Paciente>  pacientes;
    private List<Enfermero> enfermeros;

    public FormularioHojaEnfermeria() {
        super((Frame) null, "Hoja de Enfermería", true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(820, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8,8));

        // Cargar icono
        ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource("img/hojaenfermeria.png"));
        setIconImage(icon.getImage());

        // Configurar fecha y hora
        dateChooser.setDateFormatString("yyyy-MM-dd");
        for (int h=0; h<24; h++) comboHora.addItem(String.format("%02d", h));
        for (int m=0; m<60; m+=5) comboMin.addItem(String.format("%02d", m));

        // Panel búsqueda arriba
        JPanel panelBuscar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBuscar.add(new JLabel("Buscar Hoja de Enfermería por ID:"));
        panelBuscar.add(txtBuscarId);
        panelBuscar.add(btnBuscar);

        // Formulario con GridBagLayout
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(12, 12, 0, 12));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Fila 0 - Paciente
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0; form.add(new JLabel("Paciente:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; form.add(cbPaciente, gbc);

        // Fila 1 - Enfermero
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0; form.add(new JLabel("Enfermero:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; form.add(cbEnfermero, gbc);

        // Fila 2 - Fecha
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0; form.add(new JLabel("Fecha:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; form.add(dateChooser, gbc);

        // Fila 3 - Hora
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0; form.add(new JLabel("Hora:"), gbc);
        JPanel hora = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        hora.add(comboHora); hora.add(new JLabel(":")); hora.add(comboMin);
        gbc.gridx = 1; gbc.weightx = 1; form.add(hora, gbc);

        // Fila 4 - Signos Vitales
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0; gbc.anchor = GridBagConstraints.NORTHWEST;
        form.add(new JLabel("Signos Vitales:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; form.add(new JScrollPane(txtSignos), gbc);

        // Fila 5 - Observaciones
        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0; gbc.anchor = GridBagConstraints.NORTHWEST;
        form.add(new JLabel("Observaciones:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; form.add(new JScrollPane(txtObs), gbc);

        // Panel arriba con búsqueda + formulario
        JPanel panelArriba = new JPanel(new BorderLayout());
        panelArriba.add(panelBuscar, BorderLayout.NORTH);
        panelArriba.add(form, BorderLayout.CENTER);

        add(panelArriba, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        // Botones abajo a la derecha
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

        // Listeners para botones
        btnGuardar.addActionListener(e -> onGuardar());
        btnRefrescar.addActionListener(e -> { cargarCombos(); cargarTabla(); });
        btnCerrar.addActionListener(e -> dispose());
        btnEliminar.addActionListener(e -> onEliminar());
        btnBuscar.addActionListener(e -> buscarHojaEnfermeriaPorId());

        // Cargar combos y tabla al iniciar
        cargarCombos();
        cargarTabla();
    }

    // Buscar hoja por ID y mostrar en tabla
    private void buscarHojaEnfermeriaPorId() {
        String texto = txtBuscarId.getText().trim();
        if (texto.isEmpty()) {
            cargarTabla(); // Mostrar todo si vacío
            return;
        }
        try {
            int id = Integer.parseInt(texto);
            List<HojaEnfermeria> data = controller.getHojasEnfermeria();
            DefaultTableModel m = (DefaultTableModel) tabla.getModel();
            m.setRowCount(0);
            boolean encontrado = false;
            for (HojaEnfermeria h : data) {
                if (h.getId() == id) {
                    m.addRow(new Object[]{
                            h.getId(),
                            h.getPaciente().getNombre(),
                            h.getEnfermero().getNombre(),
                            h.getFechaHora().toString()
                    });
                    encontrado = true;
                    break;
                }
            }
            if (!encontrado) {
                JOptionPane.showMessageDialog(this, "No se encontró hoja de enfermería con ID: " + id);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "ID inválido, debe ser un número.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error buscando hoja de enfermería: " + ex.getMessage());
        }
    }

    // Cargar datos en combos de pacientes y enfermeros
    private void cargarCombos() {
        try {
            cbPaciente.removeAllItems();
            cbEnfermero.removeAllItems();
            pacientes  = controller.getPacientes();
            enfermeros = controller.getEnfermeros();
            for (Paciente p : pacientes)    cbPaciente.addItem(p.getNombre());
            for (Enfermero e : enfermeros)  cbEnfermero.addItem(e.getNombre());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error cargando combos: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Guardar hoja nueva con datos del formulario
    private void onGuardar() {
        try {
            int ip = cbPaciente.getSelectedIndex();
            int ie = cbEnfermero.getSelectedIndex();
            Date fecha = dateChooser.getDate();
            if (ip == -1 || ie == -1 || fecha == null) throw new IllegalArgumentException("Complete todos los campos");

            LocalDate ld = fecha.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalTime lt = LocalTime.of(
                    Integer.parseInt(comboHora.getSelectedItem().toString()),
                    Integer.parseInt(comboMin.getSelectedItem().toString())
            );
            LocalDateTime fh = LocalDateTime.of(ld, lt);

            controller.addHojaEnfermeria(
                    fh,
                    txtSignos.getText(),
                    txtObs.getText(),
                    enfermeros.get(ie),
                    pacientes.get(ip)
            );
            JOptionPane.showMessageDialog(this, "Hoja de enfermería guardada");
            cargarTabla();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al guardar: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Cargar tabla con datos
    private void cargarTabla() {
        try {
            List<HojaEnfermeria> data = controller.getHojasEnfermeria();
            DefaultTableModel m = (DefaultTableModel) tabla.getModel();
            m.setRowCount(0);
            for (HojaEnfermeria h : data) {
                m.addRow(new Object[]{
                        h.getId(),
                        h.getPaciente().getNombre(),
                        h.getEnfermero().getNombre(),
                        h.getFechaHora().toString()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error cargando hojas de enfermería: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Eliminar hoja seleccionada
    private void onEliminar() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una fila para eliminar", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int id = (int) tabla.getValueAt(fila, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "¿Está seguro de eliminar la hoja de enfermería con ID " + id + "?", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                controller.eliminarHojaEnfermeria(id);
                JOptionPane.showMessageDialog(this, "Hoja de enfermería eliminada");
                cargarTabla();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al eliminar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
