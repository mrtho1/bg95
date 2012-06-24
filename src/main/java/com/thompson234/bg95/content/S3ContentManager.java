package com.thompson234.bg95.content;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.google.common.base.Strings;
import com.thompson234.bg95.util.Utils;

import java.io.InputStream;
import java.util.Map;

public class S3ContentManager implements ContentManagerRW {

    private AmazonS3Client _s3Client;
    private String _bucket;
    private String _contentRoot;
    private boolean _hashKeys = false;

    public S3ContentManager(AmazonS3Client s3Client, String bucket) {
        _s3Client = s3Client;
        _bucket = bucket;
    }

    public S3ContentManager(AmazonS3Client s3Client, String bucket, String contentRoot) {
        this(s3Client, bucket);
        setContentRoot(contentRoot);
    }

    public boolean isHashKeys() {
        return _hashKeys;
    }

    public void setHashKeys(boolean hashKeys) {
        _hashKeys = hashKeys;
    }

    public String getContentRoot() {
        return _contentRoot;
    }

    public void setContentRoot(String root) {
        _contentRoot = root;
    }

    private String getS3Key(String key) {
        String s3Key = key;

        if (_hashKeys) {
            s3Key = Utils.md5HashString(s3Key);
        }

        if (!Strings.isNullOrEmpty(getContentRoot())) {
            s3Key = getContentRoot() + "/" + s3Key;
        }

        return s3Key;
    }

    @Override
    public void store(String key, InputStream toStore, Map<String, String> meta) {
        final ObjectMetadata metadata = new ObjectMetadata();
        metadata.addUserMetadata("bg95.key", key);

        for (String metaKey : meta.keySet()) {
            metadata.addUserMetadata(metaKey, meta.get(metaKey));
        }

        if (meta.containsKey(META_LENGTH)) {
            metadata.setContentLength(Long.parseLong(meta.get(META_LENGTH)));
        }

        if (meta.containsKey(META_CONTENT_TYPE)) {
            metadata.setContentType(meta.get(META_CONTENT_TYPE));
        }

        _s3Client.putObject(_bucket, getS3Key(key), toStore, metadata);
    }

    @Override
    public InputStream load(String key) {
        final String s3Key = getS3Key(key);

        try {
            final S3Object storedModels = _s3Client.getObject(_bucket, s3Key);
            return storedModels.getObjectContent();
        } catch (Exception ex) {
            return null;
        }
    }
}
