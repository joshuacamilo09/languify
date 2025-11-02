package io.languify.communication.chat.service;

import com.google.api.client.util.Value;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class LocationGeoService {
  private final RestTemplate restTemplate = new RestTemplate();

  @Value("${geo.api.key}")
  private String apiKey;

  @Value("${geo.api.url}")
  private String apiURL;

  public Map<String, String> getLocationByIp(String ip) {

    String url =
        UriComponentsBuilder.fromHttpUrl(apiURL)
            .queryParam("apiKey", apiKey)
            .queryParam("ip", ip)
            .build()
            .toUriString();

    Map<String, String> response = restTemplate.getForObject(url, Map.class);
    return response;
  }

  public String getCountry(String ip) {
    Map<String, String> loc = getLocationByIp(ip);
    return loc != null ? loc.get("country_name") : null;
  }

  public String getCity(String ip) {
    Map<String, String> loc = getLocationByIp(ip);
    return loc != null ? loc.get("city") : null;
  }
}
