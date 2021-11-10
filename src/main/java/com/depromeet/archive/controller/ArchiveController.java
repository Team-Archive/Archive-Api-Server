package com.depromeet.archive.controller;

import com.depromeet.archive.controller.dto.archive.ArchiveDto;
import com.depromeet.archive.controller.dto.archive.ArchiveListDto;
import com.depromeet.archive.domain.archive.service.ArchiveService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ArchiveController {

    private final ArchiveService archiveService;

    @GetMapping("/archive")
    public ResponseEntity<ArchiveListDto> archiveListView() {
        return ResponseEntity.ok(archiveService.getAllArchive());
    }

    @GetMapping("/archive/{id}")
    public ResponseEntity<ArchiveDto> archiveSpecificView(@PathVariable Long id) {
        return ResponseEntity.ok(archiveService.getOneArchiveById(id));
    }

}