package com.example.ex1_205790488_315680397;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.pm.LabeledIntent;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.util.Patterns;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firestore.v1.FirestoreGrpc;

import java.util.HashMap;

public class SignUp extends AppCompatActivity {

    private LabeledIntent Signup;
    private EditText mail;
    private EditText password;
    private EditText passwordVerification;
    private EditText name;
    private EditText surname;
    private EditText phone;
    private RadioGroup userType;
    private RadioButton farmer;
    private RadioButton agronomist;
    private RadioButton supplier;
    private Button signUp;
    private RadioButton radioButton;

    private Context context;
    //for fireStore
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        this.context = this;

        userType = (RadioGroup) findViewById(R.id.userType);
        mail = (EditText) findViewById(R.id.editTextTextEmailAddress);
        password = (EditText) findViewById(R.id.editTextPasswordSignUp);
        passwordVerification = (EditText) findViewById(R.id.editTextTextPasswordConfirmation);
        name = (EditText) findViewById(R.id.editTextTextPersonName);
        surname = (EditText) findViewById(R.id.editTextTextPersonLastName);
        phone = (EditText) findViewById(R.id.editTextPhoneNunber);

        setHints();

        //for firestore
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();


    }

    //set hints to fields
    public void setHints() {
        mail.setHint(R.string.mailHint);
        password.setHint(R.string.passwordHint);
        passwordVerification.setHint(R.string.passwordHint);
        name.setHint(R.string.nameHint);
        surname.setHint(R.string.nameHint);
        phone.setHint(R.string.phoneHint);
    }

    //validation to the inserted email
    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    //validation to the inserted password
    public boolean isValidPasswordFormat(final String pass) {

        if (password.getText().toString().length() <= 5)
            return false;
        return true;
    }

    //validation to the inserted phone number
    public boolean isPhoneNumber(String phoneNumber) {
        return PhoneNumberUtils.isGlobalPhoneNumber(phoneNumber);
    }

    //gives feedback to the user according to the error
    public void onSignUpClicked(View view) {

        if (isValidEmail(mail.getText().toString()) == false) {
            Toast.makeText(this, R.string.invalidMail, Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidPasswordFormat(password.getText().toString())) {
            Toast.makeText(this, R.string.invalidPassword, Toast.LENGTH_LONG).show();
            return;
        }
        if (!password.getText().toString().equals(passwordVerification.getText().toString())) {
            Toast.makeText(this, R.string.passwordDoesntMatch, Toast.LENGTH_LONG).show();
            return;
        }
        if (surname.getText().length() < 1 || name.getText().length() < 1) {
            Toast.makeText(this, R.string.pleaseEnterName, Toast.LENGTH_LONG).show();
            return;
        }
        if (!isPhoneNumber(phone.getText().toString())) {
            Toast.makeText(this, R.string.pleaseEnterValidPhone, Toast.LENGTH_LONG).show();
            return;
        }


        int selectedId = userType.getCheckedRadioButtonId();
        radioButton = (RadioButton) findViewById(selectedId);
        //check type of user
        if (radioButton == null) {
            Toast.makeText(this, R.string.selectUserType, Toast.LENGTH_SHORT).show();
            return;
        }

        //now after all of the checks are done we can create the user

        // create a new data with a user details;
        HashMap<String, Object> user = new HashMap<>();
        user.put("Mail", mail.getText().toString().toLowerCase());
        user.put("Name", name.getText().toString());
        user.put("Password", password.getText().toString());
        user.put("Phone number", phone.getText().toString());
        user.put("Surname", surname.getText().toString());
        user.put("Type", radioButton.getText());

        CollectionReference users = db.collection("users");

        DocumentReference docRef = db.collection("users").document(mail.getText().toString().toLowerCase());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Toast.makeText(context, getString(R.string.emailExists), Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        if (radioButton.getText().equals("Farmer")) {
                            CollectionReference farmerFarms = db.collection("farmerFarm");

                            HashMap<String, Object> farmerFarm = new HashMap<>();
                            farmerFarm.put("row", 5);
                            farmerFarm.put("column", 5);
                            farmerFarm.put("farmPlot", null);
                            String farmName = mail.getText().toString().toLowerCase() + " farm";
                            Task task3 = farmerFarms.document(farmName).set(farmerFarm);
                            task3.addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task3) {
                                    Task task2 = users.document(mail.getText().toString().toLowerCase()).set(user);
                                    task2.addOnCompleteListener(new OnCompleteListener() {
                                        @Override
                                        public void onComplete(@NonNull Task task) {
                                            if (task2.isSuccessful()) {
                                                Toast.makeText(context, getString(R.string.hello) + " " + name.getText().toString() + " " + getString(R.string.welcomeNewUser), Toast.LENGTH_LONG).show();


                                                //create Authentication
                                                mAuth.createUserWithEmailAndPassword(mail.getText().toString(), password.getText().toString());

                                                mail.setText("");
                                                name.setText("");
                                                password.setText("");
                                                passwordVerification.setText("");
                                                phone.setText("");
                                                surname.setText("");
                                                radioButton.setChecked(false);

                                                setHints();
                                            } else {
                                                Toast.makeText(context, getString(R.string.failedCreatingAnewUser), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                                }
                            });

                        }
                        else{
                                    Task task2 = users.document(mail.getText().toString().toLowerCase()).set(user);
                                    task2.addOnCompleteListener(new OnCompleteListener() {
                                        @Override
                                        public void onComplete(@NonNull Task task) {
                                            if (task2.isSuccessful()) {
                                                Toast.makeText(context, getString(R.string.hello) + " " + name.getText().toString() + " " + getString(R.string.welcomeNewUser), Toast.LENGTH_LONG).show();


                                                //create Authentication
                                                mAuth.createUserWithEmailAndPassword(mail.getText().toString(), password.getText().toString());

                                                mail.setText("");
                                                name.setText("");
                                                password.setText("");
                                                passwordVerification.setText("");
                                                phone.setText("");
                                                surname.setText("");
                                                radioButton.setChecked(false);

                                                setHints();
                                            } else {
                                                Toast.makeText(context, getString(R.string.failedCreatingAnewUser), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    }
                }
            }
        });
    }



}