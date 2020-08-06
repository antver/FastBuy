package com.fastbuyapp.omar.fastbuy.entidades;

/**
 * Created by OMAR on 24/03/2019.
 */

public class Ruta {
        private int distancia;
        private String duracionTexto;
        private int duracionSegundos;
        private String mensaje;

        public int getDistancia() {
            return distancia;
        }

        public void setDistancia(int distancia) {
            this.distancia = distancia;
        }

        public String getDuracionTexto() {
            return duracionTexto;
        }

        public void setDuracionTexto(String duracionTexto) {
            this.duracionTexto = duracionTexto;
        }

        public int getDuracionSegundos() {
            return duracionSegundos;
        }

        public void setDuracionSegundos(int duracionSegundos) {
            this.duracionSegundos = duracionSegundos;
        }

        public String getMensaje() {
            return mensaje;
        }

        public void setMensaje(String mensaje) {
            this.mensaje = mensaje;
        }

}
