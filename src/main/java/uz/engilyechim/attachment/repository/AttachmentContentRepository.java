package uz.engilyechim.attachment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.engilyechim.attachment.entity.AttachmentContent;

import java.util.Optional;

public interface AttachmentContentRepository extends JpaRepository<AttachmentContent,Long> {

    Optional<AttachmentContent> findByAttachmentId(Long attachment_id);


    void deleteByAttachmentId(Long attachment_id);
}
