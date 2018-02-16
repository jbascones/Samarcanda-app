package com.jorgebascones.samarcanda;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.dd.morphingbutton.MorphingButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;
import com.jorgebascones.samarcanda.Modelos.Articulo;
import com.jorgebascones.samarcanda.Modelos.Fecha;
import com.jorgebascones.samarcanda.Modelos.User;
import com.jorgebascones.samarcanda.Modelos.Venta;
import com.squareup.picasso.Picasso;

import me.dm7.barcodescanner.zxing.ZXingScannerView;


public class LectorQRActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler{

    private ZXingScannerView zXingScannerView;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();
    final String TAG = "venta";
    public Venta venta;
    public MorphingButton btnMorph;
    public boolean ventaCofirmada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_lector_qr);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        venta = new Venta();

        Fecha nuevaFecha = new Fecha();

        Log.d(TAG,nuevaFecha.getRutaVenta());

        gestionElements();

        ventaCofirmada = false;
        Button buttonTerminar = (Button) findViewById(R.id.id_bn_terminar_venta);
        buttonTerminar.setVisibility(View.INVISIBLE);

        //Codigo morphing button
        gestionMorph();


        /*btnMorph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                morphToSuccess(btnMorph);
            }
        }); */



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
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

    public void leerQR(View v){
        //Se comprueba si se tienen permisos de camara
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED){
            //En caso de que no, se pide dar dichos permisos
            ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.CAMERA}, 1);

        }else {
            //Cuando se tienen permisos de camara, se ejecuta el metodo que cambia el layout al lector
            sacarCamara();
        }
    }

    public void leerArticuloManual(View v){
        bajarArticulo("/camisetas/0123456789");
    }

    public void cancelarArticulo(View v){
        venta.setArticulo(null);
        gestionElements();
    }

    public void cancelarCliente(View v){
        venta.setUser(null);
        gestionElements();
    }

    public void sacarCamara(){
        //Se crea el objeto de la libreria para leer codifos QR
        zXingScannerView = new ZXingScannerView(getApplicationContext());
        //Se cambia al layoyt que contiene dicho objeto
        setContentView(zXingScannerView);
        //Se establece el Handler para recibir la lectura del QR
        zXingScannerView.setResultHandler(this);
        zXingScannerView.startCamera();
    }

    public void atras(View v){
        finish();
    }


    @Override
    public void handleResult(Result result) {
        //Toast.makeText(getApplicationContext(),result.getText().substring(5),Toast.LENGTH_SHORT).show();

        gestionLectura(result);

        //zXingScannerView.resumeCameraPreview(this);
        zXingScannerView.stopCamera();
        setContentView(R.layout.activity_main_lector_qr);

    }

    public void setCliente(User cliente){
        venta.setUser(cliente);
        gestionElements();
    }

    public void subirVenta (View v){
        if (ventaCofirmada){
            Log.d("venta","Subiendo venta");
            Fecha fechaVenta = new Fecha();
            venta.setFecha(fechaVenta.getFecha());
            prepararObjetoVenta();
            myRef.child("/ventas/"+fechaVenta.getRutaVenta()).push().setValue(venta);
            Toast.makeText(getApplicationContext(),"Venta confirmada",Toast.LENGTH_SHORT).show();
            finish();
        }else{

        }
    }

    private void morphToSquare(final MorphingButton btnMorph, int duration) {
        MorphingButton.Params square = MorphingButton.Params.create()
                .duration(duration)
                .cornerRadius(dimen(R.dimen.mb_corner_radius_2))
                .width(dimen(R.dimen.mb_width_200))
                .height(dimen(R.dimen.mb_altura_boton))
                .color(color(R.color.colorAccent))
                .colorPressed(color(R.color.colorAccent))
                .text("Confirmar venta");
        btnMorph.morph(square);
    }

    public void success (View v){
        ventaCofirmada= true;
        morphToSuccess(btnMorph);
        Button buttonTerminar = (Button) findViewById(R.id.id_bn_terminar_venta);
        buttonTerminar.setVisibility(View.VISIBLE);
    }

    private void morphToSuccess(final MorphingButton btnMorph) {
        MorphingButton.Params circle = MorphingButton.Params.create()
                .duration(integer(R.integer.mb_animation))
                .cornerRadius(dimen(R.dimen.mb_altura_circulo))
                .width(dimen(R.dimen.mb_altura_circulo))
                .height(dimen(R.dimen.mb_altura_circulo))
                .color(color(R.color.mb_green))
                .colorPressed(color(R.color.mb_green_dark))
                .icon(R.drawable.ic_venta_confirmada_96);
        btnMorph.morph(circle);
    }

    public void gestionElements(){
        Log.d(TAG,"Entra en gestion elements");
        if(venta.getUser()==null){
            View clienteElement = findViewById(R.id.cliente_element);
            clienteElement.setVisibility(View.INVISIBLE);
            Button botonLeer = (Button) findViewById(R.id.id_button_leer_cliente);
            botonLeer.setVisibility(View.VISIBLE);
        }else{
            TextView id = (TextView) findViewById(R.id.id_element_id);
            id.setText("Cliente");
            TextView nombre = (TextView) findViewById(R.id.id_element_nombre);
            nombre.setText(venta.getUser().getNombre());
            ImageView icon = (ImageView) findViewById(R.id.id_element_icon);
            Context c = getApplicationContext();
            Picasso.with(c).load(venta.getUser().getUrlFoto()).into(icon);
            Button botonLeer = (Button) findViewById(R.id.id_button_leer_cliente);
            botonLeer.setVisibility(View.INVISIBLE);
        }
        if(venta.getArticulo()==null){
            View clienteElement = findViewById(R.id.articulo_element);
            clienteElement.setVisibility(View.INVISIBLE);
            Button botonLeer = (Button) findViewById(R.id.id_button_leer_articulo);
            botonLeer.setVisibility(View.VISIBLE);
        }else{
            View clienteElement = findViewById(R.id.articulo_element);
            clienteElement.setVisibility(View.VISIBLE);
            TextView id = (TextView) findViewById(R.id.id_element_id_a);
            id.setText("Artículo");
            TextView nombre = (TextView) findViewById(R.id.id_element_nombre_a);
            nombre.setText(venta.getArticulo().getNombre());
            ImageView icon = (ImageView) findViewById(R.id.id_element_icon_a);
            Context c = getApplicationContext();
            Picasso.with(c).load(venta.getArticulo().getFotoUrl()).into(icon);
            Button botonLeer = (Button) findViewById(R.id.id_button_leer_articulo);
            botonLeer.setVisibility(View.INVISIBLE);
        }
        gestionMorph();
    }

    public int dimen(int resId) {
        return (int) getResources().getDimension(resId);
    }

    public int color(int resId) {
        return getResources().getColor(resId);
    }

    public int integer(int resId) {
        return getResources().getInteger(resId);
    }

    public void bajarUser(final String userId){

        DatabaseReference myRefUsuarios = database.getReference("/users/perfiles/"+userId);

        myRefUsuarios.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                User cliente = dataSnapshot.getValue(User.class);
                Log.d(TAG, "Cliente bajado");
                //User en la BBDD
                if(cliente!=null){
                    Log.d(TAG, "Cliente NO NULL");
                    setCliente(cliente);
                    Log.d(TAG,"Nombre del cliente "+cliente.getNombre());
                }
                //User no en la BBDD
                if(cliente==null){
                    Log.d(TAG, "Cliente NULL");
                }


            }

            @Override
            public void onCancelled(DatabaseError error) {

                Log.w(TAG, "Fallo al leer cliente", error.toException());
            }
        });
    }

    public void gestionLectura(Result result){
        if(result.getText().substring(0,5).equals("USER:")){
        bajarUser(result.getText().substring(5));
        }else if (result.getText().substring(0,9).equals("ARTICULO:")){
            int indice = 0;
            for (int i =0;i<result.getText().length();i++){
                if (result.getText().charAt(i)=='/'){
                    indice = i;
                }
            }

            Log.d(TAG,"Articulo, categoria "+ result.getText().substring(8,indice));
            bajarArticulo(result.getText().substring(9));
        }

    }

    public void bajarArticulo(final String ruta){

        DatabaseReference myRefUsuarios = database.getReference("/articulos/"+ruta);

        myRefUsuarios.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Articulo articulo = dataSnapshot.getValue(Articulo.class);
                Log.d(TAG, "Articulo bajado");
                //User en la BBDD
                if(articulo!=null){
                    Log.d(TAG, "Articulo NO NULL");
                    venta.setArticulo(articulo);
                    gestionElements();
                    Log.d(TAG,"Nombre del articulo "+articulo.getNombre());
                }
                //User no en la BBDD
                if(articulo==null){
                    Log.d(TAG, "Articulo NULL");
                }


            }

            @Override
            public void onCancelled(DatabaseError error) {

                Log.w(TAG, "Fallo al leer artículo", error.toException());
            }
        });
    }

    public void gestionMorph(){
        btnMorph = (MorphingButton) findViewById(R.id.id_leer_cliente);
        Button buttonTerminar = (Button) findViewById(R.id.id_bn_terminar_venta);
        buttonTerminar.setVisibility(View.INVISIBLE);


        if(venta.getArticulo()==null || venta.getUser()==null){
            btnMorph.setVisibility(View.INVISIBLE);

        }else{
            morphToSquare(btnMorph,1);

        }
    }

    //Necesario para que el objeto que se suba tenga una referencia al clinte y al articulo
    //de manera que no se almacenan duplicados de estos objetos
    public void prepararObjetoVenta(){
        venta.setArticuloId(venta.getArticulo().getArticuloId());
        venta.setArticulo(null);

        venta.setClienteId(venta.getUser().getUsuarioId());
        venta.setUser(null);
    }


}