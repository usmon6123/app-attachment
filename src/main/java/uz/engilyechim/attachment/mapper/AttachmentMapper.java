package uz.engilyechim.attachment.mapper;


import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import uz.engilyechim.attachment.entity.Attachment;
import uz.engilyechim.attachment.payload.AttachmentInfoByDbDto;
import uz.engilyechim.attachment.payload.AttachmentInfoDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AttachmentMapper {

    //ATTACHMENT MALUMOTLARI ASOSIDA DTO YASABERADI
    @Named(value = "useMe")
    AttachmentInfoDto attachmentToInfoDTo(Attachment attachment);

    @IterableMapping(qualifiedByName = "useMe")
    List<AttachmentInfoDto> attachmentToDtoInfoList(List<Attachment> attachments);

    AttachmentInfoByDbDto attachmentToDto(Attachment attachment);

}
