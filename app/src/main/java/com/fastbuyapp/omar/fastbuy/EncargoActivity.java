package com.fastbuyapp.omar.fastbuy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.Layout;
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
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.fastbuyapp.omar.fastbuy.Validaciones.ValidacionDatos;
import com.fastbuyapp.omar.fastbuy.config.Globales;
import com.fastbuyapp.omar.fastbuy.entidades.Ubicacion;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;

public class EncargoActivity extends AppCompatActivity {
    ArrayList<Ubicacion> listCiu;
    String ciudad, ubicacion;
    //LinearLayout animacionEncargo, generaEncargo;
    boolean state = true;
    boolean openCmb = true;
    GridView listaCiudadesEncargo;
    CiudadListAdapter adapter = null;
    SharedPreferences myPreferences;
    SharedPreferences.Editor myEditor;
    ImageButton btnCarrito;

    @Override
    protected void onResume(){
        super.onResume();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encargo);

        Globales globales = new Globales();
        listCiu = globales.getDataFromSharedPreferences("lista_ciudades");
        //LottieAnimationView btnBoxEncargo = (LottieAnimationView) findViewById(R.id.btnCajaAnimation);
        //animacionEncargo = (LinearLayout) findViewById(R.id.linerAnimacionEncargo);
        //generaEncargo = (LinearLayout) findViewById(R.id.linearGeneraEncargo);
        myPreferences =  PreferenceManager.getDefaultSharedPreferences(this);
        myEditor = myPreferences.edit();
        ciudad = myPreferences.getString("City_Cliente", "");
        ubicacion = myPreferences.getString("ubicacion", "");
        //visualizacion
        //animacionEncargo.setVisibility(View.VISIBLE);
       // generaEncargo.setVisibility(View.GONE);


        //inicializando variables
        //final TextView cmbCiudadEncargo = (TextView) findViewById(R.id.cmbCiudadEncargo);
        //listaCiudadesEncargo = (GridView) findViewById(R.id.listaDeCiudadesEncargo);
        //ScrollView myScroll = findViewById(R.id.scrollEncargo);
        //final ScrollView myScrollGeneral = findViewById(R.id.scrollGeneralEncargo);

        //Start boton flotante

        //final LinearLayout flotante = (LinearLayout) findViewById(R.id.linearFlotanteEncargo);
        //final LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
        //        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        //param.setMargins(left,top,right,bottom);
        //flotante.setPadding(left,top,right,bottom);

        /*btnBoxEncargo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (state){
                    param.setMargins(0,0,0,-50);
                    animacionEncargo.setVisibility(View.GONE);
                    generaEncargo.setVisibility(View.VISIBLE);
                    myScrollGeneral.setBackgroundResource(R.drawable.background);
                    state = false;
                    flotante.setPadding(0,0,0,0);
                }
                else {
                    param.setMargins(0,0,0,0);
                    animacionEncargo.setVisibility(View.VISIBLE);
                    generaEncargo.setVisibility(View.GONE);
                    myScrollGeneral.setBackgroundResource(R.color.blanco);
                    state = true;
                    flotante.setPadding(0,0,0,30);
                }
                flotante.setLayoutParams(param);
            }
        });*/
        //End boton flotante

        //Inicializamos boton Confirmar Encargo
        final Button btnConfirmaEncargo = (Button) findViewById(R.id.btnConfirmarEncargo);

        //Start Lista de Ciudades
        //listarCiudades();

        /*cmbCiudadEncargo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (openCmb){
                    listaCiudadesEncargo.setVisibility(View.VISIBLE);
                    openCmb =false;
                }else {
                    listaCiudadesEncargo.setVisibility(View.GONE);
                    openCmb=true;
                }
            }
        });*/

        //Ciud = Globales.CiudadEncargoSeleccionada;
        //cmbCiudadEncargo.setText(ciudad);

        /*listaCiudadesEncargo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int codigo =(listCiu.get(position).getCodigo());
                myEditor.putString("ubicacion", String.valueOf(codigo));
                ciudad = listCiu.get(position).getNombre();
                cmbCiudadEncargo.setText(ciudad);
                listaCiudadesEncargo.setVisibility(View.GONE);
            }
        });*/
        //End Lista de Ciudades

