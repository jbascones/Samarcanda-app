package com.jorgebascones.samarcanda.Modelos;

/**
 * Created by jorgebascones on 22/2/18.
 */

public class Tiempo {
    public long temperatura;
    public String descripcion;
    public String photoURL;

    public Tiempo(long temperatura,String descripcion, String photoURL){
        this.temperatura = temperatura;
        this.descripcion = descripcion;
        this.photoURL = photoURL;
    }

    public long getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(int temperatura) {
        this.temperatura = temperatura;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    public String getTitulo(){
        if(temperatura <15){
            return "¿Estás preparado para el frío?";
        }else if(temperatura > 15){
            return "Hace un buen día";
        }else{
            return "Nos alegramos de verte";
        }
    }

    public String getSubtitulo(){
        if(temperatura <15){
            return "Échale un vistazo a nuestros abrigos. Puedes encontrarlos en el Catálogo";
        }else if(temperatura > 15){
            return "Seguro que te sientan genial nuestras camisetas. Puedes encontrarlas en el Catálogo";
        }else{
            return "Seguro que tenemos algo que te guste";
        }
    }
}
