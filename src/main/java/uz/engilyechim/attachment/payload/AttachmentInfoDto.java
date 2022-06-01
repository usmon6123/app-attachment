package uz.engilyechim.attachment.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AttachmentInfoDto {

    private Long id;

    private String name;

    private String originalName;

    private String contentType;

    private String path;

    private Long size;

    public AttachmentInfoDto(String originalName, String contentType, Long size) {
        this.originalName = originalName;
        this.contentType = contentType;
        this.size = size;
    }
}
