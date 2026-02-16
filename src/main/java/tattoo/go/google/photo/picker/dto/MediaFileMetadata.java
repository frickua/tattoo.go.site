package tattoo.go.google.photo.picker.dto;

import lombok.Data;

@Data
public class MediaFileMetadata {
    private Integer width;
    private Integer height;
    private String cameraMake;
    private String cameraModel;
    private PhotoMetadata photoMetadata;
}
