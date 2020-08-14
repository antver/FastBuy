package com.fastbuyapp.omar.fastbuy.config;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;

import com.fastbuyapp.omar.fastbuy.Validaciones.ValidacionDatos;
import com.fastbuyapp.omar.fastbuy.entidades.DistanciaPedidoDetalle;
import com.fastbuyapp.omar.fastbuy.entidades.Empresa;
import com.fastbuyapp.omar.fastbuy.entidades.Establecimiento;
import com.fastbuyapp.omar.fastbuy.entidades.PedidoDetalle;
import com.fastbuyapp.omar.fastbuy.entidades.Producto;
import com.fastbuyapp.omar.fastbuy.entidades.Promocion;
import com.fastbuyapp.omar.fastbuy.entidades.Ubicacion;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by OMAR on 05/09/2018.
 */

public class Globales {
    private static Globales instance;
    //private ArrayList<PedidoDetalle> listaPedidos = new ArrayList<PedidoDetalle>(); //lista global de los pedidos
    //public static ArrayList<PedidoDetalle> listaPedidos = new ArrayList<PedidoDetalle>(); //lista global de los pedidos
    //public static ArrayList<Ubicacion> listCiudades = new ArrayList<Ubicacion>(); //lista global de las ciudades
    private ArrayList<DistanciaPedidoDetalle> listaDistancias = new ArrayList<DistanciaPedidoDetalle>(); //lista de distancias
    private ArrayList<Empresa> listaEmpresasPedido= new ArrayList<Empresa>();//lista para empresas de donde se hagan pedidos
    private Producto productoPersonalizar = new Producto();

    public Globales(){

    }

    //public ArrayList<PedidoDetalle> getListaPedidos() {
        //return listaPedidos;
    //}
    public ArrayList<Empresa> getListaEmpresasPedido() {
        return listaEmpresasPedido;
    }
    public ArrayList<DistanciaPedidoDetalle> getListaDistancias() {
        return listaDistancias;
    }

    //public void agregarPedido(PedidoDetalle detalle) {
        //this.listaPedidos.add(detalle);
    //}
    //public void agregarEmpresa(Empresa empresa) {
        //this.listaEmpresasPedido.add(empresa);
    //}

    public void agregarDistancia(DistanciaPedidoDetalle distancia) {
        this.listaDistancias.add(distancia);
    }

    //public  void addListaPedidos(PedidoDetalle detalle){
        //this.getListaPedidos().add(detalle);
    //}

    //public void setListaPedidos(ArrayList<PedidoDetalle> listaPedidos){
        //this.listaPedidos = listaPedidos;
    //}

    public void setListaDistancias(ArrayList<DistanciaPedidoDetalle> listaDistancias){
        this.listaDistancias = listaDistancias;
    }

    public  Producto getProductoPersonalizar() {
        return this.productoPersonalizar;
    }

    public void setProductoPersonalizar(Producto productoPersonalizar) {
        this.productoPersonalizar = productoPersonalizar;
    }

    public static synchronized Globales getInstance(){
        if (instance == null)
            instance = new Globales();
        return instance;
    }

    //Mi Token de ACCESO
    //public static String tokencito = "Xid20200110e34CorpFastBuySAC2020comfastbuyusuario";

    //para controlar el ingreso al activity favoritos
    //public static boolean isFavoritos = false;

    //para poder controlar la vista de direcciones
    //public static boolean addNewDirec = false;

    //para eliminar del carrito de compras
    //public static int codiProdCar = -1;


    public static Intent myService;

    //para validar si se cerro session
    //public static boolean mySession = false;
    //public static int Subcategoria = 0;
    //public static int categoria = 0;

    //para controlar el acceso a google y Facebook
    //public static boolean preSession = false;

    //para controlar que el pedido no sea de más de 2 establecimientos
    //public static int numEstablecimientos = 0;
   // public static String establecimiento1 = "";
    //public static int codEstablecimiento1;
    //public static int ubicaEstablecimiento1;

