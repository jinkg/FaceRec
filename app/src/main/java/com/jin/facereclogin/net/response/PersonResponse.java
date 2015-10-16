package com.jin.facereclogin.net.response;

/**
 * Created by YaLin on 2015/10/10.
 */
public class PersonResponse extends BaseResponse {
    public int face_count;
    public int crowd_count;
    public int fingerprint_count;
    public int iris_count;
    public String message;
    public int res_code;
    public String people_id;
    public String people_name;
}
