package br.gov.sp.fatec.vocecidadao.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.DisplayMetrics;
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
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
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

import br.gov.sp.fatec.vocecidadao.model.DetalheSugestao;
import br.gov.sp.fatec.vocecidadao.service.SugestaoService;
import br.gov.sp.fatec.vocecidadao.util.GeocodeJSONParser;
import fatec.sp.gov.br.vocecidadao.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap;
    private static final String TAG = "MainActivity";
    private DisplayMetrics displayMetrics;
    EditText etAddress;
    ImageButton ibSearch;
    LatLng latLng;
    ProgressDialog progDailog;
    ProgressBar progressBar;
    private final static int PHOTO = 2;
    Double latitude, longitude;
    MarkerOptions markerOptions;
    private final DetalheSugestao detalheSugestao  = new DetalheSugestao();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();

        etAddress = (EditText) findViewById(R.id.etAddress);
        ibSearch = (ImageButton) findViewById(R.id.btnSearch);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);



        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

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

                    addresses = geocoder.getFromLocation(latitude, longitude, 1);

                    String address = addresses.get(0).getAddressLine(0);
                    String city = addresses.get(0).getLocality();
                    String state = addresses.get(0).getAdminArea();
                    String country = addresses.get(0).getCountryName();
                    String postalCode = addresses.get(0).getPostalCode();
                    String knownName = addresses.get(0).getFeatureName();


                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(MapsActivity.this, "Error:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Log.i("Blah","blah3");

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
                                String comentario = input.getText().toString();
                                Intent intent = new Intent();
                                intent.setType("image/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(intent, PHOTO);
                                //Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                               // startActivityForResult(takePictureIntent, 5678);

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Uri uri = data.getData();
        ContentResolver contentResolver = this.getContentResolver();
        try{
            Bitmap bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(uri));
            if(bitmap.getWidth()>bitmap.getHeight())
                ScalePic(bitmap,displayMetrics.heightPixels);
            else
                ScalePic(bitmap,displayMetrics.widthPixels);

            //converting bitmap in Bae64 and set on the object
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream .toByteArray();
            String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
            detalheSugestao.setImagem(encoded);

            sendData();
        }catch (FileNotFoundException e){
            Log.d(TAG,e.toString());
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void ScalePic(Bitmap bitmap,int phone) {
        float mScale = 1;

        if (bitmap.getWidth() > phone) {
            mScale = (float) phone / (float) bitmap.getWidth();

            Matrix mMat = new Matrix();
            mMat.setScale(mScale, mScale);

            Bitmap mScaleBitmap = Bitmap.createBitmap(bitmap,
                    0,
                    0,
                    bitmap.getWidth(),
                    bitmap.getHeight(),
                    mMat,
                    false);


        }
    }

    public void sendData(){


        SugestaoService service = SugestaoService.retrofit.create(SugestaoService.class);
        final Call<DetalheSugestao> call =
                service.inserirSugestao(detalheSugestao);// repoContributors("square", "retrofit");

        call.enqueue(
                new Callback<DetalheSugestao>() {

                    @Override
                    public void onResponse(Call<DetalheSugestao> call, Response<DetalheSugestao> response) {
                        Log.i("Sucess ",response.body().toString());
                    }

                    @Override
                    public void onFailure(Call<DetalheSugestao> call, Throwable t) {
                        Log.i("Something went wrong: ", t.getMessage());
                    }
                }
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }


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

        detalheSugestao.setEndereco(name);
        detalheSugestao.setLatitude(latitude.toString());
        detalheSugestao.setLongitude(longitude.toString());
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
