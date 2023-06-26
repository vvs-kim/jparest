package jparest.practice.user.controller;

import jparest.practice.auth.jwt.JwtService;
import jparest.practice.common.utils.RestDocsTestSupport;
import jparest.practice.common.utils.fixture.UserFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
public class UserInfoControllerTest extends RestDocsTestSupport {

    private final String USER_API = "/api/user";

    @MockBean
    JwtService jwtService;

    @Test
    @DisplayName("닉네임 중복 체크")
    public void get_nickname_duplicate() throws Exception {

        //given
        given(userInfoService.chkNickNameDuplicate(any())).willReturn(false);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("nickname", UserFixture.nickname1);

        //when
        ResultActions result = mockMvc.perform(
                get(USER_API + "/nickname/duplicate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .params(params)
        );

        //then
        result
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.result").value(false)
                )
                .andDo(restDocs.document(
                        requestParameters(
                                parameterWithName("nickname").description("변경할 닉네임")
                        ),
                        responseFields(
                                fieldWithPath("success").description("성공 여부"),
                                fieldWithPath("result").description("중복 여부")
                        )
                ));
    }
}
