package com.zero.next.retailscanner;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zero.next.retailscanner.data.User;
import com.zero.next.retailscanner.sqlconnection.ConnectionHandler;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    private static final String TAG = "omni";
    private static final int REQUEST_RESOLVE_ERROR = 1001; //google default response
    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001; //google default response
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String user_email;
    private String user_name;
    private String user_id;
    PrefManager prefManager;
    SharedPreferences sharedPreferences;
    FirebaseDatabase userDatabase;
    DatabaseReference userDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userDatabase = FirebaseDatabase.getInstance();


        prefManager = new PrefManager(this);
        setContentView(R.layout.activity_login);
        sharedPreferences = getSharedPreferences(PrefManager.PREF_NAME, PrefManager.PRIVATE_MODE);
        String prefEmail = (sharedPreferences).getString(PrefManager.USER_EMAIL, "");
        if (!prefEmail.equals("")) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        // Set the dimensions of the sign-in button.
        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
        //findViewById(R.id.sign_in_button).setOnClickListener(this);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.api_signin))
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(LoginActivity.this)
        .enableAutoManage(this,this)
        .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
        .addConnectionCallbacks(this)
        .build();

        mAuth = FirebaseAuth.getInstance();
        /*mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user!=null){

                }else{

                }

            }
        };*/
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void writeNewUser(final String id, final String name, final String email, final String balance) {
        User user = new User(id, name, email, balance);
        userDatabaseRef = userDatabase.getReference("users/"+id);
        userDatabaseRef.setValue(user);

        //write to sql
        class InsertUser extends AsyncTask<Void, Void, String>{
            ConnectionHandler conn = new ConnectionHandler();
            ProgressDialog progress;
            @Override
            protected String doInBackground(Void... voids) {
                HashMap<String, String> params = new HashMap<>();
                params.put("user_id", id);
                params.put("user_name", name);
                params.put("user_email", email);
                params.put("balance", balance);
//              String s = conn.sendGetRequest("http://10.208.73.29/retail/customersRegistration.php?user_id=" + id + "&&user_name=" + name + "&&user_email=" + email + "&&balance=" + balance);
                String s = conn.sendPostRequest("http://192.168.43.87/retail/customersRegistration.php",params);

                return s;
            }

            @Override
            protected void onPreExecute() {
                progress = ProgressDialog.show(LoginActivity.this, "Processing...", "Wait....", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                progress.dismiss();
                Log.d("sql", s);
            }
        }

        InsertUser insertUser = new InsertUser();
        insertUser.execute();
    }

    /*@Override
    public void startActivityForResult(Intent intent, int requestCode){
        try {
            super.startActivityForResult(intent,requestCode);
        }catch (Exception e){
            e.printStackTrace();
        }
    }*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
            //result dalam bentuk json
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount account = result.getSignInAccount();
            user_id = account.getId();
            user_email = account.getEmail();
            user_name = account.getDisplayName();
            prefManager.setUserEmail(user_email);
            prefManager.setUserName(user_name);
            prefManager.setUserId(user_id);
            writeNewUser(user_id,user_name,user_email,"0");
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
//            TextView te = findViewById(R.id.te);
//            te.setText(user_email);
            Log.d(TAG, "signInWithCredential: " + user_email);
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
    }

    /*private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            user_email = user.getEmail();
                            Log.d(TAG, "signInWithCredential: " + user_email);
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            //Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }*/


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);

        boolean mResolvingError=false;
        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (connectionResult.hasResolution()) {
            try {
                mResolvingError = true;
                connectionResult.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mGoogleApiClient.connect();
            }
        } else {
            // Show dialog using GoogleApiAvailability.getErrorDialog()
            //showErrorDialog(connectionResult.getErrorCode());
            mResolvingError = true;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}
