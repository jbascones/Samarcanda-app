package com.jorgebascones.samarcanda;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

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

import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

import static java.lang.Math.round;


/**
 * A simple {@link Fragment} subclass.
 */

public class CatalogoFragment extends Fragment {


    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;
    public ArrayList<String> keys = new ArrayList<String>();
    public ArrayList<String> keysArticulos = new ArrayList<String>();
    public ArrayList<Object> categorias = new ArrayList<>();
    public ArrayList<Object> articulos = new ArrayList<>();
    final String TAG = "catalogo";
    Fecha fecha;
    GifImageView gif;
    CurrentWeatherInfo currentWeatherInfoC;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_catalogo, container, false);

        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Catálogo");

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        new GetDataFromFirebase().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        fecha = new Fecha();

        gif = (GifImageView) view.findViewById(R.id.gifImageView);

        descargarListaCategorias();


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

        recyclerView.setAdapter(new ComplexRecyclerViewAdapter(categorias));

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

                bindDataToAdapter();

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




}





