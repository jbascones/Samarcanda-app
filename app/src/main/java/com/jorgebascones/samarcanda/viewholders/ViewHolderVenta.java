package com.jorgebascones.samarcanda.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.jorgebascones.samarcanda.Modelos.Articulo;
import com.jorgebascones.samarcanda.R;
import com.jorgebascones.samarcanda.Modelos.User;
import com.jorgebascones.samarcanda.Modelos.Venta;

import java.util.ArrayList;

import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;

/**
 * Created by jorgebascones on 12/2/18.
 */

public class ViewHolderVenta extends RecyclerView.ViewHolder {

    private TextView label1, label2, label3;

    public ViewHolderVenta(View v, final ArrayList<Venta> ventas) {
        super(v);
        label1 = (TextView) v.findViewById(R.id.text1);
        label2 = (TextView) v.findViewById(R.id.text2);
        label3 = (TextView) v.findViewById(R.id.text3);

        v.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                int position = getAdapterPosition();


                Articulo aSeleccionado = ventas.get(position).getArticulo();
                User cliente = ventas.get(position).getUser();
                setupDialog(0,aSeleccionado.getNombre(),aSeleccionado.getUnidades(),cliente.getNombre());

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


    //Tipo 1 = cliente, 0 = articulo
    public void setupDialog(final int tipo, final String nombre, final int unidadades, final String nombreCliente){

        final String [] cabecera = {"Articulo","Cliente"};
        final String [] mensaje = {nombre + "\nUnidades restantes: " + unidadades,nombreCliente};
        String txtBoton = "Articulo";
        if (tipo== 0){
            txtBoton = "Cliente";
        }
        final PrettyDialog dialog = new PrettyDialog(label1.getContext());
        dialog
                .setTitle(cabecera[tipo])
                .setTitleColor(R.color.pdlg_color_blue)
                .setMessage(mensaje[tipo])
                .setMessageColor(R.color.pdlg_color_black)
                .setAnimationEnabled(true)
                .setIcon(R.drawable.pdlg_icon_info, R.color.colorPrimaryDark, new PrettyDialogCallback() {
                    @Override
                    public void onClick() {
                        dialog.dismiss();

                    }
                })
                .addButton("Ver "+txtBoton, R.color.pdlg_color_white, R.color.pdlg_color_blue, new PrettyDialogCallback() {
                    @Override
                    public void onClick() {
                        int nuevoTipo = 0;
                        if (tipo== 0){
                            nuevoTipo = 1;
                        }
                        setupDialog(nuevoTipo,nombre,unidadades,nombreCliente);
                        dialog.dismiss();

                    }
                })
                .addButton("Cerrar", R.color.pdlg_color_black, R.color.pdlg_color_gray, new PrettyDialogCallback() {
                    @Override
                    public void onClick() {
                        dialog.dismiss();

                    }
                }).show();



    }
}
