package fr.mysafeauto.mysafe.Services.Owner;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import fr.mysafeauto.mysafe.OAuthActivity;
import fr.mysafeauto.mysafe.R;
import fr.mysafeauto.mysafe.Services.ServiceCallBack;
import fr.mysafeauto.mysafe.Services.WebServiceUtil;

/**
 * Created by Rahghul on 09/02/2016.
 */

public class ServiceCreateOwner{
    OAuthActivity mActivity;
    String mScope;
    String mEmail;
    ServiceCallBack callBack;
    private Exception error;
    Owner owner;
    int statusCode;
    ProgressDialog dialog;

    public ServiceCreateOwner(OAuthActivity activity, String name, String scope, ServiceCallBack callBack, ProgressDialog dialog) {
        this.mActivity = activity;
        this.mScope = scope;
        this.mEmail = name;
        this.callBack = callBack;
        this.dialog = dialog;
    }

    public void createOwner() {
        new AsyncTask<Void, Void, Boolean>(){
            @Override
            protected void onPreExecute() {
                dialog.setMessage("Creating...");
                dialog.show();
            }

            /**
         * Executes the asynchronous job. This runs when you call execute()
         * on the AsyncTask instance.
         */



        @Override
        protected Boolean doInBackground (Void...params){
            try {

                String token = fetchToken();
                if (token != null) {
                    // Use the token to access the user's Google data.

                    String urlGoogleServer = "https://www.googleapis.com/oauth2/v1/userinfo?alt=json&access_token=" + token;
                    Log.d("Google Srv", urlGoogleServer);
                    owner = findAllItems(urlGoogleServer);
                    Log.d("Owner found ", owner.toString());


                    String urlOwnerCreate = "http://mysafe.cloudapp.net/mysafe/rest/owners/create";
                    String postParam = "last_name=" + owner.getLast_name() + "&first_name=" + owner.getFirst_name() + "&email=" + owner.getEmail() + "&passwd=0000";
                    statusCode = WebServiceUtil.requestWebServicePOST(urlOwnerCreate, postParam);
                    Boolean accessLogin = false;
                    if (statusCode == 201) {
                        Log.d("Owner Creation", statusCode + " New Owner created.");
                        accessLogin = true;
                    } else {
                        if (statusCode == 409) {
                            Log.d("Owner Creation", statusCode + " This owner already exists.");
                            accessLogin = true;
                        } else
                            Log.d("Owner Creation", "Problem on creation process, please retry or contact MySafe customer service.");
                    }

                    return accessLogin;
                }
                return null;

            } catch (IOException e) {
                error = e;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute (Boolean accessLogin){

            if(owner == null){
                dialog.hide();
                Toast.makeText(mActivity, R.string.loginInfo , Toast.LENGTH_LONG).show();
                return;
            }

            if (error != null) {
                callBack.serviceFailure(error);
            } else {
                callBack.serviceSuccess(accessLogin, 1);
            }

        }
        }.execute();
    }


    /**
     * Gets an authentication token from Google and handles any
     * GoogleAuthException that may occur.
     */
    protected String fetchToken() throws IOException {
        try {
            return GoogleAuthUtil.getToken(mActivity, mEmail, mScope);

        } catch (UserRecoverableAuthException userRecoverableException) {
            // GooglePlayServices.apk is either old, disabled, or not present
            // so we need to show the user some UI in the activity to recover.
            mActivity.handleException(userRecoverableException);
        } catch (GoogleAuthException fatalException) {
            // Some other type of unrecoverable exception has occurred.
            // Report and log the error as appropriate for your app.
            //...
            Log.d("ERROR",fatalException.toString());
            //callBack.serviceFailure(fatalException);
        }
        return null;
    }


    public Owner findAllItems(String endpoint) throws JSONException {
        String s = WebServiceUtil.requestWebServiceGET(endpoint);

        JSONObject data = new JSONObject(s);
        return new Owner(data.getString("given_name"), data.getString("family_name"), mEmail);
    }

    public void showMessage(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }

}
