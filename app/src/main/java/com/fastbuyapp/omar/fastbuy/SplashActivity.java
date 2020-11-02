package com.fastbuyapp.omar.fastbuy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Handler;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.fastbuyapp.omar.fastbuy.Validaciones.ValidacionDatos;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.net.URLEncoder;

public class SplashActivity extends AppCompatActivity {
    SharedPreferences myPreferences;
    SharedPreferences.Editor myEditor;
    private final int DURACION_SPLASH = 2000; // 2 segundos
    int cant = 0;
    String celular, nombre;
    boolean hayUsuario = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        setContentView(R.layout.activity_splash);

        myPreferences =  PreferenceManager.getDefaultSharedPreferences(SplashActivity.this);
        myEditor = myPreferences.edit();
        myEditor.putString("tokencito",  "Xid20200110e34CorpFastBuySAC2020comfastbuyusuario");
        myEditor.commit();
        celular = myPreferences.getString("Number_Cliente", "");
        nombre = myPreferences.getString("Name_Cliente", "");
        String tokencito = myPreferences.getString("tokencito", "");
        Log.v("celular", celular);
        if(celular.equals("") && nombre.equals("")) {
            hayUsuario = false;
            cargarSplash();
        }
        else{
            final String name = myPreferences.getString("Name_Cliente", "");
            final String number = myPreferences.getString("Number_Cliente", "");
            final String e_mail = myPreferences.getString("Email_Cliente", "");
            final String photo = myPreferences.getString("Photo_Cliente", "");
            final ValidacionDatos validacion = new ValidacionDatos();

            if(validacion.hayConexiónRed(SplashActivity.this) == false){
                Intent intentdes = new Intent(SplashActivity.this, ActivityDesconectado.class);
                startActivity(intentdes);
            }
            String URL = "https://apifbdelivery.fastbuych.com/Delivery/ValidarUsuario?auth="+ tokencito +"&telefono="+URLEncoder.encode(number);
            RequestQueue queue = Volley.newRequestQueue(SplashActivity.this);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (response.length()>0){
                        try {
                            JSONObject objeto = new JSONObject(response);
                            String repuesta = objeto.getString("respuesta");
                            if(repuesta.equals("existe")){
                                String nuevocodigo = objeto.getString("codigo");
                                String nuevonombre = objeto.getString("nombre");
                                myEditor.putString("Id_Cliente",  nuevocodigo);
                                myEditor.putString("Name_Cliente", nuevonombre);
                                myEditor.commit();
                                hayUsuario = true;
                            }
                            else{
                                hayUsuario = false;
                            }
                            cargarSplash();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, new Response.ErrorListener(){
                @Override
                public void onErrorResponse(VolleyError error) {

                    Intent intentdes = new Intent(SplashActivity.this, ActivityDesconectado.class);
                    startActivity(intentdes);
                }
            });
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(stringRequest);
        }
    }
    void cargarSplash(){
        new Handler().postDelayed(new Runnable(){
            public void run(){
                if(hayUsuario == false) {
                    Intent intent = new Intent(SplashActivity.this, TerminosYCondicionesActivity.class);
                    startActivity(intent);
                }
                else {
                    Intent intent = new Intent(SplashActivity.this,PrincipalActivity.class);
                    startActivity(intent);
                }
                finish();
            };
        }, DURACION_SPLASH);
    }


}


/*private static String convertToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (byte b : data) {
            int halfbyte = (b >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                buf.append((0 <= halfbyte) && (halfbyte <= 9) ? (char) ('0' + halfbyte) : (char) ('a' + (halfbyte - 10)));
                halfbyte = b & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }*/
    /*
    try {
        PackageInfo info = getPackageManager().getPackageInfo(
                "com.fastbuyapp.omar.fastbuy",
                PackageManager.GET_SIGNATURES);
        for (Signature signature : info.signatures) {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(signature.toByteArray());
            Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            Log.d("SHA1:", convertToHex(md.digest()));
        }
    } catch (PackageManager.NameNotFoundException e) {

    } catch (NoSuchAlgorithmException e) {

    }
    */

