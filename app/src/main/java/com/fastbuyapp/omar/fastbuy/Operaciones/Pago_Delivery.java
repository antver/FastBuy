package com.fastbuyapp.omar.fastbuy.Operaciones;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.fastbuyapp.omar.fastbuy.config.Globales;

public class Pago_Delivery {

    double CostoDelivery = 0;
    double costoExtra = 0.9; //por kilometro

    public double calcularCostoEnvio(double distancia, double tarifaBase, double distanciaBase){
        if(distancia > distanciaBase){
            CostoDelivery = tarifaBase + (costoExtra * (distancia - distanciaBase));
        }
        else{
            CostoDelivery = tarifaBase;
        }
        return CostoDelivery;
    }
}
