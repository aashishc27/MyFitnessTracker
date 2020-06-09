package networking;

import android.content.Context;
import android.content.Intent;
import android.widget.ImageView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.Map;

import helper.CommonConstants;
import helper.Util;

public class NetworkHelper {

    public static String type = "";
    public static Type clazz;
    public static JSONObject queryParams;
    public static Map<String, String> headers;
    public static Response.Listener<?> listener;
    public static Response.ErrorListener errorListener;
    public static Context context;
    public static int RequestType;
    public static Boolean isUnified;
    public static Boolean isTokenRequired, fromSplash = false;
    static ImageView imageView;
    


    //post
    public static <T> void callPostJSONRequest(String requestType, Type clazz,
                                               JSONObject queryParams, Response.Listener list,
                                               Context context, boolean enableLoader) {
        // progressCall(context);
        callGeneral(requestType,clazz,queryParams,list,context,enableLoader,0,Request.Method.POST,0);
    }


    public static <T> void callGeneral(String url, Type clazz, JSONObject queryParams, Response.Listener<T> listener, final Context context, Boolean enableLoader, int headerFlag, int requestType, int tokenFlag) {

        if (Util.isNetworkAvailable(context)) {


            NetworkHelper.clazz = clazz;
            NetworkHelper.queryParams = queryParams;
            NetworkHelper.listener = listener;
            NetworkHelper.errorListener = (Response.ErrorListener) listener;
            NetworkHelper.context = context;
            NetworkHelper.RequestType = requestType;


            GsonRequest<T> mrequest = new GsonRequest<T>( url
                    , clazz, queryParams, null, listener,
                    errorListener, context, false, isUnified, false,false );

            mrequest.setShouldCache(false);
            mrequest.setRetryPolicy(new DefaultRetryPolicy(50000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            CommonApplication.getInstance(context).addToRequestQueue(mrequest);

        }

    }

}
