package fyp.generalbusinessgame.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fyp.generalbusinessgame.Models.CostListModel;
import fyp.generalbusinessgame.Models.GamePeriodModel;
import fyp.generalbusinessgame.R;
import fyp.generalbusinessgame.Service.DownloadCallback;
import fyp.generalbusinessgame.Service.HttpRequestModel;
import fyp.generalbusinessgame.Service.HttpResult;
import fyp.generalbusinessgame.Service.HttpServiceFragment;

public class GameSummaryActivity extends AppCompatActivity implements DownloadCallback<HttpResult> {
    BottomNavigationView bottomNavigationView;
    private DrawerLayout mDrawerLayout;
    private ViewPager viewPager;
    private int mDownloadTarget;
    private HttpServiceFragment mHttpFragment = null;
    private boolean mDownloading = false;
    private int gameId;
    private int firmId;
    private GameSummaryFragment gameSummaryFragment = new GameSummaryFragment();
    private FinanceFragment financeFragment = new FinanceFragment();
    private MarketingFragment marketingFragment = new MarketingFragment();
    private ResearchDevFragment researchDevFragment = new ResearchDevFragment();
    private ProductionFragment productionFragment = new ProductionFragment();
    MenuItem prevMenuItem;
    private boolean requestedCostList = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final FragmentManager fragmentManager = getSupportFragmentManager();
        mHttpFragment = HttpServiceFragment.getInstance(getSupportFragmentManager());
        setContentView(R.layout.activity_game_summary);
        gameId = getIntent().getIntExtra(getString(R.string.game_id), 0);
        firmId = getIntent().getIntExtra(getString(R.string.firm_id), 0);
        // Adding Toolbar to Main screen
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        // Setting ViewPager for each Tabs
//        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
//        setupViewPager(viewPager);

        // Create Navigation drawer and inlfate layout
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);

        // Adding menu icon to Toolbar
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            VectorDrawableCompat indicator
                    = VectorDrawableCompat.create(getResources(), R.drawable.ic_menu, getTheme());
            indicator.setTint(ResourcesCompat.getColor(getResources(), R.color.white, getTheme()));
            supportActionBar.setHomeAsUpIndicator(indicator);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
        // Set behavior of Navigation drawer
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
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            default:
                                mDrawerLayout.closeDrawers();
                                return true;
                        }

                        // Closing drawer on item click
                    }
                });

        viewPager = (ViewPager) findViewById(R.id.viewpager);

        BottomNavigationBar bottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar);
        bottomNavigationBar.setAutoHideEnabled(false);
        bottomNavigationBar
                .addItem(new BottomNavigationItem(R.drawable.ic_home_black_24dp, "Home"))
                .addItem(new BottomNavigationItem(R.drawable.icons8_coins_24px, "Finance"))
                .addItem(new BottomNavigationItem(R.drawable.icons8_experiment_24px, "R & D"))
                .addItem(new BottomNavigationItem(R.drawable.icons8_advertising_24px, "Mkt"))
                .addItem(new BottomNavigationItem(R.drawable.icons8_prototype_24px, "Prod"))
                .setMode(BottomNavigationBar.BACKGROUND_STYLE_STATIC)
                .setMode(BottomNavigationBar.MODE_FIXED)
                .initialise();
        ShowFragment(gameSummaryFragment);

        bottomNavigationBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position) {
                switch (position) {
                    case 0:
                        ShowFragment(gameSummaryFragment);
                        break;
                    case 1:
                        ShowFragment(financeFragment);
                        break;
                    case 2:
                        ShowFragment(researchDevFragment);
                        break;
                    case 3:
                        ShowFragment(marketingFragment);
                        break;
                    case 4:
                        ShowFragment(productionFragment);
                        break;
                }
            }

            @Override
            public void onTabUnselected(int position) {
            }

            @Override
            public void onTabReselected(int position) {
            }
        });
    }

    private void ShowFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.game_summary_main_fragment_container, fragment);
        transaction.commit();
    }

    private void setupViewPager(ViewPager viewPager) {
        MainView.Adapter adapter = new MainView.Adapter(getSupportFragmentManager());
        adapter.addFragment(gameSummaryFragment, "Game Summary");
        adapter.addFragment(financeFragment, "Finance Summary");
        adapter.addFragment(researchDevFragment, "RnD Summary");
        adapter.addFragment(marketingFragment, "Marketing Summary");
        adapter.addFragment(productionFragment, "Production Summary");
        viewPager.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
                            GamePeriodModel gamePeriodModel = gson.fromJson(result.responseBody, GamePeriodModel.class);
                            gameSummaryFragment.gamePeriodModel = gamePeriodModel;
                            gameSummaryFragment.firmId = firmId;
                            gameSummaryFragment.UpdateGameInformation();
                            financeFragment.gamePeriodModel = gamePeriodModel;
                            financeFragment.firmId = firmId;
                            marketingFragment.gamePeriodModel = gamePeriodModel;
                            marketingFragment.firmId = firmId;
                            researchDevFragment.gamePeriodModel = gamePeriodModel;
                            researchDevFragment.firmId = firmId;
                            productionFragment.gamePeriodModel = gamePeriodModel;
                            productionFragment.firmId = firmId;
                            break;
                        default:
                            Toast.makeText(getApplicationContext(), result.responseBody, Toast.LENGTH_LONG).show();
                            break;
                    }

                    return;
                }

                if (mDownloadTarget == 2) {
                    switch (result.responseCode) {
                        case 200:
                            Gson gson = new GsonBuilder().create();
                            CostListModel costListModel = gson.fromJson(result.responseBody, CostListModel.class);
                            marketingFragment.costListModel = costListModel;
                            marketingFragment.firmId = firmId;
                            researchDevFragment.costListModel = costListModel;
                            researchDevFragment.firmId = firmId;
                            productionFragment.costListModel = costListModel;
                            productionFragment.firmId = firmId;
                            break;
                        default:
                            Toast.makeText(getApplicationContext(), result.responseBody, Toast.LENGTH_LONG).show();
                            break;
                    }
                    requestedCostList = true;
                }

//                setupViewPager(viewPager);
            }
        }
    }

    @Override
    public void finishDownloading() {
        mDownloading = false;

        if (mHttpFragment != null) {
            mHttpFragment.cancelDownload();
        }

        if (!requestedCostList) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    getCostModel();
                }
            });
        }
    }

    @Override
    public void finishInitializing() {
        getGamePeriod();
    }

    private void getGamePeriod() {
        HttpRequestModel httpRequest = new HttpRequestModel();
        httpRequest.urlPath = getString(R.string.get_game_period_by_game_id) + gameId;
        httpRequest.method = "GET";
        mHttpFragment.setHttpRequest(httpRequest);
        startDownload(1);
    }

    private void getCostModel() {
        HttpRequestModel httpRequest = new HttpRequestModel();
        httpRequest.urlPath = getString(R.string.get_cost_model_by_firm_id) + firmId;
        httpRequest.method = "GET";
        mHttpFragment.setHttpRequest(httpRequest);
        startDownload(2);
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
