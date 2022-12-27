package com.example.ex1_205790488_315680397;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private LabeledIntent welcomeLabel;
    private EditText mail;
    private EditText password;
    private Button logIn;
    private Button signUp;
    private int i = 1;
    private Context context;

    //for fire base
    private FirebaseAuth mAuth;
    private static String type;
    private FirebaseFirestore db;
    private RoomDB database;
    private ArrayList<UserTypeTable> tempTypes = null;
    private boolean connected = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        context = this;
        //for firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        mail = findViewById(R.id.mail);
        password = findViewById(R.id.password);
        logIn = findViewById(R.id.logIn);
        signUp = findViewById(R.id.signUp);

        mail.setHint("Mail");
        password.setHint("Password");
        //get the database
        database = RoomDB.getInstance(this);





    }

    public static void setType(String type2) {
        type = type2;
    }

    //check if the user has registered and can log-in to the app
    public void logInMethod(View view) {
        checkConnectivity();
        if (checkConnection()) {
            mAuth.signOut();
            if (password.getText().toString().equals("") && mail.getText().toString().equals("")) {
                Toast.makeText(context, R.string.pleaseEnterMailAndpassword, Toast.LENGTH_LONG).show();
                return;
            }
            if (password.getText().toString().equals("")) {
                Toast.makeText(context, R.string.pleaseEnterPassword, Toast.LENGTH_LONG).show();
                return;
            }
            if (mail.getText().toString().equals("")) {
                Toast.makeText(context, R.string.pleaseEnterMail, Toast.LENGTH_LONG).show();
                password.getText().clear();
                return;
            }

            DocumentReference docRef = null;
            if (mail.getText().toString() != null && !(mail.getText().toString().equals(" "))) {
                docRef = db.collection("users").document(mail.getText().toString());
                mAuth.signInWithEmailAndPassword(mail.getText().toString().toLowerCase(), password.getText().toString())
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    updateUI(user);
                                } else {
                                    Toast.makeText(context, R.string.noSuchUserMail, Toast.LENGTH_LONG).show();
                                    password.getText().clear();
                                }
                            }


                        });
            }
        }
    }

    public void signUpMethod(View view) {
        checkConnectivity();
        if(checkConnection()){
            Intent intent = new Intent(this, SignUp.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
//        type = null;
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            tempTypes = new ArrayList<>(database.mainDao().getAlltypes());
            if (!tempTypes.isEmpty()) {
                type = database.mainDao().getUserType(tempTypes.get(tempTypes.size()-1).getId());
            }
            if (type == null) {
                DocumentReference docRef = db.collection("users").document(user.getEmail());
                docRef.get().addOnCompleteListener(this, new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {

                                //only for first log-in
                                type = document.get("Type").toString();

                                //entering new data to roomDB so we can save on device which type of user we are
                                //and save time on next app start
                                UserTypeTable userTypeTable = new UserTypeTable();
                                userTypeTable.setType(type);
                                String tempType = userTypeTable.getType();
                                database.mainDao().insert(userTypeTable);
                                tempTypes = new ArrayList<>(database.mainDao().getAlltypes());

                                //call open intent after updating user type
                                openIntent();
                                finish();
                            }
                        }
                    }
                });
            }
            openIntent();
        }
    }


    public void openIntent() {
        if (type != null) {
            if (type.equals("Farmer")) {
                Intent intent = new Intent(this, FarmerMainScreen.class);
                startActivity(intent);
                finish();
            }
            if (type.equals("Agronomist")) {
                Intent intent = new Intent(this, AgronomistMainScreen.class);
                startActivity(intent);
                finish();
            }
            if (type.equals("Supplier")) {
                Intent intent = new Intent(this, SupplierMainScreen.class);
                startActivity(intent);
                finish();
            }
        }
    }

    public boolean checkConnection(){
        if(connected){
            return true;
        }
        else
        if(!connected){
            Snackbar snackbar;
            snackbar = Snackbar.make(this.findViewById(android.R.id.content), R.string.noInternet, Snackbar.LENGTH_LONG);
            snackbar.setDuration(4500);
            snackbar.show();
        }
        return false;
    }

    public void checkConnectivity(){
        //check if there is connection to internet
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }
        else {
            connected = false;
        }
    }
}

