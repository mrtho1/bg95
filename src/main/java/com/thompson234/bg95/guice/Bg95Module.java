package com.thompson234.bg95.guice;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.thompson234.bg95.Bg95Configuration;
import com.thompson234.bg95.content.ContentManager;
import com.thompson234.bg95.content.ContentManagerChain;
import com.thompson234.bg95.content.ContentManagerRW;
import com.thompson234.bg95.content.HttpContentManager;
import com.thompson234.bg95.content.LocalFileContentManager;
import com.thompson234.bg95.content.S3ContentManager;
import com.thompson234.bg95.dao.AircraftDao;
import com.thompson234.bg95.dao.AirmanDao;
import com.thompson234.bg95.dao.MissionDao;
import com.thompson234.bg95.dao.impl.SdbAircraftDaoImpl;
import com.thompson234.bg95.dao.impl.SdbAirmanDaoImpl;
import com.thompson234.bg95.dao.impl.SdbMissionDaoImpl;
import com.thompson234.bg95.service.HttpHarvester;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

import javax.inject.Named;

public class Bg95Module extends AbstractModule {

    private Bg95Configuration _configuration;

    public Bg95Module(Bg95Configuration configuration) {
        _configuration = configuration;
    }

    @Override
    protected void configure() {

        bind(AircraftDao.class).to(SdbAircraftDaoImpl.class).in(Singleton.class);
        bind(AirmanDao.class).to(SdbAirmanDaoImpl.class).in(Singleton.class);
        bind(MissionDao.class).to(SdbMissionDaoImpl.class).in(Singleton.class);
        bind(HttpHarvester.class).in(Singleton.class);
        bind(HttpContentManager.class).in(Singleton.class);
    }

    @Provides
    public AWSCredentials awsCredentials(@Named("awsAccessKey") String accessKey,
                                         @Named("awsSecretKey") String secretKey) {

        return new BasicAWSCredentials(accessKey, secretKey);
    }

    @Provides
    public AmazonSimpleDBClient amazonSimpleDBClient(AWSCredentials credentials) {
        return new AmazonSimpleDBClient(credentials);
    }

    @Provides
    public AmazonS3Client amazonS3Client(AWSCredentials credentials) {
        return new AmazonS3Client(credentials);
    }

    @Provides
    public ObjectMapper objectMapper() {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationConfig.Feature.INDENT_OUTPUT, true);

