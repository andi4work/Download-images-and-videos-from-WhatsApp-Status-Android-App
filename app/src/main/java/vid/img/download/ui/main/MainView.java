package vid.img.download.ui.main;


import vid.img.download.ui.base.MvpView;

/**
 * Created by CMR Labs on 14/2/17.
 */

public interface MainView extends MvpView {
    void displayWelcomeMessage(String msg);
    void displayLoadingAnimation(boolean status);
    void displayRecentAndSavedPics();
}
