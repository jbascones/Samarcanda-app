package com.jorgebascones.samarcanda.Modelos;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jorgebascones on 11/3/18.
 */

public class Reserva {

    public List<String> articulos;
    public String estado;
    public String userId;
    public String userName;
    public String textoArticulos;
    public String identificador;





    //Es necesario un constructor sin argumentos para que se pueda crear el objeto a partir de los datos bajados
    public Reserva(){
        articulos = new ArrayList<String>();
    }

    public List<String> getArticulos() {
        return articulos;
    }

    public void setArticulos(List<String> articulos) {
        this.articulos = articulos;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void addArticulo(String articuloId){
        articulos.add(articuloId);
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTextoArticulos() {
        return textoArticulos;
    }

    public void setTextoArticulos(String textoArticulos) {
        this.textoArticulos = textoArticulos;
    }

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }
}
