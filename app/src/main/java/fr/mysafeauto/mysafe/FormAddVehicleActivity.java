package fr.mysafeauto.mysafe;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import fr.mysafeauto.mysafe.Services.Vehicle.Vehicle;

/**
 * Created by Rahghul on 05/02/2016.
 */
public class FormAddVehicleActivity extends AppCompatActivity {

    Button BtnAddVehcile;
    EditText EdTxt_IMEI;
    EditText EdTxt_Brand;
    EditText EdTxt_Color;

    String postParam;

    Vehicle mvehicle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.form_add_vehicle);


        EdTxt_IMEI =(EditText)findViewById(R.id.et_imei);
        EdTxt_Brand =(EditText)findViewById(R.id.et_brand);
        EdTxt_Color =(EditText)findViewById(R.id.et_color);

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
                postParam = "imei="+EdTxt_IMEI.getText().toString()+
                            "&brand="+EdTxt_Brand.getText().toString()+
                            "&color="+EdTxt_Color.getText().toString();
                //A ecrire check if imei existe dans BD sinon output error !!!!!!!!!!!!!!!

                Intent intent = new Intent();
                intent.putExtra("postParamCreateVehicle", postParam);
                intent.putExtra("imei", old_imei);
                setResult(RESULT_OK, intent);

                finish();
            }
        });
    }


}
