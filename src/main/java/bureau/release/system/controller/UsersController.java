package bureau.release.system.controller;

import bureau.release.system.service.impl.UserService;
import bureau.release.system.service.dto.UserDto;
import bureau.release.system.service.dto.UsernameListDto;
import bureau.release.system.service.dto.error.ErrorDto;
import bureau.release.system.service.dto.error.ValidationErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Контроллер пользователей", description = "Управление пользователями (Admin rules only)")
public class UsersController {
    private final UserService userService;

    @GetMapping
    @Operation(
            summary = "Получение списка пользователей",
            description = "Позволяет получить список всех пользователей"
    )
    public List<UserDto> getAllUsers() {
        log.info("getAllUsers");
        return userService.getAllUsers();
    }

    @GetMapping("/{username}")
    @Operation(
            summary = "Получение пользователя по его username",
            description = "Позволяет получить данные о пользователе, исходя из переданного username",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешное получение"),
                    @ApiResponse(responseCode = "400", description = "Неправильный username",
                            content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Не найден пользователь по username",
                            content = @Content(schema = @Schema(implementation = ErrorDto.class)))
            }
    )
    public UserDto getUserByUsername(
            @PathVariable @Parameter(description = "Имя пользователя", example = "user") String username
    ) {
        log.info("GetUserByUsername: username={}", username);
        return userService.getUserByUsername(username);
    }

    @GetMapping("/authorities/{authority}")
    @Operation(
            summary = "Получение списка пользователей по доступу",
            description = "Позволяет получить список всех пользователей определенного доступа",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешное получение"),
                    @ApiResponse(responseCode = "400", description = "Правильный тип параметра, ошибка в значении",
                            content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class)))
            }
    )
    public UsernameListDto getUsernamesByAuthority(
            @PathVariable @Parameter(description = "Доступ пользователей", example = "user") String authority
    ) {
        log.info("GetUsernamesByAuthority: authority={}", authority);
        return userService.getUsernamesByAuthority(authority);
    }

    @PostMapping
    @Operation(
            summary = "Сохранение пользователя",
            description = "Позволяет сохранить (создать или обновить) переданные данные о пользователе"
    )
    public ResponseEntity<UserDto> saveUser(@RequestBody UserDto userData) {
        log.info("saveUser: userData={}", userData);
        UserDto savedUser = userService.saveUser(userData);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header(HttpHeaders.LOCATION, "users/"+savedUser.getUsername())
                .body(savedUser);
    }
}
