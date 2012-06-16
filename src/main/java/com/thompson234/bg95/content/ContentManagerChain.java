package com.thompson234.bg95.content;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.ByteStreams;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class ContentManagerChain implements ContentManagerRW {

    private final List<ContentManager> _contentManagers;
    private boolean _propagateLoadedContent = false;
    private boolean _deepWrites = false;

    public ContentManagerChain(ContentManager... contentManagers) {

        if (contentManagers == null || contentManagers.length == 0) {
            throw new IllegalArgumentException("Content Managers can not be null or empty");
        }

        _contentManagers = Lists.newArrayList(contentManagers);
    }

    public boolean isPropagateLoadedContent() {
        return _propagateLoadedContent;
    }

    public void setPropagateLoadedContent(boolean propagateLoadedContent) {
        _propagateLoadedContent = propagateLoadedContent;
    }

    public boolean isDeepWrites() {
        return _deepWrites;
    }

    public void setDeepWrites(boolean deepWrites) {
        _deepWrites = deepWrites;
    }

    @Override
    public void store(String key, InputStream toStore, Map<String, String> meta) {

        boolean isStored = false;

        InputStream primary = toStore;

        try {
            if (isDeepWrites()) {

                if (!primary.markSupported()) {
                    //primary needs to be resetable
                    final byte[] toStoreBytes = ByteStreams.toByteArray(toStore);
                    primary = new ByteArrayInputStream(toStoreBytes);
                }

                primary.mark(0);  //0 is garbage for ByteArrayInputStream
            }

            for (ContentManager contentManager : _contentManagers) {

                if (contentManager instanceof ContentManagerRW) {

                    ((ContentManagerRW) contentManager).store(key, primary, meta);
                    isStored = true;

                    if (!isDeepWrites()) {
                        break;
                    } else {
                        primary.reset();
                    }
                }
            }
        } catch (Exception ex) {
            throw Throwables.propagate(ex);
        }

        if (!isStored) {
            throw new IllegalStateException("Content for key '" + key + "' was not stored!");
        }
    }

    @Override
    public InputStream load(String key) {

        final ListIterator<ContentManager> it = _contentManagers.listIterator();
        InputStream toCaller = null;

        while (it.hasNext()) {
            toCaller = it.next().load(key);

            if (toCaller != null) {
                break;
            }
        }

        if (toCaller != null && isPropagateLoadedContent()) {

            try {
                final Map<String, String> meta = Maps.newHashMap();
                meta.put(META_ORIGINAL_KEY, key);

                if (!toCaller.markSupported()) {
                    final byte[] toStoreBytes = ByteStreams.toByteArray(toCaller);
                    toCaller = new ByteArrayInputStream(toStoreBytes);
                    meta.put(META_LENGTH, "" + toStoreBytes.length);
                }

                toCaller.mark(0);  //0 is garbage for ByteArrayInputStream
                store(key, toCaller, meta);
                toCaller.reset();
            } catch (IOException ex) {
                throw Throwables.propagate(ex);
            }
        }

        return toCaller;
    }
}
