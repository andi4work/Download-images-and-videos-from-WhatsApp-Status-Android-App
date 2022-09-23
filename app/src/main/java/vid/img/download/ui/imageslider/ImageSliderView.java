package vid.img.download.ui.imageslider;


import vid.img.download.data.model.ImageModel;
import vid.img.download.ui.base.MvpView;

import java.util.List;

/**
 * Created by CMR Labs on 14/2/17.
 */

public interface ImageSliderView extends MvpView {
    void displayLoadingAnimation(boolean status);
    void displayImageSlider(List<ImageModel> mediaItems, int imageToDisplayPosition);
}
