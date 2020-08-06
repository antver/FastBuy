package com.fastbuyapp.omar.fastbuy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fastbuyapp.omar.fastbuy.Operaciones.Calcular_Minutos;
import com.fastbuyapp.omar.fastbuy.Operaciones.DecimalDigitsInputFilter;
import com.fastbuyapp.omar.fastbuy.Operaciones.Pago_Visa;
import com.fastbuyapp.omar.fastbuy.config.Globales;
import com.fastbuyapp.omar.fastbuy.entidades.PedidoDetalle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.refactor.lib.colordialog.PromptDialog;
import lib.visanet.com.pe.visanetlib.VisaNet;
import lib.visanet.com.pe.visanetlib.presentation.custom.VisaNetViewAuthorizationCustom;

public class PagoTarjetaActivity extends AppCompatActivity {


    public ArrayList<PedidoDetalle> listaPedidos;
    public double total;
    double costoEnvio = Globales.montoDelivery;
    double sumaTotal = Globales.montoCompra;
    Calcular_Minutos calcula = new Calcular_Minutos();
    String timePedido;
    SharedPreferences myPreferences;
    int codigoVisa = 0;
    String codigoCupon;

    EditText txtCodCupon;
    Button btnValidarCupon;
    String codigo;

    int cantidadCupones,tipoCupon,estadoCupon;
    String promoCupon, codClienteCupon;
    String montoCupon;
    double descuento = Globales.montoDescuento;
    String DireccionSeleccionada;
    Button btnGenerarCompra;
    String laempresa = "";

