package com.jorgebascones.samarcanda;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jorgebascones.samarcanda.Modelos.Fecha;
import com.jorgebascones.samarcanda.Modelos.User;
import com.squareup.picasso.Picasso;

import ch.halcyon.squareprogressbar.SquareProgressBar;
import ch.halcyon.squareprogressbar.utils.PercentStyle;
import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MiPerfilFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MiPerfilFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MiPerfilFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    // TODO: Rename and change types of parameters
    private String miNombre;
    private String miUserId;
    private String miUrl;
    private SquareProgressBar squareProgressBar;
    private FirebaseDatabase database;
    DatabaseReference myRef;
    private View view;



    private OnFragmentInteractionListener mListener;

    public MiPerfilFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @param param3 Parameter 3.
     * @return A new instance of fragment MiPerfilFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MiPerfilFragment newInstance(String param1, String param2, String param3) {
        MiPerfilFragment fragment = new MiPerfilFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            miNombre = getArguments().getString(ARG_PARAM1);
            miUserId = getArguments().getString(ARG_PARAM2);
            miUrl = getArguments().getString(ARG_PARAM3);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.tarjeta, container, false);

        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Mi Perfil");

        TextView nombre = (TextView)view.findViewById(R.id.nombreText);
        nombre.setText("Nombre");
        TextView mail = (TextView)view.findViewById(R.id.segundoCampo);
        mail.setText("Email");

        colocarFoto(view);

        rellenarTarjeta(view);

        database = FirebaseDatabase.getInstance();

        colocarQR(view);

        colocarFoto(view);

        setProgressSquare(view);

        descargarPuntos();

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String data) {
        if (mListener != null) {
            mListener.onFragmentInteraction(data);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        // TODO: Manda informacion a MainActivity
        void onFragmentInteraction(String data);
    }

    public void colocarQR(View view){

        ImageView imageView = (ImageView) view.findViewById(R.id.codigo_qr);
        CreaQr creaQr = new CreaQr();
        Bitmap bitmap = creaQr.generarQR("USER:"+miUserId);
        imageView.setImageBitmap(bitmap);



    }

    public void colocarFoto(View view){
        ImageView imagen = (ImageView) view.findViewById(R.id.id_card_foto);

        Context c = getActivity().getApplicationContext();

        Picasso.with(c).load(miUrl).placeholder(R.drawable.inicio).into(imagen);

    }


    public void rellenarTarjeta(View view){
        User yo = new User();
        TextView nombreTV = (TextView) view.findViewById(R.id.id_nombre_card);
        nombreTV.setText(miNombre);
        TextView mailTV = (TextView) view.findViewById(R.id.id_segundoCampo);
        mailTV.setText(yo.getMail());
    }

    public void setProgressSquare(View v){
        squareProgressBar = (SquareProgressBar) v.findViewById(R.id.sprogressbar);
        squareProgressBar.showProgress(true);
        squareProgressBar.setImage(R.drawable.circle_translucent);
        squareProgressBar.setColor("#ffa000");
        squareProgressBar.setRoundedCorners(true);
        PercentStyle percentStyle = new PercentStyle(Paint.Align.CENTER, 60, true);
        percentStyle.setCustomText("/100 puntos");
        squareProgressBar.setPercentStyle(percentStyle);
        squareProgressBar.setProgress(50.0);
        squareProgressBar.drawStartline(true);

        squareProgressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogInfo();
            }
        });
    }

    public void setProgress(float p){
        squareProgressBar.setProgress(p);
    }

    public void descargarPuntos(){
        myRef = database.getReference("estadisticas/puntos/"+miUserId);
        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Long value = dataSnapshot.getValue(Long.class);

                if(value != null){

                    float progreso = 2 *  value;

                    if(progreso>100 || progreso==100){
                        tarjetaDescuento();
                    }else{
                        setProgress(progreso);
                    }
                }



            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value

            }
        });
    }

    public void dialogInfo(){

        final PrettyDialog dialog = new PrettyDialog(getContext());
        dialog
                .setTitle("Tus puntos")
                .setMessage("Con tus compras gana puntos para conseguir un descuento")
                .addButton(
                        "¡Genial!",					// button text
                        R.color.pdlg_color_white,		// button text color
                        R.color.pdlg_color_green,		// button background color
                        new PrettyDialogCallback() {		// button OnClick listener
                            @Override
                            public void onClick() {
                                setProgress(200);
                                dialog.dismiss();
                            }
                        }
                )


                .setAnimationEnabled(true)
                .show();
    }

    private void tarjetaDescuento(){
        try{
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.tarjeta_descuento, null);
            ViewGroup rootView = (ViewGroup) getView();
            rootView.removeAllViews();
            TextView nombre = (TextView)view.findViewById(R.id.nombreText);
            nombre.setText("Nombre");
            TextView mail = (TextView)view.findViewById(R.id.segundoCampo);
            mail.setText("Email");
            TextView txt = (TextView) view.findViewById(R.id.txt_descuento);
            txt.setText("DESCUENTO DE 5€");
            rellenarTarjeta(view);
            colocarFoto(view);
            colocarQR(view);
            rootView.addView(view);
        }catch (Exception e){
            Log.d("crash","Problema con tarheta de descuento");
        }
    }



}
