package com.itup.weeducation.view.activity;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itup.weeducation.Constants;
import com.itup.weeducation.GlobalParameters;
import com.itup.weeducation.MainActivity;
import com.itup.weeducation.R;
import com.itup.weeducation.model.ClassRoom;
import com.itup.weeducation.model.User;
import com.itup.weeducation.model.enums.UserType;
import com.itup.weeducation.view.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import static com.itup.weeducation.R.id.rvClassRoom;

/**
 * Created by Alex-Dell on 10/11/2016.
 */

public class LoginActivity extends BaseActivity implements OnClickListener {
    private Context context;
    private EditText etEmail, etPassword;
    private Button btnLogin, btnRegister;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase dataBase = FirebaseDatabase.getInstance();
    private DatabaseReference refClassRoom = dataBase.getReference("class_room");
    private ValueEventListener valueEventListenerClassRoom;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_login);
        super.onCreate(savedInstanceState);
        context = this;
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void initControl() {
        etEmail = (EditText)findViewById(R.id.etEmail);
        etPassword = (EditText)findViewById(R.id.etPassword);
        btnLogin = (Button)findViewById(R.id.btnLogin);
    }

    @Override
    protected void setListener() {
        etEmail.addTextChangedListener(new TextChange());
        etPassword.addTextChangedListener(new TextChange());
        btnLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogin:
                getLogin();
                break;
            default:
                break;
        }
    }

    private void getLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        getLogin(email, password);
    }

    private void getLogin(final String email, final String password) {

        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                getUserCurrent();
                                getRoomClassList(email, password);
                            }else{
                                showLongToast(getString(R.string.no_login));
                            }
                        }

                        private void getUserCurrent() {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            DatabaseReference refUser = dataBase.getReference("user/"+user.getUid());
                            refUser.addListenerForSingleValueEvent(
                                    new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            // Get user value
                                            GlobalParameters.UserCurrent.id = (String) dataSnapshot.child("id").getValue();
                                            GlobalParameters.UserCurrent.telephone = (String) dataSnapshot.child("telephone").getValue();
                                            GlobalParameters.UserCurrent.head_url = (String) dataSnapshot.child("head_url").getValue();
                                            GlobalParameters.UserCurrent.last_name = (String) dataSnapshot.child("last_name").getValue();
                                            GlobalParameters.UserCurrent.first_name = (String) dataSnapshot.child("first_name").getValue();
                                            GlobalParameters.UserCurrent.gender = (String) dataSnapshot.child("gender").getValue();
                                            GlobalParameters.UserCurrent.location = (String) dataSnapshot.child("location").getValue();
                                            GlobalParameters.UserCurrent.birthday = (long) dataSnapshot.child("birthday").getValue();
                                            GlobalParameters.UserCurrent.type = UserType.valueOf((String) dataSnapshot.child("type").getValue());
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            Log.w(Constants.ERROR, "UserCurrent: onCancelled", databaseError.toException());
                                        }
                                    });
                        }

                    });
        } else {
            showLongToast(getString(R.string.no_login));
        }

    }

    private void getRoomClassList(final String email, final String password) {

        valueEventListenerClassRoom  = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GlobalParameters.ClassRoomList.clear();
                for(DataSnapshot classRoomSnapshot : dataSnapshot.getChildren()) {
                    ClassRoom classRoom = classRoomSnapshot.getValue(ClassRoom.class);
                    GlobalParameters.ClassRoomList.add(classRoom);
                }

                ActivityManager am = (ActivityManager)context.getSystemService(ACTIVITY_SERVICE);
                List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
                ComponentName componentInfo = taskInfo.get(0).topActivity;
                String nameClass = componentInfo.getClassName();
                if(nameClass.equals("com.itup.weeducation.view.activity.LoginActivity")){
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(Constants.ERROR, "postComments:onCancelled", databaseError.toException());
                Toast.makeText(LoginActivity.this, "Failed to load Asignaturas.", Toast.LENGTH_SHORT).show();
            }
        };
        refClassRoom.addValueEventListener(valueEventListenerClassRoom);

    }

    private void signOut() {
        mAuth.signOut();
    }

    class TextChange implements TextWatcher {

        @Override
        public void afterTextChanged(Editable arg0) {

        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

        }

        @Override
        public void onTextChanged(CharSequence cs, int start, int before, int count) {

            boolean Sign2 = etEmail.getText().length() > 0;
            boolean Sign3 = etPassword.getText().length() > 5;

            if (Sign2 & Sign3) {
                btnLogin.setEnabled(true);
            } else {
                btnLogin.setEnabled(false);
            }

        }
    }

}
