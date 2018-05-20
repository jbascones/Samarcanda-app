package com.jorgebascones.samarcanda;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jorgebascones.samarcanda.Modelos.Articulo;
import com.jorgebascones.samarcanda.Modelos.Post;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class SubirArticulo extends Fragment {

    private Button btnChoose, btnUpload, btnEnviar, btnSiguiente;
    private ImageView imageView;
    public Articulo articulo;
    public RadioGroup radioGroup;
    public String categoria;

    private Uri filePath;

    Context c;

    Bitmap bitmap;

    private final int PICK_IMAGE_REQUEST = 71;

    private View v;

    //Firebase
    FirebaseStorage storage;
    StorageReference storageReference;


    public SubirArticulo() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_elegir_categoria, container, false);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Subir Artículo");
        //Initialize Views

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        c = getActivity().getApplicationContext();

        setHasOptionsMenu(true);

        v=view;

        articulo = new Articulo();

        setElementosElegircategoria();

        return view;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here
        menu.add(Menu.NONE, 1, Menu.NONE, "Rotar a la izquierda")
                .setIcon(android.R.drawable.ic_menu_preferences);
        menu.add(Menu.NONE, 2, Menu.NONE, "Rotar a la derecha")
                .setIcon(android.R.drawable.ic_menu_preferences);
        super.onCreateOptionsMenu(menu, inflater);
    }


    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }
    /*
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {

                bitmap = MediaStore.Images.Media.getBitmap(c.getContentResolver(), filePath);
                bitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, false);
                imageView.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }*/
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 71 && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {

                bitmap = MediaStore.Images.Media.getBitmap(imageView.getContext().getContentResolver(), filePath);
                int w = bitmap.getWidth();
                double escalado = w/1300;
                int h = bitmap.getHeight();
                double newH =h/escalado;
                int hDef = (int) newH;
                bitmap = Bitmap.createScaledBitmap(bitmap,1300, hDef, false);
                imageView.setImageBitmap(bitmap);
                btnUpload.setVisibility(View.VISIBLE);
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
            //final ProgressDialog progressDialog = new ProgressDialog(c);
            //progressDialog.setTitle("Uploading...");
            //progressDialog.show();
            /*
            // Get the data from an ImageView as bytes
            imageView.setDrawingCacheEnabled(true);
            imageView.buildDrawingCache();
            Bitmap bitmap = imageView.getDrawingCache();
            */
            BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
            Bitmap bitmap = drawable.getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();


            //StorageReference ref = storageReference.child("images/"+ UUID.randomUUID().toString());
            Long ruta =  System.currentTimeMillis();
            StorageReference ref = storageReference.child("articulos/"+categoria +"/" +ruta);
            ref.putBytes(data)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //progressDialog.dismiss();
                            //Toast.makeText(this, "Uploaded", Toast.LENGTH_SHORT).show();
                            Log.d("SubirPost","Uploaded");
                            @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            Log.d("SubirPost",downloadUrl.toString());
                            articulo.setFotoUrl(downloadUrl.toString());
                            imageView.setImageResource(R.drawable.ic_menu_send);
                            setViewLayout(R.layout.fragment_subir_articulo_texto);
                            setBtnEnviar();
                            EditText cuerpoEtxt = (EditText) v.findViewById(R.id.editText2);
                            cuerpoEtxt.setImeOptions(EditorInfo.IME_ACTION_DONE);
                            cuerpoEtxt.setRawInputType(InputType.TYPE_CLASS_TEXT);
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
    public void rotarImagen(int signo){
        Matrix matrix = new Matrix();
        matrix.postRotate(signo*90); // giro de 90 grados en contra del sentido del reloj
        bitmap = Bitmap.createBitmap(bitmap , 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix,true);
        imageView.setImageBitmap(bitmap);

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
        if (id == 1) {
            rotarImagen(-1);
            return true;
        }
        if (id == 2) {
            rotarImagen(1);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setViewLayout(int id){
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(id, null);
        ViewGroup rootView = (ViewGroup) getView();
        rootView.removeAllViews();
        rootView.addView(v);
    }

    public void setBtnEnviar(){
        btnEnviar = (Button) v.findViewById(R.id.btnsend);
        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviar();
            }
        });
    }

    public void enviar(){
        EditText tituloEtxt = (EditText)v.findViewById(R.id.editText);
        String titulo = tituloEtxt.getText().toString();
        EditText cuerpoEtxt = (EditText) v.findViewById(R.id.editText2);
        String precio = cuerpoEtxt.getText().toString();
        articulo.setNombre(titulo);
        articulo.setPrecio(Integer.parseInt(precio));
        EditText unidadesEtxt = (EditText)v.findViewById(R.id.editText3);
        String unidades = unidadesEtxt.getText().toString();
        articulo.setUnidades(Integer.parseInt(unidades));
        subirAFirebase(articulo);
    }
    public void subirAFirebase(Object o){

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        Log.d("Subir articulo","Subiendo "+articulo+" a "+categoria);

        DatabaseReference myRef = database.getReference("/articulos/"+categoria);

        myRef.push().setValue(o);

        setViewLayout(R.layout.fragment_post_subido);

    }

    public void setElementosImagen(){
        btnChoose = (Button) v.findViewById(R.id.btnChoose);
        btnUpload = (Button) v.findViewById(R.id.btnUpload);
        imageView = (ImageView) v.findViewById(R.id.imgView);

        btnUpload.setVisibility(View.INVISIBLE);

        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });
    }

    public void setElementosElegircategoria(){
        btnSiguiente = (Button) v.findViewById(R.id.btnSiguiente);
        btnSiguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setViewLayout(R.layout.fragment_subir_post);
                setCategoria();
                setElementosImagen();
            }
        });
        radioGroup = (RadioGroup) v.findViewById(R.id.rdgGrupo);
    }

    public void setCategoria(){
        int cElegida = radioGroup.getCheckedRadioButtonId();
        switch (cElegida){
            case R.id.rdbOne:
                categoria = "camisetas";
                break;
            case R.id.rdbTwo:
                categoria = "pantalones";
                break;
            case R.id.rdbThree:
                categoria = "vestidos";
                break;
        }
    }



}
