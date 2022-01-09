package com.example.lifecircle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

public class DetailsVolPerReqActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    public static final String TAG = "TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_vol_per_req);


        Toolbar toolbar_logout = findViewById(R.id.toolbar_logout);
        setSupportActionBar(toolbar_logout);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        String volontID = intent.getStringExtra("volID");

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();


        DocumentReference docRef = db.collection("FullNamePhoneEmail").document(volontID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task1) {
                if (task1.isSuccessful()) {
                    DocumentSnapshot document1 = task1.getResult();
                    if (document1.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document1.getData());
                        JSONObject jsonObject1 = new JSONObject(document1.getData());
                        try {
                            TextView volName = findViewById(R.id.volNameInfo);
                            TextView volPhone = findViewById(R.id.volPhoneInfo);
                            TextView volEmail = findViewById(R.id.volEmailInfo);
                            TextView volRating = findViewById(R.id.volRatingInfo);
                            volName.setText("Name : " + jsonObject1.getString("fullname"));
                            volPhone.setText("Phone Number : " + jsonObject1.getString("phone"));
                            volEmail.setText("Email : " + jsonObject1.getString("email"));

                            if (jsonObject1.getString("rating").equals("0")) {
                                volRating.setText("Rating : " + jsonObject1.getString("rating")+"/5");

                            } else {
                                String[] rate = new String[2];
                                rate = jsonObject1.getString("rating").split("/");
                                double a = (double) (Double.parseDouble(rate[0]) / Integer.parseInt(rate[1]));
                                double round = Math.round(a * 100.0) / 100.0;
                                volRating.setText("Rating : " + round +"/5");
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task1.getException());
                }
            }});

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_dash_op, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        Intent intent = null;

        switch (item.getItemId()) {
            case R.id.action_logout:
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }
}