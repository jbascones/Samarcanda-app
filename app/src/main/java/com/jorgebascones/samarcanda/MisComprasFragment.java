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
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jorgebascones.samarcanda.Modelos.Articulo;
import com.jorgebascones.samarcanda.Modelos.Categoria;
import com.jorgebascones.samarcanda.Modelos.Fecha;
import com.jorgebascones.samarcanda.Modelos.Reserva;
import com.jorgebascones.samarcanda.Modelos.User;
import com.jorgebascones.samarcanda.Modelos.Venta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import pl.droidsonroids.gif.GifImageView;


/**
 * A simple {@link Fragment} subclass.
 */

public class MisComprasFragment extends Fragment {


    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    RecyclerView recyclerView;
    public ArrayList<Venta> values = new ArrayList<Venta>();
    public ArrayList<String> keys = new ArrayList<String>();
    String TAG = "mis compras";
    public ArrayList<User> clientes = new ArrayList<User>();
    public ArrayList<ArrayList<Articulo>> articulos = new ArrayList<>();
    public ArrayList<String> nombresArticulos = new ArrayList<>();
    public ArrayList<Object> listaValores;
    public Map<String,Articulo> catalogo;
    private View v;

    public ComplexRecyclerViewAdapter adapter;

    Fecha fecha;

    GifImageView gif;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_ver_ventas, container, false);

        listaValores = new ArrayList<>();

        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Mis Compras");

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        new GetDataFromFirebase().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        fecha = new Fecha();
        catalogo = new HashMap<>();

        gif = (GifImageView) view.findViewById(R.id.gifImageView);

        descargarListaCompras();

        esconderBotones(view);

        v = view;

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
            newValue.setMiCompra(true);
            values.add(newValue);
            keys.add(key);
        }

        rellenarListaVentas();

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

    public void rellenarListaVentas(){

        for(int i=0;i<values.size();i++){
            ArrayList<String> ids = getIdsArticulos(i);
            for (int j =0; j<ids.size();j++){
                bajarDatosArticulos(ids.get(j),i);
            }
        }
    }

    public void bajarDatosArticulos(String ruta, final int posicion){
        DatabaseReference myRef = database.getReference("articulos/"+ruta);

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Articulo articulo = dataSnapshot.getValue(Articulo.class);



                catalogo.put(articulo.getArticuloId(),articulo);

                rellenarNombresArticulos();


            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    public void comprobarDatosBajados(){
        setNombresArticulos();
        if(articulos.size()==values.size()){
            for(int i =0;i<values.size();i++){
                values.get(i).setNombresArticulos(nombresArticulos.get(i));
            }
            rellenarListaValores();
            adapter.notifyDataSetChanged();
        }
    }

    public void descargarListaCompras(){

        // Read from the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference myRef = database.getReference("/ventas/mis compras/"+user.getUid());

        Log.v("Funciona",""+ "/ventas/mis compras/"+user.getDisplayName());

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                boolean vacio = true;

                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                    Log.v("Funciona",""+ childDataSnapshot.getKey()); //displays the key for the node
                    Log.v("Funciona",""+ childDataSnapshot.getValue());
                    //addValue(childDataSnapshot.getKey(), childDataSnapshot.getValue(Venta.class));
                    vacio = false;
                    bajarCompra(childDataSnapshot.getValue(String.class));

                }

                if(vacio){
                    listaValores.add("No hay compras");
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





    public void cargando(boolean cargando){
        if (cargando){
            gif.setVisibility(View.VISIBLE);
        }else{
            gif.setVisibility(View.INVISIBLE);
        }
    }

    public ArrayList<String> getIdsArticulos(int pos){
        ArrayList<String> ids = new ArrayList<>();
        ArrayList<Integer> cortes = new ArrayList<>();
        cortes.add(-1);

        for(int i =0; i< values.get(pos).getArticuloId().length(); i++){
            if (values.get(pos).getArticuloId().charAt(i)=='&'){
                cortes.add(i);
            }
        }

        for (int i =0; i<cortes.size()-1;i++){
            ids.add(values.get(pos).getArticuloId().substring(cortes.get(i)+1,cortes.get(i+1)));
        }


        return ids;
    }

    public void setNombresArticulos(){
        nombresArticulos.clear();
        for (int i=0; i<articulos.size();i++){
            String nombres = "";
            for (int j=0;j<articulos.get(i).size();j++){
                String retorno = "\n";
                if (j==articulos.get(i).size()-1){
                    retorno = "";
                }
                nombres = nombres + articulos.get(i).get(j).getNombre() + retorno;
            }
            nombresArticulos.add(nombres);

        }

    }

    public void bajarCompra(String ruta){
        DatabaseReference myRef = database.getReference("/ventas/"+ruta);

        Log.d(TAG, "ventas/"+ruta);

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Venta value = dataSnapshot.getValue(Venta.class);
                addValue(dataSnapshot.getKey(),value);
                Log.d(TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private void bindDataToAdapter() {
        // Bind adapter to recycler view object

        adapter = new ComplexRecyclerViewAdapter(listaValores);

        recyclerView.setAdapter(adapter);

        cargando(false);

    }

    public void rellenarListaValores(){
        for (int i=0;i<values.size();i++){
            if(values.get(i).getNombresArticulos()!=null&&!listaValores.contains(values.get(i))){
                //values.get(i).setNombresArticulos(nombresArticulos.get(i));
                listaValores.add(values.get(i));
            }
        }
    }

    public void rellenarNombresArticulos(){
        //Primer hay que ver qué ventas no tienen nombre de articulos
        for(int i=0;i<values.size();i++){
            //Si esta a null se debe rellenar
            if(values.get(i).getNombresArticulos()==null){
                boolean rellenar = true;
                String nombres = "";
                //Solo se rellena si tenemos todos los articulos
                ArrayList<String> ids = getIdsArticulos(i);
                for (int j =0; j<ids.size();j++){
                    //ids es la lista de articulos de cada venta, vamos a ver si los hemos descargado
                    if(!catalogo.containsKey(ids.get(j))){
                        //Si no lo contiene, no vamos a querer rellenar los nombres
                        rellenar = false;
                    }else{
                        //Si lo contiene, lo ponemos en el string para tenerlos todos guardados al final
                        Articulo aux = catalogo.get(ids.get(j));
                        nombres = nombres + '\n'+aux.getNombre();
                    }
                }
                //Salimos del bucle y ya hemos determinado si lo queremos rellenar
                if(rellenar){
                    values.get(i).setNombresArticulos(nombres);
                    rellenarListaValores();
                    adapter.notifyDataSetChanged();
                    Button b2 = (Button) v.findViewById(R.id.dia_actual);
                    b2.setText(listaValores.size()+" compras");
                }
            }
        }
    }

    public void esconderBotones(View v){
        Button b1 = (Button) v.findViewById(R.id.id_dia_anterior);
        Button b2 = (Button) v.findViewById(R.id.dia_actual);
        Button b3 = (Button) v.findViewById(R.id.dia_siguiente);
        b1.setVisibility(View.INVISIBLE);
        b2.setText(listaValores.size()+" compras");
        b3.setVisibility(View.INVISIBLE);
    }


}





