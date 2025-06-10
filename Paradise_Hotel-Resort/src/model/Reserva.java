package model;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ArrayList;

public class Reserva {
    private String codigo;
    private Date fechaInicio;
    private Date fechaFin;
    private Huesped huesped;
    private Habitacion habitacion;
    private OrigenReserva origen;
    private double deposito;
    private double saldoPendiente;
    private List<ServicioAdicional> servicios;
    private Factura factura;

    public Reserva(String codigo, Huesped huesped, Habitacion habitacion, 
                  Date fechaInicio, Date fechaFin, OrigenReserva origen) {
        this.codigo = codigo;
        this.huesped = huesped;
        this.habitacion = habitacion;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.origen = origen;
        this.servicios = new ArrayList<>();
        
        long dias = (fechaFin.getTime() - fechaInicio.getTime()) / (1000 * 60 * 60 * 24);
        if (dias < 1) dias = 1;
        double total = dias * habitacion.getPrecioBase();
        this.deposito = total * 0.3;
        this.saldoPendiente = total - deposito;
        
        this.factura = new Factura("FAC-" + codigo, total);
        habitacion.setEstado(EstadoHabitacion.OCUPADA);
    }

    public void agregarServicio(ServicioAdicional servicio) {
        servicios.add(servicio);
        factura.agregarCargo(servicio.getPrecio());
        saldoPendiente += servicio.getPrecio();
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

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return "Reserva " + codigo + " - " + huesped.getNombre() + " - Habitación: " +
                habitacion.getNumero() + " - Desde: " + sdf.format(fechaInicio) +
                " Hasta: " + sdf.format(fechaFin) + " - Origen: " + origen;
    }
}