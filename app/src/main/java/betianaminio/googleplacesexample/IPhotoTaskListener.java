package betianaminio.googleplacesexample;

import android.graphics.Bitmap;

/**
 * Created by Betiana G. Miño on 14/12/2016.
 */

public interface IPhotoTaskListener {

    void onPhotoDownloaded(Bitmap photo, CharSequence attribution );
    void onPhotoFailedToDownload(String error);
}
