package com.thompson234.bg95.content;

import com.google.common.base.Throwables;
import com.thompson234.bg95.util.Utils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.InputStream;

public class HttpContentManager implements ContentManager {

    private HttpClient _httpClient = new DefaultHttpClient();

    private long _nice = 0;
    private long _last = -1;

    public long getNice() {
        return _nice;
    }

    public void setNice(long nice) {
        _nice = nice;
    }

    private long getLast() {
        return _last;
    }

    private void updateLastToNow() {
        _last = System.currentTimeMillis();
    }

    private void politeWait() {

        if (getLast() != -1) {

            long since = System.currentTimeMillis() - getLast();
            Utils.threadSleep(getNice() - since);
        }

        updateLastToNow();
    }

    private InputStream httpGet(String url) {
        final HttpGet get = new HttpGet(url);

        try {
            final HttpResponse response = _httpClient.execute(get);
            final HttpEntity entity = response.getEntity();

            if (entity != null) {
                return entity.getContent();
            }
        } catch (Exception ex) {
            throw Throwables.propagate(ex);
        }

        return null;
    }

    @Override
    public InputStream load(String key) {

        politeWait();
        return httpGet(key);
    }
}
