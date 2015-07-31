package org.egov.android.http;

public interface IHttpClientListener {

    public void onProgress(int percent);

    public void onComplete(byte[] data);

    public void onError(byte[] data);

}
