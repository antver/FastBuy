package com.fastbuyapp.omar.fastbuy;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.fastbuyapp.omar.fastbuy.config.Globales;

public class MensajeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mensaje);
        TextView txtMensajeVisa = (TextView) findViewById(R.id.txtMensajeVisa);
        txtMensajeVisa.setText(Globales.mensaje);
    }
}
