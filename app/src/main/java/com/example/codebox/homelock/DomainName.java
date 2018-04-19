package com.example.codebox.homelock;

import android.content.Context;
import android.util.Log;

/**
 * Created by codebox on 26/6/17.
 */

public class DomainName {

    public static final String IP = UserData.getIP();
    public static final int PORT = 9000;

    private static final String ROOT_URL = "http://" + IP + "/LockBackend/";

    public static final String SIGNUP = ROOT_URL + "signup.php";
    public static final String SIGNIN = ROOT_URL + "signin.php";
    public static final String UPDATE_TOKEN = ROOT_URL + "updateToken.php";
    public static final String LOGOUT = ROOT_URL + "logout.php";
    public static final String ANDROIDIDUPDATE = ROOT_URL + "androidIdUpdate.php";
    public static final String GIVE_ACCESS = ROOT_URL + "addNewMember.php";
    public static final String GET_ACCESS = ROOT_URL + "registerMember.php";
    public static final String GET_MEMBER_LIST = ROOT_URL + "memberDetails.php";
    public static final String REMOVE_MEMBER = ROOT_URL + "removeMember.php";

    public static final String ONLINECHECK = ROOT_URL + "onlineChecker.php";
    public static final String NOTIFICATION = ROOT_URL + "notification.php";
    public static final String GETIMAGE = ROOT_URL + "getImage.php?imageId=";


    public static final String FETCH_IP = "https://techcodebox.000webhostapp.com/lock/fetch_ip.php";
}
