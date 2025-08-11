package Vista;

import Controlador.AppContext;
import Controlador.AppController;
import Modelo.Paciente;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class FormularioPaciente extends JDialog {
    private final AppController controller = AppContext.get();

    private JTextField txtBuscar = new JTextField(15);
    private JButton btnBuscar = new JButton("Buscar");

    private JTextField txtNombre   = new JTextField(20);
    private JTextField txtTelefono = new JTextField(15);
    private JComboBox<String> cbSexo = new JComboBox<>(new String[]{"M","F","Otro"});
    private JTextField txtDomicilio= new JTextField(20);
    private JTextField txtEmail    = new JTextField(20);

    private JTable tabla = new JTable(new DefaultTableModel(
            new Object[]{"ID","Nombre","Teléfono","Sexo","Domicilio","Email"}, 0));

    public FormularioPaciente() {
        super((Frame) null, "Pacientes", true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(740, 480);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8,8));

        ImageIcon icon = new ImageIcon(getClass().getResource("/img/paciente.png"));
        setIconImage(icon.getImage());

        // Panel para búsqueda (alineado a la izquierda)
        JPanel panelBuscar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        panelBuscar.add(new JLabel("Buscar paciente por nombre o ID:"));
        panelBuscar.add(txtBuscar);
        panelBuscar.add(btnBuscar);

        // Panel formulario igual que tú
        JPanel form = new JPanel(new GridLayout(5,2,8,8));
        form.setBorder(BorderFactory.createEmptyBorder(12,12,0,12));
        form.add(new JLabel("Nombre:"));   form.add(txtNombre);
        form.add(new JLabel("Teléfono:")); form.add(txtTelefono);
        form.add(new JLabel("Sexo:"));     form.add(cbSexo);
        form.add(new JLabel("Domicilio:"));form.add(txtDomicilio);
        form.add(new JLabel("Email:"));    form.add(txtEmail);

        // Panel vertical para contener búsqueda + formulario
        JPanel panelNorte = new JPanel();
        panelNorte.setLayout(new BoxLayout(panelNorte, BoxLayout.Y_AXIS));
        panelNorte.add(panelBuscar);
        panelNorte.add(form);

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnGuardar = new JButton("Guardar");
        JButton btnRefrescar = new JButton("Refrescar");
        JButton btnCerrar = new JButton("Cerrar");
        south.add(btnCerrar);
        south.add(btnRefrescar);
        south.add(btnGuardar);
        south.add(btnEliminar);

        // Ahora ponemos el panelNorte en NORTH
        add(panelNorte, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);

        // Listeners
        btnBuscar.addActionListener(e -> onBuscar());
        btnGuardar.addActionListener(e -> onGuardar());
        btnRefrescar.addActionListener(e -> cargarTabla());
        btnCerrar.addActionListener(e -> dispose());
        btnEliminar.addActionListener(e -> onEliminar());

        cargarTabla();
    }

    private void onBuscar() {
        String texto = txtBuscar.getText().trim();
        DefaultTableModel modelo = (DefaultTableModel) tabla.getModel();
        modelo.setRowCount(0);

        if (texto.isEmpty()) {
            cargarTabla();
            return;
        }

        try {
            // Buscar por ID si es número
            try {
                int id = Integer.parseInt(texto);
                Paciente p = controller.findPacienteById(id);
                if (p != null) {
                    modelo.addRow(new Object[]{p.getId(), p.getNombre(), p.getTelefono(), p.getSexo(), p.getDomicilio(), p.getEmail()});
                } else {
                    JOptionPane.showMessageDialog(this, "No se encontró paciente con ID: " + id);
                }
            } catch (NumberFormatException ex) {
                // Si no es número, buscar por nombre parcial
                List<Paciente> lista = controller.getPacientes();
                boolean encontrado = false;
                for (Paciente p : lista) {
                    if (p.getNombre().toLowerCase().contains(texto.toLowerCase())) {
                        modelo.addRow(new Object[]{p.getId(), p.getNombre(), p.getTelefono(), p.getSexo(), p.getDomicilio(), p.getEmail()});
                        encontrado = true;
                    }
                }
                if (!encontrado) {
                    JOptionPane.showMessageDialog(this, "No se encontró paciente con nombre que contenga: " + texto);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error en búsqueda: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onGuardar() {
        try {
            if (txtNombre.getText().trim().isEmpty()) throw new IllegalArgumentException("Nombre obligatorio");
            if (!txtEmail.getText().contains("@")) throw new IllegalArgumentException("Email inválido");

            controller.addPaciente(
                    txtNombre.getText().trim(),
                    txtTelefono.getText().trim(),
                    (String) cbSexo.getSelectedItem(),
                    txtDomicilio.getText().trim(),
                    txtEmail.getText().trim()
            );
            JOptionPane.showMessageDialog(this, "Paciente guardado");
            limpiar();
            cargarTabla();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiar() {
        txtNombre.setText("");
        txtTelefono.setText("");
        cbSexo.setSelectedIndex(0);
        txtDomicilio.setText("");
        txtEmail.setText("");
    }

    private void cargarTabla() {
        try {
            List<Paciente> data = controller.getPacientes();
            DefaultTableModel m = (DefaultTableModel) tabla.getModel();
            m.setRowCount(0);
            for (Paciente p : data) {
                m.addRow(new Object[]{ p.getId(), p.getNombre(), p.getTelefono(), p.getSexo(), p.getDomicilio(), p.getEmail() });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error cargando pacientes: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onEliminar() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un paciente para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int idPaciente = (int) tabla.getValueAt(fila, 0);

        int resp = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar el paciente seleccionado?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION);

        if (resp == JOptionPane.YES_OPTION) {
            try {
                controller.eliminarPaciente(idPaciente);
                JOptionPane.showMessageDialog(this, "Paciente eliminado correctamente.");
                cargarTabla();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error eliminando paciente: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
