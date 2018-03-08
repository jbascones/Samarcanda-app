package com.jorgebascones.samarcanda;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jorgebascones.samarcanda.Modelos.Articulo;
import com.jorgebascones.samarcanda.Modelos.CarritoItem;
import com.jorgebascones.samarcanda.Modelos.Categoria;
import com.jorgebascones.samarcanda.Modelos.Celda;
import com.jorgebascones.samarcanda.Modelos.Fecha;
import com.jorgebascones.samarcanda.Modelos.Tiempo;
import com.jorgebascones.samarcanda.Modelos.Venta;
import com.jorgebascones.samarcanda.viewholders.ViewHolderArticulo;
import com.jorgebascones.samarcanda.viewholders.ViewHolderCategoria;
import com.jorgebascones.samarcanda.viewholders.ViewHolderDataNull;
import com.jorgebascones.samarcanda.viewholders.ViewHolderTiempo;
import com.jorgebascones.samarcanda.viewholders.ViewHolderVenta;
import com.jorgebascones.samarcanda.viewholders.ViewHolderVentaArticulos;
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
    ArrayList<Categoria> categorias;
    ArrayList<Articulo> articulos;
    ArrayList<Celda> celdas;

    /***** Creating OnItemClickListener *****/

    // Define listener member variable
    private OnItemClickListener listener;
    // Define the listener interface
    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }
    // Define the method that allows the parent activity or fragment to define the listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


    private final int VENTA = 0, NULL = 1, TIEMPO = 2, CARRITO = 3, CATEGORIA = 4, ARTICULO=5, CELDA= 6;

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
        }else if (items.get(position) instanceof CarritoItem) {
            return CARRITO;
        }else if (items.get(position) instanceof Categoria) {
            return CATEGORIA;
        }else if (items.get(position) instanceof Articulo) {
            return ARTICULO;
        }else if (items.get(position) instanceof Celda) {
            return CELDA;
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
            case CARRITO:
                View v4 = inflater.inflate(R.layout.list_element_a, viewGroup, false);
                convertirATipo("Carrito");
                viewHolder = new ViewHolderVentaArticulos(v4);
                break;
            case CATEGORIA:
                View v5 = inflater.inflate(R.layout.viewholder_categoria, viewGroup, false);
                convertirATipo("Categoria");
                viewHolder = new ViewHolderCategoria(v5, listener);
                break;
            case ARTICULO:
                View v6 = inflater.inflate(R.layout.viewholder_articulo, viewGroup, false);
                convertirATipo("Articulo");
                viewHolder = new ViewHolderArticulo(v6, listener);
                break;
            case CELDA:
                View v7 = inflater.inflate(R.layout.viewholder_articulo, viewGroup, false);
                viewHolder = new ViewHolderArticulo(v7, listener);
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
            vh1.getLabel1().setText("Articulo: " + venta.getNombresArticulos());
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
        vh3.getLabel4().setText(tiempo.getTemperatura()+"ºC");
        Context c = vh3.getLabel1().getContext();

        Picasso.with(c).load(tiempo.getPhotoURL()).into(vh3.getIcono());

    }

    private void configureViewHolderVentaArticulos(ViewHolderVentaArticulos vh4, int position) {
        CarritoItem c = (CarritoItem) items.get(position);
        Articulo a = c.getCarritoItem();
        vh4.getLabel1().setText("Articulo");
        vh4.getLabel2().setText(a.getNombre());
        Context context = vh4.getLabel1().getContext();

        Picasso.with(context).load(a.getFotoUrl()).into(vh4.getImageView());

    }

    private void configureViewHolderCategoria(ViewHolderCategoria vh5, int position) {
        Categoria c = (Categoria) items.get(position);
        vh5.getLabel1().setText(c.getNombre());
        vh5.getIcono().setImageDrawable(ContextCompat.getDrawable(vh5.getIcono().getContext(), R.drawable.inicio));
        Context context = vh5.getLabel1().getContext();

        Picasso.with(context).load(c.getUrlFoto()).into(vh5.getIcono());

    }

    private void configureViewHolderArticulo(ViewHolderArticulo vh6, int position) {
        Articulo a = (Articulo) items.get(position);
        String nombre = a.getNombre();
        vh6.getLabel1().setText(nombre);
        vh6.getIcono().setImageDrawable(ContextCompat.getDrawable(vh6.getIcono().getContext(), R.drawable.inicio));
        Context context = vh6.getLabel1().getContext();

        Picasso.with(context).load(a.getFotoUrl()).into(vh6.getIcono());

    }
    private void configureViewHolderCelda(ViewHolderArticulo vh7, int position) {
        Celda c = (Celda) items.get(position);
        String texto = c.getTexto();
        vh7.getLabel1().setText(texto);
        vh7.getIcono().setImageDrawable(ContextCompat.getDrawable(vh7.getIcono().getContext(), R.drawable.inicio));
        Context context = vh7.getLabel1().getContext();

        if(c.getTipo().equals("Elegir foto galeria")){
            vh7.getIcono().setImageResource(R.drawable.ic_menu_camera);
        }else{
            Picasso.with(context).load(c.getFotoUrl()).resize(600,600).into(vh7.getIcono());
        }


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
            case CARRITO:
                ViewHolderVentaArticulos vh4 = (ViewHolderVentaArticulos) viewHolder;
                configureViewHolderVentaArticulos(vh4, position);
                break;
            case CATEGORIA:
                ViewHolderCategoria vh5 = (ViewHolderCategoria) viewHolder;
                configureViewHolderCategoria(vh5, position);
                break;
            case ARTICULO:
                ViewHolderArticulo vh6 = (ViewHolderArticulo) viewHolder;
                configureViewHolderArticulo(vh6, position);
                break;
            case CELDA:
                ViewHolderArticulo vh7 = (ViewHolderArticulo) viewHolder;
                configureViewHolderCelda(vh7, position);
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
            case "Categoria":
                categorias = new ArrayList<>();
                for (int i=0; i<items.size();i++){
                    categorias.add((Categoria) items.get(i));
                }

                break;
            case "Articulo":
                articulos = new ArrayList<>();
                for (int i=0; i<items.size();i++){
                    articulos.add((Articulo) items.get(i));
                }

                break;

        }
    }

    public void nuevoItem(int posicion){
        notifyItemInserted(posicion);
    }

    public void swap(ArrayList<Object> nuevaLista){

        if(nuevaLista == null || nuevaLista.size()==0)
            return;
        if (items != null && items.size()>0)
            items.clear();
        items.addAll(nuevaLista);
        notifyDataSetChanged();

    }




}