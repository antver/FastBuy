package com.fastbuyapp.omar.fastbuy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import androidx.appcompat.app.AppCompatActivity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.fastbuyapp.omar.fastbuy.Operaciones.Calcular_Minutos;
import com.fastbuyapp.omar.fastbuy.config.GlideApp;
import com.fastbuyapp.omar.fastbuy.config.Globales;
import com.fastbuyapp.omar.fastbuy.config.Servidor;
import com.fastbuyapp.omar.fastbuy.entidades.Categoria;
import com.fastbuyapp.omar.fastbuy.entidades.Empresa;
import com.fastbuyapp.omar.fastbuy.entidades.Producto;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import static android.os.Build.VERSION.SDK_INT;

public class ProductosActivity extends AppCompatActivity {
    GridView gridView;
    public String codigo;
    public String nombreComercial;
    public String categoria;
    ArrayList<Producto> list;
    ProductosListAdapter adapter = null;
    SharedPreferences myPreferences;
    ImageButton btnCarrito;
    LottieAnimationView animacion1, animacion2;
    String name, number, tokencito, ubicacion, empresaseleccionada;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productos);

        myPreferences =  PreferenceManager.getDefaultSharedPreferences(this);
         name = myPreferences.getString("Name_Cliente", "");
        number = myPreferences.getString("Number_Cliente", "");
        tokencito = myPreferences.getString("tokencito", "");
        categoria = myPreferences.getString("categoria", "");
        ubicacion = myPreferences.getString("ubicacion", "");
        empresaseleccionada = myPreferences.getString("empresaseleccionada", "");
        //añadiendo a favoritos
        ImageButton btnAddFav = (ImageButton) findViewById(R.id.btnAddFav);
        btnAddFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddFavoritos(empresaseleccionada,number,ubicacion);
            }
        });

        ImageButton btnCartita = (ImageButton) findViewById(R.id.btnCarta);
        ImageButton btnListProd = (ImageButton) findViewById(R.id.btnCarta2);
        ///animacion1 = findViewById(R.id.animacionBtnCarta);


        if (categoria.equals("1")){
            btnCartita.setVisibility(View.VISIBLE);
            btnListProd.setVisibility(View.GONE);
        }else{
            btnCartita.setVisibility(View.GONE);
            btnListProd.setVisibility(View.VISIBLE);
        }

        String fuente = "fonts/Riffic.ttf";
        Typeface typefaceRiffic = Typeface.createFromAsset(getAssets(), fuente);
        ImageView imgPortada = (ImageView) findViewById(R.id.imageViewPortada);
        final ImageView imgLogo = (ImageView) findViewById(R.id.imgPerfil);

        String nombreImagenPortada;
        Servidor s = new Servidor();
        if (Globales.imagenFondoEmpresa == "null"){
            nombreImagenPortada = "default.jpg";

        }else {
            nombreImagenPortada = Globales.imagenFondoEmpresa;
        }
        String url = "https://"+s.getServidor()+"/empresas/portadas/" + nombreImagenPortada;
        //String url = "http://"+s.getServidor()+"/empresas/subcategorias/imagenes/" + Globales.imagenSubcategoria;
        String url2 = "https://"+s.getServidor()+"/empresas/logos/" + Globales.imagenEmpresa;

        GlideApp.with(ProductosActivity.this)
                .load(url)
                .centerCrop()
                .override(350, 200)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Log.v("img_promo_carga", "Error loading image", e);
                        return false; // important to return false so the error placeholder can be placed
                    }
                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(imgPortada);

        GlideApp.with(ProductosActivity.this)
                .load(url2)
                .centerCrop()
                .override(100, 100)
                .transform(new CircleCrop())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        // log exception
                        Log.v("img_promo_carga", "Error loading image", e);
                        return false; // important to return false so the error placeholder can be placed
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(imgLogo);

        //para listar todos los productos
        Globales.catProductoSeleccionado = 0;
        listar();

        btnCartita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductosActivity.this, CartaActivity.class);
                startActivity(intent);
            }
        });
        /*animacion1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductosActivity.this, CartaActivity.class);
                startActivity(intent);
            }
        });*/

        btnListProd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductosActivity.this, CartaActivity.class);
                startActivity(intent);
            }
        });

        //botones del menu
        ImageButton btnHome = (ImageButton) findViewById(R.id.btnHome);
        ImageButton btnFavoritos = (ImageButton) findViewById(R.id.btnFavoritos);
        btnCarrito = (ImageButton) findViewById(R.id.btnCarrito);
        ImageButton btnUsuario = (ImageButton) findViewById(R.id.btnUsuario);
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductosActivity.this, PrincipalActivity.class);
                startActivity(intent);
            }
        });

        btnFavoritos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductosActivity.this, FavoritosActivity.class);
                startActivity(intent);
            }
        });

        btnCarrito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductosActivity.this, CarritoActivity.class);
                startActivity(intent);
            }
        });

        btnUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductosActivity.this, UserActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        Globales.valida.validarCarritoVacio(btnCarrito);
        listar();
    }

    /*@Override
    public void onBackPressed (){
        Intent intent = new Intent(ProductosActivity.this, EstablecimientoActivity.class);
        startActivity(intent);
    }*/

    public void listar(){
        codigo = empresaseleccionada;
        categoria = String.valueOf(Globales.catProductoSeleccionado);
        try {
            listarProductos(codigo,"",categoria);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void listarProductos(String empresa, String filtro, String categoria) throws UnsupportedEncodingException {
        String c = URLEncoder.encode(filtro, "UTF-8");
        //Log.v("empresa_categoria_ubicacion",empresa +"_"+categoria+"_"+ String.valueOf(Globales.ubicacion));
        String consulta = "https://apifbdelivery.fastbuych.com/Delivery/ListarProductosXEmpresaXUbicaXCategoriaXFiltro?auth="+tokencito+"&empresa="+empresa+"&ubica="+ubicacion+"&catego="+categoria+"&descrip="+filtro;
        EnviarRecibirDatos(consulta);
    }

    public void EnviarRecibirDatos(String URL){

        RequestQueue queue = Volley.newRequestQueue(ProductosActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.length()>0){
                    Log.v("response_Productos",response.toString());
                    try {
                        JSONArray ja = new JSONArray(response);
                        CargarLista(ja);
                        //Toast.makeText(getApplicationContext(),"a: ", Toast.LENGTH_LONG);
                    } catch (JSONException e) {
                        Toast toast = Toast.makeText(ProductosActivity.this, "Aún no Existen Productos en este Establecimiento...", Toast.LENGTH_SHORT);
                        View vistaToast = toast.getView();
                        vistaToast.setBackgroundResource(R.drawable.toast_yellow);
                        toast.show();
                        e.printStackTrace();
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

    public void CargarLista(JSONArray ja) throws JSONException {
        list = new ArrayList<>();
        for(int i = 0; i < ja.length(); i++) {
            JSONObject objeto = ja.getJSONObject(i);
            Producto producto = new Producto();
            producto.setCodigo(objeto.getInt("Codigo"));
            producto.setDescripcion(objeto.getString("Descripcion"));
            producto.setDescripcion2(objeto.getString("Descripcion2"));
            //esto es en caso la empresa seleccionada cobre el taper
            double Precio;
            if (Globales.taperEmpresaSel.equals("SI"))
                 Precio = (double) objeto.getDouble("Precio")+Globales.costoTaperEmpresaSel;
            else
                Precio = objeto.getDouble("Precio");

            producto.setPrecio(String.format("%.2f",Precio).toString().replace(",","."));
            producto.setImagen(objeto.getString("Imagen"));
            producto.setEstado(objeto.getInt("Estado"));
            Categoria categoria = new Categoria();
            categoria.setDescripcion(objeto.getString("Categoria"));
            producto.setCategoria(categoria);
            Empresa empresa = new Empresa();
            empresa.setCodigo(objeto.getInt("CodEmpresa"));
            empresa.setNombreComercial(objeto.getString("NombreComercial"));
            empresa.setLongitud(objeto.getDouble("Longitud"));
            empresa.setLatitud(objeto.getDouble("Latitud"));
            producto.setEmpresa(empresa);
            Calcular_Minutos calcula = new Calcular_Minutos();
            producto.setTiempo(calcula.ObtenMinutos(objeto.getString("TimePreparacion")));
            list.add(producto);
        }
        try {
            gridView = (GridView) findViewById(R.id.tablaListaProductos);
            adapter = new ProductosListAdapter(ProductosActivity.this, R.layout.list_productos_item, list);
            gridView.setAdapter(adapter);

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //Toast.makeText(CartaActivity.this, "Hola Producto", Toast.LENGTH_SHORT).show();
                   if(!Globales.tiendaCerrada){
                       int codigo =(list.get(position).getCodigo());
                       Producto prod = new Producto();
                       prod.setCodigo(codigo);
                       prod.setDescripcion(list.get(position).getDescripcion());
                       prod.setDescripcion2(list.get(position).getDescripcion2());
                       prod.setPrecio(list.get(position).getPrecio());
                       prod.setImagen(list.get(position).getImagen());
                       prod.setEstado(list.get(position).getEstado());

                       Categoria categoria = new Categoria();
                       categoria.setDescripcion(list.get(position).getCategoria().getDescripcion());
                       prod.setCategoria(categoria);

                       Empresa empresa = new Empresa();
                       empresa.setCodigo(list.get(position).getEmpresa().getCodigo());
                       empresa.setNombreComercial(list.get(position).getEmpresa().getNombreComercial());
                       empresa.setLongitud(list.get(position).getEmpresa().getLongitud());
                       empresa.setLatitud(list.get(position).getEmpresa().getLatitud());
                       prod.setEmpresa(empresa);
                       prod.setTiempo(list.get(position).getTiempo());
                       Globales.productoPersonalizar = prod;

                       Intent intent = new Intent(ProductosActivity.this, PersonalizaPedidoActivity.class);
                       startActivity(intent);
                   }else {
                       Toast toast = Toast.makeText(ProductosActivity.this, "El establecimiento seleccionado se encuentra CERRADO...", Toast.LENGTH_LONG);
                       View vistaToast = toast.getView();
                       vistaToast.setBackgroundResource(R.drawable.toast_yellow);
                       toast.show();
                   }
                }
            });
        }
        catch (Exception ex){

        }
    }

    public void AddFavoritos(String codEmpresa, String telefono, String codUbicacion){
        String miURL = "https://apifbdelivery.fastbuych.com/Delivery/RegistrarFavorito?auth="+tokencito+"&empresa="+codEmpresa+"&telefono="+telefono+"&ubicacion="+codUbicacion;
        RequestQueue queue = Volley.newRequestQueue(ProductosActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, miURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast toast = Toast.makeText(ProductosActivity.this, "Empresa Añadida a Favoritos...", Toast.LENGTH_SHORT);
                View vistaToast = toast.getView();
                vistaToast.setBackgroundResource(R.drawable.toast_success);
                toast.show();
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast toast = Toast.makeText(ProductosActivity.this, "La Empresa no pudo ser Añadida a Favoritos...", Toast.LENGTH_SHORT);
                View vistaToast = toast.getView();
                vistaToast.setBackgroundResource(R.drawable.toast_success);
                toast.show();
            }
        });

        queue.add(stringRequest);
    }
}
