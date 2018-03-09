package com.jorgebascones.samarcanda.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
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

    private TextView label1, label2, label3, label4;
    private ImageView imageView;
    private Button button;

    public ViewHolderVentaArticulos(View v) {
        super(v);
        imageView = (ImageView) v.findViewById(R.id.id_element_icon_a);
        label1 = (TextView) v.findViewById(R.id.id_element_id_a);
        label2 = (TextView) v.findViewById(R.id.id_element_nombre_a);
        label3 = (TextView) v.findViewById(R.id.id_element_precio);
        label4 = (TextView) v.findViewById(R.id.id_unidades);
        button = (Button) v.findViewById(R.id.id_cancelar_articulo);

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

    public TextView getLabel3() {
        return label3;
    }

    public void setLabel3(TextView label3) {
        this.label3 = label3;
    }

    public TextView getLabel4() {
        return label4;
    }

    public void setLabel4(TextView label4) {
        this.label4 = label4;
    }

    public Button getButton() {
        return button;
    }

    public void setButton(Button button) {
        this.button = button;
    }
}
