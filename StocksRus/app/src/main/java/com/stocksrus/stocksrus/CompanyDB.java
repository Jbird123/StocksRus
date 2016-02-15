package com.stocksrus.stocksrus;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.text.format.DateUtils;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Vector;

import cz.msebera.android.httpclient.Header;

/**
 * Created by divy9677 on 2/14/16.
 */
public class CompanyDB {

    private static final int MAX_LIMIT = CompanyClient.MAX_COMPANY_LIMIT;

    private static SharedPreferences sharedPref;
    private static SharedPreferences.Editor editor;
    private static Vector<String> companies = new Vector<String>(MAX_LIMIT);

    public static void createDB(Context context, String name, ProgressDialog blocker) {
        if (sharedPref != null) return;
        sharedPref = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        CompanyDB.refreshIfNeeded(context, blocker);
    }

    private static boolean refreshIfNeeded(Context context, ProgressDialog blocker) {
        blocker.show();
        boolean refreshNeeded = refreshNeeded(context);
        if (refreshNeeded) {
            CompanyDB.refresh(blocker);
        } else {
            companies = new Vector<String>(sharedPref.getAll().keySet());
            blocker.dismiss();
        }
        return refreshNeeded;
    }

    private static boolean refreshNeeded(Context context) {
        SharedPreferences timePref = context.getSharedPreferences("companyDB_time",
                                                                    Context.MODE_PRIVATE);
        long last = timePref.getLong("last_refresh_time", 0L);
        long now = System.currentTimeMillis();
        SharedPreferences.Editor timeEditor = timePref.edit();
        timeEditor.putLong("last_refresh_time", now);
        timeEditor.apply();
        return (last == 0L || (now - last) >= (DateUtils.WEEK_IN_MILLIS * 4));
    }

    private static void refresh(final ProgressDialog blocker) {
        int firstCompany = 0, lastCompany = 4750;
        for (; lastCompany <= MAX_LIMIT; firstCompany += 4750, lastCompany += 4750) {
            RequestParams params = new RequestParams();
            params.put("limit", lastCompany);
            params.put("offset", firstCompany);

            CompanyClient.get("/companies", params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                    try {
                        for (int i = 0; i < timeline.length(); i++) {
                            JSONObject currObj = timeline.getJSONObject(i);
                            String cik = currObj.getString("cik");
                            if (cik.contains("null")) continue;
                            CompanyDB.addCompany(currObj.getString("display_name"), cik);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (blocker.isShowing()) {
                        Log.i("yy", "all requests are done!");
                        editor.commit();
                        blocker.dismiss();
                    }
                }
            });
        }
    }

    public static void addCompany(String key, String cik) {
        if (editor == null) throw new IllegalStateException("Initialize DB!");
        editor.putString(key, cik);
        companies.add(key);
    }

    public static void apply() {
        if (editor == null) throw new IllegalStateException("Initialize DB!");
        editor.apply();
    }

    public static List<String> getAllCompanies() {
        if (editor == null) throw new IllegalStateException("Initialize DB!");
        return companies;
    }

    public static String getCIKByCompanyName(String name) {
        if (editor == null) throw new IllegalStateException("Initialize DB!");
        return sharedPref.getString(name, "fake_cik");
    }
}