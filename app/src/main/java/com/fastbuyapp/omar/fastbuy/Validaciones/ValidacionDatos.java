package com.fastbuyapp.omar.fastbuy.Validaciones;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.fastbuyapp.omar.fastbuy.R;
import com.fastbuyapp.omar.fastbuy.config.Globales;
import com.fastbuyapp.omar.fastbuy.entidades.Pedido;
import com.fastbuyapp.omar.fastbuy.entidades.PedidoDetalle;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by OMAR on 17/03/2019.
 */
/**
 * Edited by Luis Ysla on 31/01/2020.
 */

public class ValidacionDatos {

    public ValidacionDatos(){

    }

    public boolean validarCelular(TextView txtCelular){
        boolean correcto = false;
        if(txtCelular.getText().toString().isEmpty()){
            correcto = false;
        }else {
            if (txtCelular.getText().toString().substring(0, 1).equals("9")) {
                if (txtCelular.getText().toString().trim().length() == 9) {
                    correcto = true;
                } else {
                    correcto = false;
                }
            } else if (txtCelular.getText().toString().substring(0, 1).equals("0")) {
                if (txtCelular.getText().toString().trim().length() == 9) {
                    correcto = true;
                } else {
                    correcto = false;
                }
            }  else if (txtCelular.getText().toString().equals("000000000")) {
                correcto = false;
            } else if (txtCelular.getText().toString().equals("111111111")) {
                correcto = false;
            } else if (txtCelular.getText().toString().equals("222222222")) {
                correcto = false;
            } else if (txtCelular.getText().toString().equals("333333333")) {
                correcto = false;
            } else if (txtCelular.getText().toString().equals("444444444")) {
                correcto = false;
            } else if (txtCelular.getText().toString().equals("555555555")) {
                correcto = false;
            }
            else if (txtCelular.getText().toString().equals("666666666")) {
                correcto = false;
            }
            else if (txtCelular.getText().toString().equals("777777777")) {
                correcto = false;
            }
            else if (txtCelular.getText().toString().equals("888888888")) {
                correcto = false;
            }
            else if (txtCelular.getText().toString().equals("999999999")) {
                correcto = false;
            }
            else{
                correcto = false;
            }
        }
        return correcto;
    }

    public boolean hayConexiónRed(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo actNetInfo = connectivityManager.getActiveNetworkInfo();
        return (actNetInfo != null && actNetInfo.isConnected());
    }

    public void validarCarritoVacio(ImageButton carrito, TextView cantidad){
        Globales globales = new Globales();
        ArrayList<PedidoDetalle> listapedidos = globales.getListaPedidosCache("lista_pedidos");
        if (listapedidos.isEmpty()) {
            carrito.setImageResource(R.drawable.ic_canasta);
            cantidad.setVisibility(View.GONE);
            cantidad.setText("");
        }
        else {
            carrito.setImageResource(R.drawable.ic_canasta);
            cantidad.setVisibility(View.VISIBLE);
            cantidad.setText(String.valueOf(listapedidos.size()));
        }
    }
}
