package com.example.lifecircle;

import static com.example.lifecircle.R.drawable.edittext_field;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.BreakIterator;
import java.util.ArrayList;

public class RequirementsAdapter extends RecyclerView.Adapter<RequirementsAdapter.RequirementsViewHolder> {
    private LayoutInflater mInflater;
    private Context mContext;
    ArrayList<String> reqNameArray;
    ArrayList<String> descriptionArray;
    ArrayList<String> urgentArray;
    ArrayList<String> dateTimeArray;
    ArrayList<String> timesArray;

    public class RequirementsViewHolder extends RecyclerView.ViewHolder {
        public TextView myName;
        public TextView myDescr;
        public TextView myTimes;
        public TextView myDateTime;
        public TextView myState;


        public ImageView Pic;
        public RequirementsViewHolder(View itemView) {
            super(itemView);
            myName = (TextView) itemView.findViewById(R.id.Name);
            myDescr = (TextView) itemView.findViewById(R.id.Descr);
            myTimes = (TextView) itemView.findViewById(R.id.Times);
            myDateTime = (TextView) itemView.findViewById(R.id.DateTime);
            myState = (TextView) itemView.findViewById(R.id.State);

            Pic = (ImageView) itemView.findViewById(R.id.picture);
        }
    }

    public RequirementsAdapter(Context context, ArrayList<String> reqNameArray, ArrayList<String> descriptionArray, ArrayList<String> dateTimeArray, ArrayList<String> urgentArray, ArrayList<String> timesArray) {
        mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.reqNameArray = reqNameArray;
        this.descriptionArray = descriptionArray;
        this.dateTimeArray = dateTimeArray;
        this.urgentArray = urgentArray;
        this.timesArray = timesArray;
    }

    @NonNull
    @Override
    public RequirementsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.row_list_requirements, parent, false);
        return new RequirementsViewHolder(mItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RequirementsViewHolder viewHolder, int position) {
        viewHolder.myDescr.setText("Description: "+descriptionArray.get(position));
        viewHolder.myName.setText("Requirement Name: "+reqNameArray.get(position));
        viewHolder.myTimes.setText("Times: "+timesArray.get(position));
        viewHolder.myDateTime.setText("DateTime: "+dateTimeArray.get(position));
        viewHolder.myState.setText("State: Active  (click for volunteer's infos)");
        viewHolder.Pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DetailsVolPerReqActivity.class);
                mContext.startActivity(intent);
            }
        });
        viewHolder.myName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DetailsVolPerReqActivity.class);
                mContext.startActivity(intent);
            }
        });
        viewHolder.myDescr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DetailsVolPerReqActivity.class);
                mContext.startActivity(intent);
            }
        });
        viewHolder.myDateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DetailsVolPerReqActivity.class);
                mContext.startActivity(intent);
            }
        });
        viewHolder.myTimes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DetailsVolPerReqActivity.class);
                mContext.startActivity(intent);
            }
        });
        viewHolder.myState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DetailsVolPerReqActivity.class);
                mContext.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return reqNameArray.size();
    }


}