    //public static int catProductoSeleccionado;
    public static String apiKey = "AIzaSyCOsnfR9NuuaynSCgVAMM2d9NwMF3rv-PE";
    public static String apiKeyMiMaps = "AIzaSyB3cy1SAF1-0y0odBoZ9Tgp_0YF-2Mv-DY";
    ///public static String direccion = "";
    //public static String direccion2 = "";

    //public static String referencia = "";
    //public static Double longitudOrigen = 0.0;
    //public static Double latitudOrigen = 0.0;
    //public static double montoTotal;
    //public static double montoDelivery;
    //public static double montoCompra;
    //public static String formaPago = "Efectivo";
    //public static double montoCargo = 0;
    //public static double montoDescuento = 0;

    // variables para direcciones - Delivery Normal
    //public static String EtiquetaSeleccionada = "";
    //public static String LatitudSeleccionada = "";
    //public static String LongitudSeleccionada = "";
    //public static int CodigoDireccionSeleccionada;
    //public static String CiudadDireccionSeleccionada = "";

    // variables para direcciones - Encargo
    //public static String EtiquetaSeleccionada_Encargo = "";
    //public static String LatitudSeleccionada_Encargo = "";
    //public static String LongitudSeleccionada_Encargo = "";
    //public static int CodigoDireccionSeleccionada_Encargo;
    //public static String CiudadDireccionSeleccionada_Encargo = "";

    // variables para direcciones - Extra
    /*public static String EtiquetaSeleccionada_Extra = "";
    public static String LatitudSeleccionada_Extra = "";
    public static String LongitudSeleccionada_Extra = "";
    public static int CodigoDireccionSeleccionada_Extra;
    public static String CiudadDireccionSeleccionada_Extra = "";
    */

    // variables para Encargos
    //public static boolean isEncargo = false;
    //public static String CiudadEncargoSeleccionada = "";
    //public static int CodigoCiudadEncargoSeleccionada;
    //public static double montoDeliveryEncargo = 0.0;
    //public static String LugarRecogerEncargo = "";
    //public static String DetalleEncargo = "";
    //public static String NumeroContactoEncargo = "";

    // variables para Extras - pide lo que quieras
    //public static boolean isExtra = false;
    //public static String CiudadPideloSeleccionada = "";
    //public static int CodigoCiudadPideloSeleccionada;
    //public static double montoDeliveryPidelo = 0.0;
    //public static String LugarComprarPidelo = "";
    //public static String DetallePidelo = "";
    //public static String NumeroContactoPidelo = "";

    //variable para determinar el tipo de pago
    //public static int tipoPago;

    //variable para determinar si es promocion
    //public static boolean promo = false;

   // public static String nombreEmpresaSeleccionada="";
    //public static Double  LongitudEmpresaSeleccionada;
    //public static Double  LatitudEmpresaSeleccionada;
    //public static int ubicacionEmpresaSeleccionada;
    //public static String taperEmpresaSel = "";
    //public static double costoTaperEmpresaSel = 0.0;

    //public static int tiendasAbiertas = 0;
    //Promocion seleccionada
    public static Promocion PromocionPersonalizar;
    //Producto seleccionada

    //variables para personalización de pedido
    /*public static String personalizarNombre;
    public static String personalizarDescripcion  = "";
    public static String personalizarCategoria ;
    public static String personalizarPrecio;
    public static String personalizarCantidad;
    public static String personalizarImagen;*/

    //variables para distancia y duracion
    /*public static int Distancia;
    public static int DuracionSegundos;
    public static String DuracionTexto;*/
    public static String mensaje;
    //public static String ok = "";

    public static String mensajeVisa;

    //para validar si la tienda seleccionada está abierta
    public static boolean tiendaCerrada = false;

    //public static String imagenSubcategoria;
    public static String imagenEmpresa;
    public static String imagenFondoEmpresa;

    public static String mVerificationId;

