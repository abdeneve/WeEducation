package com.itup.weeducation;

import com.itup.weeducation.model.ClassRoom;
import com.itup.weeducation.model.User;

import java.util.ArrayList;

/**
 * Created by Alex-Dell on 10/17/2016.
 */

public class GlobalParameters {
    public static User UserCurrent = new User();
    public static ClassRoom ClassRoomCurrent = new ClassRoom();
    public static ArrayList<ClassRoom> ClassRoomList = new ArrayList<ClassRoom> ();
}
