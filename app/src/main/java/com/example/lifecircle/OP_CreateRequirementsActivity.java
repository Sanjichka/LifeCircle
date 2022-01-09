package com.example.lifecircle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OP_CreateRequirementsActivity extends AppCompatActivity {

    public static final String TAG = "TAG";
    private static final int REQ_CODE = 123789;
    EditText datetime;
    private Button createReqBtn;
    private ProgressDialog progrDialog;
    private FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    String userID;
    String urgent="Not urgent";
    String times="One-time";
    private EditText textareaDescr;
    String textArea;
    String reqName;
    String dateTime;
    private EditText requestName;
    public String lat_my = "0";
    public String long_my = "0";

    DocumentReference documentReference1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_op_create_requirements);

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        progrDialog = new ProgressDialog(this);

        datetime = findViewById(R.id.datetimepicker);
        datetime.setInputType(InputType.TYPE_NULL);
        datetime.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                showDateTimeDialog(datetime);
            }
        });

        requestName = findViewById(R.id.namereq);
        textareaDescr = findViewById(R.id.textareadescr);


        createReqBtn = findViewById(R.id.createreqbtn);
        createReqBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                createRequest();
            }
        });

        Toolbar toolbar_logout = findViewById(R.id.toolbar_logout);
        setSupportActionBar(toolbar_logout);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void createRequest() {
        textArea = textareaDescr.getText().toString().trim();
        reqName = requestName.getText().toString().trim();
        dateTime = datetime.getText().toString().trim();

        progrDialog.setMessage("Please wait...");
        progrDialog.show();
        progrDialog.setCanceledOnTouchOutside(false);

        userID = firebaseAuth.getCurrentUser().getUid();
        String r = "req_"+userID;
        DocumentReference documentReference = db.collection(r).document();

        DocumentReference DRCount = db.collection("CountReqs").document("reqs");
        DRCount.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task1) {
                if (task1.isSuccessful()) {
                    DocumentSnapshot document1 = task1.getResult();
                    if (document1.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document1.getData());
                        JSONObject jsonObject1 = new JSONObject(document1.getData());
                        try {
                            String numb = jsonObject1.getString("count");
                            Log.d(TAG, "HALOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO: " + numb);
                            Integer a = Integer.parseInt(numb) - 1;

                            documentReference1 = db.collection("reqs_all").document(Integer.toString(a));
                            Map<String, Object> reqs = new HashMap<>();
                            reqs.put("requestName", reqName);
                            reqs.put("description", textArea);
                            reqs.put("dateTime", dateTime);
                            reqs.put("urgent", urgent);
                            reqs.put("state", "active");
                            reqs.put("times", times);
                            reqs.put("id", userID);
                            reqs.put("lat", lat_my);
                            reqs.put("long", long_my);
                            reqs.put("volID", "none");
                            reqs.put("doc_id", documentReference1.getId());

                            documentReference1.set(reqs).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d(TAG, "on Success: " + userID);
                                }
                            });


                            Map<String, Object> map1 = new HashMap<>();
                            map1.put("count", Integer.toString(a));
                            db.collection("CountReqs").document("reqs").update(map1);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task1.getException());
                }
            }
        });










//        db.collection("reqs_all").get()
//                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                        if (!queryDocumentSnapshots.isEmpty()) {
//                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
//                            for (DocumentSnapshot d : list) {
//
//                            }
//                        }
//                    }
//                });



        Map<String, Object> reqs1 = new HashMap<>();
        reqs1.put("requestName", reqName);
        reqs1.put("description", textArea);
        reqs1.put("dateTime", dateTime);
        reqs1.put("urgent", urgent);
        reqs1.put("times", times);
        reqs1.put("id", userID);
        reqs1.put("lat", lat_my);
        reqs1.put("long", long_my);

        documentReference.set(reqs1).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG, "on Success: " + userID);
            }
        });


        Intent intent = new Intent(OP_CreateRequirementsActivity.this, DashboardOPActivity.class);
        startActivity(intent);
        finish();
    }

    private void showDateTimeDialog(EditText datetime) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);

                TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hour, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY, hour);
                        calendar.set(Calendar.MINUTE, minute);

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy-MM-dd HH:mm");

                        datetime.setText(simpleDateFormat.format(calendar.getTime()));
                    }
                };

                new TimePickerDialog(OP_CreateRequirementsActivity.this, timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
            }
        };
        new DatePickerDialog(OP_CreateRequirementsActivity.this, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
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

    public void radioClick1(View view) {
        if(view.getId()==R.id.rb11) {
            times = "One-time";
        }
        else if(view.getId()==R.id.rb12) {
            times = "Multiple-Times";
        }
    }
    public void radioClick2(View view) {
        if(view.getId()==R.id.rb21) {
            urgent = "Not urgent";
        }
        else if(view.getId()==R.id.rb22) {
            urgent = "Urgent";
        }
    }

    public void setMap(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
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
        }
    }
}