    public static String OpcionInicio = "";

    //public static String pagarcon = "";
    //public static boolean recoger_en_tienda = false;
    //public static double deliveryTemporal = 0; //solo para cuando se hace el rojo en tienda

    //public static double  = 0;
    /*  VARIABLES PARA EL PEDIDO
        myEditor.putString("formaPago", "Efectivo");
    */
    /*VARIABLES ALMACENADAS EN CACHÉ DE USO GENERAL - CLIENTE
     String codigo = myPreferences.getString("Id_Cliente", "0");
     String name = myPreferences.getString("Name_Cliente", "");
     String number = myPreferences.getString("Number_Cliente", "");
     String e_mail = myPreferences.getString("Email_Cliente", "");
     String city = myPreferences.getString("City_Cliente", "");
     String photo = myPreferences.getString("Photo_Cliente", "");
     */
    /* VARIABLES DE UBICACIÓN Y CIUDAD SELECCIONADA
     myEditor.putString("ubicacion", String.valueOf(ubicacion));
     myEditor.putString("latitudCiudadMapa", String.valueOf(latitudCiudadMapa));
     myEditor.putString("longitudCiudadMapa", String.valueOf(longitudCiudadMapa));
     myEditor.putString("radioCiudadMapa", String.valueOf(radioCiudadMapa));
     myEditor.putString("precioBaseCiudadMapa", String.valueOf(precioBaseCiudadMapa));
     myEditor.putString("precioExtraCiudadMapa", String.valueOf(precioExtraCiudadMapa));
     myEditor.putString("distanciabase", String.valueOf(distanciabase));
    */
    /*VARIABLES SELECCIONADAS EN TIEMPO DE EJECUCION
    myEditor.putString("empresaseleccionada", String.valueOf(latitudCiudadMapa));

     */
    /*VARIABLES PARA TIPO DE FUENTES
    String Riffic = "fonts/Riffic.ttf";
    String Nexa = "fonts/NEXABOLD.otf";
    String Gothic = "fonts/GOTHIC.ttf";
    String FontAwesome = "fonts/FontAwesome.ttf";
    Typeface typefaceRiffic;
    Typeface typefaceNexa = Typeface.createFromAsset(getAssets(), "fonts/NEXABOLD.otf");
    Typeface typefaceGothic;
    Typeface typefaceFontAwesome;


     */
    public ArrayList<Ubicacion> getDataFromSharedPreferences(String PRODUCT_TAG){
        Gson gson = new Gson();
        ArrayList<Ubicacion> productFromShared = new ArrayList<>();
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("SharedPrefs", Context.MODE_PRIVATE);
        String jsonPreferences = sharedPref.getString(PRODUCT_TAG, "");

        Type type = new TypeToken<ArrayList<Ubicacion>>() {}.getType();
        productFromShared = gson.fromJson(jsonPreferences, type);

        return productFromShared;
    }

    public <T> void setList(String key, ArrayList<T> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("SharedPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor myEditor;
        myEditor = sharedPref.edit();
        myEditor.putString(key, json);
        myEditor.commit();
    }

    public ArrayList<PedidoDetalle> getListaPedidosCache(String DETALLE_TAG){
        Gson gson = new Gson();
        ArrayList<PedidoDetalle> productFromShared = new ArrayList<>();
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("SharedPrefs", Context.MODE_PRIVATE);
        String jsonPreferences = sharedPref.getString(DETALLE_TAG, new ArrayList<PedidoDetalle>().toString());

        Type type = new TypeToken<ArrayList<PedidoDetalle>>() {}.getType();
        productFromShared = gson.fromJson(jsonPreferences, type);

        return productFromShared;
    }

    public <T> void addListaPedidos(String key, PedidoDetalle detalle) {
        ArrayList<PedidoDetalle> lista = getListaPedidosCache("lista_pedidos");
        lista.add(detalle);
        setList("lista_pedidos", lista);
    }
}
