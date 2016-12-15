package betianaminio.googleplacesexample;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String LOG_TAG              = "Google Places";
    private static final int    PLACE_PICKER_REQUEST = 1;

    private GoogleApiClient mGoogleApiClient = null;

    @BindView(R.id.tx_place_name)
    TextView mTXPlaceName;
    @BindView(R.id.tx_place_address)
    TextView mTXPlaceAddress;
    @BindView(R.id.tx_place_phone_number)
    TextView mTXPlacePhoneNumber;
    @BindView(R.id.img_place_photo)
    ImageView mImgPlacePhoto;
    @BindView(R.id.tx_attribution)
    TextView mTXAttribution;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        initGoogleApiClient();

    }

    @Override
    protected void onStart() {
        super.onStart();
        this.mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        this.mGoogleApiClient.disconnect();
        super.onStop();
    }

    private void initGoogleApiClient(){

        this.mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

    }

    private void initPlacePicker(){

        try {

            PlacePicker.IntentBuilder placePickerBuilder = new PlacePicker.IntentBuilder();
            startActivityForResult(placePickerBuilder.build(this), PLACE_PICKER_REQUEST);

        }catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e){

            Log.d(LOG_TAG,"Google places exception: " + e.getMessage());

        }
    }

    protected void onActivityResult(int request_code, int result_code, Intent intent){

        if ( request_code == PLACE_PICKER_REQUEST && result_code == RESULT_OK){

            Place place = PlacePicker.getPlace(this,intent);

            Resources res = getResources();

            this.mTXPlaceName.setText(res.getString(R.string.text_place_name) + ": " + place.getName());
            this.mTXPlaceAddress.setText(res.getString(R.string.text_place_address) + ": " + place.getAddress());
            this.mTXPlacePhoneNumber.setText(res.getString(R.string.text_place_phone_number) + ": " + place.getPhoneNumber());

            new PhotoTask(this.mGoogleApiClient, 480, 800, new IPhotoTaskListener() {
                @Override
                public void onPhotoDownloaded(Bitmap photo, CharSequence attribution) {

                   MainActivity.this.mImgPlacePhoto.setImageBitmap(photo);
                   MainActivity.this.mTXAttribution.setText(attribution);

                }

                @Override
                public void onPhotoFailedToDownload(String error) {

                    MainActivity.this.mImgPlacePhoto.setVisibility(View.GONE);

                }
            }).execute(place.getId());

        }

    }

    /*************************** GOOGLE API CLIENT CALLBACKS **************************/
    @Override
    public void onConnected(@Nullable Bundle bundle) {

        Log.d(LOG_TAG,"On connected");

        initPlacePicker();
    }

    @Override
    public void onConnectionSuspended(int i) {

        Log.d(LOG_TAG,"On connection suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Log.d(LOG_TAG,"On connection failed - error: " + connectionResult.getErrorMessage());
    }

    /*************************** GOOGLE API CLIENT CALLBACKS **************************/
}
