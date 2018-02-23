package com.jorgebascones.samarcanda;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jorgebascones.samarcanda.Modelos.Articulo;
import com.jorgebascones.samarcanda.Modelos.Fecha;
import com.jorgebascones.samarcanda.Modelos.User;
import com.jorgebascones.samarcanda.Modelos.Venta;

import java.util.ArrayList;

import pl.droidsonroids.gif.GifImageView;


/**
 * A simple {@link Fragment} subclass.
 */

public class VerVentasFragment extends Fragment {


    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;
    public ArrayList<Venta> values = new ArrayList<Venta>();
    public ArrayList<String> keys = new ArrayList<String>();
    String TAG = "verVentas";
    public ArrayList<User> clientes = new ArrayList<User>();
    public ArrayList<Articulo> articulos = new ArrayList<Articulo>();
    Fecha fecha;
    String fechaBuscada;
    GifImageView gif;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_ver_ventas, container, false);

        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Ver Ventas");

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        new GetDataFromFirebase().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        fecha = new Fecha();
        fechaBuscada = fecha.getRutaVenta();
        descargarListaVentas(fechaBuscada);

        gif = (GifImageView) view.findViewById(R.id.gifImageView);

        setListeners(view);


        return view;
    }

    public void addValue(String key, Venta newValue){

        boolean add = true;

        for(int i = 0;i<keys.size();i++){
            if (keys.get(i).equals(key)){
                add = false;
            }
        }

        if (add){
            newValue.setVentaKey(key);
            values.add(newValue);
            keys.add(key);
        }




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
        ArrayList<Object> listaValores = new ArrayList<>();
        for (int i=0;i<values.size();i++){
            listaValores.add(values.get(i));
        }
        recyclerView.setAdapter(new ComplexRecyclerViewAdapter(listaValores));

        cargando(false);

    }

    private void bindNullDataToAdapter() {
        // Bind adapter to recycler view object
        ArrayList<Object> listaValores = new ArrayList<>();
        listaValores.add("No hay ninguna venta en esa fecha");
        recyclerView.setAdapter(new ComplexRecyclerViewAdapter(listaValores));
        cargando(false);

    }


    public void rellenarListaVentas(){

        for(int i=0;i<values.size();i++){
            bajarDatosArticulos(values.get(i).getArticuloId());
            bajarDatosClientes(values.get(i).getClienteId());
        }
    }

    public void bajarDatosClientes(String ruta){
        DatabaseReference myRef = database.getReference("users/perfiles/"+ruta);

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                User cliente = dataSnapshot.getValue(User.class);
                clientes.add(cliente);
                comprobarDatosBajados();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    public void bajarDatosArticulos(String ruta){
        DatabaseReference myRef = database.getReference("articulos/"+ruta);

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Articulo articulo = dataSnapshot.getValue(Articulo.class);
                articulos.add(articulo);
                comprobarDatosBajados();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    public void comprobarDatosBajados(){
        if(clientes.size()==values.size() && articulos.size()==values.size()){
            for(int i =0;i<values.size();i++){
                values.get(i).setUser(clientes.get(i));
                values.get(i).setArticulo(articulos.get(i));
            }
                bindDataToAdapter();
        }
    }

    public void descargarListaVentas(String ruta){

        // Read from the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference myRef = database.getReference("/ventas/"+ruta);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                    Log.v("Funciona",""+ childDataSnapshot.getKey()); //displays the key for the node
                    Log.v("Funciona",""+ childDataSnapshot.getValue());
                    addValue(childDataSnapshot.getKey(), childDataSnapshot.getValue(Venta.class));


                }

                if(values.size()==0){
                    bindNullDataToAdapter();
                }

                rellenarListaVentas();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                System.out.println("Failed to read value." + error.toException());
            }
        });
    }



    public void setListeners(View view){
        Button buttonDiaAnterior = (Button) view.findViewById(R.id.id_dia_anterior);
        Button buttonDiaSiguiente = (Button) view.findViewById(R.id.dia_siguiente);
        final Button buttonDiaActual = (Button) view.findViewById(R.id.dia_actual);

        buttonDiaAnterior.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Cuando se vayan a bajar las ventas, el gif debe aparecer para mostrar actividad de red
                cargando(true);

                values.clear();
                //TODO: Cache para guardar artículos y clientes. Por ej con un mapping
                articulos.clear();
                clientes.clear();
                keys.clear();
                fechaBuscada = fecha.sumarDias(fechaBuscada, -1);
                descargarListaVentas(fechaBuscada);
                buttonDiaActual.setText(fecha.fechaPreparada(fechaBuscada, false));


            }
        });

        buttonDiaSiguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Cuando se vayan a bajar las ventas, el gif debe aparecer para mostrar actividad de red
                cargando(true);

                values.clear();
                //TODO: Cache para guardar artículos y clientes. Por ej con un mapping
                articulos.clear();
                clientes.clear();
                keys.clear();
                fechaBuscada = fecha.sumarDias(fechaBuscada, +1 );
                descargarListaVentas(fechaBuscada);
                buttonDiaActual.setText(fecha.fechaPreparada(fechaBuscada, false));


            }
        });


    }

    public void cargando(boolean cargando){
        if (cargando){
            gif.setVisibility(View.VISIBLE);
        }else{
            gif.setVisibility(View.INVISIBLE);
        }
    }


}





