package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;

import java.util.Map;

@Service
public class GHNService {
    String token = "e5863113-7b6e-11f0-9f15-fe0c36d2f0cb";
    Integer shopId= 5956446;
    @Autowired
    RestTemplate restTemplate;
    public Integer getShippingFee(int toDistrictId, String toWardCode){
        HttpHeaders headers = new HttpHeaders();
        headers.set("Token", token);
        headers.set("ShopId", String.valueOf(shopId));
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.of(
                "from_district_id", 1451,
                "service_type_id", 2,
                "to_district_id", toDistrictId,
                "height", 10,
                "length", 20,
                "weight", 200,
                "width", 20
        );
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "https://online-gateway.ghn.vn/shiip/public-api/v2/shipping-order/fee",
                entity, Map.class
        );

        Map<String, Object> data = (Map<String, Object>) response.getBody().get("data");
        return (Integer) data.get("total");
    }
}
