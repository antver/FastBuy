package com.fastbuyapp.omar.fastbuy;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fastbuyapp.omar.fastbuy.config.GlideApp;
import com.fastbuyapp.omar.fastbuy.config.Globales;
import com.fastbuyapp.omar.fastbuy.config.Servidor;
import com.fastbuyapp.omar.fastbuy.entidades.PedidoDetalle;
import com.fastbuyapp.omar.fastbuy.entidades.Producto;
import com.fastbuyapp.omar.fastbuy.entidades.ProductoPresentacion;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class PersonalizaPedidoActivity extends AppCompatActivity {
    GridView gridView;
    private int canti = 1;
    int x;
    String presentacion = "1";
    ArrayList<ProductoPresentacion> list;
    ProgressDialog progDailog = null;
    PresentacionProdListAdapter adapter = null;

    int pos; //para controlar la posicion del item

    ImageButton btnCarrito;

    @Override
    protected void onResume() {
        super.onResume();
        Globales.valida.validarCarritoVacio(btnCarrito);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personaliza_pedido);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_chevron_left_black_24dp));

        TextView txtNameProducto = (TextView) findViewById(R.id.txtNameProd);
        final TextView txtDescripcionProducto = (TextView) findViewById(R.id.txtDescripcionProd);
        TextView txtTiempoProducto = (TextView) findViewById(R.id.txtTimeProd);
        final TextView txtPrecioProducto = (TextView) findViewById(R.id.txtPrecioProd);
        final ImageView imgProducto = (ImageView) findViewById(R.id.imgProduct);
        final EditText txtPersonalizaProducto = (EditText) findViewById(R.id.txtDescribeProducto);
        final LinearLayout linearPresentacion = (LinearLayout) findViewById(R.id.LinearPresentacion);
        TextView txtIncluyeTaper = findViewById(R.id.txtIncluyeTaper);
        gridView = (GridView) findViewById(R.id.tablaListPresentacionProd);
        SharedPreferences myPreferences;
        myPreferences =  PreferenceManager.getDefaultSharedPreferences(this);
        String tokencito = myPreferences.getString("tokencito", "");
        final String ubicacion = myPreferences.getString("ubicacion", "");
        String consulta = "https://apifbdelivery.fastbuych.com/Delivery/ListarPresentacionesXProductoXUbicacion?auth="+tokencito+"&codigo="+String.valueOf(Globales.productoPersonalizar.getCodigo())+"&ubicacion="+String.valueOf(ubicacion);
        EnviarRecibirDatos(consulta, linearPresentacion);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //hasta acá debe de listar las presentaciones ahora continua la logica para poder validar esa presentacion con el precio
                for (int i = 0; i< list.size(); i++){
                    list.get(i).setCheck(false);
                    Log.v("item:"+String.valueOf(i),String.valueOf(list.get(i).isCheck()));
                }
                list.get(position).setCheck(true);
                presentacion = String.valueOf(list.get(position).getCodigo());
                Globales.productoPersonalizar.setPrecio(list.get(position).getPrecio());
                muestraPrecio(txtPrecioProducto);
                AñadeElementosGridView(list);
            }
        });

        //cargar la foto del producto seleccionado
        Servidor s = new Servidor();
        String urlImg = "https://"+s.getServidor()+"/productos/fotos/" + Globales.productoPersonalizar.getImagen();
        GlideApp.with(PersonalizaPedidoActivity.this)
                .load(urlImg)
                .centerCrop()
                .transform(new RoundedCornersTransformation(20,0))
                .into(imgProducto);

        txtNameProducto.setText(Globales.productoPersonalizar.getDescripcion());
        txtDescripcionProducto.setText(Globales.productoPersonalizar.getDescripcion2());
        if (Globales.taperEmpresaSel.equals("SI"))
            txtIncluyeTaper.setVisibility(View.VISIBLE);
        else
            txtIncluyeTaper.setVisibility(View.GONE);

        muestraPrecio(txtPrecioProducto);
        txtTiempoProducto.setText(" "+String.valueOf(Globales.productoPersonalizar.getTiempo())+" min.");

        //Botones de incrementar y disminuir
        ImageButton btnAddCant = (ImageButton) findViewById(R.id.btnAumentarCant);
        ImageButton btnRemoveCant = (ImageButton) findViewById(R.id.btnReducirCant);
        final TextView txtCanti = (TextView) findViewById(R.id.txtCantidadProd);

        btnAddCant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canti+=1;
                if (canti < 10)
                    txtCanti.setText("0"+String.valueOf(canti));
                else
                    txtCanti.setText(String.valueOf(canti));
            }
        });
        btnRemoveCant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (canti > 1)
                    canti-=1;
                else
                    canti = 1;

                if (canti < 10)
                    txtCanti.setText("0"+String.valueOf(canti));
                else
                    txtCanti.setText(String.valueOf(canti));
            }
        });

        //Boton Añadir al Carrito
        Button btnAgregaralCar = (Button) findViewById(R.id.btnAgregarAlCarrito);

        btnAgregaralCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean x = false; //esta variable permitira controlar el añadir el producto a la lista
                String nameTemp;
                //almacenamos el nombre de la empresa del producto seleccionado
                nameTemp = Globales.productoPersonalizar.getEmpresa().getNombreComercial().toString();

                if (Globales.numEstablecimientos == 0){
                    Globales.establecimiento1 = nameTemp;
                    Globales.numEstablecimientos = 1;
                    Globales.codEstablecimiento1 = Globales.productoPersonalizar.getEmpresa().getCodigo();
                   // Globales.ubicaEstablecimiento1 = Globales.ubicacion;
                    x = true;
                }else{
                    if(Globales.establecimiento1.equals(nameTemp))
                        x = true;
                    else
                        x = false;
                }

                if(x){

                    Globales.promo = false;
                    String nameProd = Globales.productoPersonalizar.getDescripcion();
                    Boolean name = comparaNombreProducto(nameProd);
                    float precio = Float.valueOf(Globales.productoPersonalizar.getPrecio());
                    double total = 0;

                    if (name){
                        int Cant2 = Globales.listaPedidos.get(pos).getCantidad();
                        //canti = Cant2+Integer.valueOf(txtCanti.getText());
                        canti = canti+Cant2;
                        Globales.listaPedidos.get(pos).setCantidad(canti);
                        total = canti*precio;
                        Globales.listaPedidos.get(pos).setTotal(total);
                    }else{
                        PedidoDetalle detallepedido = new PedidoDetalle();
                        Globales.productoPersonalizar.setPresentacion(presentacion);
                        Producto pro = new Producto();
                        pro = Globales.productoPersonalizar;
                        detallepedido.setProducto(pro);
                        detallepedido.setTiempo(pro.getTiempo());
                        detallepedido.setCantidad(canti);
                        detallepedido.setPreciounit(precio);
                        total = canti*precio;
                        detallepedido.setTotal(total);
                        String personalizando = txtPersonalizaProducto.getText().toString();

                        if(personalizando.isEmpty())
                            detallepedido.setPersonalizacion("Sin Personalización");
                        else
                            detallepedido.setPersonalizacion(personalizando);

                        detallepedido.setLatitud(Globales.LatitudEmpresaSeleccionada);
                        detallepedido.setLongitud(Globales.LongitudEmpresaSeleccionada);
                        detallepedido.setUbicacion(Integer.parseInt(ubicacion));
                        detallepedido.setEsPromocion(Globales.promo);
                        Globales.listaPedidos.add(detallepedido);

                    }
                    canti = 1;
                    txtCanti.setText("0"+String.valueOf(canti));
                    //Intent intent = new Intent(PersonalizaPedidoActivity.this, CarritoActivity.class);
                    //startActivity(intent);

                    Toast toast = Toast.makeText(PersonalizaPedidoActivity.this, "Se Añadió al Carrito",Toast.LENGTH_SHORT);
                    View vistaToast = toast.getView();
                    vistaToast.setBackgroundResource(R.drawable.toast_success);
                    toast.show();
                    onBackPressed();
                }else{
                    Toast toast = Toast.makeText(PersonalizaPedidoActivity.this, "No puede añadir productos de establecimientos diferentes",Toast.LENGTH_SHORT);
                    View vistaToast = toast.getView();
                    vistaToast.setBackgroundResource(R.drawable.toast_yellow);
                    toast.show();
                    onBackPressed();
                }
            }
        });

        //controlando scrolling
        //ScrollView miScrollView = (ScrollView) findViewById(R.id.scrollGeneral);
        /*miScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                findViewById(R.id.txtDescribeProducto).getParent()
                        .requestDisallowInterceptTouchEvent(false);
                return false;
            }
        });

        txtPersonalizaProducto.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });*/

        //Menú principal
        ImageButton btnHome = (ImageButton) findViewById(R.id.btnHome);
        ImageButton btnFavoritos = (ImageButton) findViewById(R.id.btnFavoritos);
        btnCarrito = (ImageButton) findViewById(R.id.btnCarrito);
        ImageButton btnUsuario = (ImageButton) findViewById(R.id.btnUsuario);
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PersonalizaPedidoActivity.this, PrincipalActivity.class);
                startActivity(intent);
            }
        });

        btnFavoritos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PersonalizaPedidoActivity.this, FavoritosActivity.class);
                startActivity(intent);
            }
        });

        btnCarrito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PersonalizaPedidoActivity.this, CarritoActivity.class);
                startActivity(intent);
            }
        });

        btnUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PersonalizaPedidoActivity.this, UserActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menupersonalizapedido, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return false;
    }

    public boolean comparaNombreProducto(String nameProduct){
        boolean res = false;
        final int size = Globales.listaPedidos.size();
        for (int i = 0; i < size; i++)
        {
            if(Globales.listaPedidos.get(i).getProducto() != null){
                String nameProd2 = Globales.listaPedidos.get(i).getProducto().getDescripcion();
                if(nameProduct.equals(nameProd2)){
                    String presentacion2 = Globales.listaPedidos.get(i).getProducto().getPresentacion();
                    if (presentacion2.equals(presentacion)){
                        pos = i;
                        res = true;
                    }
                    else {
                        res = false;
                    }
                }
            }
        }
        return res;
    }

    public void EnviarRecibirDatos(String URL,final LinearLayout linear){
        progDailog = new ProgressDialog(PersonalizaPedidoActivity.this);
        progDailog.setMessage("Cargando...");
        progDailog.setIndeterminate(true);
        progDailog.setCancelable(false);
        progDailog.show();

        RequestQueue queue = Volley.newRequestQueue(PersonalizaPedidoActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener < String > () {
            @Override
            public void onResponse(String response) {
                if (response.length()>0){
                    try {
                        JSONArray ja = new JSONArray(response);
                        list = new ArrayList<>();
                        for (int i = 0; i < ja.length(); i++) {
                            JSONObject objeto = ja.getJSONObject(i);
                            ProductoPresentacion prodPresentacion = new ProductoPresentacion();
                            prodPresentacion.setCodigo(objeto.getInt("PRE_Codigo"));
                            prodPresentacion.setDescripcion(objeto.getString("PRE_Descripcion"));
                            prodPresentacion.setEstado(objeto.getInt("PU_Estado"));
                            prodPresentacion.setPrecio(objeto.getString("PU_Precioventa"));
                            /*if (objeto.getInt("PRE_Codigo") == 1) // por default estrá seleccionado Unidad
                                prodPresentacion.setCheck(true);
                            else
                                prodPresentacion.setCheck(false);*/
                            if (Globales.productoPersonalizar.getPrecio().equals(objeto.getString("PU_Precioventa")))
                                prodPresentacion.setCheck(true);
                            else
                                prodPresentacion.setCheck(false);
                            list.add(prodPresentacion);
                        }

                        if (list.size()>=3){
                            //Log.v("altura1",String.valueOf(list.size()));
                            x = (int) list.size()/3;
                            double y = (double) list.size()/3;
                            if (y > x)
                                x += 1;
                            cambiaAlturaGrid(x,110,gridView);
                            if(list.size()%2 == 0){
                                gridView.setNumColumns(2);
                            }else{
                                gridView.setNumColumns(3);//3
                            }
                            linear.setVisibility(View.VISIBLE);
                        }else if (list.size()<=1)
                                linear.setVisibility(View.GONE);
                        else{
                            /*cambiaAlturaGrid(1,50,gridView);*/
                            gridView.setNumColumns(list.size());
                            linear.setVisibility(View.VISIBLE);
                        }
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

    public void cambiaAlturaGrid(int fila, int alto, GridView g){
        Log.v("altura",String.valueOf(fila));
        int altura = (int) fila*alto;
        ViewGroup.LayoutParams params = g.getLayoutParams();
        params.height = altura;
        g.setLayoutParams(params);
    }

    public void AñadeElementosGridView(ArrayList<ProductoPresentacion> miLista){
        adapter = new PresentacionProdListAdapter(PersonalizaPedidoActivity.this, R.layout.presentacion_list_item, miLista);
        gridView.setAdapter(adapter);
    }

    public void muestraPrecio(TextView txtP){
        txtP.setText("S/"+Globales.productoPersonalizar.getPrecio());
    }
}
