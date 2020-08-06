package com.fastbuyapp.omar.fastbuy;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fastbuyapp.omar.fastbuy.config.Globales;
import com.fastbuyapp.omar.fastbuy.entidades.FireBaseAnulacion;
import com.fastbuyapp.omar.fastbuy.entidades.FireBaseCancelacion;
import com.fastbuyapp.omar.fastbuy.entidades.FireBaseChat;
import com.fastbuyapp.omar.fastbuy.entidades.FireBaseConfirmacion;
import com.fastbuyapp.omar.fastbuy.entidades.FireBaseEntregado;
import com.fastbuyapp.omar.fastbuy.entidades.FireBaseFinalizado;
import com.fastbuyapp.omar.fastbuy.entidades.FireBasePreparacion;
import com.fastbuyapp.omar.fastbuy.entidades.FireBaseRepartidor;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.threatmetrix.TrustDefender.internal.A;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.Formatter;

//import java.util.Timer;
//import java.util.TimerTask;

public class SiguiendoPedidoActivity extends AppCompatActivity {

    DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference(); //hacemos referencia a la base de datos
    DatabaseReference cambioPreparacion = mDatabaseReference.child("preparacion/"); //hacemos referencia a la tabla
    DatabaseReference cambioRepartidor = mDatabaseReference.child("repartidor/"); //hacemos referencia a la tabla
    DatabaseReference cambioFinalizado = mDatabaseReference.child("finalizado/"); //hacemos referencia a la tabla
    DatabaseReference cambioAnulacion = mDatabaseReference.child("anulacion/"); //hacemos referencia a la tabla
    DatabaseReference cambioConfirmacion = mDatabaseReference.child("confirmacion/"); //hacemos referencia a la tabla
    DatabaseReference cambioCancelacion = mDatabaseReference.child("cancelacion/"); //hacemos referencia a la tabla
    DatabaseReference cambioEntregado = mDatabaseReference.child("entregado/"); //hacemos referencia a la tabla

    String statePedido,numPedido;
    String fechaPedido,horaPedido,deliveryPedido,descuentoPedido;
    TextView txtState, txtDescripState;
    TextView ind1,ind2,ind3,ind4;
    TextView line2, line3, line4;
    TextView btnIniciaChat, btnConfirmarCambio;
    LinearLayout LinearEsperando;
    Button btnConfirmaEntrega;
    LottieAnimationView animacion1, animacion2, animacion3, animacion4, animacion5, animacionEs;
    Handler mHandler;
    //Timer mTimer;
    DatabaseReference mDatabaseReferenceChat = FirebaseDatabase.getInstance().getReference(); //hacemos referencia a la base de datos
    DatabaseReference cambioChat = mDatabaseReferenceChat.child("chat/"); //hacemos referencia a la tabla
    String empresaPedido;
    String codRepartidor;
    int cantidadRespuestas;
    //SiguiendoPedidoActivity.AsyncTask_load ast;

    //Intent myService;

    ProgressDialog progDailog = null;
    ImageButton btnCarrito;

