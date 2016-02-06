package fr.mysafeauto.mysafe;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.Image;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
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


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    private GoogleMap mMap;

    List<Vehicle> mRightItems = new ArrayList<Vehicle>();;
   // ArrayAdapter mRightAdapter;
    ListView mRightDrawer;
    DrawerLayout drawer;
    CustomAdapter adapter;

    ImageView btn_add_vehicle;
    Context mContext;
    Vehicle mvehicle;

    ActionBarDrawerToggle toggle;

    ImageView delete;
    ImageView edit;

    int position = 0;
    SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = openOrCreateDatabase("MysafeAppDB", Context.MODE_PRIVATE, null);

        mRightDrawer = (ListView)findViewById(R.id.rightListView);
        //Custom Adapter
        adapter = new CustomAdapter(this, mRightItems);
        mRightDrawer.setAdapter(adapter);
        //mRightAdapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, mRightItems);
        //mRightDrawer.setAdapter(mRightAdapter);

        afficherVehicles();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Code to implement to show the details about the app", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mContext = this;
        btn_add_vehicle = (ImageView)findViewById(R.id.btn_show_form);
        btn_add_vehicle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View vw) {
                startActivityForResult(new Intent(mContext, AddVehicleActivity.class), 2);// Activity is started with requestCode 2
                // startActivity(new Intent(mContext, AddVehicleActivity.class));
            }
        });



        //viewDBApp();
       mRightDrawer.setOnTouchListener(new OnSwipeTouchListener(this, mRightDrawer) {

         //   @Override
     /*       public void onSwipeRight(int pos) {

                Toast.makeText(MainActivity.this, "right", Toast.LENGTH_LONG).show();
                showMessage("Error", "No records found but table exist");
               // showDeleteButton(pos);
            }*/

            @Override
            public void onSwipeLeft(int pos) {

                showDeleteButton(pos);
            }
        });
        adapter.notifyDataSetChanged();


    }



    public void deleteItem(int pos) {
        Cursor c=db.rawQuery("SELECT * FROM vehicles WHERE imei='"+mRightItems.get(position).getImei().toString()+"'", null);
        if(c.moveToFirst())
        {
            // Deleting record if found
            db.execSQL("DELETE FROM vehicles WHERE imei='" +mRightItems.get(position).getImei().toString()+"'");
        }
        delete.setVisibility(View.INVISIBLE);
        afficherVehicles();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        return super.dispatchTouchEvent(ev);
    }

    private boolean showDeleteButton(int pos) {
        position = pos;
        View child = mRightDrawer.getChildAt(pos - mRightDrawer.getFirstVisiblePosition());
        if (child != null) {

            delete = (ImageView) child.findViewById(R.id.delete);
            edit = (ImageView) child.findViewById(R.id.edit);
            if (delete != null) {
                if (delete.getVisibility() == View.INVISIBLE) {
                    Animation animationLeft =
                            AnimationUtils.loadAnimation(this,
                                    R.anim.slide_out_left);
                    delete.startAnimation(animationLeft);
                    delete.setVisibility(View.VISIBLE);
                    edit.setAnimation(animationLeft);
                    edit.setVisibility(View.VISIBLE);
                    delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            deleteItem(position - 1);
                        }
                    });
                    edit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivityForResult(new Intent(mContext, AddVehicleActivity.class), 3);// Activity is started with requestCode 2
                        }
                    });

                } else {
                    Animation animationRight =
                            AnimationUtils.loadAnimation(this,
                                    R.anim.slide_in_right);
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


    // Call Back method  to get the Message form other Activity    override the method
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            if(resultCode == RESULT_OK){
                afficherVehicles();
            }
        }
        if(requestCode == 3){
            if(resultCode == RESULT_OK){
                afficherVehicles();
                showMessage("Update","Code to implement if any changes !");
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
            drawer.openDrawer(mRightDrawer);
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


    public void afficherVehicles(){

        Cursor c=db.rawQuery("SELECT * FROM vehicles", null);
        // Checking if no records found
        if(c.getCount()==0)
        {
            mRightItems.clear();
            //showMessage("Error", "No records found but table exist");
            adapter.notifyDataSetChanged();
            return;
        }
        mRightItems.clear();
        // Appending records to a string buffer
        while(c.moveToNext())
        {
            mvehicle = new Vehicle();
            mvehicle.setImei(c.getString(0));
            mvehicle.setBrand(c.getString(1));
            mvehicle.setColor(c.getString(2));

            mRightItems.add(mvehicle);
        }
        // Displaying all records
   //     mRightAdapter = new ArrayAdapter<Vehicle>(this, R.layout.support_simple_spinner_dropdown_item, mRightItems);
     //   mRightDrawer.setAdapter(mRightAdapter);
        //viewDBApp();
        adapter.notifyDataSetChanged();
    }

    public void showMessage(String title,String message)
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }


    public void viewDBApp(){
        // Retrieving all records
        Cursor c=db.rawQuery("SELECT * FROM vehicles", null);
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
            buffer.append("ID" + c.getPosition()+"\n");
            buffer.append("IMEI: "+c.getString(0)+"\n");
            buffer.append("Brand: "+c.getString(1)+"\n");
            buffer.append("Color: "+c.getString(2)+"\n\n");
        }
        // Displaying all records
        showMessage("Vehicles Table", buffer.toString());

    }
}
