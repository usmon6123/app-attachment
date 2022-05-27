package uz.engilyechim.attachment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import uz.engilyechim.attachment.entity.Attachment;
import uz.engilyechim.attachment.exception.RestException;
import uz.engilyechim.attachment.mapper.AttachmentMapper;
import uz.engilyechim.attachment.payload.ApiResult;
import uz.engilyechim.attachment.payload.AttachmentByteIdDto;
import uz.engilyechim.attachment.payload.AttachmentInfoDto;
import uz.engilyechim.attachment.repository.AttachmentRepository;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Transactional
@Service
@RequiredArgsConstructor
public class AttachmentSystemServiceImpl implements AttachmentSystemService {

    private final AttachmentRepository attachmentRepository;
    private final AttachmentMapper attachmentMapper;
    @Value(value = "${UPLOAD_DIRECTORY}")
    String uploadDirectory;

    @Override
    public ApiResult<?> upload(MultipartHttpServletRequest request) {

        //REQUESTDAN FILE OLYAPMIZ
        MultipartFile file = requestToReturnFile(request);

        //ATTACHMENTNI SAQLASH UN PAPKALAR YO'LI EX :C/PDP/ATTACHMENT/FILES...
        String folder = collectFolder();

        //FILEDAN ATTACHMENT YASAB OLYAPMIZ
        Attachment attachment = createAttachmentToFile(file,folder);

        //ATTACHMENTNI MALUMOTLARINI DB GA SAQLAYAPDI
        attachmentRepository.save(attachment);

        //ATTACHMENTNI BAYTINI SAQLASH UCHUN YO'L YASAYDI
        Path path = createPath(attachment.getName(), folder);

        //ATTACHMENTNI BAYTINI SAQLANYAPDI
        fileSaveSystem(file,path);

        return ApiResult.successResponse("SUCCESS_UPLOAD_ATTACHMENT");
    }


    @Override
    public ApiResult<?> getOne(Long id, HttpServletResponse response) {

        //BAZADAN ATTACHMENT MALUMOTLARINI ID ORQALI OLIB KELADI AKS HOLDA EXCEPTIONGA OTAMIZ
        Attachment attachment = attachmentRepository.findById(id).orElseThrow(() -> new RestException("ATTACHMENT_NOT_FOUND", HttpStatus.NOT_FOUND));

        //ATTACHMENTNI BYTGA O'GIRIBERADI TOPOLMASA THROW QAYTARADI
        byte[] bytes = attachmentToBytes(attachment);

        //ATTACHMENTNI BYTE KO'RINISHIDA IDSI ORQALI QAYTARYAPMIZ
        return ApiResult.successResponse(new AttachmentByteIdDto(id,bytes));
    }

    @Override
    public ApiResult<?> getInfo(Long id) {

        //BAZADAN ATTACHMENT MALUMOTLARINI ID ORQALI OLIB KELADI AKS HOLDA EXCEPTIONGA OTAMIZ
        Attachment attachment = attachmentRepository.findById(id).orElseThrow(() -> new RestException("ATTACHMENT_NOT_FOUND", HttpStatus.NOT_FOUND));

        //ATTACHMENT ORQALI QAYTARILADIGAN DTONI YASABERADI
        AttachmentInfoDto attachmentInfoDto = attachmentMapper.attachmentToInfoDTo(attachment);

        return ApiResult.successResponse(attachmentInfoDto);
    }

    @Override
    public ApiResult<?> getAttachmentList(List<Long> ids) {

        //ID LISTDAGI IDLAR ORQALI BAZADAN ATTACHMENTLARNI OLIB KELIBERDAI
        List<Attachment> attachments = attachmentListByIds(ids);

        //ATTACHMENLAR LISTINI BYTE_ID_DTO LISTIGA O'GIRIB QAYTARIBERADI BITTA FAYLNI BO'LSADA SISTEMADAN TOPOLMASA THROW GA OTADI
        List<AttachmentByteIdDto> attachmentList = attachmentsToByteIdDtos(attachments);

        return ApiResult.successResponse(attachmentList);
    }


    @Override
    public ApiResult<?> getAttachmentListInfo(List<Long> ids) {

        //ID LISTDAGI IDLAR ORQALI BAZADAN ATTACHMENTLARNI OLIB KELIBERDAI
        List<Attachment> attachments = attachmentListByIds(ids);

        //ATTACHMENTLAR LISTI ORQALI DTOLIST YASABERADI
        List<AttachmentInfoDto> attachmentInfoDtoList = attachmentMapper.attachmentToDtoInfoList(attachments);

        //DTOLISTNI QAYTARYAPMIZ
        return ApiResult.successResponse(attachmentInfoDtoList);
    }

    @Override
    public ApiResult<?> delete(Long id) {

        //BAZADAN ATTACHMENTNI OLYAPMIZ;
        Attachment attachment = attachmentRepository.findById(id).orElseThrow(() -> new RestException("FILE_NOT_FOUND", HttpStatus.NOT_FOUND));

        // ATTACHMENT NI SYSTEMADAN O'CHIRIB TASHLAYDI AKS HOLDA EXCEPTION
        deleteFileFromSystem(attachment);

        //ATTACHMENTNI BAZADAN HAM O'CHIRIB TASHAYMIZ
        attachmentRepository.delete(attachment);

        return ApiResult.successResponse("DELETED_ATTACHMENT");}

