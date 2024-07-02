package com.kousenit.wordvocabulary.service;

import com.kousenit.wordvocabulary.dto.UploadWordListRequestDTO;
import com.kousenit.wordvocabulary.dto.UserFolderListReseponseDTO;
import com.kousenit.wordvocabulary.dto.WordListResponseDTO;
import com.kousenit.wordvocabulary.entity.Folder;
import com.kousenit.wordvocabulary.entity.User;
import com.kousenit.wordvocabulary.entity.Word;
import com.kousenit.wordvocabulary.repository.FolderRepository;
import com.kousenit.wordvocabulary.repository.UserRepository;
import com.kousenit.wordvocabulary.repository.WordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WordService {

    private final UserRepository userRepository;
    private final FolderRepository folderRepository;
    private final WordRepository wordRepository;

    @Autowired
    public WordService(UserRepository userRepository, FolderRepository folderRepository, WordRepository wordRepository) {
        this.userRepository = userRepository;
        this.folderRepository = folderRepository;
        this.wordRepository = wordRepository;
    }

    public void uploadWords(Long folderId, UploadWordListRequestDTO wordListRequestDTO) {

        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new RuntimeException("폴더를 찾을 수 없습니다: " + folderId));

        List<Word> words = wordListRequestDTO.getWordList()
                .stream()
                .map(word -> Word.builder()
                        .folder(folder)
                        .englishWord(word.englishWord())
                        .koreanMeaning(word.koreanMeaning())
                        .build())
                .collect(Collectors.toList());

        wordRepository.saveAll(words);
    }

    public WordListResponseDTO getWordsInFolder(Long folderId) {
        List<Word> wordList = wordRepository.findByFolderId(folderId);

        List<WordListResponseDTO.Word> words = new ArrayList<>();

        for(Word word : wordList) {
            WordListResponseDTO.Word word1 = WordListResponseDTO.Word.builder()
                    .englishWord(word.getEnglishWord())
                    .koreanMeaning(word.getKoreanMeaning())
                    .id(word.getId())
                    .build();

            words.add(word1);
        }

        return WordListResponseDTO.builder().wordList(words).build();
    }


    public UserFolderListReseponseDTO getFoldersInUser(String email) {
        User user = userRepository.findUserByEmail(email);
        List<Folder> folderList = user.getFolders();

        List<UserFolderListReseponseDTO.folder> folders = new ArrayList<>();

        for (Folder folder : folderList) {
            UserFolderListReseponseDTO.folder folder1 = UserFolderListReseponseDTO.folder.builder()
                    .folderId(folder.getId())
                    .folderName(folder.getFolderName())
                    .build();

            folders.add(folder1);
        }

        return UserFolderListReseponseDTO.builder().folders(folders).build();
    }


    public void deleteWordInFolder(Long wordId) {
        Optional<Word> word = wordRepository.findById(wordId);
        if(word.isPresent()) {
            wordRepository.delete(word.get());
        }
        else {
            throw new RuntimeException("단어를 찾을 수 없습니다: " + wordId);
        }
    }

    public Long createFolder(String folderName, String email) {
        User user = userRepository.findUserByEmail(email);
        Folder folder = Folder.builder().folderName(folderName).user(user).build();
        Folder saved = folderRepository.save(folder);

        return saved.getId();
    }

    public void deleteFolder(Long folderId) {
        Optional<Folder> folder = folderRepository.findById(folderId);
        if(folder.isPresent()) {
            folderRepository.delete(folder.get());
        }
        else {
            throw new RuntimeException("폴더를 찾을 수 없습니다: " + folderId);
        }
    }

    public void updateFolderName(Long folderId, String folderName) {
        Optional<Folder> folder = folderRepository.findById(folderId);
        if(folder.isPresent()) {
            folder.get().setFolderName(folderName);
            folderRepository.save(folder.get());
        }
        else {
            throw new RuntimeException("폴더를 찾을 수 없습니다: " + folderId);
        }
    }

}
