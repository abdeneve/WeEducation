package com.itup.weeducation.model;

import com.google.firebase.database.Exclude;
import com.itup.weeducation.model.enums.UserType;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alex-Dell on 10/26/2016.
 */

public class User {
    public String id;
    public String telephone;
    public String head_url;
    public String last_name;
    public String first_name;
    public String gender;
    public String location;
    public long birthday;
    public UserType type;

    public User() {
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("telephone", telephone);
        result.put("head_url", head_url);
        result.put("last_name", last_name);
        result.put("first_name", first_name);
        result.put("gender", gender);
        result.put("location", location);
        result.put("birthday", birthday);
        result.put("type", type.name());

        return result;
    }
}
