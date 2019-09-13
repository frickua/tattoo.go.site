package tattoo.go.controller;

import com.google.api.client.auth.oauth2.AuthorizationCodeResponseUrl;
import com.google.api.client.auth.oauth2.TokenResponseException;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import tattoo.go.dto.GallaryImageDto;
import tattoo.go.service.TattooImgService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Controller
public class GallaryController {

  @Autowired
  private TattooImgService tattooImgService;

  @ResponseBody
  @RequestMapping("/head/images")
  public List<GallaryImageDto> getHeadImages() throws IOException {
    return tattooImgService.getImagesFromGPhotos(12);
//    return tattooImgService.getRandomizedImagesUrls(12);
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
          new GoogleAuthorizationCodeTokenRequest(new NetHttpTransport(), new JacksonFactory(),
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

