package com.kousenit.gemini;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kousenit.gemini.dto.JsonWordListDTO;
import com.kousenit.gemini.dto.SimpleQuizListDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "Gemini", description = "Use Gemini_API for OCR and creating problem set, api 사용 순서는 ocr, update(필요시),problem(셋 중 하나), result 순입니다. 모든 요청에는 Authorization Token Bearer 타입의 토큰을 보내야 합니다.")
@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class GeminiController {
    private final GeminiService geminiService;

    @Autowired
    public GeminiController(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    @Data
    @AllArgsConstructor
    public static class Word {
        private Integer id;
        private String english;
        private String korean;
    }

    HashMap<String, List<Word>> wordHashMap = new HashMap<String, List<Word>>();
    //사용자 email requestparam "email"//결과//db저장, 단어장 불러오기, 삭제
    @PostMapping(value ="/quiz/ocr", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "이미지 OCR", description = "주어진 이미지를 대상으로 OCR을 수행하여 json형식의 단어장을 반환(Authorization Token Bearer 보내기!)")
    public ResponseEntity<List<Word>> ocr(@Parameter(description = "OCR 대상 이미지 파일 업로드") @RequestParam("file")MultipartFile multipartFile,
                                                      @RequestParam("email")String id
                                                      ) throws IOException {
        String prompt = """
                Use OCR to extract english word and korean meaning as a text.
                Give me a json file that contains english word in the image as key
                and corresponding korean word as value.
                do not include '''json at the front
                 """;
        String JsonString = geminiService.ocrImage(prompt, multipartFile);
        System.out.println(JsonString);
        wordHashMap.remove(id);
        Gson gson = new Gson();
        Type mapType = new TypeToken<Map<String, String>>() {}.getType();
        Map<String, String> map = gson.fromJson(JsonString, mapType);

        Integer i = 1;
        List<Word> wordList = new ArrayList<>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            Word word = new Word(i,entry.getKey(), entry.getValue());
            wordList.add(word);

            i++;
        }
        wordHashMap.put(id,wordList);

        System.out.println(wordHashMap);

        return new ResponseEntity<>(wordHashMap.get(id), HttpStatus.OK);
    }

    @PostMapping(value ="/quiz/ocr/update")
    @Operation(summary = "단어장 수정", description = "사용자의 피드백에 따라 앞서 주어진 json형식의 단어장을 수정(Authorization Token Bearer 보내기!)")
    public ResponseEntity<HttpStatus> updateWordList(@RequestBody List<Word> wordList,
                                                                @RequestParam("email")String id) {

        for(Word entry : wordList) {
            Integer index = entry.getId();

            List<Word> list = wordHashMap.get(id);//로컬 해쉬맵에서 유저id에 대응되는 단어장 받아오기
            int i=0;
            for(Word word : list) {//index를 통해 비교 후 값 수정
                if (word.getId().equals(index)) {
                    String english = entry.getEnglish();
                    String korean = entry.getKorean();
                    list.set(i,new Word(index, english, korean));
                }
                i++;
            }


        }

        System.out.println(wordHashMap.get(id));
        return new ResponseEntity<>( HttpStatus.OK);
    }

    @Operation(summary = "문제 생성", description = "단어장 정보를 기반으로 답이 한글인 빈칸 문제 생성하여 반환(Authorization Token Bearer 보내기!)")
    @PostMapping("/quiz/problem/korean")
    public ResponseEntity<SimpleQuizListDTO> makeEnglishProblemSet(@RequestParam("email")String id) {
        List<SimpleQuizListDTO.Quiz> quizList = new ArrayList<>();

        List<Word> wordList = wordHashMap.get(id);

        for (Word word : wordList) {
            String english = word.getEnglish();
            String korean = word.getKorean();

            SimpleQuizListDTO.Quiz quiz = SimpleQuizListDTO.Quiz.builder()
                    .quiz(english)
                    .answer(korean)
                    .build();

            quizList.add(quiz);
        }


        SimpleQuizListDTO simpleQuizListDTO = SimpleQuizListDTO.builder().quizList(quizList).build();

        return new ResponseEntity<>(simpleQuizListDTO, HttpStatus.OK);
    }

    @Operation(summary = "문제 생성", description = "단어장 정보를 기반으로 답이 영어인 빈칸 문제 생성하여 반환(Authorization Token Bearer 보내기!)")
    @PostMapping("/quiz/problem/english")
    public ResponseEntity<SimpleQuizListDTO> makeKoreanProblemSet(@RequestParam("email")String id) {
        List<SimpleQuizListDTO.Quiz> quizList = new ArrayList<>();

        List<Word> wordList = wordHashMap.get(id);

        for (Word word : wordList) {
            String english = word.getEnglish();
            String korean = word.getKorean();

            SimpleQuizListDTO.Quiz quiz = SimpleQuizListDTO.Quiz.builder()
                    .quiz(korean)
                    .answer(english)
                    .build();

            quizList.add(quiz);
        }

        SimpleQuizListDTO simpleQuizListDTO = SimpleQuizListDTO.builder().quizList(quizList).build();

        return new ResponseEntity<>(simpleQuizListDTO, HttpStatus.OK);
    }

    @Operation(summary = "문제 생성", description = "단어장 정보를 기반으로 문장 빈칸 문제 생성하여 반환(Authorization Token Bearer 보내기!)")
    @PostMapping("/quiz/problem/sentence")
    public ResponseEntity<String> makeSentenceProblemSet(@RequestParam("email") String id) {

        String words = new Gson().toJson(wordHashMap.get(id));
        System.out.println(words);
        String prompt = words + """
                From now on, you are an English fill-in-the-blank question generator.
                Your role is to create questions based on the conditions a~h below and provide these questions in JSON format:
                [question format]
                ->there should be no '''json at the front of the response
                [{"id" : 1
                "korean_translation" : “나는 손을 씻다”      -> there should be no '()' in this line
                "english_question" : “I () my hand”
                "answer" : wash
                },
                { "id" : 2 ....
                }]
                a. The given JSON contains an "english" value as an English word and a "korean" value as its meaning in Korean.
                b. You must create one question for each "english" value, in the order they are given.
                c. You must also give an translation sentence in Korean about the question you created.
                d. "english_question" with a blank space for the "english" value should be provided as a question, do not create blanks in the "korean_translation"
                e. The format of the blank should be like (). When you are making the JSON format, DO NOT CREATE BLANKS IN THE "korean_translation"
                f. The answer to all questions must be the "english" value. The form of this value should not change under any circumstances.
                g. Generate "english_question" in which "korean" value could fit well.
                h. Create the questions at a level suitable for American high school students.
                 """;

        String response = geminiService.gemini_1_5pro(prompt);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @GetMapping(value ="/quiz/problem/result")
    @Operation(summary = "단어 퀴즈 결과", description = "단어장 정보를 제공함과 동시에, 서버상의 json단어장 정보 삭제(Authorization Token Bearer 보내기!)")
    public ResponseEntity<List<Word>> updateWordList(@RequestParam("email")String id) {

        List<Word> wordList = wordHashMap.get(id);
        wordHashMap.remove(id);
        return new ResponseEntity<>(wordList, HttpStatus.OK);
    }


//    @Operation(summary = "문제 생성", description = "단어장 정보를 기반으로 문장 빈칸 문제 생성하여 반환")
//    @PostMapping("/problem/sentence1")
//    public ResponseEntity<String> makeSentenceProblemSet1() {
//        String words = new Gson().toJson(wordHashMap);
//        System.out.println(words);
//        String prompt = words + """
//                I will provide you English words along with its Korean meanings, and I'd like you to generate English sentences using this meaning
//                and provide its Korean translation as well.
//                Here is an example of JSON format where the given English word is 'light' and the provided Korean meaning is '빛'.
//                Provide response in the following JSON format.
//                Do not include '''json at the front
//                {
//                "1" : {
//                "english" : ["The light was so bright that I couldn't open my eyes"]
//                "korean" : [“빛이 너무 강해서 나는 눈을 뜰 수 없었다”]
//                },
//                "2" : { ....
//
//                 """;
//
//        String response = geminiService.gemini_1_5pro(prompt);
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }


//    @GetMapping("/problem")
//    public ResponseEntity<String> ocr(@RequestBody JsonWordListDTO wordList) {
//        String words = new Gson().toJson(wordList);
//
//        String prompt = words + """
//                지금부터 너는 영어 빈칸 문제 생성기야. 너의 역할은 다음 a~f의 조건에 따라 문제를 생성하고 이 문제를 json형식으로 제공해주는 것이야
//                a. 입력은 key가 영어 단어이고 value가 그에 대응하는 한글 뜻인 json 형태로 주어져
//                b. 모든 json의 key들에 대해 각각 하나의 문제를 만들어야 해
//                c. key가 쓰일만한 문맥을 고려해서 임의의 영어 문장을 생성해
//                d.생성한 영어 문장에 대해 한국어로 번역한 문장도 문제에서 주어져야 해
//                e.문제에서는 생성한 영어 문장에 대해 한국어로 번역한 문장과 생성한 영어 문장에서 해당 json의 key를 빈칸으로 하는 영어 문장이 보기로 주어져
//                f. 모든 문제의 정답은 언급한 json의 key여야만 해
//                g. 주어진 json의 key의 순서를 지켜서 문제를 만들어줘
//                [json 형식]
//                {
//                "1" : {
//                "question" : [“나는 손을 씻는다”, “I () my hand”]
//                "answer" : wash
//                },
//                "2" : { ....
//                 """;
//
//        String response = geminiService.getCompletion(prompt);
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }

//    String JsonString = geminiService.ocrImage(prompt, multipartFile);
//    Gson gson = new Gson();
//    Type mapType = new TypeToken<Map<String, String>>() {}.getType();
//    Map<String, String> map = gson.fromJson(JsonString, mapType);
//
//    List<JsonWordListDTO.Word> words = new ArrayList<>();
//        for (Map.Entry<String, String> entry : map.entrySet()) {
//        JsonWordListDTO.Word word = JsonWordListDTO.Word.builder()
//                .englishWord(entry.getKey())
//                .koreanMeaning(entry.getValue())
//                .build();
//        words.add(word);
//    }
//
//    JsonWordListDTO dto = JsonWordListDTO.builder()
//            .wordList(words)
//            .build();
//        System.out.println(dto);
//        return new ResponseEntity<>(dto, HttpStatus.OK);

}

