package com.jorgebascones.samarcanda;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gordonwong.materialsheetfab.MaterialSheetFab;
import com.gordonwong.materialsheetfab.MaterialSheetFabEventListener;
import com.jorgebascones.samarcanda.Modelos.Comentario;
import com.jorgebascones.samarcanda.Modelos.User;
import com.stephentuso.welcome.WelcomeActivity;
import com.stephentuso.welcome.WelcomeHelper;

import static android.graphics.Color.WHITE;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        TarjetaFragment.OnFragmentInteractionListener{



    private static final String TAG = "Log-MainActivity";

    //Atributos app
    User miUsuario;

    //Atributos Auth
    FirebaseUser user;
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    //Atributos firebase
    DatabaseReference myRef = database.getReference(); //referencia a la raiz

    WelcomeHelper welcomeScreen;

    //Intent pasa valor
    Bundle bundle;

    int counter;

    String fragmenActual;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bundle = getIntent().getExtras();

        setContentView(R.layout.activity_loading);

        descargarPerfil();

        fragmenActual = "inicial";



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
        } else if (id == R.id.action_logout) {

            FirebaseAuth.getInstance().signOut();

            Intent intent = new Intent(this, GoogleSignInActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);


            return true;
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


        } else if (id == R.id.venta_drawer) {


            VentaFragment ventaFragment = new VentaFragment();

            FragmentManager manager = getSupportFragmentManager();

            manager.beginTransaction().replace(R.id.main_fragmento,
                    ventaFragment,
                    ventaFragment.getTag()
            ).commit();

            loading.setVisibility(View.INVISIBLE);

            fragmenActual = "venta";

        } else if (id == R.id.tarjeta_drawer) {

            TarjetaFragment tarjetaFragment = TarjetaFragment.newInstance(miUsuario.getNombre(),miUsuario.getUsuarioId(),miUsuario.getUrlFoto());

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


        } else if (id == R.id.nav_send) {
            SubirPost subirPost = new SubirPost();

            FragmentManager manager = getSupportFragmentManager();

            manager.beginTransaction().replace(R.id.main_fragmento,
                    subirPost,
                    subirPost.getTag()
            ).commit();

            loading.setVisibility(View.INVISIBLE);

            fragmenActual = "subir post";


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        gestionFab();
        return true;
    }


    //Dato recibido del fragmento

    @Override
    public void onFragmentInteraction(String data) {

    }


    //Listener de cuando acaba la pantalla de bienvenida

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == WelcomeHelper.DEFAULT_WELCOME_SCREEN_REQUEST) {
            // The key of the welcome screen is in the Intent
            String welcomeKey = data.getStringExtra(WelcomeActivity.WELCOME_SCREEN_KEY);

            if (resultCode == RESULT_OK) {
                //Intent intent = new Intent(this, MainActivity.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                //startActivity(intent);
            } else {
                // Code here will run if the welcome screen was canceled
                // In most cases you'll want to call finish() here
            }

        }

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

            Log.d(TAG,"descargarPerfil quiere lanzar primera pantalla");

        } else{

            //bajar user de firebase

            bajarUser(id);

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
                    lanzarPrimeraPantalla();
                    Log.d(TAG,"bajar user(no null) quiere lanzar primera pantalla");
                }
                //User no en la BBDD
                if(miUsuario==null){
                    Log.d(TAG, "User NULL");
                    crearUser();
                    lanzarPrimeraPantalla();
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




}
