package com.itup.weeducation.model;

import com.google.firebase.database.Exclude;
import com.itup.weeducation.model.enums.TypeItemAssistance;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alex-Dell on 10/19/2016.
 */

public class AssistanceMessageBody extends MessageBody {

    public String student_id;
    public String last_name;
    public String first_name;
    public boolean is_present;
    public long date_register;
    public TypeItemAssistance type_item_assistance;

    public AssistanceMessageBody() {
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("student_id", student_id);
        result.put("last_name", last_name);
        result.put("first_name", first_name);
        result.put("is_present", is_present);
        result.put("date_register", date_register);
        result.put("type_item_assistance", type_item_assistance.name());

        return result;
    }

}
