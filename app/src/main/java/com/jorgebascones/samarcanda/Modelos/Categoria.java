package com.jorgebascones.samarcanda.Modelos;

/**
 * Created by jorgebascones on 26/2/18.
 */

public class Categoria {

    public String nombre;
    public String urlFoto;




    //Es necesario un constructor sin argumentos para que se pueda crear el objeto a partir de los datos bajados
    public Categoria(){

    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUrlFoto() {
        return urlFoto;
    }

    public void setUrlFoto(String urlFoto) {
        this.urlFoto = urlFoto;
    }
}
