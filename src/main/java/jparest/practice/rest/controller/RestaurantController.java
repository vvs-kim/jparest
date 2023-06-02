package jparest.practice.rest.controller;

import jparest.practice.auth.security.CurrentUser;
import jparest.practice.common.util.ApiResult;
import jparest.practice.common.util.ApiUtils;
import jparest.practice.rest.dto.AddFavoriteRestRequest;
import jparest.practice.rest.dto.GetFavRestListResponse;
import jparest.practice.rest.service.RestService;
import jparest.practice.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/restaurants")
public class RestaurantController {

    private final RestService restService;

    @PostMapping("/{restId}/favorite")
    public ApiResult<Boolean> addFavRest(@CurrentUser User user,
                                              @PathVariable String restId,
                                              @RequestBody AddFavoriteRestRequest addFavoriteRestRequest
                                              ) {
        return ApiUtils.success(restService.addFavRest(user, addFavoriteRestRequest.getGroupId(), restId, addFavoriteRestRequest.getRestName(), addFavoriteRestRequest.getLatitude(), addFavoriteRestRequest.getLongitude()));
    }

    @DeleteMapping("/{restId}/favorite")
    public ApiResult<Boolean> deleteFavRest(@CurrentUser User user,
                                            @PathVariable String restId,
                                            @RequestParam Long groupId
    ) {

        return ApiUtils.success(restService.deleteFavRest(user, groupId, restId));
    }

    @GetMapping("/favorite")
    public ApiResult<List<GetFavRestListResponse>> getFavRestList(@CurrentUser User user,
                                                                  @RequestParam Long groupId
    ) {

        return ApiUtils.success(restService.getFavRestList(user, groupId));
    }
}
