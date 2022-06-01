package uz.engilyechim.attachment.service;

import org.springframework.web.multipart.MultipartHttpServletRequest;
import uz.engilyechim.attachment.payload.ApiResult;

import javax.servlet.http.HttpServletResponse;
import java.util.List;


public interface AttachmentDbService {


    ApiResult<?> upload(MultipartHttpServletRequest request);

    ApiResult<?> getContent(Long id, HttpServletResponse response,String view);

    ApiResult<?> getInfo(Long id);

    ApiResult<?> getContentList(List<Long> ids);

    ApiResult<?> getInfoList(List<Long> ids);

    ApiResult<?> delete(Long id);

    ApiResult<?> deleteList(List<Long> ids);

    ApiResult<?> getContentInByte(Long id);

}
