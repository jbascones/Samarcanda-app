package com.jorgebascones.samarcanda;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by jorgebascones on 10/2/18.
 */

public class Fecha {

    public String fecha;

    public Fecha(){
        fecha = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault()).format(new Date());
    }

    public String getFecha(){
        return fecha;
    }

    public String getFechaActual(){
        return new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault()).format(new Date());
    }

    public String getMes(String fecha){
        return fecha.substring(5,7);
    }

    public String getAnno(String fecha){
        return fecha.substring(0,4);
    }

    public String getRutaVenta(){

        String mes = getMes(fecha);

        String anno = getAnno(fecha);

        String ruta = mes + "/" + anno;

        return ruta;
    }


}
