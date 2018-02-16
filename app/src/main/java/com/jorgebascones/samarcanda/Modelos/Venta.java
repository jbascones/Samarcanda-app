package com.jorgebascones.samarcanda.Modelos;

/**
 * Created by jorgebascones on 10/2/18.
 */

public class Venta {

    public String userId;
    public User user;
    public Articulo articulo;
    public String fecha;
    public String clienteId;
    public String articuloId;
    public String ventaKey;



    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Articulo getArticulo() {
        return articulo;
    }

    public void setArticulo(Articulo articulo) {
        this.articulo = articulo;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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
}
