package com.fastbuyapp.omar.fastbuy.HelperEsquema;

import android.provider.BaseColumns;

/**
 * Created by OMAR on 25/03/2019.
 */

public class UsuarioContract {

    public static abstract class UsuarioEntry implements BaseColumns {
        public static final String TABLE_NAME ="usuario";

        public static final String CODIGO = "codigo";
        public static final String NOMBRES = "nombres";
        public static final String TELEFONO = "numeroTelefono";
        public static final String FECHANACIMIENTO = "fechaNacimiento";
        public static final String EMAIL = "email";
        public static final String DNI = "dni";
        public static final String RUC = "ruc";
    }
}
