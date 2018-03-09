package com.jorgebascones.samarcanda;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.jorgebascones.samarcanda.Modelos.CarritoItem;
import com.jorgebascones.samarcanda.Modelos.Fecha;
import com.jorgebascones.samarcanda.Modelos.User;
import com.jorgebascones.samarcanda.Modelos.Venta;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;
import me.dm7.barcodescanner.zxing.ZXingScannerView;


public class LectorQRActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler{

    private ZXingScannerView zXingScannerView;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();
    final String TAG = "venta";
    public Venta venta;
    public MorphingButton btnMorph;
    public boolean ventaCofirmada;
    RecyclerView recyclerView;
    ArrayList<Object> listaValores = new ArrayList<>();

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

        setTextoCarrito();



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

    //TODO: arreglar el tema de cancelar articulos
    public void cancelarArticulo(){
        listaValores.remove(listaValores.size()-1);

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
        //gestionElements();
        gestionLectura(result);

        //zXingScannerView.resumeCameraPreview(this);
        zXingScannerView.stopCamera();
        setContentView(R.layout.activity_main_lector_qr);

    }

    public void setCliente(User cliente){
        venta.setUser(cliente);
        setEdadCliente(cliente);
        gestionElements();
    }

    public void subirVenta (){
        if (ventaCofirmada){
            Log.d("venta","Subiendo venta");
            Fecha fechaVenta = new Fecha();
            venta.setFecha(fechaVenta.getFecha());
            venta.setImporte(calcularPrecio());
            Log.d(TAG,"Importe "+venta.getImporte());
            prepararObjetoVenta();
            myRef.child("/ventas/"+fechaVenta.getRutaVenta()).push().setValue(venta);
            Toast.makeText(getApplicationContext(),"Venta confirmada",Toast.LENGTH_SHORT).show();
            finish();
        }else{

        }
    }



