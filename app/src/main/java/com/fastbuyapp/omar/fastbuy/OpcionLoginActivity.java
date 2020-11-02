package com.fastbuyapp.omar.fastbuy;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Typeface;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.LoggingBehavior;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.fastbuyapp.omar.fastbuy.Validaciones.ValidacionDatos;
import com.fastbuyapp.omar.fastbuy.config.Globales;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class OpcionLoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    //INICIO CON FACEBOOK
    CallbackManager callbackManager;
    ProfileTracker profileTracker;
    AccessTokenTracker accessTokenTracker;
    AccessToken accessToken;
    //INICIO CON GOOGLE
    GoogleApiClient googleApiClient;
    SignInButton signInButton;
    GoogleSignInClient mGoogleSignInClient;
    public static final int SIGN_IN_CODE = 777;
    String nombre, celular, email, foto, tokencito;
    Button btnIniciarFacebook,btnIniciarGoogle,btnIniciar;
    SharedPreferences myPreferences;
    SharedPreferences.Editor myEditor;
    Button btnValidarNumero;
    EditText txtCelularIngresar;
    ProgressDialog progDailog = null;
    String phoneNumber;
    String mVerificationId;
    TextView txtIniciaSesionCon, txtUsuarioLogeado;
    //FIREBASE
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.fastbuyapp.omar.fastbuy",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
        setContentView(R.layout.activity_opcion_login);
        FacebookSdk.addLoggingBehavior(LoggingBehavior.REQUESTS);

        //abrirPopUp();
        myPreferences =  PreferenceManager.getDefaultSharedPreferences(this);
        myEditor = myPreferences.edit();
        celular = myPreferences.getString("Number_Cliente", "");
        nombre = myPreferences.getString("Name_Cliente", "");
        tokencito = myPreferences.getString("tokencito", "");
        //Asignación de TypeFace para las letras de la ventana inicial y referencia de controles
        Typeface typefaceGothic = Typeface.createFromAsset(getAssets(), "fonts/GOTHIC.ttf");
        //TextView lblTerminos = (TextView) findViewById(R.id.lblTerminos);
        //lblTerminos.setTypeface(Globales.typefaceGothic);
        btnIniciarFacebook = (Button) findViewById(R.id.btnIniciarFacebook);
        btnIniciarGoogle = (Button) findViewById(R.id.btnIniciarGoogle);
        btnIniciar = (Button) findViewById(R.id.btnIniciar);
        btnValidarNumero = (Button) findViewById(R.id.btnValidarNumero);
        btnIniciarFacebook.setTypeface(typefaceGothic);
        btnIniciarGoogle.setTypeface(typefaceGothic);
        btnIniciar.setTypeface(typefaceGothic);
        txtCelularIngresar = (EditText) findViewById(R.id.txtCelularIngresar);
        txtIniciaSesionCon = (TextView) findViewById(R.id.txtIniciaSesionCon);
        txtUsuarioLogeado = (TextView) findViewById(R.id.txtUsuarioLogeado);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                VerDatosValidacion(firebaseAuth);
                // ...
            }
        };

        //The original Facebook button
        final LoginButton loginButtonFacebook = (LoginButton)findViewById(R.id.loginButonFacebook);
        final SignInButton loginButtonGoogle = (SignInButton)findViewById(R.id.loginButonGoogle);
        Toast.makeText(OpcionLoginActivity.this, nombre, Toast.LENGTH_SHORT).show();
        if(!celular.equals("")){
            Intent intent = new Intent(OpcionLoginActivity.this, IngresaNombreActivity.class);
            startActivity(intent);
        }
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        //si existe una session iniciada
        if (account != null ){
            //cerramos la session
            mGoogleSignInClient.signOut()
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                        }
                    });
        }

        btnIniciarFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //EstadoBotones(false);
                myEditor.putString("OpcionInicio",  "FACEBOOK");
                myEditor.commit();
                loginButtonFacebook.performClick();
            }
        });

        btnIniciarGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //EstadoBotones(false);
                myEditor.putString("OpcionInicio",  "GOOGLE");
                myEditor.commit();
                //loginButtonGoogle.performClick();
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, SIGN_IN_CODE);
            }
        });

        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        accessToken = loginResult.getAccessToken();
                        Log.d("TokenFB", accessToken.getToken());
                        Profile profile = Profile.getCurrentProfile();
                        VerDatos(profile);
                        accessTokenTracker = new AccessTokenTracker() {
                            @Override
                            protected void onCurrentAccessTokenChanged(
                                    AccessToken oldAccessToken,
                                    AccessToken currentAccessToken) {
                                // Set the access token using
                                // currentAccessToken when it's loaded or set.
                            }
                        };
                        profileTracker = new ProfileTracker() {
                            @Override
                            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {

                            }
                        };

                        accessTokenTracker.startTracking();
                        profileTracker.startTracking();
                        List<String> permissionNeeds= Arrays.asList("public_profile");
                        LoginManager.getInstance().logInWithReadPermissions(OpcionLoginActivity.this, permissionNeeds);
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(OpcionLoginActivity.this, "Cancelado", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {

                        Toast.makeText(OpcionLoginActivity.this,"Error "+ exception.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

        //LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile", "user_birthday"));
        /*GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
                .requestEmail()
                .build();*/


        btnIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EstadoBotones(false);
                Intent intent = new Intent(OpcionLoginActivity.this, LoginFastBuyActivity.class);
                startActivity(intent);
            }
        });

        btnValidarNumero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnValidarNumero.setEnabled(false);
                progDailog = new ProgressDialog(OpcionLoginActivity.this);
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
                    btnValidarNumero.setEnabled(true);
                    return;
                }else{
                    celular = txtCelularIngresar.getText().toString();
                    //myEditor.putString("Number_Cliente", celular);
                    myEditor.putString("Number_Cliente", celular);
                    myEditor.commit();
                    // requestCode(v); //descomentar
                    //myEditor.commit();
                    String URL = "https://apifbdelivery.fastbuych.com/Delivery/ValidarUsuario?auth="+ tokencito +"&telefono="+ URLEncoder.encode(celular);
                    RequestQueue queue = Volley.newRequestQueue(OpcionLoginActivity.this);
                    StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (response.length()>0){
                                try {
                                    JSONObject objeto = new JSONObject(response);
                                    String repuesta = objeto.getString("respuesta");
                                    if(repuesta.equals("existe")){
                                        String nuevocodigo = objeto.getString("codigo");
                                        String nombre = objeto.getString("nombre");
                                        myEditor.putString("Id_Cliente",  nuevocodigo);
                                        myEditor.putString("Name_Cliente", nombre);
                                        myEditor.putString("existe_cliente", "existe");
                                        myEditor.commit();
                                        Intent intent = new Intent(OpcionLoginActivity.this, TutorialActivity.class);
                                        startActivity(intent);
                                    }
                                    else{
                                        Intent intent = new Intent(OpcionLoginActivity.this, IngresaNombreActivity.class);
                                        startActivity(intent);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    btnValidarNumero.setEnabled(true);
                                }
                            }
                        }
                    }, new Response.ErrorListener(){
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            Intent intentdes = new Intent(OpcionLoginActivity.this, ActivityDesconectado.class);
                            startActivity(intentdes);
                        }
                    });
                    stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                            10000,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    queue.add(stringRequest);
                }
            }
        });

    }

    public void EstadoBotones(Boolean x){
        btnIniciarFacebook.setEnabled(x);
        btnIniciarGoogle.setEnabled(x);
        btnIniciar.setEnabled(x);
    }

    public void VerDatos(Profile profile) {
        if(profile != null){
            nombre = profile.getFirstName();
            myEditor.putString("Name_Cliente", nombre);
            myEditor.commit();
            GraphRequest request = GraphRequest.newMeRequest(
                    accessToken,
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            Log.v("CUMPLE",String.valueOf(response));

                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,link");
            request.setParameters(parameters);
            request.executeAsync();
            //super.recreate();
            //Intent intent = new Intent(OpcionLoginActivity.this, IngresaNumeroActivity.class);
            //esta linea sirve para evitar que una línea no sea predecesora de otra
            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            //startActivity(intent);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        EstadoBotones(true);
        String OpcionInicio = myPreferences.getString("OpcionInicio", "");
        if (OpcionInicio.equals("FACEBOOK"))
            callbackManager.onActivityResult(requestCode, resultCode, data);
        else if (OpcionInicio.equals("GOOGLE")){
            if (requestCode == SIGN_IN_CODE) {
                // The Task returned from this call is always completed, no need to attach
                // a listener.
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                handleSignInResult(task);
            }
        }

    }

    @SuppressLint("LongLogTag")
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        EstadoBotones(true);
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getBaseContext());
            if (acct != null){
                //Globales.nombreCliente = acct.getDisplayName();
                String personGivenName = acct.getGivenName();
                String personFamilyName = acct.getFamilyName();
                nombre = personGivenName+" "+personFamilyName;
                myEditor.putString("Name_Cliente", nombre);
                email = acct.getEmail();
                myEditor.putString("Email_Cliente", email);
                String personId = acct.getId();
                if (acct.getPhotoUrl() != null) {
                    String fotoCliente = acct.getPhotoUrl().toString();
                    myEditor.putString("Photo_Cliente", fotoCliente);
                }
                myEditor.commit();
                //super.recreate();
               // Log.v("NombreGoogle",Globales.nombreCliente.toString());
                //Intent intent = new Intent(OpcionLoginActivity.this, IngresaNumeroActivity.class);
                //esta linea sirve para evitar que una línea no sea predecesora de otra
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                //startActivity(intent);
            }
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.v("signInResult:failed code=",String.valueOf(e.getStatusCode()));
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //accessTokenTracker.stopTracking();
        //profileTracker.stopTracking();
    }

    @Override
    protected void onResume() {
        super.onResume();
        EstadoBotones(true);
        String OpcionInicio = myPreferences.getString("OpcionInicio", "");
            if (OpcionInicio.equals("GOOGLE")){
                GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
                //si existe una session iniciada
                if (account != null ){
                    //cerramos la session
                    mGoogleSignInClient.signOut()
                            .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                }
                            });
                }

                btnIniciarGoogle.setVisibility(View.GONE);
                btnIniciarFacebook.setVisibility(View.GONE);
                txtUsuarioLogeado.setVisibility(View.VISIBLE);
                txtIniciaSesionCon.setVisibility(View.GONE);

            }else if (OpcionInicio.equals("FACEBOOK")){
                Profile profile = Profile.getCurrentProfile();
                VerDatos(profile);
                btnIniciarGoogle.setVisibility(View.GONE);
                btnIniciarFacebook.setVisibility(View.GONE);
                txtIniciaSesionCon.setVisibility(View.GONE);
                txtUsuarioLogeado.setVisibility(View.VISIBLE);
            }
        nombre = myPreferences.getString("Name_Cliente", "");

        if (!nombre.equals("")){
            String mensaje = "Hola " + nombre + ",  para continuar con tu registro ingresa tu número de celular.";
            txtUsuarioLogeado.setText(mensaje);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void VerDatosValidacion(FirebaseAuth firebaseAuth) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            // User is signed in
            Log.d("SIGNED ENTRADA", "onAuthStateChanged:signed_in:" + user.getUid());
            //Intent intent = new Intent(IngresaNumeroActivity.this, CiudadActivity.class);
            //celular         = phoneNumber.substring(3);
            myEditor.putString("Number_Cliente", celular);
            myEditor.commit();
            Intent intent = new Intent(OpcionLoginActivity.this, IngresaNombreActivity.class);
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
                    Toast.makeText(OpcionLoginActivity.this, "Mensaje enviado", Toast.LENGTH_SHORT).show();
                    btnValidarNumero.setEnabled(true);
                    progDailog.dismiss();

                }
                else{
                    btnValidarNumero.setEnabled(true);
                    Toast.makeText(OpcionLoginActivity.this, "Ocurrió un error, inténtalo nuevamente.", Toast.LENGTH_SHORT).show();
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
                phoneNumber, 60, TimeUnit.SECONDS, OpcionLoginActivity.this,
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
                        btnValidarNumero.setEnabled(true);
                        Toast.makeText(OpcionLoginActivity.this, "onVerificationFailed " + e.getMessage(), Toast.LENGTH_LONG).show();
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
                        btnValidarNumero.setEnabled(true);
                        progDailog.dismiss();
                        Toast.makeText(OpcionLoginActivity.this, "hola onCodeAutoRetrievalTimeOut :" + verificationId, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(OpcionLoginActivity.this, VerificacionLoginActivity.class);
                        startActivity(intent);
                    }
                }
        );
    }

}
