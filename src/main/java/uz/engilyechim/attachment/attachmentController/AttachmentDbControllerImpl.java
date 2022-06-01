package uz.engilyechim.attachment.attachmentController;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import uz.engilyechim.attachment.payload.ApiResult;
import uz.engilyechim.attachment.service.AttachmentDbService;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController()
@RequiredArgsConstructor
public class AttachmentDbControllerImpl implements AttachmentDbController {
    private final AttachmentDbService attachmentDbService;
    @Override
    public ApiResult<?> upload(MultipartHttpServletRequest request) {
        return attachmentDbService.upload(request);
    }

    @Override
    public ApiResult<?> getContent(Long id, HttpServletResponse response,String view) {
        return attachmentDbService.getContent(id,response,view);
    }

    @Override
    public ApiResult<?> getContentInByte(Long id) {
        return attachmentDbService.getContentInByte(id);
    }

    @Override
    public ApiResult<?> getInfo(Long id) {
        return attachmentDbService.getInfo(id);
    }

    @Override
    public ApiResult<?> getAttachmentContentList(List<Long> ids) {
        return attachmentDbService.getContentList(ids);
    }

    @Override
    public ApiResult<?> getAttachmentListInfo(List<Long> ids) {
        return attachmentDbService.getInfoList(ids);
    }

    @Override
    public ApiResult<?> delete(Long id) {
        return attachmentDbService.delete(id);
    }

    @Override
    public ApiResult<?> deleteList(List<Long> ids) {
        return attachmentDbService.deleteList(ids);
    }
}
