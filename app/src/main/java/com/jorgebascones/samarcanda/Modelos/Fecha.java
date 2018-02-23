package com.jorgebascones.samarcanda.Modelos;

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

    //Substring (indice empieza incluido, indice acaba incluido +1)

    public String getMes(String fecha){
        return fecha.substring(5,7);
    }

    public String getAnno(String fecha){
        return fecha.substring(0,4);
    }

    public String getDia(String fecha){
        return fecha.substring(8,10);
    }

    public String getHora(String fecha){
        return fecha.substring(10,19);
    }

    public String fechaPreparada(String fecha, boolean hora) {

        String sHora = "";

        if(hora){
            sHora = getHora(fecha);
        }


        return getDia(fecha)+"/"+getMes(fecha)+"/"+getAnno(fecha)+" "+ sHora;
    }

    public String getRutaVenta(){

        String mes = getMes(fecha);

        String anno = getAnno(fecha);

        String dia = getDia(fecha);

        String ruta = anno + "/" + mes + "/" + dia;

        return ruta;
    }

    public String sumarDias(String fecha, int dias){
        String mes = getMes(fecha);

        String anno = getAnno(fecha);

        int dia =  StringToInt(getDia(fecha)) + dias;

        String ruta = anno + "/" + mes + "/" + dia;

        return ruta;
    }

    public int calcularEdadVenta(String nacimiento){

        int edad;

        edad = StringToInt(getAnno(fecha)) - StringToInt(getAnno(nacimiento));

        return edad;
    }

    public  int StringToInt(String str){
        return Integer.parseInt(str);
    }


}
