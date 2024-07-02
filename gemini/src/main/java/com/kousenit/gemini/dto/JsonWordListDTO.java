package com.kousenit.gemini.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JsonWordListDTO {

    @Builder
    public static class Word {
        private String englishWord;
        private String koreanMeaning;
    }

    private List<Word> wordList;
}
