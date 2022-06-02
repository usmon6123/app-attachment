package uz.engilyechim.attachment.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AttachmentInfoByDbDto {

    private Long id;

    private String originalName;

    private String contentType;

    private Long size;

}
