package com.example.alex.relogbipbip;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

public class Configura extends AppCompatActivity {

    private Intent datosDevolver;
    private TimePicker tpicker;
    private TextView dh;
    private Button cancela;
    private Button hecho;
    private boolean selecciondo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configura);

        datosDevolver = new Intent();

        selecciondo = false;

        tpicker = (TimePicker) findViewById(R.id.timePicker);
        dh = (TextView) findViewById(R.id.desdeHasta);
        cancela = (Button) findViewById(R.id.bCancelar);
        hecho = (Button) findViewById(R.id.bHecho);

        cancela.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelar();
            }
        });

        hecho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hacer();
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        selecciondo = false;
    }

    private void cancelar() {
        this.setResult( RESULT_CANCELED);
        this.finish();
    }

    private void hacer() {
        if(!selecciondo)
        {
            datosDevolver.putExtra("t1h", tpicker.getCurrentHour().intValue());
            datosDevolver.putExtra("t1m", tpicker.getCurrentMinute().intValue());
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            hecho.setText(getResources().getString(R.string.hecho));
            dh.setText(getResources().getString(R.string.hasta));
            selecciondo = true;
        } else {
            datosDevolver.putExtra("t2h", tpicker.getCurrentHour().intValue());
            datosDevolver.putExtra("t2m", tpicker.getCurrentMinute().intValue());
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            selecciondo = false; //Vuelve estado inicial
            this.setResult( RESULT_OK, datosDevolver);
            this.finish();
        }

    }
}
