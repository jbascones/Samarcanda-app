package com.jorgebascones.samarcanda;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jorgebascones.samarcanda.Modelos.Fecha;
import com.jorgebascones.samarcanda.Modelos.Punto;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;


/**
 * A simple {@link Fragment} subclass.
 */

public class GraficasFragment extends Fragment {


    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private LineChartView chart;
    private LineChartData data;
    private boolean hasAxes = true;
    private boolean hasAxesNames = true;
    private boolean hasLines = true;
    private boolean hasPoints = true;
    private ValueShape shape = ValueShape.CIRCLE;
    private boolean isFilled = false;
    private boolean hasLabels = true;
    private boolean isCubic = false;
    private boolean hasLabelForSelected = false;
    private boolean pointsHaveDifferentColor = true;
    private boolean hasGradientToTransparent = false;
    private int numberOfLines = 1;
    private int maxNumberOfLines = 1;
    private int numberOfPoints = 30;
    float[][] randomNumbersTab;
    List<AxisValue> axisValues = new ArrayList<AxisValue>();
    private int mesOffset;
    Fecha f;
    private String rutaMes;
    //private LineChartData lineData;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_graficas, container, false);

        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Ventas por día");

        f = new Fecha();
        mesOffset = 0;
        condicionInicialRutaMes();
        setListeners(view);
        chart = (LineChartView) view.findViewById(R.id.chart);
        chart.setZoomType(ZoomType.HORIZONTAL);
        //chart.setOnValueTouchListener(new ValueTouchListener());
        //gestionChart();
        //generateValues();
        //generateData();

        descargarListaVentas(f.sumarMesRuta(mesOffset));

        return view;
    }

    public void gestionChart(){
        List<PointValue> values = new ArrayList<PointValue>();
        values.add(new PointValue(0, 2));
        values.add(new PointValue(1, 4));
        values.add(new PointValue(2, 3));
        values.add(new PointValue(3, 4));
        Line line = new Line(values).setColor(Color.BLUE).setCubic(true);
        List<Line> lines = new ArrayList<Line>();
        lines.add(line);

        LineChartData data = new LineChartData();
        data.setLines(lines);

        LineChartView chart = new LineChartView(getContext());
        chart.setLineChartData(data);
    }
    private void generateData() {

        List<Line> lines = new ArrayList<Line>();
        for (int i = 0; i < numberOfLines; ++i) {

            List<PointValue> values = new ArrayList<PointValue>();
            for (int j = 0; j < numberOfPoints; ++j) {
                values.add(new PointValue(j, randomNumbersTab[i][j]));
                int diaInt = j+1;
                Fecha f = new Fecha();
                try {
                    String dia;
                    if(diaInt>9){
                        dia = f.getDiaSemana(rutaMes+"/"+diaInt);
                    }else{
                        dia = f.getDiaSemana(rutaMes+"/0"+diaInt);
                    }

                    axisValues.add(new AxisValue(j).setLabel(diaInt+dia));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            Line line = new Line(values);
            line.setColor(ChartUtils.COLORS[i]);
            line.setShape(shape);
            line.setCubic(isCubic);
            line.setFilled(isFilled);
            line.setHasLabels(hasLabels);
            line.setHasLabelsOnlyForSelected(hasLabelForSelected);
            line.setHasLines(hasLines);
            line.setHasPoints(hasPoints);
            //line.setHasGradientToTransparent(hasGradientToTransparent);
            if (pointsHaveDifferentColor){
                //line.setPointColor(ChartUtils.COLORS[(i + 1) % ChartUtils.COLORS.length]);
                line.setPointColor(Color.rgb(255,160,0));
            }
            lines.add(line);
            //lineData.setAxisXBottom(new Axis(axisValues));
        }

        data = new LineChartData(lines);

        if (hasAxes) {
            Axis axisX = new Axis();
            axisX.setValues(axisValues);
            Axis axisY = new Axis().setHasLines(true);
            if (hasAxesNames) {
                axisX.setName("Día de mes");
                axisX.setTextColor(Color.BLACK);
                axisY.setName("Número de ventas");
                axisY.setTextColor(Color.BLACK);

            }
            data.setAxisXBottom(axisX);
            data.setAxisYLeft(axisY);
        } else {
            data.setAxisXBottom(null);
            data.setAxisYLeft(null);
        }

        data.setBaseValue(Float.NEGATIVE_INFINITY);
        chart.setLineChartData(data);



    }
/*
    private void generateValues() {
        for (int i = 0; i < maxNumberOfLines; ++i) {
            for (int j = 0; j < numberOfPoints; ++j) {
                randomNumbersTab[i][j] = (float) Math.random() * 100f;
            }
        }
    } */

    private void generateValues() {
        for (int i = 0; i < maxNumberOfLines; ++i) {
            for (int j = 0; j < numberOfPoints; ++j) {
                randomNumbersTab[i][j] = j*j;
            }
        }

    }

    public void descargarListaVentas(String ruta){
        inicializarArrayMes();
        // Read from the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference myRef = database.getReference("/estadisticas/ventas/ventas por dia/"+ruta);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                    //Log.v("Funciona",""+ childDataSnapshot.getKey()); //displays the key for the node
                    //Log.v("Funciona",""+ childDataSnapshot.getValue());
                    Fecha fecha = new Fecha();
                    Punto p = new Punto(fecha.StringToInt((fecha.StringToInt(childDataSnapshot.getKey())-1)+""),fecha.StringToInt(childDataSnapshot.getValue().toString()));
                    randomNumbersTab[0][p.getX()]= p.getY();
                }
                generateData();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                System.out.println("Failed to read value." + error.toException());
            }
        });
    }

    public void inicializarArrayMes(){
        Fecha fecha = new Fecha();
        int numeroDias = fecha.StringToInt(fecha.getDiaMesAnterior(rutaMes.substring(5)));
        numberOfPoints = numeroDias;
        randomNumbersTab  = new float[maxNumberOfLines][numeroDias];
    }

    public void setListeners(View v){
        final Button buttonActual = (Button) v.findViewById(R.id.id_boton_simular);


        buttonActual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });

        Button mesAnterior = (Button) v.findViewById(R.id.id_mes_anterior);


        mesAnterior.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mesOffset --;
                rutaMes = f.sumarMesRuta(mesOffset);
                descargarListaVentas(rutaMes);
                buttonActual.setText(f.mes+"/"+f.anno);

            }
        });

        Button mesSiguiente = (Button) v.findViewById(R.id.id_mes_actual);


        mesSiguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mesOffset ++;
                rutaMes = f.sumarMesRuta(mesOffset);
                descargarListaVentas(rutaMes);
                buttonActual.setText(f.mes+"/"+f.anno);

            }
        });
    }

    public void condicionInicialRutaMes(){
        rutaMes = f.sumarMesRuta(0);
    }





}
