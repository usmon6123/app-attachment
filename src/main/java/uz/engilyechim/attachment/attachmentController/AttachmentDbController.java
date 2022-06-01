package uz.engilyechim.attachment.attachmentController;


import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import uz.engilyechim.attachment.payload.ApiResult;
import uz.engilyechim.attachment.utils.AppConstant;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static uz.engilyechim.attachment.attachmentController.AttachmentDbController.ATTACHMENT_DB_CONTROLLER;

@RequestMapping(path = ATTACHMENT_DB_CONTROLLER)
public interface AttachmentDbController {

    String ATTACHMENT_DB_CONTROLLER = AppConstant.BASE_PATH + "/attachment-db";

    @PostMapping("/upload")
    ApiResult<?> upload(MultipartHttpServletRequest request);

    @GetMapping("/get-one/{id}")//PARAMDA OPEN BERIB YUBORILSA RASMNI BROUZERDA OCHIBERAMIZ, AKS HOLDA YUKLABERAMIZ
    ApiResult<?> getContent(@PathVariable Long id, HttpServletResponse response,@RequestParam String view);

    //ATTACHMENTNI BYTE KO'RINISHDA QAYTARADI
    @GetMapping("/get-one-in-byte/{id}")
    ApiResult<?> getContentInByte(@PathVariable Long id);

    @GetMapping("/get-attachment-info/{id}")
    ApiResult<?> getInfo(@PathVariable Long id);

    @GetMapping("/get-attachment-list")
    ApiResult<?> getAttachmentContentList(@RequestBody List<Long> ids);

    @GetMapping("/get-attachment-list-info")
    ApiResult<?> getAttachmentListInfo(@RequestBody List<Long> ids);

    @DeleteMapping("/delete/{id}")
    ApiResult<?> delete(@PathVariable Long id);

    @DeleteMapping("/delete-list")
    ApiResult<?> deleteList(@RequestBody List<Long> ids);
}
