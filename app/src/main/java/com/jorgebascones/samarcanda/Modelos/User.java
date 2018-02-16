package com.jorgebascones.samarcanda.Modelos;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by jorgebascones on 29/10/17.
 */

public class User {

    public String usuarioId;
    public String nombre;
    public String urlFoto;
    public int numeroTelefono;
    public String mail;



    public User() {


        while(rellenarPerfil()){


        }

    }


    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
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

    public int getNumeroTelefono() {
        return numeroTelefono;
    }
    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }



    public void setNumeroTelefono(int numeroTelefono) {
        this.numeroTelefono = numeroTelefono;
    }

    public boolean rellenarPerfil(){
        try {
            FirebaseUser user;
            user = FirebaseAuth.getInstance().getCurrentUser();
            usuarioId = user.getUid();
            nombre = user.getDisplayName();
            urlFoto = user.getPhotoUrl().toString();
            mail = user.getEmail();
            Log.d("Firebase", "Creado user Nombre " + nombre + ". Su userId " + usuarioId);

            if (!user.isEmailVerified()) {
                mandarMail();

            }


            return false;



        } catch (Exception e){

            return true;

        }
    }

    private void mandarMail(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("Firebase", "Email sent.");
                        }
                    }
                });
    }



}
