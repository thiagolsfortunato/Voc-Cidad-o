package fatec.sp.gov.br.vocecidadao;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    EditText etAddress;
    ImageButton ibSearch;
    LatLng latLng;
    ProgressDialog progDailog;
    ProgressBar progressBar;

    Double latitude, longitude;
    MarkerOptions markerOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();

        etAddress = (EditText) findViewById(R.id.etAddress);
        ibSearch = (ImageButton) findViewById(R.id.btnSearch);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        ibSearch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                String location = etAddress.getText().toString();

                if (location == null || location.equals("")) {
                    Toast.makeText(MapsActivity.this, "No place found",
                            Toast.LENGTH_SHORT).show();
                    setProgress(false);
                    return;
                }
                locationOnMap(location);

            }
        });

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            public void onMapLongClick(LatLng latLng) {
                mMap.clear();
                latitude = latLng.latitude;
                longitude = latLng.longitude;

                mMap.animateCamera(CameraUpdateFactory
                        .newLatLng(latLng));
                markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                mMap.addMarker(markerOptions);

                //---------------
                try {
                    Geocoder geocoder;
                    List<Address> addresses;
                    geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());

                    addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                    String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    String city = addresses.get(0).getLocality();
                    String state = addresses.get(0).getAdminArea();
                    String country = addresses.get(0).getCountryName();
                    String postalCode = addresses.get(0).getPostalCode();
                    String knownName = addresses.get(0).getFeatureName();

                    etAddress.setText(address + "\n" + city + "\n" + state + "\n" + country +
                            "\n" + postalCode + "\n" + knownName);

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(MapsActivity.this, "Error:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Log.i("Blah","blah2");

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MapsActivity.this);
                alertDialog.setTitle("Nova Sugestão");

                final EditText input = new EditText(MapsActivity.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                alertDialog.setView(input);
                //alertDialog.setIcon(R.drawable.key);

                alertDialog.setPositiveButton("Enviar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent galleryIntent = new Intent(Intent.ACTION_VIEW, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(Intent.createChooser(galleryIntent, "Select Picture"), Bundle.CONTENTS_FILE_DESCRIPTOR);

                                String comentario = input.getText().toString();
                            }
                        });

                alertDialog.setNegativeButton("Cancelar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                alertDialog.show();
                return false;
            }
        });
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
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
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

    private void locationOnMap(String location) {
        String url = "https://maps.googleapis.com/maps/api/geocode/json?";
        setProgress(true);
        try {
            // encoding special characters like space in the user input
            // place
            location = URLEncoder.encode(location, "utf-8");
        } catch (UnsupportedEncodingException e) {
            setProgress(false);
            e.printStackTrace();
        }

        String address = "address=" + location;

        String sensor = "sensor=false";

        // url , from where the geocoding data is fetched
        url = url + address + "&" + sensor;

        // Instantiating DownloadTask to get places from Google
        // Geocoding service
        // in a non-ui thread
        DownloadTask downloadTask = new DownloadTask();

        // Start downloading the geocoding places
        downloadTask.execute(url);
    }

    private class DownloadTask extends AsyncTask<String, Integer, String> {

        String data = null;

        // Invoked by execute() method of this object
        @Override
        protected String doInBackground(String... url) {
            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                setProgress(false);
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(String result) {

            // Instantiating ParserTask which parses the json data from
            // Geocoding webservice
            // in a non-ui thread
            ParserTask parserTask = new ParserTask();

            // Start parsing the places in JSON format
            // Invokes the "doInBackground()" method of the class ParseTask
            parserTask.execute(result);
        }
    }

    @SuppressLint("LongLogTag")
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            br.close();

        } catch (Exception e) {
            Log.d("Exception while downloading url", e.toString());
        } finally {
            setProgress(false);
            iStream.close();
            urlConnection.disconnect();
        }

        return data;
    }

    // ** A class to parse the Geocoding Places in non-ui thread *//*
    class ParserTask extends
            AsyncTask<String, Integer, List<HashMap<String, String>>> {

        JSONObject jObject;

        // Invoked by execute() method of this object
        @Override
        protected List<HashMap<String, String>> doInBackground(
                String... jsonData) {

            List<HashMap<String, String>> places = null;
            GeocodeJSONParser parser = new GeocodeJSONParser();

            try {
                jObject = new JSONObject(jsonData[0]);

                // ** Getting the parsed data as a an ArrayList *//*
                places = parser.parse(jObject);

            } catch (Exception e) {
                setProgress(false);
                Log.d("Exception", e.toString());
            }
            return places;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(List<HashMap<String, String>> list) {

            // Clears all the existing markers
            mMap.clear();
            setProgress(false);
            for (int i = 0; i < list.size(); i++) {
                if (i == 0) {
                    HashMap<String, String> hmPlace = list.get(i);
                    double lat = Double.parseDouble(hmPlace.get("lat"));
                    double lng = Double.parseDouble(hmPlace.get("lng"));
                    String name = hmPlace.get("formatted_address");
                    plotMarker(lat, lng, name);
                    break;
                }
            }
        }
    }

    private void plotMarker(double lati, double longi, String name) {
        MarkerOptions markerOptions = new MarkerOptions();
        LatLng latLng = new LatLng(lati, longi);
        markerOptions.position(latLng);
        markerOptions.title(name);
        mMap.addMarker(markerOptions);
        // googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12f));
        latitude = lati;
        longitude = longi;
    }

    private void setProgress(final boolean bol) {

        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (bol) {
                    ibSearch.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    etAddress.setEnabled(false);
                } else {
                    ibSearch.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    etAddress.setEnabled(true);
                }
            }
        });
    }
}
