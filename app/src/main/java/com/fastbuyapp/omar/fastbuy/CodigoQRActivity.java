package com.fastbuyapp.omar.fastbuy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.fastbuyapp.omar.fastbuy.config.Globales;

public class CodigoQRActivity extends AppCompatActivity {

    @Override
    public void onResume() {
        super.onResume();
        //Start Logica para el pago
        double costoEnvio = Globales.montoDelivery;
        Log.v("costoEnvio2", String.format("%.1f",costoEnvio));
        double sumaTotal = Globales.montoCompra;
        final double total = costoEnvio + sumaTotal;
        Globales.montoTotal = total;

        // Iniciando variables para Mostrar datos calculados
        TextView txtSubTotal = (TextView) findViewById(R.id.txtSubTotalGeneral);
        TextView txtDeliveryTotal = (TextView) findViewById(R.id.txtDeliveryGeneral);
        TextView txtTotal = (TextView) findViewById(R.id.txtTotalGeneral);

        txtSubTotal.setText("S/"+ String.format("%.2f",sumaTotal));
        txtDeliveryTotal.setText("S/"+ String.format("%.2f",costoEnvio));
        txtTotal.setText("S/"+ String.format("%.2f",total));
        //End Logica para el pago
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_codigo_qr);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Start pop-up para seleccionar la direccion
        abrirPopUp();
        //End pop-up para seleccionar la direccion

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_chevron_left_black_24dp));

        //Start Boton Comprar
        Button btnGenerarCompra = (Button) findViewById(R.id.btnComprar);
        btnGenerarCompra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Globales.montoDelivery == 0.0)
                    abrirPopUp();
                else
                    Toast.makeText(CodigoQRActivity.this,"Generando Compra...",Toast.LENGTH_SHORT).show();
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
                Intent intent = new Intent(CodigoQRActivity.this, PrincipalActivity.class);
                startActivity(intent);
            }
        });

        btnFavoritos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CodigoQRActivity.this, FavoritosActivity.class);
                startActivity(intent);
            }
        });

        btnCarrito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CodigoQRActivity.this, CarritoActivity.class);
                startActivity(intent);
            }
        });

        btnUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CodigoQRActivity.this, UserActivity.class);
                startActivity(intent);
            }
        });
    }

    public  void abrirPopUp(){
        Intent intent = new Intent(CodigoQRActivity.this, SeleccionaDireccionActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menupagocodigoqr, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return false;
    }
}
