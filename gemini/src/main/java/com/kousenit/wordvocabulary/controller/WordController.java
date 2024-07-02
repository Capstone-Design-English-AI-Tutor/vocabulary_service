package com.kousenit.wordvocabulary.controller;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.kousenit.wordvocabulary.dto.UploadWordListRequestDTO;
import com.kousenit.wordvocabulary.dto.UserFolderListReseponseDTO;
import com.kousenit.wordvocabulary.dto.WordListResponseDTO;
import com.kousenit.wordvocabulary.entity.Word;
import com.kousenit.wordvocabulary.service.WordService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class WordController {
    private WordService wordService;

    @Autowired
    public WordController(WordService wordService) {
        this.wordService = wordService;
    }

    @GetMapping("/vocabulary/{folderId}")
    @Operation(summary = "폴더 안 단어장 조회", description = "Authorization Token Bearer 보내기!")
    public ResponseEntity<WordListResponseDTO> getWordsInFolder(@PathVariable Long folderId) {
        WordListResponseDTO wordsInFolder = wordService.getWordsInFolder(folderId);
        return new ResponseEntity<>(wordsInFolder, HttpStatus.OK);
    }

    @PostMapping("/vocabulary/{folderId}")
    @Operation(summary = "폴더에 단어장 업로드하기")
    public ResponseEntity<HttpStatus> uploadWordList(@PathVariable Long folderId,
                                                     @RequestBody UploadWordListRequestDTO uploadWordListRequestDTO
                                                     ) {
        wordService.uploadWords(folderId, uploadWordListRequestDTO);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/vocabulary/folderList")
    @Operation(summary = "사용자 폴더 리스트 받아오기", description = "Authorization Token Bearer 보내기!")
    public ResponseEntity<UserFolderListReseponseDTO> getFoldersInUser(@RequestParam String email) {
        UserFolderListReseponseDTO userFolderListReseponseDTO = wordService.getFoldersInUser(email);
        return new ResponseEntity<>(userFolderListReseponseDTO, HttpStatus.OK);
    }

    @DeleteMapping("/vocabulary{wordId}")
    @Operation(summary = "단어 삭제하기(폴더 단어장 조회 이후)")
    public ResponseEntity<HttpStatus> deleteWord(@PathVariable Long wordId) {
        wordService.deleteWordInFolder(wordId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/vocabulary/create/{folderName}")
    @Operation(summary = "폴더 생성하기", description = "Authorization Token Bearer 보내기!")
    public ResponseEntity<Long> createFolder(@PathVariable String folderName, @RequestParam String email) {
        Long folderId = wordService.createFolder(folderName, email);
        return new ResponseEntity<>(folderId, HttpStatus.CREATED);
    }

    @DeleteMapping("/vocabulary/folder/{folderId}")
    @Operation(summary = "폴더 삭제하기")
    public ResponseEntity<HttpStatus> deleteFolder(@PathVariable Long folderId) {
        wordService.deleteFolder(folderId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping("/vocabulary{folderId}/{folderName}")
    @Operation(summary = "폴더 이름 수정하기")
    public ResponseEntity<HttpStatus> updateFolderName(@PathVariable Long folderId,
                                                       @PathVariable String folderName) {

        wordService.updateFolderName(folderId, folderName);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
