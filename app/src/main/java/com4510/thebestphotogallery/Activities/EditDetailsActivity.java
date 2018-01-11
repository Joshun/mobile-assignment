package com4510.thebestphotogallery.Activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

import com4510.thebestphotogallery.Database.ImageMetadata;
import com4510.thebestphotogallery.Database.UpdateImageMetadataListener;
import com4510.thebestphotogallery.Listeners.ElevationResponseListener;
import com4510.thebestphotogallery.Listeners.ServerResponseListener;
import com4510.thebestphotogallery.R;
import com4510.thebestphotogallery.Tasks.ElevationTask;
import com4510.thebestphotogallery.Tasks.SendToServerTask;
import com4510.thebestphotogallery.Tasks.UpdateImageMetadataTask;
import com4510.thebestphotogallery.Util;

/**
 * Created by George on 02-Jan-18.
 */

public class EditDetailsActivity extends DetailsActivity implements ServerResponseListener, UpdateImageMetadataListener, ElevationResponseListener {

    private final int PICKER_REQUEST = 1;
    private final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2;
    private final int UPDATE_DATA = 3;

    private ImageMetadata currentImageMetadata = null;
    private Integer imageIndex = null;
    private PlaceDetectionClient placeDetectionClient;
    private boolean locationPermissionGranted = false;

    public EditDetailsActivity() {
        super("Image Options", R.id.editimagedetails_toolbar);
    }

    @Override
    public void onServerSuccess() {
        Toast.makeText(this, "Server upload successful", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onServerFailure() {
        Toast.makeText(this, "Server upload failed", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_editimagedetails);
        super.onCreate(savedInstanceState);

        placeDetectionClient = Places.getPlaceDetectionClient(this, null);

        currentImageMetadata = (ImageMetadata) getIntent().getSerializableExtra("metadata");
        imageIndex = getIntent().getExtras().getInt("position");
        setDetails();

    }

    private void setDetails() {
        TextView name = findViewById(R.id.btn_edit_name_text);
        TextView desc = findViewById(R.id.btn_edit_description_text);
        TextView geo = findViewById(R.id.btn_edit_geo_text);
        name.setText(currentImageMetadata.getTitle());
        desc.setText(currentImageMetadata.getDescription());
        if (currentImageMetadata.getLatitude() != 0.0 || currentImageMetadata.getLongitude() != 0.0) {
            String join = " " + getResources().getString(R.string.details_join) + " ";
            String s = Util.roundDP(currentImageMetadata.getLatitude(), 6) + join + Util.roundDP(currentImageMetadata.getLongitude(), 6);
            geo.setText(s);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("metadata", currentImageMetadata);
        setResult(RESULT_OK, intent);
        finish();
        super.onBackPressed();
    }

    public void btnSelected(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.btn_name:
                intent = new Intent(this, EditNameActivity.class);
                launchActivity(intent);
                break;
            case R.id.btn_description:
                intent = new Intent(this, EditDescActivity.class);
                launchActivity(intent);
                break;
            case R.id.btn_backup:
                uploadToServer();
                break;
            default:
                getLocationPermission();
        }
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Log.v("Permissions", "Already have permissions");
            locationPermissionGranted = true;
            openMap();
        } else {
            Log.v("Permissions", "Requesting permissions...");
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {

        Log.v("sdhjkfgdsf", "HEre");
        locationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                }
            }
        }

        openMap();
    }

    private void openMap() {
        try {
            if (locationPermissionGranted) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                Intent intent = builder.build(this);
//                Task<PlaceLikelihoodBufferResponse> placeResult = placeDetectionClient.getCurrentPlace(null);
//                placeResult.addOnCompleteListener(new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
//                    @Override
//                    public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
//                        if (task.isSuccessful()) {
//                            PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();
//                            float highest = 0.0f;
//                            for (PlaceLikelihood placeLikelihood : likelyPlaces) {
//                                highest = Math.max(highest, placeLikelihood.getLikelihood());
//                            }
//                            likelyPlaces.release();
//                        }
//                        else {
//                            Log.e("Edit Location", "Task failed");
//                        }
//                    }
//                });

                startActivityForResult(intent, PICKER_REQUEST);
            }
        }
        catch (SecurityException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                LatLng l = place.getLatLng();

                if (currentImageMetadata != null) {
                    currentImageMetadata.setLatitude(l.latitude);
                    currentImageMetadata.setLongitude(l.longitude);

                    //Altitude
                    ElevationTask task = new ElevationTask(l.latitude, l.longitude, getString(R.string.google_maps_key), this);
                    task.execute();

                    setDetails();

                    Log.v(getClass().getName(), "Updating metadata for image " + currentImageMetadata.getFilePath());
                    UpdateImageMetadataTask updateImageMetadataTask = new UpdateImageMetadataTask(this);
                    UpdateImageMetadataTask.UpdateMetadataParam updateMetadataParam = new UpdateImageMetadataTask.UpdateMetadataParam();
                    updateMetadataParam.activity = this;
                    updateMetadataParam.imageMetadata = currentImageMetadata;
                    updateImageMetadataTask.execute(updateMetadataParam);
                }

            }
        }
        else if (requestCode == UPDATE_DATA) {
            currentImageMetadata = (ImageMetadata) data.getSerializableExtra("metadata");
            setDetails();
        }
    }

    @Override
    public void imageUpdated(ImageMetadata imageMetadata) {
        Log.v(getClass().getName(), "Image " + imageMetadata.getFilePath() + " metadata update successful");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "Location Updated", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void launchActivity(final Intent intent) {
        intent.putExtra("position", imageIndex);
        intent.putExtra("metadata", currentImageMetadata);
        startActivityForResult(intent, UPDATE_DATA);
    }

    private void uploadToServer() {
        Log.v(getClass().getName(), "upload to server option selected");
        Toast.makeText(this, "Uploading...", Toast.LENGTH_SHORT).show();
        SendToServerTask sendToServerTask = new SendToServerTask(this);
        sendToServerTask.execute(currentImageMetadata);

    }

    @Override
    public void elevationResponse(double elevation) {
        currentImageMetadata.setAltitude(Util.round2DP(elevation));
        setDetails();
    }

}
