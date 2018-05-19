package com.jorgebascones.samarcanda.Modelos;

/**
 * Created by jorgebascones on 19/5/18.
 */

public class Post {


    String cuerpo;

    String titulo;

    String urlImagen;

    String comentarioKey;


    public Post(String titulo, String cuerpo, String urlImagen){
        this.cuerpo = cuerpo;
        this.titulo=titulo;
        this.urlImagen=urlImagen;

    }

    //Es necesario un constructor sin argumentos para que se pueda crear el objeto a partir de los datos bajados
    public Post(){

    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getUrlImagen() {
        return urlImagen;
    }

    public void setUrlImagen(String urlImagen) {
        this.urlImagen = urlImagen;
    }

    public String getCuerpo() {
        return cuerpo;
    }

    public void setCuerpo(String cuerpo) {
        this.cuerpo = cuerpo;
    }

    public void setComentarioKey(String comentarioKey) {this.comentarioKey = comentarioKey;}

    public String getComentarioKey(){return comentarioKey;}


}
