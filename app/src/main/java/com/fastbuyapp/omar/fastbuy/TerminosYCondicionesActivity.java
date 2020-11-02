package com.fastbuyapp.omar.fastbuy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.fastbuyapp.omar.fastbuy.config.Globales;

public class TerminosYCondicionesActivity extends AppCompatActivity {

    TextView btnPoliticas, btnTerminos;
    Button btnContinuaLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terminos_ycondiciones);

        //Start Diseño de popup
        DisplayMetrics medidasVentana = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(medidasVentana);

        int ancho = medidasVentana.widthPixels;
        final int alto = medidasVentana.heightPixels;
        getWindow().setLayout((int)(ancho*0.85), (int)(alto*0.9));
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //End Diseño de popup

        //inicializando componentes
        btnPoliticas = findViewById(R.id.btnPoliticas);
        btnTerminos = findViewById(R.id.btnTerminos);
        btnContinuaLogin = findViewById(R.id.btnContinuarAlLogin);

        btnPoliticas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("http://fastbuy.pe/app/legal/politicas");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        btnTerminos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("http://fastbuych.pe/app/legal/terminos");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        btnContinuaLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TerminosYCondicionesActivity.this, OpcionLoginActivity.class);
                startActivity(intent);
            }
        });
    }
}
