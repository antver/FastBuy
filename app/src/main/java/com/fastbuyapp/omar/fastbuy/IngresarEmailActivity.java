package com.fastbuyapp.omar.fastbuy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class IngresarEmailActivity extends AppCompatActivity {
    SharedPreferences myPreferences;
    SharedPreferences.Editor myEditor;
    String nombre, celular, email, foto, tokencito;
    EditText txtEmailUsuario;
    Button btnContinuar;
    ImageView btnAtras;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingresar_email);

        myPreferences =  PreferenceManager.getDefaultSharedPreferences(this);
        myEditor = myPreferences.edit();
        celular = myPreferences.getString("Number_Cliente", "");
        email = myPreferences.getString("Email_Cliente", "");
        tokencito = myPreferences.getString("tokencito", "");
        txtEmailUsuario = (EditText) findViewById(R.id.txtEmailUsuario);
        btnContinuar = (Button) findViewById(R.id.btnContinuar);
        btnAtras = (ImageView) findViewById(R.id.btnAtras);
        //aqui entran unicamente los usuarios que no esten regitrados y entran por primera vez
        //ya sea por google, facebook o numero de telefono

        if (!nombre.equals("")){
            txtEmailUsuario.setText(email);
        }

        btnContinuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = txtEmailUsuario.getText().toString();
                myEditor.putString("Email_Cliente", email);
                myEditor.commit();
                Intent intent = new Intent(IngresarEmailActivity.this, TutorialActivity.class);
                startActivity(intent);
            }
        });

        btnAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
