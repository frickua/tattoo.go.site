package tattoo.go.service;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.api.gax.rpc.ApiException;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.photos.library.v1.PhotosLibraryClient;
import com.google.photos.library.v1.PhotosLibrarySettings;
import com.google.photos.library.v1.internal.InternalPhotosLibraryClient;
import com.google.photos.types.proto.Album;
import com.google.photos.types.proto.MediaItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;
import tattoo.go.dto.GallaryImageDto;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class TattooImgService {

  @Autowired
  private ResourcePatternResolver resourcePatternResolver;

  @Deprecated
  public String[] getRandomizedImagesUrls(Integer limit) throws IOException {
    Resource[] tattoos = resourcePatternResolver.getResources("classpath*:/static/img-tattoos/*.jpg");
    List<Resource> tattoosList = Arrays.asList(tattoos);
    Collections.shuffle(tattoosList);
    limit = limit != null ? limit : tattoos.length;
    String[] images = new String[limit];
    for (int i = 0; i < limit; i++) {
      Resource resource = tattoosList.get(i);
      images[i] = "/img-tattoos/" + resource.getFilename();
    }
    return images;
  }

  public List<GallaryImageDto> getImagesFromGPhotos(Integer limit) throws IOException {
    List<GallaryImageDto> gallaryImageDtos = new ArrayList<>();
//		FileInputStream fis = new FileInputStream("C:\\sources\\tattoo.go.site\\cred.json");
    InputStream is = resourcePatternResolver.getResource("classpath:cred.json").getInputStream();

    GoogleCredentials credential = GoogleCredentials.fromStream(is);

    PhotosLibrarySettings settings = PhotosLibrarySettings.newBuilder()
        .setCredentialsProvider(FixedCredentialsProvider.create(credential))
        .build();
    try (PhotosLibraryClient photosLibraryClient =
             PhotosLibraryClient.initialize(settings)) {

      InternalPhotosLibraryClient.ListAlbumsPagedResponse albums = photosLibraryClient.listAlbums();
      String albumId = null;
      for (Album album : albums.iterateAll()) {
        String title = album.getTitle();
        if (title.equals("TattooGO")) {
          albumId = album.getId();
          break;
        }
      }
      if (albumId == null) {
        albumId = photosLibraryClient.createAlbum("TattooGO").getId();
      }
      InternalPhotosLibraryClient.SearchMediaItemsPagedResponse gPhotos = photosLibraryClient.searchMediaItems(albumId);
      for (MediaItem gPhoto : gPhotos.iterateAll()) {
        GallaryImageDto gallaryImageDto = new GallaryImageDto();
        gallaryImageDtos.add(gallaryImageDto);
        gallaryImageDto.setHeight(gPhoto.getMediaMetadata().getHeight());
        gallaryImageDto.setWidth(gPhoto.getMediaMetadata().getWidth());
        //setup max possible size of an image
        gallaryImageDto.setUrl(gPhoto.getBaseUrl() + "=w" + gallaryImageDto.getWidth() + "-h" + gallaryImageDto.getHeight());
        gallaryImageDto.setPreviewUrl(gPhoto.getBaseUrl());
        String description = gPhoto.getDescription();
        String[] descriptions = description.split("\n");
        if (descriptions.length == 4) {
          gallaryImageDto.setTitle(descriptions[0]);
          gallaryImageDto.setStyle(descriptions[1]);
          gallaryImageDto.setAuthor(descriptions[2]);
          gallaryImageDto.setPrice(Integer.valueOf(descriptions[3]));
        }
      }
    } catch (ApiException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    Collections.shuffle(gallaryImageDtos);
    if (limit != null && gallaryImageDtos.size() > limit) {
      // for head images
      gallaryImageDtos.subList(limit, gallaryImageDtos.size()).clear();
    }
    return gallaryImageDtos;
  }

}
