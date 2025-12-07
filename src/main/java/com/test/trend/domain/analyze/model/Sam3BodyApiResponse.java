package com.test.trend.domain.analyze.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

@Data
public class Sam3BodyApiResponse {

    private String success;
    private JsonNode data;

}