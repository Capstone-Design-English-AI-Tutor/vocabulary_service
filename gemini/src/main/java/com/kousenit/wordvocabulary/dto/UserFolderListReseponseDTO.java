package com.kousenit.wordvocabulary.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@AllArgsConstructor
@Data
public class UserFolderListReseponseDTO {

    @Builder
    public record folder(Long folderId, String folderName) {}

    private List<folder> folders;
}
