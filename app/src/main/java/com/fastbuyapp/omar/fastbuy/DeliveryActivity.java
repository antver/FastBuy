package com.fastbuyapp.omar.fastbuy;

import android.app.ProgressDialog;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fastbuyapp.omar.fastbuy.Validaciones.ValidacionDatos;
import com.fastbuyapp.omar.fastbuy.adaptadores.EmpresaSubcategoriaListAdapter;
import com.fastbuyapp.omar.fastbuy.config.Servidor;
import com.fastbuyapp.omar.fastbuy.entidades.EmpresaSubcategoria;
import com.fastbuyapp.omar.fastbuy.entidades.Pedido;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class DeliveryActivity extends AppCompatActivity {
    GridView gridView;
    ArrayList<EmpresaSubcategoria> list;
    EmpresaSubcategoriaListAdapter adapter = null;
    ProgressDialog progDailog = null;
    SharedPreferences myPreferences;
    SharedPreferences.Editor myEditor;
    ImageButton btnCarrito;

    @Override
    protected void onResume() {
        super.onResume();
        /*ValidacionDatos valida = new ValidacionDatos();
        valida.validarCarritoVacio(btnCarrito);*/
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_chevron_left_black_24dp));
        myPreferences =  PreferenceManager.getDefaultSharedPreferences(DeliveryActivity.this);
        myEditor = myPreferences.edit();
        GridView gv = (GridView) findViewById(R.id.gvSubcategorias);
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int codigo = (list.get(position).getCodigo());
                String descripcion = list.get(position).getDescripcion();
                String imagen = list.get(position).getImagen();
                //Globales.imagenSubcategoria = list.get(position).getImagen();
                myEditor.putString("subcategoria", String.valueOf(codigo));
                myEditor.commit();
                Intent intent = new Intent(DeliveryActivity.this, EstablecimientoActivity.class);
                startActivity(intent);
            }
        });
        try {
            listarSubcategorias();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //botones del menu
        ImageButton btnHome = (ImageButton) findViewById(R.id.btnHome);
        ImageButton btnFavoritos = (ImageButton) findViewById(R.id.btnFavoritos);
        btnCarrito = (ImageButton) findViewById(R.id.btnCarrito);
        ImageButton btnUsuario = (ImageButton) findViewById(R.id.btnUsuario);
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DeliveryActivity.this, PrincipalActivity.class);
                startActivity(intent);
            }
        });

        btnFavoritos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DeliveryActivity.this, FavoritosActivity.class);
                startActivity(intent);
            }
        });

        btnCarrito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DeliveryActivity.this, CarritoActivity.class);
                startActivity(intent);
            }
        });

        btnUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DeliveryActivity.this, UserActivity.class);
                startActivity(intent);
            }
        });
    }

    public void listarSubcategorias() throws UnsupportedEncodingException {
        progDailog = new ProgressDialog(DeliveryActivity.this);
        progDailog.setMessage("Cargando...");
        progDailog.setIndeterminate(true);
        progDailog.setCancelable(false);
        progDailog.show();

        Servidor s = new Servidor();
        //String URL = "https://"+s.getServidor()+"/app/consultasapp/app_empresa_subcategoria_xcategoria_xubicacion.php?categoria=1&ubicacion=1&descripcion="+"";
        //String URL = "https://"+s.getServidor()+"/app/consultasapp/app_empresa_subcategoria_xcategoria_xubicacion.php?categoria="+String.valueOf(Globales.categoria)+"&ubicacion="+String.valueOf(Globales.ubicacion)+"&descripcion="+"";

        String ubicacion = myPreferences.getString("ubicacion", "unknown");
        String tokencito = myPreferences.getString("tokencito", "unknown");
        String categoriaseleccionada = myPreferences.getString("categoria", "unknown");

        String URL = "https://apifbdelivery.fastbuych.com/Delivery/ListarSubCategorias?auth="+tokencito+"&catego="+categoriaseleccionada+"&ubica="+ubicacion; //API
        RequestQueue queue = Volley.newRequestQueue(DeliveryActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.length()>0){
                    Log.v("response_data",response.toString());
                    try {
                        JSONArray ja = new JSONArray(response);
                        try {
                            list = new ArrayList<>();
                            for (int i = 0; i < ja.length(); i++) {
                                JSONObject objeto = ja.getJSONObject(i);
                                EmpresaSubcategoria rubro = new EmpresaSubcategoria();
                                rubro.setCodigo(objeto.getInt("ES_Codigo"));
                                rubro.setDescripcion(objeto.getString("ES_Descripcion"));
                                rubro.setImagen(objeto.getString("ES_Imagen"));
                                rubro.setEstado(objeto.getString("ES_Estado"));
                                list.add(rubro);
                            }
                            gridView = (GridView) findViewById(R.id.gvSubcategorias);
                            adapter = new EmpresaSubcategoriaListAdapter(DeliveryActivity.this, R.layout.list_subcategoria_empresa_item, list);
                            gridView.setAdapter(adapter);
                            progDailog.dismiss();
                        }
                        catch(Exception ex) {}
                    } catch (JSONException e) {
                        Toast toast = Toast.makeText(DeliveryActivity.this, "No Existen SubCategorías por seleccionar en esta Categoría...", Toast.LENGTH_SHORT);
                        View vistaToast = toast.getView();
                        vistaToast.setBackgroundResource(R.drawable.toast_yellow);
                        toast.show();
                        e.printStackTrace();
                        progDailog.dismiss();
                    }
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                error.getMessage();
                Intent intentdes = new Intent(DeliveryActivity.this, ActivityDesconectado.class);
                startActivity(intentdes);
            }
        });
        queue.add(stringRequest);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menusubcategorias, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return false;
    }

    /*@Override
    public void onBackPressed(){
        Intent intent = new Intent(DeliveryActivity.this, PrincipalActivity.class);
        startActivity(intent);
    }*/
}