    @Override
    public ApiResult<?> deleteList(List<Long> ids) {

        //ID LISTDAGI IDLAR ORQALI BAZADAN ATTACHMENTLARNI OLIB KELIBERDAI
        List<Attachment> attachmentList = attachmentListByIds(ids);

        //HAR BIR ID GA TEGISHLI FAYLLARNI SISTEMADAN O'CHIRYAPDI
        for (Attachment attachment : attachmentList) {
            deleteFileFromSystem(attachment);
        }

        //HAR BIR ID GA TEGISHLI FAYLLARNINGINFOSINI BAZADAN O'CHIRYAPDI
        attachmentRepository.deleteAllById(ids);

        return ApiResult.successResponse("DELETED_ATTACHMENT_LIST_BY_IDS");
    }


//--------------------------------YORDAMCHI METHODLAR-------------------------------------


    //ATTACHMENLAR LISTINI BYTE_ID_DTO LISTIGA O'GIRIB QAYTARIBERADI BITTA FAYLNI BO'LSADA SISTEMADAN TOPOLMASA THROW GA OTADI
    private List<AttachmentByteIdDto> attachmentsToByteIdDtos(List<Attachment> attachments) {

        return attachments.stream().map(attachment ->
                new AttachmentByteIdDto(
                        attachment.getId(),
                        attachmentToBytes(attachment)))
                .collect(Collectors.toList());

    }

    //ATTACHMENTNI BYTGA O'GIRIBERADI TOPOLMASA THROW QAYTARADI
    private byte[] attachmentToBytes(Attachment attachment) {
        try {
            File file = new File(attachment.getPath());
            Path path = Paths.get(file.getPath());
            return Files.readAllBytes(path);
        } catch (IOException e) {
            throw RestException.notFound(("FILE"));
        }
    }

    //ID LISTDAGI IDLAR ORQALI BAZADAN ATTACHMENTLARNI OLIB KELIBERDAI
   private List<Attachment> attachmentListByIds(List<Long> ids){
        return attachmentRepository.findAllById(ids);

   }

//          ------------------/upload open-------------------

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

    //FILEDAN ATTACHMENT YASABERADI
    private Attachment createAttachmentToFile(MultipartFile file, String folder) {

        Attachment attachment = new Attachment();
        String originalFilename = file.getOriginalFilename();

        attachment.setContentType(file.getContentType());
        attachment.setOriginalName(originalFilename);

        //SYSTEMAGA(PAPKA ICHIGA) FILE UN UNIQUE NAME YASAB olyapmiz
        String fileName = createFileName(originalFilename);

        attachment.setName(fileName);

        //FOLDER BU RASM TURGAN PAPGAGACHA BO'LGAN YO'L EX :C/PDP.ATTTACHMENT/FILES/2022...
        attachment.setPath(folder+"/"+fileName);

        return attachment;
    }

    // BERILGAN PATH GA KO'RSATILGAN YO'LGA FILENI BYTE[] NI SAQLAYDI.SAQLOLMASA EXCEPTIONGA OTILADI
    private void fileSaveSystem(MultipartFile file, Path path) {
        try {
            Files.copy(file.getInputStream(), path);
        } catch (IOException e) {
            throw RestException.exception("FILE_GET_INPUTSTREM", HttpStatus.BAD_REQUEST);
        }
    }

    // BU METHOD PATH YASAB BERADI. YA'NI FILENI PAPKALAR ICHIGA TAXLAB SAQLAYMIZ      EX:  =>  D:\PDP\ATTACHMENT\KETMON.MP3
    private Path createPath(String fileName,String folder) {

        // BERILGAN YOL BOYICHA PAPKALARNI OCHIB BERADI
        createFolder(folder);

        String pathString = folder + "/" + fileName;

        Path path = Paths.get(pathString);

        return path;
    }

    // BERILGAN YOL BOYICHA PAPKALARNI OCHIB BERADI
    private void createFolder(String folder) {
        File file = new File(folder);
        file.mkdirs();
    }

    //FILE SAQLANADIGAN YO'LNI YOZIBERADI   EX:  =>  C:\PDP\ATTACHMENT
    private String collectFolder() {

        DateFormat dateFormat = new SimpleDateFormat("MMMMMMM");
        Calendar calendar = Calendar.getInstance();

        String year = calendar.get(Calendar.YEAR) + "";
        String month = dateFormat.format(new Date());
        String day = calendar.get(Calendar.DAY_OF_MONTH) + "";
        String hour = calendar.get(Calendar.HOUR_OF_DAY) + "";
        String folder = uploadDirectory + "/" + year + "/" + month + "/" + day + "/" + hour;

        return folder;

    }

    //    BU METHOD SYSTEMAGA(PAPKA ICHIGA) FILE UN UNIQUE NAME YASAB BERADI
    private String createFileName(String originalFilename) {

        //SYSTEMADA HAR BIR FILE UNIQUE LIGINI TAMINLASH UCHUN
        String name = UUID.randomUUID().toString();

        //FARMATINI OLYAPMIZ  jpeg; png; mp4
        String[] split = originalFilename.split("\\.");
        String contentType = split[split.length - 1];

        name = name + "." + contentType;
        return name;
    }

//          ------------------/upload close-------------------


    // ATTACHMENT NI SYSTEMADAN O'CHIRIB TASHLAYDI AKS HOLDA EXCEPTION
    private void deleteFileFromSystem(Attachment attachment) {
        try {
            Files.delete(Path.of(attachment.getPath()));
        } catch (IOException e) {
            throw RestException.notFound(("FILE"));
        }

    }







}
