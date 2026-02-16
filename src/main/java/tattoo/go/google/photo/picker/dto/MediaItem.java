package tattoo.go.google.photo.picker.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class MediaItem {
    private String id;
    private Instant createTime;
    private MediaType type;
    private MediaFile mediaFile;
}
