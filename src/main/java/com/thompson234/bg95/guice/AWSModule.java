package com.thompson234.bg95.guice;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.thompson234.bg95.AWSConfiguration;

import javax.inject.Named;

public class AWSModule extends AbstractModule {

    private final AWSConfiguration _configuration;

    public AWSModule(AWSConfiguration configuration) {
        _configuration = configuration;
    }

    @Override
    protected void configure() {
    }

    @Provides
    public AWSCredentials awsCredentials() {
        return new BasicAWSCredentials(_configuration.getAccessKey(), _configuration.getSecretKey());
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
    @Named("aws.s3bucket")
    public String s3bucket() {
        return _configuration.getS3bucket();
    }

    @Provides
    @Named("aws.airmanDomain")
    public String airmanDomain() {
        return _configuration.getAirmanDomain();
    }

    @Provides
    @Named("aws.aircraftDomain")
    public String aircraftDomain() {
        return _configuration.getAircraftDomain();
    }

    @Provides
    @Named("aws.missionDomain")
    public String missionDomain() {
        return _configuration.getMissionDomain();
    }

    public boolean forceDomainReset() {
        return true;
    }


    @Provides
    @Named("aws.forceAircraftDomainReset")
    public boolean forceAircraftDomainReset() {
        return forceDomainReset();
    }

    @Provides
    @Named("aws.forceAirmanDomainReset")
    public boolean forceAirmanDomainReset() {
        return forceDomainReset();
    }

    @Provides
    @Named("aws.forceMissionDomainReset")
    public boolean forceMissionDomainReset() {
        return forceDomainReset();
    }
}
