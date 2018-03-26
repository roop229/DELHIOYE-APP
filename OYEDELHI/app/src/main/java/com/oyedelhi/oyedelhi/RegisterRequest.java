package com.oyedelhi.oyedelhi;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class RegisterRequest extends StringRequest {
    private static final String REGISTER_REQUEST_URL ="";
    private Map<String , String> params;

    public RegisterRequest(String name, String username, String age, String password, Response.Listener<String> listerner){
        super(Method.POST, REGISTER_REQUEST_URL, listerner, null);
        params = new HashMap<>();
        params.put("name",name);
        params.put("username",username);
        params.put("age",age + "");
        params.put("password",password);
    }
    @Override
    public Map<String, String> getParams(){
        return params;
    }

}