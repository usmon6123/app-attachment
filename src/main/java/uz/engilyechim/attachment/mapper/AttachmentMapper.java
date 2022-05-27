package uz.engilyechim.attachment.mapper;


import org.mapstruct.Mapper;
import uz.engilyechim.attachment.entity.Attachment;
import uz.engilyechim.attachment.payload.AttachmentInfoDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AttachmentMapper {

    //ATTACHMENT MALUMOTLARI ASOSIDA DTO YASABERADI
    AttachmentInfoDto attachmentToInfoDTo(Attachment attachment);

    List<AttachmentInfoDto> attachmentToDtoInfoList(List<Attachment> attachments);

}