        return objectMapper;
    }

    @Provides
    @Named("awsAccessKey")
    public String awsAccessKey() {
        return _configuration.getAwsAccessKey();
    }

    @Provides
    @Named("awsSecretKey")
    public String awsSecretKey() {
        return _configuration.getAwsSecretKey();
    }

    @Provides
    @Named("localCacheRoot")
    public String localCacheRoot() {
        return _configuration.getLocalCacheRoot();
    }

    @Provides
    @Named("s3CacheBucket")
    public String s3CacheBucket() {
        return _configuration.getS3CacheBucket();
    }

    @Provides
    @Named("modelCacheName")
    public String modelCacheName() {
        return _configuration.getModelCacheName();
    }

    @Provides
    @Named("httpCacheName")
    public String httpCacheName() {
        return _configuration.getHttpCacheName();
    }

    @Provides
    @Named("modelDeepWrites")
    public boolean modelDeepWrites() {
        return _configuration.isModelDeepWrites();
    }

    @Provides
    @Named("modelPropagateLoadedContent")
    public boolean modelPropagateLoadedContent() {
        return _configuration.isModelPropagateLoadedContent();
    }

    @Provides
    @Named("httpDeepWrites")
    public boolean httpDeepWrites() {
        return _configuration.isHttpDeepWrites();
    }

    @Provides
    @Named("httpPropagateLoadedContent")
    public boolean httpPropagateLoadedContent() {
        return _configuration.isHttpPropagateLoadedContent();
    }

    @Provides
    @Named("modelContentManager")
    public ContentManagerRW modelContentManager(@Named("modelLocalFileContentManager") ContentManager localContentManager,
                                                @Named("modelS3ContentManager") ContentManager s3ContentManager,
                                                @Named("modelDeepWrites") boolean deepWrites,
                                                @Named("modelPropagateLoadedContent") boolean propagateLoadedContent) {

        final ContentManagerChain chain = new ContentManagerChain(localContentManager, s3ContentManager);
        chain.setDeepWrites(deepWrites);
        chain.setPropagateLoadedContent(propagateLoadedContent);
        return chain;
    }

    @Provides
    @Named("modelLocalFileContentManager")
    public ContentManager modelLocalFileCacheLoader(@Named("localCacheRoot") String localCacheRoot,
                                                    @Named("modelCacheName") String cacheName) {

        return new LocalFileContentManager(localCacheRoot, cacheName);
    }

    @Provides
    @Named("modelS3ContentManager")
    public ContentManager modelS3ContentManager(AmazonS3Client amazonS3Client,
                                                @Named("s3CacheBucket") String bucket,
                                                @Named("modelCacheName") String cacheRoot) {
        return new S3ContentManager(amazonS3Client, bucket, cacheRoot);
    }

    @Provides
    @Named("httpContentContentManager")
    public ContentManager httpContentContentManager(@Named("httpLocalFileContentManager") ContentManager localContentManager,
                                                    @Named("httpS3ContentManager") ContentManager s3ContentManager,
                                                    HttpContentManager httpContentManager,
                                                    @Named("httpDeepWrites") boolean deepWrites,
                                                    @Named("httpPropagateLoadedContent") boolean propagateLoadedContent) {

        final ContentManagerChain chain = new ContentManagerChain(localContentManager, s3ContentManager, httpContentManager);
        chain.setDeepWrites(deepWrites);
        chain.setPropagateLoadedContent(propagateLoadedContent);
        return chain;
    }

    @Provides
    @Named("httpLocalFileContentManager")
    public ContentManager httpLocalFileCacheLoader(@Named("localCacheRoot") String localCacheRoot,
                                                   @Named("httpCacheName") String cacheName) {

        final LocalFileContentManager lfContentManager = new LocalFileContentManager(localCacheRoot, cacheName);
        lfContentManager.setHashKeys(true);
        return lfContentManager;
    }

    @Provides
    @Named("httpS3ContentManager")
    public ContentManager httpS3ContentManager(AmazonS3Client amazonS3Client,
                                               @Named("s3CacheBucket") String bucket,
                                               @Named("httpCacheName") String cacheRoot) {

        final S3ContentManager s3ContentManager = new S3ContentManager(amazonS3Client, bucket);
        s3ContentManager.setContentRoot(cacheRoot);
        s3ContentManager.setHashKeys(true);
        return s3ContentManager;
    }

    @Provides
    @Named("domain.airman.name")
    public String airmanDomain() {
        return _configuration.getAirmanDomain();
    }

    @Provides
    @Named("domain.aircraft.name")
    public String aircraftDomain() {
        return _configuration.getAircraftDomain();
    }

    @Provides
    @Named("domain.mission.name")
    public String missionDomain() {
        return _configuration.getMissionDomain();
    }

    public boolean forceDomainReset() {
        return false;
    }

    public boolean preCache() {
        return true;
    }

    @Provides
    @Named("domain.aircraft.forceReset")
    public boolean domainAircraftForceReset() {
        return forceDomainReset();
    }

    @Provides
    @Named("domain.airman.forceReset")
    public boolean domainAirmanForceReset() {
        return forceDomainReset();
    }

    @Provides
    @Named("domain.mission.forceReset")
    public boolean domainMissionForceReset() {
        return forceDomainReset();
    }

    @Provides
    @Named("domain.aircraft.preCache")
    public boolean domainAircraftPreCache() {
        return preCache();
    }

    @Provides
    @Named("domain.airman.preCache")
    public boolean domainAirmanPreCache() {
        return preCache();
    }

    @Provides
    @Named("domain.mission.preCache")
    public boolean domainMissionPreCache() {
        return preCache();
    }
}
