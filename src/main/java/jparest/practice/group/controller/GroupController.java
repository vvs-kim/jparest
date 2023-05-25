package jparest.practice.group.controller;

import jparest.practice.auth.security.CurrentUser;
import jparest.practice.common.util.ApiResult;
import jparest.practice.common.util.ApiUtils;
import jparest.practice.group.domain.Group;
import jparest.practice.group.dto.CreateGroupRequest;
import jparest.practice.group.dto.CreateGroupResponse;
import jparest.practice.group.service.GroupService;
import jparest.practice.group.service.GroupServiceImpl;
import jparest.practice.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/group")
public class GroupController {
    private final GroupService groupService;

    @PostMapping
    public ApiResult<CreateGroupResponse> createGroup(@CurrentUser User user, @RequestBody CreateGroupRequest createGroupRequest) {
        Group group = groupService.createGroup(user, createGroupRequest.getGroupName());
        CreateGroupResponse response = CreateGroupResponse.builder()
                .id(group.getId())
                .groupName(group.getGroupName())
                .build();

        return ApiUtils.success(response);
    }

    @DeleteMapping("/{groupId}/user")
    public ApiResult<Boolean> withdrawGroup(@CurrentUser User user, @PathVariable Long groupId) {
        return ApiUtils.success(groupService.withdrawGroup(user, groupId));
    }
}
