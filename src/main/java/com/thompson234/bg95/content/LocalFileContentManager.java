package com.thompson234.bg95.content;

import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.io.ByteStreams;
import com.thompson234.bg95.util.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class LocalFileContentManager implements ContentManagerRW {

    private File _root;
    private boolean _hashKeys;

    public LocalFileContentManager(String localCacheRoot) {
        this(localCacheRoot, null);
    }

    public LocalFileContentManager(String localCacheRoot, String contentRoot) {

        if (localCacheRoot == null) {
            throw new IllegalArgumentException(localCacheRoot + " is not a valid content location");
        }

        _root = new File(localCacheRoot);
        _root.mkdirs();

        if (!Strings.isNullOrEmpty(contentRoot)) {
            _root = new File(_root, contentRoot);
            _root.mkdirs();
        }

        if (_root == null || !_root.exists() || !_root.isDirectory()) {
            throw new IllegalArgumentException(_root.getAbsolutePath() + " is not a valid content location");
        }
    }

    public boolean isHashKeys() {
        return _hashKeys;
    }

    public void setHashKeys(boolean hashKeys) {
        _hashKeys = hashKeys;
    }

    private File getLocalCacheFile(String key) {

        return new File(_root, isHashKeys() ? Utils.md5HashString(key) : key);
    }

    @Override
    public void store(String key, InputStream toStore, Map<String, String> meta) {

        try {
            ByteStreams.copy(toStore, new FileOutputStream(getLocalCacheFile(key)));
        } catch (IOException ex) {
            throw Throwables.propagate(ex);
        }
    }

    @Override
    public InputStream load(String key) {

        try {
            final File localFile = getLocalCacheFile(key);
            return localFile.exists() ? new FileInputStream(localFile) : null;
        } catch (IOException ex) {
            throw Throwables.propagate(ex);
        }
    }
}
