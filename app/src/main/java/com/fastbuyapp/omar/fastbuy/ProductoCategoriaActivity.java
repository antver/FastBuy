package com.fastbuyapp.omar.fastbuy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.fastbuyapp.omar.fastbuy.Operaciones.Calcular_Minutos;
import com.fastbuyapp.omar.fastbuy.adaptadores.EmpresaListAdapterRV;
import com.fastbuyapp.omar.fastbuy.adaptadores.ProductoListAdapter;
import com.fastbuyapp.omar.fastbuy.config.GlideApp;
import com.fastbuyapp.omar.fastbuy.config.Globales;
import com.fastbuyapp.omar.fastbuy.config.Servidor;
import com.fastbuyapp.omar.fastbuy.entidades.Categoria;
import com.fastbuyapp.omar.fastbuy.entidades.Empresa;
import com.fastbuyapp.omar.fastbuy.entidades.PedidoDetalle;
import com.fastbuyapp.omar.fastbuy.entidades.Producto;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

public class ProductoCategoriaActivity extends AppCompatActivity {
    public String codigo_empresa;
    public String nombreComercial;
    public String categoria, valoracion, tiempo;
    ArrayList<Producto> list;
    ProductosListAdapter adapter = null;
    SharedPreferences myPreferences;
    SharedPreferences.Editor myEditor;
    String name, number, tokencito, ubicacion, portada, logo, costotaper, cobrataper;
    int categoria_producto;
    ProgressDialog progDailog;
    boolean tiendaCerrada, producto_recarga;
    ArrayList<Categoria> listaCategorias;
    ViewGroup panelCategorias;
    LinearLayoutManager layoutManager;
    TextView txtCantidadItems, txtTotalCarrito, btnIrCarrito;


    @Override
    protected void onResume() {
        super.onResume();
        txtCantidadItems = (TextView) findViewById(R.id.txtCantidadItems);
        txtTotalCarrito = (TextView) findViewById(R.id.txtTotalCarrito);
        Globales globales = new Globales();
        ArrayList<PedidoDetalle> listapedidos = globales.getListaPedidosCache("lista_pedidos");
        if (listapedidos.isEmpty()) {
            txtCantidadItems.setText("0");
            txtTotalCarrito.setText("S/ 0.00");
        }else{
            txtCantidadItems.setText(String.valueOf(listapedidos.size()));
            double suma = 0;
            for (int i = 0; i < listapedidos.size(); i++){
                suma = listapedidos.get(i).getTotal();
            }
            txtTotalCarrito.setText("S/ " + String.format("%.2f", suma).toString().replace(",","."));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_producto_categoria);

        myPreferences =  PreferenceManager.getDefaultSharedPreferences(this);
        myEditor = myPreferences.edit();
        name = myPreferences.getString("Name_Cliente", "");
        number = myPreferences.getString("Number_Cliente", "");
        tokencito = myPreferences.getString("tokencito", "");
        categoria = myPreferences.getString("categoria", "");
        ubicacion = myPreferences.getString("ubicacion", "");
        codigo_empresa = myPreferences.getString("codigo_empresa", "");
        nombreComercial = myPreferences.getString("nombre_empresa", "");
        logo = myPreferences.getString("logo_empresa", "");
        portada = myPreferences.getString("portada_empresa", "");
        cobrataper = myPreferences.getString("taper_empresa", "NO");
        costotaper = myPreferences.getString("costo_taper", "");
        tiendaCerrada = myPreferences.getBoolean("tiendaCerrada", false);
        valoracion = myPreferences.getString("valoracion_empresa", "");
        tiempo = myPreferences.getString("tiempo_empresa", "");

        ImageView imgPortada = (ImageView) findViewById(R.id.imageViewPortada);
        TextView txtNombreEmpresa = (TextView) findViewById(R.id.txtNombreEmpresa);
        TextView txtValoracion = (TextView) findViewById(R.id.txtValoracion);
        TextView txtTiempo = (TextView) findViewById(R.id.txtTiempo);
        txtCantidadItems = (TextView) findViewById(R.id.txtCantidadItems);
        txtTotalCarrito = (TextView) findViewById(R.id.txtTotalCarrito);
        btnIrCarrito = (TextView) findViewById(R.id.btnIrCarrito);
        panelCategorias = (ViewGroup) findViewById(R.id.panelCategorias);

        txtNombreEmpresa.setText(nombreComercial);
        txtValoracion.setText(valoracion);
        txtTiempo.setText(tiempo);

        String nombreImagenPortada;
        Servidor s = new Servidor();
        if (portada.equals("")){
            nombreImagenPortada = "default.jpg";

        }else {
            nombreImagenPortada = portada;
        }
        String url = "https://"+s.getServidor()+"/empresas/portadas/" + nombreImagenPortada;
        String url2 = "https://"+s.getServidor()+"/empresas/logos/" + logo;

        GlideApp.with(ProductoCategoriaActivity.this)
                .load(url)
                .centerCrop()
                .override(350, 200)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false; // important to return false so the error placeholder can be placed
                    }
                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(imgPortada);


        progDailog = new ProgressDialog(ProductoCategoriaActivity.this);
        progDailog.setMessage("Cargando...");
        progDailog.setIndeterminate(true);
        progDailog.setCancelable(false);
        progDailog.show();
        String consulta = "https://apifbdelivery.fastbuych.com/Delivery/ListaProductosxPartner?auth="+ tokencito+"&telefono="+number+"&empresa="+codigo_empresa+"&ubica="+ubicacion+"&filtro=";
        RequestQueue queue = Volley.newRequestQueue(ProductoCategoriaActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, consulta, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if (response.length()>0){
                    try {
                        JSONArray ja = new JSONArray(response);
                        listaCategorias = new ArrayList<>();
                        for (int i = 0; i < ja.length(); i++) {
                            JSONObject objeto = ja.getJSONObject(i);
                            Categoria es = new Categoria();
                            es.setCodigo(objeto.getInt("codigo"));
                            LayoutInflater inflater = LayoutInflater.from(ProductoCategoriaActivity.this);
                            int id = R.layout.item_producto_rv;
                            LinearLayout linear = (LinearLayout) inflater.inflate(id, null, false);
                            TextView txtNombreCategoria = (TextView) linear.findViewById(R.id.txtNombreCategoria);
                            String nombrecat =objeto.getString("descripcion");
                            txtNombreCategoria.setText(nombrecat.substring(0,1).toUpperCase() + nombrecat.substring(1).toLowerCase());

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

                            es.setListaproductos(listaProductos);
                            listaCategorias.add(es);
                            layoutManager = new LinearLayoutManager(ProductoCategoriaActivity.this, LinearLayoutManager.HORIZONTAL, false);
                            RecyclerView rvProductos = linear.findViewById(R.id.rvProductos);
                            rvProductos.setLayoutManager(layoutManager);
                            // specify an adapter (see also next example)
                            ProductoListAdapter mAdapterProducto = new ProductoListAdapter(listaCategorias.get(i).getListaproductos(), ProductoCategoriaActivity.this);
                            rvProductos.setAdapter(mAdapterProducto);

                            panelCategorias.addView(linear);
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
                Intent intentdes = new Intent(ProductoCategoriaActivity.this, ActivityDesconectado.class);
                startActivity(intentdes);
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);

        btnIrCarrito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 Intent intentCarrito = new Intent(ProductoCategoriaActivity.this, CarritoActivity.class);
                 startActivity(intentCarrito);
            }
        });
    }
    @SuppressLint("InlineApi")
    public void addChild(){

    }
}
