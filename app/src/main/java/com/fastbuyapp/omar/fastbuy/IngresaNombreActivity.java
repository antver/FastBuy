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

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;

public class IngresaNombreActivity extends AppCompatActivity {
    SharedPreferences myPreferences;
    SharedPreferences.Editor myEditor;
    String nombre, celular, email, foto, tokencito;
    EditText txtNombreUsuario;
    Button btnContinuar;
    ImageView btnAtras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingresa_nombre);

        myPreferences =  PreferenceManager.getDefaultSharedPreferences(this);
        myEditor = myPreferences.edit();
        celular = myPreferences.getString("Number_Cliente", "");
        nombre = myPreferences.getString("Name_Cliente", "");
        tokencito = myPreferences.getString("tokencito", "");
        txtNombreUsuario = (EditText) findViewById(R.id.txtNombreUsuario);
        btnContinuar = (Button) findViewById(R.id.btnContinuar);
        btnAtras = (ImageView) findViewById(R.id.btnAtras);
        //aqui entran unicamente los usuarios que no esten regitrados y entran por primera vez
        //ya sea por google, facebook o numero de telefono

        if (!nombre.equals("")){
            txtNombreUsuario.setText(nombre);
        }

        btnContinuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nombre = txtNombreUsuario.getText().toString();
                myEditor.putString("Name_Cliente", nombre);
                myEditor.commit();
                Intent intent = new Intent(IngresaNombreActivity.this, IngresarEmailActivity.class);
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
