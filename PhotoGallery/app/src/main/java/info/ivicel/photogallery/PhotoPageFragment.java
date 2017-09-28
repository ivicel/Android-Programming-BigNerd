package info.ivicel.photogallery;

import android.net.Uri;
import android.os.Bundle;
import android.os.Debug;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import static info.ivicel.photogallery.BuildConfig.DEBUG;

/**
 * Created by Ivicel on 29/09/2017.
 */

public class PhotoPageFragment extends Fragment {
    private static final String TAG = "PhotoPageFragment";
    private static final String ARG_URI = "photo_page_url";
    
    private Uri mUri;
    private WebView mWebView;
    private ProgressBar mProgressBar;
    
    public static PhotoPageFragment newInstance(Uri uri) {
        Bundle args = new Bundle();
        args.putParcelable(ARG_URI, uri);
        PhotoPageFragment fragment = new PhotoPageFragment();
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    
        mUri = getArguments().getParcelable(ARG_URI);
    }
    
    @SuppressWarnings("SetJavaScriptEnabled")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_photo_page, container, false);
        mWebView = (WebView)v.findViewById(R.id.web_view);
        mProgressBar = (ProgressBar)v.findViewById(R.id.progress_bar);
        
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient());
        mWebView.loadUrl(mUri.toString());
        
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                AppCompatActivity activity = (AppCompatActivity)getActivity();
                try {
                    activity.getSupportActionBar().setTitle(title);
                } catch (NullPointerException npe) {
                    if (DEBUG) {
                        Log.e(TAG, "No action bar or tool bar", npe);
                    }
                }
            }
    
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    mProgressBar.setVisibility(View.GONE);
                } else {
                    mProgressBar.setProgress(newProgress);
                    mProgressBar.setVisibility(View.VISIBLE);
                }
                
                
            }
        });
        
        return v;
    }
}
