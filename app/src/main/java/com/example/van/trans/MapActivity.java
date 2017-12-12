package com.example.van.trans;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationListener;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import Modules.DirectionFinder;
import Modules.DirectionFinderListener;
import Modules.Route;
import customfonts.MyTextView;

//,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,LocationListener
public class MapActivity extends FragmentActivity implements OnMapReadyCallback, DirectionFinderListener,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,LocationListener{
    String localhost = "http://192.168.10.104:8000/";//link server database
    static String string = "";
    URL url;
    private GoogleMap mMap;
    private Button btnFindPath;
    private EditText etOrigin;
    private EditText etDestination;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;


    //test
    SupportMapFragment mapFrag;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        final Button startBtn = (Button)findViewById(R.id.btnStart);

        final Button doneBtn = (Button)findViewById(R.id.btnDone);

        //Xử lý sự kiện click button Đăng nhập
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartTask s = new StartTask();
                s.execute();
                startBtn.setVisibility(View.GONE);

                doneBtn.setVisibility(View.VISIBLE);

            }
        });

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DoneTask d = new DoneTask();
                d.execute();
            }
        });


       /* btnFindPath = (Button) findViewById(R.id.btnFindPath);
        etOrigin = (EditText) findViewById(R.id.etOrigin);
        etDestination = (EditText) findViewById(R.id.etDestination);*/

        //sendRequest();

        /*btnFindPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequest();
            }
        });*/
    }



    private void sendRequest(Location location) {
        /*String origin = etOrigin.getText().toString();
        String destination = etDestination.getText().toString();
        if (origin.isEmpty()) {
            Toast.makeText(this, "Please enter origin address!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (destination.isEmpty()) {
            Toast.makeText(this, "Please enter destination address!", Toast.LENGTH_SHORT).show();
            return;
        }*/
        String des="";
        Bundle extras = getIntent().getExtras();
        des = extras.getString("des");
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        String origin = "10.7524667,106.6648682";
        String destination = des;
        try {
            new DirectionFinder(MapActivity.this,this, String.valueOf(latLng.latitude)+","+String.valueOf(latLng.longitude), destination).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

/*    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng hcmus = new LatLng(10.762963, 106.682394);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hcmus, 18));
        originMarkers.add(mMap.addMarker(new MarkerOptions()
                .title("Đại học Khoa học tự nhiên")
                .position(hcmus)));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
    }*/

    @Override
    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(this, "Please wait.",
                "Finding direction..!", true);

        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline:polylinePaths ) {
                polyline.remove();
            }
        }
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        for (Route route : routes) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));
            ((TextView) findViewById(R.id.tvDuration)).setText(route.duration.text);
            ((TextView) findViewById(R.id.tvDistance)).setText(route.distance.text);

            originMarkers.add(mMap.addMarker(new MarkerOptions()
                    /*.icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue))*/
                    .title(route.startAddress)
                    .position(route.startLocation)));
            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    /*.icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green))*/
                    .title(route.endAddress)
                    .position(route.endLocation)));

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.BLUE).
                    width(10);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));

            polylinePaths.add(mMap.addPolyline(polylineOptions));
        }
    }



    //test

    @Override
    public void onPause() {
        super.onPause();

        //stop location updates when Activity is no longer active
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap=googleMap;

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        }
        else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }


    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);


        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient)!=null) {
                sendRequest(LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient));
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {}

    @Override
    public void onLocationChanged(Location location)
    {
        UpdateTask u = new UpdateTask();
        u.execute();
        /*mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = mMap.addMarker(markerOptions);

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,11));*/


    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MapActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }



class StartTask extends AsyncTask<Void,Void,Void> {

    protected void onPreExecute() {
        //display progress dialog.

    }

