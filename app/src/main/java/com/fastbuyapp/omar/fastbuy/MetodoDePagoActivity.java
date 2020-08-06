package com.fastbuyapp.omar.fastbuy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fastbuyapp.omar.fastbuy.config.Globales;

import cn.refactor.lib.colordialog.PromptDialog;

public class MetodoDePagoActivity extends AppCompatActivity {
    private int pago=1;
    String formaPago;
    SharedPreferences.Editor myEditor;
    @Override
    protected void onResume() {
        super.onResume();
        //Globales.tipoPago = 1;
        //Globales.formaPago = "Efectivo";
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
        //getWindow().setGravity(Gravity.LEFT);
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
                //chbxCodQR.setChecked(false);
                //pago = 1;
                //Globales.tipoPago = pago;
                formaPago = "Efectivo";
                myEditor.putString("formaPago", formaPago);

            }
        });
        chbxTarjeta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chbxEfectivo.setChecked(false);
                chbxTarjeta.setChecked(true);
                chbxYape.setChecked(false);
                chbxPlin.setChecked(false);
                //chbxCodQR.setChecked(false);
                //pago = 2;
                //Globales.tipoPago = pago;
                formaPago = "Tarjeta";
                myEditor.putString("formaPago", formaPago);
                Globales.pagarcon = "0";
            }
        });
        chbxYape.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chbxEfectivo.setChecked(false);
                chbxTarjeta.setChecked(false);
                chbxYape.setChecked(true);
                chbxPlin.setChecked(false);
                //chbxCodQR.setChecked(false);
                //pago = 3;
                //Globales.tipoPago = pago;
                formaPago = "Yape";
                myEditor.putString("formaPago", formaPago);
                Globales.pagarcon = "0";
            }
        });
        chbxPlin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chbxEfectivo.setChecked(false);
                chbxTarjeta.setChecked(false);
                chbxYape.setChecked(false);
                chbxPlin.setChecked(true);
                //chbxCodQR.setChecked(false);
                //pago = 4;
                //Globales.tipoPago = pago;
                formaPago = "Plin";
                myEditor.putString("formaPago", formaPago);
                Globales.pagarcon = "0";
            }
        });
        /*chbxCodQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chbxEfectivo.setChecked(false);
                chbxTarjeta.setChecked(false);
                chbxCodQR.setChecked(true);
                pago = 5;
                Globales.formaPago = "Código QR";
            }
        });*/

        //Boton Pagar
        Button btnPagar = (Button) findViewById(R.id.btnPagarAhora);

        btnPagar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Globales.montoDescuento = 0;
                Globales.montoCargo = 0;
                if (Globales.montoCompra < 10 && formaPago.equals("Tarjeta")){
                    new PromptDialog(MetodoDePagoActivity.this)
                            .setDialogType(PromptDialog.DIALOG_TYPE_INFO)
                            .setAnimationEnable(true)
                            .setTitleText("¡ATENCIÓN!")
                            .setContentText("Solo puede pagar con Tarjeta montos mayores o iguales a S/ 10.00; Muchas Gracias por su comprensión.")
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
            }
        });

    }
}
