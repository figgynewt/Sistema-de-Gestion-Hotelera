package system;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.*;

import model.Habitacion;
import model.Huesped;
import model.Reserva;
import model.ServicioAdicional;
import model.TipoHabitacion;
import model.EstadoHabitacion;
import model.TipoHuesped;
import model.OrigenReserva;
import model.MetodoPago;

public class SistemaHotel {
    private List<Habitacion> habitaciones;
    private List<Huesped> huespedes;
    private List<Reserva> reservas;
    private List<ServicioAdicional> servicios;
    private String archivoHuespedes = "huespedes.txt";
    private String archivoHabitaciones = "habitaciones.txt";
    private String archivoReservas = "reservas.txt";
    private String archivoServicios = "servicios.txt";
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    public SistemaHotel() {
        habitaciones = new ArrayList<>();
        huespedes = new ArrayList<>();
        reservas = new ArrayList<>();
        servicios = new ArrayList<>();
        cargarHabitaciones();
        cargarHuespedes();
        cargarServicios();
        cargarReservas();
    }

    // --- Persistencia de Habitaciones ---
    private void guardarHabitaciones() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(archivoHabitaciones))) {
            for (Habitacion h : habitaciones) {
                pw.println(String.join(",",
                    h.getNumero(),
                    h.getTipo().name(),
                    String.valueOf(h.getCapacidadMax()),
                    String.valueOf(h.getPrecioBase()),
                    h.getEstado().name()
                ));
            }
        } catch (IOException e) {
            System.err.println("Error guardando habitaciones: " + e.getMessage());
        }
    }

    private void cargarHabitaciones() {
        habitaciones.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(archivoHabitaciones))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(",");
                if (datos.length >= 5) {
                    habitaciones.add(new Habitacion(
                        datos[0],
                        TipoHabitacion.valueOf(datos[1]),
                        Integer.parseInt(datos[2]),
                        Double.parseDouble(datos[3]),
                        EstadoHabitacion.valueOf(datos[4])
                    ));
                }
            }
        } catch (IOException e) {
            // Si no existe el archivo, crea habitaciones por defecto
            habitaciones.add(new Habitacion("101", TipoHabitacion.INDIVIDUAL, 1, 100, EstadoHabitacion.DISPONIBLE));
            habitaciones.add(new Habitacion("201", TipoHabitacion.DOBLE, 2, 150, EstadoHabitacion.DISPONIBLE));
            habitaciones.add(new Habitacion("301", TipoHabitacion.SUITE, 4, 300, EstadoHabitacion.MANTENIMIENTO));
            habitaciones.add(new Habitacion("401", TipoHabitacion.PRESIDENCIAL, 6, 800, EstadoHabitacion.DISPONIBLE));
            guardarHabitaciones();
        }
    }

    // --- Persistencia de Huéspedes ---
    public void registrarHuesped(Huesped huesped) {
        huespedes.add(huesped);
        guardarHuespedes();
    }

    private void guardarHuespedes() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(archivoHuespedes))) {
            for (Huesped h : huespedes) {
                pw.println(String.join(",",
                    h.getId(),
                    h.getNombre(),
                    h.getDocumento(),
                    h.getContacto(),
                    h.getTipo().name()
                ));
            }
        } catch (IOException e) {
            System.err.println("Error guardando huéspedes: " + e.getMessage());
        }
    }

    private void cargarHuespedes() {
        huespedes.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(archivoHuespedes))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(",");
                if (datos.length >= 5) {
                    TipoHuesped tipo = TipoHuesped.valueOf(datos[4]);
                    huespedes.add(new Huesped(datos[0], datos[1], datos[2], datos[3], tipo));
                }
            }
        } catch (IOException e) {
        }
    }

    // --- Persistencia de Servicios ---
    private void guardarServicios() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(archivoServicios))) {
            for (ServicioAdicional s : servicios) {
                pw.println(s.getNombre() + "," + s.getPrecio());
            }
        } catch (IOException e) {
            System.err.println("Error guardando servicios: " + e.getMessage());
        }
    }

    private void cargarServicios() {
        servicios.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(archivoServicios))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(",");
                if (datos.length == 2) {
                    servicios.add(new ServicioAdicional(datos[0], Double.parseDouble(datos[1])));
                }
            }
        } catch (IOException e) {
            // Si no existe el archivo, crea servicios por defecto
            servicios.add(new ServicioAdicional("Transporte Aeropuerto", 50));
            servicios.add(new ServicioAdicional("Spa Premium", 120));
            servicios.add(new ServicioAdicional("Cena Romántica", 80));
            guardarServicios();
        }
    }

    // --- Persistencia de Reservas ---
    public void realizarReserva(Reserva reserva) {
        reservas.add(reserva);
        reserva.getHuesped().agregarReserva(reserva);
        guardarReservas();
        guardarHabitaciones(); // Actualiza estado de habitación
    }

    public boolean pagarReserva(Reserva reserva, model.MetodoPago metodo) {
        if (reserva == null || reserva.getFactura().isPagada()) return false;
        reserva.getFactura().pagar(metodo);
        guardarReservas();
        return true;
    }

    // Guardar reservas:
    public void guardarReservas() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(archivoReservas))) {
            for (Reserva r : reservas) {
                // Serializa los servicios adicionales como nombres separados por ;
                StringBuilder serviciosStr = new StringBuilder();
                for (ServicioAdicional s : r.getServiciosAdicionales()) {
                    if (serviciosStr.length() > 0) serviciosStr.append(";");
                    serviciosStr.append(s.getNombre());
                }
                pw.println(String.join(",",
                    r.getCodigo(),
                    r.getHuesped().getId(),
                    r.getHabitacion().getNumero(),
                    sdf.format(r.getFechaInicio()),
                    sdf.format(r.getFechaFin()),
                    r.getOrigen().name(),
                    String.valueOf(r.getFactura().isPagada()),
                    r.getFactura().getMetodoPago() != null ? r.getFactura().getMetodoPago().name() : "",
                    serviciosStr.toString()
                ));
            }
        } catch (IOException e) {
            System.err.println("Error guardando reservas: " + e.getMessage());
        }
    }

    // Cargar reservas:
    private void cargarReservas() {
        reservas.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(archivoReservas))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(",", -1);
                if (datos.length >= 9) {
                    String codigo = datos[0];
                    Huesped huesped = buscarHuespedPorId(datos[1]);
                    Habitacion habitacion = buscarHabitacionPorNumero(datos[2]);
                    Date fechaInicio = sdf.parse(datos[3]);
                    Date fechaFin = sdf.parse(datos[4]);
                    OrigenReserva origen = OrigenReserva.valueOf(datos[5]);
                    boolean pagada = Boolean.parseBoolean(datos[6]);
                    String metodoPagoStr = datos[7];
                    String serviciosStr = datos[8];
                    MetodoPago metodoPago = null;
                    if (metodoPagoStr != null && !metodoPagoStr.isEmpty()) {
                        metodoPago = MetodoPago.valueOf(metodoPagoStr);
                    }
                    Reserva reserva = new Reserva(codigo, huesped, habitacion, fechaInicio, fechaFin, origen);
                    if (pagada) {
                        reserva.getFactura().pagar(metodoPago);
                    }
                    // Asocia los servicios adicionales
                    if (serviciosStr != null && !serviciosStr.isEmpty()) {
                        String[] nombresServicios = serviciosStr.split(";");
                        for (String nombre : nombresServicios) {
                            ServicioAdicional s = buscarServicioPorNombre(nombre);
                            if (s != null) reserva.agregarServicio(s);
                        }
                    }
                    reservas.add(reserva);
                }
            }
        } catch (Exception e) {
            System.err.println("No se pudo cargar reservas: " + e.getMessage());
        }
    }

    private Huesped buscarHuespedPorId(String id) {
        for (Huesped h : huespedes) {
            if (h.getId().equals(id)) return h;
        }
        return null;
    }

    private Habitacion buscarHabitacionPorNumero(String numero) {
        for (Habitacion h : habitaciones) {
            if (h.getNumero().equals(numero)) return h;
        }
        return null;
    }

    private ServicioAdicional buscarServicioPorNombre(String nombre) {
        for (ServicioAdicional s : servicios) {
            if (s.getNombre().equals(nombre)) return s;
        }
        return null;
    }

    // --- Métodos de consulta ---
    public List<Habitacion> buscarHabitacionesDisponibles(TipoHabitacion tipo, Date fechaInicio, Date fechaFin) {
        List<Habitacion> disponibles = new ArrayList<>();
        for (Habitacion hab : habitaciones) {
            if (hab.getEstado() == EstadoHabitacion.DISPONIBLE && 
                (tipo == null || hab.getTipo() == tipo)) {
                disponibles.add(hab);
            }
        }
        return disponibles;
    }

    public Map<TipoHabitacion, Integer> demandaPorTipoHabitacion() {
        Map<TipoHabitacion, Integer> demanda = new HashMap<>();
        for (Habitacion hab : habitaciones) {
            demanda.put(hab.getTipo(), demanda.getOrDefault(hab.getTipo(), 0) + 1);
        }
        return demanda;
    }

    public List<Huesped> huespedesRecurrentes() {
        List<Huesped> recurrentes = new ArrayList<>();
        for (Huesped h : huespedes) {
            if (h.getHistorial().size() > 1) {
                recurrentes.add(h);
            }
        }
        return recurrentes;
    }

    // --- Getters ---
    public List<Habitacion> getHabitaciones() { return habitaciones; }
    public List<Huesped> getHuespedes() { return huespedes; }
    public List<Reserva> getReservas() { return reservas; }
    public List<ServicioAdicional> getServicios() { return servicios; }
}