package com.thompson234.bg95.guice;

import com.amazonaws.services.s3.AmazonS3Client;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.thompson234.bg95.ContentManagerConfiguration;
import com.thompson234.bg95.content.ContentManager;
import com.thompson234.bg95.content.ContentManagerChain;
import com.thompson234.bg95.content.ContentManagerRW;
import com.thompson234.bg95.content.HttpContentManager;
import com.thompson234.bg95.content.LocalFileContentManager;
import com.thompson234.bg95.content.S3ContentManager;

import javax.inject.Named;

public class ContentManagerModule extends AbstractModule {

    private final ContentManagerConfiguration _configuration;

    public ContentManagerModule(ContentManagerConfiguration configuration) {
        _configuration = configuration;
    }

    @Override
    protected void configure() {
    }

    @Provides
    public HttpContentManager httpContentManager() {
        final HttpContentManager httpContentManager = new HttpContentManager();
        httpContentManager.setNice(_configuration.getCacheNice());

        return httpContentManager;
    }

    @Provides
    @Named("contentManager.modelContentManager")
    public ContentManagerRW modelContentManager(@Named("contentManager.modelLocalFileContentManager") ContentManager localContentManager,
                                                @Named("contentManager.modelS3ContentManager") ContentManager s3ContentManager) {

        final ContentManagerChain chain = new ContentManagerChain(localContentManager, s3ContentManager);
        chain.setDeepWrites(_configuration.isModelDeepWrites());
        chain.setPropagateLoadedContent(_configuration.isModelPropagateLoadedContent());
        return chain;
    }

    @Provides
    @Named("contentManager.modelLocalFileContentManager")
    public ContentManager modelLocalFileContentManager() {

        return new LocalFileContentManager(_configuration.getContentRoot(), _configuration.getModelCacheName());
    }

    @Provides
    @Named("contentManager.modelS3ContentManager")
    public ContentManager modelS3ContentManager(AmazonS3Client amazonS3Client,
                                                @Named("aws.s3bucket") String s3bucket) {
        return new S3ContentManager(amazonS3Client, s3bucket, _configuration.getModelCacheName());
    }

    @Provides
    @Named("contentManager.httpContentContentManager")
    public ContentManager httpContentContentManager(@Named("contentManager.httpLocalFileContentManager") ContentManager localContentManager,
                                                    @Named("contentManager.httpS3ContentManager") ContentManager s3ContentManager,
                                                    HttpContentManager httpContentManager) {

        final ContentManagerChain chain = new ContentManagerChain(localContentManager, s3ContentManager, httpContentManager);
        chain.setDeepWrites(_configuration.isHttpDeepWrites());
        chain.setPropagateLoadedContent(_configuration.isHttpPropagateLoadedContent());
        return chain;
    }

    @Provides
    @Named("contentManager.httpLocalFileContentManager")
    public ContentManager httpLocalFileContentManager() {

        final LocalFileContentManager lfContentManager =
                new LocalFileContentManager(_configuration.getContentRoot(), _configuration.getHttpCacheName());
        lfContentManager.setHashKeys(true);
        return lfContentManager;
    }

    @Provides
    @Named("contentManager.httpS3ContentManager")
    public ContentManager httpS3ContentManager(AmazonS3Client amazonS3Client,
                                               @Named("aws.s3bucket") String s3bucket) {

        final S3ContentManager s3ContentManager = new S3ContentManager(amazonS3Client, s3bucket, _configuration.getHttpCacheName());
        s3ContentManager.setHashKeys(true);
        return s3ContentManager;
    }
}
