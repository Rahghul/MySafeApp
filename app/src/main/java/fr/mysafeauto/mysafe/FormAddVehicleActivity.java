package fr.mysafeauto.mysafe;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import fr.mysafeauto.mysafe.Filter.InputFilterMinMax;
import fr.mysafeauto.mysafe.Services.Vehicle.Vehicle;

/**
 * Created by Rahghul on 05/02/2016.
 */
public class FormAddVehicleActivity extends AppCompatActivity {

    Button BtnAddVehcile;
    AutoCompleteTextView EdTxt_IMEI;
    AutoCompleteTextView EdTxt_Brand;
    AutoCompleteTextView EdTxt_Color;

    String postParam;

    Vehicle mvehicle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.form_add_vehicle);


        EdTxt_IMEI =(AutoCompleteTextView)findViewById(R.id.et_imei);
        EdTxt_Brand =(AutoCompleteTextView)findViewById(R.id.et_brand);
        EdTxt_Color =(AutoCompleteTextView)findViewById(R.id.et_color);

        BtnAddVehcile = (Button)findViewById(R.id.btn_add_vehicle);

        Intent intent = getIntent();
        final String old_imei  = intent.getStringExtra("old_imei");
        String old_brand  = intent.getStringExtra("old_brand");
        String old_color = intent.getStringExtra("old_color");
        EdTxt_IMEI.setText(old_imei);
        EdTxt_Brand.setText(old_brand);
        EdTxt_Color.setText(old_color);

        BtnAddVehcile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EdTxt_IMEI.setError(null);
                String imei = EdTxt_IMEI.getText().toString();
                View focusView = null;
                boolean cancel = false;
                // Check for a valid email address.
                if (TextUtils.isEmpty(imei)) {
                    EdTxt_IMEI.setError(getString(R.string.error_field_required));
                    focusView = EdTxt_IMEI;
                    cancel = true;
                } else if (!isNumberValid(imei, 15)) {
                    EdTxt_IMEI.setError(getString(R.string.error_invalid_imei));
                    focusView = EdTxt_IMEI;
                    cancel = true;
                }

                EdTxt_Brand.setError(null);
                EdTxt_Color.setError(null);
                String brand = EdTxt_Brand.getText().toString();
                String color = EdTxt_Color.getText().toString();
                if(TextUtils.isEmpty(brand)){
                    EdTxt_Brand.setError(getString(R.string.error_field_required));
                    focusView = EdTxt_Brand;
                    cancel = true;
                }
                if(TextUtils.isEmpty(color)){
                    EdTxt_Color.setError(getString(R.string.error_field_required));
                    focusView = EdTxt_Color;
                    cancel = true;
                }

                if (cancel) {
                    // There was an error; don't attempt login and focus the first
                    // form field with an error.
                    focusView.requestFocus();
                }
                else {
                    postParam = "imei=" + EdTxt_IMEI.getText().toString() +
                            "&brand=" + EdTxt_Brand.getText().toString() +
                            "&color=" + EdTxt_Color.getText().toString();
                    //A ecrire check if imei existe dans BD sinon output error !!!!!!!!!!!!!!!

                    Intent intent = new Intent();
                    intent.putExtra("postParamCreateVehicle", postParam);
                    intent.putExtra("imei", old_imei);
                    setResult(RESULT_OK, intent);

                    finish();
                }

            }
        });
    }


    boolean isNumberValid(String s, int n){
        if(s.length() == n)
            return true;
        return false;
    }
}
