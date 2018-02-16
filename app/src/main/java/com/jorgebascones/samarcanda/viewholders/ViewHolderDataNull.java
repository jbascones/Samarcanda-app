package com.jorgebascones.samarcanda.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.jorgebascones.samarcanda.Modelos.Articulo;
import com.jorgebascones.samarcanda.Modelos.User;
import com.jorgebascones.samarcanda.Modelos.Venta;
import com.jorgebascones.samarcanda.R;

import java.util.ArrayList;

import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;

/**
 * Created by jorgebascones on 15/2/18.
 */

public class ViewHolderDataNull extends RecyclerView.ViewHolder {

    private TextView label1;

    public ViewHolderDataNull(View v, final ArrayList<String> dato) {
        super(v);
        label1 = (TextView) v.findViewById(R.id.text1);

    }

    public TextView getLabel1() {
        return label1;
    }

    public void setLabel1(TextView label1) {
        this.label1 = label1;
    }





}
