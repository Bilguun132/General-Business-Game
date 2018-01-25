package fyp.generalbusinessgame.Activity;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import fyp.generalbusinessgame.Models.GameInformationModel;
import fyp.generalbusinessgame.R;

import fyp.generalbusinessgame.Service.DownloadCallback;
import fyp.generalbusinessgame.Service.HttpRequestModel;
import fyp.generalbusinessgame.Service.HttpResult;
import fyp.generalbusinessgame.Service.HttpServiceFragment;
import fyp.generalbusinessgame.Service.ImageHolder;

public class GameViewActivity extends AppCompatActivity implements DownloadCallback<HttpResult> {

    public static final String EXTRA_POSITION = "position";
    Bitmap bitmap;
    GameInformationModel gameInformationModel = new GameInformationModel();
    private int mDownloadTarget;
    private HttpServiceFragment mHttpFragment = null;
    private boolean mDownloading = false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_view);
        mHttpFragment = HttpServiceFragment.getInstance(getSupportFragmentManager());

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Set Collapsing Toolbar layout to the screen
        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        // Set title of Detail page
        // collapsingToolbar.setTitle(getString(R.string.item_title));

        gameInformationModel = (GameInformationModel) getIntent().getSerializableExtra("gameInformation");
        Resources resources = getResources();
        String[] games = resources.getStringArray(R.array.games);
        collapsingToolbar.setTitle(gameInformationModel.gameName);

//        String[] placeDetails = resources.getStringArray(R.array.games_desc);
//        TextView placeDetail = (TextView) findViewById(R.id.game_description);
//        placeDetail.setText(gameInformationModel.gameDescription);

        ImageView gamePicture = (ImageView) findViewById(R.id.image);
        Bitmap decodedImage = ImageHolder.getBitmapFromMemCache(gameInformationModel.id);
        gamePicture.setImageBitmap(decodedImage);

        ImageButton imageButton = (ImageButton) findViewById(R.id.join_game_button);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpRequestModel httpRequest = new HttpRequestModel();
                SharedPreferences sharedPref =
                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                int userId =  sharedPref.getInt(getString(R.string.user_id), 0);
                httpRequest.urlPath = "api/account/" + userId + "/games/" + gameInformationModel.id +"/join";
                httpRequest.method = "GET";
                mHttpFragment.setHttpRequest(httpRequest);
                startDownload(1);
                NestedScrollView nestedScrollView = (NestedScrollView) findViewById(R.id.nested_scroll_view);
                nestedScrollView.setVisibility(View.GONE);
                ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
                progressBar.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void showSnackBar(String message) {
        Snackbar.make(findViewById(R.id.appbar), message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                default:
                    return true;
        }
    }

    @Override
    public void updateFromDownload(HttpResult result) {
        if (result == null) {
            Toast.makeText(getApplicationContext(), "No internet", Toast.LENGTH_LONG).show();
        } else {
            if (result.exception != null) {
                Toast.makeText(getApplicationContext(), result.exception.getMessage(), Toast.LENGTH_LONG).show();
            } else {
                if (mDownloadTarget == 1) {
                    switch (result.responseCode) {
                        case 200:
                            super.onBackPressed();
                            break;
                        default:
                            Toast.makeText(getApplicationContext(), result.responseBody, Toast.LENGTH_LONG).show();
                            break;
                    }
                }
            }
        }
    }

    @Override
    public void finishDownloading() {
        mDownloading = false;

        if (mHttpFragment != null) {
            mHttpFragment.cancelDownload();
        }
    }

    @Override
    public void finishInitializing() {

    }

    private void startDownload(int target) {
        if (!mDownloading && mHttpFragment != null) {
            // Execute the async download.
            mDownloadTarget = target;
            mHttpFragment.startDownload();
            mDownloading = true;
        }
    }
}
