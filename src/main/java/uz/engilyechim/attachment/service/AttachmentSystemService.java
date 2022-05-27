package uz.engilyechim.attachment.service;


import org.springframework.web.multipart.MultipartHttpServletRequest;
import uz.engilyechim.attachment.payload.ApiResult;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface AttachmentSystemService {


    ApiResult<?> upload(MultipartHttpServletRequest request);

    ApiResult<?> getOne(Long id, HttpServletResponse response);

    ApiResult<?> getInfo(Long id);

    ApiResult<?> getAttachmentList(List<Long> ids);

    ApiResult<?> getAttachmentListInfo(List<Long> ids);

    ApiResult<?> delete(Long id);

    ApiResult<?> deleteList(List<Long> ids);
}
