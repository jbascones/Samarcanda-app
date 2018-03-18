package com.jorgebascones.samarcanda.Modelos;

/**
 * Created by jorgebascones on 10/2/18.
 */

public class Venta {

    public User user;
    public String fecha;
    public String clienteId;
    public String articuloId;
    public String ventaKey;
    public int edadVenta;
    public int numeroArticulos;
    public String nombresArticulos;
    public int importe;
    public boolean fromReserva;


    public Venta() {
        fromReserva = false;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getClienteId() {
        return clienteId;
    }

    public void setClienteId(String clienteId) {
        this.clienteId = clienteId;
    }

    public String getArticuloId() {
        return articuloId;
    }

    public void setArticuloId(String articuloId) {
        this.articuloId = articuloId;
    }

    public String getVentaKey() {
        return ventaKey;
    }

    public void setVentaKey(String ventaKey) {
        this.ventaKey = ventaKey;
    }

    public int getEdadVenta() {
        return edadVenta;
    }

    public int getNumeroArticulos() {
        return numeroArticulos;
    }

    public void setNumeroArticulos(int numeroArticulos) {
        this.numeroArticulos = numeroArticulos;
    }

    public String getNombresArticulos() {
        return nombresArticulos;
    }

    public void setNombresArticulos(String nombresArticulos) {
        this.nombresArticulos = nombresArticulos;
    }

    public void addNumeroArticulos(){
        numeroArticulos++;
    }

    public void restaNumeroArticulos(){numeroArticulos--; }

    public void setEdadVenta(int edadVenta) {
        this.edadVenta = edadVenta;
    }

    public int getImporte() {
        return importe;
    }

    public void setImporte(int importe) {
        this.importe = importe;
    }

    public boolean isFromReserva() {
        return fromReserva;
    }

    public void setFromReserva(boolean fromReserva) {
        this.fromReserva = fromReserva;
    }
}
