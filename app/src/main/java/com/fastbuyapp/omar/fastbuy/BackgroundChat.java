package com.fastbuyapp.omar.fastbuy;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationCompat.BigTextStyle;

import com.fastbuyapp.omar.fastbuy.Operaciones.Calcular_Minutos;
import com.fastbuyapp.omar.fastbuy.config.Globales;
import com.fastbuyapp.omar.fastbuy.entidades.Categoria;
import com.fastbuyapp.omar.fastbuy.entidades.Empresa;
import com.fastbuyapp.omar.fastbuy.entidades.FireBaseChat;
import com.fastbuyapp.omar.fastbuy.entidades.FireBaseCupon;
import com.fastbuyapp.omar.fastbuy.entidades.FireBasePromo;
import com.fastbuyapp.omar.fastbuy.entidades.Promocion;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.threatmetrix.TrustDefender.internal.S;

import org.json.JSONException;
import org.json.JSONObject;

public class BackgroundChat extends Service {

    private static final String TAG = "BackgroundSoundService";
    //MediaPlayer player;
    DatabaseReference nDatabaseReference = FirebaseDatabase.getInstance().getReference();
    DatabaseReference cambioChat = nDatabaseReference.child("chat/");
    DatabaseReference recibePromo = nDatabaseReference.child("promocion/");
    DatabaseReference recibeCupon = nDatabaseReference.child("cupon/");
    int notificador=0,notificador2=0, notificador3=0;
    SharedPreferences myPreferences;
    BackgroundChat.AsyncTask_load ast;
    public IBinder onBind(Intent arg0) {
        Log.i(TAG, "onBind()" );
        return null;
    }

    //esto lo añadió luis buscando una solucion a la excepcion
    private AsyncTask_load createAsyncTask(){
        if (ast == null){
            return ast = new AsyncTask_load();
        }
        ast.cancel(true);
        return ast = new AsyncTask_load();
    }
    //fin de lo que añadio

    @Override
    public void onCreate() {
        super.onCreate();
        myPreferences =  PreferenceManager.getDefaultSharedPreferences(BackgroundChat.this);
        //ast = new AsyncTask_load();//esto comento luis buscando una solucion a la excepcion
    }
    public int onStartCommand(Intent intent, int flags, int startId) {
        //ast.execute();//esto comento luis buscando una solucion a la excepcion
        createAsyncTask().execute();//esto lo añadió luis buscando una solucion a la excepcion
        return Service.START_STICKY;
    }

    public IBinder onUnBind(Intent arg0) {
        Log.i(TAG, "onUnBind()");
        return null;
    }

    public void onStop() {
        Log.i(TAG, "onStop()");
    }
    public void onPause() {
        Log.i(TAG, "onPause()");
    }
    @Override
    public void onDestroy() {
        //ast.execute();//esto comento luis buscando una solucion a la excepcion
        createAsyncTask().execute();//esto lo añadió luis buscando una solucion a la excepcion
    }

    @Override
    public void onLowMemory() {
        Log.i(TAG, "onLowMemory()");
    }

    public class AsyncTask_load extends AsyncTask<Void, Integer, Boolean> {

