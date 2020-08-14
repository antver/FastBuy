package com.fastbuyapp.omar.fastbuy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fastbuyapp.omar.fastbuy.Validaciones.ValidacionDatos;
import com.fastbuyapp.omar.fastbuy.config.Globales;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import cn.refactor.lib.colordialog.PromptDialog;

public class PagoEncargoActivity extends AppCompatActivity {

    ProgressDialog progDailog = null;
    SharedPreferences.Editor myEditor;
    ImageButton btnCarrito;
    String tokencito, direccion_seleccionada;
    String number, latitud_seleccionada, longitud_seleccionada, ciudad, categoria, name;
    String LugarRecogerEncargo, DetalleEncargo, NumeroContactoEncargo;
    SharedPreferences myPreferences;
    TextView txtDeliveryTotal;
    @Override
    public void onResume() {
        super.onResume();

        ValidacionDatos valida = new ValidacionDatos();
        valida.validarCarritoVacio(btnCarrito);
        PreferenceManager.getDefaultSharedPreferences(this);
        categoria  = myPreferences.getString("categoria", "0");
        float costoEnvio = myPreferences.getFloat("monto_delivery", 0);
        //Start Logica para el pago
        // Iniciando variables para Mostrar datos calculados
        txtDeliveryTotal = (TextView) findViewById(R.id.txtDeliveryGeneralEncargo);
        txtDeliveryTotal.setText(String.format("%.2f",costoEnvio).toString().replace(",","."));
        //End Logica para el pago
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pago_encargo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Start pop-up para seleccionar la direccion
        abrirPopUp();
        //End pop-up para seleccionar la direccion

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_chevron_left_black_24dp));



        myPreferences =  PreferenceManager.getDefaultSharedPreferences(this);
        myEditor = myPreferences.edit();

        name = myPreferences.getString("Name_Cliente", "");
        number = myPreferences.getString("Number_Cliente", "");
        ciudad = myPreferences.getString("City_Cliente", "");
        final String e_mail = myPreferences.getString("Email_Cliente", "");
        tokencito = myPreferences.getString("tokencito", "");
        direccion_seleccionada = myPreferences.getString("direccion_seleccionada", "0");
        latitud_seleccionada  = myPreferences.getString("latitud_seleccionada", "0");
        longitud_seleccionada  = myPreferences.getString("longitud_seleccionada", "0");
        categoria  = myPreferences.getString("categoria", "0");
        LugarRecogerEncargo  = myPreferences.getString("LugarRecogerEncargo", "0");
        DetalleEncargo  = myPreferences.getString("DetalleEncargo", "0");
        NumeroContactoEncargo  = myPreferences.getString("NumeroContactoEncargo", "0");
        final float subtotal = myPreferences.getFloat("monto_delivery", 0);
        //Start Boton Comprar
        final Button btnGenerarEncargo = (Button) findViewById(R.id.btnGeneraEncargo);
        btnGenerarEncargo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(categoria.equals("3")){
                    if (subtotal == 0.0)
                        abrirPopUp();
                    else{
                        //Toast.makeText(PagoEncargoActivity.this,"Encargo Registrado...",Toast.LENGTH_SHORT).show();
                        AlertDialog.Builder builder = new AlertDialog.Builder(PagoEncargoActivity.this);
                        builder.setMessage("Se registrará un nuevo Encargo. ¿Desea continuar?");
                        builder.setTitle("Nuevo Encargo");
                        builder.setPositiveButton("Si", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                try {
                                    btnGenerarEncargo.setEnabled(false);
                                    btnGenerarEncargo.setBackgroundResource(R.drawable.botongris);
                                    progDailog = new ProgressDialog(PagoEncargoActivity.this);
                                    progDailog.setMessage("Registrando Encargo...");
                                    progDailog.setIndeterminate(true);
                                    progDailog.setCancelable(false);
                                    progDailog.show();
                                    registrarEncargo(name, direccion_seleccionada+ ", " + ciudad, number, NumeroContactoEncargo, LugarRecogerEncargo, DetalleEncargo, "0.0", ciudad, latitud_seleccionada, longitud_seleccionada, "0.0", "0.0", txtDeliveryTotal.getText().toString());
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                            }

                        });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                }
                if (categoria.equals("4")){
                    if (subtotal == 0.0)
                        abrirPopUp();
                    else{
                        //Toast.makeText(PagoEncargoActivity.this,"Pedido Extra Registrado...",Toast.LENGTH_SHORT).show();
                        AlertDialog.Builder builder = new AlertDialog.Builder(PagoEncargoActivity.this);
                        builder.setMessage("Se registrará un nuevo encargo. ¿Desea continuar?");
                        builder.setTitle("Nuevo Encargo");
                        builder.setPositiveButton("Si", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                try {
                                    btnGenerarEncargo.setEnabled(false);
                                    btnGenerarEncargo.setBackgroundResource(R.drawable.botongris);
                                    progDailog = new ProgressDialog(PagoEncargoActivity.this);
                                    progDailog.setMessage("Registrando Encargo...");
                                    progDailog.setIndeterminate(true);
                                    progDailog.setCancelable(false);
                                    progDailog.show();
                                    registrarEncargo(name , direccion_seleccionada + ", " + ciudad, number, NumeroContactoEncargo, LugarRecogerEncargo, DetalleEncargo, "0.0", ciudad, latitud_seleccionada, longitud_seleccionada, "0.0", "0.0", txtDeliveryTotal.getText().toString());

                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                            }

                        });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                }
            }
        });
        //End Boton Comprar

        //botones del menu
        ImageButton btnHome = (ImageButton) findViewById(R.id.btnHome);
        ImageButton btnFavoritos = (ImageButton) findViewById(R.id.btnFavoritos);
        btnCarrito = (ImageButton) findViewById(R.id.btnCarrito);
        ImageButton btnUsuario = (ImageButton) findViewById(R.id.btnUsuario);
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PagoEncargoActivity.this, PrincipalActivity.class);
                startActivity(intent);
            }
        });

        btnFavoritos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PagoEncargoActivity.this, FavoritosActivity.class);
                startActivity(intent);
            }
        });

        btnCarrito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PagoEncargoActivity.this, CarritoActivity.class);
                startActivity(intent);
            }
        });

        btnUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PagoEncargoActivity.this, UserActivity.class);
                startActivity(intent);
            }
        });
    }

    public  void abrirPopUp(){
        Intent intent = new Intent(PagoEncargoActivity.this, SeleccionaDireccionActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menupagoefectivo, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return false;
    }

    public void registrarEncargo(String nombre, String direccion, String telefonoLocal, String telefonoContacto, String tienda, String pedido, String monto, String origen, String latitud, String longitud, String pago, String vuelto, String delivery) throws UnsupportedEncodingException {
        String a = URLEncoder.encode(nombre, "UTF-8");
        String b = URLEncoder.encode(direccion, "UTF-8");
        String c = URLEncoder.encode(telefonoLocal, "UTF-8");
        String d = URLEncoder.encode(telefonoContacto, "UTF-8");
        String e = URLEncoder.encode(tienda, "UTF-8");
        String f = URLEncoder.encode(pedido, "UTF-8");
        String g = URLEncoder.encode(monto, "UTF-8");
        String h = URLEncoder.encode(origen, "UTF-8");
        String i = URLEncoder.encode(latitud, "UTF-8");
        String j = URLEncoder.encode(longitud, "UTF-8");
        String k = URLEncoder.encode(pago, "UTF-8");
        String l = URLEncoder.encode(vuelto, "UTF-8");
        String m = URLEncoder.encode(delivery, "UTF-8");
        String consulta = "https://apifbdelivery.fastbuych.com/Delivery/GuardarPedidoExtra?auth="+tokencito+"&nombre="+a+"&direccion="+b+"&telefonoLocal=" + c +"&telefonoContacto=" + d +"&telefonoCliente=" + number +"&tienda=" + e +"&pedido=" + f +"&monto=" + g+"&origen=" + h + "&latitud=" + i + "&longitud=" + j +"&pago=" + k + "&vuelto=" + l+ "&delivery=" + m ;
        Log.v("URL_ENCARGO",consulta);
        RegistrarEncargoBD(consulta);
    }

    public void RegistrarEncargoBD(String URL){
        RequestQueue queue = Volley.newRequestQueue(PagoEncargoActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.length() > 0) {
                    try {
                        JSONObject jo = new JSONObject(response);
                        Log.v("Codigo_Extra", jo.getString("Codigo_Extra"));
                        if (jo.getInt("Codigo_Extra") != 0){
                            progDailog.dismiss();
                            new PromptDialog(PagoEncargoActivity.this)
                                    .setDialogType(PromptDialog.DIALOG_TYPE_SUCCESS)
                                    .setAnimationEnable(true)
                                    .setTitleText("EXITO")
                                    .setContentText("Su encargo fue Registrado con éxito.")
                                    .setPositiveListener("OK", new PromptDialog.OnPositiveListener() {
                                        @Override
                                        public void onClick(PromptDialog dialog) {
                                            dialog.dismiss();
                                            myEditor.putString("LugarRecogerEncargo", "");
                                            myEditor.putString("DetalleEncargo", "");
                                            myEditor.putString("NumeroContactoEncargo", "");
                                            Intent intent = new Intent(PagoEncargoActivity.this, PrincipalActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);

                                        }
                                    }).show();
                        }else{
                            Toast.makeText(PagoEncargoActivity.this,"ERROR AL GENERAR PEDIDO EXTRA, INTENTELO NUEVAMENTE...",Toast.LENGTH_SHORT).show();
                            progDailog.dismiss();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(PagoEncargoActivity.this,"ERROR AL GENERAR PEDIDO EXTRA, INTENTELO NUEVAMENTE...",Toast.LENGTH_SHORT).show();
                        progDailog.dismiss();
                    }

                    //si no funciona el intent acá se debería de reiniciar los globales de Encargo
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(PagoEncargoActivity.this,"ERROR AL GENERAR PEDIDO EXTRA, INTENTELO NUEVAMENTE...",Toast.LENGTH_SHORT).show();
                progDailog.dismiss();
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);
    }
}
