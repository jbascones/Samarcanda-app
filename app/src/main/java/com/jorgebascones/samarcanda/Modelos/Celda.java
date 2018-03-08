package com.jorgebascones.samarcanda.Modelos;

/**
 * Created by jorgebascones on 7/3/18.
 */

public class Celda {

    public String tipo;

    public String fotoUrl;

    public String texto;

    public Celda (String tipo){
        this.tipo = tipo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }
}