        //Start controlando Scroll
        /*myScrollGeneral.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                findViewById(R.id.scrollEncargo).getParent()
                        .requestDisallowInterceptTouchEvent(false);
                findViewById(R.id.listaDeCiudadesEncargo).getParent()
                        .requestDisallowInterceptTouchEvent(false);
                return false;
            }
        });

        myScroll.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                findViewById(R.id.listaDeCiudadesEncargo).getParent()
                        .requestDisallowInterceptTouchEvent(false);
                return false;
            }
        });
        listaCiudadesEncargo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });*/
        //End controlando Scroll

        //Start número de contacto
        final EditText txtNumberContactoEncargo = (EditText) findViewById(R.id.txtNumeroContactoEncargo);
        SharedPreferences myPreferences;
        myPreferences =  PreferenceManager.getDefaultSharedPreferences(this);
        String numero = myPreferences.getString("Number_Cliente", "");

        txtNumberContactoEncargo.setText(numero);

        txtNumberContactoEncargo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (txtNumberContactoEncargo.getText().length() == 9) {
                    btnConfirmaEncargo.setEnabled(validaNumero(txtNumberContactoEncargo));
                } else {
                    btnConfirmaEncargo.setEnabled(false);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (txtNumberContactoEncargo.getText().length() == 9) {
                    btnConfirmaEncargo.setEnabled(validaNumero(txtNumberContactoEncargo));
                } else {
                    btnConfirmaEncargo.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (txtNumberContactoEncargo.getText().length() == 9) {
                    btnConfirmaEncargo.setEnabled(validaNumero(txtNumberContactoEncargo));
                    //btnConfirmaEncargo.setBackgroundResource(R.drawable.boton_rojo);
                } else {
                    btnConfirmaEncargo.setEnabled(false);
                    //btnConfirmaEncargo.setBackgroundResource(R.drawable.boton_disabled);
                }
            }
        });
        //End Número de contacto

        //Inicializando cajas de texto luagr y encargo
        //final EditText txtLugarRecoger = (EditText) findViewById(R.id.txtLugarRecogerEncargo);
        final EditText txtContenidoEncargo = (EditText) findViewById(R.id.txtDetallesEncargo);

        //Ejecutando Boton
        btnConfirmaEncargo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtContenidoEncargo.getText().length() >0){
                    //String LugarRecogerEncargo = txtLugarRecoger.getText().toString();
                    String DetalleEncargo = txtContenidoEncargo.getText().toString();
                    String NumeroContactoEncargo = txtNumberContactoEncargo.getText().toString();
                    //myEditor.putString("LugarRecogerEncargo", LugarRecogerEncargo);
                    myEditor.putString("DetalleEncargo", DetalleEncargo);
                    myEditor.putString("NumeroContactoEncargo", NumeroContactoEncargo);
                    myEditor.commit();
                    Intent intent = new Intent(EncargoActivity.this, PagoEncargoActivity.class);
                    startActivity(intent);
                }else{
                    Toast toast = Toast.makeText(EncargoActivity.this, "Por favor, Complete todos los datos.", Toast.LENGTH_SHORT);
                    View vistaToast = toast.getView();
                    vistaToast.setBackgroundResource(R.drawable.toast_yellow);
                    toast.show();
                }
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_encargo, menu);
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
            Toast toast = Toast.makeText(EncargoActivity.this, "Por favor, Ingrese un número de celular válido.", Toast.LENGTH_SHORT);
            View vistaToast = toast.getView();

            vistaToast.setBackgroundResource(R.drawable.toast_yellow);
            toast.show();
            return false;
        }else
            return true;
    }

    public void listarCiudades(){
        listaCiudadesEncargo.setNumColumns(1);
        adapter = new CiudadListAdapter(EncargoActivity.this, R.layout.list_ciudades_item, listCiu);
        listaCiudadesEncargo.setAdapter(adapter);
    }
}
