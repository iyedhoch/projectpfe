package com.pfe.docgen.version;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class VersionNotFoundException extends RuntimeException {

    public VersionNotFoundException(Long versionId) {
        super("Version not found with id: " + versionId);
    }
}