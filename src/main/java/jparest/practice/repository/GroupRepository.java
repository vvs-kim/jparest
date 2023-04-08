package jparest.practice.repository;

import jparest.practice.domain.Group;
import jparest.practice.domain.GroupMember;
import jparest.practice.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class GroupRepository {

    private final EntityManager em;

    public void save(Group group) {
        if (group.getId() == null) {
            em.persist(group);
        } else {
            em.merge(group);
        }
    }

    public Group findOne(Long id) {
        return em.find(Group.class, id);
    }

    public GroupMember findByGroupIdAndMemberId(Long groupId, Long memberId) {
        return em.createQuery("select g from GroupMember g where g.groupId = :groupId and :g.memberId = :memberId",
                        GroupMember.class)
                .setParameter("groupId", groupId)
                .setParameter("memberID", memberId)
                .getSingleResult();
    }

}
