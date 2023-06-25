package jparest.practice.group.controller;

import jparest.practice.common.utils.RestDocsTestSupport;
import jparest.practice.group.dto.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jparest.practice.common.utils.fixture.GroupFixture.groupName1;
import static jparest.practice.common.utils.fixture.UserFixture.nickname1;
import static jparest.practice.common.utils.fixture.UserFixture.userId;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
public class GroupControllerTest extends RestDocsTestSupport {

    private final String GROUP_API = "/api/groups";

    @Test
    @DisplayName("그룹 리스트 조회")
    void get_groups() throws Exception {

        //given
        GetGroupUserResponse response = GetGroupUserResponse.builder()
                .groupId(1L)
                .groupName(groupName1)
                .totalUsers(1)
                .build();

        List<GetGroupUserResponse> getGroupUserRespons = new ArrayList<>();

        getGroupUserRespons.add(response);

        given(groupService.getGroupUserList(any()))
                .willReturn(getGroupUserRespons);

        //when
        ResultActions result = mockMvc.perform(
                get(GROUP_API)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        result
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.result.[0].groupId").value(1L),
                        jsonPath("$.result.[0].groupName").value(groupName1),
                        jsonPath("$.result.[0].totalUsers").value(1)
                )
                .andDo(restDocs.document(
                        responseFields(
                                fieldWithPath("success").description("성공 여부"),
                                fieldWithPath("result.[].groupId").description("그룹 아이디"),
                                fieldWithPath("result.[].groupName").description("그룹 이름"),
                                fieldWithPath("result.[].totalUsers").description("그룹 인원수")
                        )));
    }

    @Test
    @DisplayName("그룹 생성")
    void add_groups() throws Exception {

        //given
        CreateGroupRequest createGroupRequest = CreateGroupRequest.builder()
                .groupName(groupName1)
                .isPublic(true)
                .build();

        String content = objectMapper.writeValueAsString(createGroupRequest);

        given(groupService.createGroup(any(), any()))
                .willReturn(CreateGroupResponse.builder()
                        .id(1L)
                        .groupName(groupName1)
                        .build());

        //when
        ResultActions result = mockMvc.perform(
                post(GROUP_API)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );

        //then
        result
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.result.groupName").value(groupName1)
                )
                .andDo(restDocs.document(
                        requestFields(
                                fieldWithPath("groupName").description("그룸 이름"),
                                fieldWithPath("isPublic").description("공개 여부")
                        ),
                        responseFields(
                                fieldWithPath("success").description("성공 여부"),
                                fieldWithPath("result.id").description("그룹 아이디"),
                                fieldWithPath("result.groupName").description("그룹 이름")
                        )));
    }

    @Test
    @DisplayName("그룹 탈퇴")
    void delete_groups_users() throws Exception {

        //given
        given(groupService.withdrawGroup(any(), any()))
                .willReturn(true);

        //when
        ResultActions result = mockMvc.perform(
                delete(GROUP_API + "/{groupId}/users", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        result
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.result").value(true)
                )
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("groupId").description("그룹 아이디")
                        ),
                        responseFields(
                                fieldWithPath("success").description("성공 여부"),
                                fieldWithPath("result").description("성공 여부")
                        )));
    }

    @Test
    @DisplayName("그룹 소유자 변경")
    void patch_owners() throws Exception {

        //given
        ChangeOwnerRequest changeOwnerRequest = ChangeOwnerRequest.builder()
                .successorId(userId)
                .build();

        String content = objectMapper.writeValueAsString(changeOwnerRequest);

        LocalDateTime currentTime = LocalDateTime.now();

        ChangeOwnerResponse changeOwnerResponse = ChangeOwnerResponse.builder()
                .ownerNickname(nickname1)
                .updatedAt(currentTime)
                .build();

        given(groupService.changeOwner(any(), any(), any()))
                .willReturn(changeOwnerResponse);

        //when
        ResultActions result = mockMvc.perform(
                patch(GROUP_API + "/{groupId}/owners", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );

        //then
        result
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.result.ownerNickname").value(nickname1)
                )
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("groupId").description("그룹 아이디")
                        ),
                        requestFields(
                            fieldWithPath("successorId").description("후임자 아이디")
                        ),
                        responseFields(
                                fieldWithPath("success").description("성공 여부"),
                                fieldWithPath("result.ownerNickname").description("그룹 소유자 닉네임"),
                                fieldWithPath("result.updatedAt").description("변경 시간")
                        )));
    }
}
