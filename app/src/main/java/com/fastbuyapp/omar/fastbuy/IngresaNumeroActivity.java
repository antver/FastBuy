package com.fastbuyapp.omar.fastbuy;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.fastbuyapp.omar.fastbuy.Validaciones.ValidacionDatos;
import com.fastbuyapp.omar.fastbuy.config.Globales;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class IngresaNumeroActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    String mVerificationId;
    EditText txtCelularIngresar;
    String phoneNumber, celular;
    ProgressDialog progDailog = null;
    Button btnRecibir;

    SharedPreferences.Editor myEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingresa_numero);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_chevron_left_black_24dp));

        TextView lblSaludo = (TextView) findViewById(R.id.lblSaludo);
        TextView lblAgradecimiento = (TextView) findViewById(R.id.lblAgradecimiento);
        TextView lblIngresa = (TextView) findViewById(R.id.lblIngresa);
        TextView lblnumero = (TextView) findViewById(R.id.lblnumero);
        TextView lblDescripcion = (TextView) findViewById(R.id.lblDescripcion);
        Typeface typefaceNexa = Typeface.createFromAsset(getAssets(), "fonts/NEXABOLD.otf");
        Typeface typefaceGothic = Typeface.createFromAsset(getAssets(), "fonts/GOTHIC.ttf");
        lblSaludo.setTypeface(typefaceNexa);
        lblAgradecimiento.setTypeface(typefaceNexa);
        lblIngresa.setTypeface(typefaceNexa);
        lblnumero.setTypeface(typefaceNexa);
        lblDescripcion.setTypeface(typefaceGothic);
        if (AccessToken.getCurrentAccessToken() == null && Globales.OpcionInicio == "FACEBOOK") {
            Intent intent = new Intent(IngresaNumeroActivity.this, OpcionLoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        SharedPreferences myPreferences =  PreferenceManager.getDefaultSharedPreferences(this);
        myEditor = myPreferences.edit();
        celular = myPreferences.getString("Number_Cliente", "");
        String nombre = myPreferences.getString("Name_Cliente", "");
        lblSaludo.setText(getString(R.string.hola) + ", " + nombre);
        if(!celular.equals("")){
            Intent intent = new Intent(IngresaNumeroActivity.this, TutorialActivity.class);
            startActivity(intent);
        }
        txtCelularIngresar = (EditText) findViewById(R.id.txtCelularIngresar);
        btnRecibir = (Button) findViewById(R.id.btnRecibir);
        btnRecibir.setTypeface(typefaceGothic);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                VerDatos(firebaseAuth);
                // ...
            }
        };

        btnRecibir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnRecibir.setEnabled(false);
                progDailog = new ProgressDialog(IngresaNumeroActivity.this);
                progDailog.setMessage("Enviando mensaje...");
                progDailog.setIndeterminate(true);
                progDailog.setCancelable(false);
                progDailog.show();
                ValidacionDatos validacion = new ValidacionDatos();
                if(validacion.validarCelular(txtCelularIngresar)==false){
                    progDailog.dismiss();
                    Toast toast = Toast.makeText(v.getContext(), "Ingrese un número de celular válido.", Toast.LENGTH_SHORT);
                    View vistaToast = toast.getView();
                    vistaToast.setBackgroundResource(R.drawable.toast_warning);
                    toast.show();
                    return;
                }else{
                    celular = txtCelularIngresar.getText().toString();
                    //myEditor.putString("Number_Cliente", celular);
                    myEditor.putString("Number_Cliente", celular);
                    myEditor.commit();
                    //requestCode(v); //descomentar
                    //myEditor.commit();
                    Intent intent = new Intent(IngresaNumeroActivity.this, TutorialActivity.class); //eliminarlas
                    startActivity(intent);
                }
            }
        });
        txtCelularIngresar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (txtCelularIngresar.getText().length() == 9) {
                    btnRecibir.setBackground(getDrawable(R.drawable.botonverde));
                    btnRecibir.setEnabled(true);
                } else {
                    btnRecibir.setBackground(getDrawable(R.drawable.botongris));
                    btnRecibir.setEnabled(false);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (txtCelularIngresar.getText().length() == 9) {
                    btnRecibir.setBackground(getDrawable(R.drawable.botonverde));
                    btnRecibir.setEnabled(true);
                } else {
                    btnRecibir.setBackground(getDrawable(R.drawable.botongris));
                    btnRecibir.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (txtCelularIngresar.getText().length() == 9) {
                    btnRecibir.setBackground(getDrawable(R.drawable.botonverde));
                    btnRecibir.setEnabled(true);
                } else {
                    btnRecibir.setBackground(getDrawable(R.drawable.botongris));
                    btnRecibir.setEnabled(false);
                }
            }
        });
    }

    private void VerDatos(FirebaseAuth firebaseAuth) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            // User is signed in
            Log.d("SIGNED ENTRADA", "onAuthStateChanged:signed_in:" + user.getUid());
            //Intent intent = new Intent(IngresaNumeroActivity.this, CiudadActivity.class);
            //celular         = phoneNumber.substring(3);
            myEditor.putString("Number_Cliente", celular);
             myEditor.commit();
            Intent intent = new Intent(IngresaNumeroActivity.this, TutorialActivity.class);
            startActivity(intent);
        } else {
            // User is signed out
            Log.d("SIGNED SALIDA", "onAuthStateChanged:signed_out");
        }
    }

    private void signInWithCredential(PhoneAuthCredential phoneAuthCredential) {
        mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(IngresaNumeroActivity.this, "Mensaje enviado", Toast.LENGTH_SHORT).show();
                    btnRecibir.setEnabled(true);
                    progDailog.dismiss();

                }
                else{
                    btnRecibir.setEnabled(true);
                    Toast.makeText(IngresaNumeroActivity.this, "Ocurrió un error, inténtalo nuevamente.", Toast.LENGTH_SHORT).show();
                    progDailog.dismiss();
                }
            }
        });
    }

    public void requestCode(View view) {
        phoneNumber = "+51" + txtCelularIngresar.getText().toString();
        Log.v("CELULARAZO", phoneNumber);
        if (TextUtils.isEmpty(phoneNumber))
            return;

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber, 60, TimeUnit.SECONDS, IngresaNumeroActivity.this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                        //Called if it is not needed to enter verification code
                        signInWithCredential(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        //incorrect phone number, verification code, emulator, etc.
                        progDailog.dismiss();
                        btnRecibir.setEnabled(true);
                        Toast.makeText(IngresaNumeroActivity.this, "onVerificationFailed " + e.getMessage(), Toast.LENGTH_LONG).show();
                        Log.v("fallo",e.getMessage().toString());
                    }

                    @Override
                    public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        //now the code has been sent, save the verificationId we may need it
                        super.onCodeSent(verificationId, forceResendingToken);
                        mVerificationId = verificationId;
                        Log.v("VERIFICACION", mVerificationId);
                        Globales.mVerificationId = mVerificationId;

                        /*Intent intent = new Intent(IngresaNumeroActivity.this, VerificacionLoginActivity.class);
                        startActivity(intent);*/
                    }

                    @Override
                    public void onCodeAutoRetrievalTimeOut(String verificationId) {
                        //called after timeout if onVerificationCompleted has not been called
                        super.onCodeAutoRetrievalTimeOut(verificationId);
                        btnRecibir.setEnabled(true);
                        progDailog.dismiss();
                        Toast.makeText(IngresaNumeroActivity.this, "hola onCodeAutoRetrievalTimeOut :" + verificationId, Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menuingreso, menu);
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
            mAuth.signOut();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
            mAuth = FirebaseAuth.getInstance();
            mAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    VerDatos(firebaseAuth);
                }
            };
    }
}
