package com.jorgebascones.samarcanda;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/**
 * A simple {@link Fragment} subclass.
 */

public class PublicarFragment extends Fragment {


    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_inbox, container, false);

        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Publicar");

        Button buttonChangeText = (Button) view.findViewById(R.id.id_botonPublicarInbox);

        final TextView textViewInboxFragment = (TextView) view.findViewById(R.id.id_txtPublicarInbox);
        final EditText editTextInbox = (EditText) view.findViewById(R.id.id_txtEscritoInbox);

        buttonChangeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               /* Log.d("Funciona","Boton activado");
                textViewInboxFragment.setText(editTextInbox.getText());

                Comentario newComentario = new Comentario(user.getUid(),editTextInbox.getText().toString(),user.getDisplayName(),user.getPhotoUrl().toString());

                myRef.child("/mensajes/").push().setValue(newComentario);
                //myRef.child("/mensajes/"+user.getUid()).setValue(editTextInbox.getText().toString());
                Log.d("Funciona","Se publica en /mensajes/"+user.getUid()+" el texto: "+ editTextInbox.getText());*/

                Intent intent = new Intent(getActivity(), LectorQRActivity.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);


            }
        });

        return view;
    }

}
