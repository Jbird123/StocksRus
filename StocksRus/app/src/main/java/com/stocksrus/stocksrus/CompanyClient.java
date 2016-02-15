package com.stocksrus.stocksrus;
import com.loopj.android.http.*;

import java.util.Vector;

public class CompanyClient {
    protected static final int MAX_COMPANY_LIMIT = 19000;
    private static final String BASE_URL = "http://sec.kimonolabs.com";
    private static final String KEY = "";

    private static Vector<RequestHandle> activeHandles = new Vector<RequestHandle>();
    private static AsyncHttpClient client = new AsyncHttpClient();

    public static RequestHandle get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        params.add("apikey", KEY);
        RequestHandle handle = client.get(getAbsoluteUrl(url), params, responseHandler);
        activeHandles.add(handle);
        return handle;
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

    public static String generateDocumentURL() {
        return  null;
    }

    public static boolean allRequestsFinished() {
        for (int i = 0; i < activeHandles.size(); i++) {
            if (activeHandles.get(i).isFinished()) {
                activeHandles.remove(i);
            } else {
                return false;
            }
        }
        return activeHandles.size() == 0;
    }
}