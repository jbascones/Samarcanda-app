package com.jorgebascones.samarcanda.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jorgebascones.samarcanda.ComplexRecyclerViewAdapter;
import com.jorgebascones.samarcanda.R;

/**
 * Created by jorgebascones on 26/2/18.
 */

public class ViewHolderCategoria extends RecyclerView.ViewHolder {

    private TextView label1;
    private ImageView icono;


    public ViewHolderCategoria(View v,final ComplexRecyclerViewAdapter.OnItemClickListener listener) {
        super(v);
        label1 = (TextView) v.findViewById(R.id.text1);
        icono = (ImageView) v.findViewById(R.id.imageView7);

        v.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                int position = getAdapterPosition();


            }
        });

        itemView.setOnClickListener(new View.OnClickListener() {
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

    public ImageView getIcono() {
        return icono;
    }

    public void setIcono(ImageView icono) {
        this.icono = icono;
    }

}

