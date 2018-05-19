package com.jorgebascones.samarcanda;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gordonwong.materialsheetfab.MaterialSheetFab;
import com.gordonwong.materialsheetfab.MaterialSheetFabEventListener;
import com.jorgebascones.samarcanda.Modelos.CarritoItem;
import com.jorgebascones.samarcanda.Modelos.Celda;
import com.jorgebascones.samarcanda.Modelos.Comentario;
import com.jorgebascones.samarcanda.Modelos.Fecha;
import com.jorgebascones.samarcanda.Modelos.Reserva;
import com.jorgebascones.samarcanda.Modelos.User;
import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment;
import com.squareup.picasso.Picasso;
import com.stephentuso.welcome.WelcomeHelper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import io.ghyeok.stickyswitch.widget.StickySwitch;
import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;

import static android.graphics.Color.WHITE;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        MiPerfilFragment.OnFragmentInteractionListener{



    private static final String TAG = "Log-MainActivity";

    //Atributos app
    User miUsuario;
    boolean isVendedor;
    boolean [] infoDescargada;

    //Atributos Auth
    FirebaseUser user;
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    //Atributos firebase
    DatabaseReference myRef = database.getReference(); //referencia a la raiz

    //Intent pasa valor
    Bundle bundle;

    int counter;

    String fragmenActual;

    //Fecha elegida en el calendario al crear perfil
    String fechaElegidaCalendario;

    //Variables para la creacion de nuevos users
    private SwitchDateTimeDialogFragment dateTimeFragment;
    RecyclerView recyclerView;
    public ArrayList<Object> lista = new ArrayList<>();
    ComplexRecyclerViewAdapter adapter;
    private Uri filePath;
    Bitmap bitmap;
    StorageReference storageReference;
    FirebaseStorage storage;
    boolean subir;

    private final int REALIZAR_VENTA = "realizar venta".hashCode();
    private final int SUBIR_POST = "subir post".hashCode();
    private final int PAGO = "pago".hashCode();
    private final int RESERVAS = "reservas".hashCode();
    private final int MI_PERFIL = "mi perfil".hashCode();
    private final int MIS_RESERVAS = "mis reservas".hashCode();
    private final int MIS_COMPRAS = "mis compras".hashCode();

    private Menu optionsMenu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bundle = getIntent().getExtras();

        setContentView(R.layout.activity_loading);

        descargarPerfil();

        fragmenActual = "inicial";

        isOnline();

        isVendedor = false;

        infoDescargada = new boolean[2];

    }


    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        //isOnline();
    }

    public void lanzarPrimeraPantalla(){

        counter++;

        Log.d(TAG,"Lanzada pantalla inicial "+counter);


        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Hola de nuevo "+miUsuario.getNombre(), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */

        //fab.callOnClick();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        gestionFab();

        gestionComentario();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        gestionDrawer(navigationView.getMenu());

        Snackbar.make(getWindow().getDecorView().getRootView(), "Hola de nuevo "+miUsuario.getNombre(), Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();

        lanzarNoticiasFragment();

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            super.onBackPressed();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        construirMenu(menu);
        getMenuInflater().inflate(R.menu.main, menu);
        optionsMenu = menu;
        setOptions();
        return true;
    }
    //TODO: menu propio de cada fragment
    public void construirMenu(Menu menu){
        menu.add(Menu.NONE, 1, Menu.NONE, "Opción con código")
                .setIcon(android.R.drawable.ic_menu_preferences);
        menu.removeItem(1);
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
        } else if (id == R.id.action_logout) {

            FirebaseAuth.getInstance().signOut();

            Intent intent = new Intent(this, GoogleSignInActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);


            return true;
        } else if(id=="simulador".hashCode()){
            SimuladorFragment simuladorFragment = new SimuladorFragment();

            FragmentManager manager = getSupportFragmentManager();

            manager.beginTransaction().replace(R.id.main_fragmento,
                    simuladorFragment,
                    simuladorFragment.getTag()
            ).commit();


            fragmenActual = "simulador";
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        ImageView loading = (ImageView) findViewById(R.id.loading);
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_noticias) {

            lanzarNoticiasFragment();

        } else if (id == R.id.nav_comentarios) {

            ComentariosFragment galeriaFragment = new ComentariosFragment();

            FragmentManager manager = getSupportFragmentManager();

            manager.beginTransaction().replace(R.id.main_fragmento,
                    galeriaFragment,
                    galeriaFragment.getTag()
            ).commit();

            loading.setVisibility(View.INVISIBLE);

            fragmenActual = "comentarios";


        } else if (id == REALIZAR_VENTA) {


            VentaFragment ventaFragment = new VentaFragment();

            FragmentManager manager = getSupportFragmentManager();

            manager.beginTransaction().replace(R.id.main_fragmento,
                    ventaFragment,
                    ventaFragment.getTag()
            ).commit();

            loading.setVisibility(View.INVISIBLE);

            fragmenActual = "venta";

        } else if (id == MI_PERFIL) {

            MiPerfilFragment tarjetaFragment = MiPerfilFragment.newInstance(miUsuario.getNombre(),miUsuario.getUsuarioId(),miUsuario.getUrlFoto());

            FragmentManager manager = getSupportFragmentManager();

            manager.beginTransaction().replace(R.id.main_fragmento,
                    tarjetaFragment,
                    tarjetaFragment.getTag()
            ).commit();

            loading.setVisibility(View.INVISIBLE);

            fragmenActual = "tarjeta";


        } else if (id == R.id.nav_catalogo) {

            CatalogoFragment catalogoFragment = new CatalogoFragment();

            FragmentManager manager = getSupportFragmentManager();

            manager.beginTransaction().replace(R.id.main_fragmento,
                    catalogoFragment,
                    catalogoFragment.getTag()
            ).commit();

            loading.setVisibility(View.INVISIBLE);
            fragmenActual = "catalogo";


        } else if (id == SUBIR_POST) {
            SubirPost subirPost = new SubirPost();

            FragmentManager manager = getSupportFragmentManager();

            manager.beginTransaction().replace(R.id.main_fragmento,
                    subirPost,
                    subirPost.getTag()
            ).commit();

            loading.setVisibility(View.INVISIBLE);

            fragmenActual = "subir post";


        }else if (id == PAGO) {
            PagoFragment pagoFragment = new PagoFragment();

            FragmentManager manager = getSupportFragmentManager();

            manager.beginTransaction().replace(R.id.main_fragmento,
                    pagoFragment,
                    pagoFragment.getTag()
            ).commit();

            loading.setVisibility(View.INVISIBLE);

            fragmenActual = "pago";


        }else if (id == RESERVAS) {
            ReservasFragment reservasFragment = new ReservasFragment();

            FragmentManager manager = getSupportFragmentManager();

            manager.beginTransaction().replace(R.id.main_fragmento,
                    reservasFragment,
                    reservasFragment.getTag()
            ).commit();

            loading.setVisibility(View.INVISIBLE);

            fragmenActual = "reservas";


        }else if (id == MIS_RESERVAS) {
            MisReservasFragment misReservasFragment = new MisReservasFragment();

            FragmentManager manager = getSupportFragmentManager();

            manager.beginTransaction().replace(R.id.main_fragmento,
                    misReservasFragment,
                    misReservasFragment.getTag()
            ).commit();

            loading.setVisibility(View.INVISIBLE);

            fragmenActual = "mis reservas";


        }else if (id == MIS_COMPRAS) {
            MisComprasFragment misComprasFragment = new MisComprasFragment();

            FragmentManager manager = getSupportFragmentManager();

            manager.beginTransaction().replace(R.id.main_fragmento,
                    misComprasFragment,
                    misComprasFragment.getTag()
            ).commit();

            loading.setVisibility(View.INVISIBLE);

            fragmenActual = "mis compras";


        }

        Log.d("Menu",item.getItemId()+"");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        gestionFab();
        return true;
    }

    public void gestionDrawer(Menu menu){

        if(isVendedor){
            setDrawerVendedor(menu);
        }else{
            setDrawerCliente(menu);
        }

        for(int i=0;i<menu.size();i++){
            menu.getItem(i).setCheckable(true);
        }


    }


    //Dato recibido del fragmento

    @Override
    public void onFragmentInteraction(String data) {

    }





