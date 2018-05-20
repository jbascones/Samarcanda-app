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
import com.jorgebascones.samarcanda.Modelos.Fecha;
import com.jorgebascones.samarcanda.Modelos.Post;
import com.jorgebascones.samarcanda.Modelos.Tiempo;
import com.jorgebascones.samarcanda.Modelos.User;
import com.jorgebascones.samarcanda.Modelos.Venta;

import java.util.ArrayList;
import java.util.List;


import pl.droidsonroids.gif.GifImageView;

import static java.lang.Math.round;


/**
 * A simple {@link Fragment} subclass.
 */

public class NoticiasFragment extends Fragment {


    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;
    public ArrayList<Venta> values = new ArrayList<Venta>();
    public ArrayList<String> keys = new ArrayList<String>();
    public ArrayList<Object> noticias = new ArrayList<>();
    final String TAG = "noticias";
    final String MADRID = "3117735";
    Fecha fecha;
    GifImageView gif;
    CurrentWeatherInfo currentWeatherInfoC;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_noticias, container, false);

        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Ofertas y Noticias");

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        new GetDataFromFirebase().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        fecha = new Fecha();

        gif = (GifImageView) view.findViewById(R.id.gifImageView);

        try{
            descargarParte();
        }catch (Exception e){
            Log.d(TAG,"Problemas al descargar parte metereologico");
        }

        descargarListaPosts();


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

        recyclerView.setAdapter(new ComplexRecyclerViewAdapter(noticias));

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

    public void descargarParte(){

        // Initialize OpenWeatherRetrieverZ by passing in  your openweathermap api key
        OpenWeatherRetrieverZ retriever = new OpenWeatherRetrieverZ("4c6886fb4b9b3e5ebc5eb7742a01837f");
        /*
        You can retrieve weather information with either OpenWeatherMap cityID or geolocation(Latitude, Logitude)
        */
        retriever.updateCurrentWeatherInfo(MADRID, new WeatherCallback() {
            @Override
            public void onReceiveWeatherInfo(CurrentWeatherInfo currentWeatherInfo) {
                currentWeatherInfoC = WeatherUnitConverter.convertToMetric(currentWeatherInfo);

                Double temperatura = Double.parseDouble(currentWeatherInfo.getCurrentTemperature()) - 273 ;
                Log.d(TAG,"Temperatura "+ temperatura);
                long intT = round(temperatura);
                Log.d(TAG,"Temperatura round "+ temperatura);
                String descripncion = currentWeatherInfoC.getWeatherDescriptionLong();
                myRef.child("/weather/"+ descripncion).setValue(true);
                Tiempo tiempo = new Tiempo(intT, descripncion, currentWeatherInfo.getWeatherIconLink());
                noticias.add(0,tiempo);
                bindDataToAdapter();

            }

            @Override
            public void onFailure(String error) {
                // Your code here
            }
        });
        retriever.updateDailyForecastInfo(MADRID, new DailyForecastCallback() {
            @Override
            public void onReceiveDailyForecastInfoList(List<DailyForecastInfo> dailyForecastInfoList) {

            }

            @Override
            public void onFailure(String error) {

            }
        });


    }

    public void descargarListaPosts(){

        // Read from the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference myRef = database.getReference("/posts/");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                    Log.v("Funciona",""+ childDataSnapshot.getKey()); //displays the key for the node
                    Log.v("Funciona",""+ childDataSnapshot.getValue());
                    addValue(childDataSnapshot.getKey(), childDataSnapshot.getValue(Post.class));


                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                System.out.println("Failed to read value." + error.toException());
            }
        });
    }

    public void addValue(String key,Post post){
        noticias.add(post);
        Log.d("Noticias",noticias.toString());
    }

}





