package Vista;

import Controlador.AppContext;
import Controlador.AppController;
import Modelo.Doctor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class FormularioDoctor extends JDialog {
    private final AppController controller = AppContext.get();

    private JTextField txtBuscarId = new JTextField(10);
    private JButton btnBuscar = new JButton("Buscar");

    private JTextField txtNombre       = new JTextField(20);
    private JTextField txtTelefono     = new JTextField(15);
    private JComboBox<String> cbSexo   = new JComboBox<>(new String[]{"M","F","Otro"});
    private JTextField txtDomicilio    = new JTextField(20);
    private JTextField txtEmail        = new JTextField(20);
    private JTextField txtEspecialidad = new JTextField(20);

    private JTable tabla;
    private DefaultTableModel modeloTabla;

    public FormularioDoctor() {
        super((Frame) null, "Doctores", true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(850, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8,8));

        ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource("img/doctor.png"));
        setIconImage(icon.getImage());

        // Panel búsqueda arriba
        JPanel panelBuscar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBuscar.add(new JLabel("Buscar Doctor por ID:"));
        panelBuscar.add(txtBuscarId);
        panelBuscar.add(btnBuscar);

        // Formulario campos
        JPanel form = new JPanel(new GridLayout(6,2,8,8));
        form.setBorder(BorderFactory.createEmptyBorder(12,12,0,12));
        form.add(new JLabel("Nombre:"));        form.add(txtNombre);
        form.add(new JLabel("Teléfono:"));      form.add(txtTelefono);
        form.add(new JLabel("Sexo:"));          form.add(cbSexo);
        form.add(new JLabel("Domicilio:"));     form.add(txtDomicilio);
        form.add(new JLabel("Email:"));         form.add(txtEmail);
        form.add(new JLabel("Especialidad:"));  form.add(txtEspecialidad);

        // Panel arriba contenedor
        JPanel panelArriba = new JPanel(new BorderLayout());
        panelArriba.add(panelBuscar, BorderLayout.NORTH);
        panelArriba.add(form, BorderLayout.CENTER);

        add(panelArriba, BorderLayout.NORTH);

        // Tabla datos
        modeloTabla = new DefaultTableModel(
                new Object[]{"ID","Nombre","Teléfono","Sexo","Domicilio","Email","Especialidad"}, 0);
        tabla = new JTable(modeloTabla);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        // Panel botones abajo
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnCerrar    = new JButton("Cerrar");
        JButton btnRefrescar = new JButton("Refrescar");
        JButton btnGuardar   = new JButton("Guardar");
        JButton btnEliminar  = new JButton("Eliminar");

        south.add(btnCerrar);
        south.add(btnRefrescar);
        south.add(btnGuardar);
        south.add(btnEliminar);
        add(south, BorderLayout.SOUTH);

        // Listeners
        btnGuardar.addActionListener(e -> onGuardar());
        btnRefrescar.addActionListener(e -> cargarTabla());
        btnCerrar.addActionListener(e -> dispose());
        btnEliminar.addActionListener(e -> onEliminar());
        btnBuscar.addActionListener(e -> buscarDoctorPorId());

        cargarTabla();
    }

    // Buscar doctor por ID
    private void buscarDoctorPorId() {
        String texto = txtBuscarId.getText().trim();
        if (texto.isEmpty()) {
            cargarTabla(); // Mostrar todos si vacío
            return;
        }
        try {
            int id = Integer.parseInt(texto);
            Doctor d = controller.findDoctorById(id);
            modeloTabla.setRowCount(0);
            if (d != null) {
                modeloTabla.addRow(new Object[]{
                        d.getId(), d.getNombre(), d.getTelefono(), d.getSexo(),
                        d.getDomicilio(), d.getEmail(), d.getEspecialidad()
                });
            } else {
                JOptionPane.showMessageDialog(this, "No se encontró doctor con ID: " + id);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "ID inválido, debe ser un número.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error buscando doctor: " + ex.getMessage());
        }
    }

    // Guardar doctor nuevo o existente
    private void onGuardar() {
        try {
            controller.addDoctor(
                    txtNombre.getText().trim(),
                    txtTelefono.getText().trim(),
                    (String) cbSexo.getSelectedItem(),
                    txtDomicilio.getText().trim(),
                    txtEmail.getText().trim(),
                    txtEspecialidad.getText().trim()
            );
            JOptionPane.showMessageDialog(this, "Doctor guardado");
            limpiar();
            cargarTabla();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Limpiar campos formulario
    private void limpiar() {
        txtBuscarId.setText("");
        txtNombre.setText("");
        txtTelefono.setText("");
        cbSexo.setSelectedIndex(0);
        txtDomicilio.setText("");
        txtEmail.setText("");
        txtEspecialidad.setText("");
    }

    // Cargar tabla con doctores
    private void cargarTabla() {
        try {
            List<Doctor> data = controller.getDoctores();
            modeloTabla.setRowCount(0);
            for (Doctor d : data) {
                modeloTabla.addRow(new Object[]{
                        d.getId(), d.getNombre(), d.getTelefono(), d.getSexo(),
                        d.getDomicilio(), d.getEmail(), d.getEspecialidad()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error cargando doctores: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Eliminar doctor seleccionado
    private void onEliminar() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un doctor para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) tabla.getValueAt(fila, 0);
        String nombre = (String) tabla.getValueAt(fila, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar al doctor: " + nombre + " ?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                controller.eliminarDoctor(id);
                JOptionPane.showMessageDialog(this, "Doctor eliminado correctamente.");
                cargarTabla();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error eliminando doctor: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
