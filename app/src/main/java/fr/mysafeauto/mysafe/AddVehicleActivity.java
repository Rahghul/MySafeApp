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
public class AddVehicleActivity extends AppCompatActivity {

    Button BtnAddVehcile;
    EditText EdTxt_IMEI;
    EditText EdTxt_Brand;
    EditText EdTxt_Color;

    Vehicle mvehicle;

    SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.form_add_vehicle);

        db = openOrCreateDatabase("MysafeAppDB", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS vehicles(imei VARCHAR,brand VARCHAR,color VARCHAR);");

        EdTxt_IMEI =(EditText)findViewById(R.id.et_imei);
        EdTxt_Brand =(EditText)findViewById(R.id.et_brand);
        EdTxt_Color =(EditText)findViewById(R.id.et_color);

        BtnAddVehcile = (Button)findViewById(R.id.btn_add_vehicle);
        BtnAddVehcile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mvehicle = new Vehicle(EdTxt_IMEI.getText().toString(), EdTxt_Brand.getText().toString(), EdTxt_Color.getText().toString());

                //A ecrire check if imei existe dans BD sinon output error !!!!!!!!!!!!!!!

                db.execSQL("INSERT INTO vehicles VALUES('" + mvehicle.getImei() + "','" + mvehicle.getBrand() +
                        "','" + mvehicle.getColor() + "');");

                Intent intent = new Intent();
                setResult(RESULT_OK, intent);

                finish();
            }
        });
    }
}
