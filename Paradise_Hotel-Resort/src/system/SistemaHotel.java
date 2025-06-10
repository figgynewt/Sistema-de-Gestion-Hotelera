package system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import model.Habitacion;
import model.Huesped;
import model.Reserva;
import model.ServicioAdicional;
import model.Factura;
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

    public SistemaHotel() {
        habitaciones = new ArrayList<>();
        huespedes = new ArrayList<>();
        reservas = new ArrayList<>();
        servicios = new ArrayList<>();
        cargarDatosIniciales();
        cargarServicios();
    }

    private void cargarDatosIniciales() {
        habitaciones.add(new Habitacion("101", TipoHabitacion.INDIVIDUAL, 1, 100, EstadoHabitacion.DISPONIBLE));
        habitaciones.add(new Habitacion("201", TipoHabitacion.DOBLE, 2, 150, EstadoHabitacion.DISPONIBLE));
        habitaciones.add(new Habitacion("301", TipoHabitacion.SUITE, 4, 300, EstadoHabitacion.MANTENIMIENTO));
        habitaciones.add(new Habitacion("401", TipoHabitacion.PRESIDENCIAL, 6, 800, EstadoHabitacion.DISPONIBLE));
        cargarDesdeArchivo();
    }

    private void cargarServicios() {
        servicios.add(new ServicioAdicional("Transporte Aeropuerto", 50));
        servicios.add(new ServicioAdicional("Spa Premium", 120));
        servicios.add(new ServicioAdicional("Cena Romántica", 80));
    }

    public void registrarHuesped(Huesped huesped) {
        huespedes.add(huesped);
        guardarHuespedes();
    }

    public void realizarReserva(Reserva reserva) {
        reservas.add(reserva);
        reserva.getHuesped().agregarReserva(reserva);
    }

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

    private void cargarDesdeArchivo() {
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
            System.out.println("No se pudo cargar huéspedes: " + e.getMessage());
        }
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
            System.out.println("Error guardando huéspedes: " + e.getMessage());
        }
    }

    public List<Habitacion> getHabitaciones() { return habitaciones; }
    public List<Huesped> getHuespedes() { return huespedes; }
    public List<Reserva> getReservas() { return reservas; }
    public List<ServicioAdicional> getServicios() { return servicios; }
}