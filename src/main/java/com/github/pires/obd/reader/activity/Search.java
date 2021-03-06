package com.github.pires.obd.reader.activity;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.pires.obd.reader.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class Search extends Activity {



    private static ArrayAdapter<String> adapter;
    private static ArrayList<String> itemList = new ArrayList<>();
     Button Search_btn=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        final ApiHelper apiHelper=new ApiHelper(this);

        final EditText editText_code = (EditText) findViewById(R.id.error_code);
        editText_code.requestFocus();
         final EditText vinInputField = ((EditText) findViewById(R.id.vin_input));
        Search_btn= (Button) findViewById(R.id.button_search);
        initViewElements();
        Search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Search_btn.setEnabled(false);
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(Search_btn.getWindowToken(), 0);
                clearListView();
                String vin="";



                String dcts[]=editText_code.getText().toString().split(",");

                if (vinInputField.getText().toString().equals("")) vin="WBAES26C05D";


                apiHelper.getErrorCodeTranslation(dcts,vin,"English");
                Search_btn.setEnabled(true);



//                obdHelper = new ObdHelper(mHandler, RequestActivity.this);
//                progressBar.setProgress(0);
//                obdHelper.connectToDevice();
            }
        });
    }

    private void initViewElements() {
//        if(getSupportActionBar()!=null)
//            getSupportActionBar().setTitle(R.string.translation_result);
        ListView listView = (ListView) findViewById(R.id.output);
        adapter = new ResultAdapter(this, itemList);
        listView.setAdapter(adapter);
        clearListView();
    }

    public static void addListItem(String value){
        itemList.add(value);
        adapter.notifyDataSetChanged();
    }

    private static void clearListView(){
        itemList.clear();
        adapter.notifyDataSetChanged();
    }

};






    class ApiHelper extends Application {
        private final String TAG = ApiHelper.class.getSimpleName();
        private static final String mainURL = "https://api.eu.apiconnect.ibmcloud.com/hella-ventures-car-diagnostic-api/api";

        /**
         * TODO: Input your authentication parameters here.
         */
        private final String CLIENT_ID = "8e9b1553-74f2-4761-8669-4370e4719b0c";

        private final String CLIENT_SECRET = "gX6qE7xF7sL4gB8mP5eQ5bK7mP4vU6nM2vQ2pK1pX2gM4oF0pX";

        private Context c;

        public ApiHelper(Context c){
            this.c = c.getApplicationContext();
        }



        /**
         * Generates an url to HELLA's Car Diagnostic API based on the given parameters.
         *
         * @param request The request to be send to the API.
         * @param param   The added parameters.
         * @return The generated url or an error if something is missing.
         */
        public String generateURL(String request, String[] param) {
            int version = 1;
            if (request != null) {
                if (!request.equals("")) {
                    String url = mainURL + "/v" + version + "/" + request;
                    if (param != null) {
                        if (param.length > 0) {
                            url += "?";
                            for (int i = 0; i < param.length; i++) {
                                url += param[i];
                                if (i != param.length - 1)
                                    url += "&";
                            }
                        }
                    }
                    return url;
                }
            }
            return "Url generation failed - Missing request.";
        }

        /**
         * Translates a number of given error codes.
         *
         * @param dtcs      The DTCs to be translated.
         * @param vin       The VIN of the specific car.
         * @param language  The translation language.
         */
        public void getErrorCodeTranslation(String[] dtcs, String vin, String language) {
            RequestQueue requestQueue = Volley.newRequestQueue(c);

            for (String dtc: dtcs) {
                String url = null;
                if (dtc != null & vin != null & language != null) {
                    if (!dtc.equals("") & !vin.equals("") & !language.equals(""))
                        url = getErrorCodeUrl(dtc, vin, language);
                }
                if (url != null)
                    requestQueue.add(GETTranslation(url, dtc, language));
            }
        }

        /**
         * Generates the url for the error code translation
         *
         * @param dtc       The DTC to be translated.
         * @param vin       The VIN of the specific car.
         * @param language  The translation language.
         * @return error code translation url or null
         */
        public String getErrorCodeUrl(String dtc, String vin, String language){
            String request = "dtc";
            String languageSF = getShortForm(language);
            if (languageSF!= null) {
                String[] params = {"code_id=" + dtc, "vin=" + vin, "language=" + languageSF, "client_id=" + CLIENT_ID , "client_secret=" + CLIENT_SECRET};
                return generateURL(request, params);
            }
            return null;
        }

        /**
         * Generates the url for supported makers.
         *
         * @return The url to get the supported makers from.
         */
        public String getSupportedMakersUrl() {
            String request = "dtc/maker";
            String[] params = {"client_id=" + CLIENT_ID , "client_secret=" + CLIENT_SECRET};
            return generateURL(request, params);
        }

        /**
         * Generates the url for supported languages.
         *
         * @return The url to get the supported languages from.
         */
        public String getSupportedLanguagesUrl() {
            String request = "dtc/langs";
            String[] params = {"client_id=" + CLIENT_ID , "client_secret=" + CLIENT_SECRET};
            return generateURL(request, params);
        }

        /**
         * Transforms a language to its short form.
         *
         * @param language  The language to be transformed.
         * @return          The language's short form.
         */
        public  String getShortForm(String language) {
            if (language.toLowerCase().equals("german")|language.toLowerCase().equals("deutsch"))
                return "DE";
            else if (language.toLowerCase().equals("english")|language.toLowerCase().equals("englisch"))
                return "EN";
            else {
                Log.e(TAG, "Your chosen language (" + language + ") is not supported.");
            }
            return null;
        }

        /**
         * Generates the url for some vin's information.
         *
         * @param vin   The vin to get information to.
         * @return      The url to get the information from.
         */
        public String getVinInformationUrl(String vin) {
            String request = "vin";
            String[] params = {"vin=" + vin.toUpperCase(), "client_id=" + CLIENT_ID , "client_secret=" + CLIENT_SECRET};
            return generateURL(request, params);
        }

        /**
         * Generates a readable response consisting of the dtc's information (System & Fault) from a json object.
         * @param response  DTC's translation received from the API.
         * @return          The formated DTC translation.
         */
        private String formatDtcTranslation(JSONObject response, String lang) {
            String result = null;
            try {
                JSONObject dtc_data = response.getJSONObject("dtc_data");
                String system = "System: " + dtc_data.getString("system");
                String fault;
                if(lang.equals(c.getString(R.string.german).toLowerCase())) {
                    fault = "Fehler: " + dtc_data.getString("fault");
                }else{
                    fault = "Fault: " + dtc_data.getString("fault");
                }
                result = system + "\n" + fault;
            } catch (JSONException jsonE) {
                Log.e(TAG, jsonE.toString());
            }
            return result;
        }

        /**
         *  Creation of the HTTP GETTranslation request.
         * @param url The url the send the GETTranslation request to.
         * @param errorCode The error code to be translated.
         * @return The request object
         */
        private JsonObjectRequest GETTranslation(String url, final String errorCode, final String lang) {
            if (url != null) {
                Log.d(TAG, "making get request:" + url);

                return new JsonObjectRequest(Request.Method.GET, url, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d(TAG, response.toString());


                                Search.addListItem(errorCode + "\n" + formatDtcTranslation(response, lang));
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                if (error instanceof ServerError)


                                    Search.addListItem(errorCode + "\n" + "Server Error occured (Status Code: " + error.networkResponse.statusCode + ")");
                                VolleyLog.e(error.toString());
                            }
                        });
            }
            return null;
        }




    }

