package com.fastbuyapp.omar.fastbuy.entidades;

import java.io.Serializable;

public class FireBaseRepartidor implements Serializable {
    private String codRepartidor;
    private String pedido;


    public String getCodRepartidor() {
        return codRepartidor;
    }

    public void setCodRepartidor(String codRepartidor) {
        this.codRepartidor = codRepartidor;
    }

    public String getPedido() {
        return pedido;
    }

    public void setPedido(String pedido) {
        this.pedido = pedido;
    }
}
