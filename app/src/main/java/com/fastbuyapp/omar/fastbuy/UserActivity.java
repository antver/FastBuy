package com.fastbuyapp.omar.fastbuy;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.fastbuyapp.omar.fastbuy.Validaciones.ValidacionDatos;
import com.fastbuyapp.omar.fastbuy.config.GlideApp;
import com.fastbuyapp.omar.fastbuy.config.Globales;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;

import java.util.Random;


public class UserActivity extends AppCompatActivity {

    ProgressDialog progDailog = null;
    SharedPreferences.Editor myEditor;
    SharedPreferences myPreferences;

    ImageButton btnCarrito;
    String newName;
    TextView txtName;

    @Override
    protected void onResume() {
        super.onResume();
        ValidacionDatos valida = new ValidacionDatos();
        valida.validarCarritoVacio(btnCarrito);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        //Inicializando variables
        txtName = (TextView) findViewById(R.id.txtNameUser);
        TextView txtNumber = (TextView) findViewById(R.id.txtNumberUser);
        TextView txtEmail = (TextView) findViewById(R.id.txtEmailUser);
        final ImageView imgFoto = (ImageView) findViewById(R.id.imgFotoUser);
        //ImageButton btnCerrarSesion =(ImageButton) findViewById(R.id.btnCloseSession);
        LinearLayout btnMisPedidos =(LinearLayout) findViewById(R.id.btnHistorialPedidos);
        //ImageButton btnMisDirecciones =(ImageButton) findViewById(R.id.btnDireccionesPrincipales);
        //ImageButton btnMisEncargos =(ImageButton) findViewById(R.id.btnHistorialEncargos);
        LinearLayout btnMisSaldos =(LinearLayout) findViewById(R.id.btnSaldos);
        LinearLayout btnContactanos =(LinearLayout) findViewById(R.id.btnContactanos);
        LinearLayout Puntos =(LinearLayout) findViewById(R.id.Puntos);
        CardView btnCompartir = (CardView) findViewById(R.id.btnCompartirAmigo);
        CardView btnCalificaAplicacion = (CardView) findViewById(R.id.btnCalificaAplicacion);
        ImageView btnEditaNombre = findViewById(R.id.btnEditaNombre);

        //Obteniendo Datos de la memoria Caché
        myPreferences = PreferenceManager.getDefaultSharedPreferences(UserActivity.this);
        myEditor = myPreferences.edit();

        final String idUser = myPreferences.getString("Id_Cliente", "0");
        String nameUser = myPreferences.getString("Name_Cliente", "Default");
        String numberUser = myPreferences.getString("Number_Cliente", "987654321");
        String emailUser = myPreferences.getString("Email_Cliente", "Ventas@fastbuych.com");
        String photoUser = myPreferences.getString("Photo_Cliente", "R.drawable.perfil");

        //mostrando datos guardados
        Log.v("miURlFavorito",idUser);
        txtName.setText(nameUser);
        txtNumber.setText(numberUser);
        txtEmail.setText(emailUser);

        GlideApp.with(UserActivity.this)
                .load(photoUser)
                .error(R.drawable.perfil)
                .fitCenter()
                .transform(new CircleCrop())
                .into(imgFoto);

        btnCompartir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String codCupon = generaCodigoCupon(6);
                Intent send = new Intent();
                send.setAction(Intent.ACTION_SEND);
                String mensajito = "¡Hola! Descarga ahora la INCREIBLE APP de FAST BUY! e ingresa este código "+codCupon+" para obtener tu delivery gratis https://winner.fastbuych.com/Winner/PromoCompartir?auth=superPromoFastBuy&codigo="+idUser+"&cupon="+codCupon;
                send.putExtra(Intent.EXTRA_TEXT, mensajito);
                send.setType("text/plain");
                //send.setPackage("com.whatsapp");
                startActivity(send);
                /*String telefono = "916586810";
                Intent _intencion = new Intent("android.intent.action.MAIN");
                _intencion.setComponent(new ComponentName("com.whatsapp","com.whatsapp.Conversation"));
                _intencion.putExtra("jid", PhoneNumberUtils.stripSeparators("51" + telefono)+"@s.whatsapp.net");
                startActivity(_intencion);*/
            }
        });

        btnEditaNombre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(UserActivity.this);
                builder.setTitle("Editar Nombre");

                // Set up the input
                final EditText input = new EditText(UserActivity.this);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        newName = input.getText().toString();
                        myEditor.putString("Name_Cliente",newName);
                        myEditor.commit();
                        txtName.setText(newName);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

        btnCalificaAplicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.fastbuyapp.omar.fastbuy");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        Puntos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserActivity.this, FastPuntosActivity.class);
                startActivity(intent);
            }
        });

        /*btnMisDirecciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserActivity.this, DireccionesActivity.class);
                startActivity(intent);
            }
        });*/

        btnMisPedidos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserActivity.this, HistorialPedidosActivity.class);
                intent.putExtra("tipo","pedido");
                startActivity(intent);
            }
        });

        btnMisSaldos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserActivity.this, MisSaldosActivity.class);
                startActivity(intent);
            }
        });

        btnContactanos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Uri uri = Uri.parse("https://www.fastbuych.com/");
                Intent intent = new Intent(UserActivity.this, SoporteActivity.class);
                startActivity(intent);
                /*Toast toast = Toast.makeText(UserActivity.this, "En Desarrollo, Escucha música y manten la calma...",Toast.LENGTH_SHORT);
                View vistaToast = toast.getView();
                vistaToast.setBackgroundResource(R.drawable.toast_yellow);
                toast.show();*/
            }
        });

        /*btnMisEncargos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserActivity.this, HistorialPedidosActivity.class);
                intent.putExtra("tipo","extra");
                startActivity(intent);
            }
        });*/

        //Cerrando la sesion
        /******btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(UserActivity.this);
                String mensajito = "Su sesion actual se cerrará. ¿Desea continuar?";
                builder.setMessage(mensajito);
                builder.setTitle("Cerrar Sesión");
                builder.setPositiveButton("Si", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        progDailog = new ProgressDialog(UserActivity.this);
                        progDailog.setMessage("Cerrando Sesión...");
                        progDailog.setIndeterminate(true);
                        progDailog.setCancelable(false);
                        progDailog.show();

                        //myEditor = myPreferences.edit();

                        if (Globales.OpcionInicio == "GOOGLE"){
                            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                    .requestEmail()
                                    .build();
                            GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(UserActivity.this,gso);
                            mGoogleSignInClient.signOut().addOnCompleteListener(UserActivity.this, new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    myEditor.clear();
                                    myEditor.commit();
                                    progDailog.dismiss();
                                    retornaAlInicio();
                                }
                            }).addOnFailureListener(UserActivity.this, new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progDailog.dismiss();
                                    Toast toast = Toast.makeText(UserActivity.this, "No se pudo cerrar sesion...",Toast.LENGTH_SHORT);
                                    View vistaToast = toast.getView();
                                    vistaToast.setBackgroundResource(R.drawable.toast_yellow);
                                    toast.show();
                                }
                            });
                        }else if (Globales.OpcionInicio == "GOOGLE"){
                            progDailog.dismiss();
                            Toast toast = Toast.makeText(UserActivity.this, "Cierre de sesion de Facebook, en desarrollo...",Toast.LENGTH_SHORT);
                            View vistaToast = toast.getView();
                            vistaToast.setBackgroundResource(R.drawable.toast_yellow);
                            toast.show();
                        }else{
                            myEditor.clear();
                            myEditor.commit();
                            progDailog.dismiss();
                            retornaAlInicio();
                        }

                        //SharedPreferences.Editor editor = myPreferences.edit();
                        //editor.clear();
                        //editor.commit();
                        UserActivity.this.finishAffinity();
                        startActivity(new Intent(getBaseContext(), OpcionLoginActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
                        finish();

                        if(mAuthListener != null){
                            Toast toast = Toast.makeText(UserActivity.this, "Tiene Datos",Toast.LENGTH_SHORT);
                            View vistaToast = toast.getView();
                            vistaToast.setBackgroundResource(R.drawable.toast_yellow);
                            toast.show();
                            mAuth.removeAuthStateListener(mAuthListener);
                            mAuth.signOut();
                        }else {
                            Toast toast = Toast.makeText(UserActivity.this, "No Tiene Datos",Toast.LENGTH_SHORT);
                            View vistaToast = toast.getView();
                            vistaToast.setBackgroundResource(R.drawable.toast_yellow);
                            toast.show();
                        }
                    }

                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });******/

        //Menu
        ImageButton btnHome = (ImageButton) findViewById(R.id.btnHome);
        ImageButton btnFavoritos = (ImageButton) findViewById(R.id.btnFavoritos);
        btnCarrito = (ImageButton) findViewById(R.id.btnCarrito);
        ImageButton btnUsuario = (ImageButton) findViewById(R.id.btnUsuario);
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserActivity.this, PrincipalActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        btnFavoritos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserActivity.this, FavoritosActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        btnCarrito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserActivity.this, CarritoActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        btnUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public static String generaCodigoCupon(int length){
        String alphabet =
                new String("0123456789AZBYCXDWEVFUGTHSIRJQKPLOMNNMOLPKQJRISHTGUFVEWDXCYBZA0123456789"); //9
        int n = alphabet.length(); //10

        String result = new String();
        Random r = new Random(); //11

        for (int i=0; i<length; i++) //12
            result = result + alphabet.charAt(r.nextInt(n)); //13

        return result;
    }

    public void retornaAlInicio()
    {
        Intent intent = new Intent(UserActivity.this,SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent intent = new Intent(UserActivity.this,PrincipalActivity.class);
        startActivity(intent);
    }
}