    protected Void doInBackground(Void... params) {

        HttpURLConnection urlConnection = null;

        String driver_id="";
        String product_id="";
        Bundle extras = getIntent().getExtras();
        driver_id = extras.getString("driver_id");
        product_id = extras.getString("product_id");




        //Tạo liên kết đến file result.php trên Database lấy name và password về
        try {
            url = new URL(localhost+"change_status_android?status=start&driver_id="+driver_id+"&product_id="+product_id);


            urlConnection = (HttpURLConnection) url
                    .openConnection();

            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

            string = bufferedReader.readLine();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                urlConnection.disconnect();
            } catch (Exception e) {
                e.printStackTrace(); //If you want further info on failure...
            }
        }
        return null;
    }
    protected void onPostExecute (Void result){
        String check = "";
        String id="";
        String name="";
        try {

                /*JSONArray mang = new JSONArray(string);
                for (int i=0; i< mang.length();i++) {

                    JSONObject obj = mang.getJSONObject(i);
                    if("1".equals(obj.getString("check"))) {
                        check = obj.getString("check");
                        id = obj.getString("id");
                    }

                }*/
            JSONObject obj = new JSONObject(string);
            if("1".equals(obj.getString("check"))) {
                check = obj.getString("check");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //So sánh nếu trùng thì chuyển sang MainActivity còn không nhắc nhở
        if(check=="1"){
            Toast.makeText(MapActivity.this, "Bắt đầu trạng thái giao hàng", Toast.LENGTH_LONG).show();
        }
    }

}


class DoneTask extends AsyncTask<Void,Void,Void> {

        protected void onPreExecute() {
            //display progress dialog.

        }

        protected Void doInBackground(Void... params) {

            HttpURLConnection urlConnection = null;

            String driver_id="";
            String product_id="";
            Bundle extras = getIntent().getExtras();
            driver_id = extras.getString("driver_id");
            product_id = extras.getString("product_id");




            //Tạo liên kết đến file result.php trên Database lấy name và password về
            try {
                url = new URL(localhost+"change_status_android?status=done&driver_id="+driver_id+"&product_id="+product_id);


                urlConnection = (HttpURLConnection) url
                        .openConnection();

                BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                string = bufferedReader.readLine();

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    urlConnection.disconnect();
                } catch (Exception e) {
                    e.printStackTrace(); //If you want further info on failure...
                }
            }
            return null;
        }
        protected void onPostExecute (Void result){

            String check = "";
            String id="";
            String name="";
            String product_id="";
            Bundle extras = getIntent().getExtras();
            product_id = extras.getString("product_id");
            try {

                /*JSONArray mang = new JSONArray(string);
                for (int i=0; i< mang.length();i++) {

                    JSONObject obj = mang.getJSONObject(i);
                    if("1".equals(obj.getString("check"))) {
                        check = obj.getString("check");
                        id = obj.getString("id");
                    }

                }*/
                JSONObject obj = new JSONObject(string);
                if("1".equals(obj.getString("check"))) {
                    check = obj.getString("check");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //So sánh nếu trùng thì chuyển sang MainActivity còn không nhắc nhở
            if(check=="1"){
                Toast.makeText(MapActivity.this, "Đơn hàng hoàn thành", Toast.LENGTH_LONG).show();
                Intent returnIntent = new Intent();
                returnIntent.putExtra("product_id",product_id);
                setResult(RESULT_OK,returnIntent);
                finish();
            }
        }

    }

class UpdateTask extends AsyncTask<Void,Void,Void> {

        protected void onPreExecute() {
            //display progress dialog.

        }

        protected Void doInBackground(Void... params) {

            HttpURLConnection urlConnection = null;

            String driver_id="";
            String product_id="";
            Bundle extras = getIntent().getExtras();
            driver_id = extras.getString("driver_id");
            product_id = extras.getString("product_id");
            String lat = "";
            String lng = "";
            if (ContextCompat.checkSelfPermission(MapActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                lat=String.valueOf(LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient).getLatitude());
                lng=String.valueOf(LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient).getLongitude());
            }


            //Tạo liên kết đến file result.php trên Database lấy name và password về
            try {
                url = new URL(localhost+"update_location_android?driver_id="+driver_id+"&lat="+lat+"&lng="+lng);


                urlConnection = (HttpURLConnection) url
                        .openConnection();

                BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                string = bufferedReader.readLine();

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    urlConnection.disconnect();
                } catch (Exception e) {
                    e.printStackTrace(); //If you want further info on failure...
                }
            }
            return null;
        }
        protected void onPostExecute (Void result){

            String check = "";
            String id="";
            String name="";
            String product_id="";
            Bundle extras = getIntent().getExtras();
            product_id = extras.getString("product_id");
            try {

                /*JSONArray mang = new JSONArray(string);
                for (int i=0; i< mang.length();i++) {

                    JSONObject obj = mang.getJSONObject(i);
                    if("1".equals(obj.getString("check"))) {
                        check = obj.getString("check");
                        id = obj.getString("id");
                    }

                }*/
                JSONObject obj = new JSONObject(string);
                if("1".equals(obj.getString("check"))) {
                    check = obj.getString("check");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //So sánh nếu trùng thì chuyển sang MainActivity còn không nhắc nhở
            if(check=="1"){

            }
        }

    }

}