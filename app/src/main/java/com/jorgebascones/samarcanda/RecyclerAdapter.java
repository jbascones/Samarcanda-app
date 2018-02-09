package com.jorgebascones.samarcanda;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static java.security.AccessController.getContext;

/**
 * Created by jorgebascones on 3/11/17.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private ArrayList<Comentario> values;
    RecyclerAdapter(ArrayList<Comentario> values) {
        this.values = values;
    }

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.bubble_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.itemTitle.setText(values.get(position).getUserName());
        holder.itemDetail.setText(values.get(position).getCuerpo());
        /*
        ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) holder.itemDetail
                .getLayoutParams();

        mlp.setMargins(0, 0, 0, 0); */
        if (values.get(position).getUserId().equals("WD7FYyDCJ0QLPZy62zoQNxU8mng1")) {
            //holder.fondo.getBackground().setColorFilter(Color.parseColor("#ffa000"), PorterDuff.Mode.DARKEN);
            holder.fondo.setBackgroundColor(Color.parseColor("#ffa000"));
        }

        Picasso.with(holder.itemImage.getContext()).load(values.get(position).getUserFoto()).into(holder.itemImage);
    }

    @Override
    public int getItemCount() {
        return values.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView itemImage;
        public TextView itemTitle;
        public TextView itemDetail;
        public View fondo;
        ViewHolder(View itemView) {
            super(itemView);
            fondo =  itemView.findViewById(R.id.card_view);
            itemImage = (ImageView)itemView.findViewById(R.id.item_image);
            itemTitle = (TextView)itemView.findViewById(R.id.item_title);
            itemDetail = (TextView)itemView.findViewById(R.id.item_detail);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    int position = getAdapterPosition();

                    Snackbar.make(v, "Click detected on item " + position,
                            Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    if(soyAdmin()) {
                        dialogo(v, values.get(position), position);
                    }

                }
            });
        }

    }

    public void dialogo(View v, final Comentario comentario, final int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        builder.setMessage("¿Quieres borrar este comentario?")
                .setCancelable(false)
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        borrarComentario(comentario.getComentarioKey(), position);

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void borrarComentario(String comentarioKey, int position){
        myRef.child("/mensajes/"+comentarioKey).setValue(null);
        values.remove(position);
    }

    public boolean soyAdmin(){
        FirebaseUser user;
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user.getUid().equals("WD7FYyDCJ0QLPZy62zoQNxU8mng1")){
            return true;
        }
        return false;
    }



}
