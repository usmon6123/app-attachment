package uz.engilyechim.attachment.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorData {
    private String errorMsg;
    private Integer errorCode;
}
