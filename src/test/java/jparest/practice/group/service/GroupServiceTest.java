package jparest.practice.group.service;

import jparest.practice.group.domain.Group;
import jparest.practice.group.domain.UserGroup;
import jparest.practice.group.dto.CreateGroupResponse;
import jparest.practice.group.dto.GetUserGroupResponse;
import jparest.practice.group.exception.GroupNotFoundException;
import jparest.practice.group.exception.UserGroupNotFoundException;
import jparest.practice.group.repository.GroupRepository;
import jparest.practice.group.repository.UserGroupRepository;
import jparest.practice.invite.domain.Invite;
import jparest.practice.invite.dto.InviteUserRequest;
import jparest.practice.invite.dto.InviteUserResponse;
import jparest.practice.invite.exception.InviteNotFoundException;
import jparest.practice.invite.repository.InviteRepository;
import jparest.practice.invite.service.InviteService;
import jparest.practice.user.domain.User;
import jparest.practice.user.service.UserAuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static jparest.practice.common.utils.fixture.GroupFixture.*;
import static jparest.practice.common.utils.fixture.UserFixture.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class GroupServiceTest {

    private User firstUser;
    private User secondUser;

    @Autowired
    UserAuthService userAuthService;

    @Autowired
    GroupService groupService;

    @Autowired
    GroupRepository groupRepository;

    @Autowired
    UserGroupRepository userGroupRepository;

    @Autowired
    InviteService inviteService;

    @Autowired
    InviteRepository inviteRepository;

    @BeforeEach
    void setUp() {
        firstUser = userAuthService.join(createFirstUser());
    }

    @Test
    public void 그룹생성() throws Exception {

        //given

        //when
        CreateGroupResponse response = groupService.createGroup(firstUser, groupName1);

        //then
        String saveGroupName = response.getGroupName();

        assertEquals(groupName1, saveGroupName, "생성한 그룹의 이름이 일치해야 한다.");
    }

    @Test
    public void 그룹탈퇴() throws Exception {

        //given
        Long saveGroupId = groupService.createGroup(firstUser, groupName1).getId();

        //when
        groupService.withdrawGroup(firstUser, saveGroupId);

        //then
        assertThrows(UserGroupNotFoundException.class, () -> findUserGroup(firstUser.getId(), saveGroupId));
    }

    @Test
    public void 그룹탈퇴_시_유저가_보낸_초대_모두_삭제() throws Exception {

        //given
        secondUser = userAuthService.join(createSecondUser());
        Long saveGroupId = groupService.createGroup(firstUser, groupName1).getId();
        InviteUserResponse response = inviteService.inviteToGroup(firstUser, new InviteUserRequest(secondUser.getId(), saveGroupId));

        //when
        groupService.withdrawGroup(firstUser, saveGroupId);

        //then
        assertAll(
                () -> assertThrows(UserGroupNotFoundException.class, () -> findUserGroup(firstUser.getId(), saveGroupId)),
                () -> assertThrows(InviteNotFoundException.class, () -> findInvite(response.getInviteId()))
        );
    }

    @Test
    public void 그룹_리스트_조회() throws Exception {

        //given
        Long id1 = groupService.createGroup(firstUser, groupName1).getId();
        Long id2 = groupService.createGroup(firstUser, groupName2).getId();

        Group group1 = findGroup(id1);
        Group group2 = findGroup(id2);

        //when
        List<GetUserGroupResponse> userGroupList = groupService.getUserGroupList(firstUser);
        GetUserGroupResponse result1 = userGroupList.get(0);
        GetUserGroupResponse result2 = userGroupList.get(1);

        //then
        assertAll(
                () -> assertEquals(group1.getId(), result1.getGroupId()),
                () -> assertEquals(group1.getGroupName(), result1.getGroupName()),
                () -> assertEquals(group1.getUserCount(), result1.getTotalUsers()),
                () -> assertEquals(group2.getId(), result2.getGroupId()),
                () -> assertEquals(group2.getGroupName(), result2.getGroupName()),
                () -> assertEquals(group2.getUserCount(), result2.getTotalUsers())
        );

    }

    private Group findGroup(Long groupId) {
        return groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException("groupId = " + groupId));
    }

    private UserGroup findUserGroup(UUID userId, Long groupId) {
        return userGroupRepository.findByUserIdAndGroupId(userId, groupId)
                .orElseThrow(() -> new UserGroupNotFoundException("userId = " + userId + ", groupId = " + groupId));
    }

    private Invite findInvite(Long inviteId) {
        return inviteRepository.findById(inviteId)
                .orElseThrow(() -> new InviteNotFoundException("inviteId = " + inviteId));
    }
}