        private Context context;
        @Override
        protected Boolean doInBackground(Void... voids) {
            cambioChat.addValueEventListener(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    notificador++;
                    if(notificador > 2) {
                        FireBaseChat data = dataSnapshot.getValue(FireBaseChat.class);
                        String codusuario = data.getCodusuario();
                        String codigoRepartidor = data.getIdrepartidor();
                        String mensaje = data.getMensaje();
                        String codped = data.getPedido();
                        String celular = myPreferences.getString("Number_Cliente", "unknown");
                        if(codusuario.equals(celular)){
                            String channelId  = "chat_cliente";
                            String channelName = "chat";
                            NotificationChannel androidChannel = null;

                            Uri soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"+ getApplicationContext().getPackageName() + "/" + R.raw.notify);
                            NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                            int icono = R.drawable.logo_fastbuy_2;
                            Intent intent = new Intent(getApplicationContext(), ChatFastBuyActivity.class);
                            intent.putExtra("codigo", codped);
                            intent.putExtra("repartidor", codigoRepartidor);
                            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                androidChannel= new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
                                androidChannel.enableLights(true);
                                androidChannel.enableVibration(true);
                                androidChannel.setLightColor(Color.GREEN);
                                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                                        .build();

                                androidChannel.setVibrationPattern((new long[]{Notification.DEFAULT_VIBRATE}));
                                androidChannel.setSound(soundUri,audioAttributes);

                                if (nm != null) {
                                    nm.createNotificationChannel( androidChannel );
                                    Notification.Builder builder = new Notification.Builder(getApplicationContext(), "chat_cliente")
                                            .setContentIntent(pendingIntent)
                                            .setSmallIcon(icono)
                                            .setContentTitle("FastBuy")
                                            .setContentText("Repartidor: " + mensaje)
                                            .setAutoCancel(true)
                                            .setShowWhen(true);

                                    nm.notify(0, builder.build());//notificationBuilder.setSound(Uri.parse("android.resource://" + getPackageame() + "/" + R.raw.audio));
                                }
                            } else {
                                NotificationCompat.Builder mBuilder;
                                NotificationManager mNotifyMgr = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);

                                mBuilder = new NotificationCompat.Builder(getApplicationContext())
                                        .setContentIntent(pendingIntent)
                                        .setSmallIcon(icono)
                                        .setContentTitle("FastBuy")
                                        .setContentText("Repartidor: " + mensaje)
                                        .setShowWhen(true)
                                        .setVibrate(new long[]{100, 250, 100, 500})
                                        .setAutoCancel(true)
                                        .setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.notify));

                                mNotifyMgr.notify(0, mBuilder.build());
                            }
                        }
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            recibePromo.addValueEventListener(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    notificador2++;
                    if(notificador2 > 2) {
                        FireBasePromo prom = dataSnapshot.getValue(FireBasePromo.class);
                        String mensajePromo = prom.getMensaje();
                        String contenidoPromo = prom.getContenido();
                        String ciudadPromo = prom.getCiudad();
                        String ciudad = myPreferences.getString("City_Cliente", "");
                        if(ciudadPromo.equals(ciudad)){

                            String[] items = contenidoPromo.split("##F_B##");

                            Promocion promocion = new Promocion();
                            promocion.setCodigo(Integer.valueOf(items[0]));
                            promocion.setDescripcion(items[1]);
                            promocion.setPrecio(items[2]);
                            promocion.setImagen(items[3]);
                            promocion.setEstado(Integer.valueOf(items[4]));
                            Categoria categoria = new Categoria();
                            categoria.setDescripcion(items[5]);
                            promocion.setCategoria(categoria);
                            Empresa empresa = new Empresa();
                            empresa.setCodigo(Integer.valueOf(items[6]));
                            empresa.setNombreComercial(items[7]);
                            empresa.setLongitud(Double.valueOf(items[8]));
                            empresa.setLatitud(Double.valueOf(items[9]));
                            promocion.setEmpresa(empresa);
                            Calcular_Minutos calcula = new Calcular_Minutos();
                            promocion.setTiempo(calcula.ObtenMinutos(items[10]));

                            Globales.PromocionPersonalizar = promocion;

                            String MensajeLargo = mensajePromo;
                            String channelId  = "promo_cliente";
                            String channelName = "promocion";
                            NotificationChannel androidChannel = null;

                            Uri soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"+ getApplicationContext().getPackageName() + "/" + R.raw.notify);
                            NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                            int icono = R.drawable.logo_fastbuy_2;
                            Intent intent = new Intent(getApplicationContext(), PersonalizaPromoActivity.class);
                            PendingIntent pendingIntent2 = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                androidChannel= new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
                                androidChannel.enableLights(true);
                                androidChannel.enableVibration(true);
                                androidChannel.setLightColor(Color.GREEN);
                                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                                        .build();

                                androidChannel.setVibrationPattern((new long[]{Notification.DEFAULT_VIBRATE}));
                                androidChannel.setSound(soundUri,audioAttributes);

                                if (nm != null) {
                                    nm.createNotificationChannel( androidChannel );
                                    Notification.Builder builder = new Notification.Builder(getApplicationContext(), "promo_cliente")
                                            .setContentIntent(pendingIntent2)
                                            .setSmallIcon(icono)
                                            .setContentTitle("FastBuy")
                                            .setContentText(MensajeLargo)
                                            .setStyle(new Notification.BigTextStyle().bigText(MensajeLargo))
                                            .setAutoCancel(true)
                                            .setShowWhen(true);

                                    nm.notify(0, builder.build());//notificationBuilder.setSound(Uri.parse("android.resource://" + getPackageame() + "/" + R.raw.audio));
                                }
                            } else {
                                NotificationCompat.Builder mBuilder;
                                NotificationManager mNotifyMgr = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);

                                mBuilder = new NotificationCompat.Builder(getApplicationContext())
                                        .setContentIntent(pendingIntent2)
                                        .setSmallIcon(icono)
                                        .setContentTitle("FastBuy")
                                        .setContentText(MensajeLargo)
                                        .setStyle(new BigTextStyle().bigText(MensajeLargo))
                                        .setShowWhen(true)
                                        .setVibrate(new long[]{100, 250, 100, 500})
                                        .setAutoCancel(true)
                                        .setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.notify));

