package com.jorgebascones.samarcanda.Modelos;

import java.util.ArrayList;

/**
 * Created by jorgebascones on 10/2/18.
 */

public class CarritoItem {


    public Articulo carritoItem;

    public int unidades;

    public CarritoItem(Articulo articulo){
        carritoItem = articulo;
        unidades = 1;
    }

    public Articulo getCarritoItem() {
        return carritoItem;
    }

    public void setCarritoItem(Articulo carritoItem) {
        this.carritoItem = carritoItem;
    }

    public int getUnidades() {
        return unidades;
    }

    public void setUnidades(int unidades) {
        this.unidades = unidades;
    }

    public void addUnidad(){
        unidades++;
    }

    public void restaUnidad(){
        unidades--;
    }
}
