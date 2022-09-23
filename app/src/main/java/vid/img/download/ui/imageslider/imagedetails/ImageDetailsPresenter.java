package vid.img.download.ui.imageslider.imagedetails;

import vid.img.download.data.local.FileHelper;
import vid.img.download.data.model.ImageModel;
import vid.img.download.ui.base.BasePresenter;

import javax.inject.Inject;

/**
 * Created by CMR Labs on 14/2/17.
 */

public class ImageDetailsPresenter extends BasePresenter<ImageDetailsView> {

    private static final String TAG = ImageDetailsPresenter.class.getSimpleName();
    private final FileHelper fileHelper;

    @Inject
    public ImageDetailsPresenter(FileHelper fileHelper) {
        this.fileHelper = fileHelper;
    }

    void setLoadingAnimation(boolean status) {
        getMvpView().displayLoadingAnimation(status);
    }

    void saveMedia(ImageModel imageModel) {
        boolean status = fileHelper.saveMediaToLocalDir(imageModel);
        if (status) {
            getMvpView().displayImageSavedMsg();
        }
    }

    void deleteLocalImage(ImageModel imageModel) {
        boolean status = fileHelper.deleteImageFromLocalDir(imageModel);
        if (status) {
            getMvpView().displayDeleteSuccessMsg();
        }
    }

}
