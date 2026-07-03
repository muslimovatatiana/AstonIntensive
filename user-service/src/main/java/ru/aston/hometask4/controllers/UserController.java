package ru.aston.hometask4.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.aston.hometask4.dto.NotificationRequestDto;
import ru.aston.hometask4.dto.UserRequestDto;
import ru.aston.hometask4.dto.UserResponseDto;
import ru.aston.hometask4.services.UserService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "${swagger.controller.tag.name}", description = "${swagger.controller.tag.desc}")
public class UserController {

    private final UserService userService;

    @PostMapping
    @Operation(summary = "${swagger.op.create.summary}", description = "${swagger.op.create.desc}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "${swagger.resp.201}"),
            @ApiResponse(responseCode = "400", description = "${swagger.resp.400}"),
            @ApiResponse(responseCode = "409", description = "${swagger.resp.409}"),
            @ApiResponse(responseCode = "500", description = "${swagger.resp.500}")
    })
    public ResponseEntity<EntityModel<UserResponseDto>> createUser(@Valid @RequestBody UserRequestDto requestDto) {
        UserResponseDto createdUser = userService.createUser(requestDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toEntityModel(createdUser));
    }

    @GetMapping("/{id}")
    @Operation(summary = "${swagger.op.get.summary}", description = "${swagger.op.get.desc}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "${swagger.resp.200}"),
            @ApiResponse(responseCode = "404", description = "${swagger.resp.404}"),
            @ApiResponse(responseCode = "500", description = "${swagger.resp.500}")
    })
    public ResponseEntity<EntityModel<UserResponseDto>> getUserById(
            @PathVariable @Parameter(description = "${swagger.param.id}", example = "123e4567-e89b-12d3-a456-426614174000") UUID id) {
        UserResponseDto user = userService.getUserById(id);
        return ResponseEntity.ok(toEntityModel(user));
    }

    @GetMapping
    @Operation(summary = "${swagger.op.getall.summary}", description = "${swagger.op.getall.desc}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "${swagger.resp.200}"),
            @ApiResponse(responseCode = "500", description = "${swagger.resp.500}")
    })
    public ResponseEntity<CollectionModel<EntityModel<UserResponseDto>>> getAllUsers() {
        List<EntityModel<UserResponseDto>> usersWithLinks = userService.getAllUsers().stream()
                .map(this::toEntityModel)
                .collect(Collectors.toList());

        CollectionModel<EntityModel<UserResponseDto>> collectionModel = CollectionModel.of(usersWithLinks,
                linkTo(methodOn(UserController.class).getAllUsers()).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    @PutMapping("/{id}")
    @Operation(summary = "${swagger.op.update.summary}", description = "${swagger.op.update.desc}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "${swagger.resp.200}"),
            @ApiResponse(responseCode = "400", description = "${swagger.resp.400}"),
            @ApiResponse(responseCode = "404", description = "${swagger.resp.404}"),
            @ApiResponse(responseCode = "409", description = "${swagger.resp.409}"),
            @ApiResponse(responseCode = "500", description = "${swagger.resp.500}")
    })
    public ResponseEntity<EntityModel<UserResponseDto>> updateUser(
            @PathVariable @Parameter(description = "${swagger.param.id}", example = "123e4567-e89b-12d3-a456-426614174000") UUID id,
            @Valid @RequestBody UserRequestDto requestDto) {
        UserResponseDto updatedUser = userService.updateUser(id, requestDto);
        return ResponseEntity.ok(toEntityModel(updatedUser));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "${swagger.op.delete.summary}", description = "${swagger.op.delete.desc}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "${swagger.resp.204}"), // Исправлен статус-код с 24 на 204
            @ApiResponse(responseCode = "404", description = "${swagger.resp.404}"),
            @ApiResponse(responseCode = "500", description = "${swagger.resp.500}")
    })
    public ResponseEntity<Void> deleteUser(
            @PathVariable @Parameter(description = "${swagger.param.id}", example = "123e4567-e89b-12d3-a456-426614174000") UUID id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/notify")
    @Operation(summary = "${swagger.op.notify.summary}", description = "${swagger.op.notify.desc}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "${swagger.resp.notify.200}"),
            @ApiResponse(responseCode = "400", description = "${swagger.resp.notify.400}"),
            @ApiResponse(responseCode = "500", description = "${swagger.resp.notify.500}")
    })
    public ResponseEntity<Void> sendDirectNotification(@Valid @RequestBody NotificationRequestDto requestDto) {
        userService.sendDirectNotification(requestDto.action(), requestDto.email());
        return ResponseEntity.ok().build();
    }

    private EntityModel<UserResponseDto> toEntityModel(UserResponseDto dto) {
        return EntityModel.of(dto,
                linkTo(methodOn(UserController.class).getUserById(dto.id())).withSelfRel(),
                linkTo(methodOn(UserController.class).updateUser(dto.id(), null)).withRel("update"),
                linkTo(methodOn(UserController.class).deleteUser(dto.id())).withRel("delete"),
                linkTo(methodOn(UserController.class).getAllUsers()).withRel("all-users")
        );
    }
}
