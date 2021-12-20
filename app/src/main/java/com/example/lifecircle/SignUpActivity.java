package com.example.lifecircle;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    public static final String TAG = "TAG";
    private EditText emailET, passwordET, passwordET1;
    private Button SignUpBtn;
    private String type="olderperson";
    private TextView SignInTV;
    private ProgressDialog progrDialog;
    private FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    String userID;
    private EditText fullnameET, phoneET;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        emailET = findViewById(R.id.email);
        passwordET = findViewById(R.id.password);
        passwordET1 = findViewById(R.id.password1);
        phoneET = findViewById(R.id.phone);
        fullnameET = findViewById(R.id.fullname);
        SignUpBtn = findViewById(R.id.register);
        progrDialog = new ProgressDialog(this);
        SignInTV = findViewById(R.id.signInTV);
        SignUpBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Register();
            }
        });
        SignInTV.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });


        Toolbar toolbar_start = findViewById(R.id.toolbar_start);
        setSupportActionBar(toolbar_start);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_start, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        Intent intent = null;

        switch (item.getItemId()) {
            case R.id.action_signin:
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_signup:
                intent = new Intent(this, SignUpActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }


    private void Register() {
        String email = emailET.getText().toString().trim();
        String password = passwordET.getText().toString().trim();
        String password1 = passwordET1.getText().toString().trim();
        String fullname = fullnameET.getText().toString().trim();
        String phone = phoneET.getText().toString().trim();
        if(TextUtils.isEmpty(email)) {
            emailET.setError("Enter your email!");
            return;
        }
        else if(TextUtils.isEmpty(password)) {
            passwordET.setError("Enter your password!");
            return;
        }
        else if(TextUtils.isEmpty(password1)) {
            passwordET1.setError("Confirm your password!");
            return;
        }
        else if(password.length()<4) {
            passwordET.setError("Length should be >4!");
            return;
        }
        else if(!password1.equals(password)) {
            passwordET1.setError("Incorrect confirmation!");
            return;
        }
        else if(!isValidEmail(email)) {
            emailET.setError("Invalid email");
            return;
        }
        else if(!isValidPhone(phone)) {
            phoneET.setError("Invalid phone");
            return;
        }
        progrDialog.setMessage("Please wait...");
        progrDialog.show();
        progrDialog.setCanceledOnTouchOutside(false);
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(SignUpActivity.this, "Successfully registered", Toast.LENGTH_LONG).show();

                    userID = firebaseAuth.getCurrentUser().getUid();
                    DocumentReference documentReference = db.collection("types").document(userID);
                    DocumentReference DRFullNamePhoneEmail = db.collection("FullNamePhoneEmail").document(userID);

                    Map<String, Object> typeh = new HashMap<>();
                    typeh.put("email", email);
                    typeh.put("type", type);

                    Map<String, Object> fnpeh = new HashMap<>();
                    fnpeh.put("fullname", fullname);
                    fnpeh.put("phone", phone);
                    fnpeh.put("email", email);

                    documentReference.set(typeh).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d(TAG, "on Success: " + userID);
                        }
                    });

                    DRFullNamePhoneEmail.set(fnpeh).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d(TAG, "on Success: " + userID);
                        }
                    });

                    if(type.equals("olderperson")) {
                        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else {
                        Intent intent = new Intent(SignUpActivity.this, DashboardActivity.class);
                        intent.putExtra("type", type);
                        startActivity(intent);
                        finish();
                    }

                }
                else {
                    Toast.makeText(SignUpActivity.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
                progrDialog.dismiss();
            }
        });
    }

    private boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
    private boolean isValidPhone(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.PHONE.matcher(target).matches());
    }


    public void radioClick(View view) {
        if(view.getId()==R.id.rb1) {
            type = "organizer";
        }
        else if(view.getId()==R.id.rb2) {
            type = "volunteer";
        }
        else if(view.getId()==R.id.rb3) {
            type = "olderperson";
        }
    }
}