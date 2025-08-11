package Vista;

import Controlador.AppContext;
import Controlador.AppController;
import Modelo.*;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class FormularioVacuna extends JDialog {
    private final AppController controller = AppContext.get();

    // Controles búsqueda por ID arriba
    private JTextField txtBuscarId = new JTextField(10);
    private JButton btnBuscar = new JButton("Buscar");

    // Controles formulario
    private JComboBox<String> cbPaciente  = new JComboBox<>();
    private JTextField txtVacuna = new JTextField(20);
    private JDateChooser dateChooser = new JDateChooser();
    private JComboBox<String> cbAplicador = new JComboBox<>();

    private JTable tabla = new JTable(new DefaultTableModel(
            new Object[]{"ID","Paciente","Vacuna","Fecha","Aplicado por"}, 0));

    private List<Paciente>  pacientes;
    private List<Doctor>    doctores;
    private List<Enfermero> enfermeros;

    public FormularioVacuna() {
        super((Frame) null, "Vacunas", true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(820, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8,8));

        // Icono
        ImageIcon icon = new ImageIcon(getClass().getResource("/img/vacuna.png"));
        setIconImage(icon.getImage());

        dateChooser.setDateFormatString("yyyy-MM-dd");

        // Panel búsqueda por ID arriba
        JPanel panelBuscar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBuscar.add(new JLabel("Buscar Vacuna por ID:"));
        panelBuscar.add(txtBuscarId);
        panelBuscar.add(btnBuscar);

        // Formulario campos debajo búsqueda
        JPanel form = new JPanel(new GridLayout(4,2,8,8));
        form.setBorder(BorderFactory.createEmptyBorder(12,12,0,12));
        form.add(new JLabel("Paciente:"));      form.add(cbPaciente);
        form.add(new JLabel("Vacuna:"));        form.add(txtVacuna);
        form.add(new JLabel("Fecha:"));         form.add(dateChooser);
        form.add(new JLabel("Aplicado por:"));  form.add(cbAplicador);

        // Panel arriba con búsqueda + formulario
        JPanel panelArriba = new JPanel(new BorderLayout());
        panelArriba.add(panelBuscar, BorderLayout.NORTH);
        panelArriba.add(form, BorderLayout.CENTER);

        add(panelArriba, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        // Panel botones abajo derecha
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
        btnBuscar.addActionListener(e -> buscarVacunaPorId());

        cargarCombos();
        cargarTabla();
    }

    private void buscarVacunaPorId() {
        String texto = txtBuscarId.getText().trim();
        if (texto.isEmpty()) {
            cargarTabla(); // mostrar todos si vacío
            return;
        }
        try {
            int id = Integer.parseInt(texto);
            List<Vacuna> data = controller.getVacunas();
            DefaultTableModel m = (DefaultTableModel) tabla.getModel();
            m.setRowCount(0);
            boolean encontrado = false;
            for (Vacuna v : data) {
                if (v.getId() == id) {
                    String apl = (v.getAplicadoPor() instanceof Doctor ? "Dr. " : "Enf. ") +
                            v.getAplicadoPor().getNombre();
                    m.addRow(new Object[]{
                            v.getId(),
                            v.getPaciente().getNombre(),
                            v.getVacuna(),
                            v.getFecha(),
                            apl
                    });
                    encontrado = true;
                    break; // si solo quieres mostrar ese
                }
            }
            if (!encontrado) {
                JOptionPane.showMessageDialog(this, "No se encontró vacuna con ID: " + id);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "ID inválido, debe ser un número.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error buscando vacuna: " + ex.getMessage());
        }
    }

    private void cargarCombos() {
        try {
            cbPaciente.removeAllItems();
            cbAplicador.removeAllItems();

            pacientes  = controller.getPacientes();
            doctores   = controller.getDoctores();
            enfermeros = controller.getEnfermeros();

            for (Paciente p : pacientes) cbPaciente.addItem(p.getNombre());

            for (Doctor d : doctores) cbAplicador.addItem("Dr. " + d.getNombre());
            for (Enfermero e : enfermeros) cbAplicador.addItem("Enf. " + e.getNombre());

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error cargando combos: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onGuardar() {
        try {
            int ip = cbPaciente.getSelectedIndex();
            int ia = cbAplicador.getSelectedIndex();
            Date fecha = dateChooser.getDate();
            if (ip == -1 || ia == -1 || fecha == null || txtVacuna.getText().trim().isEmpty())
                throw new IllegalArgumentException("Complete todos los campos");

            LocalDate f = fecha.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            Paciente p = pacientes.get(ip);

            Persona aplicador;
            if (ia < doctores.size()) aplicador = doctores.get(ia);
            else aplicador = enfermeros.get(ia - doctores.size());

            controller.addVacuna(f, txtVacuna.getText().trim(), aplicador, p);
            JOptionPane.showMessageDialog(this, "Vacuna registrada");
            cargarTabla();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al guardar: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarTabla() {
        try {
            List<Vacuna> data = controller.getVacunas();
            DefaultTableModel m = (DefaultTableModel) tabla.getModel();
            m.setRowCount(0);
            for (Vacuna v : data) {
                String apl = (v.getAplicadoPor() instanceof Doctor ? "Dr. " : "Enf. ") +
                        v.getAplicadoPor().getNombre();
                m.addRow(new Object[]{
                        v.getId(),
                        v.getPaciente().getNombre(),
                        v.getVacuna(),
                        v.getFecha(),
                        apl
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error cargando vacunas: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onEliminar() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una vacuna para eliminar",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idVacuna = (int) tabla.getValueAt(fila, 0);
        int resp = JOptionPane.showConfirmDialog(this,
                "¿Está seguro que desea eliminar la vacuna seleccionada?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION);

        if (resp == JOptionPane.YES_OPTION) {
            try {
                controller.eliminarVacuna(idVacuna);
                JOptionPane.showMessageDialog(this, "Vacuna eliminada correctamente");
                cargarTabla();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al eliminar vacuna: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
