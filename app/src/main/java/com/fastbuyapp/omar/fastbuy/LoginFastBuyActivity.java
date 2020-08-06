package com.fastbuyapp.omar.fastbuy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fastbuyapp.omar.fastbuy.HelperEsquema.UsuarioDbHelper;
import com.fastbuyapp.omar.fastbuy.Validaciones.ValidacionDatos;
import com.fastbuyapp.omar.fastbuy.config.Globales;
import com.fastbuyapp.omar.fastbuy.entidades.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeUnit;

public class LoginFastBuyActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    String nombreAll, correo, telefono;
    String mVerificationId;
    ProgressDialog progDailog = null;
    Button btnRegistrar;
    SharedPreferences myPreferences;
    SharedPreferences.Editor myEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_fast_buy);

        final EditText txtNameAll = (EditText) findViewById(R.id.txtNombresApellidos_Reg);
        final EditText txtCorreo = (EditText) findViewById(R.id.txtCorreoElectronico_Reg);
        final EditText txtNumPhone = (EditText) findViewById(R.id.txtNumCelular_Reg);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                VerDatos(firebaseAuth);
                // ...
            }
        };
        myPreferences =  PreferenceManager.getDefaultSharedPreferences(this);
        myEditor = myPreferences.edit();

        btnRegistrar = (Button) findViewById(R.id.btnRegistrarUsuario);
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nombreAll = txtNameAll.getText().toString();
                correo = txtCorreo.getText().toString();
                telefono = txtNumPhone.getText().toString();

                if(nombreAll.trim().length() == 0){
                    Toast toast = Toast.makeText(v.getContext(), "Ingrese su nombre y apellidos.", Toast.LENGTH_SHORT);
                    View vistaToast = toast.getView();
                    vistaToast.setBackgroundResource(R.drawable.toast_yellow);
                    toast.show();
                    return;
                }
                ValidacionDatos validacion = new ValidacionDatos();
                if(validacion.validarCelular(txtNumPhone)==false){
                    Toast toast = Toast.makeText(v.getContext(), "Ingrese un número de celular válido.", Toast.LENGTH_SHORT);
                    View vistaToast = toast.getView();
                    vistaToast.setBackgroundResource(R.drawable.toast_yellow);
                    toast.show();
                    return;
                }

                myEditor.putString("Name_Cliente", nombreAll);
                myEditor.putString("Number_Cliente", telefono);
                myEditor.putString("Email_Cliente", correo);

                btnRegistrar.setEnabled(false);
                progDailog = new ProgressDialog(LoginFastBuyActivity.this);
                progDailog.setMessage("Enviando mensaje...");
                progDailog.setIndeterminate(true);
                progDailog.setCancelable(false);
                progDailog.show();
                requestCode(v);
                //Toast.makeText(LoginFastBuyActivity.this, nombreAll +", "+ correo +", "+ telefono, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void VerDatos(FirebaseAuth firebaseAuth) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            // User is signed in
            Log.d("SIGNED ENTRADA", "onAuthStateChanged:signed_in:" + user.getUid());
            //Intent intent = new Intent(LoginFastBuyActivity.this, CiudadActivity.class);
            //Intent intent = new Intent(LoginFastBuyActivity.this, InstruccionesActivity.class);
            Intent intent = new Intent(LoginFastBuyActivity.this, TutorialActivity.class);
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
                    Toast.makeText(LoginFastBuyActivity.this, "Mensaje enviado", Toast.LENGTH_SHORT).show();
                    btnRegistrar.setEnabled(true);
                    progDailog.dismiss();
                }
                else{
                    Toast.makeText(LoginFastBuyActivity.this, "Ocurrió un error, inténtalo nuevamente.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void requestCode(View view) {
        String phoneNumber = "+51" + telefono;
        Log.v("CELULARAZO", phoneNumber);
        if (TextUtils.isEmpty(phoneNumber))
            return;

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber, 60, TimeUnit.SECONDS, LoginFastBuyActivity.this,
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
                        btnRegistrar.setEnabled(true);
                        Toast.makeText(LoginFastBuyActivity.this, "onVerificationFailed " + e.getMessage(), Toast.LENGTH_LONG).show();
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
                        progDailog.dismiss();
                        Toast.makeText(LoginFastBuyActivity.this, "onCodeAutoRetrievalTimeOut :" + verificationId, Toast.LENGTH_SHORT).show();
                    }
                }
        );
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
