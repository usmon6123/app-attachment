package uz.engilyechim.attachment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uz.engilyechim.attachment.entity.AttachmentContent;

import java.util.List;
import java.util.Optional;

public interface AttachmentContentRepository extends JpaRepository<AttachmentContent,Long> {

    Optional<AttachmentContent> findByAttachmentId(Long attachment_id);

    @Query(value = "select * from attachment_content as ac where\n" +
            "            attachment_id in(select id from attachment where id in(:ids))",nativeQuery = true)
    List<AttachmentContent> findAllByIds(List<Long> ids);

    void deleteByAttachmentId(Long attachment_id);
}
