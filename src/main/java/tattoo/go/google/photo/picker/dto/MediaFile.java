package tattoo.go.google.photo.picker.dto;

import lombok.Data;

@Data
public class MediaFile {
    private String baseUrl;
    private String mimeType;
    private MediaFileMetadata mediaFileMetadata;
    private String filename;
}
