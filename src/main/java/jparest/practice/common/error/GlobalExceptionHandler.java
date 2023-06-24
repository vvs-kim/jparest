package jparest.practice.common.error;

import jparest.practice.group.exception.ExistGroupUserException;
import jparest.practice.group.exception.GroupAccessDeniedException;
import jparest.practice.group.exception.GroupNotFoundException;
import jparest.practice.group.exception.GroupUserNotFoundException;
import jparest.practice.invite.exception.AlreadyProcessedInviteException;
import jparest.practice.invite.exception.ExistInviteForUserException;
import jparest.practice.invite.exception.InviteNotFoundException;
import jparest.practice.rest.exception.ExistGroupRestException;
import jparest.practice.rest.exception.GroupRestNotFoundException;
import jparest.practice.rest.exception.RestNotFoundException;
import jparest.practice.user.exception.ExistLoginIdException;
import jparest.practice.user.exception.LoginFailException;
import jparest.practice.user.exception.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // COMMON
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ErrorResponse> methodArgumentNotValidException(MethodArgumentNotValidException e) {
        FieldError error = e.getBindingResult().getFieldErrors().get(0);

        return getErrorResponseEntity(e,
                ErrorCode.METHOD_ARGUMENT_NOT_VALID,
                error.getDefaultMessage(),
                error.getField());
    }

    // USER
    @ExceptionHandler(ExistLoginIdException.class)
    ResponseEntity<ErrorResponse> existUserHandler(ExistLoginIdException e) {
        return getErrorResponseEntity(e, ErrorCode.LOGIN_FAIL);
    }

    @ExceptionHandler(LoginFailException.class)
    ResponseEntity<ErrorResponse> loginFailHandler(LoginFailException e) {
        return getErrorResponseEntity(e, ErrorCode.LOGIN_FAIL);
    }

    @ExceptionHandler(UserNotFoundException.class)
    ResponseEntity<ErrorResponse> userNotFoundHandler(UserNotFoundException e) {
        return getErrorResponseEntity(e, ErrorCode.USER_NOT_FOUND);
    }

    // GROUP
    @ExceptionHandler(GroupNotFoundException.class)
    ResponseEntity<ErrorResponse> groupNotFoundHandler(GroupNotFoundException e) {
        return getErrorResponseEntity(e, ErrorCode.GROUP_NOT_FOUND);
    }

    @ExceptionHandler(GroupUserNotFoundException.class)
    ResponseEntity<ErrorResponse> GroupUserNotFoundHandler(GroupUserNotFoundException e) {
        return getErrorResponseEntity(e, ErrorCode.GROUP_USER_NOT_FOUND);
    }

    @ExceptionHandler(ExistGroupUserException.class)
    ResponseEntity<ErrorResponse> existGroupUserHandler(ExistGroupUserException e) {
        return getErrorResponseEntity(e, ErrorCode.EXIST_GROUP_USER);
    }

    @ExceptionHandler(GroupAccessDeniedException.class)
    ResponseEntity<ErrorResponse> groupAccessDeniedHandler(GroupAccessDeniedException e) {
        return getErrorResponseEntity(e, ErrorCode.GROUP_ACCESS_DENIED);
    }

    // INVITE
    @ExceptionHandler(ExistInviteForUserException.class)
    ResponseEntity<ErrorResponse> existInviteForUserHandler(ExistInviteForUserException e) {

        return getErrorResponseEntity(e, ErrorCode.EXIST_INVITE_FOR_USER);
    }

    @ExceptionHandler(InviteNotFoundException.class)
    ResponseEntity<ErrorResponse> inviteNotFoundHandler(InviteNotFoundException e) {
        return getErrorResponseEntity(e, ErrorCode.INVITE_NOT_FOUND);
    }

    @ExceptionHandler(AlreadyProcessedInviteException.class)
    ResponseEntity<ErrorResponse> AlreadyProcessedInviteHandler(AlreadyProcessedInviteException e) {
        return getErrorResponseEntity(e, ErrorCode.ALREADY_PROCESSED_INVITE);
    }

    // REST
    @ExceptionHandler(ExistGroupRestException.class)
    ResponseEntity<ErrorResponse> existGroupRestHandler(ExistGroupRestException e) {
        return getErrorResponseEntity(e, ErrorCode.EXIST_GROUP_REST);
    }

    @ExceptionHandler(GroupRestNotFoundException.class)
    ResponseEntity<ErrorResponse> groupRestNotFoundHandler(GroupRestNotFoundException e) {
        return getErrorResponseEntity(e, ErrorCode.GROUP_REST_NOT_FOUND);
    }

    @ExceptionHandler(RestNotFoundException.class)
    ResponseEntity<ErrorResponse> restNotFoundHandler(RestNotFoundException e) {
        return getErrorResponseEntity(e, ErrorCode.REST_NOT_FOUND);
    }

    private static ResponseEntity<ErrorResponse> getErrorResponseEntity(Exception e, ErrorCode errorCode) {
        log.error("GlobalException = {} {}", e.getCause().getMessage(), e.getMessage());

        final ErrorResponse res = ErrorResponse.of(errorCode);
        return new ResponseEntity<>(res, HttpStatus.valueOf(errorCode.getStatus()));
    }

    private static ResponseEntity<ErrorResponse> getErrorResponseEntity(Exception e, ErrorCode errorCode, String message, String field) {
        log.error("GlobalException = {}", e.getMessage());

        final ErrorResponse res = ErrorResponse.of(errorCode, message, field);
        return new ResponseEntity<>(res, HttpStatus.valueOf(errorCode.getStatus()));
    }
}
