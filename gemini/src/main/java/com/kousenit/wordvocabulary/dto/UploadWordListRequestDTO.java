package com.kousenit.wordvocabulary.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UploadWordListRequestDTO {

    @Builder
    public record Word(String englishWord, String koreanMeaning) {}

    private List<WordListResponseDTO.Word> wordList;
}
