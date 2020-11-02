package com.fastbuyapp.omar.fastbuy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fastbuyapp.omar.fastbuy.Interfaces.OnListItemClick;
import com.fastbuyapp.omar.fastbuy.adaptadores.EmpresaListAdapterRV;
import com.fastbuyapp.omar.fastbuy.adaptadores.SubcategoriaListAdapter;
import com.fastbuyapp.omar.fastbuy.entidades.Empresa;
import com.fastbuyapp.omar.fastbuy.entidades.EmpresaSubcategoria;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RestauranteActivity extends AppCompatActivity {
    ArrayList<EmpresaSubcategoria> listaSubcategorias;
    int tiendas;
    ProgressDialog progDailog;
    LinearLayoutManager llm;
    LinearLayoutManager layoutManager1,layoutManager2;
    SharedPreferences myPreferences;
    SharedPreferences.Editor myEditor;
    String codigoZona_usuario, tokencito, categoria, categoria_nombre;
    SubcategoriaListAdapter mAdapter;
    EmpresaListAdapterRV mAdapterEmpresa;
    RecyclerView rvSubcategorias, rvEmpresas;
    int codigoseleccionado;
    ImageView btnAtras;
    TextView txtTituloPantalla;
    Button btnCarga;
    LinearLayout llSubcategorias;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurante);
        //Captura de variables en memoria
        myPreferences =  PreferenceManager.getDefaultSharedPreferences(this);
        myEditor = myPreferences.edit();
        codigoZona_usuario = myPreferences.getString("codigoZona_usuario", "");
        tokencito = myPreferences.getString("tokencito", "");
        categoria = myPreferences.getString("categoria", "");
        categoria_nombre = myPreferences.getString("categoria_nombre", "");

        rvSubcategorias = (RecyclerView) findViewById(R.id.rvSubcategorias);
        rvEmpresas = (RecyclerView) findViewById(R.id.rvEmpresas);
        txtTituloPantalla = (TextView) findViewById(R.id.txtTituloPantalla);
        btnAtras = (ImageView) findViewById(R.id.btnAtras);
        llSubcategorias = (LinearLayout) findViewById(R.id.llSubcategorias);
        rvSubcategorias.setHasFixedSize(true);
        rvEmpresas.setHasFixedSize(true);
        btnCarga = new Button(this);
        txtTituloPantalla.setText(categoria_nombre);
        progDailog = new ProgressDialog(RestauranteActivity.this);
        progDailog.setMessage("Cargando...");
        progDailog.setIndeterminate(true);
        progDailog.setCancelable(false);
        progDailog.show();

        String consulta = "https://apifbdelivery.fastbuych.com/Delivery/ListarInterfazPartners?auth="+ tokencito+"&zona=" + codigoZona_usuario + "&categoria=" + categoria +"&descrip=";

        RequestQueue queue = Volley.newRequestQueue(RestauranteActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, consulta, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.v("ruta_resultado",response);
                if (response.length()>0){
                    try {
                        JSONArray ja = new JSONArray(response);
                        listaSubcategorias = new ArrayList<>();
                        tiendas = 0;
                        for (int i = 0; i < ja.length(); i++) {
                            JSONObject objeto = ja.getJSONObject(i);
                            EmpresaSubcategoria es = new EmpresaSubcategoria();
                            es.setCodigo(objeto.getInt("codigo"));
                            es.setDescripcion(objeto.getString("nombre"));
                            es.setImagen(objeto.getString("imagen"));
                            es.setPosicion(i);

                            ArrayList<Empresa> listaEmpresas = new ArrayList<>();
                            JSONArray jaEmpresas = ja.getJSONObject(i).getJSONArray("partners");
                            for (int j = 0; j < jaEmpresas.length(); j++){
                                JSONObject objectEmpresa = jaEmpresas.getJSONObject(j);
                                Empresa empresa = new Empresa();
                                empresa.setCodigo(objectEmpresa.getInt("EMP_Codigo"));
                                empresa.setNombreComercial(objectEmpresa.getString("EMP_NombreComercial"));
                                empresa.setRazonSocial(objectEmpresa.getString("EMP_RazonSocial"));
                                empresa.setDireccion(objectEmpresa.getString("EMP_Direccion"));
                                empresa.setImagen(objectEmpresa.getString("EMP_Imagen"));
                                empresa.setTelefonos(objectEmpresa.getString("EMP_Telefonos"));
                                empresa.setEstado(objectEmpresa.getInt("EMP_Estado"));
                                empresa.setEstadoAbierto(objectEmpresa.getString("EstadoAbierto"));
                                empresa.setLatitud(objectEmpresa.getDouble("EU_Latitud"));
                                empresa.setLongitud(objectEmpresa.getDouble("EU_Longitud"));
                                empresa.setTaper(objectEmpresa.getString("EMP_CobraTaper"));
                                empresa.setCostoTaper(objectEmpresa.getDouble("EMP_CostoTaper"));
                                empresa.setImagenFondo(objectEmpresa.getString("EMP_Portada"));
                                empresa.setValoracion(objectEmpresa.getString("Valoracion"));
                                empresa.setUbicacion(objectEmpresa.getInt("UBI_Codigo"));
                                empresa.setTiempo(objectEmpresa.getString("TiempoMinimo") + " - " + objectEmpresa.getString("TiempoMaximo") + "'");
                                listaEmpresas.add(empresa);
                            }
                            es.setListEmpresas(listaEmpresas);
                            listaSubcategorias.add(es);

                        }
                        // use a linear layout manager
                        layoutManager1 = new LinearLayoutManager(RestauranteActivity.this, LinearLayoutManager.HORIZONTAL, false);
                        rvSubcategorias.setLayoutManager(layoutManager1);

                        // specify an adapter (see also next example)
                        mAdapter = new SubcategoriaListAdapter(listaSubcategorias, RestauranteActivity.this);
                        rvSubcategorias.setAdapter(mAdapter);

                        OnListItemClick onListItemClick = new OnListItemClick() {
                            @Override
                            public void onClick(View view, int position) {
                                // you will get click here
                                // do your code here
                                if (view != null) {
                                    //int position = rvSubcategorias.getChildAdapterPosition(view);

                                    //RecyclerView.ViewHolder holder = rvSubcategorias.getChildViewHolder(view);
                                    //holder.itemView.findViewById(R.id.row_layout).callOnClick();

                                    codigoseleccionado = listaSubcategorias.get(position).getCodigo();

                                    layoutManager2 = new LinearLayoutManager(RestauranteActivity.this);
                                    rvEmpresas.setLayoutManager(layoutManager2);
                                    // specify an adapter (see also next example)
                                    mAdapterEmpresa = new EmpresaListAdapterRV(listaSubcategorias.get(position).getListEmpresas(), RestauranteActivity.this);
                                    rvEmpresas.setAdapter(mAdapterEmpresa);
                                }
                            }

                        };

                        mAdapter.setClickListener(onListItemClick);

                        if(listaSubcategorias.size()>0){
                            codigoseleccionado = listaSubcategorias.get(0).getCodigo();layoutManager2 = new LinearLayoutManager(RestauranteActivity.this);
                            rvEmpresas.setLayoutManager(layoutManager2);
                            // specify an adapter (see also next example)
                            mAdapterEmpresa = new EmpresaListAdapterRV(listaSubcategorias.get(0).getListEmpresas(), RestauranteActivity.this);
                            rvEmpresas.setAdapter(mAdapterEmpresa);
                            if(listaSubcategorias.size() == 1){
                                llSubcategorias.setVisibility(View.GONE);
                            }
                        }

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
                Intent intentdes = new Intent(RestauranteActivity.this, ActivityDesconectado.class);
                startActivity(intentdes);
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);


        btnAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    

}
