package com.jorgebascones.samarcanda;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jorgebascones.samarcanda.Modelos.Fecha;
import com.jorgebascones.samarcanda.Modelos.Reserva;
import com.jorgebascones.samarcanda.Modelos.User;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */

public class SimuladorFragment extends Fragment {


    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private final String TAG = "simulador";

    private ArrayList<User> users;
    private View v;
    private boolean reservasHoy;
    private boolean reservasManana;
    private boolean reservasAmbos;
    private Herramientas h;
    private Button buttonSimular;
    private Map<String,Integer> datosVentas;
    private int mesOffset;
    private Fecha f;
    private String rutaVentassimuladas;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_simulador, container, false);

        v = view;

        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Simulador");
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        users = new ArrayList<>();

        f = new Fecha();

        mesOffset =0;

        setButtonsReservas();

        setListenersBotonesVentas();

        estadoInicialSimulacionVentas();

        descargarListaUsers();

        buttonSimular = (Button) view.findViewById(R.id.id_boton_simular);

        buttonSimular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            generarReservas();

            }
        });

        Button buttonSimularVentas = (Button) view.findViewById(R.id.id_boton_simular_ventas);

        buttonSimularVentas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generarDatosVentas();
                subirDatosventas();

            }
        });

        h = new Herramientas();


        return view;
    }

    public void descargarListaUsers(){

        // Read from the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference myRef = database.getReference("/users/perfiles");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                    Log.v("Funciona",""+ childDataSnapshot.getKey()); //displays the key for the node
                    Log.v("Funciona",""+ childDataSnapshot.getValue());
                    users.add(childDataSnapshot.getValue(User.class));

                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                System.out.println("Failed to read value." + error.toException());
            }
        });
    }

    public void generarReservas(){
        EditText editText = (EditText) v.findViewById(R.id.numero_reservas_simuladas);
        int numero = Integer.valueOf(editText.getText().toString());
        if(reservasHoy || reservasAmbos){
            for(int i=0;i<numero;i++){
                Fecha fecha = new Fecha();
                fecha.setFecha(fecha.sumarDias(fecha.getFecha(),0));
                String ruta = fecha.getRutaVenta();
                subirReserva(ruta);
            }
        }if(reservasManana || reservasAmbos){
            for(int i=0;i<numero;i++){
                Fecha fecha = new Fecha();
                fecha.setFecha(fecha.sumarDias(fecha.getFecha(),1));
                String ruta = fecha.getRutaVenta();
                subirReserva(ruta);
            }
        }

    }

    public void setButtonsReservas(){

        Button reservasMananaButton = (Button) v.findViewById(R.id.id_reservas_manana);
        reservasMananaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reservasHoy = false;
                reservasManana = true;
                reservasAmbos = false;
                buttonSimular.setText("Simular Reservas Mañana");
            }
        });

        Button reservasHoyButton = (Button) v.findViewById(R.id.id_reservas_hoy);
        reservasHoyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reservasHoy = true;
                reservasManana = false;
                reservasAmbos = false;
                buttonSimular.setText("Simular Reservas Hoy");
            }
        });

        Button reservasAmbosButton = (Button) v.findViewById(R.id.id_reservas_ambos);
        reservasAmbosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reservasHoy = false;
                reservasManana = false;
                reservasAmbos = true;
                buttonSimular.setText("Simular Reservas Ambos");
            }
        });

    }

    public void addArticulosReservados(Reserva reserva){
        String [] articulos = {"camisetas/0123456789","camisetas/camiseta3","camisetas/camiseta2"};
        for(int i=0; i<h.random(5)+1;i++){
            reserva.addArticulo(articulos[h.random(articulos.length)]);
        }

    }

    public void subirReserva(String ruta){
        Log.d(TAG,"Subiendo reserva");
        Reserva reserva = new Reserva();
        addArticulosReservados(reserva);
        reserva.setEstado("Por confirmar");
        reserva.setUserId(users.get(h.random(users.size())).getUsuarioId());
        reserva.setUserName(user.getDisplayName());
        String key = myRef.child("/reservas/"+ruta+"/").push().getKey();
        reserva.setIdentificador(key);
        myRef.child("/reservas/"+ruta+"/"+key+"/").setValue(reserva);
        Toast.makeText(getContext(),"Reserva registrada",Toast.LENGTH_SHORT).show();
    }

    public void generarDatosVentas(){
        datosVentas = new HashMap<>();
        Fecha f = new Fecha();
        int diasMes = f.StringToInt(f.getDiaMesAnterior(rutaVentassimuladas.substring(5)))+1;
        for (int i=1;i<diasMes;i++){
            int minimoVentas = 0;
            try {
                minimoVentas = getMinimoVentasDia(f.getDiaSemana(rutaVentassimuladas+"/"+i));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            int variacion = h.random(5);
            Log.d(TAG,"variacion dia "+i+" es de "+ variacion);
            datosVentas.put(""+i,variacion+minimoVentas);
        }

    }

    public int getMinimoVentasDia(String dia){
        switch (dia){
            case "L":
                return 10;
            case "M":
                return 12;
            case "X":
                return 10;
            case "J":
                return 8;
            case "V":
                return 15;
            case "S":
                return 20;
            case "D":
                return 13;
            default:
                return 10;
        }
    }

    public void setListenersBotonesVentas(){

        final Button buttonActual = (Button) v.findViewById(R.id.id_mes_actual);


        buttonActual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });

        Button mesAnterior = (Button) v.findViewById(R.id.mes_anterior);


        mesAnterior.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mesOffset --;
                rutaVentassimuladas = f.sumarMesRuta(mesOffset);
                buttonActual.setText(f.mes+"/"+f.anno);

            }
        });

        Button mesSiguiente = (Button) v.findViewById(R.id.id_mes_siguiente);


        mesSiguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mesOffset ++;
                rutaVentassimuladas = f.sumarMesRuta(mesOffset);
                buttonActual.setText(f.mes+"/"+f.anno);

            }
        });
    }

    public void estadoInicialSimulacionVentas(){
        rutaVentassimuladas = f.sumarMesRuta(mesOffset);
        Button buttonActual = (Button) v.findViewById(R.id.id_mes_actual);
        buttonActual.setText(f.mes+"/"+f.anno);
    }

    public void subirDatosventas(){
        DatabaseReference myRef = database.getReference("estadisticas/ventas/ventas por dia/"+rutaVentassimuladas);

        myRef.setValue(datosVentas);
    }



}
