package vid.img.download.injection.module;

import android.app.Application;
import android.content.Context;

import vid.img.download.data.local.FileHelper;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by CMR Labs on 14/2/17.
 */

@Module
public class AppModule {

    private Application application;

    public AppModule(Application application) {
        this.application = application;
    }

    @Provides
    public Application provideApplication() {
        return application;
    }

    @Provides
    public Context provideContext() {
        return application;
    }

    @Provides
    @Singleton
    public FileHelper provideFileHelper(Context context) {
        return new FileHelper(context);
    }

}