    public void gestionElements(){

        try {

            Log.d(TAG, "Entra en gestion elements");
            if (venta.getUser() == null) {
                View clienteElement = findViewById(R.id.cliente_element);
                clienteElement.setVisibility(View.INVISIBLE);
                Button botonLeer = (Button) findViewById(R.id.id_button_leer_cliente);
                botonLeer.setVisibility(View.VISIBLE);
            } else {
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
            if (listaValores.size() == 0) {
                View clienteElement = findViewById(R.id.articulo_element);
                clienteElement.setVisibility(View.INVISIBLE);
                Button botonLeer = (Button) findViewById(R.id.id_button_leer_articulo);
                botonLeer.setVisibility(View.VISIBLE);
            } else {
                CarritoItem carritoItem = (CarritoItem) listaValores.get(listaValores.size() - 1);
                Articulo articuloActual = carritoItem.getCarritoItem();
                View clienteElement = findViewById(R.id.articulo_element);
                clienteElement.setVisibility(View.VISIBLE);
                TextView id = (TextView) findViewById(R.id.id_element_id_a);
                id.setText("Artículo");
                TextView nombre = (TextView) findViewById(R.id.id_element_nombre_a);
                nombre.setText(articuloActual.getNombre());
                TextView unidades = (TextView) findViewById(R.id.id_unidades);
                unidades.setText("x"+carritoItem.getUnidades());
                TextView precio = (TextView) findViewById(R.id.id_element_precio);
                precio.setText(carritoItem.getCarritoItem().getPrecio()+"€");
                ImageView icon = (ImageView) findViewById(R.id.id_element_icon_a);
                Context c = getApplicationContext();
                Picasso.with(c).load(articuloActual.getFotoUrl()).into(icon);
                Button botonLeer = (Button) findViewById(R.id.id_button_leer_articulo);
                botonLeer.setVisibility(View.INVISIBLE);
            }
            setTextoCarrito();
            visibilityAddACarrito();
            visibilityIrCarrito();
        }catch (Exception e){
            Log.d(TAG,"Error visibility que crashea la app");
        }
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

            Log.d(TAG,"Articulo, categorias "+ result.getText().substring(8,indice));
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
                    venta.addNumeroArticulos();
                    addArticulo(articulo);
                    gestionElements();
                    Log.d(TAG,"Nombre del articulo "+articulo.getNombre());
                    Log.d(TAG,"Articulos en carrito "+listaValores.size());
                    setTextoCarrito();
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



    //Necesario para que el objeto que se suba tenga una referencia al clinte y al articulo
    //de manera que no se almacenan duplicados de estos objetos
    public void prepararObjetoVenta(){

        String idsArticulos = "";

        for(int i =0; i<listaValores.size();i++){
            CarritoItem aux = (CarritoItem)listaValores.get(i);
            Log.d("idsArticulos",aux.getCarritoItem().getArticuloId());
            idsArticulos = idsArticulos + aux.getCarritoItem().getArticuloId()+"&";
        }

        venta.setArticuloId(idsArticulos);

        venta.setClienteId(venta.getUser().getUsuarioId());
        venta.setUser(null);

    }

    public void setEdadCliente(User cliente){

        Fecha fecha = new Fecha();

        venta.setEdadVenta(fecha.calcularEdadVenta(cliente.getFechaNacimiento()));
    }

    private void bindDataToAdapter() {
        // Bind adapter to recycler view object
        Log.d(TAG,"Lista valores size "+listaValores.size());
        recyclerView.setAdapter(new ComplexRecyclerViewAdapter(listaValores));



    }


    public void manual(View v){
        bajarArticulo("camisetas/0123456789");
    }

    public void setupRecyclerView(){
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(false);

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

    public void setTextoCarrito(){
        try {
            TextView text = (TextView) findViewById(R.id.id_txt_articulos_carrito);
            String s = "s";
            if (venta.getNumeroArticulos() == 1) {
                s = "";
            }
            text.setText("Hay " + venta.getNumeroArticulos() + " artículo" + s + " en el carrito");
        }catch (Exception e){
            Log.d(TAG,"Crash por visibility de textoCarrito");
        }
    }

    public void visibilityAddACarrito(){
        Button boton = (Button) findViewById(R.id.id_bn_siguiente_articulo);

        if(venta.getNumeroArticulos()>0){
            boton.setVisibility(View.VISIBLE);
        }else{
            boton.setVisibility(View.INVISIBLE);
        }
    }

    public void visibilityIrCarrito(){
        Button boton = (Button) findViewById(R.id.id_bn_ir_carrito);
        if(venta.getUser() != null && venta.getNumeroArticulos()>0){
            boton.setVisibility(View.VISIBLE);
        } else {
            boton.setVisibility(View.INVISIBLE);
        }
    }

    public void lanzarPantallaCarrito(View v){
        setContentView(R.layout.activity_carrito);
        setupRecyclerView();
        bindDataToAdapter();
        setPrecioCarrito();
    }

    public void lanzarPantallaMetodoPago(View v){
        setContentView(R.layout.activity_pagar);
    }

    public void setPrecioCarrito(){
        TextView precio = (TextView) findViewById(R.id.txt_pagar);
        precio.setText(calcularPrecio()+"€");
    }

    public void pagado(View v){
        ventaCofirmada = true;
        subirVenta();
        finish();
    }

    public int calcularPrecio(){
        int precio =0;
        for (int i =0; i<listaValores.size();i++){
            CarritoItem aux = (CarritoItem) listaValores.get(i);
            precio = precio + aux.getCarritoItem().getPrecio();
        }
        return precio;
    }

    //Al añadir un articulo, si ya estaba en la lista aumenta una unidad del objeto CarritoItem. Si no estaba, lo añade
    public void addArticulo(Articulo articulo){

        if(listaValores.size()==0){
            CarritoItem carritoItem = new CarritoItem(articulo);
            listaValores.add(carritoItem);
        }else{
            for(int i=0;i<listaValores.size();i++){
                CarritoItem aux = (CarritoItem) listaValores.get(i);
                if(aux.getCarritoItem().getArticuloId().equals(articulo.getArticuloId())){
                    aux.addUnidad();
                    listaValores.remove(i);
                    listaValores.add(i,aux);
                }else{
                    CarritoItem carritoItem = new CarritoItem(articulo);
                    listaValores.add(carritoItem);
                }
            }
        }

    }

    public void restaArticulo(Articulo articulo){

        for(int i=0;i<listaValores.size();i++){
            CarritoItem aux = (CarritoItem) listaValores.get(i);
            if(aux.getCarritoItem().getArticuloId().equals(articulo.getArticuloId())){
                aux.restaUnidad();
                listaValores.remove(i);
                if(aux.getUnidades()>0){
                    listaValores.add(i,aux);
                }

                }else{

                }
            }


    }

    public void modificarArticulo(View v){

        CarritoItem carritoItem = (CarritoItem) listaValores.get(listaValores.size()-1);

        dialogModificar(carritoItem);
    }

    public void dialogModificar(final CarritoItem carritoItem){
        View view = findViewById(R.id.id_unidades);
        final PrettyDialog dialog = new PrettyDialog(view.getContext());
        dialog
                .setTitle("¿Qué deseas modificar?")
                .setMessage(carritoItem.getUnidades()+" unidades")
                .addButton(
                        "\nAñadir unidad\n",					// button text
                        R.color.pdlg_color_white,		// button text color
                        R.color.pdlg_color_green,		// button background color
                        new PrettyDialogCallback() {		// button OnClick listener
                            @Override
                            public void onClick() {
                                addArticulo(carritoItem.getCarritoItem());
                                dialogModificar(carritoItem);
                                gestionElements();
                                venta.addNumeroArticulos();
                                setTextoCarrito();
                                dialog.dismiss();
                            }
                        }
                )

                // Cancel button
                .addButton(
                        "\nRestar unidad\n",
                        R.color.pdlg_color_white,
                        R.color.pdlg_color_red,
                        new PrettyDialogCallback() {
                            @Override
                            public void onClick() {
                                restaArticulo(carritoItem.getCarritoItem());
                                if(listaValores.size()>0){
                                    dialogModificar(carritoItem);
                                }else{
                                    Button button = (Button) findViewById(R.id.id_bn_siguiente_articulo);
                                    button.setVisibility(View.INVISIBLE);
                                }
                                venta.restaNumeroArticulos();
                                gestionElements();
                                setTextoCarrito();
                                dialog.dismiss();
                            }
                        }
                )

                // 3rd button
                .addButton(
                        "\nBorrar artículo\n",
                        R.color.pdlg_color_black,
                        R.color.pdlg_color_gray,
                        new PrettyDialogCallback() {
                            @Override
                            public void onClick() {
                                cancelarArticulo();
                                Button button = (Button) findViewById(R.id.id_bn_siguiente_articulo);
                                button.setVisibility(View.INVISIBLE);
                                venta.setNumeroArticulos(0);
                                setTextoCarrito();
                                dialog.dismiss();
                            }
                        }
                )
                .setAnimationEnabled(false)
                .show();
    }


}