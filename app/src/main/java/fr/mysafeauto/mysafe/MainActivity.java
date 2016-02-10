package fr.mysafeauto.mysafe;

import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.GooglePlayServicesUtil;

import fr.mysafeauto.mysafe.Services.Owner.Owner;
import fr.mysafeauto.mysafe.Services.Owner.ServiceCreateOwner;
import fr.mysafeauto.mysafe.Services.Owner.ServiceGetOwner;
import fr.mysafeauto.mysafe.Services.ServiceCallBack;


public class MainActivity extends AppCompatActivity
        implements ServiceCallBack {

    Button buttonLogin;
    Owner owner;

    static final int REQUEST_CODE_PICK_ACCOUNT = 1000;
    static final int REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR = 1001;
    String SCOPE = "oauth2:https://www.googleapis.com/auth/userinfo.profile";
    String mEmail; // Received from newChooseAccountIntent(); passed to getToken()
    String dbEmail;

    SQLiteDatabase db;
    ServiceGetOwner serviceGetOwner;
    ServiceCreateOwner serviceCreateOwner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = openOrCreateDatabase("MysafeAppDB", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS owners(id INT, first_name VARCHAR,last_name VARCHAR,email VARCHAR, passwd VARCHAR);");

       // dbEmail = getEmailFromInternalDb();
        //dbEmail = "rahghul.madivanane@gmail.com";
        if(dbEmail == null) {
            pickUserAccount();
        }
        else {
            mEmail = dbEmail;
            Log.d("mEmail", dbEmail);
            getUsername(dbEmail);
        }

        buttonLogin = (Button) findViewById(R.id.btn_login);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickUserAccount();
            }
        });
    }



    private void pickUserAccount() {
        String[] accountTypes = new String[]{"com.google"};
        Intent intent = AccountPicker.newChooseAccountIntent(null, null,
                accountTypes, false, null, null, null, null);
        startActivityForResult(intent, REQUEST_CODE_PICK_ACCOUNT);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PICK_ACCOUNT) {
            // Receiving a result from the AccountPicker
            if (resultCode == RESULT_OK) {
                mEmail = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                // With the account name acquired, go get the auth token
                getUsername(mEmail);
            } else if (resultCode == RESULT_CANCELED) {
                // The account picker dialog closed without selecting an account.
                // Notify users that they must pick an account to proceed.
                //  Toast.makeText(this, R.string.pick_account, Toast.LENGTH_SHORT).show();
            }
        } else if ((
                requestCode == REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR)
                && resultCode == RESULT_OK) {
            // Receiving a result that follows a GoogleAuthException, try auth again
            getUsername(mEmail);
        }
    }

    /**
     * Attempts to retrieve the username.
     * If the account is not yet known, invoke the picker. Once the account is known,
     * start an instance of the AsyncTask to get the auth token and do work with it.
     */
    private void getUsername(String email) {
        if (email == null) {
            pickUserAccount();
        } else {
            serviceGetOwner = new ServiceGetOwner(this, email);
            serviceGetOwner.refreshOwner();
        }
    }

    public void showMessage(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }


    @Override
    public void serviceSuccess(Object object, int id_srv) {
        if (id_srv == 0) {
            owner = (Owner)object;
            if(owner == null){
                serviceCreateOwner = new ServiceCreateOwner(MainActivity.this, mEmail, SCOPE, this);
                serviceCreateOwner.createOwner();
                //new ServiceCreateOwner(MainActivity.this, mEmail, SCOPE, this).execute();
            }
            else{
                db.execSQL("DROP TABLE IF EXISTS owners");
                db.execSQL("CREATE TABLE IF NOT EXISTS owners(id INT, first_name VARCHAR,last_name VARCHAR,email VARCHAR, passwd VARCHAR);");
                //Insert into db to transfer the object to another activity
                db.execSQL("INSERT INTO owners VALUES('" + owner.getId()+ "','" + owner.getFirst_name() + "','" + owner.getLast_name() +
                        "','" + owner.getEmail() + "','" + owner.getPasswd() + "');");

                Intent myIntent = new Intent(MainActivity.this, ContentActivity.class);
                MainActivity.this.startActivity(myIntent);
            }
        }
        if (id_srv == 1) {
            Boolean accessLogin = (Boolean) object;
            if (accessLogin == true) {
                //viewDBApp();
                serviceGetOwner = new ServiceGetOwner(this, mEmail);
                serviceGetOwner.refreshOwner();
                //new ServiceGetOwner(this, mEmail).execute();
            }
            else{
                showMessage("Login Result","Login failed, please retry or contact MySafe customer care service to contact@mysafeauto.fr");
            }
        }
    }

    @Override
    public void serviceFailure(Exception exception) {
        Toast.makeText(this, exception.getMessage(), Toast.LENGTH_LONG).show();
    }

    /**
     * This method is a hook for background threads and async tasks that need to
     * provide the user a response UI when an exception occurs.
     */
    public void handleException(final Exception e) {
        // Because this call comes from the AsyncTask, we must ensure that the following
        // code instead executes on the UI thread.
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (e instanceof GooglePlayServicesAvailabilityException) {
                    // The Google Play services APK is old, disabled, or not present.
                    // Show a dialog created by Google Play services that allows
                    // the user to update the APK
                    int statusCode = ((GooglePlayServicesAvailabilityException) e)
                            .getConnectionStatusCode();
                    Dialog dialog = GooglePlayServicesUtil.getErrorDialog(statusCode,
                            MainActivity.this,
                            REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
                    dialog.show();
                } else if (e instanceof UserRecoverableAuthException) {
                    // Unable to authenticate, such as when the user has not yet granted
                    // the app access to the account, but the user can fix this.
                    // Forward the user to an activity in Google Play services.
                    Intent intent = ((UserRecoverableAuthException) e).getIntent();
                    startActivityForResult(intent,
                            REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
                }
            }
        });
    }


    public void viewDBApp(){
        // Retrieving all records
        Cursor c=db.rawQuery("SELECT * FROM owners", null);
        // Checking if no records found
        if(c.getCount()==0)
        {
            showMessage("Error", "No records found but table exist");
            return;
        }
        // Appending records to a string buffer
        StringBuffer buffer=new StringBuffer();
        while(c.moveToNext())
        {
            buffer.append("Id: "+c.getString(0)+"\n");
            buffer.append("First_name: "+c.getString(1)+"\n");
            buffer.append("Last_name: "+c.getString(2)+"\n");
            buffer.append("Email: "+c.getString(4)+"\n");
            buffer.append("Passwd: "+c.getString(3)+"\n\n");
        }

        // Displaying all records
        showMessage("Owner Table", buffer.toString());

    }

    public String getEmailFromInternalDb(){
        // Retrieving all records
        String email = null;
        Cursor c=db.rawQuery("SELECT * FROM owners", null);
        // Checking if no records found
        if(c.getCount()==0)
        {
            showMessage("Error", "No records found but table exist");
            return null;
        }
        // Appending records to a string buffer
        while(c.moveToNext())
        {
            email = c.getString(4);
        }
        return email;
    }
}