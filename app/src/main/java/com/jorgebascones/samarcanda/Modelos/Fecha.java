package com.jorgebascones.samarcanda.Modelos;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by jorgebascones on 10/2/18.
 */

public class Fecha {

    public String fecha;
    public String mes;
    public String anno;

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

        String dia = getDia(fecha);

        if(dia.length()==1){
            dia = "0"+dia;
        }


        return dia+"/"+getMes(fecha)+"/"+getAnno(fecha)+" "+ sHora;
    }

    public String getRutaVenta(){

        String mes = getMes(fecha);

        String anno = getAnno(fecha);

        String dia = getDia(fecha);

        if(dia.length()==1){
            dia = "0"+dia;
        }

        String ruta = anno + "/" + mes + "/" + dia;

        return ruta;
    }

    public String sumarDias(String fecha, int dias){
        String mes = getMes(fecha);

        String anno = getAnno(fecha);

        int dia =  StringToInt(getDia(fecha)) + dias;

        String diaStr = ""+dia;

        if (diaStr.length()==1){
            diaStr = "0"+diaStr;
        }

        String [] datos = {diaStr,mes,anno};

        datos = verificar(datos);


        String ruta = datos[2] + "/" + datos[1] + "/" + datos[0];

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

    public int getIntAnno(){
        return StringToInt(getAnno(fecha));
    }

    public int getIntMes(){
        return StringToInt(getMes(fecha));
    }
    public int getIntDia(){
        return StringToInt(getDia(fecha));
    }

    public String [] verificar(String [] datos){
        //Si llego al dia 0, paso al mes anterior
        if(datos[0].equals("00")){
            //Paso al mes anterior
            String newMes = ""+(StringToInt(datos[1])-1);

            //Si el mes tiene menos de 1 digito, le añado un 0
            if(newMes.length()==1){
                newMes = "0"+newMes;
            }
            //Si llego al mes 0, paso al 31 de diciembre del año anterior
             else if(newMes.equals("00")){
                newMes = "12";
                datos[2] = ""+(StringToInt(datos[2])-1);
            }

            //Paso al ultimo dia del mes anterior
            datos[0] = getDiaMesAnterior(newMes);

            datos[1] = newMes;
        }

        return datos;
    }

    public String getDiaMesAnterior(String mes){
        Map<String,String> diccionario = new HashMap<>();
        diccionario.put("01","31"); //Enero
        diccionario.put("02","28"); //Febrero
        diccionario.put("03","31"); //Marzo
        diccionario.put("04","30"); //Abril
        diccionario.put("05","31"); //Mayo
        diccionario.put("06","30"); //Junio
        diccionario.put("07","31"); //Julio
        diccionario.put("08","31"); //Agosto
        diccionario.put("09","30"); //Septiembre
        diccionario.put("10","31"); //Octubre
        diccionario.put("11","30"); //Noviembre
        diccionario.put("12","31"); //Diciembre

        return diccionario.get(mes);

    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getDiaSemana(String string) throws ParseException {

        DateFormat format = new SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH);
        Date date = format.parse(string);
        return traduccionDia(date.toString().substring(0,3));
    }

    public String traduccionDia(String string){
        switch (string){
            case "Mon":
                return "L";
            case "Tue":
                return "M";
            case "Wed":
                return "X";
            case "Thu":
                return "J";
            case "Fri":
                return "V";
            case "Sat":
                return "S";
            case "Sun":
                return "D";
            default:
                return "No se";
        }
    }

    public String sumarMesRuta(int suma){
        int mes = StringToInt(getMes(fecha)) + suma;

        int anno = StringToInt(getAnno(fecha));

        if(mes>12){
            mes = 1;
            anno = anno +1;
        }else if(mes==0){
            mes = 12;
            anno = anno -1;
        }
        String mesStr = mes + "";
        if(mesStr.length()==1){
            mesStr = "0" + mesStr;
        }
        this.mes = mesStr;
        this.anno = anno + "";
        return anno+"/"+mesStr;
    }

}
