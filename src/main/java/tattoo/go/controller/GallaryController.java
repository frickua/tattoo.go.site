package tattoo.go.controller;

import com.google.api.client.auth.oauth2.AuthorizationCodeResponseUrl;
import com.google.api.client.auth.oauth2.TokenResponseException;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import tattoo.go.dto.GallaryImageDto;
import tattoo.go.google.photo.picker.PickerTask;
import tattoo.go.service.TattooImgService;

import java.io.IOException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
public class GallaryController {

  @Autowired
  private TattooImgService tattooImgService;

  @Autowired
  private TaskScheduler taskScheduler;

  @ResponseBody
  @RequestMapping("/head/images")
  public List<GallaryImageDto> getHeadImages() throws IOException {
    return tattooImgService.getImagesFromGPhotos(12);
//    return tattooImgService.getRandomizedImagesUrls(12);
  }

  @GetMapping("/photo")
  public String redirectToPicker() {
    return "redirect:/picker.html";
  }

  @GetMapping("/picker")
  public String pickerRedirect(@RequestParam Map<String, String> allRequestParams, HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Change code to access token and give it back to picker
    log.info("Try to get auth token for {}", request.getRequestURL().toString());
    String url = request.getRequestURL().toString();
    if (url.toLowerCase().contains("tattoogo.com.ua")) { // Behind revers proxy return http instead https, but fail on uri mismatch
      url = url.replace("http:", "https:");
    }
    GoogleTokenResponse tResponse = new GoogleAuthorizationCodeTokenRequest(
            new NetHttpTransport(),
            new GsonFactory(),
            "481370185603-9es1nm32g1d3i8k4sv5r6a97s3a3aq78.apps.googleusercontent.com",
            "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",
            allRequestParams.get("code"),
            url // Спеціальне значення для popup-авторизації
    ).execute();
    return "redirect:/picker.html?accessToken=" + tResponse.getAccessToken();
  }

  @GetMapping("/media-items")
  public String getMediaItems(@RequestParam String sessionId, @RequestParam String accessToken) {
    // Wait media items by session id while selected by picker
    PickerTask task = new PickerTask(tattooImgService, accessToken, sessionId);
    task.setFuture(taskScheduler.scheduleWithFixedDelay(task, Duration.of(1, ChronoUnit.SECONDS)));
    return "redirect:/";
  }

  @RequestMapping("/gphotos")
  public String getGPhotosImages(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String url = new GoogleAuthorizationCodeRequestUrl("481370185603-9es1nm32g1d3i8k4sv5r6a97s3a3aq78.apps.googleusercontent.com",
        "http://localhost:8080/gphotos/callback", Arrays.asList(
        "https://www.googleapis.com/auth/userinfo.email",
        "https://www.googleapis.com/auth/userinfo.profile",
        "https://www.googleapis.com/auth/photoslibrary.readonly",
        "https://www.googleapis.com/auth/photoslibrary.appendonly",
        "https://www.googleapis.com/auth/photoslibrary.readonly.appcreateddata",
        "https://www.googleapis.com/auth/photoslibrary",
        "https://www.googleapis.com/auth/photoslibrary.sharing")).setApprovalPrompt("force").setAccessType("offline").build();
//        tattooImgService.getImagesFromGPhotos();
    return "redirect:" + url;
  }

  @RequestMapping(value = "/gphotos/callback", produces = "application/json")
  @ResponseBody
  public String getGPhotosImagesCallBack(HttpServletRequest request, HttpServletResponse response) throws IOException {
    StringBuffer fullUrlBuf = request.getRequestURL();
    if (request.getQueryString() != null) {
      fullUrlBuf.append('?').append(request.getQueryString());
    }
    AuthorizationCodeResponseUrl authResponse =
        new AuthorizationCodeResponseUrl(fullUrlBuf.toString());
    // check for user-denied error
    if (authResponse.getError() != null) {
      // authorization denied...
      return authResponse.getError() + "\n" + authResponse.getErrorDescription();
    } else {
      System.out.println(authResponse.getCode());
      GoogleTokenResponse tokenResponse = requestAccessToken(authResponse.getCode());
      return "{\"type\":\"authorized_user\",\n" +
          "\"client_id\":\"481370185603-9es1nm32g1d3i8k4sv5r6a97s3a3aq78.apps.googleusercontent.com\",\n" +
          "\"project_id\":\"tattoo-go-site-1535546251007\",\n" +
          "\"access_token\": \"" + tokenResponse.getAccessToken() + "\",\n" +
          "\"client_secret\":\"4V-0cjy7yX4M58FCjP_WyNNA\",\n" +
          "\"refresh_token\":\"" + tokenResponse.getRefreshToken() + "\"}";
    }
  }

  private GoogleTokenResponse requestAccessToken(String code) throws IOException {
    try {
      GoogleTokenResponse response =
          new GoogleAuthorizationCodeTokenRequest(new NetHttpTransport(), new GsonFactory(),
              "481370185603-9es1nm32g1d3i8k4sv5r6a97s3a3aq78.apps.googleusercontent.com",
              "4V-0cjy7yX4M58FCjP_WyNNA",
              code,
              "http://localhost:8080/gphotos/callback")
              .execute();
      System.out.println("Access token: " + response.getAccessToken());
      System.out.println("Refresh token: " + response.getRefreshToken());
      return response;
    } catch (TokenResponseException e) {
      if (e.getDetails() != null) {
        System.err.println("Error: " + e.getDetails().getError());
        if (e.getDetails().getErrorDescription() != null) {
          System.err.println(e.getDetails().getErrorDescription());
        }
        if (e.getDetails().getErrorUri() != null) {
          System.err.println(e.getDetails().getErrorUri());
        }
      } else {
        System.err.println(e.getMessage());
      }
    }
    return null;
  }

}

