package com.itup.weeducation.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alex-Dell on 10/13/2016.
 */

@IgnoreExtraProperties
public class ClassRoom {

    public String id;
    public String title;
    public String sub_title;
    public boolean play_asistance;
    public int ini_asistance;
    public int end_asistance;

    public ClassRoom(){

    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("title", title);
        result.put("sub_title", sub_title);
        result.put("play_asistance", play_asistance);
        result.put("ini_asistance", ini_asistance);
        result.put("end_asistance", end_asistance);
        return result;
    }

}
