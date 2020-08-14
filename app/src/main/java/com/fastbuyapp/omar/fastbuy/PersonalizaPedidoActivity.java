package com.fastbuyapp.omar.fastbuy;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fastbuyapp.omar.fastbuy.Validaciones.ValidacionDatos;
import com.fastbuyapp.omar.fastbuy.config.GlideApp;
import com.fastbuyapp.omar.fastbuy.config.Globales;
import com.fastbuyapp.omar.fastbuy.config.Servidor;
import com.fastbuyapp.omar.fastbuy.entidades.Empresa;
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
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
    SharedPreferences myPreferences;
    String presentacion = "1", taper_empresa, latitud_empresa, longitud_empresa;
    ArrayList<ProductoPresentacion> list;
    ProgressDialog progDailog = null;
    PresentacionProdListAdapter adapter = null;
    SharedPreferences.Editor myEditor;
    int pos; //para controlar la posicion del item
    int numero_empresas_encarrito, empresa_encarrito;
    ImageButton btnCarrito;

    @Override
    protected void onResume() {
        super.onResume();
        ValidacionDatos valida = new ValidacionDatos();
        valida.validarCarritoVacio(btnCarrito);
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
        myPreferences =  PreferenceManager.getDefaultSharedPreferences(this);
        myEditor = myPreferences.edit();
        String tokencito = myPreferences.getString("tokencito", "");
        taper_empresa = myPreferences.getString("taper_empresa", "NO");
        longitud_empresa = myPreferences.getString("longitud_empresa", "0");
        latitud_empresa = myPreferences.getString("latitud_empresa", "0");
        final String ubicacion = myPreferences.getString("ubicacion", "");
        String consulta = "https://apifbdelivery.fastbuych.com/Delivery/ListarPresentacionesXProductoXUbicacion?auth="+tokencito+"&codigo="+String.valueOf(Globales.getInstance().getProductoPersonalizar().getCodigo())+"&ubicacion="+String.valueOf(ubicacion);
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
                Globales.getInstance().getProductoPersonalizar().setPrecio(list.get(position).getPrecio());
                muestraPrecio(txtPrecioProducto);
                AñadeElementosGridView(list);
            }
        });

        //cargar la foto del producto seleccionado
        Servidor s = new Servidor();
        String urlImg = "https://"+s.getServidor()+"/productos/fotos/" + Globales.getInstance().getProductoPersonalizar().getImagen();
        GlideApp.with(PersonalizaPedidoActivity.this)
                .load(urlImg)
                .centerCrop()
                .transform(new RoundedCornersTransformation(20,0))
                .into(imgProducto);

        txtNameProducto.setText(Globales.getInstance().getProductoPersonalizar().getDescripcion());
        txtDescripcionProducto.setText(Globales.getInstance().getProductoPersonalizar().getDescripcion2());
        if (taper_empresa.equals("SI"))
            txtIncluyeTaper.setVisibility(View.VISIBLE);
        else
            txtIncluyeTaper.setVisibility(View.GONE);

        muestraPrecio(txtPrecioProducto);
        txtTiempoProducto.setText(" "+String.valueOf(Globales.getInstance().getProductoPersonalizar().getTiempo())+" min.");

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
                boolean x = true; //true = el producto se pued agregar al carrito / false = la empresa no se encuentra en el carrito y el producto no se puede agregar
                int codigoEmpresaTemp;
                int ubicaciontemp;
                //almacenamos el nombre de la empresa del producto seleccionado
                Globales globales = new Globales();
                ArrayList<PedidoDetalle> listapedidos = globales.getListaPedidosCache("lista_pedidos");
                codigoEmpresaTemp = Globales.getInstance().getProductoPersonalizar().getEmpresa().getCodigo();
                if(listapedidos.size() > 0) {
                    for (int i = 0; i < listapedidos.size(); i++){
                        //Toast.makeText(PersonalizaPedidoActivity.this, String.valueOf(codigoEmpresaTemp) + " - " + Globales.getInstance().getListaPedidos().get(i).getEmpresa(), Toast.LENGTH_SHORT).show();
                        if(listapedidos.get(i).getEmpresa() != codigoEmpresaTemp){
                            x = false;
                        }
                    }
                }
                if(x) {
                    String nameProd = Globales.getInstance().getProductoPersonalizar().getDescripcion();
                    Boolean name = comparaNombreProducto(nameProd, listapedidos);
                    float precio = Float.valueOf(Globales.getInstance().getProductoPersonalizar().getPrecio());
                    double total = 0;
                    if (name) {
                        int Cant2 = listapedidos.get(pos).getCantidad();
                        //canti = Cant2+Integer.valueOf(txtCanti.getText());
                        canti = canti + Cant2;
                        listapedidos.get(pos).setCantidad(canti);
                        total = canti * precio;
                        listapedidos.get(pos).setTotal(total);
                        globales.setList("lista_pedidos", listapedidos);
                    } else {
                        PedidoDetalle detallepedido = new PedidoDetalle();
                        Globales.getInstance().getProductoPersonalizar().setPresentacion(presentacion);
                        Producto pro = new Producto();
                        pro = Globales.getInstance().getProductoPersonalizar();
                        detallepedido.setProducto(pro);
                        detallepedido.setTiempo(pro.getTiempo());
                        detallepedido.setCantidad(canti);
                        detallepedido.setPreciounit(precio);
                        detallepedido.setEmpresa(pro.getEmpresa().getCodigo());
                        total = canti * precio;
                        detallepedido.setTotal(total);
                        String personalizando = txtPersonalizaProducto.getText().toString();

                        if (personalizando.isEmpty())
                            detallepedido.setPersonalizacion("Sin Personalización");
                        else
                            detallepedido.setPersonalizacion(personalizando);

                        detallepedido.setLatitud(Double.parseDouble(latitud_empresa));
                        detallepedido.setLongitud(Double.parseDouble(longitud_empresa));
                        detallepedido.setUbicacion(Integer.parseInt(ubicacion));
                        detallepedido.setEsPromocion(false);

                        globales.addListaPedidos("lista_pedidos", detallepedido);

                    }
                    canti = 1;
                    txtCanti.setText("0" + String.valueOf(canti));
                    //Intent intent = new Intent(PersonalizaPedidoActivity.this, CarritoActivity.class);
                    //startActivity(intent);

                    Toast toast = Toast.makeText(PersonalizaPedidoActivity.this, "Se Añadió al Carrito", Toast.LENGTH_SHORT);
                    View vistaToast = toast.getView();
                    vistaToast.setBackgroundResource(R.drawable.toast_success);
                    toast.show();
                    onBackPressed();
                } else{
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

    public boolean comparaNombreProducto(String nameProduct, ArrayList<PedidoDetalle> lista){
        boolean res = false;
        final int size = lista.size();
        for (int i = 0; i < size; i++)
        {
            if(lista.get(i).getProducto() != null){
                String nameProd2 = lista.get(i).getProducto().getDescripcion();
                if(nameProduct.equals(nameProd2)){
                    String presentacion2 = lista.get(i).getProducto().getPresentacion();
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
        final ProgressDialog progDailogCarga = new ProgressDialog(PersonalizaPedidoActivity.this);
        progDailogCarga.setMessage("Cargando...");
        progDailogCarga.setIndeterminate(true);
        progDailogCarga.setCancelable(false);
        progDailogCarga.show();

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
                            if (Globales.getInstance().getProductoPersonalizar().getPrecio().equals(objeto.getString("PU_Precioventa")))
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
                        progDailogCarga.dismiss();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        progDailogCarga.dismiss();
                    }
                    catch (IllegalArgumentException i){
                        i.printStackTrace();
                        progDailogCarga.dismiss();
                    }
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                progDailogCarga.dismiss();
                Intent intent = new Intent(PersonalizaPedidoActivity.this, ActivityDesconectado.class);
                startActivity(intent);
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
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
        txtP.setText("S/"+Globales.getInstance().getProductoPersonalizar().getPrecio());
    }
}
