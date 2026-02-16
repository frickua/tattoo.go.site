package tattoo.go.google.photo.picker;

import lombok.Data;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;
import tattoo.go.google.photo.picker.dto.MediaResponse;
import tattoo.go.google.photo.picker.dto.PickerSession;
import tattoo.go.service.TattooImgService;

import java.util.concurrent.ScheduledFuture;

@Data
public class PickerTask implements Runnable {
    ScheduledFuture future = null;
    final String accessToken;
    final String sessionId;
    final RestTemplate restTemplate;
    final HttpHeaders httpHeaders;
    final TattooImgService tattooImgService;

    public PickerTask(TattooImgService tattooImgService, String accessToken, String sessionId) {
        this.tattooImgService = tattooImgService;
        this.accessToken = accessToken;
        this.sessionId = sessionId;
        this.httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
        httpHeaders.setBearerAuth(accessToken);
        restTemplate = new RestTemplate();
    }

    @Override
    public void run() {
        PickerSession pickerSession = restTemplate.exchange("https://photospicker.googleapis.com/v1/sessions/" + sessionId, HttpMethod.GET, new HttpEntity<>(httpHeaders), PickerSession.class).getBody();
        if (pickerSession != null && pickerSession.isMediaItemsSet()) {
            String url = "https://photospicker.googleapis.com/v1/mediaItems?sessionId=" + sessionId;
            MediaResponse response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(httpHeaders), MediaResponse.class).getBody();
            System.out.println(response);
            tattooImgService.saveImagesToGPhotos(response.getMediaItems(), accessToken);
            if (future != null) {
                future.cancel(false);
            }
        }
    }
}
