package fyp.generalbusinessgame.Activity;

import android.content.Intent;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fyp.generalbusinessgame.Models.UserModel;
import fyp.generalbusinessgame.Service.DownloadCallback;
import fyp.generalbusinessgame.Service.HttpRequestModel;
import fyp.generalbusinessgame.Service.HttpResult;
import fyp.generalbusinessgame.Service.HttpServiceFragment;
import fyp.generalbusinessgame.R;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements DownloadCallback<HttpResult> {
    // UI references.
    private TextView mUsernameView;
    private TextView mEmailView;
    private EditText mPasswordView;
    private View mLoginFormView;

    private int mDownloadTarget;
    private HttpServiceFragment mHttpFragment = null;
    private boolean mDownloading = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHttpFragment = HttpServiceFragment.getInstance(getSupportFragmentManager());

        setContentView(R.layout.activity_login);

        // Adding Toolbar to Main screen
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set up the login form.
        mUsernameView = (TextView) findViewById(R.id.username);
        mEmailView = (TextView) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mLoginFormView = findViewById(R.id.login_form);
        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);

        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                loginButtonClicked(view);
            }
        });

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                loginButtonClicked(textView);
                return true;
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void loginButtonClicked(View view) {
        HttpRequestModel httpRequest = new HttpRequestModel();
        httpRequest.urlPath = getString(R.string.login_url);
        httpRequest.method = "POST";
        Pair<String, String> usernamePair = new Pair<String, String>("username", mUsernameView.getText().toString());
        httpRequest.bodyFields.add(usernamePair);
        Pair<String, String> emailPair = new Pair<String, String>("email", mEmailView.getText().toString());
        httpRequest.bodyFields.add(emailPair);
        Pair<String, String> passwordPair = new Pair<String, String>("password", mPasswordView.getText().toString());
        httpRequest.bodyFields.add(passwordPair);
        mHttpFragment.setHttpRequest(httpRequest);
        startDownload(1);
    }

    public void signUpButtonClicked(View view) {
        HttpRequestModel httpRequest = new HttpRequestModel();
        httpRequest.urlPath = getString(R.string.sign_up_url);
        httpRequest.method = "POST";
        Pair<String, String> usernamePair = new Pair<String, String>("username", mUsernameView.getText().toString());
        httpRequest.bodyFields.add(usernamePair);
        Pair<String, String> emailPair = new Pair<String, String>("email", mEmailView.getText().toString());
        httpRequest.bodyFields.add(emailPair);
        Pair<String, String> passwordPair = new Pair<String, String>("password", mPasswordView.getText().toString());
        httpRequest.bodyFields.add(passwordPair);
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
                            UserModel user = gson.fromJson(result.responseBody, UserModel.class);
                           if (user.responseCode == 0) {
                               SharedPreferences sharedPref =
                                       PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                               SharedPreferences.Editor editor = sharedPref.edit();
                               int userId = Integer.parseInt(user.responseMessage.toString().substring(14));
                               editor.putInt(getString(R.string.user_id), userId);
                               editor.commit();
                               ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);
                               progressBar.setVisibility(View.VISIBLE);
                            Intent intent = new Intent(getApplicationContext(), GameTypeSelectionActivity.class);
                            intent.putExtra("userName", mUsernameView.getText().toString());
                            startActivity(intent);

                           }
                        default:
                            Toast.makeText(getApplicationContext(), result.responseBody, Toast.LENGTH_LONG).show();
                            break;
                    }
                } else {
                    switch (result.responseCode) {
                        case 200:
                            Toast.makeText(getApplicationContext(), result.responseBody, Toast.LENGTH_LONG).show();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
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
}

