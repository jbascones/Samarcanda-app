package com.jorgebascones.samarcanda.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jorgebascones.samarcanda.Modelos.Articulo;
import com.jorgebascones.samarcanda.Modelos.User;
import com.jorgebascones.samarcanda.Modelos.Venta;
import com.jorgebascones.samarcanda.R;

import java.util.ArrayList;

import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;

/**
 * Created by jorgebascones on 21/2/18.
 */

public class ViewHolderTiempo extends RecyclerView.ViewHolder {

    private TextView label1, label2, label3;
    private ImageView icono;

    public ViewHolderTiempo(View v) {
        super(v);
        label1 = (TextView) v.findViewById(R.id.text1);
        label2 = (TextView) v.findViewById(R.id.text2);
        label3 = (TextView) v.findViewById(R.id.text3);
        icono = (ImageView) v.findViewById(R.id.imageView7);

        v.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                int position = getAdapterPosition();





            }
        });
    }

    public TextView getLabel1() {
        return label1;
    }

    public void setLabel1(TextView label1) {
        this.label1 = label1;
    }

    public TextView getLabel2() {
        return label2;
    }

    public void setLabel2(TextView label2) {
        this.label2 = label2;
    }

    public TextView getLabel3() {
        return label3;
    }

    public ImageView getIcono() {
        return icono;
    }

    public void setIcono(ImageView icono) {
        this.icono = icono;
    }
}

