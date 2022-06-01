package uz.engilyechim.attachment.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor@NoArgsConstructor@Data
public class AttachmentContentDto {
    private Long attachmentId;
    private byte[] bytes;
}
