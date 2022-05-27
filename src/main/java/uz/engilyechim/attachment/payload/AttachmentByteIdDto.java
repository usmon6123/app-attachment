package uz.engilyechim.attachment.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttachmentByteIdDto {

    private Long id;

    private byte[] bytes;
}
