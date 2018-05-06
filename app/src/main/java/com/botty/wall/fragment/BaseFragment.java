package com.botty.wall.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.botty.wall.R;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;

import java.io.File;

abstract class BaseFragment extends Fragment {

    protected Snackbar mySnackbar;
    protected ProgressDialog mProgressDialog;
    protected View rootView,bottomSheet;

    protected BottomSheetDialog mBottomSheetDialog;

    //Download Image via Ion
    protected Future<File> downloading;
    protected boolean downloaded = false;
    protected String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/WallApp";

    protected Toolbar toolbar;
    protected RecyclerView recyclerView;
    protected BottomNavigationView bottomNavigationView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return null;
    }

    /*
     * UI and Method for download wallpaper With Ion
     */
    protected void IonDownloadMethod(String mURL,int position){

        String URL = mURL;
        if (downloading != null && !downloading.isCancelled()) {
            resetDownload();
            return;
        }

        downloading = Ion.with(this)
                .load(URL)
                // have a ProgressBar get updated automatically with the percent
                // and a ProgressDialog
                .progressDialog(Progress())
                .progress(new ProgressCallback() {
                    @Override
                    public void onProgress(long downloaded, long total) {
                        System.out.println("" + downloaded + " / " + total);
                    }
                })
                .write(new File(fullPath, "wall_"+position+".jpg"))
                .setCallback(new FutureCallback<File>() {
                    @Override
                    public void onCompleted(Exception e, File file) {
                        // download done...
                        // do stuff with the File or error
                        mProgressDialog.dismiss();
                        setMySnackbar(R.string.toast_info_downloaded);
                    }
                });
        return;

    }

    public ProgressDialog Progress(){
        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setMessage("Downloading ...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        return mProgressDialog;
    }

    void resetDownload() {
        // cancel any pending download
        downloading.cancel();
        downloading = null;
    }

    // end  for UI Ion Method

    public void setMySnackbar(int message) {
        mySnackbar = Snackbar.make(rootView.findViewById(R.id.root_view),
                getString(message), Snackbar.LENGTH_SHORT);
        TextView mainTextView = (mySnackbar.getView()).findViewById(android.support.design.R.id.snackbar_text);
        TextView actionTextView = (mySnackbar.getView()).findViewById(android.support.design.R.id.snackbar_action);
        // To Apply Custom Fonts for Message and Action
        Typeface robotomono_regular = ResourcesCompat.getFont(getContext(), R.font.robotomono_regular);
        Typeface robotomono_bold = ResourcesCompat.getFont(getContext(), R.font.robotomono_bold);
        mainTextView.setTypeface(robotomono_regular);
        actionTextView.setTypeface(robotomono_bold);
        mySnackbar.show();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
