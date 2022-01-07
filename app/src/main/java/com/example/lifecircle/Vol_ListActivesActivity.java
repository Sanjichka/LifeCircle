package com.example.lifecircle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Vol_ListActivesActivity extends AppCompatActivity {


    private RecyclerView mRecyclerView;
    private VolAdapter mAdapter;
    ArrayList<String> reqNameArray = new ArrayList<String>();
    ArrayList<String> dateTimeArray = new ArrayList<String>();
    ArrayList<String> opArray = new ArrayList<String>();
    ArrayList<String> latArray = new ArrayList<String>();
    ArrayList<String> longArray = new ArrayList<String>();
    ArrayList<String> ratingArray = new ArrayList<String>();
    ArrayList<String> idArray = new ArrayList<String>();
    ArrayList<String> stateArray = new ArrayList<String>();
    private Button applybtn;

    public static final String TAG = "TAG";
    private ProgressDialog progrDialog;
    private FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vol_list_actives);




        Toolbar toolbar_logout = findViewById(R.id.toolbar_logout);
        setSupportActionBar(toolbar_logout);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }









        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        progrDialog = new ProgressDialog(this);
        userID = firebaseAuth.getCurrentUser().getUid();
        db.collection("reqs_all")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                JSONObject jsonObject = new JSONObject(document.getData());



                                try {
                                    String requestName = jsonObject.getString("requestName");
                                    String dateTime = jsonObject.getString("dateTime");
                                    String lats = jsonObject.getString("lat");
                                    String longs = jsonObject.getString("long");
                                    String state = jsonObject.getString("state");
                                    Log.d(TAG, document.getId() + " => " + requestName);
                                    if(state.equals("pending") || state.equals("active")) {
                                        reqNameArray.add(requestName);
                                        dateTimeArray.add(dateTime);
                                        latArray.add(lats);
                                        longArray.add(longs);
                                        stateArray.add(state);


                                        idArray.add(jsonObject.getString("id"));
                                    }

                                    Log.d(TAG, document.getId() + " => " + idArray);




                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }

                        for(int i=0; i<idArray.size();i++) {
                            DocumentReference docRef = db.collection("FullNamePhoneEmail").document(idArray.get(i));
                            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task1) {
                                    if (task1.isSuccessful()) {
                                        DocumentSnapshot document1 = task1.getResult();
                                        if (document1.exists()) {
                                            Log.d(TAG, "DocumentSnapshot data: " + document1.getData());
                                            JSONObject jsonObject1 = new JSONObject(document1.getData());
                                            try {
                                                opArray.add(jsonObject1.getString("fullname"));
                                                Log.d(TAG, "HALOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO: " + opArray);
                                                ratingArray.add(jsonObject1.getString("rating"));
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                        } else {
                                            Log.d(TAG, "No such document");
                                        }
                                    } else {
                                        Log.d(TAG, "get failed with ", task1.getException());
                                    }




                                    mRecyclerView = (RecyclerView) findViewById(R.id.listvol);
                                    mRecyclerView.setHasFixedSize(true);
                                    mRecyclerView.setLayoutManager(new LinearLayoutManager(Vol_ListActivesActivity.this));
                                    mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                                    mAdapter = new VolAdapter(Vol_ListActivesActivity.this, reqNameArray, dateTimeArray, opArray, ratingArray, latArray, longArray, idArray, stateArray);
                                    mRecyclerView.setAdapter(mAdapter);
                                }
                            });
                        }


                    }
                });

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