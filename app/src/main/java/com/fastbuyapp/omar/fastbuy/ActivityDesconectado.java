package com.fastbuyapp.omar.fastbuy;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Handler;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.fastbuyapp.omar.fastbuy.Validaciones.ValidacionDatos;
import com.fastbuyapp.omar.fastbuy.config.Globales;

public class ActivityDesconectado extends AppCompatActivity {
    private SwipeRefreshLayout swipeLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_desconectado);
        Typeface typefaceNexa = Typeface.createFromAsset(getAssets(), "fonts/NEXABOLD.otf");
        Typeface typefaceGothic = Typeface.createFromAsset(getAssets(), "fonts/GOTHIC.ttf");
        TextView txtLoSentimos = (TextView) findViewById(R.id.txtLoSentimos);
        TextView txtComprueba = (TextView) findViewById(R.id.txtComprueba);
        txtLoSentimos.setTypeface(typefaceNexa);
        txtComprueba.setTypeface(typefaceGothic);
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //Aqui ejecutamos el codigo necesario para refrescar nuestra interfaz grafica.
                //Antes de ejecutarlo, indicamos al swipe layout que muestre la barra indeterminada de progreso.
                swipeLayout.setRefreshing(true);
                //Vamos a simular un refresco con un handle.
                ValidacionDatos validacion = new ValidacionDatos();
                if(validacion.hayConexiónRed(ActivityDesconectado.this)){
                    //Intent intent=new Intent(ActivityDesconectado.this, OpcionLoginActivity.class);
                    Intent intent=new Intent(ActivityDesconectado.this, SplashActivity.class);
                    startActivity(intent);
                    finish();
                }
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        swipeLayout.setRefreshing(false);
                    }
                }, 3000);

            }

        });

    }

    @SuppressLint("NewApi")
    public static final void recreateActivityCompat(final Activity a) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            a.recreate();
        } else {
            final Intent intent = a.getIntent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            a.finish();
            a.overridePendingTransition(0, 0);
            a.startActivity(intent);
            a.overridePendingTransition(0, 0);
        }
    }
}
