package gui;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.Map;
import java.util.HashMap;

import model.Habitacion;
import model.Huesped;
import model.Reserva;
import model.ServicioAdicional;
import model.TipoHabitacion;
import model.TipoHuesped;
import model.OrigenReserva;
import model.MetodoPago;
import system.SistemaHotel;

public class HotelGUI extends JFrame {
    private SistemaHotel sistema;
    private JTabbedPane tabbedPane;

    public HotelGUI() {
        sistema = new SistemaHotel();
        setTitle("Sistema de Gestión Hotelera");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 15));
        tabbedPane.addTab("Habitaciones", crearPanelHabitaciones());
        tabbedPane.addTab("Huéspedes", crearPanelHuespedes());
        tabbedPane.addTab("Reservas", crearPanelReservas());
        tabbedPane.addTab("Reportes", crearPanelReportes());

        add(tabbedPane);
        setVisible(true);
    }

    private JPanel crearPanelHabitaciones() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        DefaultListModel<Habitacion> modelo = new DefaultListModel<>();
        for (Habitacion hab : sistema.getHabitaciones()) {
            modelo.addElement(hab);
        }

        JList<Habitacion> lista = new JList<>(modelo);
        lista.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lista.setVisibleRowCount(10);
        lista.setFixedCellHeight(28);
        JScrollPane scroll = new JScrollPane(lista);
        panel.add(scroll, BorderLayout.CENTER);

        JButton btnActualizar = new JButton("Actualizar");
        btnActualizar.addActionListener(e -> {
            modelo.clear();
            for (Habitacion hab : sistema.getHabitaciones()) {
                modelo.addElement(hab);
            }
        });
        JPanel panelBoton = new JPanel();
        panelBoton.add(btnActualizar);
        panel.add(panelBoton, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel crearPanelHuespedes() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        DefaultListModel<Huesped> modelo = new DefaultListModel<>();
        for (Huesped h : sistema.getHuespedes()) {
            modelo.addElement(h);
        }

        JList<Huesped> lista = new JList<>(modelo);
        lista.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lista.setVisibleRowCount(10);
        lista.setFixedCellHeight(28);
        JScrollPane scroll = new JScrollPane(lista);
        panel.add(scroll, BorderLayout.CENTER);

        JPanel formulario = new JPanel();
        formulario.setLayout(new GridLayout(0, 2, 6, 6));
        formulario.setBorder(BorderFactory.createTitledBorder("Registrar nuevo huésped"));

        JTextField txtNombre = new JTextField();
        JTextField txtDocumento = new JTextField();
        JComboBox<TipoHuesped> cmbTipo = new JComboBox<>(TipoHuesped.values());
        JTextField txtContacto = new JTextField();

        formulario.add(new JLabel("Nombre:"));
        formulario.add(txtNombre);
        formulario.add(new JLabel("Documento:"));
        formulario.add(txtDocumento);
        formulario.add(new JLabel("Tipo:"));
        formulario.add(cmbTipo);
        formulario.add(new JLabel("Contacto:"));
        formulario.add(txtContacto);

        JButton btnRegistrar = new JButton("Registrar");
        formulario.add(new JLabel());
        formulario.add(btnRegistrar);

        btnRegistrar.addActionListener(e -> {
            String nombre = txtNombre.getText().trim();
            String documento = txtDocumento.getText().trim();
            String contacto = txtContacto.getText().trim();
            if (nombre.isEmpty() || documento.isEmpty() || contacto.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.");
                return;
            }
            Huesped nuevo = new Huesped(
                UUID.randomUUID().toString(),
                nombre,
                documento,
                contacto,
                (TipoHuesped) cmbTipo.getSelectedItem()
            );
            sistema.registrarHuesped(nuevo);
            modelo.addElement(nuevo);
            txtNombre.setText("");
            txtDocumento.setText("");
            txtContacto.setText("");
        });

        panel.add(formulario, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel crearPanelReservas() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Formulario de reserva
        JPanel formulario = new JPanel();
        formulario.setLayout(new GridLayout(0, 2, 6, 6));
        formulario.setBorder(BorderFactory.createTitledBorder("Nueva reserva"));

        JComboBox<Huesped> cmbHuesped = new JComboBox<>();
        for (Huesped h : sistema.getHuespedes()) {
            cmbHuesped.addItem(h);
        }
        JComboBox<TipoHabitacion> cmbTipoHab = new JComboBox<>(TipoHabitacion.values());
        JTextField txtFechaInicio = new JTextField(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
        JTextField txtFechaFin = new JTextField(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));

        formulario.add(new JLabel("Huésped:"));
        formulario.add(cmbHuesped);
        formulario.add(new JLabel("Tipo Habitación:"));
        formulario.add(cmbTipoHab);
        formulario.add(new JLabel("Fecha Inicio (dd/MM/yyyy):"));
        formulario.add(txtFechaInicio);
        formulario.add(new JLabel("Fecha Fin (dd/MM/yyyy):"));
        formulario.add(txtFechaFin);

        // Servicios adicionales
        formulario.add(new JLabel("Servicios adicionales:"));
        DefaultListModel<ServicioAdicional> modeloServicios = new DefaultListModel<>();
        for (ServicioAdicional s : sistema.getServicios()) {
            modeloServicios.addElement(s);
        }
        JList<ServicioAdicional> listaServicios = new JList<>(modeloServicios);
        listaServicios.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listaServicios.setVisibleRowCount(3);
        listaServicios.setFixedCellHeight(24);
        JScrollPane scrollServicios = new JScrollPane(listaServicios);
        formulario.add(scrollServicios);

        // Buscar habitaciones
        JButton btnBuscar = new JButton("Buscar Habitaciones");
        formulario.add(new JLabel());
        formulario.add(btnBuscar);

        DefaultListModel<Habitacion> modeloHab = new DefaultListModel<>();
        JList<Habitacion> listaHab = new JList<>(modeloHab);
        listaHab.setVisibleRowCount(4);
        listaHab.setFixedCellHeight(24);
        JScrollPane scrollHab = new JScrollPane(listaHab);

        btnBuscar.addActionListener(e -> {
            modeloHab.clear();
            try {
                Date fechaInicio = new SimpleDateFormat("dd/MM/yyyy").parse(txtFechaInicio.getText());
                Date fechaFin = new SimpleDateFormat("dd/MM/yyyy").parse(txtFechaFin.getText());
                List<Habitacion> disponibles = sistema.buscarHabitacionesDisponibles(
                    (TipoHabitacion) cmbTipoHab.getSelectedItem(), fechaInicio, fechaFin);
                for (Habitacion hab : disponibles) {
                    modeloHab.addElement(hab);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Formato de fecha incorrecto (dd/MM/yyyy)");
            }
        });

        // Botón de reserva
        JButton btnReservar = new JButton("Reservar");
        formulario.add(new JLabel());
        formulario.add(btnReservar);

        // Panel izquierdo: formulario y habitaciones disponibles
        JPanel panelIzq = new JPanel(new BorderLayout(6, 6));
        panelIzq.add(formulario, BorderLayout.NORTH);
        panelIzq.add(new JLabel("Habitaciones disponibles:"), BorderLayout.CENTER);
        panelIzq.add(scrollHab, BorderLayout.SOUTH);

        // Panel derecho: lista de reservas con scroll y botones pequeños
        DefaultListModel<Reserva> modeloReservas = new DefaultListModel<>();
        JList<Reserva> listaReservas = new JList<>(modeloReservas);
        listaReservas.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        listaReservas.setVisibleRowCount(12);
        listaReservas.setFixedCellHeight(24);
        for (Reserva r : sistema.getReservas()) {
            modeloReservas.addElement(r);
        }
        JScrollPane scrollReservas = new JScrollPane(listaReservas);

        JButton btnArriba = new JButton("↑");
        JButton btnAbajo = new JButton("↓");
        Dimension btnDim = new Dimension(26, 20);
        btnArriba.setPreferredSize(btnDim);
        btnAbajo.setPreferredSize(btnDim);

        btnArriba.addActionListener(e -> {
            int index = listaReservas.getSelectedIndex();
            if (index > 0) {
                listaReservas.setSelectedIndex(index - 1);
                listaReservas.ensureIndexIsVisible(index - 1);
            }
        });
        btnAbajo.addActionListener(e -> {
            int index = listaReservas.getSelectedIndex();
            if (index < modeloReservas.size() - 1) {
                listaReservas.setSelectedIndex(index + 1);
                listaReservas.ensureIndexIsVisible(index + 1);
            }
        });

        JPanel panelScroll = new JPanel();
        panelScroll.setLayout(new BoxLayout(panelScroll, BoxLayout.Y_AXIS));
        panelScroll.add(btnArriba);
        panelScroll.add(Box.createVerticalStrut(3));
        panelScroll.add(btnAbajo);

        JPanel panelListaReservas = new JPanel(new BorderLayout());
        panelListaReservas.add(scrollReservas, BorderLayout.CENTER);
        panelListaReservas.add(panelScroll, BorderLayout.EAST);

        // Panel de pago
        JComboBox<MetodoPago> cmbMetodoPago = new JComboBox<>(MetodoPago.values());
        JButton btnPagar = new JButton("Pagar");
        btnPagar.setPreferredSize(new Dimension(80, 24));

        btnPagar.addActionListener(e -> {
            Reserva reserva = listaReservas.getSelectedValue();
            MetodoPago metodo = (MetodoPago) cmbMetodoPago.getSelectedItem();
            if (reserva == null) {
                JOptionPane.showMessageDialog(this, "Seleccione una reserva para pagar.");
                return;
            }
            if (reserva.getFactura().isPagada()) {
                JOptionPane.showMessageDialog(this, "La reserva ya está pagada.");
                return;
            }
            boolean pagado = sistema.pagarReserva(reserva, metodo);
            if (pagado) {
                JOptionPane.showMessageDialog(this, "Reserva pagada con " + metodo + ".\nTotal: $" + reserva.getFactura().getTotal());
                listaReservas.repaint();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo realizar el pago.");
            }
        });

        JPanel panelPago = new JPanel();
        panelPago.add(new JLabel("Método de Pago:"));
        panelPago.add(cmbMetodoPago);
        panelPago.add(btnPagar);

        // Listener para mostrar detalles de la reserva seleccionada
        listaReservas.addListSelectionListener(e -> {
            Reserva reserva = listaReservas.getSelectedValue();
            if (reserva != null) {
                String info = "Reserva: " + reserva.getCodigo() + "\n"
                    + "Huésped: " + reserva.getHuesped().getNombre() + "\n"
                    + "Habitación: " + reserva.getHabitacion().getNumero() + "\n"
                    + "Estado de pago: " + (reserva.getFactura().isPagada() ? "Pagada" : "Pendiente");
                if (reserva.getFactura().isPagada()) {
                    info += "\nMétodo de pago: " + reserva.getFactura().getMetodoPago();
                }
                List<ServicioAdicional> servicios = reserva.getServiciosAdicionales();
                if (!servicios.isEmpty()) {
                    info += "\nServicios: ";
                    for (ServicioAdicional s : servicios) {
                        info += "\n - " + s.getNombre() + " ($" + s.getPrecio() + ")";
                    }
                }
                JOptionPane.showMessageDialog(this, info);
            }
        });

        // Acción de reservar
        btnReservar.addActionListener(e -> {
            Huesped huesped = (Huesped) cmbHuesped.getSelectedItem();
            Habitacion habitacion = listaHab.getSelectedValue();
            String fechaInicioStr = txtFechaInicio.getText().trim();
            String fechaFinStr = txtFechaFin.getText().trim();
            if (huesped == null || habitacion == null || fechaInicioStr.isEmpty() || fechaFinStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios y debe seleccionar una habitación.");
                return;
            }
            try {
                Date fechaInicio = new SimpleDateFormat("dd/MM/yyyy").parse(fechaInicioStr);
                Date fechaFin = new SimpleDateFormat("dd/MM/yyyy").parse(fechaFinStr);
                if (!fechaFin.after(fechaInicio)) {
                    JOptionPane.showMessageDialog(this, "La fecha de fin debe ser posterior a la de inicio.");
                    return;
                }
                List<ServicioAdicional> serviciosSeleccionados = listaServicios.getSelectedValuesList();
                Reserva reserva = new Reserva(
                    "RES" + System.currentTimeMillis(),
                    huesped,
                    habitacion,
                    fechaInicio,
                    fechaFin,
                    OrigenReserva.WEB
                );
                for (ServicioAdicional s : serviciosSeleccionados) {
                    reserva.agregarServicio(s);
                }
                sistema.realizarReserva(reserva);
                modeloReservas.addElement(reserva);
                JOptionPane.showMessageDialog(this, "Reserva realizada!\nCódigo: " + reserva.getCodigo());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Formato de fecha incorrecto (dd/MM/yyyy)");
            }
        });

        // Layout final
        panel.add(panelIzq, BorderLayout.CENTER);
        panel.add(panelListaReservas, BorderLayout.EAST);
        panel.add(panelPago, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel crearPanelReportes() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Demanda por tipo de habitación
        JPanel panelDemanda = new JPanel(new BorderLayout(5, 5));
        panelDemanda.setBorder(BorderFactory.createTitledBorder("Demanda por Tipo de Habitación"));

        DefaultListModel<String> modeloDemanda = new DefaultListModel<>();
        Map<TipoHabitacion, Integer> demanda = new HashMap<>();
        for (Reserva r : sistema.getReservas()) {
            TipoHabitacion tipo = r.getHabitacion().getTipo();
            demanda.put(tipo, demanda.getOrDefault(tipo, 0) + 1);
        }
        for (TipoHabitacion tipo : TipoHabitacion.values()) {
            int count = demanda.getOrDefault(tipo, 0);
            modeloDemanda.addElement(tipo + ": " + count + " reservas");
        }
        JList<String> listaDemanda = new JList<>(modeloDemanda);
        listaDemanda.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panelDemanda.add(new JScrollPane(listaDemanda), BorderLayout.CENTER);

        // Huéspedes recurrentes
        JPanel panelRecurrentes = new JPanel(new BorderLayout(5, 5));
        panelRecurrentes.setBorder(BorderFactory.createTitledBorder("Huéspedes Recurrentes"));

        DefaultListModel<String> modeloRecurrentes = new DefaultListModel<>();
        for (Huesped h : sistema.huespedesRecurrentes()) {
            modeloRecurrentes.addElement(h.getNombre() + " (" + h.getDocumento() + ") - Reservas: " + h.getHistorial().size());
        }
        JList<String> listaRecurrentes = new JList<>(modeloRecurrentes);
        listaRecurrentes.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panelRecurrentes.add(new JScrollPane(listaRecurrentes), BorderLayout.CENTER);

        panel.add(panelDemanda);
        panel.add(panelRecurrentes);
        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HotelGUI());
    }
}