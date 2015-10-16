package com.jin.facereclogin.net.response;

import com.jin.facereclogin.domain.Face;

import java.util.List;

/**
 * Created by YaLin on 2015/10/10.
 */
public class DetectResponse extends BaseResponse {
    public List<Face> face;
    public int img_height;
    public int img_width;
    public String img_id;
    public String message;
    public int res_code;
}
