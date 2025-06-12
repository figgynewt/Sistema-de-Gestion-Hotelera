package model;

public class Habitacion {
    private String numero;
    private TipoHabitacion tipo;
    private int capacidadMax;
    private double precioBase;
    private EstadoHabitacion estado;

    public Habitacion(String numero, TipoHabitacion tipo, int capacidadMax, double precioBase, EstadoHabitacion estado) {
        this.numero = numero;
        this.tipo = tipo;
        this.capacidadMax = capacidadMax;
        this.precioBase = precioBase;
        this.estado = estado;
    }

    public String getNumero() {
        return numero;
    }

    public TipoHabitacion getTipo() {
        return tipo;
    }

    public int getCapacidadMax() {
        return capacidadMax;
    }

    public double getPrecioBase() {
        return precioBase;
    }

    public EstadoHabitacion getEstado() {
        return estado;
    }

    public void setEstado(EstadoHabitacion estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Habitación " + numero + " (" + tipo + ") - $" + precioBase + " - Estado: " + estado;
    }
}