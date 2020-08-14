package com.fastbuyapp.omar.fastbuy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fastbuyapp.omar.fastbuy.config.Globales;
import com.fastbuyapp.omar.fastbuy.entidades.Tutorial_Item;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class TutorialActivity extends AppCompatActivity {

    private TutorialAdapter tutorialAdapter;
    private LinearLayout layoutTutorialIndicador;
    private Button btnNextTuto, btnComenzar;
    String codiClien;
    SharedPreferences myPreferences;
    SharedPreferences.Editor myEditor;
    ProgressDialog progDailog = null;
    String codigo, tokencito;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);
        //Registrando en DB y guardando datos del usuario en memoria Caché
        //esto es para guardar en la caché
        myPreferences =  PreferenceManager.getDefaultSharedPreferences(TutorialActivity.this);
        myEditor = myPreferences.edit();
        codigo = myPreferences.getString("Id_Cliente", "0");
        String name = myPreferences.getString("Name_Cliente", "");
        String number = myPreferences.getString("Number_Cliente", "");
        String e_mail = myPreferences.getString("Email_Cliente", "");
        String city = myPreferences.getString("City_Cliente", "");
        String photo = myPreferences.getString("Photo_Cliente", "");
        tokencito = myPreferences.getString("tokencito", "");
        GuardarUsuario(name, number, e_mail, photo);
        layoutTutorialIndicador = (LinearLayout) findViewById(R.id.indicadorTutorial);
        btnNextTuto = (Button) findViewById(R.id.btnNextTutorial);
        btnComenzar = (Button) findViewById(R.id.btnComenzar);
        setupTutorialItems();
        final ViewPager2 viewPager2Tutorial = (ViewPager2) findViewById(R.id.tutorialViewPager2);
        viewPager2Tutorial.setAdapter(tutorialAdapter);
        setupTutorialIndicadores();
        setEstadoIndicadorTutorial(0);

        final int cuenta;

        viewPager2Tutorial.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setEstadoIndicadorTutorial(position);
                if (position == 2){
                    btnComenzar.setVisibility(View.VISIBLE);
                    btnNextTuto.setVisibility(View.GONE);
                }
                else {
                    btnComenzar.setVisibility(View.GONE);
                    btnNextTuto.setVisibility(View.VISIBLE);
                }
            }
        });

        btnNextTuto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewPager2Tutorial.getCurrentItem()+1 < tutorialAdapter.getItemCount()){
                    viewPager2Tutorial.setCurrentItem(viewPager2Tutorial.getCurrentItem()+1);
                }
            }
        });

        btnComenzar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Toast toast = Toast.makeText(v.getContext(), "Bienvenido...", Toast.LENGTH_SHORT);
                View vistaToast = toast.getView();
                vistaToast.setBackgroundResource(R.drawable.toast_bienvenido);
                toast.show();*/
                finish();
                Intent intent = new Intent(TutorialActivity.this, CiudadActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setupTutorialItems(){
        List<Tutorial_Item> tutorialItems = new ArrayList<>();
        Tutorial_Item item1 = new Tutorial_Item();
        item1.setImagen(R.drawable.paso_01);
        Tutorial_Item item2 = new Tutorial_Item();
        item2.setImagen(R.drawable.paso_02);
        Tutorial_Item item3 = new Tutorial_Item();
        item3.setImagen(R.drawable.paso_03);
        tutorialItems.add(item1);
        tutorialItems.add(item2);
        tutorialItems.add(item3);
        tutorialAdapter = new TutorialAdapter(tutorialItems);
    }

    private void setupTutorialIndicadores(){
        ImageView[] indicadores = new ImageView[tutorialAdapter.getItemCount()];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(10,0,10,0);
        for (int i=0; i<indicadores.length; i++){
            indicadores[i] = new ImageView(getApplicationContext());
            indicadores[i].setImageDrawable(ContextCompat.getDrawable(
                    getApplicationContext(), R.drawable.indicador_tutorial_off));
            indicadores[i].setLayoutParams(layoutParams);
            layoutTutorialIndicador.addView(indicadores[i]);
            Log.v("Indicador",String.valueOf(i));
        }
    }

    private void setEstadoIndicadorTutorial(int index){
        int childCount = layoutTutorialIndicador.getChildCount();
        for(int i=0; i< childCount; i++){
            ImageView imageView = (ImageView) layoutTutorialIndicador.getChildAt(i);
            if (i == index)
                imageView.setImageDrawable(ContextCompat.getDrawable(
                        getApplicationContext(),R.drawable.indicador_tutorial_on));
            else
                imageView.setImageDrawable(ContextCompat.getDrawable(
                        getApplicationContext(),R.drawable.indicador_tutorial_off));
        }
    }

    public void GuardarUsuario(final String nombre, final String numero, final String email, final String foto){
        try {
            String nombreClie = URLEncoder.encode(nombre,"UTF-8");
            String consulta = "https://apifbdelivery.fastbuych.com/Delivery/RegistrarUsuario?auth="+tokencito+"&nombre="+nombreClie+"&telefono="+numero+"&email="+email;
            Log.v("CONSULTAES", consulta);
            progDailog = new ProgressDialog(TutorialActivity.this);
            progDailog.setMessage("Cargando...");
            progDailog.setIndeterminate(true);
            progDailog.setCancelable(false);
            progDailog.show();
            RequestQueue queue = Volley.newRequestQueue(TutorialActivity.this);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, consulta, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (response.length() > 0) {
                        try {
                            JSONObject jo = new JSONObject(response);
                            codiClien = jo.getString("Codigo_Cliente");
                            Log.v("REGISTRO USUARIO","Éxito");
                            if (myPreferences.getString("Name_Cliente","").equals("")){
                                Log.v("numerito",numero);
                                //esto es para guardar en la caché
                                myEditor.putString("Id_Cliente",  codiClien);
                                myEditor.putString("Name_Cliente",nombre);
                                myEditor.putString("Number_Cliente",numero);
                                myEditor.putString("Email_Cliente",email);
                                myEditor.putString("Photo_Cliente",foto);
                                myEditor.commit();
                            }
                            else
                                Log.v("MENSAJITO","YA EXISTE DATOS DEL CLIENTE");

                            progDailog.dismiss();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            //progDailog.dismiss();
                        }
                    }

                }
            }, new Response.ErrorListener(){
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.v("REGISTRO USUARIO","Falló");
                }
            });
            queue.add(stringRequest);
        } catch (Exception e) {
            e.printStackTrace();
            //progDailog.dismiss();
        }
    }

}
