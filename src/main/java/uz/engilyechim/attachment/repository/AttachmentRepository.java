package uz.engilyechim.attachment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.engilyechim.attachment.entity.Attachment;

public interface AttachmentRepository extends JpaRepository<Attachment,Long> {



}
