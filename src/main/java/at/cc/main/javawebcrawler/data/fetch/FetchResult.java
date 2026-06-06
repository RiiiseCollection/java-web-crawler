package at.cc.main.javawebcrawler.data.fetch;

public class FetchResult {
    private final String url;
    private String body;
    private int statusCode;
    private boolean success;
    private String errorMsg;

    public FetchResult(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean wasSuccessful) {
        this.success = wasSuccessful;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public boolean isBrokenUrl() {
        return !success;
    }
}
