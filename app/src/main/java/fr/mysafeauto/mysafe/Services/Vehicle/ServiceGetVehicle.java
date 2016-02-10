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

    public ServiceGetVehicle(ServiceCallBack callBack, ProgressDialog progressDialog, String service) {
        this.service = service;
        this.dialog = progressDialog;
        this.callBack = callBack;
    }

    public void setService(String service) {
        this.service = service;
    }

    public void refreshLocalisation(final String endpoint, final String postParam){

        new AsyncTask<String, Void, List<Vehicle>>() {

            @Override
            protected void onPreExecute() {
                // TODO i18n
             //   dialog = new ProgressDialog(c);
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
                            //WebServiceUtil.requestWebService2(params[0], params[1], "POST");
                            WebServiceUtil.requestWebServicePOST(params[0], params[1]);
                            return null;
                        case "delete":
                            //Vehicle Delete
                           // WebServiceUtil.requestWebService2(params[0],null, "DELETE");
                            WebServiceUtil.requestWebServiceDELETE(params[0]);
                            return null;
                        case "edit":
                            //Vehicle Edit
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
                    switch (service) {
                        case "display":
                            // Vehicle Display to ListView
                            if(vehicles == null && error !=null){
                                callBack.serviceFailure(error);
                                return;
                            }
                            callBack.serviceSuccess(vehicles, 2);
                            return;
                        case "create":
                            // Vehicle Create
                            callBack.serviceSuccess("Success", 3);
                            return;
                        case "delete":
                            //Vehicle Delete
                            callBack.serviceSuccess("Success", 4);
                            return;
                        case "edit":
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
        }.execute(endpoint, postParam);
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
