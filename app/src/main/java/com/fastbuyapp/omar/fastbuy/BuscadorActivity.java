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
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.fastbuyapp.omar.fastbuy.Operaciones.Calcular_Minutos;
import com.fastbuyapp.omar.fastbuy.adaptadores.ProductoListAdapter;
import com.fastbuyapp.omar.fastbuy.config.GlideApp;
import com.fastbuyapp.omar.fastbuy.config.Servidor;
import com.fastbuyapp.omar.fastbuy.entidades.Categoria;
import com.fastbuyapp.omar.fastbuy.entidades.Empresa;
import com.fastbuyapp.omar.fastbuy.entidades.Producto;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class BuscadorActivity extends AppCompatActivity {
    SharedPreferences myPreferences;
    String tokencito, codigoZona_usuario, number;
    ProgressDialog progDailog = null;
    ViewGroup panelEmpresas;
    LinearLayoutManager layoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscador);

        SearchView txtBuscador = (SearchView) findViewById(R.id.txtBuscador);

        myPreferences =  PreferenceManager.getDefaultSharedPreferences(this);
        tokencito = myPreferences.getString("tokencito", "");
        number = myPreferences.getString("Number_Cliente", "");
        codigoZona_usuario = myPreferences.getString("codigoZona_usuario", "");
        String txtBuscado = txtBuscador.getQuery().toString();
        panelEmpresas = (ViewGroup) findViewById(R.id.panelEmpresas);
        txtBuscador.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                /*if (list.contains(query)) {
                    adapter.getFilter().filter(query);
                }
                    else {
                    // Search query not found in List View
                    Toast.makeText(MainActivity.this,
                                    "Not found",
                                    Toast.LENGTH_LONG)
                            .show();
                }*/
                consultar(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //adapter.getFilter().filter(newText);
                return false;
            }
        });

    }
    ArrayList<Empresa> listaEmpresa;
    void consultar(String texto){
        panelEmpresas.removeAllViews();
        progDailog = new ProgressDialog(this);
        progDailog.setMessage("Cargando...");
        progDailog.setIndeterminate(true);
        progDailog.setCancelable(false);
        progDailog.show();
        String consulta = "https://apifbdelivery.fastbuych.com/Delivery/BotonBusquedaGeneralApp?auth="+ tokencito+"&telefono="+number+"&zona="+codigoZona_usuario+"&filtro="+texto;
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, consulta, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.v("consulta",response);
                if (response.length()>0){
                    try {
                        JSONArray ja = new JSONArray(response);
                        listaEmpresa = new ArrayList<>();
                        for (int i = 0; i < ja.length(); i++) {
                            JSONObject objeto = ja.getJSONObject(i);
                            Empresa es = new Empresa();
                            es.setCodigo(objeto.getInt("codigo"));
                            LayoutInflater inflater = LayoutInflater.from(BuscadorActivity.this);
                            int id = R.layout.item_buscador_empresa;
                            LinearLayout linear = (LinearLayout) inflater.inflate(id, null, false);
                            TextView txtNombreEmpresa = (TextView) linear.findViewById(R.id.txtNombrePartner);
                            TextView txtValoracion = (TextView) linear.findViewById(R.id.txtValoracion);
                            TextView txtTiempo = (TextView) linear.findViewById(R.id.txtTiempo);
                            ImageView ivLogoPartner = (ImageView) linear.findViewById(R.id.ivLogoPartner);
                            String nombrecat =objeto.getString("nombre");
                            String valoracion =  objeto.getString("valoracion");
                            String tiempo = objeto.getString("tiempo_minimo") + "-" + objeto.getString("tiempo_maximo") + "'";
                            String logo = objeto.getString("logo");
                            String cobrataper = objeto.getString("cobrataper");
                            String costotaper = objeto.getString("costotaper");
                            txtNombreEmpresa.setText(nombrecat.substring(0,1).toUpperCase() + nombrecat.substring(1).toLowerCase());
                            txtValoracion.setText(valoracion);
                            txtTiempo.setText(tiempo);
                            Servidor s = new Servidor();
                            String url2 = "https://"+s.getServidor()+"/empresas/logos/" + logo;
                            GlideApp.with(BuscadorActivity.this)
                                    .load(url2)
                                    .centerCrop()
                                    .override(150, 150)
                                    .placeholder(R.drawable.loader_img)
                                    .transform(new CircleCrop())
                                    .into(ivLogoPartner);
                            ArrayList<Producto> listaProductos = new ArrayList<>();
                            JSONArray jaProductos = ja.getJSONObject(i).getJSONArray("productos");
                            for (int j = 0; j < jaProductos.length(); j++){
                                JSONObject objectProducto = jaProductos.getJSONObject(j);
                                Producto producto = new Producto();
                                producto.setCodigo(objectProducto.getInt("Codigo"));
                                producto.setDescripcion(objectProducto.getString("Descripcion"));
                                producto.setDescripcion2(objectProducto.getString("Descripcion2"));
                                //esto es en caso la empresa seleccionada cobre el taper
                                double Precio;
                                if (cobrataper.equals("SI"))
                                    Precio = (double) objectProducto.getDouble("Precio") + Double.parseDouble(costotaper);
                                else
                                    Precio = objectProducto.getDouble("Precio");

                                producto.setPrecio(String.format("%.2f",Precio).toString().replace(",","."));
                                producto.setImagen(objectProducto.getString("Imagen"));
                                producto.setEstado(objectProducto.getInt("Estado"));
                                producto.setFavorito(objectProducto.getString("favorito"));
                                Categoria categoria = new Categoria();
                                categoria.setDescripcion(objectProducto.getString("Categoria"));
                                producto.setCategoria(categoria);
                                Empresa empresa = new Empresa();
                                empresa.setCodigo(objectProducto.getInt("CodEmpresa"));
                                empresa.setNombreComercial(objectProducto.getString("NombreComercial"));
                                empresa.setLongitud(objectProducto.getDouble("Longitud"));
                                empresa.setLatitud(objectProducto.getDouble("Latitud"));
                                producto.setEmpresa(empresa);
                                Calcular_Minutos calcula = new Calcular_Minutos();
                                producto.setTiempo(calcula.ObtenMinutos(objectProducto.getString("TimePreparacion")));
                                listaProductos.add(producto);
                            }

                            es.setProductos(listaProductos);
                            listaEmpresa.add(es);
                            layoutManager = new LinearLayoutManager(BuscadorActivity.this, LinearLayoutManager.HORIZONTAL, false);
                            RecyclerView rvProductos = linear.findViewById(R.id.rvProductos);
                            rvProductos.setLayoutManager(layoutManager);
                            // specify an adapter (see also next example)
                            ProductoListAdapter mAdapterProducto = new ProductoListAdapter(listaEmpresa.get(i).getProductos(), BuscadorActivity.this);
                            rvProductos.setAdapter(mAdapterProducto);

                            panelEmpresas.addView(linear);
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
                Intent intentdes = new Intent(BuscadorActivity.this, ActivityDesconectado.class);
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
