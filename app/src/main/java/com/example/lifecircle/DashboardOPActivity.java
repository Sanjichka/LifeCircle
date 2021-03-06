package com.example.lifecircle;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class DashboardOPActivity extends AppCompatActivity {

    private Button createreq;
    private Button listactreq;
    private Button listinprogress;
    private Button listdone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_op);

        Intent intentStart = getIntent();
        String type = intentStart.getStringExtra("type");

        createreq = findViewById(R.id.createreq);
        listactreq = findViewById(R.id.listactreq);
        listinprogress = findViewById(R.id.listinprogress);
        listdone = findViewById(R.id.listdone);


        createreq.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(DashboardOPActivity.this, OP_CreateRequirementsActivity.class);
                startActivity(intent1);
                //finish();
            }
        });
        listactreq.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(DashboardOPActivity.this, OP_ListActiveRequirementsActivity.class);
                startActivity(intent1);
                //finish();
            }
        });
        listinprogress.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(DashboardOPActivity.this, InProgressOPActivity.class);
                startActivity(intent1);
                //finish();
            }
        });
        listdone.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(DashboardOPActivity.this, OP_DoneRequirementsActivity.class);
                startActivity(intent1);
            }
        });


        Toolbar toolbar_logout = findViewById(R.id.toolbar_logout);
        setSupportActionBar(toolbar_logout);
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