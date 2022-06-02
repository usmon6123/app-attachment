package uz.engilyechim.attachment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import uz.engilyechim.attachment.entity.Attachment;
import uz.engilyechim.attachment.entity.AttachmentContent;
import uz.engilyechim.attachment.exception.RestException;
import uz.engilyechim.attachment.mapper.AttachmentMapper;
import uz.engilyechim.attachment.payload.ApiResult;
import uz.engilyechim.attachment.payload.AttachmentContentDto;
import uz.engilyechim.attachment.payload.AttachmentInfoByDbDto;
import uz.engilyechim.attachment.repository.AttachmentContentRepository;
import uz.engilyechim.attachment.repository.AttachmentRepository;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttachmentDbServiceImpl implements AttachmentDbService {

    private final AttachmentRepository attachmentRepository;
    private final AttachmentContentRepository attachmentContentRepository;
    private final AttachmentMapper attachmentMapper;

    @Override
    public ApiResult<?> upload(MultipartHttpServletRequest request) {

        //REQUESTDAN FILE OLYAPMIZ
        MultipartFile file = requestToReturnFile(request);

        //FILEDAN ATTACHMENT YASABERADI
        Attachment attachment = createAttachmentToFile(file);

        //ATTACHMENTNI MALUMOTLARINI DB GA SAQLAYAPDI
        Attachment savedAttachment = attachmentRepository.save(attachment);

        //ATTACHMENTNI CONTENTINI(BYTENI) BAZAGA SAQLABERADI
        savedAttachmentContentToDb(file, savedAttachment);

        return ApiResult.successResponse("SUCCESS_UPLOAD_ATTACHMENT id = " + savedAttachment.getId());
    }


    @Override
    public ApiResult<?> getContent(Long id, HttpServletResponse response,String view) {

        //ATTTACHMENTNING CONTENTINI QAYTARADIGAN METHOD
        if (getAttachmentContent(id,response,view))
        return ApiResult.successResponse();
        return new ApiResult<>("ATTACHMENT_NOT_FOUND", HttpStatus.NOT_FOUND.value());
    }

    @Override//ATTACHMENTNI BYTE KO'RINISHDA QAYTARADI
    public ApiResult<?> getContentInByte(Long id) {
        AttachmentContent attachmentContent = attachmentContentRepository.findByAttachmentId(id).orElseThrow(() -> new RestException("ATTACHMENT_NOT_FOUND", HttpStatus.NOT_FOUND));
        return ApiResult.successResponse(new AttachmentContentDto(attachmentContent.getAttachment().getId(), attachmentContent.getBytes()));
    }

    @Override
    public ApiResult<?> getInfo(Long id) {

        //ID ORQALI BAZADAGI ATTACHMENTNI QAYTARADI AKS HOLDA THROW
        Attachment attachment = returnAttachment(id);

        //ATTACHMENTDAN BIZGA KERAK FIELDLARNI OLIB DTO YASABERADI
        AttachmentInfoByDbDto attachmentInfoByDbDto = attachmentMapper.attachmentToDto(attachment);

        return ApiResult.successResponse(attachmentInfoByDbDto);
    }

    @Override
    public ApiResult<?> getContentList(List<Long> ids) {

        //IDLAR BO'YICHA BAZADAN ATTACHMENT_CONTENTLARNI OLIB KELIBERADI
        List<AttachmentContent> attachmentContents = returnAttachmentContentList(ids);

        //CONTENTNI DTOGA O'GIRIB LISTGA YI'G'YAPDI
        List<AttachmentContentDto> contentDtoList = attachmentContents.stream().map(attachmentContent ->
                new AttachmentContentDto(
                        attachmentContent.getAttachment().getId(),
                        attachmentContent.getBytes()
                )).collect(Collectors.toList());

        return ApiResult.successResponse(contentDtoList);
    }

    @Override
    public ApiResult<?> getInfoList(List<Long> ids) {

        //IDLAR BO'YICHA BAZADAN ATTACHMENTLARNI OLIB KELIBERADI
        List<Attachment> attachments = returnAttachmentList(ids);

        if (attachments.isEmpty())return ApiResult.successResponse(new ArrayList<>());

        List<AttachmentInfoByDbDto> infoByDbDtos = attachments.stream().map(attachmentMapper::attachmentToDto).collect(Collectors.toList());

        return ApiResult.successResponse(infoByDbDtos);
    }

    @Override
    public ApiResult<?> delete(Long id) {
        attachmentRepository.findById(id).orElseThrow(() -> new RestException("ATTACHMENT_NOT_FOUND",HttpStatus.NOT_FOUND));
        attachmentRepository.deleteById(id);
        return ApiResult.successResponse("DELETED_ATTACHMENT");
    }

    @Override
    public ApiResult<?> deleteList(List<Long> ids) {
        List<Attachment> attachmentList = attachmentRepository.findAllById(ids);
        attachmentRepository.deleteAll(attachmentList);
        return ApiResult.successResponse("DELETED_ATTACHMENT_LIST");
    }

    //ID ORQALI BAZADAGI ATTACHMENTNI QAYTARADI AKS HOLDA THROW
    private Attachment returnAttachment(Long id){
        return attachmentRepository.findById(id).orElseThrow(()->new RestException("ATTACHMENT_NOT_FOUND", HttpStatus.NOT_FOUND));
    }



    //IDLAR BO'YICHA BAZADAN ATTACHMENTLARNI OLIB KELIBERADI
    private List<Attachment> returnAttachmentList(List<Long> ids){
        return attachmentRepository.findAllById(ids);
    }


    //IDLAR BO'YICHA BAZADAN ATTACHMENT_CONTENTLARNI OLIB KELIBERADI
    private List<AttachmentContent> returnAttachmentContentList(List<Long> ids){
        return attachmentContentRepository.findAllByIds(ids);
    }

    //ATTTACHMENTNING CONTENTINI QAYTARADIGAN METHOD
    private boolean getAttachmentContent(Long id, HttpServletResponse response,String view){
        try {
            String attachmentOrInline = "";
            // @requestParamda view=open kelsa fileni browserda ochib beramz.
            // aks holda browserga file yuklanadi
            if (view != null && !view.isBlank() && view.equalsIgnoreCase("open")) {
                attachmentOrInline = "inline";
            } else {
                attachmentOrInline = "attachment";
            }
            Attachment attachment = attachmentRepository.findById(id).orElseThrow(() -> new RestException("ATTACHMENT_NOT_FOUND", HttpStatus.NOT_FOUND));
            AttachmentContent attachmentContent = attachmentContentRepository.findByAttachmentId(id).orElseThrow(() -> new RestException("ATTACHMENT_CONTENT_NOT_FOUND", HttpStatus.NOT_FOUND));
            response.setHeader("Content-Disposition",attachmentOrInline+"; filename=\""+attachment.getOriginalName()+"\"");
            response.setContentType(attachment.getContentType());
            FileCopyUtils.copy(attachmentContent.getBytes(),response.getOutputStream());
        }catch (Exception e){
            return false;
        }
        return true;
    }

    //ATTACHMENTNI CONTENTINI(BYTENI) BAZAGA SAQLABERADI
    public void savedAttachmentContentToDb(MultipartFile file, Attachment attachment) {
        try {
            AttachmentContent attachmentContent = new AttachmentContent(file.getBytes(), attachment);
            attachmentContentRepository.save(attachmentContent);
        } catch (Exception e) {
            throw RestException.exception("FILE_BYTE_NOT_FOUND", HttpStatus.NOT_FOUND);
        }
    }

    // REQUESTDAN FILENI OLIBERADI AKS HOLDA THROW
    private MultipartFile requestToReturnFile(MultipartHttpServletRequest request) {

        //REQUESTDAN KELGAN FILENI OLISHLIK UCHUN UNING NOMI OLINADI OLDIN
        Iterator<String> fileNames = request.getFileNames();

        //FILE NOMSIZ KELSA EXSEPTIONGA OTAMIZ
        if (!fileNames.hasNext()) throw RestException.exception("FILE_NAME_REQUIRED", HttpStatus.BAD_REQUEST);

        MultipartFile file = request.getFile(fileNames.next());

        //FILE BO'SH BO'LSA EXSEPTIONGA OTAMIZ
        if (file == null) throw RestException.notFound("FILE");

        return file;
    }

    //FILEDAN ATTACHMENTGA MA'LUMOT SET QILIB QAYTARADI
    private Attachment createAttachmentToFile(MultipartFile file) {

        Attachment attachment = new Attachment();
        String originalFilename = file.getOriginalFilename();

        attachment.setContentType(file.getContentType());
        attachment.setOriginalName(originalFilename);
        attachment.setSize(file.getSize());

        //ATTACHMENTNI SYSTEMAGA SAQLASHIMIZ PATHNI KIRITISHNI MAJBURIY QILGANIMIZ UCHUN BU YERDA HAM KIRITIB KETYAPMIZ, ASLIDA BAZAGA SAQLAGANDA PATH KERAK BO'LMAYDI
        attachment.setPath("postgres");
        return attachment;
    }


}
