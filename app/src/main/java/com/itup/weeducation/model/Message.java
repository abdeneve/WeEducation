package com.itup.weeducation.model;

import com.google.firebase.database.Exclude;
import com.itup.weeducation.model.enums.ChatType;
import com.itup.weeducation.model.enums.Direct;
import com.itup.weeducation.model.enums.MessageType;
import com.itup.weeducation.model.enums.Status;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alex-Dell on 10/13/2016.
 */

public class Message {
    public String id;
    public MessageType message_type;
    public Status status;
    public ChatType chat_type;
    public boolean is_delivered;
    public long msg_time;
    public boolean is_listened;
    public String author;

    public MessageBody body;

    public transient Direct direct;
    public transient int progress;
    public transient boolean is_read;
    public transient boolean offline;


    public Message() {
    }

    @Exclude
    public Map<String, Object> toMapText() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("message_type", message_type.name());
        result.put("status", status.name());
        result.put("chat_type", chat_type.name());
        result.put("is_delivered", is_delivered);
        result.put("msg_time", msg_time);
        result.put("is_listened", is_listened);
        result.put("author", author);
        result.put("message", ((TextMessageBody)body).message);

        return result;
    }

    @Exclude
    public Map<String, Object> toMapAssistance() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("message_type", message_type.name());
        result.put("status", status.name());
        result.put("chat_type", chat_type.name());
        result.put("is_delivered", is_delivered);
        result.put("msg_time", msg_time);
        result.put("is_listened", is_listened);
        result.put("author", author);

        result.put("student_id", ((AssistanceMessageBody)body).student_id);
        result.put("first_name", ((AssistanceMessageBody)body).first_name);
        result.put("last_name", ((AssistanceMessageBody)body).last_name);
        result.put("date_register", ((AssistanceMessageBody)body).date_register);
        result.put("is_present", ((AssistanceMessageBody)body).is_present);
        result.put("type_item_assistance", ((AssistanceMessageBody)body).type_item_assistance.name());

        return result;
    }


}
