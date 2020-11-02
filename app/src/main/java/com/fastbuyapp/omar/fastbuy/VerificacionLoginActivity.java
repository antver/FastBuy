package com.fastbuyapp.omar.fastbuy;

import android.app.Activity;
import android.graphics.Typeface;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fastbuyapp.omar.fastbuy.config.Globales;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class VerificacionLoginActivity extends AppCompatActivity {
    Keyboard mKeyboard;
    KeyboardView mKeyboardView;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    Button btnSiguiente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verificacion_login);

        Typeface typefaceGothic = Typeface.createFromAsset(getAssets(), "fonts/GOTHIC.ttf");
        Typeface typefaceNexa = Typeface.createFromAsset(getAssets(), "fonts/NEXABOLD.otf");
        TextView lblIngresaCodigo = (TextView) findViewById(R.id.lblIngresaCodigo);
        TextView lblDescripcion = (TextView) findViewById(R.id.lblDescripcion);
        TextView lblReenviar = (TextView) findViewById(R.id.lblReenviar);
        lblIngresaCodigo.setTypeface(typefaceNexa);
        lblDescripcion.setTypeface(typefaceGothic);
        lblReenviar.setTypeface(typefaceGothic);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("SIGNED ENTRADA", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d("SIGNED SALIDA", "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        final EditText edNum11 = (EditText) findViewById(R.id.edNum1);
        final EditText edNum12 = (EditText) findViewById(R.id.edNum2);
        final EditText edNum13 = (EditText) findViewById(R.id.edNum3);
        final EditText edNum14 = (EditText) findViewById(R.id.edNum4);
        final EditText edNum15 = (EditText) findViewById(R.id.edNum5);
        final EditText edNum16 = (EditText) findViewById(R.id.edNum6);
        edNum11.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(edNum11.getText().length() == 0){

                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(edNum11.getText().length() == 1){
                    edNum12.requestFocus();
                }
            }
        });

        edNum12.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(edNum12.getText().length() == 0){
                    edNum11.requestFocus();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(edNum12.getText().length() == 0){
                    edNum11.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(edNum12.getText().length() == 1){
                    edNum13.requestFocus();
                }
            }
        });

        edNum13.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(edNum13.getText().length() == 0){
                    edNum12.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(edNum13.getText().length() == 1){
                    edNum14.requestFocus();
                }
            }
        });

        edNum14.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(edNum14.getText().length() == 0){
                    edNum13.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(edNum14.getText().length() == 1){
                    edNum15.requestFocus();
                }
            }
        });

        edNum15.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(edNum15.getText().length() == 0){
                    edNum14.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(edNum15.getText().length() == 1){
                    edNum16.requestFocus();
                }
            }
        });

        edNum16.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(edNum16.getText().length() == 0){
                    edNum15.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(edNum16.getText().length() == 1){
                    btnSiguiente.setEnabled(true);
                }
            }
        });

        btnSiguiente = (Button) findViewById(R.id.btnSiguiente);
        btnSiguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = edNum11.getText().toString() + edNum12.getText().toString() + edNum13.getText().toString()+ edNum14.getText().toString()+ edNum15.getText().toString() + edNum16.getText().toString();
                if (TextUtils.isEmpty(code))
                    return;
                signInWithCredential(PhoneAuthProvider.getCredential(Globales.mVerificationId, code));
            }
        });
    }
    private void signInWithCredential(PhoneAuthCredential phoneAuthCredential) {
        mAuth.signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(VerificacionLoginActivity.this, "Correcto", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(VerificacionLoginActivity.this, "Errorazo" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void openKeyboard(View v)
    {
        mKeyboardView.setVisibility(View.VISIBLE);
        mKeyboardView.setEnabled(true);
        if( v!=null)((InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    private KeyboardView.OnKeyboardActionListener mOnKeyboardActionListener = new KeyboardView.OnKeyboardActionListener() {
        @Override public void onKey(int primaryCode, int[] keyCodes)
        {
            //Here check the primaryCode to see which key is pressed
            //based on the Android:codes property
            if(primaryCode==1)
            {
                Log.i("Key","You just pressed 1 button");
            }
        }

        @Override
        public void onPress(int arg0) {
        }

        @Override
        public void onRelease(int primaryCode) {
        }

        @Override
        public void onText(CharSequence text) {
        }

        @Override
        public void swipeDown() {
        }

        @Override
        public void swipeLeft() {
        }

        @Override
        public void swipeRight() {
        }

        @Override
        public void swipeUp() {
        }
    };

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.verificarmovil, menu);
        return true;
    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

}
