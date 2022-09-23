package vid.img.download.ui.main.recentscreen;

import vid.img.download.data.model.ImageModel;
import vid.img.download.ui.base.MvpView;

import java.util.List;

/**
 * Created by CMR Labs on 15/06/2018.
 */

public interface RecentPicsView extends MvpView {
    void displayLoadingAnimation(boolean status);
    void displayRecentImages(List<ImageModel> images);
    void displayNoImagesInfo();
    void displayImageSavedMsg();
    void displayImage(int position, ImageModel imageModel);
    void displayDeleteSuccessMsg();
    void displayDeleteConfirmPrompt(ImageModel imageModel, int itemPosition);
}
