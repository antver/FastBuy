package com.fastbuyapp.omar.fastbuy;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.fastbuyapp.omar.fastbuy.adaptadores.MensajesChatAdapter;
import com.fastbuyapp.omar.fastbuy.config.GlideApp;
import com.fastbuyapp.omar.fastbuy.config.Globales;
import com.fastbuyapp.omar.fastbuy.config.Servidor;
import com.fastbuyapp.omar.fastbuy.entidades.FireBaseChat;
import com.fastbuyapp.omar.fastbuy.entidades.MensajeChat;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChatFastBuyActivity extends AppCompatActivity {

    DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference(); //hacemos referencia a la base de datos
    DatabaseReference cambioChat = mDatabaseReference.child("chat/"); //hacemos referencia a la tabla

    ImageView imgRepartidor; //por ahora no lo usamos
    TextView NumeroPedido, NameRepartidor;
    GridView gvMensajes;
    EditText txtMensajeEnviar;
    ImageButton btnEnviarMensaje, btnRegresar;

    String CodigoPedido;
    String codCliente;
    String id, repartidor,idrepartidor, mensajeChat, fecha, hora;

    ArrayList<MensajeChat> mensajesList;
    MensajesChatAdapter adapter;
    String tokencito;
    int numMensaje = 0;

    @Override
    protected void onResume() {
        super.onResume();
        cambioChat.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                FireBaseChat data = dataSnapshot.getValue(FireBaseChat.class);

                String codPedido = data.getPedido();
                if (CodigoPedido.equals(codPedido)){

                    codCliente = data.getCodusuario();
                    id = data.getId();
                    idrepartidor = data.getIdrepartidor();
                    mensajeChat = data.getMensaje();
                    fecha = data.getFecha();
                    hora = data.getHora();
                    repartidor = data.getRepartidor();

                    /*try {
                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                        r.play();
                    } catch (Exception e) { e.printStackTrace(); }*/

                    muestraRepartidor();
                    numMensaje++;
                    if (numMensaje > 1){
                        MensajeChat mensaje = new MensajeChat();
                        mensaje.setMensaje(mensajeChat);
                        mensaje.setFecha(fecha);
                        mensaje.setHora(hora);
                        mensaje.setOrigen("REPARTIDOR");
                        mensajesList.add(mensaje);
                        adapter.notifyDataSetChanged();
                        gvMensajes.setAdapter(adapter);
                        //gvMensajes.setSelection(mensajesList.size() - 1);
                        gvMensajes.setTranscriptMode(GridView.TRANSCRIPT_MODE_NORMAL);
                        gvMensajes.setStackFromBottom(true);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_fast_buy);

        //acá debo de recibir el codigo del pedido y el código del repartidor
        CodigoPedido = getIntent().getStringExtra("codigo");
        idrepartidor = getIntent().getStringExtra("repartidor");
        mensajesList = new ArrayList<>();

        //Inicializamos componentes
        NumeroPedido = findViewById(R.id.txtNumPedido);
        NameRepartidor = findViewById(R.id.txtNombreRepartidor);
        txtMensajeEnviar = findViewById(R.id.txtMensajeEnviarChat);
        btnEnviarMensaje = findViewById(R.id.btnEnviarMensaje);
        btnRegresar = findViewById(R.id.btnReturn);
        imgRepartidor = findViewById(R.id.ivFotoRepartidor);
        gvMensajes = findViewById(R.id.gvMensajes);

        Servidor s = new Servidor();
        String photoUser = "https://"+s.getServidor()+"/imagenes/agentesreparto/r" + idrepartidor+".jpg";

        GlideApp.with(ChatFastBuyActivity.this)
                .load(photoUser)
                .error(R.drawable.user_image)
                .fitCenter()
                .transform(new CircleCrop())
                .into(imgRepartidor);
        try {
            adapter = new MensajesChatAdapter(ChatFastBuyActivity.this, R.layout.item_mensaje_usuario, R.layout.item_mensaje_repartidor, mensajesList, Integer.valueOf(idrepartidor));
        }catch (Exception ex){}
        gvMensajes.setAdapter(adapter);

        cargarChat(CodigoPedido, idrepartidor);

        btnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        NumeroPedido.setText("Pedido Nº "+CodigoPedido);

        //para que el boton enviar solo se muestre si hay texto en la caja
        txtMensajeEnviar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (txtMensajeEnviar.getText().toString().trim().length()>0)
                    btnEnviarMensaje.setVisibility(View.VISIBLE);
                else
                    btnEnviarMensaje.setVisibility(View.GONE);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (txtMensajeEnviar.getText().toString().trim().length()>0)
                    btnEnviarMensaje.setVisibility(View.VISIBLE);
                else
                    btnEnviarMensaje.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (txtMensajeEnviar.getText().toString().trim().length()>0)
                    btnEnviarMensaje.setVisibility(View.VISIBLE);
                else
                    btnEnviarMensaje.setVisibility(View.GONE);
            }
        });

        btnEnviarMensaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtMensajeEnviar.getText().toString().trim().length() == 0){
                    return;
                }
                Date fecha = new Date();
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat hourFormat = new SimpleDateFormat("HH:mm a");
                String fech = dateFormat.format(fecha);
                String hour = hourFormat.format(fecha);
                String fechaHoy = fech +" "+hour;
                String mensajitoEnviar = txtMensajeEnviar.getText().toString();
                String origenEnviar = "USUARIO";

                final MensajeChat mensa = new MensajeChat();
                mensa.setMensaje(mensajitoEnviar);
                mensa.setFecha(fech);
                mensa.setHora(hour);
                mensa.setOrigen(origenEnviar);

                txtMensajeEnviar.setText("");

                String msg = URLEncoder.encode(mensajitoEnviar.toString());
                String fechita = URLEncoder.encode(fechaHoy.toString());
                SharedPreferences myPreferences;
                myPreferences =  PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                tokencito = myPreferences.getString("tokencito", "");
                String consulta = "https://apifbdelivery.fastbuych.com/Delivery/InsertarMensaje_XChat?auth="+tokencito+"&codigo="+id+"&mensaje="+msg+"&fecha="+fechita+"&origen="+origenEnviar+"&cliente="+codCliente+"&repartidor="+idrepartidor+"&pedido="+CodigoPedido;
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                StringRequest stringRequest = new StringRequest(Request.Method.GET, consulta, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.length() > 0) {
                            mensajesList.add(mensa);
                            adapter.notifyDataSetChanged();
                            gvMensajes.setAdapter(adapter);
                            gvMensajes.setTranscriptMode(GridView.TRANSCRIPT_MODE_NORMAL);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

                queue.add(stringRequest);
            }
        });

    }

    public void muestraRepartidor()
    {
        NameRepartidor.setText(repartidor);
    }

    public void cargarChat(String pedido, String _idrepartidor){
        //Toast.makeText(ChatFastBuyActivity.this,"e ingresado "+idrepartidor,Toast.LENGTH_LONG).show();
        String consulta = "https://apifbdelivery.fastbuych.com/Delivery/ObtenChat_XPedido_XRepartidor?auth="+ tokencito+"&codigo="+pedido+"&repartidor="+ _idrepartidor;
        RequestQueue queue = Volley.newRequestQueue(ChatFastBuyActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, consulta, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.length() > 0) {
                    try {
                        JSONArray array = new JSONArray(response);
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            codCliente = object.getString("CLI_Codigo");
                            id = object.getString("CRC_Codigo");
                            MensajeChat mensaje = new MensajeChat();
                            mensaje.setMensaje(object.getString("MRC_Mensaje"));
                            mensaje.setOrigen(object.getString("MRC_Envia"));
                            mensaje.setFecha(object.getString("MRC_FechaCrea"));
                            mensaje.setHora(object.getString("MRC_HoraCrea"));
                            mensajesList.add(mensaje);
                        }
                        //adapter = new MensajesChatAdapter(ChatFastBuyActivity.this,R.layout.item_mensaje_usuario,R.layout.item_mensaje_repartidor , mensajesList);
                        adapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        queue.add(stringRequest);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent intent = new Intent(ChatFastBuyActivity.this, SiguiendoPedidoActivity.class);
        startActivity(intent);
    }
}
