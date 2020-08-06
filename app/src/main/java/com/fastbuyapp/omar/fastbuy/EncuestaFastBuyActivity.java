package com.fastbuyapp.omar.fastbuy;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputFilter;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fastbuyapp.omar.fastbuy.config.Globales;
import com.fastbuyapp.omar.fastbuy.entidades.Encuesta;
import com.fastbuyapp.omar.fastbuy.entidades.ItemEncuesta;
import com.fastbuyapp.omar.fastbuy.entidades.ProductoPresentacion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Random;

public class EncuestaFastBuyActivity extends AppCompatActivity {

    ProgressDialog progDailog = null;
    GridView listaRespuestas;
    TextView txtComentarioEncuesta,txtPreguntaEncuesta,txtOtro;
    ArrayList<Encuesta> list;
    String Respuesta = "", CodPregunta = "", CodRespuesta = "", tokencito, celular, nombre;
    int CantidadRespuestas = 0, posiRespuesta = 0;

    Button btnEnviarRespuestaEncuesta;

    ItemEncuestaListAdapter adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encuesta_fast_buy);

        //Start Diseño de popup
        DisplayMetrics medidasVentana = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(medidasVentana);

        int ancho = medidasVentana.widthPixels;
        final int alto = medidasVentana.heightPixels;

        getWindow().setLayout((int)(ancho*0.90), (int)(alto*0.90));
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //End Diseño de popup

        //inicializando variables
        txtPreguntaEncuesta = findViewById(R.id.txtPreguntaEncuesta);
        txtComentarioEncuesta = findViewById(R.id.txtComentarioEncuesta);
        txtOtro = findViewById(R.id.txtOtro);
        listaRespuestas = findViewById(R.id.listRespuestasEncuentas);
        Button btnOmitirEncuesta = findViewById(R.id.btnOmitirEncuesta);
        btnEnviarRespuestaEncuesta = findViewById(R.id.btnEnviarRespuestaEncuesta);
        SharedPreferences myPreferences;
        myPreferences =  PreferenceManager.getDefaultSharedPreferences(this);
        tokencito = myPreferences.getString("tokencito", "");
        celular = myPreferences.getString("Number_Cliente", "unknown");
        nombre = myPreferences.getString("Name_Cliente", "");
        String consulta = "https://apifbdelivery.fastbuych.com/Delivery/ListarEncuesta?auth="+ tokencito;
        EnviarRecibirDatos(consulta);

        listaRespuestas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                for (int i = 0; i< list.size(); i++){
                    list.get(i).getListItems().setCheck(false);
                }
                list.get(position).getListItems().setCheck(true);
                AñadeElementosGridView(list);

                posiRespuesta = position;
                CantidadRespuestas = list.get(position).getListItems().getCantidad();

                if(list.get(position).getListItems().getRespuesta().equals("Otro")){
                    respuestaAlternativa(position);
                }
                else{
                    Respuesta = list.get(position).getListItems().getRespuesta();
                    CodRespuesta = String.valueOf(list.get(position).getListItems().getCodigo());
                    txtOtro.setVisibility(View.GONE);
                    txtOtro.setText("Otro: ");
                    //CantidadRespuestas = list.get(position).getListItems().getCantidad();
                }
            }
        });

        btnOmitirEncuesta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnEnviarRespuestaEncuesta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Respuesta.equals("") && !CodRespuesta.equals("")){
                    btnEnviarRespuestaEncuesta.setEnabled(false);
                    try {
                        EnviarRespuesta(CodPregunta,Respuesta,CodRespuesta,CantidadRespuestas);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    if (list.get(posiRespuesta).getListItems().getRespuesta().equals("Otro")){
                        respuestaAlternativa(posiRespuesta);
                    }
                }
            }
        });

    }

    public void respuestaAlternativa(final int pos){
        AlertDialog.Builder builder = new AlertDialog.Builder(EncuestaFastBuyActivity.this);
        builder.setTitle("Etiqueta Personalizada");

        // Set up the input
        final EditText input = new EditText(EncuestaFastBuyActivity.this);
        input.setHint(R.string.text_respuesta_personalizada2);
        //input.setBackgroundResource(R.drawable.caja_texto_input);
        input.setFilters(new InputFilter[] {new InputFilter.LengthFilter(200)});//200
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setGravity(Gravity.BOTTOM);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Respuesta = input.getText().toString();
                txtOtro.setVisibility(View.VISIBLE);
                txtOtro.setText("Otro: "+Respuesta);
                CodRespuesta = String.valueOf(list.get(pos).getListItems().getCodigo());
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Respuesta = "";
                CodRespuesta = "";
                txtOtro.setVisibility(View.GONE);
                txtOtro.setText("Otro: ");
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void EnviarRecibirDatos(String URL){
        progDailog = new ProgressDialog(EncuestaFastBuyActivity.this);
        progDailog.setMessage("Cargando...");
        progDailog.setIndeterminate(true);
        progDailog.setCancelable(false);
        progDailog.show();

        RequestQueue queue = Volley.newRequestQueue(EncuestaFastBuyActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener < String > () {
            @Override
            public void onResponse(String response) {
                if (response.length()>0){
                    try {
                        JSONArray ja = new JSONArray(response);
                        list = new ArrayList<>();
                        for (int i = 0; i < ja.length(); i++) {
                            JSONObject objeto = ja.getJSONObject(i);
                            Encuesta encuesta = new Encuesta();
                            encuesta.setCodigo(objeto.getInt("PRG_Codigo"));
                            encuesta.setPregunta(objeto.getString("PRG_Pregunta"));
                            encuesta.setDescripcion(objeto.getString("PRG_Descripcion"));

                            ItemEncuesta item = new ItemEncuesta();
                            item.setCodigo(objeto.getInt("ALT_Codigo"));
                            item.setRespuesta(objeto.getString("ALT_Alternativa"));
                            item.setCantidad(objeto.getInt("PRA_Cantidad"));
                            item.setCheck(false);

                            encuesta.setListItems(item);

                            txtPreguntaEncuesta.setText(encuesta.getPregunta());
                            txtComentarioEncuesta.setText(encuesta.getDescripcion());
                            CodPregunta = String.valueOf(encuesta.getCodigo());
                            list.add(encuesta);
                        }

                        listaRespuestas.setNumColumns(1);

                        int altura = (int) list.size()*110;
                        ViewGroup.LayoutParams params = listaRespuestas.getLayoutParams();
                        params.height = altura;
                        listaRespuestas.setLayoutParams(params);

                        AñadeElementosGridView(list);

                        progDailog.dismiss();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        progDailog.dismiss();
                    }
                    catch (IllegalArgumentException i){
                        i.printStackTrace();
                        progDailog.dismiss();
                    }
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        queue.add(stringRequest);
    }

    public void AñadeElementosGridView(ArrayList<Encuesta> miLista){
        adapter = new ItemEncuestaListAdapter(EncuestaFastBuyActivity.this, R.layout.presentacion_list_item, miLista);
        listaRespuestas.setAdapter(adapter);
    }

    public static String generaCodigoCupon(int length){
        String alphabet =
                new String("0123456789AZBYCXDWEVFUGTHSIRJQKPLOMNNMOLPKQJRISHTGUFVEWDXCYBZA0123456789"); //9
        int n = alphabet.length(); //10

        String result = new String();
        Random r = new Random(); //11

        for (int i=0; i<length; i++) //12
            result = result + alphabet.charAt(r.nextInt(n)); //13

        return result;
    }

    public void EnviarRespuesta(String copPreg, String respuesta,String codRes, int CantidadRes) throws UnsupportedEncodingException {
        String a = URLEncoder.encode(copPreg, "UTF-8");
        String b = URLEncoder.encode(codRes, "UTF-8");
        String c = URLEncoder.encode(respuesta, "UTF-8");
        String d = URLEncoder.encode(String.valueOf(CantidadRes), "UTF-8");
        String e = URLEncoder.encode(celular, "UTF-8");
        String f = URLEncoder.encode(nombre, "UTF-8");
        String g = generaCodigoCupon(6);
        String URL = "https://apifbdelivery.fastbuych.com/Delivery/RespuestaEncuesta?auth="+ tokencito+"&codPregunta="+a+"&codRespuesta="+b+"&Respuesta="+c+"&cantRespuesta="+d+"&telefono="+e+"&usuario="+f+"&cupon="+g;
        Log.v("respuesta",URL);
        progDailog = new ProgressDialog(EncuestaFastBuyActivity.this);
        progDailog.setMessage("Enviando Respuesta...");
        progDailog.setIndeterminate(true);
        progDailog.setCancelable(false);
        progDailog.show();

        RequestQueue queue = Volley.newRequestQueue(EncuestaFastBuyActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener < String > () {
            @Override
            public void onResponse(String response) {
                btnEnviarRespuestaEncuesta.setEnabled(true);
                if(response.equals("ERROR")){
                    progDailog.dismiss();
                    Toast.makeText(EncuestaFastBuyActivity.this, "Error al enviar Respuesta", Toast.LENGTH_SHORT).show();
                }
                else {
                    progDailog.dismiss();
                    Toast.makeText(EncuestaFastBuyActivity.this, "Respuesta Enviada con éxito", Toast.LENGTH_SHORT).show();
                }
                onBackPressed();
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                progDailog.dismiss();
            }
        });

        queue.add(stringRequest);
    }
}
