package com.example.lifecircle;

import static com.example.lifecircle.R.drawable.edittext_field;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
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

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RequirementsAdapter extends RecyclerView.Adapter<RequirementsAdapter.RequirementsViewHolder> {
    private LayoutInflater mInflater;
    private Context mContext;
    ArrayList<String> reqNameArray;
    ArrayList<String> descriptionArray;
    ArrayList<String> urgentArray;
    ArrayList<String> dateTimeArray;
    ArrayList<String> timesArray;
    ArrayList<String> stateArray;
    ArrayList<String> volIDArray;
    private FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    String userID;

    public class RequirementsViewHolder extends RecyclerView.ViewHolder {
        public TextView myName;
        public TextView myDescr;
        public TextView myTimes;
        public TextView myDateTime;
        public TextView myState;
        public Button acceptbtn;
        public Button rejectbtn;


        public ImageView Pic;
        public RequirementsViewHolder(View itemView) {
            super(itemView);
            myName = (TextView) itemView.findViewById(R.id.Name);
            myDescr = (TextView) itemView.findViewById(R.id.Descr);
            myTimes = (TextView) itemView.findViewById(R.id.Times);
            myDateTime = (TextView) itemView.findViewById(R.id.DateTime);
            myState = (TextView) itemView.findViewById(R.id.State);
            acceptbtn = (Button) itemView.findViewById(R.id.btnaccept);
            rejectbtn = (Button) itemView.findViewById(R.id.btnreject);

            Pic = (ImageView) itemView.findViewById(R.id.picture);
        }
    }

    public RequirementsAdapter(Context context, ArrayList<String> reqNameArray, ArrayList<String> descriptionArray, ArrayList<String> dateTimeArray, ArrayList<String> urgentArray, ArrayList<String> timesArray, ArrayList<String> stateArray, ArrayList<String> volIDArray) {
        mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.reqNameArray = reqNameArray;
        this.descriptionArray = descriptionArray;
        this.dateTimeArray = dateTimeArray;
        this.urgentArray = urgentArray;
        this.timesArray = timesArray;
        this.stateArray = stateArray;
        this.volIDArray = volIDArray;
    }

    @NonNull
    @Override
    public RequirementsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.row_list_requirements, parent, false);
        return new RequirementsViewHolder(mItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RequirementsViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();

        viewHolder.myDescr.setText("Description: "+descriptionArray.get(position));
        viewHolder.myName.setText("Requirement Name: "+reqNameArray.get(position));
        viewHolder.myTimes.setText("Times: "+timesArray.get(position));
        viewHolder.myDateTime.setText("DateTime: "+dateTimeArray.get(position));
        viewHolder.myState.setText("State: " + stateArray.get(position));
        if(stateArray.get(position).equals("pending")) {
            viewHolder.acceptbtn.setVisibility(View.VISIBLE);
            viewHolder.rejectbtn.setVisibility(View.VISIBLE);
        }
        viewHolder.acceptbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sostojbata da premine vo in-progress, volID za req si ostanuva, dvete kopchinja pak invisible, da ostane na istata strana
                viewHolder.acceptbtn.setVisibility(View.INVISIBLE);
                viewHolder.rejectbtn.setVisibility(View.INVISIBLE);


                Map<String, Object> map1 = new HashMap<>();
                map1.put("state", "in-progress");

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

                                            //Checks if i equals to the position needed.
                                            if(i == position)
                                            {
                                                //Do something with the document at that index.
                                                db.collection("reqs_all").document(_documentSnapshot.getId()).update(map1);
                                                viewHolder.myState.setText("State: in-progress");
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
        viewHolder.rejectbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //da se vrati samo sostojbata vo active, stavi volID od req_all kako none...

                viewHolder.acceptbtn.setVisibility(View.INVISIBLE);
                viewHolder.rejectbtn.setVisibility(View.INVISIBLE);

                Map<String, Object> map1 = new HashMap<>();
                map1.put("state", "active");
                map1.put("volID", "none");

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

                                            //Checks if i equals to the position needed.
                                            if(i == position)
                                            {
                                                //Do something with the document at that index.
                                                db.collection("reqs_all").document(_documentSnapshot.getId()).update(map1);
                                                viewHolder.myState.setText("State: active");
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
        if(stateArray.get(position).equals("pending")) {
            viewHolder.Pic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, DetailsVolPerReqActivity.class);
                    intent.putExtra("volID", volIDArray.get(position));
                    mContext.startActivity(intent);
                }
            });
            viewHolder.myName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, DetailsVolPerReqActivity.class);
                    intent.putExtra("volID", volIDArray.get(position));
                    mContext.startActivity(intent);
                }
            });
            viewHolder.myDescr.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, DetailsVolPerReqActivity.class);
                    intent.putExtra("volID", volIDArray.get(position));
                    mContext.startActivity(intent);
                }
            });
            viewHolder.myDateTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, DetailsVolPerReqActivity.class);
                    intent.putExtra("volID", volIDArray.get(position));
                    mContext.startActivity(intent);
                }
            });
            viewHolder.myTimes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, DetailsVolPerReqActivity.class);
                    intent.putExtra("volID", volIDArray.get(position));
                    mContext.startActivity(intent);
                }
            });
            viewHolder.myState.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, DetailsVolPerReqActivity.class);
                    intent.putExtra("volID", volIDArray.get(position));
                    mContext.startActivity(intent);
                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return reqNameArray.size();
    }


}
