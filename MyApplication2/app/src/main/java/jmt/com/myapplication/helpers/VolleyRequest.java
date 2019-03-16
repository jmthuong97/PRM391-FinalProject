package jmt.com.myapplication.helpers;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class VolleyRequest {

    private final static String baseURL = "https://teamwork-api.herokuapp.com";
    private Context context;

    public VolleyRequest(Context context) {
        this.context = context;
    }

    public void GET(final String path, final Map<String, String> params, final IVolleyCallback callback) {
        Helper.getAccessToken(new IAccessTokenCallback() {
            @Override
            public void onSuccessGetAccessToken(final String accessToken) {
                JsonObjectRequest objectRequest = new JsonObjectRequest(
                        Request.Method.GET,
                        baseURL.concat(path),
                        null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    Log.d("GET Request", response.toString());
                                    JSONObject body = response.getJSONObject("body");
                                    callback.onSuccessResponse(body);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("GET Request Error", error.toString());
                            }
                        }
                ) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("Content-Type", "application/json; charset=UTF-8");
                        params.put("Authorization", "Bearer ".concat(accessToken));
                        return params;
                    }

                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        return params;
                    }
                };

                RequestQueue requestQueue = Volley.newRequestQueue(context);
                requestQueue.add(objectRequest);
            }
        });
    }

    public void POST(final String path, final JSONObject jsonBody, final IVolleyCallback callback) {
        Helper.getAccessToken(new IAccessTokenCallback() {
            @Override
            public void onSuccessGetAccessToken(final String accessToken) {
                JsonObjectRequest objectRequest = new JsonObjectRequest(
                        Request.Method.POST,
                        baseURL.concat(path),
                        null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                callback.onSuccessResponse(response);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("POST Request Error", error.toString());
                            }
                        }
                ) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("Content-Type", "application/json; charset=UTF-8");
                        params.put("Authorization", "Bearer ".concat(accessToken));
                        return params;
                    }

                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8";
                    }

                    @Override
                    public byte[] getBody() {
                        return jsonBody.toString().getBytes(StandardCharsets.UTF_8);
                    }
                };

                RequestQueue requestQueue = Volley.newRequestQueue(context);
                requestQueue.add(objectRequest);
            }
        });
    }
}