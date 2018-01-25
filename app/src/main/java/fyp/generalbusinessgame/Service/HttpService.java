package fyp.generalbusinessgame.Service;

import android.support.v4.util.Pair;
import android.text.TextUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class HttpService {
    private HttpURLConnection conn = null;
    private URL url = null;
    private int responseCode = -1;
    private String responseMessage = "";
    private boolean initializedSuccess = false;
    private boolean downloadFinished = true;
    private InputStream inputStream = new InputStream() {
        @Override
        public int read() throws IOException {
            return 0;
        }
    };

    public HttpService(String passedInUrl, String requestMethod) {
        if (!TextUtils.isEmpty(passedInUrl)) {
            try {
                url = new URL(passedInUrl);
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod(requestMethod);
                conn.setDoInput(true);
                initializedSuccess = true;
            } catch (Exception ex) {
                initializedSuccess = false;
            }
        } else {
            initializedSuccess = false;
        }
    }

    public void buildRequestBody(ArrayList<Pair<String, String>> keyValuePairList) throws Exception {
        if (!(conn == null) && !(keyValuePairList == null || keyValuePairList.isEmpty())) {
            StringBuilder postData = new StringBuilder();
            for (Pair<String, String> pair : keyValuePairList) {
                if (postData.length() != 0) postData.append('&');
                postData.append(URLEncoder.encode(pair.first, "UTF-8"));
                postData.append('=');
                postData.append(URLEncoder.encode(pair.second, "UTF-8"));
            }
            conn.setDoOutput(true);
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(postData.toString());
            writer.flush();
            writer.close();
            os.close();
        }
    }

    public void buildRequestHeader(ArrayList<Pair<String, String>> keyValuePairList) throws Exception {
        if (!(conn == null) && !(keyValuePairList == null || keyValuePairList.isEmpty())) {
            for (Pair<String, String> pair : keyValuePairList) {
                conn.setRequestProperty(pair.first, pair.second);
            }
        }
    }

    public void buildRequestUrlParameter(ArrayList<Pair<String, String>> keyValuePairList) throws Exception {
        if (!(conn == null) && !(keyValuePairList == null || keyValuePairList.isEmpty())) {
            StringBuilder postData = new StringBuilder();
            for (Pair<String, String> pair : keyValuePairList) {
                if (postData.length() != 0) postData.append('&');
                postData.append(URLEncoder.encode(pair.first, "UTF-8"));
                postData.append('=');
                postData.append(URLEncoder.encode(pair.second, "UTF-8"));
            }
            String newUrl = this.url.toString() + "?" + postData.toString();
            HttpURLConnection newConn = (HttpURLConnection) new URL(newUrl).openConnection();
            newConn.setRequestMethod(conn.getRequestMethod());
            newConn.setReadTimeout(conn.getReadTimeout());
            newConn.setConnectTimeout(conn.getConnectTimeout());
            newConn.setDoInput(conn.getDoInput());
            newConn.setDoOutput(conn.getDoOutput());
            conn = newConn;
        }
    }

    public boolean connectAndDownloadResponse() {
        if (initializedSuccess) {
            try {
                downloadFinished = false;
                conn.connect();
                inputStream = conn.getInputStream();
                responseCode = conn.getResponseCode();
                responseMessage = conn.getResponseMessage();
                downloadFinished = true;
                return true;
            } catch (Exception e) {
                downloadFinished = true;
                return false;
            }
        } else {
            return false;
        }
    }

    public int getResponseCode() {
        if (downloadFinished) {
            return responseCode;
        } else {
            return -1;
        }
    }

    public String getResponseMessage() {
        if (downloadFinished) {
            return responseMessage;
        } else {
            return "";
        }
    }

    public InputStream getInputStream() {
        if (downloadFinished) {
            return inputStream;
        } else {
            return null;
        }
    }

    public String getInputStreamText() {
        if (downloadFinished) {
            try {
                return readIt(inputStream, 500);
            } catch (Exception e) {
                return "";
            }
        } else {
            return "";
        }
    }

    private String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        if (conn.getRequestMethod().equals("123")) {
            char[] buffer = new char[len];
            reader.read(buffer);
            return new String(buffer);
        } else {
            StringBuilder sb = new StringBuilder();
            for (int c; (c = reader.read()) >= 0; )
                sb.append((char) c);
            String response = sb.toString();
            return response;
        }
    }
}