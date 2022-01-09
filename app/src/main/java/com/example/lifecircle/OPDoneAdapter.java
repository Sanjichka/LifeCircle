package com.example.lifecircle;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class OPDoneAdapter extends RecyclerView.Adapter<OPDoneAdapter.DoneViewHolder> {
    private LayoutInflater mInflater;
    private Context mContext;
    private FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    public static final String TAG = "TAG";
    private static final int REQ_CODE = 789132;
    ArrayList<String> reqNameArray;
    ArrayList<String> volIDArray;
    ArrayList<String> stateArray;
    ArrayList<String> docIDArray;


    public class DoneViewHolder extends RecyclerView.ViewHolder {
        public TextView myName;
        public TextView myState;
        public TextView volName;
        public TextView volRating;
        public TextView volEmail;
        public TextView volPhone;
        public Spinner spinner;

        public ImageView Pic;
        public DoneViewHolder(View itemView) {
            super(itemView);
            myName = (TextView) itemView.findViewById(R.id.Name);
            myState = (TextView) itemView.findViewById(R.id.State);
            volName = (TextView) itemView.findViewById(R.id.volName);
            volEmail = (TextView) itemView.findViewById(R.id.volEmail);
            volRating = (TextView) itemView.findViewById(R.id.volRating);
            volPhone = (TextView) itemView.findViewById(R.id.volPhone);
            spinner = (Spinner) itemView.findViewById(R.id.spinner);

            Pic = (ImageView) itemView.findViewById(R.id.picture);
        }
    }

    public OPDoneAdapter(Context context, ArrayList<String> reqNameArray, ArrayList<String> stateArray, ArrayList<String> volIDArray, ArrayList<String> docIDArray) {
        mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.reqNameArray = reqNameArray;
        this.stateArray = stateArray;
        this.volIDArray = volIDArray;
        this.docIDArray = docIDArray;
    }

    @NonNull
    @Override
    public DoneViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.row_list_op_done, parent, false);
        return new DoneViewHolder(mItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DoneViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {
        viewHolder.myName.setText("Requirement Name: "+reqNameArray.get(position));
        viewHolder.myState.setText("State : "+stateArray.get(position));
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        String volontID = volIDArray.get(position);
        if(stateArray.get(position).equals("done")) {
            viewHolder.spinner.setVisibility(View.VISIBLE);
        }


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

                            if (jsonObject1.getString("rating").equals("0")) {

                                viewHolder.volRating.setText("Volunteer's Rating : " + jsonObject1.getString("rating")+"/5");

                            } else {
                                String[] rate = new String[2];
                                rate = jsonObject1.getString("rating").split("/");
                                double a = (double) (Double.parseDouble(rate[0]) / Integer.parseInt(rate[1]));
                                double round = Math.round(a * 100.0) / 100.0;
                                viewHolder.volRating.setText("Volunteer's Rating : " + round +"/5");
                            }
                            viewHolder.volPhone.setText("Volunteer's Phone Number : " + jsonObject1.getString("phone"));
                            viewHolder.volEmail.setText("Volunteer's Email : " + jsonObject1.getString("email"));
                            viewHolder.volName.setText("Volunteer's Name : " + jsonObject1.getString("fullname"));


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


            viewHolder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int pos, long id) {

                    if (!viewHolder.spinner.getSelectedItem().equals("Select")) {
                        // your code here
                        String in_rating = "0";
                        if (viewHolder.spinner.getSelectedItem().equals("1")) {
                            in_rating = "1";
                        } else if (viewHolder.spinner.getSelectedItem().equals("2")) {
                            in_rating = "2";
                        } else if (viewHolder.spinner.getSelectedItem().equals("3")) {
                            in_rating = "3";
                        } else if (viewHolder.spinner.getSelectedItem().equals("4")) {
                            in_rating = "4";
                        } else if (viewHolder.spinner.getSelectedItem().equals("5")) {
                            in_rating = "5";
                        }

                        Log.d(TAG, "IN-RATINGGGGGGGGGGGGGGGGGGGGGGGGGGGG: " + in_rating);


                        final String[] rating = new String[1];
                        //prvo izvlechi go rating, pa calculate, pa update

                        DocumentReference docRef = db.collection("FullNamePhoneEmail").document(volontID);
                        String finalIn_rating = in_rating;
                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task1) {
                                if (task1.isSuccessful()) {
                                    DocumentSnapshot document1 = task1.getResult();
                                    if (document1.exists()) {
                                        Log.d(TAG, "DocumentSnapshot data: " + document1.getData());
                                        JSONObject jsonObject1 = new JSONObject(document1.getData());
                                        try {
                                            rating[0] = jsonObject1.getString("rating");
                                            String[] rate = new String[2];
                                            String[] finalrate = new String[2];
                                            if (rating[0].equals("0")) {
                                                rating[0] = "" + finalIn_rating + "/5";
                                                finalrate[0] = finalIn_rating;
                                                finalrate[1] = "1";

                                                viewHolder.volRating.setText("Volunteer's Rating : " + rating[0]);

                                            } else {
                                                rate = rating[0].split("/");
                                                double a = (double) (Double.parseDouble(rate[0]) / Integer.parseInt(rate[1]));
                                                double round = Math.round(a * 100.0) / 100.0;
                                                rating[0] = Double.toString(round);
                                                rating[0] += "/5";
                                                finalrate[1] = Integer.toString(Integer.parseInt(rate[1])+1);
                                                finalrate[0] = Integer.toString(Integer.parseInt(rate[0])+Integer.parseInt(finalIn_rating));

                                                viewHolder.volRating.setText("Volunteer's Rating : " + rating[0]);
                                            }


                                            String s = finalrate[0] + "/" + finalrate[1];
                                            Map<String, Object> map1 = new HashMap<>();
                                            map1.put("rating", s);


                                            db.collection("FullNamePhoneEmail").document(volontID).update(map1);


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


                        Map<String, Object> map2 = new HashMap<>();
                        map2.put("state", "rated");

                        db.collection("reqs_all").document(docIDArray.get(position)).update(map2);
                        viewHolder.myState.setText("State: rated");



                        viewHolder.spinner.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // your code here
                }

            });

    }

 /*
        *
        * viewHolder.rate_op_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(mContext, Pop.class);
                mContext.startActivity(intent);

                Intent intent2 = ((Activity) mContext).getIntent();
                String in_rating = intent2.getStringExtra("rating");
                Log.d(TAG, "IN-RATING data: " + in_rating);


                final String[] rating = new String[1];
                //prvo izvlechi go rating, pa calculate, pa update

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
                                    rating[0] = jsonObject1.getString("rating");
                                    if(rating[0].equals("0")) {
                                        rating[0] = "" + (Integer.parseInt(in_rating) * 2) + "/10";
                                    }
                                    else {
                                        String[] rate = rating[0].split("/");
                                        double a = Double.parseDouble(rate[0]) / Double.parseDouble(rate[1]) * 10;
                                        double round = Math.round(a * 100.0) / 100.0;
                                        rating[0] = Double.toString(round);
                                        rating[0] += "/10";
                                    }


                                    Map<String, Object> map1 = new HashMap<>();
                                    map1.put("rating", rating[0]);



                                    db.collection("FullNamePhoneEmail").document(volontID).update(map1);
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




                viewHolder.rate_op_btn.setVisibility(View.INVISIBLE);

            }


        });*/






    @Override
    public int getItemCount() {
        return reqNameArray.size();
    }


}

