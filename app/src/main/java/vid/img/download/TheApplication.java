package vid.img.download;

import android.app.Application;

import com.google.firebase.analytics.FirebaseAnalytics;

import vid.img.download.injection.component.AppComponent;
import vid.img.download.injection.component.DaggerAppComponent;
import vid.img.download.injection.module.AppModule;


public class TheApplication extends Application {
    AppComponent appComponent;
    private FirebaseAnalytics mFirebaseAnalytics;
    @Override
    public void onCreate() {
        super.onCreate();
        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

 }

    public AppComponent getAppComponent() {
        return appComponent;
    }
}