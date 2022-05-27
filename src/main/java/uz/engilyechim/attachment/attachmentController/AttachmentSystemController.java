package uz.engilyechim.attachment.attachmentController;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import uz.engilyechim.attachment.payload.ApiResult;
import uz.engilyechim.attachment.utils.AppConstant;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static uz.engilyechim.attachment.attachmentController.AttachmentSystemController.ATTACHMENT_SYSTEM_CONTROLLER;

@RequestMapping(path = ATTACHMENT_SYSTEM_CONTROLLER)
public interface AttachmentSystemController {
    String ATTACHMENT_SYSTEM_CONTROLLER = AppConstant.BASE_PATH + "/attachment-system";


    @PostMapping("/upload")
    ApiResult<?> upload(MultipartHttpServletRequest request);

    @GetMapping("/get-one/{id}")
    ApiResult<?>getOne(@PathVariable Long id, HttpServletResponse response);

    @GetMapping("/get-attachment-info/{id}")
    ApiResult<?>getInfo(@PathVariable Long id);

    @GetMapping("/get-attachment-list")
    ApiResult<?>getAttachmentList(@RequestBody List<Long> ids);

    @GetMapping("/get-attachment-list-info")
    ApiResult<?>getAttachmentListInfo(@RequestBody List<Long> ids);

    @DeleteMapping("/delete/{id}")
    ApiResult<?>delete(@PathVariable Long id);

    @DeleteMapping("/delete-list")
    ApiResult<?>deleteList(@RequestBody List<Long>ids);
}