/*
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        welcomeScreen.onSaveInstanceState(outState);
    }


*/

    public void descargarPerfil(){

        //Se recoge el string pasado desde la Activity anterior

        String id = bundle.getString("id");

        if(id.equals("vacio")) {

            //Se acaba de hacer login, la sesion no estaba iniciada
            //Puede ser que el usuario sea nuevo
            Log.d("Firebase","Entro en bucle");

            User nuevoUser = new User();

            Log.d("Firebase","Salgo del bucle");

            bajarUser(nuevoUser.usuarioId);

            isVendedor(nuevoUser.usuarioId);

            Log.d(TAG,"descargarPerfil quiere lanzar primera pantalla");

        } else{

            //bajar user de firebase

            bajarUser(id);

            isVendedor(id);

        }

    }

    public void bajarUser(final String userId){

        DatabaseReference myRefUsuarios = database.getReference("/users/perfiles/"+userId);

        myRefUsuarios.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                miUsuario = dataSnapshot.getValue(User.class);
                Log.d(TAG, "User bajado");
                //User en la BBDD
                if(miUsuario!=null){
                    Log.d(TAG, "User NO NULL");
                    if(miUsuario.getEstatus()==1){
                        infoDescargada[0] = true;
                        if(infoDescargada[0]==true && infoDescargada[1]==true){
                            lanzarPrimeraPantalla();
                        }
                    } else if(miUsuario.getEstatus()==0){

                        lanzarCreacionUser();
                    }

                    Log.d(TAG,"bajar user(no null) quiere lanzar primera pantalla");
                }
                //User no en la BBDD
                if(miUsuario==null){
                    Log.d(TAG, "User NULL");
                    crearUser();
                    lanzarCreacionUser();
                    Log.d(TAG,"bajar user (null) quiere lanzar primera pantalla");
                    Log.d(TAG, "User null "+userId);
                }


            }

            @Override
            public void onCancelled(DatabaseError error) {

                Log.w(TAG, "Fallo al leer mi Usuario.", error.toException());
            }
        });
    }

    public void crearUser(){

        Log.d(TAG,"Entro en bucle");

        miUsuario = new User();

        Log.d(TAG,"Salgo del bucle");


        subirAFirebase(miUsuario,"/users/perfiles/"+miUsuario.getUsuarioId());
        subirIdAFirebasePush();

        Log.d(TAG,"Subo a firebase el user "+miUsuario.getUsuarioId());

    }

    public void subirAFirebase(Object o, String ruta){


        myRef.child(ruta).setValue(o);

    }

    public void subirIdAFirebasePush(){

        DatabaseReference myRefIds = database.getReference();

        myRefIds.child("/users/ids/").push().setValue(miUsuario.getUsuarioId());

        Log.d(TAG,"Se ha añadido ids "+miUsuario.getUsuarioId());



    }

    public void gestionFab(){
        Fab fab = (Fab) findViewById(R.id.fab);
        View sheetView = findViewById(R.id.fab_sheet);
        View overlay = findViewById(R.id.overlay);
        int sheetColor = WHITE;//getResources().getColor(R.color.colorPrimary);
        int fabColor = getResources().getColor(R.color.colorAccent);

        // Initialize material sheet FAB
        final MaterialSheetFab materialSheetFab = new MaterialSheetFab<>(fab, sheetView, overlay,
                sheetColor, fabColor);

        materialSheetFab.setEventListener(new MaterialSheetFabEventListener() {
            @Override
            public void onShowSheet() {
                // Called when the material sheet's "show" animation starts.

            }

            @Override
            public void onSheetShown() {
                // Called when the material sheet's "show" animation ends.

            }

            @Override
            public void onHideSheet() {
                //Para que el teclado se esconda cuando se cierra la sheet
                // Check if no view has focus:
                View view = getWindow().getDecorView().getRootView();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

            }

            public void onSheetHidden() {
                // Called when the material sheet's "hide" animation ends.

            }
        });
        if (fragmenActual.equals("comentarios")){
            fab.setVisibility(View.VISIBLE);
        }else {
            fab.setVisibility(View.INVISIBLE);
        }

        }

        public void gestionComentario(){
            final EditText editTextInbox = (EditText) findViewById(R.id.id_txtEscritoInboxMain);

            Button buttonChangeText = (Button) findViewById(R.id.id_botonPublicarInboxMain);

            buttonChangeText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("Funciona","Boton activado");
                    user = FirebaseAuth.getInstance().getCurrentUser();
                    Comentario newComentario = new Comentario(user.getUid(),editTextInbox.getText().toString(),user.getDisplayName(),user.getPhotoUrl().toString());

                    myRef.child("/mensajes/").push().setValue(newComentario);
                    //myRef.child("/mensajes/"+user.getUid()).setValue(editTextInbox.getText().toString());
                    Log.d("Funciona","Se publica en /mensajes/"+user.getUid()+" el texto: "+ editTextInbox.getText());
                    editTextInbox.setText("");

                }
            });
        }

        public void lanzarNoticiasFragment(){
            try {
                NoticiasFragment noticiasFragment = new NoticiasFragment();

                FragmentManager manager = getSupportFragmentManager();

                manager.beginTransaction().replace(R.id.main_fragmento,
                        noticiasFragment,
                        noticiasFragment.getTag()
                ).commit();


                fragmenActual = "noticias";
            }catch (Exception e ){
                Log.d("Crash","Crash por onSaveInstanceState");
            }

        }

        public void lanzarCreacionUser(){
            setContentView(R.layout.flujo_crear_user_1);
        }

        public void mostrarFlujo2(View v){
            View f1 = findViewById(R.id.flujo1);
            f1.setVisibility(View.INVISIBLE);
            View f2 = findViewById(R.id.flujo2);
            f2.setVisibility(View.VISIBLE);
            EditText editText = (EditText) findViewById(R.id.editText_nombre);
            editText.setText(""+miUsuario.getNombre());

        }

        public void mostrarDatePicker(View v){

        }

    public void gestionarFragmentCalendario(View v){

        //final TextView pruebaTV = (TextView) findViewById(R.id.pruebaCalendarioId);

        // Construct SwitchDateTimePicker
        dateTimeFragment = (SwitchDateTimeDialogFragment) getSupportFragmentManager().findFragmentByTag("TAG_DATETIME_FRAGMENT");
        if(dateTimeFragment == null) {
            dateTimeFragment = SwitchDateTimeDialogFragment.newInstance(
                    getString(R.string.label_datetime_dialog),
                    getString(R.string.positive_button_datetime_picker),
                    getString(R.string.negative_button_datetime_picker)
            );
        }

        // Assign values we want
        final Fecha fecha = new Fecha();
        final SimpleDateFormat myDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", java.util.Locale.getDefault());
        dateTimeFragment.startAtCalendarView();
        dateTimeFragment.set24HoursMode(false);
        dateTimeFragment.setMinimumDateTime(new GregorianCalendar(1900,1 ,1 ).getTime());
        dateTimeFragment.setMaximumDateTime(new GregorianCalendar(fecha.getIntAnno(),fecha.getIntMes() ,fecha.getIntDia() ).getTime());
        dateTimeFragment.setDefaultDateTime(new GregorianCalendar(fecha.getIntAnno(),fecha.getIntMes() -1 , fecha.getIntDia()).getTime());

        // Define new day and month format
        try {
            dateTimeFragment.setSimpleDateMonthAndDayFormat(new SimpleDateFormat("MMMM dd", Locale.getDefault()));
        } catch (SwitchDateTimeDialogFragment.SimpleDateMonthAndDayFormatException e) {
            Log.e(TAG, e.getMessage());
        }

        // Set listener for date
        dateTimeFragment.setOnButtonClickListener(new SwitchDateTimeDialogFragment.OnButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Date date) {

                TextView txt6 = (TextView) findViewById(R.id.textView6);
                fechaElegidaCalendario = myDateFormat.format(date);
                String fechaLeer = fecha.fechaPreparada(fechaElegidaCalendario,false );
                txt6.setText(fechaLeer);
                Log.d("Calendario",myDateFormat.format(date));
                Button button = (Button) findViewById(R.id.bn_continuar);
                button.setVisibility(View.VISIBLE);

            }

            @Override
            public void onNegativeButtonClick(Date date) {

            }
        });

        Button buttonView = (Button) findViewById(R.id.bn_calendario);
        buttonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateTimeFragment.show(getSupportFragmentManager(), "TAG_DATETIME_FRAGMENT");
            }
        });
    }

    public void lanzarElegirFoto(View v){
        View v1 = findViewById(R.id.flujo2);
        View v2 = findViewById(R.id.flujo3);
        v1.setVisibility(View.INVISIBLE);
        v2.setVisibility(View.VISIBLE);
        prepararElegirFoto();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        setInfo();
    }

    public void prepararElegirFoto(){
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2, GridLayoutManager.VERTICAL, false));
        rellenarLista();
        subir = false;
        visibilityRotar(false);
    }


    private void bindDataToAdapter() {

        final ImageView imagenElegida = (ImageView) findViewById(R.id.imagen_elegida);

        // Bind adapter to recycler view object

        adapter = new ComplexRecyclerViewAdapter(lista);

        adapter.setOnItemClickListener(new ComplexRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Celda cElegida = (Celda) lista.get(position);
                if(cElegida.getTipo().equals("Elegir foto galeria")){
                    chooseImage();
                    subir = true;
                    visibilityRotar(true);
                    visibilityTerminar();
                }else{
                    Picasso.with(getApplicationContext()).load(cElegida.getFotoUrl()).resize(600,600).into(imagenElegida);
                    miUsuario.setUrlFoto(cElegida.getFotoUrl());
                    subir = false;
                    visibilityRotar(false);
                    visibilityTerminar();
                }
            }
        });

        recyclerView.setAdapter(adapter);


    }

    public void rellenarLista(){
        Celda galeria = new Celda("Elegir foto galeria");
        galeria.setTexto("Elegir foto de la galería");
        Celda google = new Celda("Elegir Foto");
        google.setTexto("Foto de perfil de Google");
        google.setFotoUrl(miUsuario.getUrlFoto());
        lista.add(galeria);
        lista.add(google);
        descargarListaFotos();
    }

    public void descargarListaFotos(){

        // Read from the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference myRef = database.getReference("/Valores/fotos");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                    Log.v(TAG,""+ childDataSnapshot.getKey()); //displays the key for the node
                    Log.v(TAG,""+ childDataSnapshot.getValue());
                    addUrlFoto(childDataSnapshot.getValue(String.class));

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

    public void addUrlFoto(String newValue){
        try {
            boolean add = true;

            for (int i=1; i<lista.size(); i++){
                Celda c = (Celda) lista.get(i);
                if(c.getFotoUrl().equals(newValue)){
                    add = false;
                }
            }

            if(add){
                Celda newCelda = new Celda("Elegir Foto");
                newCelda.setFotoUrl(newValue);
                int numero = lista.size() -1;
                newCelda.setTexto("Icono "+ numero);
                lista.add(newCelda);
            }
        }catch (Exception e){

        }

    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 71);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 71 && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                ImageView imageView = (ImageView) findViewById(R.id.imagen_elegida);
                bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), filePath);
                int w = bitmap.getWidth();
                double escalado = w/600;
                int h = bitmap.getHeight();
                double newH =h/escalado;
                int hDef = (int) newH;
                bitmap = Bitmap.createScaledBitmap(bitmap,600, hDef, false);
                imageView.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage() {


        if(filePath != null)
        {

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();


            //StorageReference ref = storageReference.child("images/"+ UUID.randomUUID().toString());
            String ruta = miUsuario.getUsuarioId();
            StorageReference ref = storageReference.child("fotos_perfil/"+ ruta);
            ref.putBytes(data)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //progressDialog.dismiss();
                            //Toast.makeText(this, "Uploaded", Toast.LENGTH_SHORT).show();
                            Log.d("SubirPost","Uploaded");
                            @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            miUsuario.setUrlFoto(downloadUrl.toString());
                            lanzarPrimeraPantalla();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //progressDialog.dismiss();
                            //Toast.makeText(this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.d("SubirPost","Failed"+e.getMessage());
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                            //        .getTotalByteCount());
                            //progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
    }

    public void rotarImagen(View v){
        Matrix matrix = new Matrix();
        matrix.postRotate(-90); // giro de 90 grados en contra del sentido del reloj
        bitmap = Bitmap.createBitmap(bitmap , 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix,true);
        ImageView imagenElegida = (ImageView) findViewById(R.id.imagen_elegida);
        imagenElegida.setImageBitmap(bitmap);

    }

    public void visibilityRotar(boolean visible){
        try{
            Button button = (Button) findViewById(R.id.bn_rotar);
            if(visible){
                button.setVisibility(View.VISIBLE);
            }else{
                button.setVisibility(View.INVISIBLE);
            }
        }catch (Exception e){

        }

    }

    public void terminar(View v){
        setContentView(R.layout.activity_loading);
        if(subir){
            uploadImage();
        }else{

            lanzarPrimeraPantalla();
        }
        subirAFirebase(miUsuario,"/users/perfiles/"+miUsuario.getUsuarioId());
    }

    public void visibilityTerminar(){
        Button botonContinuar = (Button) findViewById(R.id.bn_terminar);
        botonContinuar.setVisibility(View.VISIBLE);
    }

    public void setInfo(){
        EditText editText = (EditText) findViewById(R.id.editText_nombre);
        miUsuario.setNombre(editText.getText().toString());
        StickySwitch stickySwitch = (StickySwitch) findViewById(R.id.sticky_switch);
        StickySwitch.Direction d = stickySwitch.getDirection();
        if(d == StickySwitch.Direction.LEFT){
            miUsuario.setGenero("masculino");
        }else{
            miUsuario.setGenero("femenino");
        }
        miUsuario.setFechaNacimiento(fechaElegidaCalendario.substring(0,10));
        miUsuario.setEstatus(1);
    }

    public boolean isOnline() {
        ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if(netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()){
            Toast.makeText(getApplicationContext(), "No hay conexión a Internet", Toast.LENGTH_LONG).show();
            dialogModificar();
            return false;
        }
        return true;
    }

    public void dialogModificar(){

        final PrettyDialog dialog = new PrettyDialog(MainActivity.this);
        dialog
                .setTitle("¡¡Sin conexión!!")
                .setMessage("Revisa tu red")
                .addButton(
                        "Reintentar",					// button text
                        R.color.pdlg_color_white,		// button text color
                        R.color.pdlg_color_green,		// button background color
                        new PrettyDialogCallback() {		// button OnClick listener
                            @Override
                            public void onClick() {
                              isOnline();
                            }
                        }
                )

                // Cancel button
                .addButton(
                        "Cerrar la app",
                        R.color.pdlg_color_white,
                        R.color.pdlg_color_red,
                        new PrettyDialogCallback() {
                            @Override
                            public void onClick() {
                                finish();
                            }
                        }
                )


                .setAnimationEnabled(false)
                .show();
    }

    public void setDrawerVendedor(Menu menu){
        menu.add(Menu.NONE,REALIZAR_VENTA,Menu.NONE,"Ventas")
                .setIcon(R.drawable.ic_menu_slideshow);
        menu.add(Menu.NONE,RESERVAS,Menu.NONE,"Reservas")
                .setIcon(R.drawable.ic_venta_confirmada);
        menu.add(Menu.NONE,SUBIR_POST,Menu.NONE,"Subir Post")
                .setIcon(R.drawable.ic_menu_send);
    }

    public void setDrawerCliente(Menu menu){
        menu.add(Menu.NONE,MI_PERFIL,Menu.NONE,"Mi perfil")
                .setIcon(R.mipmap.ic_tarjeta);
        menu.add(Menu.NONE,MIS_RESERVAS,Menu.NONE,"Mis reservas")
                .setIcon(R.drawable.ic_venta_confirmada);
        menu.add(Menu.NONE,MIS_COMPRAS,Menu.NONE,"Mis compras")
                .setIcon(R.drawable.ic_menu_gallery);
    }

    public void isVendedor(String userId){
        DatabaseReference myRefVendedores = database.getReference("/vendedores/"+userId);
        myRefVendedores.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Boolean value = dataSnapshot.getValue(Boolean.class);

                if(value != null){
                    isVendedor = true;
                }else{
                    isVendedor= false;
                }

                infoDescargada[1] = true;

                if(infoDescargada[0]==true && infoDescargada[1]==true){
                    lanzarPrimeraPantalla();
                }

                Log.d(TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    public void setOptions(){
        if(isVendedor){
            optionsMenu.add(Menu.NONE, "simulador".hashCode(), Menu.NONE, "Simulador")
                    .setIcon(android.R.drawable.ic_menu_preferences);
        }
    }






}
