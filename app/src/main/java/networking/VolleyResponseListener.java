package networking;

import com.android.volley.VolleyError;

public interface VolleyResponseListener<T> {

    void onError(VolleyError message);

    void onResponse(T response);
}
