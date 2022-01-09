package com.example.lifecircle;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
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

public class VolDoneAdapter extends RecyclerView.Adapter<VolDoneAdapter.VolViewHolder> {
    private LayoutInflater mInflater;
    private Context mContext;
    private FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    String userID;
    public static final String TAG = "TAG";
    ArrayList<String> reqNameArray;
    ArrayList<String> dateTimeArray;
    ArrayList<String> ratingArray;
    ArrayList<String> opArray;
    ArrayList<String> latArray;
    ArrayList<String> longArray;
    ArrayList<String> idArray;
    ArrayList<String> stateArray;
    ArrayList<String> docIDArray;

    public class VolViewHolder extends RecyclerView.ViewHolder {
        public TextView myName;
        public TextView myDateTime;
        public TextView myState;
        public TextView myDistance;
        public TextView myRating;
        public TextView myWhose;
        public Spinner spinner;


        public ImageView Pic;
        public VolViewHolder(View itemView) {
            super(itemView);
            myName = (TextView) itemView.findViewById(R.id.Name);
            myDateTime = (TextView) itemView.findViewById(R.id.DateTime);
            myState = (TextView) itemView.findViewById(R.id.State);
            myDistance = (TextView) itemView.findViewById(R.id.distance);
            myRating = (TextView) itemView.findViewById(R.id.rating);
            myWhose = (TextView) itemView.findViewById(R.id.whose);

            spinner = (Spinner) itemView.findViewById(R.id.spinner_vol);

            Pic = (ImageView) itemView.findViewById(R.id.picture);
        }
    }

    public VolDoneAdapter(Context context, ArrayList<String> reqNameArray, ArrayList<String> dateTimeArray, ArrayList<String> latArray, ArrayList<String> longArray, ArrayList<String> idArray, ArrayList<String> stateArray, ArrayList<String> docIDArray) {
        mInflater = LayoutInflater.from(context);
        this.mContext = context;



        this.reqNameArray = reqNameArray;
        this.dateTimeArray = dateTimeArray;
        this.opArray = opArray;
        this.ratingArray = ratingArray;
        this.latArray = latArray;
        this.longArray = longArray;
        this.idArray = idArray;
        this.docIDArray = docIDArray;
        this.stateArray = stateArray;

    }

    @NonNull
    @Override
    public VolViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.row_list_vol_done, parent, false);
        return new VolViewHolder(mItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull VolViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {
            viewHolder.myName.setText("Requirement Name: " + reqNameArray.get(position));
            viewHolder.myDateTime.setText("DateTime: " + dateTimeArray.get(position));
            //calculate distance
            db = FirebaseFirestore.getInstance();
            firebaseAuth = FirebaseAuth.getInstance();
            userID = firebaseAuth.getCurrentUser().getUid();
            String opID = idArray.get(position);
            if(stateArray.get(position).equals("done") || stateArray.get(position).equals("rated_o")) {
                viewHolder.spinner.setVisibility(View.VISIBLE);
            }



            DocumentReference docRefz = db.collection("FullNamePhoneEmail").document(opID);
            docRefz.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task1) {
                    if (task1.isSuccessful()) {
                        DocumentSnapshot document1 = task1.getResult();
                        if (document1.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: " + document1.getData());
                            JSONObject jsonObject1 = new JSONObject(document1.getData());
                            try {

                                viewHolder.myWhose.setText("Older Person : " + jsonObject1.getString("fullname"));
                                if (jsonObject1.getString("rating").equals("0")) {

                                    viewHolder.myRating.setText("Older Person's Rating : " + jsonObject1.getString("rating")+"/5");

                                } else {
                                    String[] rate = new String[2];
                                    rate = jsonObject1.getString("rating").split("/");
                                    double a = (double) (Double.parseDouble(rate[0]) / Integer.parseInt(rate[1]));
                                    double round = Math.round(a * 100.0) / 100.0;
                                    viewHolder.myRating.setText("Older Person's Rating : " + round +"/5");
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

                }
            });






            final double[] req_lat = {Double.parseDouble(latArray.get(position))};
            final double[] req_long = {Double.parseDouble(longArray.get(position))};
            String r = "vol_loc_" + userID;
            DocumentReference docRef = db.collection(r).document(userID);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task1) {
                    if (task1.isSuccessful()) {
                        DocumentSnapshot document1 = task1.getResult();
                        if (document1.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: " + document1.getData());
                            JSONObject jsonObject1 = new JSONObject(document1.getData());
                            try {
                                Log.d(TAG, "VALUEEEEEEEEEEEEEEEEEEEEEEEE: " + jsonObject1.getString("lat"));
                                if (jsonObject1.getString("lat").equals("0")) {
                                    viewHolder.myDistance.setText("Distance(km) : go back & set your location");
                                } else {
                                    jsonObject1.getString("lat");
                                    double vol_lat = Double.parseDouble(jsonObject1.getString("lat"));
                                    double vol_long = Double.parseDouble(jsonObject1.getString("long"));

                                    vol_lat = Math.toRadians(vol_lat);
                                    vol_long = Math.toRadians(vol_long);
                                    req_lat[0] = Math.toRadians(req_lat[0]);
                                    req_long[0] = Math.toRadians(req_long[0]);
                                    double dlon = Math.abs(req_long[0] - vol_long);
                                    double dlat = Math.abs(req_lat[0] - vol_lat);
                                    double a = Math.pow(Math.sin(dlat / 2), 2)
                                            + Math.cos(vol_lat) * Math.cos(req_lat[0])
                                            * Math.pow(Math.sin(dlon / 2), 2);
                                    double c = 2 * Math.asin(Math.sqrt(a));
                                    double radius = 6371;

                                    viewHolder.myDistance.setText("Distance(km) : " + String.format("%.2f", c * radius));


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
                }
            });
            viewHolder.myState.setText("State: " + stateArray.get(position));





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

                    DocumentReference docRef = db.collection("FullNamePhoneEmail").document(opID);
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

                                            viewHolder.myRating.setText("Older Person's Rating : " + rating[0]);

                                        } else {
                                            rate = rating[0].split("/");
                                            double a = (double) (Double.parseDouble(rate[0]) / Integer.parseInt(rate[1]));
                                            double round = Math.round(a * 100.0) / 100.0;
                                            rating[0] = Double.toString(round);
                                            rating[0] += "/5";
                                            finalrate[1] = Integer.toString(Integer.parseInt(rate[1])+1);
                                            finalrate[0] = Integer.toString(Integer.parseInt(rate[0])+Integer.parseInt(finalIn_rating));

                                            viewHolder.myRating.setText("Older Person's Rating : " + rating[0]);
                                        }


                                        String s = finalrate[0] + "/" + finalrate[1];
                                        Map<String, Object> map1 = new HashMap<>();
                                        map1.put("rating", s);


                                        db.collection("FullNamePhoneEmail").document(opID).update(map1);


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
                    if(stateArray.get(position).equals("done")) {
                        map2.put("state", "rated_v");
                        db.collection("reqs_all").document(docIDArray.get(position)).update(map2);
                        viewHolder.myState.setText("State: rated_v");
                    }
                    else{
                        map2.put("state", "rated_vo");
                        db.collection("reqs_all").document(docIDArray.get(position)).update(map2);
                        viewHolder.myState.setText("State: rated_vo");
                    }





                    viewHolder.spinner.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
    }

    @Override
    public int getItemCount() {
        return reqNameArray.size();
    }


}

