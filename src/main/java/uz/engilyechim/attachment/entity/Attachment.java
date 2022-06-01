package uz.engilyechim.attachment.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uz.engilyechim.attachment.entity.template.AbsLongEntity;

import javax.persistence.Column;
import javax.persistence.Entity;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Attachment extends AbsLongEntity {

    private String originalName;//car.jpg lorem.png

    // @Column(nullable = false,unique = true)
    private String name;  // papkani ichiga saqlash un

    private Long size;

    private String contentType;

    //    BU FILE SYSTEMGA SAQLAGANDA KK BOLADI  // papkani ichidan topish un
    @Column(nullable = false)
    private String path;    // PAPKANI ICHIDAN TURGAN JOYIGACHA SAQLAYDI EX: C:\UZ\PDP\ATTACHMENT\FILES\SERDT5666TGS.JPEG
}
