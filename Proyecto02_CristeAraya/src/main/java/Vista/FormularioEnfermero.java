package Vista;

import Controlador.AppContext;
import Controlador.AppController;
import Modelo.Enfermero;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class FormularioEnfermero extends JDialog {
    private final AppController controller = AppContext.get();

    private JTextField txtNombre   = new JTextField(20);
    private JTextField txtTelefono = new JTextField(15);
    private JComboBox<String> cbSexo = new JComboBox<>(new String[]{"M","F","Otro"});
    private JTextField txtDomicilio= new JTextField(20);
    private JTextField txtEmail    = new JTextField(20);

    private JTextField txtBuscarId = new JTextField(10);
    private JButton btnBuscar = new JButton("Buscar");

    private JTable tabla = new JTable(new DefaultTableModel(
            new Object[]{"ID","Nombre","Teléfono","Sexo","Domicilio","Email"}, 0));

    public FormularioEnfermero() {
        super((Frame) null, "Enfermeros", true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(740, 480);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8,8));

        ImageIcon icon = new ImageIcon(getClass().getResource("/img/enfermero.png"));
        setIconImage(icon.getImage());

        // Panel búsqueda arriba
        JPanel panelBuscar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBuscar.add(new JLabel("Buscar ID:"));
        panelBuscar.add(txtBuscarId);
        panelBuscar.add(btnBuscar);

        // Formulario campos
        JPanel form = new JPanel(new GridLayout(5,2,8,8));
        form.setBorder(BorderFactory.createEmptyBorder(12,12,0,12));
        form.add(new JLabel("Nombre:"));    form.add(txtNombre);
        form.add(new JLabel("Teléfono:"));  form.add(txtTelefono);
        form.add(new JLabel("Sexo:"));      form.add(cbSexo);
        form.add(new JLabel("Domicilio:")); form.add(txtDomicilio);
        form.add(new JLabel("Email:"));     form.add(txtEmail);

        // Panel arriba contenedor
        JPanel panelArriba = new JPanel(new BorderLayout());
        panelArriba.add(panelBuscar, BorderLayout.NORTH);
        panelArriba.add(form, BorderLayout.CENTER);

        add(panelArriba, BorderLayout.NORTH);

        // Tabla con scroll en el centro
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        // Panel botones abajo
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnCerrar = new JButton("Cerrar");
        JButton btnRefrescar = new JButton("Refrescar");
        JButton btnGuardar = new JButton("Guardar");
        JButton btnEliminar = new JButton("Eliminar");

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
        btnBuscar.addActionListener(e -> buscarEnfermeroPorId());

        cargarTabla();
    }

    // Buscar enfermero por ID
    private void buscarEnfermeroPorId() {
        String texto = txtBuscarId.getText().trim();
        if (texto.isEmpty()) {
            cargarTabla(); // Mostrar todos si vacío
            return;
        }
        try {
            int id = Integer.parseInt(texto);
            Enfermero e = controller.findEnfermeroById(id);
            DefaultTableModel model = (DefaultTableModel) tabla.getModel();
            model.setRowCount(0);
            if (e != null) {
                model.addRow(new Object[]{
                        e.getId(), e.getNombre(), e.getTelefono(), e.getSexo(), e.getDomicilio(), e.getEmail()
                });
            } else {
                JOptionPane.showMessageDialog(this, "No se encontró enfermero con ID: " + id);
                // Tabla queda vacía
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "ID inválido, debe ser un número.");
        }
    }

    // Guardar nuevo enfermero
    private void onGuardar() {
        try {
            controller.addEnfermero(
                    txtNombre.getText().trim(),
                    txtTelefono.getText().trim(),
                    (String) cbSexo.getSelectedItem(),
                    txtDomicilio.getText().trim(),
                    txtEmail.getText().trim()
            );
            JOptionPane.showMessageDialog(this, "Enfermero guardado");
            limpiar();
            cargarTabla();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Limpiar campos formulario
    private void limpiar() {
        txtNombre.setText("");
        txtTelefono.setText("");
        cbSexo.setSelectedIndex(0);
        txtDomicilio.setText("");
        txtEmail.setText("");
        txtBuscarId.setText("");
    }

    // Cargar datos en tabla
    private void cargarTabla() {
        try {
            List<Enfermero> data = controller.getEnfermeros();
            DefaultTableModel m = (DefaultTableModel) tabla.getModel();
            m.setRowCount(0);
            for (Enfermero e : data) {
                m.addRow(new Object[]{
                        e.getId(), e.getNombre(), e.getTelefono(), e.getSexo(), e.getDomicilio(), e.getEmail()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error cargando enfermeros: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Eliminar enfermero seleccionado
    private void onEliminar() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un enfermero para eliminar", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro que desea eliminar este enfermero?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                DefaultTableModel model = (DefaultTableModel) tabla.getModel();
                int id = (int) model.getValueAt(fila, 0);
                controller.eliminarEnfermero(id);
                cargarTabla();
                JOptionPane.showMessageDialog(this, "Enfermero eliminado");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al eliminar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
