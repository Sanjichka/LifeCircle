package com.example.lifecircle;

import android.app.Activity;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "TAG";
    private EditText emailET, passwordET;
    private Button SignInBtn;
    private TextView SignUpTV;
    private ProgressDialog progrDialog;
    private FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    String userID;
    String type = "o";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        emailET = findViewById(R.id.email);
        passwordET = findViewById(R.id.password);
        SignInBtn = findViewById(R.id.login);
        progrDialog = new ProgressDialog(this);
        SignUpTV = findViewById(R.id.signUpTV);
        SignInBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Login();
            }
        });
        SignUpTV.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
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

    private void Login() {
        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();
        if(TextUtils.isEmpty(email)) {
            emailET.setError("Enter your email!");
            return;
        }
        else if(TextUtils.isEmpty(password)) {
            passwordET.setError("Enter your password!");
            return;
        }
        progrDialog.setMessage("Please wait...");
        progrDialog.show();
        progrDialog.setCanceledOnTouchOutside(false);
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Successfully logged in", Toast.LENGTH_LONG).show();

                    userID = firebaseAuth.getCurrentUser().getUid();
                    //READ DATA

                    DocumentReference docRef = db.collection("types").document(userID);

                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {

                                    JSONObject jsonObject = new JSONObject(document.getData());
                                    try {
                                        type = jsonObject.getString("type");
                                        Log.d(TAG, document.getId() + " => " + type);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    Log.d(TAG, "DocumentSnapshot data: " + type);


                                } else {
                                    Log.d(TAG, "No such document");
                                }
                                if(!type.equals("olderperson")) {
                                    Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                                    intent.putExtra("type", type);
                                    startActivity(intent);
                                    finish();
                                }
                                else{
                                    Intent intent = new Intent(MainActivity.this, DashboardOPActivity.class);
                                    intent.putExtra("type", type);
                                    startActivity(intent);
                                    finish();
                                }

                            } else {
                                Log.d(TAG, "get failed with ", task.getException());
                            }
                        }
                    });


                }
                else {
                    Toast.makeText(MainActivity.this, "Sign In fail!", Toast.LENGTH_LONG).show();
                }
                progrDialog.dismiss();
            }
        });
    }
}