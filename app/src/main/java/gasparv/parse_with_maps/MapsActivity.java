package gasparv.parse_with_maps;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import java.util.ArrayList;
import java.util.List;


public class MapsActivity extends FragmentActivity implements LocationListener{

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private ProgressDialog pDialog;
    List<ParseObject> ob;
    private ArrayList Longitudes;
    private ArrayList Latitudes;
    LocationManager locationManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "5JeZkVB7d4MfG1an64w2f3oQYbtndG6ZiWU9BFWc", "aMWvc1fam1TzKQVOOXeFXFqyGLhozqJBfFnsTHZZ");
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }


    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }
    public void CargarDatos(View view){
        stopCapturing();
        Toast.makeText(getApplicationContext(), "EMPIEZA DESCARGA.", Toast.LENGTH_SHORT).show();
        new GetData().execute();
    }
    public void MandarDatos (View view){
        stopCapturing();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,0,this);
        Toast.makeText(getApplicationContext(), "SU UBICACIÓN ESTA SIENDO GUARDADA EN PARSE", Toast.LENGTH_SHORT).show();
    }

    public void stopCapturing() {

        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }

    }
    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isNetworkAvaible = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isNetworkAvaible = true;
        } else {
            Toast.makeText(this, "Network not available. Please check your connection ", Toast.LENGTH_LONG)
                    .show();
        }
        return isNetworkAvaible;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (isNetworkAvailable()){
            new SendData(location).execute();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    private class SendData extends AsyncTask<Void, Void, Void> {

        Location location;
        public SendData(Location location){
            super();
            this.location = location;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... arg0) {


            ParseObject testObject = new ParseObject("Posiciones");
            testObject.put("latitud",location.getLatitude());
            testObject.put("longitud",location.getLongitude());
            testObject.saveInBackground();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
        }

    }

    // RemoteDataTask AsyncTask
    private class GetData extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            pDialog = new ProgressDialog(MapsActivity.this);
            // Set progressdialog title
            pDialog.setTitle("Cargando datos de Parse");
            // Set progressdialog message
            pDialog.setMessage("Loading...");
            pDialog.setIndeterminate(false);
            // Show progressdialog
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            // Create the array
            Latitudes = new ArrayList<String>();
            Longitudes = new ArrayList<String>();
            try {
                ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
                        "Posiciones");

                ob = query.find();
                for (ParseObject dato : ob) {
                    Latitudes.add(dato.get("latitud"));
                    Longitudes.add(dato.get("longitud"));
                    Log.i("Latitud: ", dato.get("latitud").toString());
                    Log.i("Longitud: ", dato.get("longitud").toString());
                }
            } catch (ParseException e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(MapsActivity.this,
                    android.R.layout.simple_list_item_1, android.R.id.text1, Latitudes);
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            PolylineOptions rectOptions = new PolylineOptions();
            for(int i=0;i<Longitudes.size();i++)
            {
                rectOptions.add(new LatLng(Double.parseDouble(Latitudes.get(i).toString()),Double.parseDouble(Longitudes.get(i).toString())));
            }
            Polyline polyline = mMap.addPolyline(rectOptions);
            rectOptions.width(2);
            rectOptions.color(Color.BLUE);
            CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(Double.parseDouble(Latitudes.get(0).toString()),Double.parseDouble(Longitudes.get(0).toString()))).zoom(13).build();
            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
            mMap.moveCamera(cameraUpdate);
            pDialog.dismiss();
        }
    }
}
