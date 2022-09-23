package vid.img.download.ui.main.recentscreen;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.img.download.R;

import vid.img.download.data.model.ImageModel;
import vid.img.download.ui.base.BaseFragment;
import vid.img.download.ui.imageslider.ImageSliderActivity;
import vid.img.download.util.DialogFactory;

import java.util.List;

import javax.inject.Inject;

import rx.functions.Action1;

/**
 * Created by CMR Labs on 15/06/2018.
 */

public class RecentPicsFragment extends BaseFragment implements RecentPicsView {
    private AdView mAdView;

    private static final String TAG = RecentPicsFragment.class.getSimpleName();
    View rootView;
    @Inject
    RecentPicsPresenter presenter;
    @Inject
    RecentImageListAdapter adapter;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    SwipeRefreshLayout swipeRefreshLayout;
    TextView noRecentImagesMsgTextView;
    private GridLayoutManager layoutManager;

    public static RecentPicsFragment newInstance() {

        Bundle args = new Bundle();

        RecentPicsFragment fragment = new RecentPicsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getTheApplication().getAppComponent().inject(this);
        rootView = inflater.inflate(R.layout.fragment_recent_pics, container, false);
        recyclerView = rootView.findViewById(R.id.images_recycler_view);
        progressBar = rootView.findViewById(R.id.progress_bar);
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        noRecentImagesMsgTextView = rootView.findViewById(R.id.msg_no_media_text_view);

        mAdView = rootView.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        // set the ad unit ID
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                Log.d("TAG", "ad loaded.");

            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
                Log.d("TAG", "ad wasn't loaded yet.");


            }
        });
        presenter.attachView(this);

        presenter.setLoadingAnimation(true);

        // Setup recycler view
        layoutManager = new GridLayoutManager(getActivity(), 3);
        recyclerView.setLayoutManager(layoutManager);

        adapter.getOnItemClicks().subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer position) {
//                presenter.saveMedia(adapter.getItemAtPosition(position));
                presenter.loadImageViewer(adapter.getItemAtPosition(position), position);
            }
        });
        adapter.getOnSaveItemClicks().subscribe(new Action1<Integer>() {
            @Override
            public void call(final Integer position) {
                // Get the save icon of current item
                final View view = layoutManager.findViewByPosition(position);
                adapter.showSaveProgress(view);

                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        presenter.saveMedia(adapter.getItemAtPosition(position));
                        adapter.showDeleteButton(view);
                    }
                }, 500);

            }
        });
        adapter.getOnDeleteItemClicks().subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer position) {
                presenter.confirmDeleteAction(adapter.getItemAtPosition(position), position);
            }
        });

        recyclerView.setAdapter(adapter);

        presenter.setLoadingAnimation(true);

        presenter.loadRecentImages();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.loadRecentImages();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        return rootView;
    }

    @Override
    public void displayLoadingAnimation(boolean status) {
        if (status) {
            recyclerView.setVisibility(View.GONE);
            noRecentImagesMsgTextView.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void displayRecentImages(List<ImageModel> images) {
        recyclerView.setVisibility(View.VISIBLE);
        presenter.setLoadingAnimation(false);
        noRecentImagesMsgTextView.setVisibility(View.GONE);
        adapter.setItems(images);
    }

    @Override
    public void displayNoImagesInfo() {
        presenter.setLoadingAnimation(false);
        noRecentImagesMsgTextView.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    @Override
    public void displayImageSavedMsg() {
        Snackbar.make(rootView, "Image saved", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void displayImage(int position, ImageModel imageModel) {

        // Get ImageView at current position, to apply transition effect
        View view = layoutManager.findViewByPosition(position);
        ImageView imageView = (ImageView) view.findViewById(R.id.image_thumbnail);

        Intent intent = new Intent(getActivity(), ImageSliderActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(ImageSliderActivity.INTENT_IMAGE_TYPE, ImageSliderActivity.IMAGES_TYPE_RECENT);
        bundle.putParcelable(ImageSliderActivity.INTENT_IMAGE_DATA, imageModel);
        intent.putExtras(bundle);
        intent.putExtra(ImageSliderActivity.EXTRA_IMAGE_TRANSITION_NAME, ViewCompat.getTransitionName(imageView));

        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                getActivity(),
                imageView,
                ViewCompat.getTransitionName(imageView));

        startActivity(intent, options.toBundle());
    }

    @Override
    public void displayDeleteSuccessMsg() {
        Snackbar.make(rootView, "Image removed from saved items", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void displayDeleteConfirmPrompt(final ImageModel imageModel, final int itemPosition) {

        String title = getString(R.string.title_delete_confirm_dialog);
        String msg = getString(R.string.msg_alert_delete_item_confirm);

        DialogFactory.createOKCancelDialog(getActivity(), title, msg, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Ok
                        adapter.showSaveButton(layoutManager.findViewByPosition(itemPosition));
                        presenter.deleteLocalImage(imageModel);
                    }
                },
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Cancel
                        dialog.dismiss();
                    }
                }).show();
    }
}
