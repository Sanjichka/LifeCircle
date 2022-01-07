package com.example.lifecircle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.firestore.WriteBatch;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DashboardVolActivity extends AppCompatActivity {

    public static final String TAG = "TAG";
    private static final int REQ_CODE = 789123;
    private Button listmine;
    private Button listactives;
    public String lat_my = "0";
    public String long_my = "0";
    private FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboardvol);

        Intent intentStart = getIntent();
        String type = intentStart.getStringExtra("type");

        listmine = findViewById(R.id.listmine);
        listactives = findViewById(R.id.listactives);

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();


        userID = firebaseAuth.getCurrentUser().getUid();
        String r = "vol_loc_"+userID;


        DocumentReference dR = db.collection(r).document(userID);

        dR.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task1) {
                if (task1.isSuccessful()) {
                    DocumentSnapshot document1 = task1.getResult();
                    if (document1.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document1.getData());
                        JSONObject jsonObject1 = new JSONObject(document1.getData());
                        try {
                            Log.d(TAG, "VALUEEEEEEEEEEEEEEEEEEEEEEEE: " + jsonObject1.getString("lat"));



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else {
                        Log.d(TAG, "No such document");
                        Map<String, Object> vol = new HashMap<>();
                        vol.put("lat", "0");
                        vol.put("long", "0");


                        dR.set(vol).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d(TAG, "on Success: " + userID);
                            }
                        });
                    }
                } else {
                    Log.d(TAG, "get failed with ", task1.getException());
                }
            }
        });



        listmine.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(DashboardVolActivity.this, Vol_ListMineActivity.class);
                startActivity(intent1);
                //finish();
            }
        });
        listactives.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(DashboardVolActivity.this, Vol_ListActivesActivity.class);
                startActivity(intent1);
                //finish();
            }
        });


        Toolbar toolbar_logout = findViewById(R.id.toolbar_logout);
        setSupportActionBar(toolbar_logout);
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


    public void setMap(View view) {
        Intent intent = new Intent(this, MapsVolActivity.class);
        intent.putExtra("lat", lat_my);
        intent.putExtra("long", long_my);
        startActivityForResult(intent, REQ_CODE);
    }
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == REQ_CODE) {
            // came back from SecondActivity
            lat_my = intent.getStringExtra("lat");
            long_my = intent.getStringExtra("long");
            userID = firebaseAuth.getCurrentUser().getUid();
            String r = "vol_loc_"+userID;

            Map<String, Object> vol = new HashMap<>();
            vol.put("lat", lat_my);
            vol.put("long", long_my);


            db.collection(r).document(userID).update(vol);

            /*db.runTransaction(new Transaction.Function<Void>() {
                @Override
                public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                    DocumentSnapshot snapshot = transaction.get(sfRef);

                    // Note: this could be done without a transaction
                    //       by updating the population using FieldValue.increment()
                    transaction.update(sfRef, "lat", lat_my);
                    transaction.update(sfRef, "long", long_my);

                    // Success
                    return null;
                }
            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "Transaction success!");
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "Transaction failure.", e);
                }
            });
*/

        }
    }
}