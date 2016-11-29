package com.itup.weeducation;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itup.weeducation.adpter.ClassRoomAdapter;
import com.itup.weeducation.model.ClassRoom;
import com.itup.weeducation.view.activity.ClassRoomActivity;
import com.itup.weeducation.view.activity.LoginActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private Context context;
    private TextView tvAppTitle, tvAppSubTitle;
    private RecyclerView rvClassRoom;
    private ClassRoomAdapter classRoomAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;
        initViews();

        //Inicialización RecyclerView
        rvClassRoom = (RecyclerView) findViewById(R.id.rvClassRoom);
        rvClassRoom.setHasFixedSize(true);
        classRoomAdapter = new ClassRoomAdapter();

        classRoomAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ClassRoomActivity.class);
                GlobalParameters.ClassRoomCurrent = (ClassRoom)v.getTag();
                startActivity(intent);
            }
        });

        rvClassRoom.setLayoutManager(new LinearLayoutManager(context));
        rvClassRoom.setAdapter(classRoomAdapter);
    }

    private void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarMain);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        tvAppTitle = (TextView)findViewById(R.id.tvAppTitle);
        tvAppSubTitle = (TextView)findViewById(R.id.tvAppSubTitle);

        tvAppTitle.setText(R.string.class_room);
        tvAppSubTitle.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_about:

                return true;
            case R.id.action_power:
                finish();
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
