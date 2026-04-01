package com.pfe.docgen.controller;

import com.pfe.docgen.service.TemplateConfigService;
import com.pfe.docgen.template.TemplateConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/config")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Template Configuration", description = "Template configuration management endpoints")
public class TemplateConfigController {

    private final TemplateConfigService templateConfigService;

    @GetMapping
    @Operation(summary = "Get active template configuration", description = "Retrieve the currently active template configuration")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Configuration retrieved successfully",
            content = @Content(schema = @Schema(implementation = TemplateConfig.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions")
    })
    public TemplateConfig getActiveConfig() {
        return templateConfigService.getActiveConfig();
    }

    @GetMapping("/all")
    @Operation(summary = "Get all template configurations", description = "Retrieve all available template configurations")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Configurations retrieved successfully",
            content = @Content(schema = @Schema(implementation = TemplateConfig.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions")
    })
    public List<TemplateConfig> getAllTemplates() {
        return templateConfigService.getAllTemplates();
    }

    @PostMapping
    @Operation(summary = "Update template configuration", description = "Create or update a template configuration")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Configuration updated successfully",
            content = @Content(schema = @Schema(implementation = TemplateConfig.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
        @ApiResponse(responseCode = "400", description = "Invalid configuration data")
    })
    public TemplateConfig updateConfig(@RequestBody TemplateConfig config) {
        return templateConfigService.save(config);
    }
}