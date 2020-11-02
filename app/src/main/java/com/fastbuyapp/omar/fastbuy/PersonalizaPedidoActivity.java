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
import com.fastbuyapp.omar.fastbuy.Interfaces.OnItemChange;
import com.fastbuyapp.omar.fastbuy.Interfaces.OnListItemClick;
import com.fastbuyapp.omar.fastbuy.Operaciones.Calcular_Total;
import com.fastbuyapp.omar.fastbuy.Validaciones.ValidacionDatos;
import com.fastbuyapp.omar.fastbuy.adaptadores.AgregadoListAdapter;
import com.fastbuyapp.omar.fastbuy.config.GlideApp;
import com.fastbuyapp.omar.fastbuy.config.Globales;
import com.fastbuyapp.omar.fastbuy.config.Servidor;
import com.fastbuyapp.omar.fastbuy.entidades.Empresa;
import com.fastbuyapp.omar.fastbuy.entidades.PedidoDetalle;
import com.fastbuyapp.omar.fastbuy.entidades.Producto;
import com.fastbuyapp.omar.fastbuy.entidades.ProductoAgregados;
import com.fastbuyapp.omar.fastbuy.entidades.ProductoPresentacion;
import com.google.android.gms.actions.ItemListIntents;

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
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class PersonalizaPedidoActivity extends AppCompatActivity {
    GridView gridView, tablaListIngredientes;
    private int canti = 1;
    int x;
    SharedPreferences myPreferences;
    String presentacion = "1", taper_empresa, latitud_empresa, longitud_empresa;
    ArrayList<ProductoPresentacion> list;
    ArrayList<ProductoAgregados> listAgregados, listAgregadosSeleccionados;
    ProgressDialog progDailog = null;
    PresentacionProdListAdapter adapter = null;
    SharedPreferences.Editor myEditor;
    int pos; //para controlar la posicion del item
    int numero_empresas_encarrito, empresa_encarrito;
    ImageButton btnCarrito;
    TextView txtPrecioProducto;
    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personaliza_pedido);

        TextView txtNameProducto = (TextView) findViewById(R.id.txtNameProd);
        final TextView txtDescripcionProducto = (TextView) findViewById(R.id.txtDescripcionProd);
        TextView txtTiempoProducto = (TextView) findViewById(R.id.txtTimeProd);
        txtPrecioProducto = (TextView) findViewById(R.id.txtPrecioProd);
        final ImageView imgProducto = (ImageView) findViewById(R.id.imgProduct);
        final EditText txtPersonalizaProducto = (EditText) findViewById(R.id.txtDescribeProducto);
        final LinearLayout linearPresentacion = (LinearLayout) findViewById(R.id.LinearPresentacion);
        TextView txtIncluyeTaper = findViewById(R.id.txtIncluyeTaper);
        gridView = (GridView) findViewById(R.id.tablaListPresentacionProd);
        tablaListIngredientes = (GridView) findViewById(R.id.tablaListIngredientes);
        myPreferences =  PreferenceManager.getDefaultSharedPreferences(this);
        myEditor = myPreferences.edit();
        String tokencito = myPreferences.getString("tokencito", "");
        taper_empresa = myPreferences.getString("taper_empresa", "NO");
        longitud_empresa = myPreferences.getString("longitud_empresa", "0");
        latitud_empresa = myPreferences.getString("latitud_empresa", "0");
        final String ubicacion = myPreferences.getString("ubicacion", "");
        String datos = "https://apifbdelivery.fastbuych.com/Delivery/MotrarProducto?auth="+tokencito+"&codigo="+ Globales.getInstance().getProductoPersonalizar().getCodigo() +"&ubicacion=" + ubicacion;
        //String consulta = "https://apifbdelivery.fastbuych.com/Delivery/ListarPresentacionesXProductoXUbicacion?auth="+tokencito+"&codigo="+String.valueOf(Globales.getInstance().getProductoPersonalizar().getCodigo())+"&ubicacion="+String.valueOf(ubicacion);
        EnviarRecibirDatos(datos, linearPresentacion);
        listAgregadosSeleccionados = new ArrayList<>();

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //hasta acá debe de listar las presentaciones ahora continua la logica para poder validar esa presentacion con el precio
                for (int i = 0; i< list.size(); i++){
                    list.get(i).setCheck(false);
                    //Log.v("item:"+String.valueOf(i),String.valueOf(list.get(i).isCheck()));
                }
                list.get(position).setCheck(true);
                presentacion = String.valueOf(list.get(position).getCodigo());
                Globales.getInstance().getProductoPersonalizar().setPrecio(list.get(position).getPrecio());
                //muestraPrecio(txtPrecioProducto);
                CalcularPago();
                AñadeElementosGridView(list);
            }
        });

        /*tablaListIngredientes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String precio = listAgregados.get(position).getPrecio();
                Toast.makeText(PersonalizaPedidoActivity.this, precio, Toast.LENGTH_SHORT).show();
            }
        });*/

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
        CalcularPago();
        //muestraPrecio(txtPrecioProducto);
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
                        double totalagregados = 0;
                        for (int i= 0; i < listAgregadosSeleccionados.size(); i++){
                            totalagregados +=  Double.parseDouble(listAgregadosSeleccionados.get(i).getPrecio());
                        }
                        total = (canti * precio) + totalagregados;
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
                        detallepedido.setAgregados(listAgregadosSeleccionados);
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
                        if (!response.equals("nulo")) {
                            JSONObject objectProducto = new JSONObject(response);
                            JSONArray jaPresentaciones = objectProducto.getJSONArray("presentaciones");
                            JSONArray jaAgregados = objectProducto.getJSONArray("agregados");
                            list = new ArrayList<>();
                            listAgregados = new ArrayList<>();
                            for (int i = 0; i < jaPresentaciones.length(); i++) {
                                JSONObject objeto = jaPresentaciones.getJSONObject(i);
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
                            for (int j = 0; j < jaAgregados.length(); j++){
                                JSONObject objAgregado = jaAgregados.getJSONObject(j);
                                ProductoAgregados agregado = new ProductoAgregados();
                                agregado.setCodigo(Integer.parseInt(objAgregado.getString("codigo")));
                                agregado.setNombre(objAgregado.getString("nombre"));
                                agregado.setPrecio(objAgregado.getString("precio"));
                                listAgregados.add(agregado);
                            }



                        /*if (list.size()>=3){
                            //Log.v("altura1",String.valueOf(list.size()));
                            x = (int) list.size()/3;
                            double y = (double) list.size()/3;
                            if (y > x)
                                x += 1;
                            cambiaAlturaGrid(x,110,gridView);
                            if(list.size()%2 == 0){
                                //gridView.setNumColumns(2);
                            }else{
                                //gridView.setNumColumns(3);//3
                            }
                            linear.setVisibility(View.VISIBLE);
                        }else*/
                            if (list.size() <= 1)
                                linear.setVisibility(View.GONE);
                            else {
                                //gridView.setNumColumns(list.size());
                                linear.setVisibility(View.VISIBLE);
                            }
                            AñadeElementosGridView(list);
                            ListarAgregados(listAgregados);
                            progDailogCarga.dismiss();
                        }
                    } catch(JSONException e){
                        e.printStackTrace();
                        progDailogCarga.dismiss();
                    }

                    catch(IllegalArgumentException i){
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
        gridView.setVerticalScrollBarEnabled(false);
        setGridViewHeightBasedOnChildren(gridView, 1);
    }

    public void ListarAgregados(ArrayList<ProductoAgregados> miLista){
        AgregadoListAdapter adapteragregados = new AgregadoListAdapter(PersonalizaPedidoActivity.this, R.layout.item_agregados, miLista);
        OnItemChange onItemChange = new OnItemChange() {
            @Override
            public void onChange(View view, int position, boolean isChecked) {
                if (view != null) {
                    int codigo = listAgregados.get(position).getCodigo();
                    String nombre = listAgregados.get(position).getNombre();
                    float precio = Float.parseFloat(listAgregados.get(position).getPrecio());
                    float precioActual = Float.parseFloat(txtPrecioProducto.getText().toString().replace("S/ ", ""));
                    float nuevoprecio = 0;
                    if(isChecked){
                        nuevoprecio = precioActual + precio;
                        addAgregado(codigo, precio, nombre);
                    }else{
                        nuevoprecio = precioActual - precio;
                        removeAgregado(codigo);
                    }
                    //Log.v("lista_agregados", new JSONArray(listAgregadosSeleccionados).toString());
                    //txtPrecioProducto.setText("S/ " + String.format("%.2f", nuevoprecio).replace(",", "."));
                    CalcularPago();
                }
            }
        };
        adapteragregados.setOnItemChange(onItemChange);
        tablaListIngredientes.setAdapter(adapteragregados);
        tablaListIngredientes.setVerticalScrollBarEnabled(false);
        setGridViewHeightBasedOnChildren(tablaListIngredientes, 1);
    }

    //public void muestraPrecio(TextView txtP){
    //    txtP.setText("S/ "+Globales.getInstance().getProductoPersonalizar().getPrecio());
    //}

    void CalcularPago(){
        double precioProducto = Double.parseDouble(Globales.getInstance().getProductoPersonalizar().getPrecio());
        if(listAgregadosSeleccionados.size() > 0){
            double item = 0;
            for (int i = 0; i < listAgregadosSeleccionados.size(); i++){
                item = item + Double.parseDouble(listAgregadosSeleccionados.get(i).getPrecio());
            }
            precioProducto = precioProducto + item;
        }
        txtPrecioProducto.setText("S/ "+ String.format("%.2f",precioProducto).replace(",","."));

    }

    public void setGridViewHeightBasedOnChildren(GridView gridView, int columns) {
        ListAdapter listAdapter = gridView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int items = listAdapter.getCount();
        int rows = 0;

        View listItem = listAdapter.getView(0, null, gridView);
        listItem.measure(0, 0);
        totalHeight = listItem.getMeasuredHeight();

        float x = 1;
        if( items > columns ){
            x = items/columns;
            rows = (int) (x + 1);
            totalHeight *= rows;
        }

        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        params.height = totalHeight;
        gridView.setLayoutParams(params);

    }

    void addAgregado(int codigo, float precio, String nombre){
        ProductoAgregados pa = new ProductoAgregados();
        pa.setCodigo(codigo);
        pa.setNombre(nombre);
        pa.setPrecio(String.valueOf(precio).replace(",", "."));
        listAgregadosSeleccionados.add(pa);
    }

    void  removeAgregado(int codigo ){
        for (int i = 0; i < listAgregadosSeleccionados.size(); i++){
            if(listAgregadosSeleccionados.get(i).getCodigo() == codigo){
                listAgregadosSeleccionados.remove(i);
            }
        }
    }
}
