package fr.mysafeauto.mysafe;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import fr.mysafeauto.mysafe.Forms.FormAddVehicleActivity;
import fr.mysafeauto.mysafe.Services.Coordinate.Coordinate;
import fr.mysafeauto.mysafe.Services.Coordinate.CustomAdapterLeft;
import fr.mysafeauto.mysafe.Services.Coordinate.ServiceGetCoordinate;
import fr.mysafeauto.mysafe.Services.Owner.Owner;
import fr.mysafeauto.mysafe.Services.ServiceCallBack;
import fr.mysafeauto.mysafe.Services.Vehicle.CustomAdapterRight;
import fr.mysafeauto.mysafe.Services.Vehicle.OnSwipeTouchListener;
import fr.mysafeauto.mysafe.Services.Vehicle.ServiceGetVehicle;
import fr.mysafeauto.mysafe.Services.Vehicle.Vehicle;

/**
 * Created by Rahghul on 10/02/2016.
 */
public class ContentActivity extends AppCompatActivity
        implements OnMapReadyCallback, ServiceCallBack {

    private GoogleMap mMap;
    Toolbar toolbar;
    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    Context mContext;

    // Right drawer elements
    List<Vehicle> vehicleList = new ArrayList<Vehicle>();
    ListView vehicleListView;
    CustomAdapterRight vehicleAdapter;
    ImageView btn_add_vehicle;
    ImageView delete;
    ImageView edit;

    ProgressDialog dialog;
    ServiceGetVehicle serviceGetVehicle;
    String urlVehicleDisplay;
    String urlVehicleCreate;
    String urlVehicleDelete="http://mysafe.cloudapp.net/mysafe/rest/vehicles/delete/";
    String postParam;

    int position = 0;
    int savedItemPos = 0;

    // Left drawer elements
    List<Coordinate> coordinateList = new ArrayList<Coordinate>();
    ListView coordinateListView;
    CustomAdapterLeft coordinateAdapter;

    ServiceGetCoordinate serviceGetCoordinate;
    String urlCooridnateDisplay="http://mysafe.cloudapp.net/mysafe/rest/coordinates/imei/";

    Owner owner;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = openOrCreateDatabase("MysafeAppDB", Context.MODE_PRIVATE, null);

        owner = getOwnerFromDb();
        //showMessage("Owner Data transfered",owner.toString());
        if(owner !=null){
            urlVehicleDisplay = "http://mysafe.cloudapp.net/mysafe/rest/owners/id/"+owner.getId()+"/vehicles";
            urlVehicleCreate = "http://mysafe.cloudapp.net/mysafe/rest/vehicles/create?owner_id="+owner.getId()+"";
        }

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        vehicleListView = (ListView)findViewById(R.id.rightListView);
        vehicleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object o = vehicleListView.getItemAtPosition(position);
                callServiceCoordinateDisplay(((Vehicle) o).getImei());
                drawer.closeDrawer(Gravity.LEFT);
                drawer.openDrawer(Gravity.RIGHT);

                parent.getChildAt(position).setBackgroundColor(Color.parseColor("#678FBA"));
                if (savedItemPos != position) {
                    parent.getChildAt(savedItemPos).setBackgroundColor(Color.TRANSPARENT);
                }

                savedItemPos = position;
            }
        });
        coordinateListView = (ListView)findViewById(R.id.leftListView);

        vehicleAdapter = new CustomAdapterRight(this, vehicleList);
        vehicleListView.setAdapter(vehicleAdapter);

        coordinateAdapter = new CustomAdapterLeft(this, coordinateList);
        coordinateListView.setAdapter(coordinateAdapter);

        dialog = new ProgressDialog(this);

        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                for(int i=0; i< vehicleList.size();i++){
                    hideDeleteEditButton(i);
                }
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                for(int i=0; i< vehicleList.size();i++){
                    hideDeleteEditButton(i);
                }
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //MAJ des services
        callServiceVehicleDisplay();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Refresh last localisation.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mContext = this;
        btn_add_vehicle = (ImageView)findViewById(R.id.btn_show_form);
        btn_add_vehicle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View vw) {
                startActivityForResult(new Intent(ContentActivity.this, FormAddVehicleActivity.class), 2);// Activity is started with requestCode 2

            }
        });



        vehicleListView.setOnTouchListener(new OnSwipeTouchListener(this, vehicleListView) {
            @Override
            public void onSwipeRight(int pos) {
                showDeleteEditButton(pos);
            }

            @Override
            public void onSwipeLeft(int pos) {

            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        return super.dispatchTouchEvent(ev);
    }

    private boolean showDeleteEditButton(int pos) {
        hideDeleteEditButton(position);
        position = pos;
        View child = vehicleListView.getChildAt(pos - vehicleListView.getFirstVisiblePosition());
        if (child != null) {
            delete = (ImageView) child.findViewById(R.id.delete);
            edit = (ImageView) child.findViewById(R.id.edit);
            if (delete != null) {
                if (delete.getVisibility() == View.INVISIBLE) {
                    Animation animationLeft = AnimationUtils.loadAnimation(this, R.anim.slide_out_left);
                    delete.startAnimation(animationLeft);
                    delete.setVisibility(View.VISIBLE);
                    edit.setAnimation(animationLeft);
                    edit.setVisibility(View.VISIBLE);
                    delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            callServiceVehicleDelete(vehicleList.get(position).getImei().toString());
                        }
                    });
                    edit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent  = new Intent(mContext,FormAddVehicleActivity.class);
                            intent.putExtra("old_imei", vehicleList.get(position).getImei().toString());
                            intent.putExtra("old_brand", vehicleList.get(position).getBrand().toString());
                            intent.putExtra("old_color", vehicleList.get(position).getColor().toString());
                            startActivityForResult(intent, 3);

                        }
                    });

                } else {
                    Animation animationRight = AnimationUtils.loadAnimation(this,R.anim.slide_in_right);
                    delete.startAnimation(animationRight);
                    delete.setVisibility(View.INVISIBLE);
                    edit.startAnimation(animationRight);
                    edit.setVisibility(View.INVISIBLE);

                }
            }
            return true;
        }
        return false;
    }


    private boolean hideDeleteEditButton(int pos) {
        position = pos;
        View child = vehicleListView.getChildAt(pos - vehicleListView.getFirstVisiblePosition());
        if (child != null) {

            delete = (ImageView) child.findViewById(R.id.delete);
            edit = (ImageView) child.findViewById(R.id.edit);
            if (delete != null && edit != null) {
                delete.setVisibility(View.INVISIBLE);
                edit.setVisibility(View.INVISIBLE);
            }
            return true;
        }
        return false;
    }

    // Call Back method  to get the Message form other Activity    override the method
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == RESULT_OK) {
            if(data.hasExtra("postParamCreateVehicle")) {
                postParam = data.getExtras().getString("postParamCreateVehicle");
                callServiceVehicleCreate(postParam);

            }
        }
        if(requestCode == 3 && resultCode == RESULT_OK){
            if(data.hasExtra("postParamCreateVehicle") && data.hasExtra("imei")){
                callServiceVehicleDelete(data.getExtras().getString("imei"));
                postParam = data.getExtras().getString("postParamCreateVehicle");
                callServiceVehicleCreate(postParam);
            }
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            drawer.openDrawer(vehicleListView);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

  /*  @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }*/

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }



    public void showMessage(String title,String message)
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }



    @Override
    public void serviceSuccess(Object object, int id_srv) {

        if(id_srv == 2) {
            //Vehicle display
            vehicleList.clear();
            if(object != null) {
                vehicleList.addAll((List<Vehicle>) object);
                vehicleAdapter.notifyDataSetChanged();
                callServiceCoordinateDisplay(vehicleList.get(0).getImei());
            }
            else
                showMessage("Info","Your vehicle list is empty, add vehicles to track them.");

        }
        if(id_srv == 3){
            //showMessage("Result", "Vehicle added.");
        }
        if(id_srv == 4){
            //showMessage("Result","Vehicle deleted.");
        }

        if(id_srv == 5){
            coordinateList.clear();
            if((List<Coordinate>) object != null){
                // Coordinate display
                coordinateList.addAll((List<Coordinate>) object);
            }
            coordinateAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void serviceFailure(Exception exception) {
        Toast.makeText(this, exception.getMessage(), Toast.LENGTH_LONG).show();
    }


    public void callServiceVehicleDisplay(){
        serviceGetVehicle = new ServiceGetVehicle(this, dialog, "display");
        serviceGetVehicle.refreshLocalisation(urlVehicleDisplay, null);
    }

    public void callServiceVehicleCreate(String postParamCreate){
        serviceGetVehicle = new ServiceGetVehicle(this, dialog, "create");
        serviceGetVehicle.refreshLocalisation(urlVehicleCreate, postParamCreate);
        callServiceVehicleDisplay();

    }

    public void callServiceVehicleDelete(String imei){
        serviceGetVehicle = new ServiceGetVehicle(this, dialog, "delete");
        serviceGetVehicle.refreshLocalisation(urlVehicleDelete + imei, null);
        callServiceVehicleDisplay();
    }

    public void callServiceCoordinateDisplay(String imei){
        serviceGetCoordinate = new ServiceGetCoordinate(this, dialog);
        serviceGetCoordinate.refreshLocalisation(urlCooridnateDisplay + imei);
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

    public Owner getOwnerFromDb(){

        // Retrieving all records
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
            return new Owner(c.getInt(0), c.getString(1), c.getString(2), c.getString(4), c.getString(3));
        }
        return  null;
    }
}


