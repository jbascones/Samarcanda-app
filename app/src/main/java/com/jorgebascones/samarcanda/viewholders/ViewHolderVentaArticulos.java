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

public class ViewHolderVentaArticulos extends RecyclerView.ViewHolder {

    private TextView label1, label2;
    private ImageView imageView;

    public ViewHolderVentaArticulos(View v) {
        super(v);
        imageView = (ImageView) v.findViewById(R.id.id_element_icon_a);
        label1 = (TextView) v.findViewById(R.id.id_element_id_a);
        label2 = (TextView) v.findViewById(R.id.id_element_nombre_a);

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

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }
}
