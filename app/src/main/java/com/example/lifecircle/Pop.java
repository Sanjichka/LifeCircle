package com.example.lifecircle;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;

public class Pop extends Activity {

    String volontID;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popupwindow);

        Intent intent = getIntent();
        volontID = intent.getStringExtra("volontID");


        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.8), (int)(height*.6));



    }

    @Override
    public void onBackPressed() {


        RadioGroup rgroup = findViewById(R.id.raterg);
        int selectedId = rgroup.getCheckedRadioButtonId();
        RadioButton selectedRadioButton = (RadioButton)findViewById(selectedId);
        String radioButtonText = selectedRadioButton.getText().toString();


        Intent inte = new Intent(this, OP_DoneRequirementsActivity.class);
        inte.putExtra("rating", radioButtonText);
        inte.putExtra("volontID", volontID);
        startActivity(inte);

    }
}
