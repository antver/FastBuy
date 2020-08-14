package com.fastbuyapp.omar.fastbuy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;


import com.fastbuyapp.omar.fastbuy.config.Globales;
import com.fastbuyapp.omar.fastbuy.entidades.PedidoDetalle;

import java.util.ArrayList;

import cn.refactor.lib.colordialog.PromptDialog;

public class MetodoDePagoActivity extends AppCompatActivity {
    private int pago=1;
    String formaPago;
    SharedPreferences.Editor myEditor;
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_metodo_de_pago);

        //Start Diseño de popup
        DisplayMetrics medidasVentana = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(medidasVentana);

        int ancho = medidasVentana.widthPixels;
        final int alto = medidasVentana.heightPixels;

        getWindow().setLayout((int)(ancho*0.85), (int)(alto*0.50));
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //End Diseño de popup

        //Checkbox
        final CheckBox chbxEfectivo = (CheckBox) findViewById(R.id.chbxEfectivo);
        final CheckBox chbxTarjeta = (CheckBox) findViewById(R.id.chbxTarjeta);
        final CheckBox chbxYape = findViewById(R.id.chbxYape);
        final CheckBox chbxPlin = findViewById(R.id.chbxPlin);
        //final CheckBox chbxCodQR = (CheckBox) findViewById(R.id.chbxCodigoQR);

        SharedPreferences myPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        myEditor = myPreferences.edit();
        String ciudad = myPreferences.getString("City_Cliente", "");
        formaPago = myPreferences.getString("formaPago", "");
        chbxEfectivo.setChecked(false);
        chbxTarjeta.setChecked(false);
        chbxYape.setChecked(false);
        chbxPlin.setChecked(false);

        if(formaPago.equals("Efectivo")){
            chbxEfectivo.setChecked(true);
        }else{
            if(formaPago.equals("Tarjeta")){
                chbxTarjeta.setChecked(true);
            }
            else{
                if(formaPago.equals("Yape")){
                    chbxYape.setChecked(true);
                }else{
                    if(formaPago.equals("Plin")){
                        chbxPlin.setChecked(true);
                    }
                    else{
                        chbxEfectivo.setChecked(true);
                    }
                }
            }
        }
        if(ciudad.equals("Huaraz"))
        {
            chbxEfectivo.setVisibility(View.GONE);
            chbxYape.setVisibility(View.GONE);
            chbxPlin.setVisibility(View.GONE);
        }
        else{
            chbxEfectivo.setVisibility(View.VISIBLE);
            chbxYape.setVisibility(View.VISIBLE);
            chbxPlin.setVisibility(View.VISIBLE);
        }
        chbxEfectivo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chbxEfectivo.setChecked(true);
                chbxTarjeta.setChecked(false);
                chbxYape.setChecked(false);
                chbxPlin.setChecked(false);
                formaPago = "Efectivo";
                myEditor.putString("formaPago", formaPago);
                myEditor.commit();

            }
        });
        chbxTarjeta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chbxEfectivo.setChecked(false);
                chbxTarjeta.setChecked(true);
                chbxYape.setChecked(false);
                chbxPlin.setChecked(false);
                formaPago = "Tarjeta";
                myEditor.putString("formaPago", formaPago);
                myEditor.putFloat("pagarcon", 0);
                myEditor.commit();
            }
        });
        chbxYape.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chbxEfectivo.setChecked(false);
                chbxTarjeta.setChecked(false);
                chbxYape.setChecked(true);
                chbxPlin.setChecked(false);
                formaPago = "Yape";
                myEditor.putString("formaPago", formaPago);
                myEditor.putFloat("pagarcon", 0);
                myEditor.commit();
            }
        });
        chbxPlin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chbxEfectivo.setChecked(false);
                chbxTarjeta.setChecked(false);
                chbxYape.setChecked(false);
                chbxPlin.setChecked(true);
                formaPago = "Plin";
                myEditor.putString("formaPago", formaPago);
                myEditor.putFloat("pagarcon", 0);
                myEditor.commit();
            }
        });
        //Boton Pagar
        Button btnPagar = (Button) findViewById(R.id.btnPagarAhora);

        btnPagar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double montoCompra = 0;
                Globales globales = new Globales();
                ArrayList<PedidoDetalle> list = globales.getListaPedidosCache("lista_pedidos");
                for (int i = 0; i < list.size(); i++){
                    montoCompra += list.get(i).getTotal();
                }
                if (montoCompra < 10 && formaPago.equals("Tarjeta")){
                    new PromptDialog(MetodoDePagoActivity.this)
                            .setDialogType(PromptDialog.DIALOG_TYPE_INFO)
                            .setAnimationEnable(true)
                            .setTitleText("¡ATENCIÓN!")
                            .setContentText("Los pagos con tarjeta son a partir de S/ 10.00; Gracias por su comprensión.")
                            .setPositiveListener("OK", new PromptDialog.OnPositiveListener() {
                                @Override
                                public void onClick(PromptDialog dialog) {
                                    dialog.dismiss();
                                }
                            }).show();
                }else {
                    Intent intent = new Intent(MetodoDePagoActivity.this, PagoTarjetaActivity.class);
                    startActivity(intent);
                }
                if(chbxEfectivo.isChecked())
                {
                    chbxEfectivo.callOnClick();
                }
                if(chbxTarjeta.isChecked())
                {
                    chbxTarjeta.callOnClick();
                }
                if(chbxPlin.isChecked())
                {
                    chbxPlin.callOnClick();
                }
                if(chbxYape.isChecked())
                {
                    chbxYape.callOnClick();
                }
            }
        });

    }
}
