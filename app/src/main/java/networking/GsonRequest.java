package networking;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.example.myfitnesstracker.R;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import helper.CommonConstants;
import helper.Util;


public class GsonRequest<T> extends Request<T> {
    private static String TOKEN_VAL;
    private static Context volleyContext;
    private static boolean isSessionExpired;
    private static ImageView imageView;
    private final Gson gson = new Gson();
    private Type clazz;
    private Map<String, String> headers;
    private int headerFlag = -1;
    private JSONObject params;

    //Activity mactivity;
    private Response.Listener<T> listener;
    private ProgressDialog progressDialog;
    private boolean isUnified = false,showWarning=false;
    private boolean isTokenRequired = false, enableLoader;
    private String url;

    public GsonRequest(String url, Type clazz, JSONObject queryParams, Map<String, String> headers, Response.Listener<T> listener, Response.ErrorListener errorListener, final Context context, Boolean enableLoader, Boolean isUnified, Boolean isTokenRequired, Boolean showWarning) {

        super(NetworkHelper.RequestType, url, errorListener);


        this.url = url;
        this.clazz = clazz;
        this.headers = headers;
        this.params = convertToJson(queryParams);
        this.listener = listener;
        //this.mactivity = (Activity) context;
        volleyContext = context;

        if (enableLoader) { progressCall(context); }
        // session is not expired check for expiration


    }

    @Override
    public String getBodyContentType() {

        return "";
    }

    public void progressCall(Context context) {
        if (progressDialog != null) {
            progressDialog.dismiss();
            dismissGIF();
            progressDialog = null;
        }

        try {

            if (progressDialog.getWindow() != null)
                progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setOnCancelListener(dialog -> {

                try {
                    if (progressDialog != null) {
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                        dismissGIF();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void progressCallWithNonCanclable() {

        progressDialog = ProgressDialog.show(volleyContext, "Please Wait", "Loading...");
        progressDialog.getWindow().setBackgroundDrawableResource(R.color.fade_white);
        //progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(volleyContext.getResources().getColor(R.color.fade_white)));
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {

        HashMap<String, String> headerMap = new HashMap<String, String>();
        headerMap.put("accept", "application/json");
        headerMap.put("Content-Type", "application/json; charset=utf-8");
        return headerMap;

    }

    @Override
    protected void deliverResponse(T response) {
        if (progressDialog != null) {
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            dismissGIF();
        }
        try {
            listener.onResponse(response);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void deliverError(VolleyError error) {
        VolleyError error1;
        // TODO Auto-generated method stub
        if (progressDialog != null) {
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            dismissGIF();

        }
        if (Util.isNetworkConnected(volleyContext)) {

            if (error.networkResponse != null && error.networkResponse.data != null) {
                //error1 = new VolleyError(new String(error.networkResponse.data));
            }

            if (error.networkResponse != null) {
                if (error.networkResponse.statusCode == 401) {
                    //SessionManager.getInstance(context).forceLogout(context);
                    super.deliverError(error);
                } else if (error.networkResponse.statusCode == 404) {
                    //SessionManager.getInstance(context).forceLogout(context);
                    super.deliverError(error);
                } else {
                    super.deliverError(error);
                }
            } else {
                super.deliverError(error);
            }
        } else {
            //super.deliverError(error);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        if (progressDialog != null) {
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            dismissGIF();
        }

        try {

            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            Map<String, String> map = response.headers;
            boolean isAcess = false;


            return (Response<T>) Response.success(gson.fromJson(json, clazz), HttpHeaderParser.parseCacheHeaders(response));


        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }

    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        try {
            if (params.length() > 0) {
                return params.toString().getBytes(getParamsEncoding());
            }


        } catch (UnsupportedEncodingException e) {
        }
        return null;
    }

    private JSONObject convertToJson(JSONObject queryParams) {
        JSONObject convertedObject = new JSONObject();
        try {
            convertedObject = new JSONObject(new Gson().toJson(queryParams));
            return (JSONObject) convertedObject.get("nameValuePairs");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return convertedObject;
    }

    void dismissGIF() {
        if (enableLoader) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    try {
                        synchronized (this) {
                            wait(0);
                        }
                    } catch (InterruptedException e) {
//                        e.printStackTrace();
                        dismissGIF();
                    }
                }
            };
            thread.start();
        }
    }

}