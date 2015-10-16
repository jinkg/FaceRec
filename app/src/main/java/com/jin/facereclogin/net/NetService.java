package com.jin.facereclogin.net;

import android.support.v4.util.ArrayMap;


import com.jin.facereclogin.constant.Constants;

import java.util.List;
import java.util.Map;

/**
 * Created by YaLin on 2015/9/15.
 */
public class NetService {
    static {
        init();
    }

    public static final String BASE_URL = "http://api.eyekey.com";
    public static final String GET_GROUP_LIST_SUB = "/info/get_group_list";
    public static final String DETECT_SUB = "/face/Check/checking";
    public static final String CREATE_PERSON_SUB = "/People/people_create";
    public static final String GET_PERSON_SUB = "/People/people_get";
    public static final String ADD_FACE_SUB = "/person/add_face";
    public static final String VERIFY_SUB = "/face/Match/match_verify";
    public static final String TRAIN_SUB = "/train/verify";

    public static String GET_GROUP_LIST_URL;
    public static String DETECT_URL;
    public static String CREATE_PERSON_URL;
    public static String GET_PERSON_URL;
    public static String ADD_FACE_URL;
    public static String VERIFY_URL;
    public static String TRAIN_URL;

    private static void init() {
        GET_GROUP_LIST_URL = BASE_URL + GET_GROUP_LIST_SUB;
        DETECT_URL = BASE_URL + DETECT_SUB;
        CREATE_PERSON_URL = BASE_URL + CREATE_PERSON_SUB;
        GET_PERSON_URL = BASE_URL + GET_PERSON_SUB;
        ADD_FACE_URL = BASE_URL + ADD_FACE_SUB;
        VERIFY_URL = BASE_URL + VERIFY_SUB;
        TRAIN_URL = BASE_URL + TRAIN_SUB;
    }

    public static Map<String, String> getGroupList() {
        ArrayMap params = new ArrayMap();
        params.put("app_id", Constants.API_ID);
        params.put("app_key", Constants.API_KEY);
        return params;
    }

    public static Map<String, String> detect(String base64Img) {
        ArrayMap params = new ArrayMap();
        params.put("app_id", Constants.API_ID);
        params.put("app_key", Constants.API_KEY);
        params.put("img", base64Img);
        return params;
    }

    public static String createPerson(String personName, List<String> faceIds) {
        StringBuffer ids = new StringBuffer();
        for (int i = 0; i < faceIds.size(); i++) {
            ids.append(faceIds.get(i));
            if (i != faceIds.size() - 1)
                ids.append(",");
        }
        StringBuilder sb = new StringBuilder(CREATE_PERSON_URL);
        sb.append("?app_id=")
                .append(Constants.API_ID)
                .append("&app_key=")
                .append(Constants.API_KEY)
                .append("&people_name=")
                .append(personName)
                .append("&face_id=")
                .append(ids.toString());

        return sb.toString();
    }

    public static String getPerson(String personName) {
        StringBuilder sb = new StringBuilder(GET_PERSON_URL);
        sb.append("?app_id=")
                .append(Constants.API_ID)
                .append("&app_key=")
                .append(Constants.API_KEY)
                .append("&people_name=")
                .append(personName);
        return sb.toString();
    }

    public static Map<String, String> addFace(String personName, String faceId) {
        ArrayMap params = new ArrayMap();
        params.put("app_id", Constants.API_ID);
        params.put("app_key", Constants.API_KEY);
        params.put("people_name", personName);
        params.put("face_id", faceId);
        return params;
    }

    public static String verify(String personName, String faceId) {
        StringBuilder sb = new StringBuilder(VERIFY_URL);
        sb.append("?app_id=")
                .append(Constants.API_ID)
                .append("&app_key=")
                .append(Constants.API_KEY)
                .append("&people_name=")
                .append(personName)
                .append("&face_id=")
                .append(faceId);

        return sb.toString();
    }

    public static Map<String, String> train(String personName) {
        ArrayMap params = new ArrayMap();
        params.put("app_id", Constants.API_ID);
        params.put("app_key", Constants.API_KEY);
        params.put("people_name", personName);
        return params;
    }
}
