package com.jorgebascones.samarcanda.Modelos;

/**
 * Created by jorgebascones on 11/11/17.
 */

public class Comentario {

    String userId;

    String cuerpo;

    String userName;

    String userFoto;

    String comentarioKey;


    public Comentario(String user, String cuerpo, String userName, String userFoto){
        this.userId = user;
        this.cuerpo = cuerpo;
        this.userName = userName;
        this.userFoto = userFoto;
    }

    //Es necesario un constructor sin argumentos para que se pueda crear el objeto a partir de los datos bajados
    public Comentario(){

    }

    public String getUserId() {
        return userId;
    }

    public void setUser(String user) {
        this.userId = user;
    }

    public String getCuerpo() {
        return cuerpo;
    }

    public void setCuerpo(String cuerpo) {
        this.cuerpo = cuerpo;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserFoto() {
        return userFoto;
    }

    public void setComentarioKey(String comentarioKey) {this.comentarioKey = comentarioKey;}

    public String getComentarioKey(){return comentarioKey;}


}
