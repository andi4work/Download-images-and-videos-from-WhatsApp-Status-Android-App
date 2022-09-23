package vid.img.download.ui.base;

import android.support.v4.app.Fragment;

import vid.img.download.TheApplication;

public class BaseFragment extends Fragment {

    public TheApplication getTheApplication() {
        return ((TheApplication) getActivity().getApplication());
    }

}