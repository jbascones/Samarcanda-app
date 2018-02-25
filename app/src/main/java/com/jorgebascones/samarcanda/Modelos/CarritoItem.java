package com.jorgebascones.samarcanda.Modelos;

import java.util.ArrayList;

/**
 * Created by jorgebascones on 10/2/18.
 */

public class CarritoItem {


    public Articulo carritoItem;

    public CarritoItem(Articulo articulo){
        carritoItem = articulo;
    }

    public Articulo getCarritoItem() {
        return carritoItem;
    }

    public void setCarritoItem(Articulo carritoItem) {
        this.carritoItem = carritoItem;
    }
}
