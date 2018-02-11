package com.jorgebascones.samarcanda;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.squareup.picasso.Picasso;

import static com.jorgebascones.samarcanda.R.id.imageView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TarjetaFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TarjetaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TarjetaFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    // TODO: Rename and change types of parameters
    private String miNombre;
    private String miUserId;
    private String miUrl;
    private String estado;



    private OnFragmentInteractionListener mListener;

    public TarjetaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @param param3 Parameter 3.
     * @return A new instance of fragment TarjetaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TarjetaFragment newInstance(String param1, String param2, String param3) {
        TarjetaFragment fragment = new TarjetaFragment();
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
        final View view = inflater.inflate(R.layout.tarjeta, container, false);

        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Tarjeta");

        Button buttonQr = (Button) view.findViewById(R.id.id_card_botonCambio);

        TextView nombre = (TextView)view.findViewById(R.id.nombreText);
        nombre.setText("Nombre");
        TextView mail = (TextView)view.findViewById(R.id.segundoCampo);
        mail.setText("Email");

        colocarFoto(view);

        rellenarTarjeta(view);


        buttonQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(estado.equals("foto")){
                    colocarQR(view);

                }else{
                    colocarFoto(view);
                }


            }
        });

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

        ImageView imageView = (ImageView) view.findViewById(R.id.id_card_foto);
        CreaQr creaQr = new CreaQr();
        Bitmap bitmap = creaQr.generarQR("USER:"+miUserId);
        imageView.setImageBitmap(bitmap);

        estado = "qr";

        cambiarBoton(view,"Ver foto");


    }

    public void colocarFoto(View view){
        ImageView imagen = (ImageView) view.findViewById(R.id.id_card_foto);

        Context c = getActivity().getApplicationContext();

        Picasso.with(c).load(miUrl).into(imagen);

        estado = "foto";

        cambiarBoton(view,"Ver código QR");

    }

    public void cambiarBoton(View view, String texto){
        Button boton = (Button) view.findViewById(R.id.id_card_botonCambio);
        boton.setText(texto);
    }

    public void rellenarTarjeta(View view){
        User yo = new User();
        TextView nombreTV = (TextView) view.findViewById(R.id.id_nombre_card);
        nombreTV.setText(miNombre);
        TextView mailTV = (TextView) view.findViewById(R.id.id_segundoCampo);
        mailTV.setText(yo.getMail());
    }




}
