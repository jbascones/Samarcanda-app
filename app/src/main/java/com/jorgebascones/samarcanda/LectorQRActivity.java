package com.jorgebascones.samarcanda;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;


public class LectorQRActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler{

    private ZXingScannerView zXingScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_lector_qr);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


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
        Toast.makeText(getApplicationContext(),result.getText(),Toast.LENGTH_SHORT).show();
        //zXingScannerView.resumeCameraPreview(this);
        zXingScannerView.stopCamera();
        setContentView(R.layout.activity_main_lector_qr);

    }
}