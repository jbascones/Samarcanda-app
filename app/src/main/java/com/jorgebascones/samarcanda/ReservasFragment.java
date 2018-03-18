package com.jorgebascones.samarcanda;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.lujun.popmenulayout.MenuBean;
import co.lujun.popmenulayout.OnMenuClickListener;
import co.lujun.popmenulayout.PopMenuLayout;
import pl.droidsonroids.gif.GifImageView;


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
    private final int HOY = 0, MANANA=1,PROCESO=2,RESERVADO=3;
    public int dia = HOY;
    public int estadoQ = PROCESO;
    private View v;
    GifImageView gif;





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

        bindDataToAdapter();

        myTimer();

        v = view;

        gif = (GifImageView) v.findViewById(R.id.gifImageView);
        cargando(false);

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



    public void setPeticionConfirmacion(){
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
        reservas.add("No hay reservas");
        adapter.notifyDataSetChanged();
        keys.clear();
        myRef2.orderByChild("estado").equalTo(estado).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                cargando(true);
                if(addReserva(dataSnapshot.getKey(),dataSnapshot.getValue(Reserva.class))){
                    for (int i=0;i<dataSnapshot.getValue(Reserva.class).getArticulos().size();i++){
                        descargarListaArticulos(dataSnapshot.getValue(Reserva.class).getArticulos().get(i), dataSnapshot.getValue(Reserva.class));
                    }
                }


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG,dataSnapshot.getValue().toString()+" changed");
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG,dataSnapshot.getValue().toString()+" removed");
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG,dataSnapshot.getValue().toString()+" moved");
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

                gestionMenu(""+level1Index+level2Index);

                realizarConsulta();

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
                Reserva aux = (Reserva)reservas.get(position);
                if(aux.getEstado().equals("Por confirmar")){
                    confirmarReserva(aux);
                }else{
                    realizarVenta(aux);
                    eliminarReserva(aux);

                }
            }
        });

        recyclerView.setAdapter(adapter);


    }


    public void gestionMenu(String eleccion){
        switch (eleccion) {
            case "00":  dia = HOY;
                break;
            case "01":  dia = MANANA;
                break;
            case "10":  estadoQ = PROCESO;
                break;
            case "11":  estadoQ = RESERVADO;
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
            reservasNoNull();
            reservas.add(newValue);
            keys.add(key);
        }

        return add;

    }

    public void reservasNoNull(){
        if(reservas.get(0) instanceof  String){
            reservas.remove(0);
        }
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
            cargando(false);


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

    public void realizarConsulta(){
        String diaStr;
        if (dia == HOY){
            diaStr = getHoy();
        }else{
            diaStr = getManana();
        }
        String estadoStr;
        if(estadoQ==PROCESO){
            estadoStr = "Por confirmar";
        }else{
            estadoStr = "Reserva confirmada";
        }
        setTextoQuery();
        whereQuery(diaStr,estadoStr);

    }

    public void myTimer(){
        new Handler().postDelayed(new Runnable() {
            public void run() {
                Log.d(TAG,"Timer "+new Fecha().fechaPreparada(new Fecha().getFecha(),true));

            }
        }, 1000);
    }

    public void setTextoQuery(){
        TextView txt = (TextView) v.findViewById(R.id.txt_query);
        String diaStr;
        String texto;
        if (dia == HOY){
            diaStr = "hoy";
        }else{
            diaStr = "mañana";
        }
        String estadoStr;
        if(estadoQ==PROCESO){
            estadoStr = "por confirmar";
        }else{
            estadoStr = "reserva confirmada";
        }
        texto = "Reservas para "+diaStr+" y"+ " "+estadoStr;
        txt.setText(texto);
    }

    public void realizarVenta(Reserva reserva){
        Intent myIntent = new Intent(getActivity(), LectorQRActivity.class);
        myIntent.putExtra("reserva","si");
        myIntent.putExtra("user",reserva.getUserId());
        myIntent.putExtra("lista",(Serializable)reserva.getArticulos());
        startActivity(myIntent);
    }

    public void confirmarReserva(Reserva reserva){
        reserva.setEstado("Reserva confirmada");
        reserva.setTextoArticulos(null);
        for (int i=0;i<reservas.size();i++){
            Reserva aux = (Reserva) reservas.get(i);
            if(aux.getIdentificador().equals(reserva.getIdentificador())){
                reservas.remove(i);
                adapter.notifyDataSetChanged();
                String fechaReserva;
                if(dia == HOY){
                    fechaReserva = getHoy();
                }else{
                    fechaReserva = getManana();
                }
                DatabaseReference myRef = database.getReference("/reservas/"+fechaReserva+"/"+reserva.getIdentificador());
                myRef.setValue(reserva);
            }
        }
    }

    public void eliminarReserva(Reserva reserva){

        for (int i=0;i<reservas.size();i++){
            Reserva aux = (Reserva) reservas.get(i);
            if(aux.getIdentificador().equals(reserva.getIdentificador())){
                reservas.remove(i);
                adapter.notifyDataSetChanged();
                String fechaReserva;
                if(dia == HOY){
                    fechaReserva = getHoy();
                }else{
                    fechaReserva = getManana();
                }
                DatabaseReference myRef = database.getReference("/reservas/"+fechaReserva+"/"+reserva.getIdentificador());
                myRef.setValue(null);
            }
        }
    }

    public void cargando(boolean cargando){
        if (cargando){
            gif.setVisibility(View.VISIBLE);
        }else{
            gif.setVisibility(View.INVISIBLE);
        }
    }



}





