package fr.mysafeauto.mysafe.Services.Vehicle;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import fr.mysafeauto.mysafe.Services.ServiceCallBack;
import fr.mysafeauto.mysafe.Services.WebServiceUtil;

/**
 * Created by Rahghul on 06/02/2016.
 */

public class ServiceGetVehicle {
    private ServiceCallBack callBack;
    private Exception error;
    private ProgressDialog dialog;
    private String service; // option: create, delete, update, display
    private int result;

    public ServiceGetVehicle(ServiceCallBack callBack, ProgressDialog progressDialog, String service) {
        this.service = service;
        this.dialog = progressDialog;
        this.callBack = callBack;
    }

    public void setService(String service) {
        this.service = service;
    }

    public void refreshLocalisation(final String str1, final String str2, final String str3){

        new AsyncTask<String, Void, List<Vehicle>>() {

            @Override
            protected void onPreExecute() {
                dialog.setMessage("Please wait..");
                dialog.show();
            }

            @Override
            protected List<Vehicle> doInBackground(String... params) {
                try{

                    switch (service){
                        case "display":
                            // Vehicle Display to ListView
                            return findAllItems(params[0]);
                        case "create":
                            // Vehicle Create
                            result = WebServiceUtil.requestWebServicePOST(params[0], params[1]);
                            return null;
                        case "delete":
                            //Vehicle Delete
                            result = WebServiceUtil.requestWebServiceDELETE(params[0]);
                            return null;
                        case "update":
                            //Vehicle Edit
                            String urlVehicleUpdate="http://mysafe.cloudapp.net/mysafe/rest/vehicles/update/";
                            String urlVehicleGetJSON="http://mysafe.cloudapp.net/mysafe/rest/vehicles/";

                            String jsonStr = WebServiceUtil.requestWebServiceGET(urlVehicleGetJSON+params[0]);
                            String jsonStrModify;
                            try {
                                JSONObject json = new JSONObject(jsonStr);
                                json.put("brand", params[1]);
                                json.put("color", params[2]);
                                jsonStrModify = json.toString();
                                WebServiceUtil.requestWebServicePUT(urlVehicleUpdate+params[0], jsonStrModify);
                            } catch (JSONException e) {
                                error = e;
                            }

                            return null;
                        default:
                            throw (new Exception("Unknown Service : Vehicle"));
                    }

                } catch (Exception e) {
                    error = e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<Vehicle> vehicles) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                try {
                    if(error !=null){
                        callBack.serviceFailure(error);
                        return;
                    }
                    switch (service) {
                        case "display":
                            // Vehicle Display to ListView
                            if(vehicles == null){
                                callBack.serviceFailure(error);
                                return;
                            }
                            callBack.serviceSuccess(vehicles, 2);
                            return;
                        case "create":
                            // Vehicle Create
                            if(result != 201)
                                callBack.serviceSuccess("Error", 3);
                            else
                                callBack.serviceSuccess("Success", 3);
                            return;
                        case "delete":
                            //Vehicle Delete
                            if(result != 200)
                                callBack.serviceSuccess("Error",4);
                            else
                                callBack.serviceSuccess("Success", 4);
                            return;
                        case "update":
                            callBack.serviceSuccess("Success", 5);
                            //Vehicle Edit
                            return;
                        default:
                            throw (new Exception("Unknown Service : Vehicle"));
                    }
                }
                catch (Exception e) {
                    error = e;
                }

            }
        }.execute(str1, str2, str3);
    }

    public List<Vehicle> findAllItems(String endpoint) throws JSONException {
        String s = WebServiceUtil.requestWebServiceGET(endpoint);
        if(s==null){
            return null;
        }

        List<Vehicle> foundVehicles = new ArrayList<Vehicle>();

        JSONArray  data = new JSONArray(s);

        for(int i =0; i<data.length(); i++){
            JSONObject p = null;
                p = data.getJSONObject(i);

                foundVehicles.add(
                        new Vehicle(p.getString("imei"),
                                p.getString("brand"),
                                p.getString("color")));
        }
        return foundVehicles;
    }

}
