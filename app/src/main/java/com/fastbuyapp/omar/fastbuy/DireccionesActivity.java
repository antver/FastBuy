package com.fastbuyapp.omar.fastbuy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fastbuyapp.omar.fastbuy.Validaciones.ValidacionDatos;
import com.fastbuyapp.omar.fastbuy.adaptadores.MisDireccionesAdapter;
import com.fastbuyapp.omar.fastbuy.config.Globales;
import com.fastbuyapp.omar.fastbuy.entidades.Ubicaciones;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class DireccionesActivity extends AppCompatActivity {

    GridView gridView;
    ProgressDialog progDailog=null;
    ArrayList<Ubicaciones> list;
    MisDireccionesAdapter adapter = null;
    SharedPreferences myPreferences;
    String tokencito, number;
    Intent intent;

    ImageButton btnCarrito;

    @Override
    protected void onResume() {
        super.onResume();
        ValidacionDatos valida = new ValidacionDatos();
        valida.validarCarritoVacio(btnCarrito);
        listarDirecciones();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direcciones);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_chevron_left_black_24dp));

        //Inicializando Componentes
        gridView = (GridView) findViewById(R.id.gridMisDirecciones);
        TextView txtNombreUser = (TextView) findViewById(R.id.txtNameUserHistorial);
        TextView txtNumberUser = (TextView) findViewById(R.id.txtNumberUserHistorial);
        TextView btnAddDirecc = (TextView) findViewById(R.id.btnNewDirec);

        myPreferences =  PreferenceManager.getDefaultSharedPreferences(this);
        String name = myPreferences.getString("Name_Cliente", "");
        number = myPreferences.getString("Number_Cliente", "");
        tokencito = myPreferences.getString("tokencito", "");
        txtNombreUser.setText(name);
        txtNumberUser.setText(number);

        //listarDirecciones();

        btnAddDirecc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(DireccionesActivity.this,NewDireccionActivity.class);
                intent.putExtra("tipo","new");
                startActivity(intent);
            }
        });

        //Menu
        ImageButton btnHome = (ImageButton) findViewById(R.id.btnHome);
        ImageButton btnFavoritos = (ImageButton) findViewById(R.id.btnFavoritos);
        btnCarrito = (ImageButton) findViewById(R.id.btnCarrito);
        ImageButton btnUsuario = (ImageButton) findViewById(R.id.btnUsuario);
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DireccionesActivity.this, PrincipalActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        btnFavoritos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DireccionesActivity.this, FavoritosActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        btnCarrito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DireccionesActivity.this, CarritoActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        btnUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    public void listarDirecciones()
    {
        String consulta = "https://apifbdelivery.fastbuych.com/Delivery/ListarDirecciones_Cliente_XTelefono?auth="+tokencito+"&telefono="+number;
        progDailog = new ProgressDialog(DireccionesActivity.this);
        progDailog.setMessage("Cargando Direcciones...");
        progDailog.setIndeterminate(true);
        progDailog.setCancelable(false);
        progDailog.show();
        EnviarRecibirDatos(consulta);
    }

    public void EnviarRecibirDatos(String URL){
        RequestQueue queue = Volley.newRequestQueue(DireccionesActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.length() > 0) {
                    try {
                        JSONArray ja = new JSONArray(response);
                        list = new ArrayList<>();
                        for (int i = 0; i < ja.length(); i++) {
                            JSONObject objeto = ja.getJSONObject(i);
                            Ubicaciones direc = new Ubicaciones();
                            direc.setCodigo(objeto.getInt("CD_Codigo"));
                            direc.setDireccion(objeto.getString("CD_Direccion"));
                            direc.setEtiqueta(objeto.getString("CD_Etiqueta"));
                            direc.setCiudad(objeto.getString("CD_Ciudad"));
                            direc.setLatitud(objeto.getString("CD_Latitud"));
                            direc.setLongitud(objeto.getString("CD_Longitud"));
                            list.add(direc);
                        }

                        gridView.setNumColumns(1);
                        adapter = new MisDireccionesAdapter(DireccionesActivity.this, R.layout.list_direccion_item, list);
                        gridView.setAdapter(adapter);

                        progDailog.dismiss();
                    } catch (JSONException e) {
                        e.printStackTrace();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_mis_direcciones, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return false;
    }
}
