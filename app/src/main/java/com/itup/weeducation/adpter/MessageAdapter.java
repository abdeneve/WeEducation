package com.itup.weeducation.adpter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.itup.weeducation.GlobalParameters;
import com.itup.weeducation.R;
import com.itup.weeducation.model.Assistance;
import com.itup.weeducation.model.AssistanceMessageBody;
import com.itup.weeducation.model.Message;
import com.itup.weeducation.model.TextMessageBody;
import com.itup.weeducation.model.enums.Direct;
import com.itup.weeducation.model.enums.MessageType;
import com.itup.weeducation.model.enums.TypeItemAssistance;
import com.rockerhieu.emojicon.EmojiconTextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alex-Dell on 10/16/2016.
 */

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements View.OnClickListener {

    private View.OnClickListener Listener;
    private ArrayList<Message> MessageList;

    private static final int MESSAGE_TYPE_SEND_TXT = 0;
    private static final int MESSAGE_TYPE_RECV_TXT = 1;
    private static final int MESSAGE_TYPE_SENT_ASSISTANCE = 3;

    private static boolean showDate(Message message, Message previousMessage){

        if (previousMessage == null) return true;

        Date dateCurrentMsg=new Date(message.msg_time);
        Date datePreviousMsg=new Date(previousMessage.msg_time);

        if(dateCurrentMsg.getYear() > datePreviousMsg.getYear()){
            return true;
        }

        if(dateCurrentMsg.getYear() == datePreviousMsg.getYear() &&
                dateCurrentMsg.getMonth() > datePreviousMsg.getMonth()){
            return true;
        }

        if(dateCurrentMsg.getYear() == datePreviousMsg.getYear() &&
                dateCurrentMsg.getMonth() == datePreviousMsg.getMonth() &&
                dateCurrentMsg.getDay() > datePreviousMsg.getDay()){
            return true;
        }

        return false;
    }

    private static String getTextDate(long dateLong){
        Date date = new Date(dateLong);
        String dateText = new SimpleDateFormat("dd").format(date) + " de " +
                new SimpleDateFormat("MMMM").format(date)  + " del " +
                new SimpleDateFormat("yyyy").format(date);
        return dateText;
    }

    private static String getTextDateTime(long dateLong){
        Date date = new Date(dateLong);
        String dateText = new SimpleDateFormat("dd").format(date) + " de " +
                new SimpleDateFormat("MMMM").format(date)  + " del " +
                new SimpleDateFormat("yyyy").format(date) + " " +
                new SimpleDateFormat("HH:mm").format(date);
        return dateText;
    }

    public MessageAdapter(ArrayList<Message> messageList) {
        this.MessageList = messageList;
    }

    public static class ViewHolderText extends RecyclerView.ViewHolder {

        LinearLayout centeredLayout;
        private EmojiconTextView tvEmojicon;
        private TextView tvTime, tvDate;

        public ViewHolderText(View itemView) {
            super(itemView);
            centeredLayout = (LinearLayout)itemView.findViewById(R.id.centeredLayout);
            tvDate = (TextView)itemView.findViewById(R.id.tvDate);
            tvEmojicon = (EmojiconTextView)itemView.findViewById(R.id.tvEmojiconContent);
            tvTime = (TextView)itemView.findViewById(R.id.tvTime);
        }

        public void bindMessage(Message message, Message previousMessage){

            String dateText = getTextDate(message.msg_time);
            tvDate.setText(dateText);
            int isVisible = showDate(message, previousMessage)? View.VISIBLE : View.GONE;
            centeredLayout.setVisibility(isVisible);

            TextMessageBody txtBody = (TextMessageBody) message.body;
            tvEmojicon.setText(txtBody.message);
            tvTime.setText(new SimpleDateFormat("HH:mm").format(new Date(message.msg_time)));
        }

    }

    public static class ViewHolderAssistance extends RecyclerView.ViewHolder{

        LinearLayout layoutStartAssistance, layoutEndAssistance;
        private TextView tvLastName, tvFirstName, tvStartAssistance, tvEndAssistance;
        private CheckBox checkBoxAssistance;

        public ViewHolderAssistance(View itemView) {
            super(itemView);
            layoutStartAssistance = (LinearLayout)itemView.findViewById(R.id.layoutStartAssistance);
            layoutEndAssistance = (LinearLayout)itemView.findViewById(R.id.layoutEndAssistance);
            tvStartAssistance = (TextView)itemView.findViewById(R.id.tvStartAssistance);
            tvEndAssistance = (TextView)itemView.findViewById(R.id.tvEndAssistance);
            tvLastName = (TextView)itemView.findViewById(R.id.tvLastName);
            tvFirstName = (TextView)itemView.findViewById(R.id.tvFirstName);
            checkBoxAssistance = (CheckBox)itemView.findViewById(R.id.checkboxAssistance);
        }

        public void bindMessage(Message message){

            TypeItemAssistance typeItemAssistance = ((AssistanceMessageBody)message.body).type_item_assistance;

            String dateText = getTextDateTime(message.msg_time);
            switch (typeItemAssistance){
                case START:
                    tvStartAssistance.setText("INICIO DE ASISTENCIA: " + dateText);
                    layoutStartAssistance.setVisibility(View.VISIBLE);
                    layoutEndAssistance.setVisibility(View.GONE);
                    break;
                case MEDIUM:
                    layoutStartAssistance.setVisibility(View.GONE);
                    layoutEndAssistance.setVisibility(View.GONE);
                    break;
                case END:
                    tvEndAssistance.setText("FIN DE ASISTENCIA: " + dateText);
                    layoutStartAssistance.setVisibility(View.GONE);
                    layoutEndAssistance.setVisibility(View.VISIBLE);
                    break;
            }

            AssistanceMessageBody assistanceBody = (AssistanceMessageBody)message.body;
            tvLastName.setText(assistanceBody.last_name);
            tvFirstName.setText(assistanceBody.first_name);
            checkBoxAssistance.setChecked(assistanceBody.is_present);
            checkBoxAssistance.setEnabled(GlobalParameters.ClassRoomCurrent.play_asistance);
            itemView.setTag(message);

            checkBoxAssistance.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Calendar calendar = Calendar.getInstance();
                    Message message = (Message)itemView.getTag();
                    AssistanceMessageBody ambToUpdate = (AssistanceMessageBody)message.body;
                    ambToUpdate.date_register = calendar.getTimeInMillis();
                    ambToUpdate.is_present = isChecked;
                    message.body = ambToUpdate;

                    // Write a message to the database
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference mDatabase = database.getReference("message/"+ GlobalParameters.ClassRoomCurrent.id);

                    Map<String, Object> messageValues = message.toMapAssistance();
                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put(message.id, messageValues);
                    mDatabase.updateChildren(childUpdates);
                }
            });
        }

    }

    public static class ViewHolderImage extends RecyclerView.ViewHolder {

        public ViewHolderImage(View itemView) {
            super(itemView);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View itemView;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        switch (viewType) {
            case MESSAGE_TYPE_SEND_TXT:
                itemView = inflater.inflate(R.layout.layout_item_msg_send_text, viewGroup, false);
                itemView.setOnClickListener(this);
                return new ViewHolderText(itemView);

            case MESSAGE_TYPE_RECV_TXT:
                itemView = inflater.inflate(R.layout.layout_item_msg_receive_text, viewGroup, false);
                itemView.setOnClickListener(this);
                return new ViewHolderText(itemView);

            case MESSAGE_TYPE_SENT_ASSISTANCE:
                itemView = inflater.inflate(R.layout.layout_item_msg_assistance, viewGroup, false);
                itemView.setOnClickListener(this);
                return new ViewHolderAssistance(itemView);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Message item = MessageList.get(position);
        Message previousItem = position > 0 ? MessageList.get(position - 1) : null;

        switch (item.message_type) {
            case TXT:
                ((ViewHolderText)holder).bindMessage(item, previousItem);
                break;
            case ASSISTANCE:
                ((ViewHolderAssistance)holder).bindMessage(item);
                break;
            case IMAGE:

                break;
        }

    }

    @Override
    public int getItemCount() {
        return MessageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = MessageList.get(position);
        if (message != null) {
            switch (message.message_type) {
                case TXT:
                    return message.direct == Direct.SEND ? MESSAGE_TYPE_SEND_TXT : MESSAGE_TYPE_RECV_TXT;
                case ASSISTANCE:
                    return MESSAGE_TYPE_SENT_ASSISTANCE;
            }
        }
        return 0;
    }

    public void setOnClickListener(View.OnClickListener listener) {
        this.Listener = listener;
    }

    @Override
    public void onClick(View view) {
        if(Listener != null)
            Listener.onClick(view);
    }
}
