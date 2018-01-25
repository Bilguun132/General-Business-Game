package fyp.generalbusinessgame.Service;

/**
 * Created by Arcstone on 30-Jan-17.
 */

/**
 * Wrapper class that serves as a union of a result value and an exception. When the download
 * task has completed, either the result value or exception can be a non-null value.
 * This allows you to pass exceptions to the UI thread that were thrown during doInBackground().
 */
public class HttpResult {
    public String responseBody;
    public int responseCode;
    public String responseMessage;
    public Exception exception;

    public HttpResult(String _responseBody, int _responseCode, String _responseMesasge, Exception _exception) {
        responseBody = _responseBody;
        responseCode = _responseCode;
        responseMessage = _responseMesasge;
        exception = _exception;
    }
}
