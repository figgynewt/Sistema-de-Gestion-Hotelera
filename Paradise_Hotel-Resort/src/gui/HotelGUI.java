package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
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
import system.SistemaHotel;

public class HotelGUI extends JFrame {
    private SistemaHotel sistema;
    private JTabbedPane tabbedPane;

    public HotelGUI() {
        sistema = new SistemaHotel();
        setTitle("Sistema de Gestión Hotel Paraíso");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Habitaciones", crearPanelHabitaciones());
        tabbedPane.addTab("Huéspedes", crearPanelHuespedes());
        tabbedPane.addTab("Reservas", crearPanelReservas());
        tabbedPane.addTab("Reportes", crearPanelReportes());
        
        add(tabbedPane);
        setVisible(true);
    }

    private JPanel crearPanelHabitaciones() {
        JPanel panel = new JPanel(new BorderLayout());
        DefaultListModel<Habitacion> modelo = new DefaultListModel<>();
        for (Habitacion hab : sistema.getHabitaciones()) {
            modelo.addElement(hab);
        }
        
        JList<Habitacion> lista = new JList<>(modelo);
        lista.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, 
                                                         boolean isSelected, boolean cellHasFocus) {
                Habitacion hab = (Habitacion) value;
                String estado = "";
                switch (hab.getEstado()) {
                    case DISPONIBLE: estado = "🟢"; break;
                    case OCUPADA: estado = "🔴"; break;
                    case MANTENIMIENTO: estado = "🟡"; break;
                }
                return super.getListCellRendererComponent(
                    list, estado + " " + hab, index, isSelected, cellHasFocus);
            }
        });
        
        panel.add(new JScrollPane(lista), BorderLayout.CENTER);
        
        // Botones de accion:
        JPanel panelBotones = new JPanel();
        JButton btnActualizar = new JButton("Actualizar Estados");
        btnActualizar.addActionListener(e -> {
            modelo.clear();
            for (Habitacion hab : sistema.getHabitaciones()) {
                modelo.addElement(hab);
            }
        });
        panelBotones.add(btnActualizar);
        
        panel.add(panelBotones, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel crearPanelHuespedes() {
        JPanel panel = new JPanel(new BorderLayout());
        DefaultListModel<Huesped> modelo = new DefaultListModel<>();
        for (Huesped h : sistema.getHuespedes()) {
            modelo.addElement(h);
        }
        
        JList<Huesped> lista = new JList<>(modelo);
        panel.add(new JScrollPane(lista), BorderLayout.CENTER);
        
        // Formulario de registro:
        JPanel formulario = new JPanel(new GridLayout(5, 2));
        formulario.add(new JLabel("Nombre:"));
        JTextField txtNombre = new JTextField();
        formulario.add(txtNombre);
        
        formulario.add(new JLabel("Documento:"));
        JTextField txtDocumento = new JTextField();
        formulario.add(txtDocumento);
        
        formulario.add(new JLabel("Tipo:"));
        JComboBox<TipoHuesped> cmbTipo = new JComboBox<>(TipoHuesped.values());
        formulario.add(cmbTipo);
        
        formulario.add(new JLabel("Contacto:"));
        JTextField txtContacto = new JTextField();
        formulario.add(txtContacto);
        
        JButton btnRegistrar = new JButton("Registrar Huésped");
        btnRegistrar.addActionListener(e -> {
            Huesped nuevo = new Huesped(
                UUID.randomUUID().toString(),
                txtNombre.getText(),
                txtDocumento.getText(),
                txtContacto.getText(),
                (TipoHuesped) cmbTipo.getSelectedItem()
            );
            sistema.registrarHuesped(nuevo);
            modelo.addElement(nuevo);
            txtNombre.setText("");
            txtDocumento.setText("");
            txtContacto.setText("");
        });
        formulario.add(btnRegistrar);
        
        panel.add(formulario, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel crearPanelReservas() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel formulario = new JPanel(new GridLayout(6, 2));
        
        // Seleccion de huesped:
        formulario.add(new JLabel("Huésped:"));
        JComboBox<Huesped> cmbHuesped = new JComboBox<>();
        for (Huesped h : sistema.getHuespedes()) {
            cmbHuesped.addItem(h);
        }
        formulario.add(cmbHuesped);
        
        // Tipo de habitacion:
        formulario.add(new JLabel("Tipo Habitación:"));
        JComboBox<TipoHabitacion> cmbTipoHab = new JComboBox<>(TipoHabitacion.values());
        formulario.add(cmbTipoHab);
        
        // Fechas:
        formulario.add(new JLabel("Fecha Inicio:"));
        JTextField txtFechaInicio = new JTextField(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
        formulario.add(txtFechaInicio);
        
        formulario.add(new JLabel("Fecha Fin:"));
        JTextField txtFechaFin = new JTextField(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
        formulario.add(txtFechaFin);
        
        // Boton de busqueda:
        JButton btnBuscar = new JButton("Buscar Habitaciones Disponibles");
        formulario.add(btnBuscar);
        
        // Lista de habitaciones disponibles:
        DefaultListModel<Habitacion> modeloHab = new DefaultListModel<>();
        JList<Habitacion> listaHab = new JList<>(modeloHab);
        
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
        
        // Boton de reserva:
        JButton btnReservar = new JButton("Realizar Reserva");
        btnReservar.addActionListener(e -> {
            Huesped huesped = (Huesped) cmbHuesped.getSelectedItem();
            Habitacion habitacion = listaHab.getSelectedValue();
            try {
                Date fechaInicio = new SimpleDateFormat("dd/MM/yyyy").parse(txtFechaInicio.getText());
                Date fechaFin = new SimpleDateFormat("dd/MM/yyyy").parse(txtFechaFin.getText());
                if (huesped != null && habitacion != null) {
                    Reserva reserva = new Reserva(
                        "RES" + System.currentTimeMillis(),
                        huesped,
                        habitacion,
                        fechaInicio,
                        fechaFin,
                        OrigenReserva.WEB
                    );
                    sistema.realizarReserva(reserva);
                    JOptionPane.showMessageDialog(this, "Reserva realizada!\nCódigo: " + reserva.getCodigo());
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Formato de fecha incorrecto (dd/MM/yyyy)");
            }
        });
        formulario.add(btnReservar);
        
        panel.add(formulario, BorderLayout.NORTH);
        panel.add(new JScrollPane(listaHab), BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelReportes() {
        JPanel panel = new JPanel(new GridLayout(2, 1));
        
        // Demanda por tipo de habitacion:
        JPanel panelDemanda = new JPanel(new BorderLayout());
        panelDemanda.add(new JLabel("Demanda por Tipo de Habitación", SwingConstants.CENTER), BorderLayout.NORTH);
        
        DefaultListModel<String> modeloDemanda = new DefaultListModel<>();
        // Contar reservas por tipo de habitacion:
        Map<TipoHabitacion, Integer> demanda = new HashMap<>();
        for (Reserva r : sistema.getReservas()) {
            TipoHabitacion tipo = r.getHabitacion().getTipo();
            demanda.put(tipo, demanda.getOrDefault(tipo, 0) + 1);
        }
        for (Map.Entry<TipoHabitacion, Integer> entry : demanda.entrySet()) {
            modeloDemanda.addElement(entry.getKey() + ": " + entry.getValue() + " reservas");
        }
        panelDemanda.add(new JScrollPane(new JList<>(modeloDemanda)), BorderLayout.CENTER);

        // Huespedes recurrentes:
        JPanel panelRecurrentes = new JPanel(new BorderLayout());
        panelRecurrentes.add(new JLabel("Huéspedes Recurrentes", SwingConstants.CENTER), BorderLayout.NORTH);
        
        DefaultListModel<Huesped> modeloRecurrentes = new DefaultListModel<>();
        for (Huesped h : sistema.huespedesRecurrentes()) {
            modeloRecurrentes.addElement(h);
        }
        panelRecurrentes.add(new JScrollPane(new JList<>(modeloRecurrentes)), BorderLayout.CENTER);
        
        panel.add(panelDemanda);
        panel.add(panelRecurrentes);
        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HotelGUI());
    }
}