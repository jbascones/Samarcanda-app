package com.jorgebascones.samarcanda;


import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.jorgebascones.samarcanda.Modelos.Fecha;
import com.jorgebascones.samarcanda.Modelos.Reserva;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import co.lujun.popmenulayout.OnMenuClickListener;
import co.lujun.popmenulayout.PopMenuLayout;
import pl.droidsonroids.gif.GifImageView;


/**
 * A simple {@link Fragment} subclass.
 */

public class MisReservasFragment extends Fragment {


    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String TAG = "reservas";
    RecyclerView recyclerView;
    ComplexRecyclerViewAdapter adapter;
    public ArrayList<Object> reservas = new ArrayList<>();
    public ArrayList<Object> reservasHoy = new ArrayList<>();
    public ArrayList<Object> reservasManana = new ArrayList<>();
    public ArrayList<String> keys = new ArrayList<String>();
    public ArrayList<String> keysArticulos = new ArrayList<String>();
    public Map<String,Articulo> articulos = new HashMap<>();
    private final int HOY = 0, MANANA=1,PROCESO=2,RESERVADO=3;
    private boolean todosDias;
    public int dia = -1;
    public int estadoQ = PROCESO;
    private View v;
    GifImageView gif;





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_reservas, container, false);

        v = view;

        gif = (GifImageView) v.findViewById(R.id.gifImageView);

        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Mis reservas");

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(false);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        bindDataToAdapter();

        new GetDataFromFirebase().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        setPopUpMenu(view);

        bindDataToAdapter();

        bajarTodosDatos();

        cargando(false);

        myTimer();

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



    public void whereQuery(final String ruta, String estado){
        cargando(true);
        DatabaseReference myRef2 = database.getReference("/reservas/"+ruta);
        Log.d("query","Entrando en whereQuery()");
        reservas.clear();
        reservas.add("No hay reservas");
        adapter.notifyDataSetChanged();
        //TODO: filtrado de las reservas
        keys.clear();
        myRef2.orderByChild("userId").equalTo(user.getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                if(addReserva(dataSnapshot.getKey(),dataSnapshot.getValue(Reserva.class),ruta)){
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
        cargando(false);
    }


    public void setPopUpMenu(View v){
        PopMenuLayout popMenuLayout = (PopMenuLayout) v.findViewById(R.id.popMenuLayout);

        String confJson = new Herramientas().getConfJsonCliente();

        popMenuLayout.setConfigJson(confJson);


        popMenuLayout.setOnMenuClickListener(new OnMenuClickListener() {
            @Override
            public void onMenuClick(int level1Index, int level2Index, int level3Index) {

                gestionMenu(""+level1Index+level2Index);

                if(dia==-1){
                    todosDias= true;
                }else{
                    todosDias= false;
                }

                realizarConsulta();

            }
        });
    }

    private void bindDataToAdapter() {
        // Bind adapter to recycler view object

        adapter = new ComplexRecyclerViewAdapter(reservas);

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

    public boolean addReserva(String key, Reserva newValue, String ruta){

        boolean add = true;

        for(int i = 0;i<keys.size();i++){
            if (keys.get(i).equals(key)){
                add = false;
            }
        }

        if (add){
            reservasNoNull();
            Fecha fecha = new Fecha();
            newValue.setValidez(fecha.fechaPreparada(ruta,false));
            newValue.setMisReservas(true);
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
            guardarCacheReservas();
            if(todosDias){
                rellenarReservasTodosDias();
            }
            cargando(false);
            filtrarPorEstado();
            quitarSobrantes();
            if(reservas.size()==0){
                reservas.add("No hay reservas");
            }
            adapter.notifyDataSetChanged();


    }

    public void guardarCacheReservas(){
        if (dia == HOY){
            reservasHoy.addAll(reservas);
        }else{
            reservasManana.addAll(reservas);
        }

    }

    public void rellenarReservasTodosDias(){
        reservas.clear();
        for(int i=0;i<reservasHoy.size();i++){
            reservas.add(reservasHoy.get(i));
        }
        for(int i=0;i<reservasManana.size();i++){
            reservas.add(reservasManana.get(i));
        }
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
            try{
                for(int i=0;i<reservas.size();i++){
                    Reserva aux = (Reserva) reservas.get(i);
                    String id1 = aux.getIdentificador();
                    String id2 = reserva.getIdentificador();
                    if(id1.equals(id2)){
                        posicion= i;
                    }

                }
            }catch (Exception e){
                Log.d("crash","Casting String a reserva");
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

        String diaStr;
        String texto;
        if (dia == HOY){
            diaStr = "hoy";
        }else{
            diaStr = "mañana";
        }
        String estadoStr;
        if(estadoQ==PROCESO){
            estadoStr = "pendientes";
        }else{
            estadoStr = "ya disponibles";
        }

        texto = "Reservado hasta "+diaStr+" y"+ " "+estadoStr;

        if(todosDias){
            texto = "Reservas "+estadoStr;
        }

        TextView txt = (TextView) v.findViewById(R.id.txt_query);
        txt.setText(texto);

    }

    public void sacarSnackBar(String texto){
        Snackbar.make(v.getRootView(), texto, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    public void cargando(boolean cargando){
        if (cargando){
            gif.setVisibility(View.VISIBLE);
        }else{
            gif.setVisibility(View.INVISIBLE);
        }
    }

    public void bajarTodosDatos(){
        todosDias = true;
        dia = HOY;
        realizarConsulta();
        dia = MANANA;
        realizarConsulta();
        dia = -1;

    }

    public void filtrarPorEstado() {
        if (estadoQ == PROCESO) {
            for (int i = 0; i < reservas.size(); i++) {
                Reserva aux = (Reserva) reservas.get(i);
                if (aux.getEstado().equals("Reserva confirmada")) {
                    reservas.remove(i);
                    i--;
                }
            }
        } else {
            for (int i = 0; i < reservas.size(); i++) {
                Reserva aux = (Reserva) reservas.get(i);
                if (aux.getEstado().equals("Por confirmar")) {
                    reservas.remove(i);
                    i--;
                }
            }
        }

    }

    public void quitarSobrantes() {
        for (int i = 0; i < reservas.size(); i++) {
            Reserva aux = (Reserva) reservas.get(i);
            String id = aux.getIdentificador();
            for (int j = i + 1; j < reservas.size(); j++) {
                Reserva aux2 = (Reserva) reservas.get(j);
                if (aux2.getIdentificador().equals(id)) {
                    reservas.remove(j);
                    j--;
                }
            }
        }
    }



}





