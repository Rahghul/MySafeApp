package fr.mysafeauto.mysafe;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import fr.mysafeauto.mysafe.Services.Coordinate.CustomAdapterLeft;
import fr.mysafeauto.mysafe.Services.Coordinate.ServiceGetCoordinate;
import fr.mysafeauto.mysafe.Services.Vehicle.CustomAdapterRight;
import fr.mysafeauto.mysafe.Forms.FormAddVehicleActivity;
import fr.mysafeauto.mysafe.Services.Vehicle.OnSwipeTouchListener;
import fr.mysafeauto.mysafe.Services.Coordinate.Coordinate;
import fr.mysafeauto.mysafe.Services.ServiceCallBack;
import fr.mysafeauto.mysafe.Services.Vehicle.ServiceGetVehicle;
import fr.mysafeauto.mysafe.Services.Vehicle.Vehicle;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, ServiceCallBack {

    private GoogleMap mMap;

    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    Context mContext;

    // Right drawer elements
    List<Vehicle> rightList = new ArrayList<Vehicle>();
    ListView rightListView;
    CustomAdapterRight rightAdapter;
    ImageView btn_add_vehicle;
    ImageView delete;
    ImageView edit;

    ProgressDialog dialog;
    ServiceGetVehicle serviceGetVehicle;
    String urlVehicleDisplay = "http://mysafe.cloudapp.net/mysafe/rest/owners/id/1/vehicles";
    //String urlVehicleDisplay = "http://mysafe.cloudapp.net/mysafe/rest/vehicles";
    String urlVehicleCreate = "http://mysafe.cloudapp.net/mysafe/rest/vehicles/create?owner_id=1";
    String urlVehicleDelete="http://mysafe.cloudapp.net/mysafe/rest/vehicles/delete/";
    String postParam;

    int position = 0;
    int savedItemPos = 0;
    // Left drawer elements
    List<Coordinate> leftList = new ArrayList<Coordinate>();
    ListView leftListView;
    CustomAdapterLeft leftAdapter;

    ServiceGetCoordinate serviceGetCoordinate;
    String urlCooridnateDisplay="http://mysafe.cloudapp.net/mysafe/rest/coordinates/imei/";





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        rightListView = (ListView)findViewById(R.id.rightListView);
        rightListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object o = rightListView.getItemAtPosition(position);
                callServiceCoordinateDisplay(((Vehicle) o).getImei());
                rightAdapter.notifyDataSetChanged();
                parent.getChildAt(position).setBackgroundColor(Color.parseColor("#678FBA"));

                if (savedItemPos != position){
                    parent.getChildAt(savedItemPos).setBackgroundColor(Color.TRANSPARENT);
                }

                savedItemPos = position;
            }
        });
        leftListView = (ListView)findViewById(R.id.leftListView);

        dialog = new ProgressDialog(this);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                for(int i=0; i< rightList.size();i++){
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
                for(int i=0; i< rightList.size();i++){
                    hideDeleteEditButton(i);
                }
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //MAJ des services
        callServiceVehicleDisplay();
        //showMessage("rightList",rightList.get(1).toString());
        //callServiceCoordinateDisplay(rightList.get(1).getImei());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Refresh last localisation.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });



        //NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        //navigationView.setNavigationItemSelectedListener(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mContext = this;
        btn_add_vehicle = (ImageView)findViewById(R.id.btn_show_form);
        btn_add_vehicle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View vw) {
                startActivityForResult(new Intent(mContext, FormAddVehicleActivity.class), 2);// Activity is started with requestCode 2

            }
        });



        rightListView.setOnTouchListener(new OnSwipeTouchListener(this, rightListView) {
            /*   @Override
               public void onSwipeRight(int pos) {
                   showDeleteEditButton(pos);
               }*/
            @Override
            public void onSwipeLeft(int pos) {
                showDeleteEditButton(pos);
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
        View child = rightListView.getChildAt(pos - rightListView.getFirstVisiblePosition());
        if (child != null) {
            delete = (ImageView) child.findViewById(R.id.delete);
            edit = (ImageView) child.findViewById(R.id.edit);
            if (delete != null) {
                if (delete.getVisibility() == View.INVISIBLE) {
                    Animation animationLeft = AnimationUtils.loadAnimation(this,R.anim.slide_out_left);
                    delete.startAnimation(animationLeft);
                    delete.setVisibility(View.VISIBLE);
                    edit.setAnimation(animationLeft);
                    edit.setVisibility(View.VISIBLE);
                    delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            callServiceVehicleDelete(rightList.get(position).getImei().toString());
                        }
                    });
                    edit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent  = new Intent(mContext,FormAddVehicleActivity.class);
                            intent.putExtra("old_imei", rightList.get(position).getImei().toString());
                            intent.putExtra("old_brand", rightList.get(position).getBrand().toString());
                            intent.putExtra("old_color", rightList.get(position).getColor().toString());
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
        View child = rightListView.getChildAt(pos - rightListView.getFirstVisiblePosition());
        if (child != null) {

            delete = (ImageView) child.findViewById(R.id.delete);
            edit = (ImageView) child.findViewById(R.id.edit);
            if (delete != null && edit != null) {
                //Animation animationRight = AnimationUtils.loadAnimation(this,R.anim.slide_in_right);
               // delete.startAnimation(animationRight);
                delete.setVisibility(View.INVISIBLE);
               // edit.startAnimation(animationRight);
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
                callServiceVehicleCreate();
            }
        }
        if(requestCode == 3 && resultCode == RESULT_OK){
            if(data.hasExtra("postParamCreateVehicle") && data.hasExtra("imei")){
                callServiceVehicleDelete(data.getExtras().getString("imei"));
                postParam = data.getExtras().getString("postParamCreateVehicle");
                callServiceVehicleCreate();
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
            drawer.openDrawer(rightListView);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
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
            rightList.clear();
            rightList = (List<Vehicle>) object;
            rightAdapter = new CustomAdapterRight(this, rightList);
            rightListView.setAdapter(rightAdapter);
            callServiceCoordinateDisplay(rightList.get(0).getImei());
        }
        if(id_srv == 3){
            //showMessage("Result", "Vehicle added.");
        }
        if(id_srv == 4){
            //showMessage("Result","Vehicle deleted.");
        }

        if(id_srv == 5){
            // Coordinate display
            leftList.clear();
            leftList = (List<Coordinate>) object;

            leftAdapter = new CustomAdapterLeft(this, leftList);
            leftListView.setAdapter(leftAdapter);
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

    public void callServiceVehicleCreate(){
        serviceGetVehicle = new ServiceGetVehicle(this, dialog, "create");
        serviceGetVehicle.refreshLocalisation(urlVehicleCreate, postParam);
        callServiceVehicleDisplay();

    }

    public void callServiceVehicleDelete(String imei){
        serviceGetVehicle = new ServiceGetVehicle(this, dialog, "delete");
        serviceGetVehicle.refreshLocalisation(urlVehicleDelete + imei, null);
        callServiceVehicleDisplay();
    }

    public void callServiceCoordinateDisplay(String imei){
        serviceGetCoordinate = new ServiceGetCoordinate(this, dialog);
        serviceGetCoordinate.refreshLocalisation(urlCooridnateDisplay+imei);
    }
}
