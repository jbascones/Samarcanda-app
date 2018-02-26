package com.jorgebascones.samarcanda.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jorgebascones.samarcanda.OnItemClickListener;
import com.jorgebascones.samarcanda.R;

/**
 * Created by jorgebascones on 26/2/18.
 */

public class ViewHolderCategoria extends RecyclerView.ViewHolder implements View.OnClickListener{

    private TextView label1;
    private ImageView icono;
    private OnItemClickListener mListener;

    public ViewHolderCategoria(View v) {
        super(v);
        label1 = (TextView) v.findViewById(R.id.text1);
        icono = (ImageView) v.findViewById(R.id.imageView7);
        v.setOnClickListener(this);

        v.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                int position = getAdapterPosition();





            }
        });
    }

    public ViewHolderCategoria(View v, OnItemClickListener listener) {
        this(v);
        mListener = listener;
    }

    @Override
    public void onClick(View v) {
        if (mListener != null) {
            mListener.onItemClick(v, getPosition());
        }
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

