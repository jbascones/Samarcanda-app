package com.jorgebascones.samarcanda.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jorgebascones.samarcanda.ComplexRecyclerViewAdapter;
import com.jorgebascones.samarcanda.R;

/**
 * Created by jorgebascones on 19/5/18.
 */

public class ViewHolderPost extends RecyclerView.ViewHolder {

    private TextView titulo;
    private TextView cuerpo;
    private ImageView icono;


    public ViewHolderPost(View v) {
        super(v);
        titulo = (TextView) v.findViewById(R.id.text1);
        cuerpo = (TextView) v.findViewById(R.id.text2);
        icono = (ImageView) v.findViewById(R.id.imageView6);


    }


    public TextView getTitulo() {
        return titulo;
    }

    public void setTitulo(TextView titulo) {
        this.titulo = titulo;
    }

    public TextView getCuerpo() {
        return cuerpo;
    }

    public void setCuerpo(TextView cuerpo) {
        this.cuerpo = cuerpo;
    }

    public ImageView getIcono() {
        return icono;
    }

    public void setIcono(ImageView icono) {
        this.icono = icono;
    }

}

