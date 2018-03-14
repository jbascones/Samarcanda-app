package com.jorgebascones.samarcanda.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jorgebascones.samarcanda.ComplexRecyclerViewAdapter;
import com.jorgebascones.samarcanda.Modelos.Venta;
import com.jorgebascones.samarcanda.R;

import java.util.ArrayList;

import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;

/**
 * Created by jorgebascones on 14/3/18.
 */

public class ViewHolderReserva extends RecyclerView.ViewHolder {

    private TextView label1, label2, label3;
    private Button button;

    public ViewHolderReserva(View v,final ComplexRecyclerViewAdapter.OnItemClickListener listener) {
        super(v);
        label1 = (TextView) v.findViewById(R.id.text1);
        label2 = (TextView) v.findViewById(R.id.text2);
        label3 = (TextView) v.findViewById(R.id.text3);
        button = (Button) v.findViewById(R.id.button5);

        button.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                int position = getAdapterPosition();


            }
        });
        //button y no itemview porque queremos que se notifique cuando pulsamos el boton y no la celda
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Triggers click upwards to the adapter on click
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(itemView, position);
                    }
                }
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

    public void setLabel3(TextView label3) {
        this.label3 = label3;
    }

    public Button getButton() {
        return button;
    }

    public void setButton(Button button) {
        this.button = button;
    }
}
