package uz.engilyechim.attachment.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uz.engilyechim.attachment.entity.template.AbsLongEntity;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

@EqualsAndHashCode(callSuper = true)
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AttachmentContent extends AbsLongEntity {

    private byte[] bytes;

    @OneToOne
    private Attachment attachment;

}