    @Override
    protected void onStart() {
        super.onStart();
        int estad = Integer.valueOf(statePedido);
        if (estad != 1 && estad != 10 && estad != 2 && estad != 9){
            if (estad != 4){
                cambioConfirmacion.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String confirmacion = dataSnapshot.getValue().toString();
                        FireBaseConfirmacion data = dataSnapshot.getValue(FireBaseConfirmacion.class);
                        String estadoPedido = data.getEstado();
                        empresaPedido = data.getEmpresa();
                        String codPedido = data.getPedido();
                        if (numPedido.equals(codPedido)){
                            validaState(Integer.valueOf(estadoPedido));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            cambioPreparacion.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String preparacion = dataSnapshot.getValue().toString();
                    FireBasePreparacion data = dataSnapshot.getValue(FireBasePreparacion.class);
                    String estadoPedido = data.getEstado();
                    String codPedido = data.getPedido();
                    if (numPedido.equals(codPedido)){
                        validaState(Integer.valueOf(estadoPedido));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            cambioRepartidor.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String repartidor = dataSnapshot.getValue().toString();
                    FireBaseRepartidor data = dataSnapshot.getValue(FireBaseRepartidor.class);
                    String codPedido = data.getPedido();
                    codRepartidor = data.getCodRepartidor();
                    if (numPedido.equals(codPedido)){
                        validaState(3);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            cambioAnulacion.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String anulacion = dataSnapshot.getValue().toString();
                    FireBaseAnulacion data = dataSnapshot.getValue(FireBaseAnulacion.class);
                    String empresa = data.getEmpresa();
                    String codPedido = data.getPedido();
                    if (numPedido.equals(codPedido)){
                        validaState(Integer.valueOf(2));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            cambioCancelacion.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String confirmacion = dataSnapshot.getValue().toString();
                    FireBaseCancelacion data = dataSnapshot.getValue(FireBaseCancelacion.class);
                    String estadoPedido = data.getEstado();
                    String codPedido = data.getPedido();
                    if (numPedido.equals(codPedido)){
                        validaState(Integer.valueOf(estadoPedido));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            /*cambioEntregado.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String finalizado = dataSnapshot.getValue().toString();
                    FireBaseEntregado data = dataSnapshot.getValue(FireBaseEntregado.class);
                    String estadoPedido = data.getEstado();
                    String codPedido = data.getPedido();
                    if (numPedido.equals(codPedido)){
                        validaState(Integer.valueOf(estadoPedido));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });*/
            /*cambioFinalizado.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String finalizado = dataSnapshot.getValue().toString();
                    FireBaseFinalizado data = dataSnapshot.getValue(FireBaseFinalizado.class);
                    String estadoPedido = data.getEstado();
                    String codPedido = data.getPedido();
                    if (numPedido.equals(codPedido)){
                        validaState(Integer.valueOf(estadoPedido));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });*/
        }

    }

    @Override
    protected void onPause() {
        /*if (Integer.valueOf(statePedido)!=2 && Integer.valueOf(statePedido)!=1)
            mTimer.cancel();*/
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Globales.valida.validarCarritoVacio(btnCarrito);

        /*if (Integer.valueOf(statePedido)!=2 && Integer.valueOf(statePedido)!=1){
            //refrescando activity
            mHandler = new Handler();
            mTimer = new Timer();
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    mHandler.post(m_Runnable);
                }
            }, 0, 2000);
        }*/

    }
    SharedPreferences myPreferences;
    String tokencito;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_siguiendo_pedido);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_chevron_left_black_24dp));
        myPreferences =  PreferenceManager.getDefaultSharedPreferences(this);
        tokencito = myPreferences.getString("tokencito", "");
        //recibiendo datos de activity anterior
        statePedido = getIntent().getStringExtra("state");
        empresaPedido = getIntent().getStringExtra("empresa");
        numPedido = getIntent().getStringExtra("pedido");
        //este me ayuda a saber si es la primera vez que pide o no
        cantidadRespuestas = Integer.valueOf(getIntent().getStringExtra("cantidadRespuestas"));
        TextView txtNroPedido = (TextView) findViewById(R.id.txtNroPedido);

        Formatter fmt = new Formatter();
        txtNroPedido.setText("PEDIDO N° " + fmt.format("%06d", Integer.parseInt(numPedido)));

        //Inicializando Componentes
        animacion1 = (LottieAnimationView) findViewById(R.id.animationPendiente);
        animacion2 = (LottieAnimationView) findViewById(R.id.animationAceptado);
        animacion3 = (LottieAnimationView) findViewById(R.id.animationEnCamino);
        animacion4 = (LottieAnimationView) findViewById(R.id.animationEntregado);
        animacion5 = (LottieAnimationView) findViewById(R.id.animationCancelado);
        animacionEs = (LottieAnimationView) findViewById(R.id.animationEsperando);

        txtState = (TextView) findViewById(R.id.txtStatePedido);
        txtDescripState = (TextView) findViewById(R.id.txtDescripcionStatePedido);
        ind1 = (TextView) findViewById(R.id.indicador1);
        ind2 = (TextView) findViewById(R.id.indicador2);
        ind3 = (TextView) findViewById(R.id.indicador3);
        ind4 = (TextView) findViewById(R.id.indicador4);
        line2 = (TextView) findViewById(R.id.lineIndicador2);
        line3 = (TextView) findViewById(R.id.lineIndicador3);
        line4 = (TextView) findViewById(R.id.lineIndicador4);
        LinearEsperando = (LinearLayout) findViewById(R.id.LinearEsperando);
        btnIniciaChat = (TextView) findViewById(R.id.btnIniciarChat);
        btnConfirmarCambio = findViewById(R.id.btnConfirmarCambio);
        btnConfirmaEntrega = findViewById(R.id.btnConfirmaEntrega);

        validaState(Integer.valueOf(statePedido));

        /*Intent myService = new Intent(SiguiendoPedidoActivity.this, BackgroundChat.class);*/
        /*if (myService == null){
            myService = new Intent(getApplicationContext(), BackgroundChat.class);
        }
        startService(myService);*/

        //ast = new AsyncTask_load();
        //ast.execute();
        /*btnCancelarPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SiguiendoPedidoActivity.this);
                builder.setMessage("Está a punto de cancelar el pedido. ¿Desea continuar?");
                builder.setTitle("Cancelar Pedido");
                builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        btnCancelarPedido.setEnabled(false);
                        //mTimer.cancel();
                        validaState(2);
                        String consulta = "https://apifbdelivery.fastbuych.com/Delivery/CancelarPedido?auth="+ Globales.tokencito+"&codigo="+numPedido+"&empresa="+empresaPedido;
                        CancelandoPedido(consulta);
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog a = builder.create();
                a.show();
            }
        });*/

        btnIniciaChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progDailog3 = new ProgressDialog(SiguiendoPedidoActivity.this);
                progDailog3.setMessage("Comprobando mensajes...");
                progDailog3.setIndeterminate(true);
                progDailog3.setCancelable(false);
                progDailog3.show();
                String URL = "https://apifbdelivery.fastbuych.com/Delivery/verificarchat?auth="+ tokencito+"&pedido="+numPedido;
                RequestQueue queue = Volley.newRequestQueue(SiguiendoPedidoActivity.this);
                StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.length() > 0) {
                            try {
                                JSONObject jo = new JSONObject(response);
                                String respuesta = jo.getString("respuesta");
                                if(respuesta.equals("SI")) {
                                    Intent intent = new Intent(SiguiendoPedidoActivity.this, ChatFastBuyActivity.class);
                                    intent.putExtra("codigo", numPedido);
                                    intent.putExtra("repartidor", codRepartidor);
                                    startActivity(intent);
                                }else{
                                    Toast toast = Toast.makeText(SiguiendoPedidoActivity.this, "Espere a que un repartidor tome su pedido.", Toast.LENGTH_SHORT);
                                    View vistaToast = toast.getView();
                                    vistaToast.setBackgroundResource(R.drawable.toast_yellow);
                                    toast.show();
                                }
                                progDailog3.dismiss();
                            } catch (JSONException e) {
                                e.printStackTrace();
                                progDailog3.dismiss();
                            }
                        }
                    }
                }, new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progDailog3.dismiss();
                    }
                });

                queue.add(stringRequest);

            }
        });

        btnConfirmarCambio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SiguiendoPedidoActivity.this, DetallePedidoConfirmar.class);
                intent.putExtra("codigo",numPedido);
                intent.putExtra("empresa",empresaPedido);
                String consulta = "https://apifbdelivery.fastbuych.com/Delivery/DatosPedido_Confirmar_XCodigo?auth="+ tokencito+"&codigo="+numPedido;
                EnviarRecibirDatos2(consulta,intent);
            }
        });

        btnConfirmaEntrega.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String consulta = "https://apifbdelivery.fastbuych.com/Delivery/ConfirmarEntrega_XPedido_XCodigo?auth="+ tokencito+"&codigo="+numPedido;
                EnviarRecibirDatos3(consulta);
            }
        });

        //Menu
        ImageButton btnHome = (ImageButton) findViewById(R.id.btnHome);
        ImageButton btnFavoritos = (ImageButton) findViewById(R.id.btnFavoritos);
        btnCarrito = (ImageButton) findViewById(R.id.btnCarrito);
        ImageButton btnUsuario = (ImageButton) findViewById(R.id.btnUsuario);
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SiguiendoPedidoActivity.this, PrincipalActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        btnFavoritos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SiguiendoPedidoActivity.this, FavoritosActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        btnCarrito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SiguiendoPedidoActivity.this, CarritoActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        btnUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    private final Runnable m_Runnable = new Runnable() {
        @Override
        public void run() {
            /*Toast toast = Toast.makeText(SiguiendoPedidoActivity.this, "Me Actualicé..."+statePedido, Toast.LENGTH_SHORT);
            View vistaToast = toast.getView();
            vistaToast.setBackgroundResource(R.drawable.toast_yellow);
            toast.show();*/
            String consulta = "https://apifbdelivery.fastbuych.com/Delivery/EstadoPedido_XCodigo?auth="+ tokencito+"&codigo="+numPedido;
            EnviarRecibirDatos(consulta);
            validaState(Integer.valueOf(statePedido));
            /*if (Integer.valueOf(statePedido)==1)
                mTimer.cancel();*/
        }
    };

    public void CancelandoPedido(String URL){
        progDailog = new ProgressDialog(SiguiendoPedidoActivity.this);
        progDailog.setMessage("Cancelando pedido...");
        progDailog.setIndeterminate(true);
        progDailog.setCancelable(false);
        progDailog.show();
        RequestQueue queue = Volley.newRequestQueue(SiguiendoPedidoActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progDailog.dismiss();
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                progDailog.dismiss();
            }
        });

        queue.add(stringRequest);
    }

    public void EnviarRecibirDatos(String URL){
        RequestQueue queue = Volley.newRequestQueue(SiguiendoPedidoActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.length() > 0) {
                    try {
                        JSONObject jo = new JSONObject(response);
                        statePedido = jo.getString("PED_Estado");
                    } catch (JSONException e) {
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

    public void EnviarRecibirDatos2(String URL, final Intent intent){
        RequestQueue queue = Volley.newRequestQueue(SiguiendoPedidoActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.length() > 0) {
                    try {
                        JSONObject jo = new JSONObject(response);
                        fechaPedido = jo.getString("PED_Fecha");
                        horaPedido = jo.getString("PED_Hora");
                        deliveryPedido = jo.getString("PED_Delivery");
                        descuentoPedido = jo.getString("PED_Descuento");

                        intent.putExtra("Fecha",fechaPedido);
                        intent.putExtra("Hora",horaPedido);
                        intent.putExtra("Delivery",deliveryPedido);
                        intent.putExtra("Descuento",descuentoPedido);
                        startActivity(intent);
                    } catch (JSONException e) {
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

    public void EnviarRecibirDatos3(String URL){
        RequestQueue queue = Volley.newRequestQueue(SiguiendoPedidoActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.length() > 0) {
                    try {
                        JSONObject jo = new JSONObject(response);
                        statePedido = jo.getString("PED_Atendido");
                        validaState(Integer.valueOf(statePedido));
                    } catch (JSONException e) {
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

    public void miEncuesta(){
        if(cantidadRespuestas < 1){
            Intent intentEncuesta = new Intent(SiguiendoPedidoActivity.this, EncuestaFastBuyActivity.class);
            startActivity(intentEncuesta);
        }
    }

    public void validaState(int estado){
        if (estado == 0){
            controlaAnimacion(animacion1, animacion2, animacion3, animacion4, animacion5);
            txtState.setText("Pendiente");
            txtDescripState.setText("Hemos enviado tu orden, espera que el establecimiento lo acepte");
            activaCirculo(ind1);
            desactivaLinea(line2);
            desactivaCirculo(ind2);
            desactivaLinea(line3);
            desactivaCirculo(ind3);
            desactivaLinea(line4);
            desactivaCirculo(ind4);
            muestraEspera(LinearEsperando,animacionEs);
            desactivaBoton(btnIniciaChat);
            desactivaBoton(btnConfirmarCambio);
            desactivaBoton(btnConfirmaEntrega);
        }else if (estado == 4){
            controlaAnimacion(animacion2, animacion1, animacion3, animacion4, animacion5);
            txtState.setText("Aceptado");
            txtDescripState.setText("Tu orden fue aceptada, estamos preparando tu pedido");
            activaCirculo(ind1);
            activaLinea(line2);
            activaCirculo(ind2);
            desactivaLinea(line3);
            desactivaCirculo(ind3);
            desactivaLinea(line4);
            desactivaCirculo(ind4);
            ocultaEspera(LinearEsperando,animacionEs);
            activaBoton(btnIniciaChat);
            desactivaBoton(btnConfirmarCambio);
            desactivaBoton(btnConfirmaEntrega);
        }else if (estado == 3){
            controlaAnimacion(animacion3, animacion2, animacion1, animacion4, animacion5);
            txtState.setText("En Camino");
            txtDescripState.setText("Nuestro colaborador está en camino");
            activaCirculo(ind1);
            activaLinea(line2);
            activaCirculo(ind2);
            activaLinea(line3);
            activaCirculo(ind3);
            desactivaLinea(line4);
            desactivaCirculo(ind4);
            ocultaEspera(LinearEsperando,animacionEs);
            activaBoton(btnIniciaChat);
            desactivaBoton(btnConfirmarCambio);
            activaBoton(btnConfirmaEntrega);
        }else if (estado == 1){
            controlaAnimacion(animacion4, animacion3, animacion2, animacion1, animacion5);
            txtState.setText("Entregado");
            txtDescripState.setText("Tu pedido fue entregado con éxito, gracias por usar FastBuy");
            activaCirculo(ind1);
            activaLinea(line2);
            activaCirculo(ind2);
            activaLinea(line3);
            activaCirculo(ind3);
            activaLinea(line4);
            activaCirculo(ind4);
            ocultaEspera(LinearEsperando,animacionEs);
            desactivaBoton(btnIniciaChat);
            desactivaBoton(btnConfirmarCambio);
            desactivaBoton(btnConfirmaEntrega);
        }else if (estado == 2){
            controlaAnimacion(animacion5, animacion4, animacion3, animacion2, animacion1);
            txtState.setText("Cancelado");
            txtDescripState.setText("Tu orden fue cancelada");
            circuloGris(ind1);
            lineaGris(line2);
            circuloGris(ind2);
            lineaGris(line3);
            circuloGris(ind3);
            lineaGris(line4);
            circuloGris(ind4);
            ocultaEspera(LinearEsperando,animacionEs);
            desactivaBoton(btnIniciaChat);
            desactivaBoton(btnConfirmarCambio);
            desactivaBoton(btnConfirmaEntrega);
        }else if (estado == 7){
            controlaAnimacion(animacion2, animacion1, animacion3, animacion4, animacion5);
            txtState.setText("Confirmación");
            txtDescripState.setText("Tu pedido necesita ser confirmado");
            activaCirculo(ind1);
            activaLinea(line2);
            activaCirculo(ind2);
            desactivaLinea(line3);
            desactivaCirculo(ind3);
            desactivaLinea(line4);
            desactivaCirculo(ind4);
            ocultaEspera(LinearEsperando,animacionEs);
            desactivaBoton(btnIniciaChat);
            activaBoton(btnConfirmarCambio);
            desactivaBoton(btnConfirmaEntrega);
        }else if (estado == 9){
            controlaAnimacion(animacion5, animacion1, animacion3, animacion4, animacion5);
            txtState.setText("Cancelado");
            txtDescripState.setText("Nos comunicaremos contigo para realizar el reembolso");
            activaCirculo(ind1);
            activaLinea(line2);
            activaCirculo(ind2);
            desactivaLinea(line3);
            desactivaCirculo(ind3);
            desactivaLinea(line4);
            desactivaCirculo(ind4);
            ocultaEspera(LinearEsperando,animacionEs);
            desactivaBoton(btnIniciaChat);
            desactivaBoton(btnConfirmarCambio);
            desactivaBoton(btnConfirmaEntrega);
        }else if (estado == 10){
            controlaAnimacion(animacion4, animacion3, animacion2, animacion1, animacion5);
            txtState.setText("Entregado");
            txtDescripState.setText("Tu pedido fue entregado con éxito, gracias por usar FastBuy");
            activaCirculo(ind1);
            activaLinea(line2);
            activaCirculo(ind2);
            activaLinea(line3);
            activaCirculo(ind3);
            activaLinea(line4);
            activaCirculo(ind4);
            ocultaEspera(LinearEsperando,animacionEs);
            desactivaBoton(btnIniciaChat);
            desactivaBoton(btnConfirmarCambio);
            desactivaBoton(btnConfirmaEntrega);
        }

        miEncuesta();
    }

    public void controlaAnimacion(LottieAnimationView lot1, LottieAnimationView lot2,
                                  LottieAnimationView lot3, LottieAnimationView lot4,
                                  LottieAnimationView lot5){
        lot1.playAnimation();
        lot1.setVisibility(View.VISIBLE);
        lot2.pauseAnimation();
        lot2.setVisibility(View.GONE);
        lot3.pauseAnimation();
        lot3.setVisibility(View.GONE);
        lot4.pauseAnimation();
        lot4.setVisibility(View.GONE);
        lot5.pauseAnimation();
        lot5.setVisibility(View.GONE);
    }

    public void activaCirculo(TextView txt){
        txt.setBackgroundResource(R.drawable.boton_blanco_verde_circulo);
        txt.setTextColor(txt.getContext().getResources().getColor(R.color.fastbuy));
    }

    public void desactivaCirculo(TextView txt){
        txt.setBackgroundResource(R.drawable.boton_blanco_amarillo_circulo);
        txt.setTextColor(txt.getContext().getResources().getColor(R.color.amarillo));
    }

    public void activaLinea(TextView txt){
        txt.setBackgroundResource(R.drawable.linea_verde_fastbuy);
    }

    public void desactivaLinea(TextView txt){
        txt.setBackgroundResource(R.drawable.linea_amarilla);
    }

    public void circuloGris(TextView txt){
        txt.setBackgroundResource(R.drawable.boton_blanco_gris_circulo);
        txt.setTextColor(txt.getContext().getResources().getColor(R.color.humo_gris));
    }

    public void lineaGris(TextView txt){
        txt.setBackgroundResource(R.drawable.linea_gris);
    }

    public void activaBoton(TextView btn){
        btn.setVisibility(View.VISIBLE);
        btn.setEnabled(true);
    }

    public void desactivaBoton(TextView btn){
        btn.setVisibility(View.GONE);
        btn.setEnabled(false);
    }

    public void muestraEspera(LinearLayout espera,LottieAnimationView espAnimation){
        espAnimation.playAnimation();
        espera.setVisibility(View.VISIBLE);
    }

    public void ocultaEspera(LinearLayout espera, LottieAnimationView espAnimation){
        espAnimation.pauseAnimation();
        espera.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_siguiendo_pedido, menu);
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
        Intent intent = new Intent(SiguiendoPedidoActivity.this,HistorialPedidosActivity.class);
        intent.putExtra("tipo","pedido");
        startActivity(intent);
    }


}
