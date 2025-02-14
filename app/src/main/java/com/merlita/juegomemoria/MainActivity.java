package com.merlita.juegomemoria;

import static android.view.View.INVISIBLE;
import static android.view.View.TEXT_ALIGNMENT_TEXT_START;
import static android.view.View.VISIBLE;
import static kotlinx.coroutines.DelayKt.delay;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.gridlayout.widget.GridLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class MainActivity extends AppCompatActivity {


    Point p;
    int anchoPantalla, altoPantalla;
    GridLayout gridLayout;
    TextView tv, tv2;
    Button btJugar;

    final int FILAS = 2, COLUMNAS = 2;
    ArrayList<Integer> colores = new ArrayList<>();
    private int[] ids = new int[4];
    Intent upIntent;


    int[] orden = new int[4];
    Button[] bts = new Button[4];
    Button b1, b2, b3, b4;
    int espera = 700;
    int[] ordenJugador = new int[4];
    int numPulsados = 0;
    int pJugador=0, pMaquina=0;
    boolean isJugando=false, isTiempoJugador=false,
            isPistasEnabled=false;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        p = new Point();
        Display pantallaDisplay = getWindowManager().
                getDefaultDisplay();
        pantallaDisplay.getSize(p);
        anchoPantalla = p.x;
        altoPantalla = p.y;
        gridLayout = findViewById(R.id.grilla_layout);
        tv = findViewById(R.id.textView);
        tv2 = findViewById(R.id.textView2);
        btJugar = findViewById(R.id.button);

        añadirBotones();

        btJugar.setOnClickListener(v -> {
            if(!isJugando){
                isJugando=true;
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    hacerOrden();
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        hacerBlancos();
                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                            encender();
                        }, espera);
                    }, espera);
                }, espera);
            }
        });

    }

    private void encender() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            b1.setBackgroundColor(colores.get(orden[0]));
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                b2.setBackgroundColor(colores.get(orden[1]));
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    b3.setBackgroundColor(colores.get(orden[2]));
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        b4.setBackgroundColor(colores.get(orden[3]));
                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                            hacerBlancos();
                            isTiempoJugador=true;
                        }, espera);
                    }, espera);
                }, espera);
            }, espera);
        }, espera);
    }


    private void hacerBlancos() {

        b1 = (Button) gridLayout.getChildAt(orden[0]);
        b2 = (Button) gridLayout.getChildAt(orden[1]);
        b3 = (Button) gridLayout.getChildAt(orden[2]);
        b4 = (Button) gridLayout.getChildAt(orden[3]);
        b1.setBackgroundColor(Color.rgb(255, 255, 255));
        b2.setBackgroundColor(Color.rgb(255, 255, 255));
        b3.setBackgroundColor(Color.rgb(255, 255, 255));
        b4.setBackgroundColor(Color.rgb(255, 255, 255));

    }

    private void hacerOrden() {
        String ordenSt="";
        ArrayList<Integer> dichos = new ArrayList<>();
        int numero=0;
        for (int i = 0; i < 4; i++) {
            do {
                numero = (int) (Math.random()*4);
            }while(dichos.contains(numero));
            orden[i]=numero;
            dichos.add(numero);
            ordenSt += (numero+1);
        }
        tv2.setText(ordenSt);

    }

    private void añadirBotones() {
        ViewGroup.LayoutParams lp =
                new ViewGroup.LayoutParams(
                        anchoPantalla / COLUMNAS-30, altoPantalla / FILAS-400);
        gridLayout.removeAllViews();
        gridLayout.setRowCount(2);
        gridLayout.setColumnCount(2);

        for (int i = 0; i < FILAS * COLUMNAS; i++) {
            Button b = new Button(this);

            b.setLayoutParams(lp);
            b.setTextSize(60);
            colores.add(Color.rgb(
                    5 * i + (int) (Math.random() * 150 + 100),
                    5 * i + (int) (Math.random() * 150 + 120),
                    5 * i + (int) (Math.random() * 150 + 80)));
            b.setBackgroundColor(colores.get(i));
            ids[i] = ViewGroup.generateViewId();
            b.setId(ids[i]);
            if(isPistasEnabled)
                b.setText((i+1)+"");
            b.setTextSize(20);
            b.setTextAlignment(TEXT_ALIGNMENT_TEXT_START);
            b.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    if(isJugando && isTiempoJugador){
                        b.setBackgroundColor(colores.get(
                                gridLayout.indexOfChild(b)));

                        ordenJugador[numPulsados] = gridLayout.indexOfChild(b);
                        numPulsados++;

                        if(numPulsados==4){
                            tv.setText(haGanado());
                            numPulsados=0;
                            isTiempoJugador=false;
                            isJugando=false;
                        }
                    }
                }
            });
            gridLayout.setUseDefaultMargins(false);
            gridLayout.addView(b);
        }

    }

    private String haGanado() {
        String res = "";
        if(Arrays.equals(orden, ordenJugador)){
            pJugador++;
        }else{
            pMaquina++;
        }
        res += ". Puntos jugador: "+pJugador+". Puntos máquina: "+pMaquina;

        if(pJugador==3){
            res = "HA GANADO EL JUGADOR. ";
            pJugador=pMaquina=0;
        }
        if(pMaquina==3){
            res = "HA GANADO LA MAQUINA";
            pJugador=pMaquina=0;
        }
        return res;
    }

    public void aprende(View view) {
        if(isPistasEnabled){
            tv2.setVisibility(INVISIBLE);
            isPistasEnabled=false;
            añadirBotones();
        }else{
            tv2.setVisibility(VISIBLE);
            isPistasEnabled=true;
            añadirBotones();
        }
    }
}