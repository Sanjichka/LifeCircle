package com.example.lifecircle;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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

public class VolAdapter extends RecyclerView.Adapter<VolAdapter.VolViewHolder> {
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

    public class VolViewHolder extends RecyclerView.ViewHolder {
        public TextView myName;
        public TextView myDateTime;
        public TextView myState;
        public TextView myDistance;
        public TextView myRating;
        public TextView myWhose;
        public Button apply_btn;

        public ImageView Pic;
        public VolViewHolder(View itemView) {
            super(itemView);
            myName = (TextView) itemView.findViewById(R.id.Name);
            myDateTime = (TextView) itemView.findViewById(R.id.DateTime);
            myState = (TextView) itemView.findViewById(R.id.State);
            myDistance = (TextView) itemView.findViewById(R.id.distance);
            myRating = (TextView) itemView.findViewById(R.id.rating);
            myWhose = (TextView) itemView.findViewById(R.id.whose);
            apply_btn = (Button) itemView.findViewById(R.id.btnapply);

            Pic = (ImageView) itemView.findViewById(R.id.picture);
        }
    }

    public VolAdapter(Context context, ArrayList<String> reqNameArray, ArrayList<String> dateTimeArray, ArrayList<String> opArray, ArrayList<String> ratingArray, ArrayList<String> latArray, ArrayList<String> longArray, ArrayList<String> idArray) {
        mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.reqNameArray = reqNameArray;
        this.dateTimeArray = dateTimeArray;
        this.opArray = opArray;
        this.ratingArray = ratingArray;
        this.latArray = latArray;
        this.longArray = longArray;
        this.idArray = idArray;
    }

    @NonNull
    @Override
    public VolViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.row_list_actives, parent, false);
        return new VolViewHolder(mItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull VolViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {
        viewHolder.myName.setText("Requirement Name: "+reqNameArray.get(position));
        viewHolder.myDateTime.setText("DateTime: "+dateTimeArray.get(position));
        viewHolder.myWhose.setText("Older Person : "+opArray.get(position));
        //calculate distance
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();

        final double[] req_lat = {Double.parseDouble(latArray.get(position))};
        final double[] req_long = {Double.parseDouble(longArray.get(position))};
        String r = "vol_loc_"+userID;
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
                            if(jsonObject1.getString("lat").equals("0")) {
                                viewHolder.myDistance.setText("Distance(km) : go back & set your location");
                            }
                            else {
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
                                        * Math.pow(Math.sin(dlon / 2),2);
                                double c = 2 * Math.asin(Math.sqrt(a));
                                double radius = 6371;

                                viewHolder.myDistance.setText("Distance(km) : " + String.format("%.2f", c*radius));


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
        viewHolder.myRating.setText("Rating : "+ratingArray.get(position));
        viewHolder.myState.setText("State: Active");

        viewHolder.apply_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(mContext, DetailsVolPerReqActivity.class);
//                mContext.startActivity(intent);
                String OPid = idArray.get(position);

                Map<String, Object> map1 = new HashMap<>();
                map1.put("state", "pending");

                DocumentReference sfRef = db.collection("reqs_all").document();
                db.collection("reqs_all")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                        {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task)
                            {
                                if(task.isSuccessful())
                                {
                                    if(Objects.requireNonNull(task.getResult()).size() > 0)
                                    {
                                        int i = 0; //temporary index
                                        Map<Integer, Object> _docList = new HashMap<>(); // A hashMap to store document id

                                        for(DocumentSnapshot _documentSnapshot : Objects.requireNonNull(task.getResult()))
                                        {
                                            _docList.put(i, _documentSnapshot.getId());//Store temporary index (i) mapping to each document

                                            //Checks if i equals to the documentIndex needed.
                                            //Let's say documentIndex is 2 (Declared as a parameter of a method).
                                            if(i == position)
                                            {
                                                //Do something with the document at that index.
                                                db.collection("reqs_all").document(_documentSnapshot.getId()).update(map1);
                                                return;
                                            }

                                            i++; //Increase the temp index if the statement is not true
                                        }
                                    }
                                }
                            }
                        });

            }
        });


    }

    @Override
    public int getItemCount() {
        return reqNameArray.size();
    }


}

