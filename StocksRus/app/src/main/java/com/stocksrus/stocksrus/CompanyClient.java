package com.stocksrus.stocksrus;
import com.loopj.android.http.*;

public class CompanyClient {
    protected static final int MAX_COMPANY_LIMIT = 19000;

    private static final String BASE_URL = "http://sec.kimonolabs.com";
    private static final String KEY = "";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        params.add("apikey", KEY);
        client.get(getAbsoluteUrl(url), params, responseHandler);
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
}