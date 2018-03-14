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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardInputWidget;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.lujun.popmenulayout.MenuBean;
import co.lujun.popmenulayout.OnMenuClickListener;
import co.lujun.popmenulayout.PopMenuLayout;


/**
 * A simple {@link Fragment} subclass.
 */

public class ReservasFragment extends Fragment {


    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String TAG = "reservas";
    RecyclerView recyclerView;
    ComplexRecyclerViewAdapter adapter;
    public ArrayList<Object> reservas = new ArrayList<>();
    public ArrayList<String> keys = new ArrayList<String>();
    public ArrayList<String> keysArticulos = new ArrayList<String>();
    public Map<String,Articulo> articulos = new HashMap<>();
    public ArrayList<boolean[]> check = new ArrayList<boolean[]>();





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_reservas, container, false);

        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Reservas");

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(false);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        bindDataToAdapter();

        new GetDataFromFirebase().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        setPopUpMenu(view);

        Log.d("mañana",getManana());

        bindDataToAdapter();


        whereQuery(getManana(),"Por confirmar");




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



    public void confirmarReserva(){
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url ="https://us-central1-samarcanda-f80f3.cloudfunctions.net/probandoReserva";

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Volley", "Response "+response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Volley", "Error "+error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("name", "Alif");
                params.put("domain", "http://itsalif.info");

                return params;
            }
        };
        queue.add(postRequest);
    }

    public void whereQuery(String ruta, String estado){
        DatabaseReference myRef2 = database.getReference("/reservas/"+ruta);
        Log.d("query","Entrando en whereQuery()");
        reservas.clear();
        myRef2.orderByChild("estado").equalTo(estado).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                //System.out.println(dataSnapshot.getKey());
                Log.d("query",dataSnapshot.getKey());
                if(addReserva(dataSnapshot.getKey(),dataSnapshot.getValue(Reserva.class))){
                    for (int i=0;i<dataSnapshot.getValue(Reserva.class).getArticulos().size();i++){
                        descargarListaArticulos(dataSnapshot.getValue(Reserva.class).getArticulos().get(i), dataSnapshot.getValue(Reserva.class));
                    }
                }


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

    }
    /*
    public void bajarReservas(){
        //Hay que determinar la fecha actual
        Fecha fecha = new Fecha();
        whereQuery(fecha.getRutaVenta());
        fecha.setFecha(fecha.sumarDias(fecha.getFecha(),1));
        String ruta = fecha.getRutaVenta();
        whereQuery(ruta);
    }*/

    public void setPopUpMenu(View v){
        PopMenuLayout popMenuLayout = (PopMenuLayout) v.findViewById(R.id.popMenuLayout);

        String confJson = new Herramientas().getConfJson();

        popMenuLayout.setConfigJson(confJson);


        popMenuLayout.setOnMenuClickListener(new OnMenuClickListener() {
            @Override
            public void onMenuClick(int level1Index, int level2Index, int level3Index) {
                Log.d("pop prueba","Click menu index:" +
                        "\nlevel1 index = " + level1Index + "\nlevel2 index = " +
                        level2Index + "\nlevel3 index = " + level3Index);
                gestionMenu(""+level1Index+level2Index);

                reservas.add(""+level1Index+level2Index);

                adapter.notifyItemInserted(reservas.size()-1);

            }
        });
    }

    private void bindDataToAdapter() {
        // Bind adapter to recycler view object

        adapter = new ComplexRecyclerViewAdapter(reservas);

        adapter.setOnItemClickListener(new ComplexRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.d("query",""+view.getId()+"  "+position);
            }
        });

        recyclerView.setAdapter(adapter);


    }

    private void bindNullDataToAdapter() {
        // Bind adapter to recycler view object
        ArrayList<Object> listaValores = new ArrayList<>();
        listaValores.add("No hay noticias disponibles");
        adapter.swap(listaValores);


    }

    public void gestionMenu(String eleccion){
        switch (eleccion) {
            case "00":  Log.d("Reservas","Hoy");
                break;
            case "01":  Log.d("Reservas","Mañana");
                break;
            case "10":  Log.d("Reservas","En proceso");
                break;
            case "11":  Log.d("Reservas","Reservado");
                break;
            default: Log.d("Reservas","Default");
                break;
        }

    }

    public String getHoy(){
        Fecha fecha = new Fecha();
        return fecha.getRutaVenta();
    }

    public String getManana(){
        Fecha fecha = new Fecha();
        fecha.setFecha(fecha.sumarDias(fecha.getFecha(),1));
        return fecha.getRutaVenta();
    }

    public boolean addReserva(String key, Reserva newValue){

        boolean add = true;

        for(int i = 0;i<keys.size();i++){
            if (keys.get(i).equals(key)){
                add = false;
            }
        }

        if (add){
            reservas.add(newValue);
            keys.add(key);
        }

        return add;

    }

    public void addArticulo(String key, Articulo newValue){

        boolean add = true;

        for(int i = 0;i<keysArticulos.size();i++){
            if (keysArticulos.get(i).equals(key)){
                add = false;
            }
        }

        if (add){
            articulos.put(key,newValue);
            keysArticulos.add(key);
        }

    }

    public void descargarListaArticulos(String ruta, final Reserva reserva){

        // Read from the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference myRef = database.getReference("/articulos/"+ruta);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                    Log.v(TAG,"getKey "+ dataSnapshot.getKey()); //displays the key for the node
                    Log.v(TAG,"getValue "+ dataSnapshot.getValue());
                    addArticulo(dataSnapshot.getKey(), dataSnapshot.getValue(Articulo.class));

                    checkArticulos(reserva);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                System.out.println("Failed to read value." + error.toException());
            }
        });
    }

    public void rellenarReservas(int posicion){
            Log.d(TAG,"Posicion "+posicion);
            Reserva aux = (Reserva) reservas.get(posicion);
            String articulosStr = "";
            for (int j = 0; j < aux.getArticulos().size(); j++) {
                String retornoCarro = "\n";
                if (j == aux.getArticulos().size() - 1) {
                    retornoCarro = "";
                }
                articulosStr = articulosStr + articulos.get(getArticuloIdFromRuta(aux.getArticulos().get(j))).getNombre() + retornoCarro;
            }
            aux.setTextoArticulos(articulosStr);
            reservas.remove(posicion);
            reservas.add(posicion, aux);
            adapter.notifyDataSetChanged();


    }

    public void checkArticulos(Reserva reserva){
        boolean todos = true;
        for(int i=0;i<reserva.getArticulos().size();i++){
            if(!articulos.containsKey(getArticuloIdFromRuta(reserva.getArticulos().get(i)))){
                todos = false;
            }
        }

        if(todos){
            int posicion=-1;
            for(int i=0;i<reservas.size();i++){
                Reserva aux = (Reserva) reservas.get(i);
                String id1 = aux.getIdentificador();
                String id2 = reserva.getIdentificador();
                if(id1.equals(id2)){
                    posicion= i;
                }

            }
            if(posicion!=-1){
                rellenarReservas(posicion);
            }

        }

    }

    public String getArticuloIdFromRuta(String ruta){
        int index = ruta.indexOf('/');
        return ruta.substring(index+1,ruta.length());
    }



}





