package model;

import java.util.List;
import java.util.ArrayList;

public class Huesped {
    private String id;
    private String nombre;
    private String documento;
    private String contacto;
    private TipoHuesped tipo;
    private List<Reserva> historial;

    public Huesped(String id, String nombre, String documento, String contacto, TipoHuesped tipo) {
        this.id = id;
        this.nombre = nombre;
        this.documento = documento;
        this.contacto = contacto;
        this.tipo = tipo;
        this.historial = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDocumento() {
        return documento;
    }

    public String getContacto() {
        return contacto;
    }

    public TipoHuesped getTipo() {
        return tipo;
    }

    public List<Reserva> getHistorial() {
        return historial;
    }

    public void agregarReserva(Reserva reserva) {
        historial.add(reserva);
    }

    @Override
    public String toString() {
        return nombre + " (" + documento + ") - " + tipo;
    }
}