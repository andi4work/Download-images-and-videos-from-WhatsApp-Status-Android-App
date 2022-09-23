package vid.img.download.ui.main.recentscreen;

import vid.img.download.data.local.FileHelper;
import vid.img.download.data.model.ImageModel;
import vid.img.download.ui.base.BasePresenter;

import java.util.List;

import javax.inject.Inject;

import rx.functions.Action1;

/**
 * Created by CMR Labs on 15/06/2018.
 */

public class RecentPicsPresenter extends BasePresenter<RecentPicsView> {

    @Inject
    public FileHelper fileHelper;

    @Inject
    public RecentPicsPresenter(FileHelper fileHelper) {
        this.fileHelper = fileHelper;
    }

    void setLoadingAnimation(boolean status) {
        getMvpView().displayLoadingAnimation(status);
    }

    void loadRecentImages() {
        List<ImageModel> mediaItems = fileHelper.getRecentImages();
        if (!mediaItems.isEmpty()) {
            getMvpView().displayRecentImages(mediaItems);
        }else{
            getMvpView().displayNoImagesInfo();
        }

        fileHelper.getMediaStateObservable().subscribe(new Action1<ImageModel>() {
            @Override
            public void call(ImageModel imageModel) {
                List<ImageModel> mediaItems = fileHelper.getRecentImages();
                if (!mediaItems.isEmpty()) {
                    getMvpView().displayRecentImages(mediaItems);
                }else{
                    getMvpView().displayNoImagesInfo();
                }
            }
        });
    }

    void saveMedia(ImageModel imageModel) {
        boolean status = fileHelper.saveMediaToLocalDir(imageModel);
        getMvpView().displayImageSavedMsg();
    }

    void loadImageViewer(ImageModel imageModel, int position) {
        getMvpView().displayImage(position, imageModel);
    }

    void deleteLocalImage(ImageModel imageModel) {
        boolean status = fileHelper.deleteImageFromLocalDir(imageModel);
        if (status) {
            getMvpView().displayDeleteSuccessMsg();
        }
    }


    public void confirmDeleteAction(ImageModel imageModel, int itemPosition) {
        getMvpView().displayDeleteConfirmPrompt(imageModel, itemPosition);
    }
}
