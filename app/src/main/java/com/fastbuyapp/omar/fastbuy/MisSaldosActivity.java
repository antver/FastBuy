package com.fastbuyapp.omar.fastbuy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fastbuyapp.omar.fastbuy.Validaciones.ValidacionDatos;
import com.fastbuyapp.omar.fastbuy.adaptadores.MisDireccionesAdapter;
import com.fastbuyapp.omar.fastbuy.config.Globales;
import com.fastbuyapp.omar.fastbuy.entidades.Cupon;
import com.fastbuyapp.omar.fastbuy.entidades.Ubicaciones;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class MisSaldosActivity extends AppCompatActivity {

    GridView gridView;
    ProgressDialog progDailog=null;
    ArrayList<Cupon> list;
    CuponListAdapter adapter = null;
    SharedPreferences myPreferences;
    String tokencito, celular;
    LinearLayout layoutNoHay;

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_saldos);

        //Inicializando Componentes
        gridView = (GridView) findViewById(R.id.gridMisSaldos);
        //TextView txtNombreUser = (TextView) findViewById(R.id.txtNameUserSaldos);
        //TextView txtNumberUser = (TextView) findViewById(R.id.txtNumberUserSaldos);
        myPreferences =  PreferenceManager.getDefaultSharedPreferences(this);
        celular = myPreferences.getString("Number_Cliente", "unknown");
        String nombre = myPreferences.getString("Name_Cliente", "");
        tokencito = myPreferences.getString("tokencito", "");
        //txtNombreUser.setText(nombre);
        //txtNumberUser.setText(celular);
        layoutNoHay = (LinearLayout) findViewById(R.id.layoutNoHay);
    }

    public void listarCupones()
    {
        String consulta = "https://apifbdelivery.fastbuych.com/Delivery/ListaCuponXCliente?auth="+tokencito+"&telefono="+ celular;
        progDailog = new ProgressDialog(MisSaldosActivity.this);
        progDailog.setMessage("Cargando Cupones...");
        progDailog.setIndeterminate(true);
        progDailog.setCancelable(false);
        progDailog.show();
        EnviarRecibirDatos(consulta);
    }

    public void EnviarRecibirDatos(String URL){
        RequestQueue queue = Volley.newRequestQueue(MisSaldosActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.length() > 0) {
                    try {
                        JSONArray ja = new JSONArray(response);
                        list = new ArrayList<>();
                        layoutNoHay.setVisibility(View.GONE);
                        if(ja.length() == 0){
                            layoutNoHay.setVisibility(View.VISIBLE);
                        }
                        for (int i = 0; i < ja.length(); i++) {
                            JSONObject objeto = ja.getJSONObject(i);
                            Cupon cupon = new Cupon();
                            cupon.setCodigo(objeto.getString("CUP_Codigo"));
                            cupon.setCantidad(objeto.getInt("CUP_Cantidad"));
                            cupon.setDescripcion(objeto.getString("CUP_Descripcion"));
                            cupon.setEstado(objeto.getInt("CUP_Estado"));
                            cupon.setPromo(objeto.getString("CUP_Promo"));
                            /*DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                            String fecha1 = dateFormat.format(objeto.getString("CUP_FechaInicio"));
                            String fecha2 = dateFormat.format(objeto.getString("CUP_FechaFin"));*/
                            cupon.setFecha(objeto.getString("CUP_FechaInicio")+" hasta "+objeto.getString("CUP_FechaFin"));
                            list.add(cupon);
                        }

                        gridView.setNumColumns(1);
                        adapter = new CuponListAdapter(MisSaldosActivity.this, R.layout.list_item_cupones, list);
                        gridView.setAdapter(adapter);

                        progDailog.dismiss();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        progDailog.dismiss();
                    }
                }
                else{
                    progDailog.dismiss();
                }
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
