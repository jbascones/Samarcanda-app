package com.jorgebascones.samarcanda;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jorgebascones.samarcanda.Modelos.Fecha;
import com.jorgebascones.samarcanda.Modelos.Tiempo;
import com.jorgebascones.samarcanda.Modelos.Venta;
import com.jorgebascones.samarcanda.viewholders.ViewHolderDataNull;
import com.jorgebascones.samarcanda.viewholders.ViewHolderTiempo;
import com.jorgebascones.samarcanda.viewholders.ViewHolderVenta;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by jorgebascones on 12/2/18.
 */

public class ComplexRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // The items to display in your RecyclerView
    private ArrayList<Object> items;
    ArrayList<Venta> ventas;
    ArrayList<String> textoNull;
    Tiempo tiempo;

    private final int VENTA = 0, NULL = 1, TIEMPO = 2;

    // Provide a suitable constructor (depends on the kind of dataset)
    public ComplexRecyclerViewAdapter(ArrayList<Object> items) {
        this.items = items;

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return this.items.size();
    }

    //Devuelve el tipo de view del item en la posicion para el reciclado
    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof Venta) {
            return VENTA;
        } else if (items.get(position) instanceof String) {
            return NULL;
        } else if (items.get(position) instanceof Tiempo) {
            return TIEMPO;
        }
        return -1;
    }

    /**
     * Este metodo crea diferentes objetos RecyclerView.ViewHolder  basados en  el item view type.\
     *
     * @param viewGroup ViewGroup contenedor del item
     * @param viewType tipo del view que se va a "inflar"
     * @return viewHolder que se va a "inflar"
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        switch (viewType) {
            case VENTA:
                View v1 = inflater.inflate(R.layout.viewholder_venta, viewGroup, false);
                convertirATipo("Ventas");
                viewHolder = new ViewHolderVenta(v1,ventas);
                break;
            case NULL:
                View v2 = inflater.inflate(R.layout.viewholder_data_null, viewGroup, false);
                convertirATipo("Texto");
                viewHolder = new ViewHolderDataNull(v2, textoNull);
                break;
            case TIEMPO:
                View v3 = inflater.inflate(R.layout.viewholder_tiempo, viewGroup, false);
                convertirATipo("Tiempo");
                viewHolder = new ViewHolderTiempo(v3);
                break;
            default:
                v1 = inflater.inflate(R.layout.viewholder_venta, viewGroup, false);
                viewHolder = new ViewHolderVenta(v1,ventas);
                break;
        }
        return viewHolder;
    }
    private void configureViewHolderVenta(ViewHolderVenta vh1, int position) {
        Venta venta = (Venta) items.get(position);
        if (venta != null) {
            vh1.getLabel1().setText("Articulo: " + venta.getArticulo().getNombre());
            vh1.getLabel2().setText("Cliente: " + venta.getUser().getNombre());
            Fecha fecha = new Fecha();
            vh1.getLabel3().setText("Fecha: "+fecha.fechaPreparada(venta.getFecha(), true));
        }
    }

    private void configureViewHolderDataNull(ViewHolderDataNull vh2, int position) {
        String texto = (String) items.get(position);
        if (texto != null) {
            vh2.getLabel1().setText(texto);

        }
    }

    private void configureViewHolderTiempo(ViewHolderTiempo vh3, int position) {

        vh3.getLabel1().setText(tiempo.getTitulo());
        vh3.getLabel2().setText(tiempo.getSubtitulo());
        Context c = vh3.getLabel1().getContext();

        Picasso.with(c).load(tiempo.getPhotoURL()).into(vh3.getIcono());


    }

    /**
     * This method internally calls onBindViewHolder(ViewHolder, int) to update the
     * RecyclerView.ViewHolder contents with the item at the given position
     * and also sets up some private fields to be used by RecyclerView.
     *
     * @param viewHolder The type of RecyclerView.ViewHolder to populate
     * @param position Item position in the viewgroup.
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch (viewHolder.getItemViewType()) {
            case VENTA:
                ViewHolderVenta vh1 = (ViewHolderVenta) viewHolder;
                configureViewHolderVenta(vh1, position);
                break;
            case NULL:
                ViewHolderDataNull vh2 = (ViewHolderDataNull) viewHolder;
                configureViewHolderDataNull(vh2, position);
                break;
            case TIEMPO:
                ViewHolderTiempo vh3 = (ViewHolderTiempo) viewHolder;
                configureViewHolderTiempo(vh3, position);
                break;
            default:
                vh1 = (ViewHolderVenta) viewHolder;
                configureViewHolderVenta(vh1, position);
                break;
        }
    }

    public void convertirATipo(String tipo){
        switch (tipo){
            case "Ventas":
                ventas = new ArrayList<>();
                for (int i=0; i<items.size();i++){
                    ventas.add((Venta)items.get(i));
                }
                break;
            case "Texto":
                textoNull = new ArrayList<>();
                for (int i=0; i<items.size();i++){
                    textoNull.add((String) items.get(i));
                }
                break;
            case "Tiempo":
                tiempo =(Tiempo) items.get(0);

                break;

        }
    }

}