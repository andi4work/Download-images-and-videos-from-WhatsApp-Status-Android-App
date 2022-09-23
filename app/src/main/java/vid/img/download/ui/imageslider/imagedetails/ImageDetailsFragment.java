package vid.img.download.ui.imageslider.imagedetails;

import android.animation.Animator;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.img.download.R;

import java.io.File;

import javax.inject.Inject;

import vid.img.download.data.model.ImageModel;
import vid.img.download.ui.base.BaseFragment;
import vid.img.download.ui.imageslider.ImageSliderActivity;
import vid.img.download.util.DialogFactory;

import static vid.img.download.ui.imageslider.ImageSliderActivity.IMAGES_TYPE_RECENT;
import static vid.img.download.ui.imageslider.ImageSliderActivity.IMAGES_TYPE_SAVED;

public class ImageDetailsFragment extends BaseFragment implements ImageDetailsView {
    private AdView mAdView;
    public static boolean toolbarVisible = true;
    float dX, dY;

    @Inject
    ImageDetailsPresenter presenter;
    View rootView;
    ImageView imageView;
    ImageView playImageView;
    int imageType = -1;
    private ImageModel imageModel;
    private Menu menu;
    private Toolbar toolbar;
    private ActionBar actionBar;
    private static final String TAG = "Touch";
    @SuppressWarnings("unused")
    private static final float MIN_ZOOM = 1f,MAX_ZOOM = 1f;

    // These matrices will be used to scale points of the image
    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();

    // The 3 states (events) which the user is trying to perform
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;

    // these PointF objects are used to record the point(s) the user is touching
    PointF start = new PointF();
    PointF mid = new PointF();
    float oldDist = 1f;

    /**
     * @param imageModel
     * @param imageType  Image Type available in ImageSliderActivity.java
     * @return
     */
    public static ImageDetailsFragment newInstance(ImageModel imageModel, int imageType) {

        Bundle args = new Bundle();
        args.putParcelable("imageData", imageModel);
        args.putInt("imageType", imageType);
        ImageDetailsFragment fragment = new ImageDetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        postponeEnterTransition();
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setSharedElementEnterTransition(TransitionInflater.from(getContext()).inflateTransition(android.R.transition.move));
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_image_details, container, false);
        imageView = rootView.findViewById(R.id.image_view);
        playImageView = rootView.findViewById(R.id.play_image_view);

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


            }});
        getTheApplication().getAppComponent().inject(this);

        imageView.setOnTouchListener(new ImageMatrixTouchHandler(rootView.getContext()));

        setHasOptionsMenu(true);

        actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);

        if (!toolbarVisible) {
            actionBar.hide();
        }

        presenter.attachView(this);

        // Get data
        imageModel = getArguments().getParcelable("imageData");
        imageType = getArguments().getInt("imageType");

        // Handle transition animation
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            imageView.setTransitionName(imageModel.getFileName());
        }

        // Load Image
        Glide.with(getActivity())
                .load(new File(imageModel.getCompletePath()))
                .listener(new RequestListener<File, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, File model, Target<GlideDrawable> target, boolean isFirstResource) {
                        startPostponedEnterTransition();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, File model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        startPostponedEnterTransition();
                        getActivity().supportStartPostponedEnterTransition();
                        return false;
                    }
                })
                .into(imageView);


        // Load Play button if required
        if (imageModel.isPlayableMedia()) {
            playImageView.setVisibility(View.VISIBLE);
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHideToolbar();
            }
        });

        playImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(imageModel.getCompletePath()), "video/mp4");
                startActivity(intent);
            }
        });

        return rootView;
    }


    @Override
    public void displayLoadingAnimation(boolean status) {

    }

    @Override
    public void displayImageSavedMsg() {
        Snackbar.make(rootView, "Image saved", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void displayDeleteSuccessMsg() {
        Snackbar.make(rootView, "Image removed from saved items", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.image_slider_menu, menu);
        this.menu = menu;

        MenuItem saveItem = menu.findItem(R.id.item_save_image);
        MenuItem deleteItem = menu.findItem(R.id.item_delete);

        if (imageType == IMAGES_TYPE_RECENT) {
            if (imageModel.isSavedLocally()) {
                deleteItem.setVisible(true);
            } else {
                saveItem.setVisible(true);
            }
        } else if (imageType == IMAGES_TYPE_SAVED) {
            deleteItem.setVisible(true);
        }

    }
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        final MenuItem saveItem = menu.findItem(R.id.item_save_image);
        MenuItem deleteItem = menu.findItem(R.id.item_delete);
        int id = item.getItemId();
        switch (id) {
            case R.id.item_save_image: {
                presenter.saveMedia(imageModel);
                item.setVisible(false);
                deleteItem.setVisible(true);
                break;
            }
            case R.id.item_delete: {

                String title = getString(R.string.title_delete_confirm_dialog);
                String msg = getString(R.string.msg_alert_delete_item_confirm);

                DialogFactory.createOKCancelDialog(getActivity(), title, msg, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Ok
                                presenter.deleteLocalImage(imageModel);
                                item.setVisible(false);
                                saveItem.setVisible(true);
                                if (imageType == IMAGES_TYPE_SAVED) {
                                    ((ImageSliderActivity) getActivity()).getImageDeletedSubject().onNext(imageModel);
                                }
                            }
                        },
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Cancel
                                dialog.dismiss();
                            }
                        }).show();
                break;
            }
            case R.id.item_share: {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("image/jpg");
                File file = new File(imageModel.getCompletePath());
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                startActivity(Intent.createChooser(shareIntent, "Share image using"));
                break;
            }
            case R.id.item_repost: {
                Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                whatsappIntent.setType("image/jpeg");
                whatsappIntent.setPackage("com.whatsapp");
                Uri uri = Uri.parse(imageModel.getCompletePath());
                whatsappIntent.putExtra(Intent.EXTRA_STREAM, uri);
                whatsappIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                try {
                    startActivity(whatsappIntent);
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "Please install Whatsapp to repost.", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                break;
            }
            case android.R.id.home: {
                getActivity().onBackPressed();
            }
        }

        return true;
    }

    private void showHideToolbar() {

        if (toolbarVisible) {
            // Hide toolbar
            toolbar.animate().translationY(-toolbar.getBottom()).setInterpolator(new AccelerateInterpolator())
                    .start();
        } else {
            // Show toolbar
            toolbar.setVisibility(View.VISIBLE);
            toolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).start();

        }

        // Animation listener
        toolbar.animate().setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (toolbarVisible) {
                    toolbar.setVisibility(View.GONE);
                    toolbarVisible = false;
                } else {
                    toolbarVisible = true;
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
    }

}
