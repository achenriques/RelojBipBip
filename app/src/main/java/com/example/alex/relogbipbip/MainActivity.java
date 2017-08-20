package com.example.alex.relogbipbip;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity {

    private final int REQUEST_CODE = 1;
    private final String zero = "0";

    private int h1, h2, m1, m2; //h1 y m1 son el limite inferior y los otros el superior.
    private String h11, h22, m11, m22 = "";

    private Button pruebame;
    private Button configurame;
    private TextView hora;
    private TextView fecha;
    private TextView planificado;
    private CheckBox cb;
    private MediaPlayer mp1;
    private MediaPlayer mp2;

    private boolean tocar;
    private boolean probar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tocar = false;
        probar = false;
        h1 = -1;
        h2 = -1;
        m1 = -1;
        m2 = -1;

        mp1 = MediaPlayer.create(this,R.raw.cuco);
        mp1.setLooping(false);
        mp1.setVolume(100,100);

        mp2 = MediaPlayer.create(this,R.raw.bong);
        mp2.setLooping(false);
        mp2.setVolume(100,100);


        planificado = (TextView) findViewById(R.id.planificacion);
        pruebame = (Button) findViewById(R.id.bProbarBip);
        configurame = (Button) findViewById(R.id.bconfigura);
        cb = (CheckBox) findViewById(R.id.tocando);

        cb.setClickable(false);

        pruebame.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(probar) {
                    mp1.start();
                    probar = false;
                }
                else {
                    mp2.start();
                    probar = true;
                }
            }
        });

        configurame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aConfigurar();
            }
        });

        planificado.setText(getResources().getString(R.string.noPlan));

        //Generamos un thread para actualizar la hora cada segundo
        Thread t = new Thread() {
            @Override
            public void run() {

                try {
                    hora = (TextView) findViewById(R.id.horaActual);
                    fecha = (TextView) findViewById(R.id.fechaActual);
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                long date = System.currentTimeMillis();
                                SimpleDateFormat h = new SimpleDateFormat("HH:mm:ss");
                                SimpleDateFormat f = new SimpleDateFormat("dd-MMM-yyyy");
                                String horaString = h.format(date);
                                String fechaString = f.format(date);

                                //Para el Bip
                                String hh = horaString.substring(0,2);
                                String mm = horaString.substring(3,5);
                                String ss = horaString.substring(6,8);

                                int hhh = Integer.parseInt(hh); //hora actual en int
                                int mmm = Integer.parseInt(mm); //minutos actuales en int
                                int sss = Integer.parseInt(ss); //segundos actuales en int

                                if (tocar)
                                {
                                    //if(h1 <  h2) {
                                        if ((h1 < hhh) || (h1 == hhh && m1 <= mmm)) {
                                            if ((h2 > hhh) || (h2 == hhh && m2 > mmm)) { //Si h2> hhh tengo que parar si o si
                                                cb.setChecked(true);
                                                if (sss == 0) {
                                                    if (mmm == 15 || mmm == 30 || mmm == 45) {
                                                        mp2.start();
                                                    }
                                                    if (mmm == 0) {
                                                        mp1.start();
                                                    }
                                                }
                                            } else
                                                cb.setChecked(false);
                                        }

                                }

                                //Para mostrar la hora actual
                                hora.setText(horaString);
                                fecha.setText(fechaString);
                            }
                        });
                    }
                }catch (InterruptedException e) {}
            }
        };
        t.start();
    }

    public void aConfigurar() {
        this.startActivityForResult( new Intent( this.getApplicationContext(), Configura.class ), REQUEST_CODE );
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if ( requestCode == REQUEST_CODE
                && resultCode == RESULT_OK )
        {
            h1 = data.getIntExtra("t1h", -1);
            m1 = data.getIntExtra("t1m", -1);
            h2 = data.getIntExtra("t2h", -1);
            m2 = data.getIntExtra("t2m", -1);

            if (h1 > -1 && h2 > -1 && m1 > -1 && m2 > -1)
            {
                if(h1<10)
                {
                    h11 = (zero + String.valueOf(h1));
                }
                else
                    h11 = String.valueOf(h1);
                if(h2<10)
                {
                    h22 = (zero + String.valueOf(h2));
                } else
                    h22 = String.valueOf(h2);
                if(m1<10)
                {
                    m11 = (zero + String.valueOf(m1));
                } else
                    m11 = String.valueOf(m1);
                if(m2<10)
                {
                    m22 = (zero + String.valueOf(m2));
                } else
                    m22 = String.valueOf(m2);

                String p = ("Desde las: " + h11 + ":" + m11 + "  hasta las: " + h22 + ":" + m22);
                planificado.setText(p);
                tocar = true;
            }
        }

        if ( requestCode == REQUEST_CODE
                && resultCode == RESULT_CANCELED )
        {
            if (!tocar)
                cb.setChecked(false);
                planificado.setText(getResources().getString(R.string.noPlan));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.dejar) {
            tocar = false;
            cb.setChecked(false);
            planificado.setText(getResources().getString(R.string.noPlan));
            return true;
        }

        if (id == R.id.salir) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