    Pago_Visa visa = new Pago_Visa();
    double cargo;
    String name, number, tokencito, formaPago, ubicacion, ciudad;
    TextView txtSubTotal, txtDeliveryTotal, txtCargoTotal, txtTotal, txtDescuentoTotal;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //esto sucedera luego de ejecutar el activity de visa
        super.onActivityResult(requestCode, resultCode, data);
        Log.v("RESULT CODE", String.valueOf(resultCode));
        Log.v("DATA", String.valueOf(data));
        if (requestCode == VisaNet.VISANET_AUTHORIZATION) {
            if (data != null) {
                if (resultCode == RESULT_OK) {
                    String JSONString = data.getExtras().getString("keySuccess");
                    Globales.mensajeVisa = JSONString;
                    try {
                        ProgressDialog progDailog = null;
                        progDailog = new ProgressDialog(PagoTarjetaActivity.this);
                        progDailog.setMessage("Generando pedido...");
                        progDailog.setIndeterminate(true);
                        progDailog.setCancelable(true);
                        progDailog.show();
                        //registrarPedido(Globales.nombreCliente, Globales.direccion2 + ", " + Globales.ciudadOrigen, Globales.numeroTelefono, String.valueOf(Globales.montoDelivery), String.valueOf(Globales.montoCargo), Globales.formaPago, "00:30:00", Globales.getInstance().getListaPedidos());
                        timePedido = calcula.ObtenHora().toString();
                        registrarPedido(name, DireccionSeleccionada + ", " + Globales.CiudadDireccionSeleccionada, number, String.valueOf(Globales.montoDelivery), String.valueOf(Globales.montoCargo), formaPago, timePedido, "0.00", "0.00", String.valueOf(Globales.montoDescuento), Globales.listaPedidos, progDailog);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                } else {
                    String JSONString = data.getExtras().getString("keyError");
                    JSONString = JSONString != null ? JSONString : "";
                    Globales.mensajeVisa = JSONString;
                    Intent intent = new Intent(this, PagoExitosoVisaActivity.class);
                    intent.putExtra("estado", "false");
                    intent.putExtra("state", "0");
                    intent.putExtra("empresa", "");
                    intent.putExtra("pedido", "");
                    intent.putExtra("cantidadRespuestas", "0");
                    startActivity(intent);
                }
            } else {
                btnGenerarCompra.setEnabled(true);
                Toast toast1 = Toast.makeText(PagoTarjetaActivity.this, "Transacción Detenida...", Toast.LENGTH_LONG);
                View vistaToast = toast1.getView();
                vistaToast.setBackgroundResource(R.drawable.toast_yellow);
                toast1.show();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        inicializaVariablesCupon();
        //Start Logica para el pago
        costoEnvio = Globales.montoDelivery;
        descuento = Globales.montoDescuento;
        if (formaPago.equals("Tarjeta"))
            cargo = visa.calcularCargoVisa(sumaTotal + costoEnvio - descuento);
        else
            cargo = 0;
        total = costoEnvio + sumaTotal + cargo - descuento;
        Globales.montoCargo = cargo;
        Globales.montoTotal = total;

        muestraTextos();
        //End Logica para el pago
    }

    public void muestraTextos(){
        txtSubTotal.setText("S/"+ String.format("%.2f",sumaTotal).toString().replace(",","."));
        costoEnvio = Globales.montoDelivery;
        txtDeliveryTotal.setText("S/"+ String.format("%.2f",costoEnvio).toString().replace(",","."));
        cargo = Globales.montoCargo;
        txtCargoTotal.setText("S/"+ String.format("%.2f",cargo).toString().replace(",","."));
        descuento = Globales.montoDescuento;
        txtDescuentoTotal.setText("S/-"+ String.format("%.2f",descuento).toString().replace(",","."));
        total = Globales.montoTotal;
        txtTotal.setText("S/"+ String.format("%.2f",total).toString().replace(",","."));
    }

    private void inicializaVariablesCupon(){
        promoCupon= "";
        codClienteCupon= "";
        montoCupon= "";
        cantidadCupones = 0;
        tipoCupon = 0;
        estadoCupon = -1;
    }
    TextView txtMensajePagoEfectivo;
    EditText etMontoPagoEfectivo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pago_tarjeta);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        myPreferences =  PreferenceManager.getDefaultSharedPreferences(this);
        codigo = myPreferences.getString("Id_Cliente", "0");
        name = myPreferences.getString("Name_Cliente", "");
        formaPago = myPreferences.getString("formaPago", "");
        number = myPreferences.getString("Number_Cliente", "");
        tokencito = myPreferences.getString("tokencito", "");

        DireccionSeleccionada = myPreferences.getString("DireccionSeleccionada", "");
        ciudad = myPreferences.getString("City_Cliente", "");
        ubicacion = myPreferences.getString("ubicacion", "");
        // Iniciando variables para Mostrar datos calculados
        txtSubTotal = (TextView) findViewById(R.id.txtSubTotalGeneral);
        txtDeliveryTotal = (TextView) findViewById(R.id.txtDeliveryGeneral);
        txtCargoTotal = (TextView) findViewById(R.id.txtCargoGeneral);
        txtDescuentoTotal = (TextView) findViewById(R.id.txtDescuentoGeneral);
        txtTotal = (TextView) findViewById(R.id.txtTotalGeneral);
        etMontoPagoEfectivo = (EditText) findViewById(R.id.etMontoPagoEfectivo);
        txtMensajePagoEfectivo  = (TextView) findViewById(R.id.txtMensajePagoEfectivo);
        //Start Valida Cupon
        txtCodCupon = (EditText) findViewById(R.id.txtCodigoCupon);
        btnValidarCupon = (Button) findViewById(R.id.btnValidaCupon);

        etMontoPagoEfectivo.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(3,2)});

        txtCodCupon.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (txtCodCupon.getText().length() == 6) {
                    btnValidarCupon.setBackground(getDrawable(R.drawable.botonamarillo2));
                    btnValidarCupon.setEnabled(true);
                    //InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    //inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } else {
                    btnValidarCupon.setBackground(getDrawable(R.drawable.boton_gris2));
                    btnValidarCupon.setEnabled(false);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (txtCodCupon.getText().length() == 6) {
                    btnValidarCupon.setBackground(getDrawable(R.drawable.botonamarillo2));
                    btnValidarCupon.setEnabled(true);
                } else {
                    btnValidarCupon.setBackground(getDrawable(R.drawable.boton_gris2));
                    btnValidarCupon.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (txtCodCupon.getText().length() == 6) {
                    btnValidarCupon.setBackground(getDrawable(R.drawable.botonamarillo2));
                    btnValidarCupon.setEnabled(true);
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } else {
                    btnValidarCupon.setBackground(getDrawable(R.drawable.boton_gris2));
                    btnValidarCupon.setEnabled(false);
                }
            }
        });

        btnValidarCupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Logica para validar Cupón
                codigoCupon = txtCodCupon.getText().toString();
                ProgressDialog progDailog = null;
                progDailog = new ProgressDialog(PagoTarjetaActivity.this);
                progDailog.setMessage("Validando Cupón...");
                progDailog.setIndeterminate(true);
                progDailog.setCancelable(false);
                progDailog.show();
                String consulta = "https://apifbdelivery.fastbuych.com/Delivery/ValidaCupon?auth="+tokencito+"&codigo="+codigoCupon;
                ConsultaCupon(consulta, progDailog);
            }
        });
        //Start Validar Tipo de pago
        LinearLayout LinearCargo = (LinearLayout) findViewById(R.id.linearCargo);
        ImageView imgPago = (ImageView) findViewById(R.id.imagenPago);
        if(formaPago.equals("Tarjeta")){ // Pago con tarjeta
            LinearCargo.setVisibility(View.VISIBLE);
            etMontoPagoEfectivo.setVisibility(View.GONE);
            txtMensajePagoEfectivo.setVisibility(View.GONE);
        }else if(formaPago.equals("Yape")){ // Pago con Yape
            LinearCargo.setVisibility(View.GONE);
            etMontoPagoEfectivo.setVisibility(View.GONE);
            txtMensajePagoEfectivo.setVisibility(View.GONE);
            Bitmap bmp = BitmapFactory.decodeResource(getResources(),R.drawable.yape);
            imgPago.setImageBitmap(bmp);
        }else if(formaPago.equals("Plin")){ // Pago con plin
            LinearCargo.setVisibility(View.GONE);
            etMontoPagoEfectivo.setVisibility(View.GONE);
            txtMensajePagoEfectivo.setVisibility(View.GONE);
            Bitmap bmp = BitmapFactory.decodeResource(getResources(),R.drawable.plin);
            imgPago.setImageBitmap(bmp);
        }else{
            LinearCargo.setVisibility(View.GONE);
            etMontoPagoEfectivo.setVisibility(View.VISIBLE);
            txtMensajePagoEfectivo.setVisibility(View.VISIBLE);
            Bitmap bmp = BitmapFactory.decodeResource(getResources(),R.drawable.ic_efectivo);
            imgPago.setImageBitmap(bmp);
        }
        //End Validar Tipo de pago

        //Start pop-up para seleccionar la direccion
        abrirPopUp();
        //End pop-up para seleccionar la direccion

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_chevron_left_black_24dp));

        //Start Boton Comprar
        btnGenerarCompra = (Button) findViewById(R.id.btnComprar);
        btnGenerarCompra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DireccionSeleccionada.equals("")){
                    abrirPopUp();
                }else{
                    btnGenerarCompra.setEnabled(false);
                    //Toast.makeText(PagoTarjetaActivity.this,"Generando Compra...",Toast.LENGTH_SHORT).show();
                    if(formaPago.equals("Efectivo")){ // cuando es pago en efectivo
                        if(etMontoPagoEfectivo.getText().toString().trim().length() == 0){
                            Toast toast1 = Toast.makeText(PagoTarjetaActivity.this, "Ingrese el monto de pago.", Toast.LENGTH_SHORT);
                            View vistaToast = toast1.getView();
                            vistaToast.setBackgroundResource(R.drawable.toast_yellow);
                            toast1.show();
                            btnGenerarCompra.setEnabled(true);
                            return;
                        }
                        if(Double.parseDouble(etMontoPagoEfectivo.getText().toString()) < Globales.montoTotal){
                            Toast toast1 = Toast.makeText(PagoTarjetaActivity.this, "El monto en efectivo no puede ser menor al total del pedido.", Toast.LENGTH_SHORT);
                            View vistaToast = toast1.getView();
                            vistaToast.setBackgroundResource(R.drawable.toast_yellow);
                            toast1.show();
                            btnGenerarCompra.setEnabled(true);
                            return;
                        }
                        DecimalFormat df = new DecimalFormat("#.#");
                        String _PagoEfectivo = df.format(Double.parseDouble(etMontoPagoEfectivo.getText().toString())).replace(",",".");
                        //Globales.pagarcon = etMontoPagoEfectivo.getText().toString();
                        Globales.pagarcon = _PagoEfectivo;
                        final String vueltito = calculaVuelto();
                        AlertDialog.Builder builder = new AlertDialog.Builder(PagoTarjetaActivity.this);
                        builder.setMessage("Se registrará un nueva Compra. ¿Desea continuar?");
                        builder.setTitle("Nueva Compra");
                        builder.setCancelable(false);
                        builder.setPositiveButton("Si", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                listaPedidos = Globales.listaPedidos;
                                try {
                                    btnGenerarCompra.setEnabled(false);
                                    ProgressDialog progDailog = null;
                                    progDailog = new ProgressDialog(PagoTarjetaActivity.this);
                                    progDailog.setMessage("Generando pedido...");
                                    progDailog.setIndeterminate(true);
                                    progDailog.setCancelable(false);
                                    progDailog.show();
                                    //registrarPedido(Globales.nombreCliente, Globales.direccion2 + ", " + Globales.ciudadOrigen, Globales.numeroTelefono, String.valueOf(Globales.montoDelivery), String.valueOf(Globales.montoCargo), Globales.formaPago, "00:30:00", Globales.getInstance().getListaPedidos());
                                    //registrarPedido("Omar Vera", "Oficina FastBuy, " + Globales.ciudadOrigen,"953957038", String.valueOf(Globales.montoDelivery), String.valueOf(Globales.montoCargo), Globales.formaPago, "00:30:00", Globales.listaPedidos);
                                    timePedido = calcula.ObtenHora().toString();
                                    registrarPedido(name, DireccionSeleccionada+ ", " + Globales.CiudadDireccionSeleccionada,number, String.valueOf(Globales.montoDelivery), String.valueOf(Globales.montoCargo), formaPago, timePedido, Globales.pagarcon, vueltito, String.valueOf(Globales.montoDescuento), Globales.listaPedidos, progDailog);
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                            }

                        });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                btnGenerarCompra.setEnabled(true);
                                dialogInterface.cancel();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                    else if(formaPago.equals("Tarjeta")){
                        ProgressDialog progDailog = null;
                        progDailog = new ProgressDialog(PagoTarjetaActivity.this);
                        progDailog.setMessage("Cargando datos...");
                        progDailog.setIndeterminate(true);
                        progDailog.setCancelable(false);
                        progDailog.show();
                        String consultita = "https://apifbdelivery.fastbuych.com/Delivery/CorrelativoVisa?auth="+ tokencito;
                        //CorrelativoVisa("http://fastbuych.com/app/consultasapp/app_visa_correlativo.php", total);
                        CorrelativoVisa(consultita, total, progDailog);
                    }
                    else if(formaPago.equals("Yape")){ //pago con Yape
                        Globales.pagarcon = "0";
                        final String vueltito = "0";
                        AlertDialog.Builder builder = new AlertDialog.Builder(PagoTarjetaActivity.this);
                        builder.setMessage("Se registrará un nueva Compra. ¿Desea continuar?");
                        builder.setTitle("Nueva Compra");
                        builder.setCancelable(false);
                        builder.setPositiveButton("Si", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                listaPedidos = Globales.listaPedidos;
                                try {
                                    btnGenerarCompra.setEnabled(false);
                                    ProgressDialog progDailog = null;
                                    progDailog = new ProgressDialog(PagoTarjetaActivity.this);
                                    progDailog.setMessage("Generando pedido...");
                                    progDailog.setIndeterminate(true);
                                    progDailog.setCancelable(false);
                                    progDailog.show();
                                    //registrarPedido(Globales.nombreCliente, Globales.direccion2 + ", " + Globales.ciudadOrigen, Globales.numeroTelefono, String.valueOf(Globales.montoDelivery), String.valueOf(Globales.montoCargo), Globales.formaPago, "00:30:00", Globales.getInstance().getListaPedidos());
                                    //registrarPedido("Omar Vera", "Oficina FastBuy, " + Globales.ciudadOrigen,"953957038", String.valueOf(Globales.montoDelivery), String.valueOf(Globales.montoCargo), Globales.formaPago, "00:30:00", Globales.listaPedidos);
                                    timePedido = calcula.ObtenHora().toString();
                                    registrarPedido(name, DireccionSeleccionada+ ", " + Globales.CiudadDireccionSeleccionada,number, String.valueOf(Globales.montoDelivery), String.valueOf(Globales.montoCargo), formaPago, timePedido, Globales.pagarcon, vueltito, String.valueOf(Globales.montoDescuento), Globales.listaPedidos, progDailog);
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                            }

                        });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                    else if(formaPago.equals("Plin")){ //pago con plin
                        Globales.pagarcon = "0";
                        final String vueltito = "0";
                        AlertDialog.Builder builder = new AlertDialog.Builder(PagoTarjetaActivity.this);
                        builder.setMessage("Se registrará un nueva Compra. ¿Desea continuar?");
                        builder.setTitle("Nueva Compra");
                        builder.setPositiveButton("Si", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                listaPedidos = Globales.listaPedidos;
                                try {
                                    btnGenerarCompra.setEnabled(false);
                                    ProgressDialog progDailog = null;
                                    progDailog = new ProgressDialog(PagoTarjetaActivity.this);
                                    progDailog.setMessage("Generando pedido...");
                                    progDailog.setIndeterminate(true);
                                    progDailog.setCancelable(false);
                                    progDailog.show();
                                    //registrarPedido(Globales.nombreCliente, Globales.direccion2 + ", " + Globales.ciudadOrigen, Globales.numeroTelefono, String.valueOf(Globales.montoDelivery), String.valueOf(Globales.montoCargo), Globales.formaPago, "00:30:00", Globales.getInstance().getListaPedidos());
                                    //registrarPedido("Omar Vera", "Oficina FastBuy, " + Globales.ciudadOrigen,"953957038", String.valueOf(Globales.montoDelivery), String.valueOf(Globales.montoCargo), Globales.formaPago, "00:30:00", Globales.listaPedidos);
                                    timePedido = calcula.ObtenHora().toString();
                                    registrarPedido(name, DireccionSeleccionada+ ", " + Globales.CiudadDireccionSeleccionada,number, String.valueOf(Globales.montoDelivery), String.valueOf(Globales.montoCargo), formaPago, timePedido, Globales.pagarcon, vueltito, String.valueOf(Globales.montoDescuento), Globales.listaPedidos, progDailog);
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                            }

                        });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                }
            }
        });
        //End Boton Comprar

        //botones del menu
        ImageButton btnHome = (ImageButton) findViewById(R.id.btnHome);
        ImageButton btnFavoritos = (ImageButton) findViewById(R.id.btnFavoritos);
        ImageButton btnCarrito = (ImageButton) findViewById(R.id.btnCarrito);
        ImageButton btnUsuario = (ImageButton) findViewById(R.id.btnUsuario);
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PagoTarjetaActivity.this, PrincipalActivity.class);
                startActivity(intent);
            }
        });

        btnFavoritos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PagoTarjetaActivity.this, FavoritosActivity.class);
                startActivity(intent);
            }
        });

        btnCarrito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PagoTarjetaActivity.this, CarritoActivity.class);
                startActivity(intent);
            }
        });

        btnUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PagoTarjetaActivity.this, UserActivity.class);
                startActivity(intent);
            }
        });

        CheckBox ckRecogerEnTienda = (CheckBox) findViewById(R.id.ckRecogerEnTienda);
        ckRecogerEnTienda.setChecked(Globales.recoger_en_tienda);
        ckRecogerEnTienda.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){
                    costoEnvio = 0;
                    Globales.montoDelivery = 0;
                    Globales.recoger_en_tienda = true;
                }
                else {
                    Globales.montoDelivery = Globales.deliveryTemporal;
                    costoEnvio = Globales.deliveryTemporal;
                    Globales.recoger_en_tienda = false;
                }
                if (formaPago.equals("Tarjeta"))
                    cargo = visa.calcularCargoVisa(sumaTotal + costoEnvio - descuento);
                else
                    cargo = 0;
                total = sumaTotal + cargo + costoEnvio - descuento;
                Globales.montoCargo = cargo;
                Globales.montoTotal = total;
                muestraTextos();
            }
        });
    }

    public String calculaVuelto(){
        double elVuelto = Double.parseDouble(Globales.pagarcon) - Globales.montoTotal;
        return String.valueOf(elVuelto);
    }

    public  void abrirPopUp(){
        Intent intent = new Intent(PagoTarjetaActivity.this, SeleccionaDireccionActivity.class);
        startActivity(intent);
    }

    public void ConsultaCupon(String URL, final ProgressDialog progDialog){
        RequestQueue queue = Volley.newRequestQueue(PagoTarjetaActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progDialog.dismiss();
                if (response.length()>0){
                    try {
                        JSONArray ja = new JSONArray(response);
                        for (int i = 0; i < ja.length(); i++){
                            JSONObject jo = ja.getJSONObject(i);
                            cantidadCupones = jo.getInt("CUP_Cantidad");

                            if (cantidadCupones > 0){

                                montoCupon = jo.getString("CUP_Monto");
                                tipoCupon = jo.getInt("TIC_Codigo");
                                promoCupon = jo.getString("CUP_Promo");
                                estadoCupon = jo.getInt("CUP_Estado");
                                codClienteCupon = jo.getString("CLI_Codigo");

                                if (promoCupon.equals("COMPARTIDO")){
                                    if(estadoCupon == 1){
                                        if (codClienteCupon.equals(codigo)){
                                            Toast toast1 = Toast.makeText(PagoTarjetaActivity.this, "Cupón Inhabilitado, Su invitado aún no activa el cupón.", Toast.LENGTH_SHORT);
                                            View vistaToast = toast1.getView();
                                            vistaToast.setBackgroundResource(R.drawable.toast_yellow);
                                            toast1.show();
                                        }
                                        else{
                                            String URL = "https://apifbdelivery.fastbuych.com/Delivery/ValidaCuponCompartido?auth="+tokencito+"&codigo="+codigoCupon+"&cliente="+codigo;
                                            RequestQueue queue = Volley.newRequestQueue(PagoTarjetaActivity.this);
                                            StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>(){
                                                @Override
                                                public void onResponse(String response) {
                                                    if (response.length()>0){
                                                        if (response.equals("NO")){
                                                            Toast toast1 = Toast.makeText(PagoTarjetaActivity.this, "Ud. No puede usar este Cupón.", Toast.LENGTH_SHORT);
                                                            View vistaToast = toast1.getView();
                                                            vistaToast.setBackgroundResource(R.drawable.toast_yellow);
                                                            toast1.show();
                                                        }
                                                        else{
                                                            AlertDialog.Builder builder = new AlertDialog.Builder(PagoTarjetaActivity.this);
                                                            builder.setMessage("Se aplicará un descuento de acuerdo al codigo del cupón ingresado. ¿Desea continuar?");
                                                            builder.setTitle("Cupón Válido");
                                                            builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    txtCodCupon.setEnabled(false);
                                                                    btnValidarCupon.setEnabled(false);
                                                                    btnValidarCupon.setBackgroundResource(R.drawable.boton_gris2);

                                                                    if (tipoCupon == 1){
                                                                        //monto en soles
                                                                        descuento = Double.valueOf(montoCupon.toString());
                                                                        Toast toast1 = Toast.makeText(PagoTarjetaActivity.this, "Obtendras un descuento de S/"+ String.format("%.2f",descuento).toString().replace(",","."), Toast.LENGTH_SHORT);
                                                                        View vistaToast = toast1.getView();
                                                                        vistaToast.setBackgroundResource(R.drawable.toast_success);
                                                                        toast1.show();
                                                                        if (formaPago.equals("Tarjeta"))
                                                                            cargo = visa.calcularCargoVisa(sumaTotal + costoEnvio - descuento);
                                                                        else
                                                                            cargo = 0;
                                                                        total = sumaTotal + cargo + costoEnvio - descuento;
                                                                    }else if (tipoCupon == 2){
                                                                        //monto en porcentaje
                                                                        descuento = (double) ((double)Double.valueOf(montoCupon.toString())/100)*sumaTotal;
                                                                        Toast toast1 = Toast.makeText(PagoTarjetaActivity.this, "Obtendras un descuento del "+ montoCupon.toString()+"%", Toast.LENGTH_SHORT);
                                                                        View vistaToast = toast1.getView();
                                                                        vistaToast.setBackgroundResource(R.drawable.toast_success);
                                                                        toast1.show();
                                                                        if (formaPago.equals("Tarjeta"))
                                                                            cargo = visa.calcularCargoVisa(sumaTotal + costoEnvio - descuento);
                                                                        else
                                                                            cargo = 0;
                                                                        total = sumaTotal + cargo + costoEnvio - descuento;
                                                                    }else if (tipoCupon == 3){
                                                                        //no se cobra delivery
                                                                        descuento = costoEnvio;
                                                                        Toast toast1 = Toast.makeText(PagoTarjetaActivity.this, "Obtendras el delivery gratis", Toast.LENGTH_SHORT);
                                                                        View vistaToast = toast1.getView();
                                                                        vistaToast.setBackgroundResource(R.drawable.toast_success);
                                                                        toast1.show();
                                                                        if (formaPago.equals("Tarjeta"))
                                                                            cargo = visa.calcularCargoVisa(sumaTotal + costoEnvio - descuento);
                                                                        else
                                                                            cargo = 0;
                                                                        total = sumaTotal + cargo + costoEnvio - descuento;
                                                                    }
                                                                    Globales.montoDescuento = descuento;
                                                                    Globales.montoCargo = cargo;
                                                                    Globales.montoTotal = total;
                                                                    muestraTextos();
                                                                    //actualizaCantidadCupon(codigoCupon,Globales.codigoCliente);
                                                                }
                                                            });
                                                            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    txtCodCupon.setEnabled(true);
                                                                    btnValidarCupon.setEnabled(true);
                                                                    btnValidarCupon.setBackgroundResource(R.drawable.botonamarillo2);
                                                                    dialog.cancel();
                                                                }
                                                            });
                                                            AlertDialog dialog = builder.create();
                                                            dialog.show();
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
                                    }
                                    else{
                                        if (codClienteCupon.equals(codigo)){
                                            AlertDialog.Builder builder = new AlertDialog.Builder(PagoTarjetaActivity.this);
                                            builder.setMessage("Se aplicará un descuento de acuerdo al codigo del cupón ingresado. ¿Desea continuar?");
                                            builder.setTitle("Cupón Válido");
                                            builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    txtCodCupon.setEnabled(false);
                                                    btnValidarCupon.setEnabled(false);
                                                    btnValidarCupon.setBackgroundResource(R.drawable.boton_gris2);

                                                    if (tipoCupon == 1){
                                                        //monto en soles
                                                        descuento = Double.valueOf(montoCupon.toString());
                                                        Toast toast1 = Toast.makeText(PagoTarjetaActivity.this, "Obtendras un descuento de S/"+ String.format("%.2f",descuento).toString().replace(",","."), Toast.LENGTH_SHORT);
                                                        View vistaToast = toast1.getView();
                                                        vistaToast.setBackgroundResource(R.drawable.toast_success);
                                                        toast1.show();
                                                        if (formaPago.equals("Tarjeta"))
                                                            cargo = visa.calcularCargoVisa(sumaTotal + costoEnvio - descuento);
                                                        else
                                                            cargo = 0;
                                                        total = sumaTotal + cargo + costoEnvio - descuento;
                                                    }else if (tipoCupon == 2){
                                                        //monto en porcentaje
                                                        descuento = (double) ((double)Double.valueOf(montoCupon.toString())/100)*sumaTotal;
                                                        Toast toast1 = Toast.makeText(PagoTarjetaActivity.this, "Obtendras un descuento del "+ montoCupon.toString()+"%", Toast.LENGTH_SHORT);
                                                        View vistaToast = toast1.getView();
                                                        vistaToast.setBackgroundResource(R.drawable.toast_success);
                                                        toast1.show();
                                                        if (formaPago.equals("Tarjeta"))
                                                            cargo = visa.calcularCargoVisa(sumaTotal + costoEnvio - descuento);
                                                        else
                                                            cargo = 0;
                                                        total = sumaTotal + cargo + costoEnvio - descuento;
                                                    }else if (tipoCupon == 3){
                                                        //no se cobra delivery
                                                        descuento = costoEnvio;
                                                        Toast toast1 = Toast.makeText(PagoTarjetaActivity.this, "Obtendras el delivery gratis", Toast.LENGTH_SHORT);
                                                        View vistaToast = toast1.getView();
                                                        vistaToast.setBackgroundResource(R.drawable.toast_success);
                                                        toast1.show();
                                                        if (formaPago.equals("Tarjeta"))
                                                            cargo = visa.calcularCargoVisa(sumaTotal + costoEnvio - descuento);
                                                        else
                                                            cargo = 0;
                                                        total = sumaTotal + cargo + costoEnvio - descuento;
                                                    }
                                                    Globales.montoDescuento = descuento;
                                                    Globales.montoCargo = cargo;
                                                    Globales.montoTotal = total;
                                                    muestraTextos();
                                                }
                                            });
                                            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    txtCodCupon.setEnabled(true);
                                                    btnValidarCupon.setEnabled(true);
                                                    btnValidarCupon.setBackgroundResource(R.drawable.botonamarillo2);
                                                    dialog.cancel();
                                                }
                                            });
                                            AlertDialog dialog = builder.create();
                                            dialog.show();
                                        }
                                        else{
                                            Toast toast1 = Toast.makeText(PagoTarjetaActivity.this, "No puede hacer uso de este Cupón.", Toast.LENGTH_SHORT);
                                            View vistaToast = toast1.getView();
                                            vistaToast.setBackgroundResource(R.drawable.toast_yellow);
                                            toast1.show();
                                        }
                                    }
                                }
                                else if(promoCupon.equals("UNINTENTO")){
                                    String URL = "https://apifbdelivery.fastbuych.com/Delivery/ValidaCupon1intento?auth="+ tokencito+"&codigo="+codigoCupon+"&cliente="+codigo;
                                    RequestQueue queue = Volley.newRequestQueue(PagoTarjetaActivity.this);
                                    StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>(){
                                        @Override
                                        public void onResponse(String response) {
                                            if (response.length()>0){
                                                if (response.equals("USADO")){
                                                    Toast toast1 = Toast.makeText(PagoTarjetaActivity.this, "Ud. ya hizo uso de este Cupón.", Toast.LENGTH_SHORT);
                                                    View vistaToast = toast1.getView();
                                                    vistaToast.setBackgroundResource(R.drawable.toast_yellow);
                                                    toast1.show();
                                                }
                                                else {
                                                    AlertDialog.Builder builder = new AlertDialog.Builder(PagoTarjetaActivity.this);
                                                    builder.setMessage("Se aplicará un descuento de acuerdo al codigo del cupón ingresado. ¿Desea continuar?");
                                                    builder.setTitle("Cupón Válido");
                                                    builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            txtCodCupon.setEnabled(false);
                                                            btnValidarCupon.setEnabled(false);
                                                            btnValidarCupon.setBackgroundResource(R.drawable.boton_gris2);

                                                            if (tipoCupon == 1){
                                                                //monto en soles
                                                                descuento = Double.valueOf(montoCupon.toString());
                                                                Toast toast1 = Toast.makeText(PagoTarjetaActivity.this, "Obtendras un descuento de S/"+ String.format("%.2f",descuento).toString().replace(",","."), Toast.LENGTH_SHORT);
                                                                View vistaToast = toast1.getView();
                                                                vistaToast.setBackgroundResource(R.drawable.toast_success);
                                                                toast1.show();
                                                                if (formaPago.equals("Tarjeta"))
                                                                    cargo = visa.calcularCargoVisa(sumaTotal + costoEnvio - descuento);
                                                                else
                                                                    cargo = 0;
                                                                total = sumaTotal + cargo + costoEnvio - descuento;
                                                            }else if (tipoCupon == 2){
                                                                //monto en porcentaje
                                                                descuento = (double) ((double)Double.valueOf(montoCupon.toString())/100)*sumaTotal;
                                                                Toast toast1 = Toast.makeText(PagoTarjetaActivity.this, "Obtendras un descuento del "+ montoCupon.toString()+"%", Toast.LENGTH_SHORT);
                                                                View vistaToast = toast1.getView();
                                                                vistaToast.setBackgroundResource(R.drawable.toast_success);
                                                                toast1.show();
                                                                if (formaPago.equals("Tarjeta"))
                                                                    cargo = visa.calcularCargoVisa(sumaTotal + costoEnvio - descuento);
                                                                else
                                                                    cargo = 0;
                                                                total = sumaTotal + cargo + costoEnvio - descuento;
                                                            }else if (tipoCupon == 3){
                                                                //no se cobra delivery
                                                                descuento = costoEnvio;
                                                                Toast toast1 = Toast.makeText(PagoTarjetaActivity.this, "Obtendras el delivery gratis", Toast.LENGTH_SHORT);
                                                                View vistaToast = toast1.getView();
                                                                vistaToast.setBackgroundResource(R.drawable.toast_success);
                                                                toast1.show();
                                                                if (formaPago.equals("Tarjeta"))
                                                                    cargo = visa.calcularCargoVisa(sumaTotal + costoEnvio - descuento);
                                                                else
                                                                    cargo = 0;
                                                                total = sumaTotal + cargo + costoEnvio - descuento;
                                                            }
                                                            Globales.montoDescuento = descuento;
                                                            Globales.montoCargo = cargo;
                                                            Globales.montoTotal = total;
                                                            muestraTextos();
                                                        }
                                                    });
                                                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            txtCodCupon.setEnabled(true);
                                                            btnValidarCupon.setEnabled(true);
                                                            btnValidarCupon.setBackgroundResource(R.drawable.botonamarillo2);
                                                            dialog.cancel();
                                                        }
                                                    });
                                                    AlertDialog dialog = builder.create();
                                                    dialog.show();
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
                                else if(promoCupon.equals("PERSONAL")){
                                    AlertDialog.Builder builder = new AlertDialog.Builder(PagoTarjetaActivity.this);
                                    builder.setMessage("Se aplicará un descuento de acuerdo al codigo del cupón ingresado. ¿Desea continuar?");
                                    builder.setTitle("Cupón Válido");
                                    builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            txtCodCupon.setEnabled(false);
                                            btnValidarCupon.setEnabled(false);
                                            btnValidarCupon.setBackgroundResource(R.drawable.boton_gris2);

                                            if (tipoCupon == 1){
                                                //monto en soles
                                                descuento = Double.valueOf(montoCupon.toString());
                                                Toast toast1 = Toast.makeText(PagoTarjetaActivity.this, "Obtendras un descuento de S/"+ String.format("%.2f",descuento).toString().replace(",","."), Toast.LENGTH_SHORT);
                                                View vistaToast = toast1.getView();
                                                vistaToast.setBackgroundResource(R.drawable.toast_success);
                                                toast1.show();
                                                if (formaPago.equals("Tarjeta"))
                                                    cargo = visa.calcularCargoVisa(sumaTotal + costoEnvio - descuento);
                                                else
                                                    cargo = 0;
                                                total = sumaTotal + cargo + costoEnvio - descuento;
                                            }
                                            else if (tipoCupon == 2){
                                                //monto en porcentaje
                                                descuento = (double) ((double)Double.valueOf(montoCupon.toString())/100)*sumaTotal;
                                                Toast toast1 = Toast.makeText(PagoTarjetaActivity.this, "Obtendras un descuento del "+ montoCupon.toString()+"%", Toast.LENGTH_SHORT);
                                                View vistaToast = toast1.getView();

                                                vistaToast.setBackgroundResource(R.drawable.toast_success);
                                                toast1.show();
                                                if (formaPago.equals("Tarjeta"))
                                                    cargo = visa.calcularCargoVisa(sumaTotal + costoEnvio - descuento);
                                                else
                                                    cargo = 0;
                                                total = sumaTotal + cargo + costoEnvio - descuento;
                                            }
                                            else if (tipoCupon == 3){
                                                //no se cobra delivery
                                                descuento = costoEnvio;
                                                Toast toast1 = Toast.makeText(PagoTarjetaActivity.this, "Obtendras el delivery gratis", Toast.LENGTH_SHORT);
                                                View vistaToast = toast1.getView();
                                                vistaToast.setBackgroundResource(R.drawable.toast_success);
                                                toast1.show();
                                                if (formaPago.equals("Tarjeta"))
                                                    cargo = visa.calcularCargoVisa(sumaTotal + costoEnvio - descuento);
                                                else
                                                    cargo = 0;
                                                total = sumaTotal + cargo + costoEnvio - descuento;
                                            }
                                            Globales.montoDescuento = descuento;
                                            Globales.montoCargo = cargo;
                                            Globales.montoTotal = total;
                                            muestraTextos();
                                        }
                                    });
                                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            txtCodCupon.setEnabled(true);
                                            btnValidarCupon.setEnabled(true);
                                            btnValidarCupon.setBackgroundResource(R.drawable.botonamarillo2);
                                            dialog.cancel();
                                        }
                                    });
                                    AlertDialog dialog = builder.create();
                                    dialog.show();
                                }
                            }
                            else{
                                Toast toast1 = Toast.makeText(PagoTarjetaActivity.this, "Cupones agotados", Toast.LENGTH_SHORT);
                                View vistaToast = toast1.getView();
                                vistaToast.setBackgroundResource(R.drawable.toast_yellow);
                                toast1.show();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    txtCodCupon.setEnabled(true);
                    btnValidarCupon.setEnabled(true);
                    btnValidarCupon.setBackgroundResource(R.drawable.botonamarillo2);

                    descuento = 0;
                    if (formaPago.equals("Tarjeta"))
                        cargo = visa.calcularCargoVisa(sumaTotal + costoEnvio - descuento);
                    else
                        cargo = 0;
                    total = sumaTotal + cargo + costoEnvio - descuento;
                    Toast toast1 = Toast.makeText(PagoTarjetaActivity.this, "Código de Cupón No Valido o a expirado", Toast.LENGTH_SHORT);
                    View vistaToast = toast1.getView();
                    vistaToast.setBackgroundResource(R.drawable.toast_yellow);
                    toast1.show();
                    Globales.montoDescuento = descuento;
                    Globales.montoCargo = cargo;
                    Globales.montoTotal = total;
                    muestraTextos();
                }

            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(stringRequest);
    }

    public void CorrelativoVisa(final String URL, final double total, final ProgressDialog progressDialog){
        RequestQueue queue = Volley.newRequestQueue(PagoTarjetaActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                if (response.length()>0){
                    try {
                        JSONObject jo = new JSONObject(response);
                        codigoVisa = jo.getInt("codigo");
                        Log.i("VISA_SALIENTE", String.valueOf(codigoVisa));
                        DecimalFormatSymbols separador = new DecimalFormatSymbols();
                        separador.setDecimalSeparator('.');
                        DecimalFormat df = new DecimalFormat("#.##",separador);
                        //String nTotal = String.format("%.1f",total);
                        String TotalGeneral = df.format(Globales.montoTotal);
                        Map<String, Object> data = new HashMap<>();
                        data.put(VisaNet.VISANET_CHANNEL,VisaNet.Channel.MOBILE);
                        data.put(VisaNet.VISANET_COUNTABLE, true);
                        data.put(VisaNet.VISANET_USERNAME, "diegodelucio01@gmail.com");
                        data.put(VisaNet.VISANET_PASSWORD, "@Am$168Z");
                        data.put(VisaNet.VISANET_MERCHANT, "604410301");//604410301-test-101039934
                        Log.i("VISA", String.valueOf(codigoVisa));
                        data.put(VisaNet.VISANET_PURCHASE_NUMBER, (Object) String.valueOf(codigoVisa));//codigo de compra visa
                        //data.put(VisaNet.VISANET_AMOUNT, (Object) String.valueOf(Globales.montoCompra).toString().replace(",","."));
                        data.put(VisaNet.VISANET_AMOUNT, (Object) TotalGeneral); // este es para enviar en modo produccion
                        //data.put(VisaNet.VISANET_AMOUNT, (Object) "0.50");//a modo de prueba
                        data.put(VisaNet.VISANET_END_POINT_URL, "https://apiprod.vnforapps.com/");
                        //Personalización (Ingreso opcional)
                        VisaNetViewAuthorizationCustom custom = new VisaNetViewAuthorizationCustom();
                        custom.setLogoTextMerchant(true);
                        custom.setLogoTextMerchantText("FASTBUY");
                        custom.setLogoTextMerchantTextColor(R.color.visanet_black);
                        custom.setLogoTextMerchantTextSize(20);
                        //Personalización 2: Configuración del color del botón pagar en el formulario de pago
                        custom.setButtonColorMerchant(R.color.verde_fosforescente);
                        custom.setInputCustom(true);
                        progressDialog.dismiss();
                        VisaNet.authorization(PagoTarjetaActivity.this, data, custom);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
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

    public int codigoRegistro = 0;

    //generando el Url para registrar el pedido_añadido 27_02_2020
    public void registrarPedido(String nombre, String direccion, String telefono,String delivery,String cargo, String forma, String tiempo, String pago, String vuelto, String descuento, ArrayList<PedidoDetalle> lista, ProgressDialog progreso) throws UnsupportedEncodingException {

        String a = URLEncoder.encode(nombre, "UTF-8");
        String b = URLEncoder.encode(direccion, "UTF-8");
        String c = URLEncoder.encode(telefono, "UTF-8");
        String d = URLEncoder.encode(delivery, "UTF-8");
        String e = URLEncoder.encode(cargo, "UTF-8");
        String f = URLEncoder.encode(forma, "UTF-8");
        String g = URLEncoder.encode(tiempo, "UTF-8");
        String h = URLEncoder.encode(ciudad, "UTF-8");
        String i = URLEncoder.encode(String.valueOf(Globales.LatitudSeleccionada), "UTF-8");
        String j = URLEncoder.encode(String.valueOf(Globales.LongitudSeleccionada), "UTF-8");
        String k = URLEncoder.encode(pago, "UTF-8");
        String l = URLEncoder.encode(vuelto, "UTF-8");
        String m = URLEncoder.encode(descuento, "UTF-8");
        String n = URLEncoder.encode(String.valueOf(Globales.montoCompra), "UTF-8");

        String listaDetallada = "";
        for (int z=0; z<lista.size();z++) {
            PedidoDetalle pd = lista.get(z);
            String item = String.valueOf(z+1);
            int promo = 0;
            String codigoProducto;
            String cantidad = String.valueOf(pd.getCantidad());
            String precio = String.valueOf(pd.getPreciounit());
            String total = String.valueOf(pd.getTotal());
            String ubicacion = String.valueOf(pd.getUbicacion());
            //String personalizado = URLEncoder.encode(pd.getPersonalizacion(), "UTF-8");
            String personalizado = pd.getPersonalizacion();
            String presentacion = "1";
            if(pd.isEsPromocion()){
                promo = 1;
                codigoProducto = String.valueOf(pd.getPromocion().getCodigo());
            }
            else{
                promo = 0;
                codigoProducto = String.valueOf(pd.getProducto().getCodigo());
                presentacion = pd.getProducto().getPresentacion();
            }

            String itemLista = item+"##F_B##"+codigoProducto+"##F_B##"+cantidad+"##F_B##"+precio+"##F_B##" + total+"##F_B##" + ubicacion +"##F_B##" + personalizado + "##F_B##" + String.valueOf(promo)+ "##F_B##"+presentacion;
            if(z == lista.size() - 1)
                listaDetallada = listaDetallada + itemLista;
            else
                listaDetallada = listaDetallada + itemLista+"##F_B_N##";

        }

        String o = URLEncoder.encode(listaDetallada,"UTF-8");
        String recoger = (Globales.recoger_en_tienda) ? "1" : "0";
        String consulta = "https://apifbdelivery.fastbuych.com/Delivery/GuardarPedido2?auth="+tokencito+"&nombre="+a+"&direccion="+b+"&telefono=" + c +"&delivery=" + d +"&cargo=" + e +"&forma=" + f +"&tiempo=" + g+"&origen=" + h + "&latitud=" + i + "&longitud=" + j + "&pago=" + k + "&vuelto=" + l+ "&descuento=" + m +"&montoPedido=" + n+"&empresa=" + Globales.codEstablecimiento1+"&recoger=" + recoger +"&ubicacion=" + ubicacion+"&detalle="+o;
        RegistrarPedidoBD(consulta,lista,String.valueOf(Globales.codEstablecimiento1), progreso);
    }

    //Registrando el pedido en la Base de Datos _añadido 27_02_2020
    public void RegistrarPedidoBD(String URL, final ArrayList<PedidoDetalle> lista, final String empresa, final ProgressDialog progreso){
        Log.v("urlDetalle",URL);
        RequestQueue queue = Volley.newRequestQueue(PagoTarjetaActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.length()>0){
                    try {
                        JSONObject jo = new JSONObject(response);
                        codigoRegistro = jo.getInt("Codigo_Pedido");
                        final String cantidadRespuestas = jo.getString("Respuestas_Cantidad");
                        String formapagopedidotemp = formaPago;
                        Log.v("encuesta",cantidadRespuestas);
                        //start añadido el 27-02-2020
                        laempresa = empresa;

                        //actualizaCantidadCupon(codigoCupon);
                        if(promoCupon != ""){
                            actualizaCantidadCupon(codigoCupon,codigo);
                        }

                        //reiniciamos los valores del control máximo de establecimientos
                        Globales.establecimiento1 = "";
                        Globales.numEstablecimientos = 0;

                        //inicializamos datos de la dirección
                        Globales.EtiquetaSeleccionada = "";
                        //Globales.DireccionSeleccionada = "";
                        Globales.LatitudSeleccionada = "";
                        Globales.LongitudSeleccionada = "";
                        Globales.montoDelivery = 0;
                        Globales.CiudadDireccionSeleccionada = "";

                        Globales.montoDescuento = 0;
                        //Globales.formaPago = "Efectivo";
                        Globales.deliveryTemporal = 0;
                        btnGenerarCompra.setEnabled(true);

                        Globales.listaPedidos.clear();
                        progreso.dismiss();
                        if(formapagopedidotemp.equals("Tarjeta")) {
                            Intent intent = new Intent(PagoTarjetaActivity.this, PagoExitosoVisaActivity.class);
                            intent.putExtra("estado", "true");
                            intent.putExtra("state", String.valueOf(0));
                            intent.putExtra("empresa", laempresa);
                            intent.putExtra("pedido", String.valueOf(codigoRegistro));
                            intent.putExtra("cantidadRespuestas", cantidadRespuestas);
                            startActivity(intent);
                        }
                        else {
                            Globales.recoger_en_tienda = false;
                            new PromptDialog(PagoTarjetaActivity.this)
                                    .setDialogType(PromptDialog.DIALOG_TYPE_SUCCESS)
                                    .setAnimationEnable(true)
                                    .setTitleText("PEDIDO N°" +  codigoRegistro)
                                    .setContentText("Su pedido fue generado con éxito.")
                                    .setPositiveListener("OK", new PromptDialog.OnPositiveListener() {
                                        @Override
                                        public void onClick(PromptDialog dialog) {
                                            dialog.dismiss();
                                            Log.v("encuesta", cantidadRespuestas);


                                            Intent intent = new Intent(PagoTarjetaActivity.this, SiguiendoPedidoActivity.class);
                                            intent.putExtra("state", String.valueOf(0));
                                            intent.putExtra("empresa", laempresa);
                                            intent.putExtra("pedido", String.valueOf(codigoRegistro));
                                            intent.putExtra("cantidadRespuestas", cantidadRespuestas);
                                            startActivity(intent);
                                        }
                                    }).show();
                        }
                        //End añadido el 27-02-2020
                    } catch (JSONException e) {
                        e.printStackTrace();
                        progreso.dismiss();
                    }

                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        queue.add(stringRequest);
    }

    /*
    //generando el Url para registrar el pedido
    public void registrarPedido_(String nombre, String direccion, String telefono,String delivery,String cargo, String forma, String tiempo, String pago, String vuelto, String descuento, ArrayList<PedidoDetalle> lista, ProgressDialog progreso) throws UnsupportedEncodingException {
        String a = URLEncoder.encode(nombre, "UTF-8");
        String b = URLEncoder.encode(direccion, "UTF-8");
        String c = URLEncoder.encode(telefono, "UTF-8");
        String d = URLEncoder.encode(delivery, "UTF-8");
        String e = URLEncoder.encode(cargo, "UTF-8");
        String f = URLEncoder.encode(forma, "UTF-8");
        String g = URLEncoder.encode(tiempo, "UTF-8");
        String h = URLEncoder.encode(Globales.ciudadOrigen, "UTF-8");
        String i = URLEncoder.encode(String.valueOf(Globales.LatitudSeleccionada), "UTF-8");
        String j = URLEncoder.encode(String.valueOf(Globales.LongitudSeleccionada), "UTF-8");
        String k = URLEncoder.encode(pago, "UTF-8");
        String l = URLEncoder.encode(vuelto, "UTF-8");
        String m = URLEncoder.encode(descuento, "UTF-8");
        String n = URLEncoder.encode(String.valueOf(Globales.montoCompra), "UTF-8");
        String consulta = "https://apifbdelivery.fastbuych.com/Delivery/GuardarPedido?auth="+Globales.tokencito+"&nombre="+a+"&direccion="+b+"&telefono=" + c +"&delivery=" + d +"&cargo=" + e +"&forma=" + f +"&tiempo=" + g+"&origen=" + h + "&latitud=" + i + "&longitud=" + j + "&pago=" + k + "&vuelto=" + l+ "&descuento=" + m +"&montoPedido=" + n+"&empresa=" + Globales.codEstablecimiento1+"&ubicacion=" + Globales.ubicaEstablecimiento1;
        RegistrarPedidoBD(consulta,lista,String.valueOf(Globales.codEstablecimiento1), progreso);

        //actualizaCantidadCupon(codigoCupon);
        if(promoCupon != ""){
            actualizaCantidadCupon(codigoCupon,Globales.codigoCliente);
        }

        //reiniciamos los valores del control máximo de establecimientos
        Globales.establecimiento1 = "";
        Globales.codEstablecimiento1 = -1;
        Globales.ubicaEstablecimiento1 = -1;
        Globales.numEstablecimientos = 0;

        //inicializamos datos de la dirección
        Globales.EtiquetaSeleccionada = "";
        Globales.DireccionSeleccionada = "";
        Globales.LatitudSeleccionada = "";
        Globales.LongitudSeleccionada = "";
        Globales.montoDelivery = 0;
        //Globales.CodigoDireccionSeleccionada=null;
        Globales.CiudadDireccionSeleccionada = "";

        Globales.montoDescuento = 0;
        Globales.tipoPago = 1;
        Globales.formaPago = "Efectivo";
        btnGenerarCompra.setEnabled(true);
    }

    //Registrando el pedido en la Base de Datos
    public void RegistrarPedidoBD_(String URL, final ArrayList<PedidoDetalle> lista, final String empresa, final ProgressDialog progreso){
        Log.v("urlDetalle",URL);
        RequestQueue queue = Volley.newRequestQueue(PagoTarjetaActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.length()>0){
                    try {
                        JSONObject jo = new JSONObject(response);
                        codigoRegistro = jo.getInt("Codigo_Pedido");
                        //int i = 1;
                        for (int i=0; i<lista.size();i++) {
                            PedidoDetalle pd = lista.get(i);
                            String item = String.valueOf(i+1);
                            int promo = 0;
                            laempresa = empresa;
                            String codigoProducto;
                            String cantidad = String.valueOf(pd.getCantidad());
                            String precio = String.valueOf(pd.getPreciounit());
                            String total = String.valueOf(pd.getTotal());
                            String ubicacion = String.valueOf(pd.getUbicacion());
                            String personalizado = URLEncoder.encode(pd.getPersonalizacion(), "UTF-8");
                            String presentacion = "1";
                            if(pd.isEsPromocion()){
                                promo = 1;
                                codigoProducto = String.valueOf(pd.getPromocion().getCodigo());
                            }
                            else{
                                promo = 0;
                                codigoProducto = String.valueOf(pd.getProducto().getCodigo());
                                presentacion = pd.getProducto().getPresentacion();
                            }
                            Servidor s = new Servidor();
                            //String consulta = "http://"+s.getServidor()+"/app/consultasapp/app_detallepedido_guardar.php?pedido=" + String.valueOf(codigoRegistro) +"&item="+item+"&producto="+codigoProducto+"&cantidad="+cantidad+"&precio="+precio+"&total=" + total+"&ubicacion=" + ubicacion +"&personalizado=" + personalizado + "&promo=" + String.valueOf(promo);
                            String consulta = "https://apifbdelivery.fastbuych.com/Delivery/GuardarPedidoDetalle?auth="+Globales.tokencito+"&pedido=" + String.valueOf(codigoRegistro) +"&item="+item+"&producto="+codigoProducto+"&cantidad="+cantidad+"&precio="+precio+"&total=" + total+"&ubicacion=" + ubicacion +"&personalizado=" + personalizado + "&promo=" + String.valueOf(promo)+ "&presentacion="+presentacion;
                            //txtCodigoPedido.setText(consulta);
                            if(i == lista.size() - 1)
                                RegistrarDetallePedido(consulta, true, progreso);
                            else
                                RegistrarDetallePedido(consulta, false, null);

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        progreso.dismiss();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        progreso.dismiss();
                    }

                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        queue.add(stringRequest);
    }

    //String laempresa = "";
    public void RegistrarDetallePedido(String URL, final boolean ultimo, final ProgressDialog barra) {
        Log.v("urlDetalle",URL);
        RequestQueue queue = Volley.newRequestQueue(PagoTarjetaActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.length()>0){
                    try {
                        JSONArray AR = new JSONArray(response);
                        JSONObject jo = AR.getJSONObject(0);
                        if (ultimo){
                            //lista.clear();
                            Globales.listaPedidos.clear();
                            barra.dismiss();
                            new PromptDialog(PagoTarjetaActivity.this)
                                    .setDialogType(PromptDialog.DIALOG_TYPE_SUCCESS)
                                    .setAnimationEnable(true)
                                    .setTitleText("EXITO")
                                    .setContentText("Su pedido fue generado con éxito.")
                                    .setPositiveListener("OK", new PromptDialog.OnPositiveListener() {
                                        @Override
                                        public void onClick(PromptDialog dialog) {
                                            dialog.dismiss();
                                            Intent intent = new Intent(PagoTarjetaActivity.this, SiguiendoPedidoActivity.class);
                                            intent.putExtra("state",String.valueOf(0));
                                            intent.putExtra("empresa",laempresa);
                                            intent.putExtra("pedido",String.valueOf(codigoRegistro));
                                            startActivity(intent);
                                        }
                                    }).show();
                        }
                    }catch (JSONException e){
                        e.printStackTrace();

                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        queue.add(stringRequest);
    }

    public void actualizaCorrelativoVisa(int codi){
        String consulta = "https://apifbdelivery.fastbuych.com/Delivery/UpdateCorrelativoVisa?auth="+Globales.tokencito+"&codigo="+String.valueOf(codi);
        RequestQueue queue = Volley.newRequestQueue(PagoTarjetaActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, consulta, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(stringRequest);
    }
    */
    public void actualizaCantidadCupon(String cod, String cliente){
        int newCantidad = cantidadCupones - 1;
        String consulta = "https://apifbdelivery.fastbuych.com/Delivery/UpdateCantidadCupones?auth="+tokencito+"&codigo="+cod+"&cantidad="+newCantidad+"&cliente="+cliente+"&clienteCupon="+codClienteCupon+"&cuponPromo="+promoCupon;
        RequestQueue queue = Volley.newRequestQueue(PagoTarjetaActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, consulta, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(stringRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if (formaPago.equals("Efectivo"))
            inflater.inflate(R.menu.menupagoefectivo, menu);
        else if (formaPago.equals("Tarjeta"))
            inflater.inflate(R.menu.menupagotajeta, menu);
        else if (formaPago.equals("Yape"))
            inflater.inflate(R.menu.menupagoyape, menu);
        else if (formaPago.equals("Plin"))
            inflater.inflate(R.menu.menupagoplin, menu);
        else//5
            inflater.inflate(R.menu.menupagocodigoqr, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return false;
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        inicializaVariablesCupon();
        Intent intent = new Intent(PagoTarjetaActivity.this,CarritoActivity.class);
        startActivity(intent);
    }
}
