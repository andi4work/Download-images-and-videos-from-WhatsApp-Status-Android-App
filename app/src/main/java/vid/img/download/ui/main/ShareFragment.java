package vid.img.download.ui.main;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codemybrainsout.ratingdialog.RatingDialog;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.img.download.R;

import vid.img.download.ui.base.BaseFragment;

/**
 * Created by CMR Labs on 15/06/2018.
 */

public class ShareFragment extends BaseFragment {
    View rootView;
    private String packageName;
    private AdView mAdView;
    private TextView bRateUs, bShare, bUpdate;
    String versionName;
    TextView tvVersion;

    public static ShareFragment newInstance() {
        Bundle args = new Bundle();
        ShareFragment fragment = new ShareFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_share, container, false);
        packageName = getActivity().getPackageName();
        mAdView = rootView.findViewById(R.id.adView);
        bRateUs = (TextView) rootView.findViewById(R.id.bRateUs);
        bUpdate = (TextView) rootView.findViewById(R.id.bUpdate);
        tvVersion = (TextView) rootView.findViewById(R.id.tvVersion);
        try {
            PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            versionName = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        tvVersion.setText("current version " + versionName);

        bRateUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
        bShare = (TextView) rootView.findViewById(R.id.bShare);
        bShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_SUBJECT, "WhatsApp Status Downloader");
                    String sAux = "\nLet me recommend you this application ⬇⬇⬇\n\n";
                    sAux = sAux + "https://play.google.com/store/apps/details?id=" + getActivity().getPackageName() + "\n\n";
                    i.putExtra(Intent.EXTRA_TEXT, sAux);
                    startActivity(Intent.createChooser(i, "choose one"));
                } catch (Exception e) {
                    //e.toString();
                }
            }
        });
        bUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getActivity().getPackageName()));
                    startActivity(intent);
                } finally {
                    Toast.makeText(getActivity(), "Update app securly in Google play store", Toast.LENGTH_LONG).show();
                }
            }
        });
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
        return rootView;
    }

    private void showDialog() {

        final RatingDialog ratingDialog = new RatingDialog.Builder(getActivity())
                .threshold(5)
                .ratingBarColor(R.color.colorPrimaryTranslucent)
                .playstoreUrl("https://play.google.com/store/apps/details?id=" + packageName)
                .onThresholdFailed(new RatingDialog.Builder.RatingThresholdFailedListener() {
                    @Override
                    public void onThresholdFailed(RatingDialog ratingDialog, float rating, boolean thresholdCleared) {
                        Toast.makeText(getActivity(), "Thank you for rating the App...!", Toast.LENGTH_LONG).show();
                        ratingDialog.cancel();
                    }
                })
                /*.onRatingBarFormSumbit(new RatingDialog.Builder.RatingDialogFormListener() {
                    @Override
                    public void onFormSubmitted(String feedback) {
                        Log.i("", "Feedback:" + feedback);
                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                "mailto","singapore.ddy@gmail.com", null));
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "FEEDBACK: Status downloader for WhatsApp");
                        emailIntent.putExtra(Intent.EXTRA_TEXT, feedback);
                        startActivity(Intent.createChooser(emailIntent, "Send email..."));
                    }
                })*/
                .build();


        ratingDialog.show();
    }
}
