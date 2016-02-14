package com.stocksrus.stocksrus;

import android.app.Activity;
import android.app.ProgressDialog;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Vector;

import cz.msebera.android.httpclient.Header;

/**
 * Created by divy9677 on 2/13/16.
 */
public class Company {

    private String cik;

    public Company(String cik) {
        this.cik = cik;
    }

    public void getDocuments(final ProgressDialog toShow, Form type, final Vector<JSONObject> documents) {
        toShow.show();
        RequestParams params = new RequestParams();
        String url = CompanyClient.generateDocumentURL();
        CompanyClient.get(url, params, new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                try {
                    for (int i = 0; i < timeline.length(); i++) {
                        JSONObject currObj = timeline.getJSONObject(i);
                        if (currObj == null) continue;
                        documents.add(currObj);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                toShow.dismiss();
            }
        });
    }

    public void getFormTypes(final ProgressDialog toShow, final Vector<Form> types) {
        toShow.show();
        RequestParams params = new RequestParams();
        CompanyClient.get("/companies/" + cik + "/forms/", params, new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                try {
                    for (int i = 0; i < timeline.length(); i++) {
                        JSONObject currObj = timeline.getJSONObject(i);
                        Form form = Form.getForm(currObj.getString("name"));
                        if (form == Form.INVALID) continue;
                        types.add(form);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                toShow.dismiss();
            }
        });
    }
}
