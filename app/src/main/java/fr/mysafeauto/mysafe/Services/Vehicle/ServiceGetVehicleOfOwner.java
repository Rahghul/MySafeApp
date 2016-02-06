package fr.mysafeauto.mysafe.Services.Vehicle;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

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

public class ServiceGetVehicleOfOwner {
    private ServiceCallBack callBack;
    private Exception error;
    private ProgressDialog dialog;

    public ServiceGetVehicleOfOwner(ServiceCallBack callBack, ProgressDialog progressDialog) {
        this.dialog = progressDialog;
        this.callBack = callBack;
    }

    public void refreshLocalisation(final String endpoint){

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
                   return findAllItems(params[0]);
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
                if(vehicles == null && error !=null){
                    callBack.serviceFailure(error);
                    return;
                }
                callBack.serviceSuccess(vehicles, 2);
            }
        }.execute(endpoint);
    }

    public List<Vehicle> findAllItems(String endpoint) throws JSONException {
        String s = null;
        s = WebServiceUtil.requestWebService(endpoint);


        List<Vehicle> foundVehicles = new ArrayList<Vehicle>(20);

        JSONArray data = null;
            data = new JSONArray(s);

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
