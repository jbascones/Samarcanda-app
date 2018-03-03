package com.jorgebascones.samarcanda;


import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ftoslab.openweatherretrieverz.CurrentWeatherInfo;
import com.ftoslab.openweatherretrieverz.DailyForecastCallback;
import com.ftoslab.openweatherretrieverz.DailyForecastInfo;
import com.ftoslab.openweatherretrieverz.OpenWeatherRetrieverZ;
import com.ftoslab.openweatherretrieverz.WeatherCallback;
import com.ftoslab.openweatherretrieverz.WeatherUnitConverter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jorgebascones.samarcanda.Modelos.Articulo;
import com.jorgebascones.samarcanda.Modelos.Categoria;
import com.jorgebascones.samarcanda.Modelos.Fecha;
import com.jorgebascones.samarcanda.Modelos.Tiempo;
import com.jorgebascones.samarcanda.Modelos.Venta;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;
import pl.droidsonroids.gif.GifImageView;

import static java.lang.Math.round;
import static java.lang.Math.subtractExact;


/**
 * A simple {@link Fragment} subclass.
 */

public class CatalogoFragment extends Fragment {


    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    ComplexRecyclerViewAdapter adapter;
    public ArrayList<String> keys = new ArrayList<String>();
    public ArrayList<String> keysArticulos = new ArrayList<String>();
    public ArrayList<Object> categorias = new ArrayList<>();
    public ArrayList<Object> articulos = new ArrayList<>();
    public ArrayList<Object> data = new ArrayList<>();
    public ArrayList<Object> carrito = new ArrayList<>();
    public String categoriaElegida;
    public Articulo articuloElegido;
    Button atras;
    Button botonCarrito;
    TextView cabecera;
    final String TAG = "catalogo";
    Fecha fecha;
    GifImageView gif;
    Dialog dialog;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_catalogo, container, false);

        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Catálogo");

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false));


        new GetDataFromFirebase().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        fecha = new Fecha();

        gif = (GifImageView) view.findViewById(R.id.gifImageView);

        descargarListaCategorias();

        atras = (Button) view.findViewById(R.id.id_bn_atras);
        cabecera = (TextView) view.findViewById(R.id.textView6);
        botonCarrito = (Button) view.findViewById(R.id.button2);
        botonCarrito.setVisibility(View.INVISIBLE);

        categoriaElegida= "CATEGORIAS";
        visibilityAtras(false);

        setClickBotonAtras(view);
        setListenerBotonCarrito();


        return view;
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    //TODO: como funciona GetDataFromFirebase??
    private class GetDataFromFirebase extends AsyncTask<Void,Void,Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
        }
    }

    private void bindDataToAdapter() {
        // Bind adapter to recycler view object

        adapter = new ComplexRecyclerViewAdapter(data);

        adapter.setOnItemClickListener(new ComplexRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(categoriaElegida.equals("CATEGORIAS")) {
                    Categoria categoriaSeleccionada = (Categoria) categorias.get(position);
                    //Toast.makeText(getActivity(), categoriaSeleccionada.getNombre() + " seleccionada!", Toast.LENGTH_SHORT).show();
                    articulos.clear();
                    keysArticulos.clear();
                    categoriaElegida = categoriaSeleccionada.getNombre();
                    descargarListaArticulos(categoriaSeleccionada.getRuta());
                    setCabecera();
                }else{
                    articuloElegido = (Articulo)articulos.get(position);
                    showDialog();
                }
            }
        });

        recyclerView.setAdapter(adapter);

        cargando(false);

    }

    private void bindNullDataToAdapter() {
        // Bind adapter to recycler view object
        ArrayList<Object> listaValores = new ArrayList<>();
        listaValores.add("No hay noticias disponibles");
        recyclerView.setAdapter(new ComplexRecyclerViewAdapter(listaValores));
        cargando(false);

    }




    public void cargando(boolean cargando){
        if (cargando){
            gif.setVisibility(View.VISIBLE);
        }else{
            gif.setVisibility(View.INVISIBLE);
        }
    }

    public void descargarListaCategorias(){

        // Read from the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference myRef = database.getReference("/articulos/categorias/");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                    Log.v(TAG,""+ childDataSnapshot.getKey()); //displays the key for the node
                    Log.v(TAG,""+ childDataSnapshot.getValue());
                    addValue(childDataSnapshot.getKey(), childDataSnapshot.getValue(Categoria.class));

                }

                if(categorias.size()==0){
                    bindNullDataToAdapter();
                }
                data.addAll(categorias);
                bindDataToAdapter();

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                System.out.println("Failed to read value." + error.toException());
            }
        });
    }

    public void addValue(String key, Categoria newValue){

        boolean add = true;

        for(int i = 0;i<keys.size();i++){
            if (keys.get(i).equals(key)){
                add = false;
            }
        }

        if (add){
            categorias.add(newValue);
            keys.add(key);
        }

    }
    public void descargarListaArticulos(String ruta){

        // Read from the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference myRef = database.getReference("/articulos/"+ruta);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                    Log.v(TAG,""+ childDataSnapshot.getKey()); //displays the key for the node
                    Log.v(TAG,""+ childDataSnapshot.getValue());
                    addArticulo(childDataSnapshot.getKey(), childDataSnapshot.getValue(Articulo.class));

                }

                if(categorias.size()==0){
                    bindNullDataToAdapter();
                }


                ArrayList<Object> nuevoData = new ArrayList<>();
                nuevoData.addAll(articulos);
                visibilityAtras(true);

                adapter.swap(nuevoData);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                System.out.println("Failed to read value." + error.toException());
            }
        });
    }

    public void addArticulo(String key, Articulo newValue){

        boolean add = true;

        for(int i = 0;i<keysArticulos.size();i++){
            if (keysArticulos.get(i).equals(key)){
                add = false;
            }
        }

        if (add){
            articulos.add(newValue);
            keysArticulos.add(key);
        }

    }

    public void setClickBotonAtras(View v){
        atras = (Button) v.findViewById(R.id.id_bn_atras);
        atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.swap(categorias);
                visibilityAtras(false);
                categoriaElegida = "CATEGORIAS";
                setCabecera();
                try{
                    if(carrito.size()>0){
                        gestionBotonCarrito();
                    }
                }catch (Exception e){

                }

            }
        });
    }

    public void visibilityAtras(Boolean visible){
            atras.setText("Atrás");
        if(visible){
            atras.setVisibility(View.VISIBLE);
        }else {
            atras.setVisibility(View.INVISIBLE);
        }
    }

    public void setCabecera(){
        cabecera.setText(categoriaElegida);
    }

    //Codigo del pop up cuando seleccionas un articulo

    private void showDialog() {
        // custom dialog
        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.custom_dialog);

        // set the custom dialog components - text, image and button
        ImageButton close = (ImageButton) dialog.findViewById(R.id.btnClose);
        Button buy = (Button) dialog.findViewById(R.id.btnBuy);
        ImageView foto = (ImageView) dialog.findViewById(R.id.foto_pop_up);
        TextView titulo = (TextView) dialog.findViewById(R.id.pop_titulo);
        TextView subtitulo = (TextView) dialog.findViewById(R.id.pop_subtitulo);
        TextView subtitulo2 = (TextView) dialog.findViewById(R.id.pop_subtitulo2);

        // Close Button
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                //TODO Close button action
            }
        });

        // Buy Button
        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                carrito.add(articuloElegido);
                gestionBotonCarrito();
            }
        });

        //Set informacion del articulo
        Picasso.with(foto.getContext()).load(articuloElegido.getFotoUrl()).into(foto);
        titulo.setText(articuloElegido.getNombre());
        subtitulo.setText("Precio: "+articuloElegido.getPrecio()+"€");
        subtitulo2.setText("Unidades: "+articuloElegido.getUnidades());

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.show();
    }

    public void gestionBotonCarrito(){
        botonCarrito.setVisibility(View.VISIBLE);
        botonCarrito.setText("Ir al carrito ("+carrito.size()+" artículos)");

    }

    public void setListenerBotonCarrito(){
        botonCarrito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(categoriaElegida.equals("Carrito")){
                    dialogConfirmar();
                }else{
                    flujoIrCarrito();
                }
            }
        });
    }

    public void flujoIrCarrito(){
        adapter.swap(carrito);
        categoriaElegida = "Carrito";
        setCabecera();
        visibilityAtras(true);
        botonCarrito.setText("Confirmar Reserva");
    }

    public void flujoCompletarReserva(){
        atras.setText("Catálogo");
        ArrayList<Object> texto = new ArrayList<>();
        texto.add("Reserva realizada");
        adapter.swap(texto);
        carrito.clear();
        botonCarrito.setVisibility(View.INVISIBLE);


    }

    public void dialogConfirmar(){
        final PrettyDialog dialog = new PrettyDialog(getContext());
        dialog
                .setTitle("¿Confirmar reserva?")
                .setMessage("El importe a pagar sería de "+ getPrecio()+"€")
                .addButton(
                        "Confirmar reserva",					// button text
                        R.color.pdlg_color_white,		// button text color
                        R.color.pdlg_color_green,		// button background color
                        new PrettyDialogCallback() {		// button OnClick listener
                            @Override
                            public void onClick() {
                                subirReserva();
                                flujoCompletarReserva();
                                dialog.dismiss();
                            }
                        }
                )

                // Cancel button
                .addButton(
                        "Volver",
                        R.color.pdlg_color_white,
                        R.color.pdlg_color_red,
                        new PrettyDialogCallback() {
                            @Override
                            public void onClick() {
                                dialog.dismiss();
                            }
                        }
                )

                // 3rd button
                .addButton(
                        "Borrar reserva",
                        R.color.pdlg_color_black,
                        R.color.pdlg_color_gray,
                        new PrettyDialogCallback() {
                            @Override
                            public void onClick() {
                                carrito.clear();
                                dialog.dismiss();
                            }
                        }
                )
                .show();
    }

    public int getPrecio(){
        int precio = 0;
        for(int i=0;i<carrito.size();i++){
            Articulo a = (Articulo)carrito.get(i);
            precio = precio + a.getPrecio();
        }
        return precio;
    }

    public void subirReserva(){
        Log.d(TAG,"Subiendo reserva");
        Fecha fecha = new Fecha();
        String ruta = fecha.getRutaVenta();
        String hora = fecha.getHora(fecha.getFecha());
        myRef.child("/reservas/"+ruta+"/"+user.getUid()+"/"+hora).setValue(articulosReservados());
        Toast.makeText(getContext(),"Reserva registrada",Toast.LENGTH_SHORT).show();

    }

    public String articulosReservados(){
        String articulosReservados = "";
        for (int i =0; i< carrito.size(); i++){
            Articulo aux = (Articulo) carrito.get(i);
            articulosReservados = articulosReservados + aux.getArticuloId() + "&";
        }
        return articulosReservados;
    }

    public void guardarReservaEnPerfil(){

    }



}





