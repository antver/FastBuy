package com.fastbuyapp.omar.fastbuy.Operaciones;

import android.util.Log;

import com.fastbuyapp.omar.fastbuy.config.Globales;

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
        Log.v("horita",miHora.toString());
        return miHora;
    }

    public int ObtenMayor(){
        int mayor = 0;
        for (int i=0; i<Globales.listaPedidos.size(); i++){
            if (Globales.listaPedidos.get(i).getTiempo()> mayor)
                mayor = Globales.listaPedidos.get(i).getTiempo();
        }

        return mayor;
    }
}
