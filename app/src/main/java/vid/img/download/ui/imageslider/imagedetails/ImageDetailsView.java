package vid.img.download.ui.imageslider.imagedetails;


import vid.img.download.ui.base.MvpView;

/**
 * Created by CMR Labs on 14/2/17.
 */

public interface ImageDetailsView extends MvpView {
    void displayLoadingAnimation(boolean status);
    void displayImageSavedMsg();
    void displayDeleteSuccessMsg();
}
