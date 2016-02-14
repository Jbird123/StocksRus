package com.stocksrus.stocksrus;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import com.loopj.android.http.*;

import java.util.Vector;

public class SearchActivity extends AppCompatActivity {

    final private int MAX_LIMIT = CompanyClient.MAX_COMPANY_LIMIT;
    Vector<String> companies = new Vector<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton myFab = (FloatingActionButton) findViewById(R.id.fab);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                clearDynamicViews();
                ((AutoCompleteTextView)
                        findViewById(R.id.autoCompleteTextView)).setText("");
            }
        });

        initializeCompanySearch();
    }

    public void clearDynamicViews() {
        RelativeLayout rL = (RelativeLayout)findViewById(R.id.programLayout);
        rL.removeAllViews();
    }

    public AutoCompleteTextView createSearchViews() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, companies);
        final AutoCompleteTextView textView = (AutoCompleteTextView)
                findViewById(R.id.autoCompleteTextView);
        textView.setAdapter(adapter);

        textView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                closeKeyboard();
                String[] viewText = textView.getText().toString().split("\t");
                Company selected = new Company(viewText[viewText.length - 1]);
                final ProgressDialog dialog = putUpLoadingDialog("Getting company forms info...");
                final Vector<Form> forms = new Vector<Form>();
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        createFormButtons(forms);
                    }
                });
                selected.getFormTypes(dialog, forms);
            }
        });
        return textView;
    }

    private void closeKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow((null == getCurrentFocus()) ?
                null : getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void createFormButtons(Vector<Form> forms) {
        RelativeLayout rL = (RelativeLayout)findViewById(R.id.programLayout);
        View textView = View.inflate(this.getApplication(), R.layout.simple_dropdown_file, null);
        rL.addView(textView);
        textView.setId(1);
        int prevButton = -1;
        for (int i = 0; i < forms.size(); i++) {
            Form form = forms.get(i);
            final ToggleButtonGroup tbGroup = new ToggleButtonGroup();
            ToggleButton button = new ToggleButton(getApplicationContext());
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tbGroup.onClick(v);

                }
            });
            button.setText(form.toString());
            button.setTextOn(form.toString());
            button.setTextOff(form.toString());
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.BELOW, 1);
            if (prevButton != -1) {
                params.addRule(RelativeLayout.RIGHT_OF, prevButton);
            }
            button.setLayoutParams(params);
            button.setSelected(false);
            button.setId(i + 2);
            prevButton = button.getId();
            tbGroup.add(button);
            rL.addView(button);
        }
    }

    private ProgressDialog putUpLoadingDialog(String message) {
        final ProgressDialog dialog = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
        dialog.setMessage(message);
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);
        dialog.show();
        return dialog;
    }

    private void initializeCompanySearch() {
        final ProgressDialog dialog = putUpLoadingDialog("Loading companies for you...\n\nThis may take a moment," +
                " especially the first time.");
        companies = new Vector<String>(MAX_LIMIT);
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
                            String label = currObj.getString("display_name") + "\t" + currObj.getString("cik");
                            if (label.contains("null")) continue;
                            companies.add(label);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    dialog.dismiss();
                }
            });
            createSearchViews();
        }
    }
}