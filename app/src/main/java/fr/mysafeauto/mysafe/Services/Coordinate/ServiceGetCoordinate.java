package fr.mysafeauto.mysafe.Services.Coordinate;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import fr.mysafeauto.mysafe.Services.ServiceCallBack;
import fr.mysafeauto.mysafe.Services.Vehicle.Vehicle;
import fr.mysafeauto.mysafe.Services.WebServiceUtil;

/**
 * Created by Rahghul on 08/02/2016.
 */
public class ServiceGetCoordinate {
    ServiceCallBack callBack;
    private Exception error;
    private ProgressDialog dialog;

    public ServiceGetCoordinate(ServiceCallBack callBack, ProgressDialog dialog) {
        this.callBack = callBack;
        this.dialog = dialog;
    }

    public void refreshLocalisation(final String endpoint){

        new AsyncTask<String, Void, List<Coordinate>>() {

            @Override
            protected void onPreExecute() {
                // TODO i18n
                //   dialog = new ProgressDialog(c);
                dialog.setMessage("Please wait..");
                dialog.show();
            }

            @Override
            protected List<Coordinate> doInBackground(String... params) {
                try{
                 return findAllItems(params[0]);
                } catch (Exception e) {
                 error = e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<Coordinate> coordinates) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                try {
                    if (error != null) {
                        callBack.serviceFailure(error);
                        return;
                    }
                    callBack.serviceSuccess(coordinates, 6);
                }
                catch (Exception e) {
                        error = e;
                }
            }
        }.execute(endpoint);
    }

    public List<Coordinate> findAllItems(String endpoint) throws JSONException {
        String s = WebServiceUtil.requestWebServiceGET(endpoint);

        if(s == null){
            return null;
        }
        List<Coordinate> foundCoordinates = new ArrayList<Coordinate>();

        JSONArray data = new JSONArray(s);
        for(int i =0; i<data.length(); i++){
            JSONObject p = data.getJSONObject(i);

            foundCoordinates.add(
                    new Coordinate(p.getString("id"),
                            p.getString("latitude"),
                            p.getString("longitude"),
                            p.getString("speed"),
                            p.getString("battery"),
                            new Date(Long.parseLong(p.getString("dateTime")))));
        }
        return foundCoordinates;
    }
}
