package fr.mysafeauto.mysafe;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import fr.mysafeauto.mysafe.Forms.FormAddVehicleActivity;
import fr.mysafeauto.mysafe.Services.Coordinate.Coordinate;
import fr.mysafeauto.mysafe.Services.Coordinate.CustomAdapterCoordinate;
import fr.mysafeauto.mysafe.Services.Coordinate.ServiceGetCoordinate;
import fr.mysafeauto.mysafe.Services.Owner.Owner;
import fr.mysafeauto.mysafe.Services.ServiceCallBack;
import fr.mysafeauto.mysafe.Services.Vehicle.CustomAdapterVehicle;
import fr.mysafeauto.mysafe.Services.Vehicle.OnSwipeTouchListener;
import fr.mysafeauto.mysafe.Services.Vehicle.ServiceGetVehicle;
import fr.mysafeauto.mysafe.Services.Vehicle.Vehicle;


public class ContentActivity extends AppCompatActivity
        implements OnMapReadyCallback, ServiceCallBack, GoogleMap.InfoWindowAdapter, GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap;
    Toolbar toolbar;
    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    Context mContext;

    // Right drawer elements
    List<Vehicle> vehicleList = new ArrayList<>();
    ListView vehicleListView;
    CustomAdapterVehicle vehicleAdapter;
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
    int savedItemPosVehicle = 0;
    //int savedItemPosCoord = 0;

    // Left drawer elements
    List<Coordinate> coordinateList = new ArrayList<>();
    ListView coordinateListView;
    CustomAdapterCoordinate coordinateAdapter;

    ServiceGetCoordinate serviceGetCoordinate;
    String urlCooridnateDisplay="http://mysafe.cloudapp.net/mysafe/rest/coordinates/imei/";

    Owner owner;
    SQLiteDatabase db;

    //header listview Info
    TextView tv_owner_firstname;
    TextView tv_owner_lastname;
    TextView tv_owner_email;

    TextView tv_vehicle_brand;
    TextView tv_vehicle_color;

    // MAps marker
    LatLng coord_GPS;
    String content_address;
    TextView tv_address;

    //Float Image logout
    ImageView btn_logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



        db = openOrCreateDatabase("MysafeAppDB", Context.MODE_PRIVATE, null);

        owner = getOwnerFromDb();
        //showMessage("Owner Data transfered",owner.toString());
        if(owner !=null){
            urlVehicleDisplay = "http://mysafe.cloudapp.net/mysafe/rest/owners/id/"+owner.getId()+"/vehicles";
            urlVehicleCreate = "http://mysafe.cloudapp.net/mysafe/rest/vehicles/create?owner_id="+owner.getId()+"";
        }

        tv_owner_firstname = (TextView)findViewById(R.id.txt_owner_firstname);
        tv_owner_lastname = (TextView)findViewById(R.id.txt_owner_lastname);
        tv_owner_email = (TextView)findViewById(R.id.txt_owner_email);

        tv_vehicle_brand = (TextView)findViewById(R.id.txt_vehicle_brand);
        tv_vehicle_color = (TextView)findViewById(R.id.txt_vehicle_color);
        tv_vehicle_color.setVisibility(View.INVISIBLE);
        tv_vehicle_brand.setVisibility(View.INVISIBLE);

        tv_owner_firstname.setText(owner.getFirst_name());
        tv_owner_lastname.setText(owner.getLast_name());
        tv_owner_email.setText(owner.getEmail());

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        dialog = new ProgressDialog(this);


        vehicleListView = (ListView)findViewById(R.id.vehicleListView);
        vehicleAdapter = new CustomAdapterVehicle(this, vehicleList);
        vehicleListView.setAdapter(vehicleAdapter);
        vehicleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object o = vehicleListView.getItemAtPosition(position);
                callServiceCoordinateDisplay(((Vehicle) o).getImei());


                //Mettre à jour le header coordonnee
                tv_vehicle_brand.setText(((Vehicle) o).getBrand());
                tv_vehicle_color.setText(((Vehicle) o).getColor());

                parent.getChildAt(position).setBackgroundColor(Color.parseColor("#678FBA"));
                if (savedItemPosVehicle != position) {
                    parent.getChildAt(savedItemPosVehicle).setBackgroundColor(Color.TRANSPARENT);
                }

                savedItemPosVehicle = position;
            }
        });
        coordinateListView = (ListView)findViewById(R.id.coordListView);

        coordinateAdapter = new CustomAdapterCoordinate(this, coordinateList);
        coordinateListView.setAdapter(coordinateAdapter);

        //MAJ des services
        callServiceVehicleDisplay();


        coordinateListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Object o = coordinateListView.getItemAtPosition(position);

                // parent.getChildAt(position).setBackgroundColor(Color.parseColor("#678FBA"));
                // coordinateListView.setItemChecked(position, true);
                double tmp_lat = Double.parseDouble(((Coordinate) o).getLatitude());
                double tmp_lon = Double.parseDouble(((Coordinate) o).getLongitude());
                putMarker(tmp_lat, tmp_lon);

                // if (savedItemPosCoord != position) {
                //   parent.getChildAt(savedItemPosCoord).setBackgroundColor(Color.TRANSPARENT);
                // }

                //savedItemPosCoord = position;
            }
        });





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

        btn_logout = (ImageView)findViewById(R.id.btn_logout);
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(mContext)
                        .setTitle("User disconnect")
                        .setMessage("Are you sure you want to disconnect ?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //Delete dattabase about the user
                                File file = new File("data/data/fr.mysafeauto.mysafe/databases/MysafeAppDB");
                                file.delete();
                                //Kill app
                                System.exit(1);
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            }
        });


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

    /*@Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        return super.dispatchTouchEvent(ev);
    }*/

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
                            callServiceVehicleDelete(vehicleList.get(position).getImei());
                        }
                    });
                    edit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent  = new Intent(mContext,FormAddVehicleActivity.class);
                            intent.putExtra("old_imei", vehicleList.get(position).getImei());
                            intent.putExtra("old_brand", vehicleList.get(position).getBrand());
                            intent.putExtra("old_color", vehicleList.get(position).getColor());
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
            if(data.hasExtra("imei") && data.hasExtra("brand") && data.hasExtra("color")){
                callServiceVehicleUpdate(data.getExtras().getString("imei"), data.getExtras().getString("brand"), data.getExtras().getString("color"));
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
                tv_vehicle_color.setVisibility(View.VISIBLE);
                tv_vehicle_brand.setVisibility(View.VISIBLE);
                //Mettre à jour le header coordonnee
                tv_vehicle_brand.setText(vehicleList.get(0).getBrand());
                tv_vehicle_color.setText(vehicleList.get(0).getColor());
            }
            else
                showMessage("Info","Your vehicle list is empty, add vehicles to track them.");

        }
        if(id_srv == 3){
            //showMessage("Result", "Vehicle added.");
            String result = (String)object;
            if(result.equals("Error")){
                showMessage("Alert creation vehicle","This imei is unavailable.");
            }
        }
        if(id_srv == 4){
            //showMessage("Result","Vehicle deleted.");
            String result = (String)object;
            if(result.equals("Error")){
                showMessage("Alert deletion vehicle","This vehicle is already deleted.");
            }
        }

        if(id_srv == 5){
            //showMessage("Result", "Vehicle updated");
        }
        if(id_srv == 6){
            coordinateList.clear();
            if( object != null){
                // Coordinate display
                coordinateList.addAll((List<Coordinate>) object);
                double tmp_lat = Double.parseDouble(((List<Coordinate>) object).get(0).getLatitude());
                double tmp_lon = Double.parseDouble(((List<Coordinate>) object).get(0).getLongitude());
                putMarker(tmp_lat, tmp_lon);
                drawer.closeDrawer(Gravity.LEFT);
                drawer.openDrawer(Gravity.RIGHT);

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
        serviceGetVehicle.refreshLocalisation(urlVehicleDisplay, null, null);
    }

    public void callServiceVehicleCreate(String postParamCreate){
        serviceGetVehicle = new ServiceGetVehicle(this, dialog, "create");
        serviceGetVehicle.refreshLocalisation(urlVehicleCreate, postParamCreate, null);
        callServiceVehicleDisplay();

    }

    public void callServiceVehicleDelete(String imei){
        serviceGetVehicle = new ServiceGetVehicle(this, dialog, "delete");
        serviceGetVehicle.refreshLocalisation(urlVehicleDelete + imei, null, null);
        callServiceVehicleDisplay();
    }

    public void callServiceVehicleUpdate(String imei, String brand, String color){
        serviceGetVehicle = new ServiceGetVehicle(this, dialog, "update");
        serviceGetVehicle.refreshLocalisation(imei, brand, color);
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
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        // Add a marker in Sydney and move the camera

   //     mMap.setOnInfoWindowClickListener(this);
        mMap.setInfoWindowAdapter(this);

    }



    @Override
    public View getInfoContents(Marker marker) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.marker_window, null);
        tv_address = (TextView)view.findViewById(R.id.txt_address);
        tv_address.setText(content_address);
        return view;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
    }

    public void putMarker(double tmp_lat, double tmp_lon ){
        coord_GPS =  new LatLng(tmp_lat,tmp_lon);
        content_address = getAddress(tmp_lat, tmp_lon);
        drawer.closeDrawer(Gravity.RIGHT);
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(coord_GPS)).showInfoWindow();
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coord_GPS, 18));


    }

    public String getAddress(double lat,double lon){
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());
        StringBuilder sb = new StringBuilder();
        try {
            addresses = geocoder.getFromLocation(lat, lon, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();

            sb.append(address).append("\n").append(postalCode).append(" ").append(city).append("\n").append(country);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

}


