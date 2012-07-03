package com.thompson234.bg95;

import com.thompson234.bg95.model.Type;
import org.codehaus.jackson.annotate.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

public class AWSConfiguration {

    @NotEmpty
    @JsonProperty("accessKey")
    private String _accessKey;

    @NotEmpty
    @JsonProperty("secretKey")
    private String _secretKey;

    @NotEmpty
    @JsonProperty("s3bucket")
    private String _s3bucket;

    @NotEmpty
    @JsonProperty("airmanDomain")
    private String _airmanDomain = Type.AIRMAN;

    @NotEmpty
    @JsonProperty("aircraftDomain")
    private String _aircraftDomain = Type.AIRCRAFT;

    @NotEmpty
    @JsonProperty("missionDomain")
    private String _missionDomain = Type.MISSION;

    public AWSConfiguration() {
    }

    public String getAccessKey() {
        return _accessKey;
    }

    public String getSecretKey() {
        return _secretKey;
    }

    public String getS3bucket() {
        return _s3bucket;
    }

    public String getAirmanDomain() {
        return _airmanDomain;
    }

    public String getAircraftDomain() {
        return _aircraftDomain;
    }

    public String getMissionDomain() {
        return _missionDomain;
    }
}
