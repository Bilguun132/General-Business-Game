package fyp.generalbusinessgame.Service;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.util.Pair;

import java.io.IOException;

import fyp.generalbusinessgame.R;


public class HttpServiceFragment extends Fragment {
    public static final String TAG = "HttpServiceFragment";
    public DownloadCallback mCallback;
    private DownloadTask mDownloadTask;

    private static final String requestKey = "requestKey";
    private static HttpRequestModel httpRequest = null;

    public static HttpServiceFragment getInstance(FragmentManager fragmentManager) {
        HttpServiceFragment httpServiceFragment = new HttpServiceFragment();
        fragmentManager.beginTransaction().add(httpServiceFragment, TAG).commit();
        return httpServiceFragment;
    }

    public void setHttpRequest(HttpRequestModel _httpRequest) {
        httpRequest = _httpRequest;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        String jsonString = getArguments().getString(requestKey);
//        Gson gson = new Gson();
//        httpRequest = (HttpRequestModel) gson.fromJson(jsonString, HttpRequestModel.class);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Host Activity will handle callbacks from task.
        mCallback = (DownloadCallback) context;
        mCallback.finishInitializing();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        // Clear reference to host Activity to avoid memory leak.
        mCallback = null;
    }

    @Override
    public void onDestroy() {
        // Cancel task when Fragment is destroyed.
        cancelDownload();
        super.onDestroy();
    }

    /**
     * Start non-blocking execution of DownloadTask.
     */
    public void startDownload() {
        cancelDownload();
        mDownloadTask = new DownloadTask(mCallback);
        mDownloadTask.execute(httpRequest);
    }

    public void cancelDownload() {
        if (mDownloadTask != null) {
            mDownloadTask.cancel(true);
        }
    }

    /**
     * Implementation of AsyncTask designed to fetch data from the network.
     */
    private class DownloadTask extends AsyncTask<HttpRequestModel, Void, HttpResult> {

        private DownloadCallback<HttpResult> mCallback;

        DownloadTask(DownloadCallback<HttpResult> callback) {
            setCallback(callback);
        }

        void setCallback(DownloadCallback<HttpResult> callback) {
            mCallback = callback;
        }

        /**
         * Cancel background network operation if we do not have network connectivity.
         */
//        @Override
//        protected void onPreExecute() {
//            if (mCallback != null) {
//                NetworkInfo networkInfo = mCallback.getActiveNetworkInfo();
//                if (networkInfo == null || !networkInfo.isConnected() ||
//                        (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
//                                && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
//                    // If no connectivity, cancel task and update Callback with null data.
//                    mCallback.updateFromDownload(null);
//                    cancel(true);
//                }
//            }
//        }

        /**
         * Defines work to perform on the background thread.
         */
        @Override
        protected HttpResult doInBackground(HttpRequestModel... httpRequestModels) {
            HttpResult result = null;
            if (!isCancelled() && httpRequestModels != null && httpRequestModels.length > 0) {
                HttpRequestModel httpRequest = httpRequestModels[0];
                try {
                    String domainName = "";
                    String subdomainName = "";
                    SharedPreferences sharedPref =
                            PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
                    if (sharedPref != null) {
                        domainName = sharedPref.getString(getString(R.string.domain_name_key), null);
                        if (domainName == null || domainName.isEmpty()) {
                            domainName = getString(R.string.domain_name);
                        }
                        subdomainName = sharedPref.getString(getString(R.string.subdomain_name_key), null);
                        if (subdomainName == null || subdomainName.isEmpty()) {
                            subdomainName = getString(R.string.subdomain_name);
                        }
                    }
                    HttpService downloadService = new HttpService(domainName + subdomainName + httpRequest.urlPath, httpRequest.method);
                    downloadService.buildRequestUrlParameter(httpRequest.urlParameterFields);
                    Pair<String,String> authenticationToken = getAuthenticationToken();
                    httpRequest.headerFields.add(authenticationToken);
                    downloadService.buildRequestHeader(httpRequest.headerFields);
                    downloadService.buildRequestBody(httpRequest.bodyFields);
                    boolean downdloadResult = downloadService.connectAndDownloadResponse();
                    if (downdloadResult) {
                        String responseBody = downloadService.getInputStreamText();
                        int responseCode = downloadService.getResponseCode();
                        String responseMessage = downloadService.getResponseMessage();
                        if (responseBody != null) {
                            result = new HttpResult(responseBody, responseCode, responseMessage, null);
                        } else {
                            throw new IOException("No response received.");
                        }
                    } else {
                        throw new IOException("Cannot connect to the server.");
                    }
                } catch (Exception e) {
                    result = new HttpResult("", -1, "", e);
                }
            }
            return result;
        }

        /**
         * Updates the DownloadCallback with the result.
         */
        @Override
        protected void onPostExecute(HttpResult result) {
            if (result != null && mCallback != null) {
                mCallback.updateFromDownload(result);
            }
            if (mCallback != null) {
                mCallback.finishDownloading();
            }
        }

        /**
         * Override to add special behavior for cancelled AsyncTask.
         */
        @Override
        protected void onCancelled(HttpResult result) {
            mCallback.finishDownloading();
        }
    }

    private Pair<String,String> getAuthenticationToken() {
        Pair<String, String> returnedTokenPair;
        String token = "";
        SharedPreferences sharedPref = getContext().getSharedPreferences(getString(R.string.shared_preference_key), Context.MODE_PRIVATE);
        if (sharedPref != null) {
            token = sharedPref.getString(getString(R.string.authentication_token_key), null);
            if (token == null) {
                token = "";
            }
        }
        returnedTokenPair = new Pair<String, String>("Authorization", token);
        return returnedTokenPair;
    }
}