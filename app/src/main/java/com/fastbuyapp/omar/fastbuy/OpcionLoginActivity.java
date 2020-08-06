package com.fastbuyapp.omar.fastbuy;

import android.annotation.SuppressLint;
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
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

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
    String nombre, celular, email, foto;
    Button btnIniciarFacebook,btnIniciarGoogle,btnIniciar;
    SharedPreferences myPreferences;
    SharedPreferences.Editor myEditor;
    //FIREBASE
    /*FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener fiAuthStateListener;*/

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
        //Asignación de TypeFace para las letras de la ventana inicial y referencia de controles
        Typeface typefaceGothic = Typeface.createFromAsset(getAssets(), "fonts/GOTHIC.ttf");
        //TextView lblTerminos = (TextView) findViewById(R.id.lblTerminos);
        //lblTerminos.setTypeface(Globales.typefaceGothic);
        btnIniciarFacebook = (Button) findViewById(R.id.btnIniciarFacebook);
        btnIniciarGoogle = (Button) findViewById(R.id.btnIniciarGoogle);
        btnIniciar = (Button) findViewById(R.id.btnIniciar);
        btnIniciarFacebook.setTypeface(typefaceGothic);
        btnIniciarGoogle.setTypeface(typefaceGothic);
        btnIniciar.setTypeface(typefaceGothic);
        //The original Facebook button
        final LoginButton loginButtonFacebook = (LoginButton)findViewById(R.id.loginButonFacebook);
        final SignInButton loginButtonGoogle = (SignInButton)findViewById(R.id.loginButonGoogle);

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
                Globales.OpcionInicio = "FACEBOOK";
                loginButtonFacebook.performClick();
            }
        });

        btnIniciarGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //EstadoBotones(false);
                Globales.OpcionInicio = "GOOGLE";
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

    }

    public void EstadoBotones(Boolean x){
        btnIniciarFacebook.setEnabled(x);
        btnIniciarGoogle.setEnabled(x);
        btnIniciar.setEnabled(x);
    }

    public void VerDatos(Profile profile){
        if(profile != null){
            nombre = profile.getFirstName();
            myEditor.putString("Name_Cliente", nombre);
            Log.v("NombreFacebook", profile.getFirstName());
            Log.v("ApellidoFacebook", profile.getLastName());
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
            Intent intent = new Intent(OpcionLoginActivity.this, IngresaNumeroActivity.class);
            //esta linea sirve para evitar que una línea no sea predecesora de otra
            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        EstadoBotones(true);

        if (Globales.OpcionInicio.equals("FACEBOOK"))
            callbackManager.onActivityResult(requestCode, resultCode, data);
        else if (Globales.OpcionInicio.equals("GOOGLE")){
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
               // Log.v("NombreGoogle",Globales.nombreCliente.toString());
                Intent intent = new Intent(OpcionLoginActivity.this, IngresaNumeroActivity.class);
                //esta linea sirve para evitar que una línea no sea predecesora de otra
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
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
        if (Globales.OpcionInicio.equals("GOOGLE")){
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
        }else if (Globales.OpcionInicio.equals("FACEBOOK")){
            Profile profile = Profile.getCurrentProfile();
            VerDatos(profile);
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


}
