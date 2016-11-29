package com.itup.weeducation.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itup.weeducation.Constants;
import com.itup.weeducation.GlobalParameters;
import com.itup.weeducation.R;
import com.itup.weeducation.adpter.MessageAdapter;
import com.itup.weeducation.model.Assistance;
import com.itup.weeducation.model.AssistanceMessageBody;
import com.itup.weeducation.model.Message;
import com.itup.weeducation.model.Student;
import com.itup.weeducation.model.TextMessageBody;
import com.itup.weeducation.model.enums.ChatType;
import com.itup.weeducation.model.enums.Direct;
import com.itup.weeducation.model.enums.MessageType;
import com.itup.weeducation.model.enums.Status;
import com.itup.weeducation.model.enums.TypeItemAssistance;
import com.itup.weeducation.model.enums.UserType;
import com.rockerhieu.emojicon.EmojiconEditText;
import com.rockerhieu.emojicon.EmojiconGridFragment;
import com.rockerhieu.emojicon.EmojiconsFragment;
import com.rockerhieu.emojicon.emoji.Emojicon;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ClassRoomActivity extends AppCompatActivity implements
        EmojiconGridFragment.OnEmojiconClickedListener,
        EmojiconsFragment.OnEmojiconBackspaceClickedListener {

    private TextView tvAppTitle, tvAppSubTitle;
    private FrameLayout emojicons;
    private EmojiconsFragment emojiconsFragment;
    private EmojiconEditText mEditEmojicon;
    private Button btnShowEmojicon;
    private Button btnSend;
    private ToggleButton toggleTakeAsistance;
    private RecyclerView rvMessage;
    private ArrayList<Message> MessageList;

    private FirebaseDatabase dataBase = FirebaseDatabase.getInstance();
    private DatabaseReference messageRef = dataBase.getReference("message/" + GlobalParameters.ClassRoomCurrent.id);
    private DatabaseReference classRoomUsersRef = dataBase.getReference("class_room/" + GlobalParameters.ClassRoomCurrent.id + "/students");
    private DatabaseReference assistanceRef = dataBase.getReference("assistance/" + GlobalParameters.ClassRoomCurrent.id);
    private DatabaseReference classRoomCurrentRef = dataBase.getReference("class_room/" + GlobalParameters.ClassRoomCurrent.id);
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_room);
        initViews();

        user = FirebaseAuth.getInstance().getCurrentUser();

        mEditEmojicon = (EmojiconEditText) findViewById(R.id.editEmojicon);
        btnShowEmojicon = (Button)findViewById(R.id.btnShowEmojicon);
        btnSend = (Button)findViewById(R.id.btnSend);
        toggleTakeAsistance = (ToggleButton)findViewById(R.id.toggleTakeAsistance);
        toggleTakeAsistance.setChecked(GlobalParameters.ClassRoomCurrent.play_asistance);
        emojicons = (FrameLayout)findViewById(R.id.emojicons);

        mEditEmojicon.addTextChangedListener(new TextChange());

        btnShowEmojicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(emojiconsFragment.isHidden()){
                    showEmojiconFragment();
                    emojicons.setVisibility(View.VISIBLE);
                }
                else{
                    hideEmojiconFragment();
                    emojicons.setVisibility(View.GONE);
                }
            }
        });


        //inicialización de la lista de datos de ejemplo
        MessageList = new ArrayList<Message>();

        //Inicialización RecyclerView
        rvMessage = (RecyclerView) findViewById(R.id.rvMessage);
        rvMessage.setHasFixedSize(true);
        final MessageAdapter messageAdapter = new MessageAdapter(MessageList);

        // Read a Message of database
        ValueEventListener valueEventListener  = new ValueEventListener () {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                MessageList.clear();

                if (user == null) showLongToast(getString(R.string.no_login));

                for(DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    Message message = messageSnapshot.getValue(Message.class);
                    String userId = user.getUid();
                    message.direct = message.author.equals(userId) ? Direct.SEND : Direct.RECEIVE;
                    String studentId = "";

                    switch (message.message_type){
                        case TXT:
                            TextMessageBody txtBody = new TextMessageBody();
                            txtBody.message = (String) messageSnapshot.child("message").getValue();
                            message.body = txtBody;
                            break;
                        case ASSISTANCE:
                            AssistanceMessageBody assistanceBody = new AssistanceMessageBody();
                            assistanceBody.student_id = (String) messageSnapshot.child("student_id").getValue();
                            studentId = assistanceBody.student_id;
                            assistanceBody.first_name = (String) messageSnapshot.child("first_name").getValue();
                            assistanceBody.last_name = (String) messageSnapshot.child("last_name").getValue();
                            assistanceBody.date_register = (long) messageSnapshot.child("date_register").getValue();
                            assistanceBody.is_present = (boolean) messageSnapshot.child("is_present").getValue();
                            assistanceBody.type_item_assistance = TypeItemAssistance.valueOf((String)messageSnapshot.child("type_item_assistance").getValue());
                            message.body = assistanceBody;
                            break;
                    }

                    if(message.message_type == MessageType.ASSISTANCE){
                        if(GlobalParameters.UserCurrent.type.equals(UserType.TEACHER)){
                            MessageList.add(message);
                        }
                        else {
                            if(studentId.equals(GlobalParameters.UserCurrent.id)){
                                MessageList.add(message);
                            }
                        }
                    }else{
                        MessageList.add(message);
                    }

                }
                rvMessage.setAdapter(messageAdapter);
                if(MessageList.size() > 0){
                    rvMessage.scrollToPosition(MessageList.size() - 1);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(Constants.ERROR, "postComments:onCancelled", databaseError.toException());
                //Toast.makeText(this, getString(R.string.fail_load_classroom), Toast.LENGTH_SHORT).show();
            }
        };
        messageRef.addValueEventListener(valueEventListener);

        messageAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        rvMessage.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        rvMessage.setItemAnimator(new DefaultItemAnimator());
        rvMessage.setAdapter(messageAdapter);

        setEmojiconFragment(false);
        hideEmojiconFragment();

        btnSend.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (!isStringEmpty(mEditEmojicon.getText().toString()))
                {
                    Calendar calendar = Calendar.getInstance();
                    Message messageToAdd = new Message();
                    messageToAdd.message_type = MessageType.TXT;

                    TextMessageBody txtBody = new TextMessageBody();
                    txtBody.message = mEditEmojicon.getText().toString();
                    messageToAdd.body = txtBody;

                    messageToAdd.msg_time = calendar.getTimeInMillis();
                    messageToAdd.author = user.getUid();
                    messageToAdd.status = Status.CREATE;
                    messageToAdd.chat_type = ChatType.GroupChat;
                    messageToAdd.is_delivered = false;
                    messageToAdd.is_listened = false;

                    // Write a message to the database

                    String key = messageRef.child("message").push().getKey();
                    messageToAdd.id = key;
                    Map<String, Object> messageValues = messageToAdd.toMapText();

                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put(key, messageValues);
                    messageRef.updateChildren(childUpdates);
                    MessageList.add(messageToAdd);
                    int messageIndex = MessageList.indexOf(messageToAdd);
                    messageAdapter.notifyItemChanged(messageIndex);
                    mEditEmojicon.setText("");

                    hideEmojiconFragment();
                    emojicons.setVisibility(View.GONE);
                }
            }
        });

        toggleTakeAsistance.setOnClickListener(new View.OnClickListener(){
                @Override
            public void onClick(View v){

                if(toggleTakeAsistance.isChecked()){

                    ValueEventListener valueEventListener  = new ValueEventListener () {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            ArrayList<Student> StudentList = new ArrayList<Student>();

                            for(DataSnapshot studentSnapshot : dataSnapshot.getChildren()) {
                                Student student = studentSnapshot.getValue(Student.class);
                                student.id = studentSnapshot.getKey();
                                StudentList.add(student);
                            }

                            for (Student student : StudentList){
                                int indexStudent = StudentList.indexOf(student);
                                Message messageToAdd = CreateMessage(student, indexStudent);

                                // Write a message to the database
                                String key = messageRef.child("message").push().getKey();
                                messageToAdd.id = key;
                                Map<String, Object> messageValues = messageToAdd.toMapAssistance();
                                Map<String, Object> childUpdates = new HashMap<>();
                                childUpdates.put(key, messageValues);
                                messageRef.updateChildren(childUpdates);

                                MessageList.add(messageToAdd);

                                Integer messageIndex = MessageList.indexOf(messageToAdd);
                                messageAdapter.notifyItemChanged(messageIndex);

                                if(indexStudent == 0)
                                    GlobalParameters.ClassRoomCurrent.ini_asistance = messageIndex;

                                if(indexStudent == StudentList.size() - 1)
                                    GlobalParameters.ClassRoomCurrent.end_asistance = messageIndex;

                            }

                            GlobalParameters.ClassRoomCurrent.play_asistance = true;
                            classRoomCurrentRef.child("ini_asistance").setValue(GlobalParameters.ClassRoomCurrent.ini_asistance);
                            classRoomCurrentRef.child("end_asistance").setValue(GlobalParameters.ClassRoomCurrent.end_asistance);
                            classRoomCurrentRef.child("play_asistance").setValue(GlobalParameters.ClassRoomCurrent.play_asistance);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.w(Constants.ERROR, "Assistance:onCancelled", databaseError.toException());
                        }
                    };
                    classRoomUsersRef.addValueEventListener(valueEventListener);

                    hideEmojiconFragment();
                    emojicons.setVisibility(View.GONE);

                }else{

                    String key = assistanceRef.child("assistance").push().getKey();

                    for (int index = GlobalParameters.ClassRoomCurrent.ini_asistance; index <= GlobalParameters.ClassRoomCurrent.end_asistance; index++){
                        Message messageAssistance = MessageList.get(index);
                        AssistanceMessageBody amb = (AssistanceMessageBody)messageAssistance.body;
                        Assistance assistanceToSave = CreateAssistance(amb);

                        // Write a assistance to the database
                        DatabaseReference iDatabase = dataBase.getReference("assistance/" + GlobalParameters.ClassRoomCurrent.id + "/" + key);

                        Map<String, Object> messageValues = assistanceToSave.toMap();
                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put(assistanceToSave.student_id, messageValues);
                        iDatabase.updateChildren(childUpdates);
                    }

                    int indexLastAssistance = GlobalParameters.ClassRoomCurrent.end_asistance;
                    if(indexLastAssistance > -1) {
                        Message messageToUpdate = MessageList.get(indexLastAssistance);
                        AssistanceMessageBody ambToUpdate = (AssistanceMessageBody)messageToUpdate.body;
                        ambToUpdate.type_item_assistance = TypeItemAssistance.END;
                        messageToUpdate.body = ambToUpdate;
                        MessageList.set(indexLastAssistance, messageToUpdate);
                        Map<String, Object> messageValues = messageToUpdate.toMapAssistance();
                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put(messageToUpdate.id, messageValues);
                        messageRef.updateChildren(childUpdates);
                        messageAdapter.notifyItemChanged(indexLastAssistance);
                    }

                    GlobalParameters.ClassRoomCurrent.play_asistance = false;
                    classRoomCurrentRef.child("ini_asistance").setValue(0);
                    classRoomCurrentRef.child("end_asistance").setValue(0);
                    classRoomCurrentRef.child("play_asistance").setValue(GlobalParameters.ClassRoomCurrent.play_asistance);
                }

            }
        });

    }

    private Message CreateMessage(Student student, int index){
        Calendar calendar = Calendar.getInstance();

        Message messageToAdd = new Message();
        messageToAdd.message_type = MessageType.ASSISTANCE;
        messageToAdd.author = user.getUid();
        messageToAdd.status = Status.CREATE;
        messageToAdd.chat_type = ChatType.GroupChat;
        messageToAdd.is_delivered = false;
        messageToAdd.msg_time = calendar.getTimeInMillis();
        messageToAdd.is_listened = false;
        AssistanceMessageBody assistanceBody = new AssistanceMessageBody();
        assistanceBody.student_id = student.id;
        assistanceBody.last_name = student.last_name;
        assistanceBody.first_name = student.first_name;
        assistanceBody.is_present = false;
        assistanceBody.type_item_assistance = TypeItemAssistance.MEDIUM;

        if(index == 0)
            assistanceBody.type_item_assistance = TypeItemAssistance.START;

        messageToAdd.body = assistanceBody;

        return messageToAdd;
    }

    private Assistance CreateAssistance(AssistanceMessageBody ambToSave){
        Assistance assistance = new Assistance();
        Calendar calendar = Calendar.getInstance();
        assistance.student_id = ambToSave.student_id;
        assistance.last_name = ambToSave.last_name;
        assistance.first_name = ambToSave.first_name;
        assistance.is_present = ambToSave.is_present;
        assistance.date_register = calendar.getTimeInMillis();
        return assistance;
    }

    private void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.top_bar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        tvAppTitle = (TextView)findViewById(R.id.tvAppTitle);
        tvAppTitle.setText(GlobalParameters.ClassRoomCurrent.title);
        tvAppSubTitle = (TextView)findViewById(R.id.tvAppSubTitle);
        tvAppSubTitle.setVisibility(View.GONE);
    }

    private void setEmojiconFragment(boolean useSystemDefault) {
        emojiconsFragment = EmojiconsFragment.newInstance(useSystemDefault);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.emojicons, emojiconsFragment)
                .commit();
    }

    private void showEmojiconFragment(){
        getSupportFragmentManager()
                .beginTransaction()
                .show(emojiconsFragment)
                .commit();
    }

    private void hideEmojiconFragment(){
        getSupportFragmentManager()
                .beginTransaction()
                .hide(emojiconsFragment)
                .commit();
    }

    private boolean isStringEmpty(String text){
        return text == null || text.trim().equals("");
    }

    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        EmojiconsFragment.input(mEditEmojicon, emojicon);
    }

    @Override
    public void onEmojiconBackspaceClicked(View v) {
        EmojiconsFragment.backspace(mEditEmojicon);
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

    protected void showLongToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    class TextChange implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            boolean msgText = mEditEmojicon.getText().length() > 0;

            if (msgText) {
                btnSend.setEnabled(true);
            } else {
                btnSend.setEnabled(false);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

}
