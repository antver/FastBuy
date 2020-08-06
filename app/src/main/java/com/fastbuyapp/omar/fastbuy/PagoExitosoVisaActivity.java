package com.fastbuyapp.omar.fastbuy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.fastbuyapp.omar.fastbuy.config.Globales;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.Formatter;

public class PagoExitosoVisaActivity extends AppCompatActivity {

    TextView txtEstadoPago,txtImporteVisa1,txtNumOperacionVisa, txtTitularVisa, txtNumTarjetaVisa, txtFechaVisa, txtDescripcionVisa, txtImporteVisa;
    TextView lblTitularVisa, lblNumOperacionVisa;
    Button btnreturnback;
    LottieAnimationView animacion1, animacion2;
    String statePedido, empresaPedido, numPedido;
    int cantidadRespuestas;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        final String exito = getIntent().getStringExtra("estado");
        setContentView(R.layout.activity_pago_exitoso_visa);

        statePedido = getIntent().getStringExtra("state");
        empresaPedido = getIntent().getStringExtra("empresa");
        numPedido = getIntent().getStringExtra("pedido");
        //este me ayuda a saber si es la primera vez que pide o no
        cantidadRespuestas = Integer.valueOf(getIntent().getStringExtra("cantidadRespuestas"));
        SharedPreferences myPreferences;
        myPreferences =  PreferenceManager.getDefaultSharedPreferences(this);
        String name = myPreferences.getString("Name_Cliente", "");
        //inicializar componentes
        txtEstadoPago = findViewById(R.id.txtEstadoVisa);
        txtNumOperacionVisa = findViewById(R.id.txtNumOperacionVisa);
        txtTitularVisa = findViewById(R.id.txtTitularVisa);
        txtNumTarjetaVisa = findViewById(R.id.txtNumTarjetaVisa);
        txtFechaVisa = findViewById(R.id.txtFechaVisa);
        txtDescripcionVisa = findViewById(R.id.txtDescripcionVisa);
        txtImporteVisa = findViewById(R.id.txtImporteVisa);
        btnreturnback = findViewById(R.id.btnCerrarDetallePago);
        txtImporteVisa1 = findViewById(R.id.txtImporteVisa1);

        lblNumOperacionVisa = findViewById(R.id.lblNumOperacionVisa);
        lblTitularVisa = findViewById(R.id.lblTitularVisa);

        animacion1 = findViewById(R.id.animationExitoso);
        animacion2 = findViewById(R.id.animationRechazado);

        btnreturnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(exito.equals("false")){
                    Intent intent = new Intent(PagoExitosoVisaActivity.this,PagoTarjetaActivity.class);
                    startActivity(intent);
                }else
                    onBackPressed();
            }
        });

        //String json = "{\"errorCode\":400,\"errorMessage\":\"REJECT\",\"header\":{\"ecoreTransactionUUID\":\"298c572a-d83c-40ad-9394-d5bd71a98fd8\",\"ecoreTransactionDate\":1561132464704,\"millis\":187},\"data\":{\"CURRENCY\":\"0604\",\"TRANSACTION_DATE\":\"190621105424\",\"ACTION_CODE\":\"670\",\"STATUS\":\"Reject\",\"ACTION_DESCRIPTION\":\"Operacion denegada\",\"TRACE_NUMBER\": \"0\",\"AMOUNT\":\"30.0\",\"ECI\":\"00\",\"SIGNATURE\":\"558c4dec-7923-484e-bd14-d565d8962137\",\"CARD\":\"421410******7184\",\"BRAND\":\"visa\",\"MERCHANT\":\"604410301\"}}";

        String json = Globales.mensajeVisa;
        if (exito.equals("false")){
            try {
                txtEstadoPago.setText("¡PAGO RECHAZADO!");
                controlaAnimacion(animacion2,animacion1);
                txtEstadoPago.setTextColor(getResources().getColor(R.color.rojo));
                JSONObject objeto = new JSONObject(json);
                JSONObject dataJson = objeto.getJSONObject("data");
                String estado = dataJson.getString("STATUS");
                String fecha = dataJson.getString("TRANSACTION_DATE");
                String año = fecha.substring(0, 2);
                String mes = fecha.substring(2, 4);
                String dia = fecha.substring(4, 6);
                String hora = fecha.substring(6, 8);
                String min = fecha.substring(8, 10);
                String seg = fecha.substring(10, 12);
                String fechaN = dia + "/" + mes + "/" + "20" + año + " " + hora + ":" + min + ":" + seg;
                String descripcion = dataJson.getString("ACTION_DESCRIPTION");
                String tarjeta = dataJson.getString("CARD");
                String monto = dataJson.getString("AMOUNT");
                txtNumTarjetaVisa.setText(tarjeta);
                txtFechaVisa.setText(fechaN);
                double importe = Double.valueOf(monto);
                txtImporteVisa1.setText("S/ "+String.format("%.2f",importe).replace(",","."));
                txtImporteVisa.setText("S/ "+String.format("%.2f",importe).replace(",","."));
                txtDescripcionVisa.setText(descripcion);
                txtTitularVisa.setVisibility(View.GONE);
                txtNumOperacionVisa.setVisibility(View.GONE);
                lblNumOperacionVisa.setVisibility(View.GONE);
                lblTitularVisa.setVisibility(View.GONE);
            } catch(JSONException e){
                e.printStackTrace();
                Log.v("PagoVisaRespuesta",e.toString());
            }
        }
        else if(exito.equals("true")){
            LinearLayout panelpedido = (LinearLayout) findViewById(R.id.panelpedido);
            TextView codigopedido = (TextView) findViewById(R.id.codigopedido);
            if(Globales.recoger_en_tienda){
                panelpedido.setVisibility(View.VISIBLE);
                Formatter fmt = new Formatter();
                codigopedido.setText("PEDIDO N° " + fmt.format("%06d", Integer.parseInt(numPedido)));
            }
            else{
                panelpedido.setVisibility(View.GONE);
            }
            txtEstadoPago.setText("¡PAGO EXITOSO!");
            txtEstadoPago.setTextColor(getResources().getColor(R.color.verde_fosforescente2));
            controlaAnimacion(animacion1,animacion2);
            txtTitularVisa.setVisibility(View.VISIBLE);
            txtNumOperacionVisa.setVisibility(View.VISIBLE);
            lblNumOperacionVisa.setVisibility(View.VISIBLE);
            lblTitularVisa.setVisibility(View.VISIBLE);
            JSONObject objeto = null;
            try {
                objeto = new JSONObject(json);
                JSONObject dataJson = objeto.getJSONObject("dataMap");
                String estado = dataJson.getString("STATUS");
                String fecha = dataJson.getString("TRANSACTION_DATE");
                String año = fecha.substring(0, 2);
                String mes = fecha.substring(2, 4);
                String dia = fecha.substring(4, 6);
                String hora = fecha.substring(6, 8);
                String min = fecha.substring(8, 10);
                String seg = fecha.substring(10, 12);
                String fechaN = dia + "/" + mes + "/" + "20" + año + " " + hora + ":" + min + ":" + seg;
                String descripcion = dataJson.getString("ACTION_DESCRIPTION");
                String codOperacion = dataJson.getString("TRACE_NUMBER");
                String tarjeta = dataJson.getString("CARD");
                String monto = dataJson.getString("AMOUNT");
                txtNumTarjetaVisa.setText(tarjeta);
                txtFechaVisa.setText(fechaN);
                double importe = Double.valueOf(monto);
                txtImporteVisa1.setText("S/ "+String.format("%.2f",importe).replace(",","."));
                txtImporteVisa.setText("S/ "+String.format("%.2f",importe).replace(",","."));
                txtDescripcionVisa.setText("Servicio de compra - delivery");
                txtNumOperacionVisa.setText(codOperacion);
                txtTitularVisa.setText(name.toUpperCase());

            } catch (JSONException e) {
                e.printStackTrace();
                Log.v("PagoVisaRespuesta",e.toString());
            }

        }

    }

    public void controlaAnimacion(LottieAnimationView lot1, LottieAnimationView lot2){
        lot1.playAnimation();
        lot1.setVisibility(View.VISIBLE);
        lot2.pauseAnimation();
        lot2.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Globales.recoger_en_tienda = false;
        Intent intent = new Intent(PagoExitosoVisaActivity.this, SiguiendoPedidoActivity.class);
        intent.putExtra("state",statePedido);
        intent.putExtra("empresa",empresaPedido);
        intent.putExtra("pedido",String.valueOf(numPedido));
        intent.putExtra("cantidadRespuestas",String.valueOf(cantidadRespuestas));
        startActivity(intent);
        //Intent intent = new Intent(PagoExitosoVisaActivity.this,PrincipalActivity.class);
        //startActivity(intent);
    }
}
