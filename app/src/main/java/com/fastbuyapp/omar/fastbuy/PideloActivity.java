package com.fastbuyapp.omar.fastbuy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.fastbuyapp.omar.fastbuy.Validaciones.ValidacionDatos;
import com.fastbuyapp.omar.fastbuy.config.Globales;
import com.fastbuyapp.omar.fastbuy.entidades.Ubicacion;

import java.util.ArrayList;

public class PideloActivity extends AppCompatActivity {

    ArrayList<Ubicacion> listCiu;
    //String Ciud;
    LinearLayout generaPidelo;
    boolean state = true;
    boolean openCmb = true;
    //GridView listaCiudadesPidelo;
    //CiudadListAdapter adapter = null;
    SharedPreferences myPreferences;
    SharedPreferences.Editor myEditor;

    ImageView btnAtras, btnDondeCompramos;
    String ciudad, ubicacion;
    TextView txtDireccionPidelo;
    @Override
    protected void onResume(){
        super.onResume();
        String direccionpidelo = myPreferences.getString("LugarRecogerEncargo",  "Dirección del establecimiento");
        txtDireccionPidelo.setText(direccionpidelo);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pidelo);

        Globales globales = new Globales();
        listCiu = globales.getDataFromSharedPreferences("lista_ciudades");
        /*LottieAnimationView btnBoxPidelo = (LottieAnimationView) findViewById(R.id.btnAddAnimation);
        animacionPidelo = (LinearLayout) findViewById(R.id.linerAnimacionPidelo);*/
        //generaPidelo = (LinearLayout) findViewById(R.id.linearGeneraPidelo);
        myPreferences =  PreferenceManager.getDefaultSharedPreferences(this);
        myEditor = myPreferences.edit();
        ciudad = myPreferences.getString("City_Cliente", "");
        ubicacion = myPreferences.getString("ubicacion", "");
        //visualizacion
        //animacionPidelo.setVisibility(View.VISIBLE);
        //generaPidelo.setVisibility(View.GONE);
        btnDondeCompramos = (ImageView) findViewById(R.id.btnDondeCompramos);
        txtDireccionPidelo = (TextView) findViewById(R.id.txtDireccionPidelo);
        btnAtras = (ImageView) findViewById(R.id.btnAtras);
        btnDondeCompramos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMapa = new Intent(PideloActivity.this, DondeLoCompramosFragment.class);
                startActivity(intentMapa);
            }
        });

        //inicializando variables
        //final TextView cmbCiudadPidelo = (TextView) findViewById(R.id.cmbCiudadPidelo);
        //listaCiudadesPidelo = (GridView) findViewById(R.id.listaDeCiudadesPidelo);

        //ScrollView myScroll = findViewById(R.id.scrollPidelo);
        //final ScrollView myScrollGeneral = findViewById(R.id.scrollGeneralPidelo);

        //Start boton flotante
        //final LinearLayout flotante = (LinearLayout) findViewById(R.id.linearFlotantePidelo);
        //final LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
               // LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        //param.setMargins(left,top,right,bottom);
        //flotante.setPadding(left,top,right,bottom);

        /*btnBoxPidelo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (state){
                    param.setMargins(0,0,0,-50);
                    animacionPidelo.setVisibility(View.GONE);
                    generaPidelo.setVisibility(View.VISIBLE);
                    myScrollGeneral.setBackgroundResource(R.drawable.background);
                    state = false;
                    flotante.setPadding(0,0,0,0);
                }
                else {
                    param.setMargins(0,0,0,0);
                    animacionPidelo.setVisibility(View.VISIBLE);
                    generaPidelo.setVisibility(View.GONE);
                    myScrollGeneral.setBackgroundResource(R.color.blanco);
                    state = true;
                    flotante.setPadding(0,0,0,30);
                }
                flotante.setLayoutParams(param);
            }
        });*/
        //End boton flotante

        //Inicializamos boton Confirmar Encargo
        final Button btnConfirmaPidelo = (Button) findViewById(R.id.btnConfirmarPidelo);

        //Start Lista de Ciudades
        //listarCiudades();

        /*cmbCiudadPidelo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (openCmb){
                    listaCiudadesPidelo.setVisibility(View.VISIBLE);
                    openCmb =false;
                }else {
                    listaCiudadesPidelo.setVisibility(View.GONE);
                    openCmb=true;
                }
            }
        });*/

        //Ciud = Globales.CiudadPideloSeleccionada;
        //cmbCiudadPidelo.setText(ciudad);

        /*listaCiudadesPidelo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int codigo =(listCiu.get(position).getCodigo());

                //Globales.CodigoCiudadPideloSeleccionada = codigo;
                myEditor.putString("ubicacion", String.valueOf(codigo));
                ciudad = listCiu.get(position).getNombre();
                //Globales.CiudadPideloSeleccionada = Ciud; //EXT_Origen
                myEditor.putString("City_Cliente", String.valueOf(ciudad));
                cmbCiudadPidelo.setText(ciudad);
                listaCiudadesPidelo.setVisibility(View.GONE);
            }
        });*/
        //End Lista de Ciudades

        //Start controlando Scroll
        /*myScrollGeneral.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                findViewById(R.id.scrollPidelo).getParent()
                        .requestDisallowInterceptTouchEvent(false);
                findViewById(R.id.listaDeCiudadesPidelo).getParent()
                        .requestDisallowInterceptTouchEvent(false);
                return false;
            }
        });

        myScroll.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                findViewById(R.id.listaDeCiudadesPidelo).getParent()
                        .requestDisallowInterceptTouchEvent(false);
                return false;
            }
        });*/
        /*listaCiudadesPidelo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });*/
        //End controlando Scroll

        //Start número de contacto
        final EditText txtNumberContactoPidelo = (EditText) findViewById(R.id.txtNumeroContactoPidelo);
        SharedPreferences myPreferences;
        myPreferences =  PreferenceManager.getDefaultSharedPreferences(this);
        String number = myPreferences.getString("Number_Cliente", "");
        txtNumberContactoPidelo.setText(number);

        txtNumberContactoPidelo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (txtNumberContactoPidelo.getText().length() == 9) {
                    btnConfirmaPidelo.setEnabled(validaNumero(txtNumberContactoPidelo));
                    //btnConfirmaPidelo.setBackgroundResource(R.drawable.boton_rojo);
                } else {
                    btnConfirmaPidelo.setEnabled(false);
                    //btnConfirmaPidelo.setBackgroundResource(R.drawable.boton_disabled);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (txtNumberContactoPidelo.getText().length() == 9) {
                    btnConfirmaPidelo.setEnabled(validaNumero(txtNumberContactoPidelo));
                    //btnConfirmaPidelo.setBackgroundResource(R.drawable.boton_rojo);
                } else {
                    btnConfirmaPidelo.setEnabled(false);
                    //btnConfirmaPidelo.setBackgroundResource(R.drawable.boton_disabled);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (txtNumberContactoPidelo.getText().length() == 9) {
                    btnConfirmaPidelo.setEnabled(validaNumero(txtNumberContactoPidelo));
                    //btnConfirmaPidelo.setBackgroundResource(R.drawable.boton_rojo);
                } else {
                    btnConfirmaPidelo.setEnabled(false);
                    //btnConfirmaPidelo.setBackgroundResource(R.drawable.boton_disabled);
                }
            }
        });
        //End Número de contacto

        //Inicializando cajas de texto luagr y encargo
        //final EditText txtLugarComprar = (EditText) findViewById(R.id.txtLugarComprarPidelo);
        final EditText txtProductoComprar = (EditText) findViewById(R.id.txtProductoPidelo);

        //Ejecutando Boton
        btnConfirmaPidelo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtProductoComprar.getText().length() >0){
                    //String LugarRecogerEncargo =  txtLugarComprar.getText().toString();
                    String DetalleEncargo =  txtProductoComprar.getText().toString();
                    String NumeroContactoEncargo = txtNumberContactoPidelo.getText().toString();
                    //myEditor.putString("LugarRecogerEncargo", LugarRecogerEncargo);
                    myEditor.putString("DetalleEncargo", DetalleEncargo);
                    myEditor.putString("NumeroContactoEncargo", NumeroContactoEncargo);
                    myEditor.commit();

                    Intent intent = new Intent(PideloActivity.this, PagoEncargoActivity.class);
                    startActivity(intent);
                }else{
                    Toast toast = Toast.makeText(PideloActivity.this, "Por favor, Complete todos los datos.", Toast.LENGTH_SHORT);
                    View vistaToast = toast.getView();
                    vistaToast.setBackgroundResource(R.drawable.toast_yellow);
                    toast.show();
                }
            }
        });

        btnAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_pidelo, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return false;
    }

    public boolean validaNumero(EditText txtCelularIngresar){
        //Inicializamos el metodo Validar numero
        ValidacionDatos validacion = new ValidacionDatos();
        if(validacion.validarCelular(txtCelularIngresar)==false){
            Toast toast = Toast.makeText(PideloActivity.this, "Por favor, Ingrese un número de celular válido.", Toast.LENGTH_SHORT);
            View vistaToast = toast.getView();
            vistaToast.setBackgroundResource(R.drawable.toast_yellow);
            toast.show();
            return false;
        }else
            return true;
    }

}
