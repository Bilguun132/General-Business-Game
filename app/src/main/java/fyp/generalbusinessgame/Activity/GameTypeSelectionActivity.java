package fyp.generalbusinessgame.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.design.widget.NavigationView;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.util.LruCache;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import fyp.generalbusinessgame.Models.GameTypeInformationListModel;
import fyp.generalbusinessgame.Models.GameTypeInformationModel;
import fyp.generalbusinessgame.R;
import fyp.generalbusinessgame.Service.DownloadCallback;
import fyp.generalbusinessgame.Service.HttpRequestModel;
import fyp.generalbusinessgame.Service.HttpResult;
import fyp.generalbusinessgame.Service.HttpServiceFragment;
import fyp.generalbusinessgame.Service.ImageHolder;

public class GameTypeSelectionActivity extends AppCompatActivity implements DownloadCallback<HttpResult>{

    private String userName = "admin";
    private DrawerLayout mDrawerLayout;
    private int mDownloadTarget;
    private HttpServiceFragment mHttpFragment = null;
    private boolean mDownloading = false;
    private GameTypeFragment gameTypeFragment = new GameTypeFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_type_selection);
        // Get max available VM memory, exceeding this amount will throw an
// OutOfMemory exception. Stored in kilobytes as LruCache takes an
// int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        ImageHolder.mMemoryCache = new LruCache<Integer, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(Integer key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };

        userName = getIntent().getStringExtra("userName");
        mHttpFragment = HttpServiceFragment.getInstance(getSupportFragmentManager());

        // Adding Toolbar to Main screen
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Setting ViewPager for each Tabs
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        // Create Navigation drawer and inlfate layout
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        // Adding menu icon to Toolbar
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            VectorDrawableCompat indicator
                    = VectorDrawableCompat.create(getResources(), R.drawable.ic_menu, getTheme());
            indicator.setTint(ResourcesCompat.getColor(getResources(),R.color.white,getTheme()));
            supportActionBar.setHomeAsUpIndicator(indicator);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
        // Set behavior of Navigation drawer
        EditText userNameEditText = (EditText) navigationView.getHeaderView(0).findViewById(R.id.navheader_username);
        userNameEditText.setText(userName);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    // This method will trigger on item Click of navigation menu
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // Set item in checked state
                        menuItem.setChecked(true);

                        int id = menuItem.getItemId();
                        //noinspection SimplifiableIfStatement
                        switch (id) {
                            case R.id.logout:
                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            default:
                                mDrawerLayout.closeDrawers();
                                return true;
                        }

                        // Closing drawer on item click
                    }
                });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {

    }

    // Add Fragments to Tabs
    private void setupViewPager(ViewPager viewPager) {
        MainView.Adapter adapter = new MainView.Adapter(getSupportFragmentManager());
        adapter.addFragment(gameTypeFragment, "GameTypes");
        viewPager.setAdapter(adapter);
    }

    private void setupGameTypeList() {
        HttpRequestModel httpRequest = new HttpRequestModel();
        httpRequest.urlPath = getString(R.string.get_game_type_url);
        httpRequest.method = "GET";
        mHttpFragment.setHttpRequest(httpRequest);
        startDownload(1);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        switch (id) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void startDownload(int target) {
        if (!mDownloading && mHttpFragment != null) {
            // Execute the async download.
            mDownloadTarget = target;
            mHttpFragment.startDownload();
            mDownloading = true;
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
                            Gson gson = new GsonBuilder().create();
                            Type gameTypeList = new TypeToken<ArrayList<GameTypeInformationModel>>() {}.getType();
                            gameTypeFragment.gameTypeInformationListModel = gson.fromJson(result.responseBody, GameTypeInformationListModel.class);
                            gameTypeFragment.UpdateGameTypes();
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

        setupGameTypeList();
    }
}
