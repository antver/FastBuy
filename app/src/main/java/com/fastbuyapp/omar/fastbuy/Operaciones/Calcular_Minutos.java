package com.fastbuyapp.omar.fastbuy.Operaciones;

import android.util.Log;

import com.fastbuyapp.omar.fastbuy.config.Globales;
import com.fastbuyapp.omar.fastbuy.entidades.PedidoDetalle;

import java.util.ArrayList;

public class Calcular_Minutos {
    public int ObtenMinutos(String Hora){
        String[] Partes = Hora.split(":");
        int minutos = (Integer.valueOf(Partes[0].toString())*60)+Integer.valueOf(Partes[1].toString());

        return minutos;
    }

    public String ObtenHora(){
        String miHora = "00:00:00";
        int min = ObtenMayor();
        int Hora = (int) min/60;
        double minutos = (((double) min/60)-Hora)*60;
        min = (int) minutos + 10;//los 10 min son el tiempo que demora el repartidor en llevar el pedido.
        if (Hora<10)
            miHora = "0"+String.valueOf(Hora)+":"+String.valueOf(min)+":00";
        else
            miHora = String.valueOf(Hora)+":"+String.valueOf(min)+":00";
        return miHora;
    }

    public int ObtenMayor(){
        Globales globales = new Globales();
        ArrayList<PedidoDetalle> listapedidos = globales.getListaPedidosCache("lista_pedidos");
        int mayor = 0;
        for (int i=0; i<listapedidos.size(); i++){
            if (listapedidos.get(i).getTiempo()> mayor)
                mayor = listapedidos.get(i).getTiempo();
        }
        return mayor;
    }
}
