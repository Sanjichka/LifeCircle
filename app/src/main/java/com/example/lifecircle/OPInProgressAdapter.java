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

public class OPInProgressAdapter extends RecyclerView.Adapter<OPInProgressAdapter.InProgressViewHolder> {
    private LayoutInflater mInflater;
    private Context mContext;
    private FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    public static final String TAG = "TAG";
    ArrayList<String> reqNameArray;
    ArrayList<String> volIDArray;
    ArrayList<String> stateArray;

    public class InProgressViewHolder extends RecyclerView.ViewHolder {
        public TextView myName;
        public TextView myState;
        public TextView volName;
        public TextView volRating;
        public TextView volEmail;
        public TextView volPhone;

        public ImageView Pic;
        public InProgressViewHolder(View itemView) {
            super(itemView);
            myName = (TextView) itemView.findViewById(R.id.Name);
            myState = (TextView) itemView.findViewById(R.id.State);
            volName = (TextView) itemView.findViewById(R.id.volName);
            volEmail = (TextView) itemView.findViewById(R.id.volEmail);
            volRating = (TextView) itemView.findViewById(R.id.volRating);
            volPhone = (TextView) itemView.findViewById(R.id.volPhone);

            Pic = (ImageView) itemView.findViewById(R.id.picture);
        }
    }

    public OPInProgressAdapter(Context context, ArrayList<String> reqNameArray, ArrayList<String> stateArray, ArrayList<String> volIDArray) {
        mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.reqNameArray = reqNameArray;
        this.stateArray = stateArray;
        this.volIDArray = volIDArray;
    }

    @NonNull
    @Override
    public InProgressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.row_inprogress_op, parent, false);
        return new InProgressViewHolder(mItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InProgressViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {
            viewHolder.myName.setText("Requirement Name: "+reqNameArray.get(position));
            viewHolder.myState.setText("State : "+stateArray.get(position));
            db = FirebaseFirestore.getInstance();
            firebaseAuth = FirebaseAuth.getInstance();
            String volontID = volIDArray.get(position);

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

                                viewHolder.volRating.setText("Volunteer's Rating : " + jsonObject1.getString("rating"));
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


    }

    @Override
    public int getItemCount() {
        return reqNameArray.size();
    }


}

