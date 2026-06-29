package ru.aston.hometask4.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@RestController
@RequestMapping("/api/v1")
@Tag(name = "${swagger.root.tag.name}", description = "${swagger.root.tag.desc}")
public class RootController {

    @GetMapping
    @Operation(summary = "${swagger.root.op.summary}", description = "${swagger.root.op.desc}")
    @ApiResponse(responseCode = "200", description = "${swagger.resp.200}")
    public ResponseEntity<RepresentationModel<?>> getApiRoot() {
        RepresentationModel<?> rootModel = new RepresentationModel<>();
        rootModel.add(linkTo(methodOn(RootController.class).getApiRoot()).withSelfRel());
        rootModel.add(linkTo(methodOn(UserController.class).getAllUsers()).withRel("users-url"));

        return ResponseEntity.ok(rootModel);
    }
}
