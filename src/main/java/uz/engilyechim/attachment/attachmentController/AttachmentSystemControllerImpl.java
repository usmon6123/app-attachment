package uz.engilyechim.attachment.attachmentController;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import uz.engilyechim.attachment.payload.ApiResult;
import uz.engilyechim.attachment.service.AttachmentSystemService;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController()
@RequiredArgsConstructor
public class AttachmentSystemControllerImpl implements AttachmentSystemController {

    private final AttachmentSystemService attachmentSystemService;

    @Override
    public ApiResult<?> upload(MultipartHttpServletRequest request) {

        return attachmentSystemService.upload(request);
    }

    @Override
    public ApiResult<?> getOne(Long id, HttpServletResponse response) {
        return attachmentSystemService.getOne(id,response);
    }

    @Override
    public ApiResult<?> getInfo(Long id) {
        return attachmentSystemService.getInfo(id);
    }

    @Override
    public ApiResult<?> getAttachmentList(List<Long> ids) {
        return attachmentSystemService.getAttachmentList(ids);
    }

    @Override
    public ApiResult<?> getAttachmentListInfo(List<Long> ids) {
        return attachmentSystemService.getAttachmentListInfo(ids);
    }

    @Override
    public ApiResult<?> delete(Long id) {
        return attachmentSystemService.delete(id);
    }

    @Override
    public ApiResult<?> deleteList(List<Long> ids) {
        return attachmentSystemService.deleteList(ids);
    }
}
