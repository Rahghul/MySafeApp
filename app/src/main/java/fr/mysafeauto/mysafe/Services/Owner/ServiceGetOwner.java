package fr.mysafeauto.mysafe.Services.Owner;

import android.app.ProgressDialog;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import fr.mysafeauto.mysafe.MainActivity;
import fr.mysafeauto.mysafe.Services.ServiceCallBack;
import fr.mysafeauto.mysafe.Services.Vehicle.Vehicle;
import fr.mysafeauto.mysafe.Services.WebServiceUtil;

/**
 * Created by Rahghul on 10/02/2016.
 */
public class ServiceGetOwner  {
    private String mEmail;
    private Exception error;
    private ServiceCallBack callBack;
    private ProgressDialog dialog;

    public ServiceGetOwner(ServiceCallBack callBack, String email, ProgressDialog dialog) {
        this.mEmail = email;
        this.callBack = callBack;
        this.dialog = dialog;
    }

    public void refreshOwner() {
        new AsyncTask<Void, Void, Owner>() {

            @Override
            protected void onPreExecute() {
                dialog.setMessage("Please wait..");
                dialog.show();
            }

            /**
             * Executes the asynchronous job. This runs when you call execute()
             * on the AsyncTask instance.
             */
            @Override
            protected Owner doInBackground(Void... params) {
                try {
                    String urlOwnerGet = "http://mysafe.cloudapp.net/mysafe/rest/owners/email/";
                    return findAllItems(urlOwnerGet + mEmail);
                } catch (Exception e) {
                    error = e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Owner owner) {

                if (error != null) {
                    callBack.serviceFailure(error);
                } else {
                    callBack.serviceSuccess(owner, 0);
                }

            }
        }.execute();
    }

    public Owner findAllItems(String endpoint) throws JSONException {
        String s = WebServiceUtil.requestWebServiceGET(endpoint);

        if(s==null){
            return null;
        }
        JSONObject data = new JSONObject(s);
        return new Owner(data.getInt("id"), data.getString("firstname"),
                data.getString("lastname"), data.getString("passwd"), mEmail);
    }


}
