package vid.img.download.injection.component;


import vid.img.download.data.local.FileHelper;
import vid.img.download.injection.module.AppModule;
import vid.img.download.ui.imageslider.ImageSliderActivity;
import vid.img.download.ui.imageslider.imagedetails.ImageDetailsFragment;
import vid.img.download.ui.main.MainActivity;
import vid.img.download.ui.main.recentscreen.RecentPicsFragment;
import vid.img.download.ui.main.saved.SavedPicsFragment;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by CMR Labs on 14/2/17.
 */

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {
    void inject(MainActivity activity);
    void inject(RecentPicsFragment fragment);
    void inject(SavedPicsFragment fragment);
    void inject(ImageSliderActivity activity);
    void inject(ImageDetailsFragment fragment);
    FileHelper fileHelper();
}
