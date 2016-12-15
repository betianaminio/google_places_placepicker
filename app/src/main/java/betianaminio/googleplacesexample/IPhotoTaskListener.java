package betianaminio.googleplacesexample;

import android.graphics.Bitmap;

interface IPhotoTaskListener {

    void onPhotoDownloaded(Bitmap photo, CharSequence attribution );
    void onPhotoFailedToDownload(String error);
}
