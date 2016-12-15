package betianaminio.googleplacesexample;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.Places;


class PhotoTask extends AsyncTask<String,Void,PhotoTask.AttributedPhoto> {

    private int mWidth;
    private int mHeight;
    private GoogleApiClient mGoogleApiClient;
    private IPhotoTaskListener mListener;

    PhotoTask( GoogleApiClient googleApiClient,int width, int height, IPhotoTaskListener listener ){

        this.mGoogleApiClient = googleApiClient;
        this.mWidth = width;
        this.mHeight = height;
        this.mListener = listener;
    }


    @Override
    protected PhotoTask.AttributedPhoto doInBackground(String... voids) {

        if ( voids.length != 1)
            return null;

        final String placeId = voids[0];

        AttributedPhoto attributedPhoto = null;

        PlacePhotoMetadataResult placePhotoMetadataResult = Places.GeoDataApi.getPlacePhotos(this.mGoogleApiClient,placeId).await();

        if ( placePhotoMetadataResult.getStatus().isSuccess()){

            PlacePhotoMetadataBuffer placePhotoMetadataBuffer = placePhotoMetadataResult.getPhotoMetadata();

            if ( placePhotoMetadataBuffer.getCount() > 0 && !isCancelled()){

                PlacePhotoMetadata photo =  placePhotoMetadataBuffer.get(0);
                CharSequence attributions = photo.getAttributions();

                Bitmap image = photo.getScaledPhoto(this.mGoogleApiClient,this.mWidth,this.mHeight).await().getBitmap();

                attributedPhoto = new AttributedPhoto( attributions,image);

            }
            //IMPORTANT!!!!!!!!!!!!!!!!!!!!
            placePhotoMetadataBuffer.release();
        }

        return attributedPhoto;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(PhotoTask.AttributedPhoto attributedPhoto) {
        super.onPostExecute(attributedPhoto);


        if ( attributedPhoto != null )
            this.mListener.onPhotoDownloaded( attributedPhoto.bitmap, attributedPhoto.attribution );
        else {
            this.mListener.onPhotoFailedToDownload("no picture available");

        }
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    /**
     * Holder for an image and its attribution.
     */
    class AttributedPhoto {

        public final CharSequence attribution;

        public final Bitmap bitmap;

        public AttributedPhoto(CharSequence attribution, Bitmap bitmap) {
            this.attribution = attribution;
            this.bitmap = bitmap;
        }
    }


}
