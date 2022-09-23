package vid.img.download.ui.main;

import vid.img.download.ui.base.BasePresenter;

import javax.inject.Inject;

/**
 * Created by CMR Labs on 14/2/17.
 */

public class MainPresenter extends BasePresenter<MainView> {

    private static final String TAG = MainPresenter.class.getSimpleName();

    @Inject
    public MainPresenter() {
    }

    void loadWelcomeMessage() {
        getMvpView().displayWelcomeMessage("Hello world!");
    }

    void setLoadingAnimation(boolean status) {
        getMvpView().displayLoadingAnimation(status);
    }

    void loadRecentAndSavedPics() {
        getMvpView().displayRecentAndSavedPics();
    }

}
