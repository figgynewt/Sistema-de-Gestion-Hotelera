package model;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

public class Reserva {
    private String codigo;
    private Huesped huesped;
    private Habitacion habitacion;
    private Date fechaInicio;
    private Date fechaFin;
    private OrigenReserva origen;
    private double deposito;
    private double saldoPendiente;
    private List<ServicioAdicional> serviciosAdicionales = new ArrayList<>();
    private Factura factura;

    public Reserva(String codigo, Huesped huesped, Habitacion habitacion, 
                  Date fechaInicio, Date fechaFin, OrigenReserva origen) {
        this.codigo = codigo;
        this.huesped = huesped;
        this.habitacion = habitacion;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.origen = origen;
        long dias = (fechaFin.getTime() - fechaInicio.getTime()) / (1000 * 60 * 60 * 24);
        if (dias == 0) dias = 1; // Al menos una noche
        double total = dias * habitacion.getPrecioBase();
        this.deposito = total * 0.3;
        this.saldoPendiente = total - deposito;
        this.factura = new Factura("FAC-" + codigo, total);
        habitacion.setEstado(EstadoHabitacion.OCUPADA);
    }

    public void agregarServicio(ServicioAdicional servicio) {
        serviciosAdicionales.add(servicio);
        factura.agregarCargo(servicio.getPrecio());
    }

    public void cancelar() {
        habitacion.setEstado(EstadoHabitacion.DISPONIBLE);
        saldoPendiente = 0;
    }
    
    public String getCodigo() { return codigo; }
    public Huesped getHuesped() { return huesped; }
    public Habitacion getHabitacion() { return habitacion; }
    public double getSaldoPendiente() { return saldoPendiente; }
    public Factura getFactura() { return factura; }
    public Date getFechaFin() { return fechaFin; }
    public OrigenReserva getOrigen() { return origen; }
    public Date getFechaInicio() { return fechaInicio; }
    public List<ServicioAdicional> getServiciosAdicionales() { return serviciosAdicionales; }

    @Override
    public String toString() {
        String s = codigo + " - " + habitacion.getNumero() + " - " + huesped.getNombre();
        if (factura.isPagada()) {
            s += " (Pagada: " + factura.getMetodoPago() + ")";
        }
        return s;
    }
}