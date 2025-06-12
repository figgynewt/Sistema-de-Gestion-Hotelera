package model;

public class Factura {
    private String id;
    private double total;
    private MetodoPago metodo;
    private boolean pagada;

    public Factura(String id, double total) {
        this.id = id;
        this.total = total;
        this.pagada = false;
    }

    public void agregarCargo(double monto) {
        total += monto;
    }

    public void pagar(MetodoPago metodo) {
        this.metodo = metodo;
        this.pagada = true;
    }

    public String getId() {
        return id;
    }

    public double getTotal() {
        return total;
    }

    public MetodoPago getMetodoPago() {
        return metodo;
    }

    public boolean isPagada() {
        return pagada;
    }

    @Override
    public String toString() {
        return "Factura ID: " + id + ", Total: $" + total + ", Pagada: " + pagada;
    }
}