                                mNotifyMgr.notify(0, mBuilder.build());
                            }
                        }
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            recibeCupon.addValueEventListener(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    notificador3++;
                    if(notificador3 > 2) {
                        FireBaseCupon cup = dataSnapshot.getValue(FireBaseCupon.class);
                        String telefonousuario = cup.getTelefonousuario();
                        String codcupon = cup.getCodcupon();
                        String cantidad = cup.getCantidad();
                        String celular = myPreferences.getString("Number_Cliente", "unknown");
                        if(telefonousuario.equals(celular)){

                            String channelId  = "cupon_cliente";
                            String channelName = "cupon";
                            NotificationChannel androidChannel = null;

                            Uri soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"+ getApplicationContext().getPackageName() + "/" + R.raw.notify);
                            NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                            int icono = R.drawable.logo_fastbuy_2;
                            Intent intent = new Intent(getApplicationContext(), MisSaldosActivity.class);
                            PendingIntent pendingIntent2 = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                androidChannel= new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
                                androidChannel.enableLights(true);
                                androidChannel.enableVibration(true);
                                androidChannel.setLightColor(Color.GREEN);
                                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                                        .build();

                                androidChannel.setVibrationPattern((new long[]{Notification.DEFAULT_VIBRATE}));
                                androidChannel.setSound(soundUri,audioAttributes);

                                if (nm != null) {
                                    nm.createNotificationChannel( androidChannel );
                                    Notification.Builder builder = new Notification.Builder(getApplicationContext(), "cupon_cliente")
                                            .setContentIntent(pendingIntent2)
                                            .setSmallIcon(icono)
                                            .setContentTitle("FastBuy")
                                            .setContentText("Ganaste un Cupón: "+codcupon+" con "+cantidad+" unidad(es).")
                                            .setAutoCancel(true)
                                            .setShowWhen(true);

                                    nm.notify(0, builder.build());//notificationBuilder.setSound(Uri.parse("android.resource://" + getPackageame() + "/" + R.raw.audio));
                                }
                            } else {
                                NotificationCompat.Builder mBuilder;
                                NotificationManager mNotifyMgr = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);

                                mBuilder = new NotificationCompat.Builder(getApplicationContext())
                                        .setContentIntent(pendingIntent2)
                                        .setSmallIcon(icono)
                                        .setContentTitle("FastBuy")
                                        .setContentText("Ganaste un Cupón: "+codcupon+" con "+cantidad+" unidad(es).")
                                        .setShowWhen(true)
                                        .setVibrate(new long[]{100, 250, 100, 500})
                                        .setAutoCancel(true)
                                        .setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.notify));

                                mNotifyMgr.notify(0, mBuilder.build());
                            }
                        }
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            return true;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }
}

