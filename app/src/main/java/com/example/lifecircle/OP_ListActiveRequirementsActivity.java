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
import android.content.res.TypedArray;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class OP_ListActiveRequirementsActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RequirementsAdapter mAdapter;
    ArrayList<String> reqNameArray = new ArrayList<String>();
    ArrayList<String> descriptionArray = new ArrayList<String>();
    ArrayList<String> urgentArray = new ArrayList<String>();
    ArrayList<String> dateTimeArray = new ArrayList<String>();
    ArrayList<String> timesArray = new ArrayList<String>();

    public static final String TAG = "TAG";
    private ProgressDialog progrDialog;
    private FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_op_list_active_requirements);

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
        db.collection("req_"+userID)
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
                            String description = jsonObject.getString("description");
                            String dateTime = jsonObject.getString("dateTime");
                            String urgent = jsonObject.getString("urgent");
                            String times = jsonObject.getString("times");
                            Log.d(TAG, document.getId() + " => " + requestName);
                            reqNameArray.add(requestName);
                            descriptionArray.add(description);
                            dateTimeArray.add(dateTime);
                            urgentArray.add(urgent);
                            timesArray.add(times);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }

                mRecyclerView = (RecyclerView) findViewById(R.id.list);
                mRecyclerView.setHasFixedSize(true);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(OP_ListActiveRequirementsActivity.this));
                mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                mAdapter = new RequirementsAdapter(OP_ListActiveRequirementsActivity.this, reqNameArray, descriptionArray, dateTimeArray, urgentArray, timesArray);
                mRecyclerView.setAdapter(mAdapter);
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

