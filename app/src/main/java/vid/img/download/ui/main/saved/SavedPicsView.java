package vid.img.download.ui.main.saved;

import vid.img.download.data.model.ImageModel;
import vid.img.download.ui.base.MvpView;

import java.util.List;

/**
 * Created by CMR Labs on 15/06/2018.
 */

public interface SavedPicsView extends MvpView {
    void displayLoadingAnimation(boolean status);
    void displaySavedImages(List<ImageModel> images);
    void displayNoImagesInfo();
    void displayImage(int position, ImageModel imageModel);
    void displayDeleteSuccessMsg();
    void displayDeleteConfirm(List<ImageModel> imageModels);
}
