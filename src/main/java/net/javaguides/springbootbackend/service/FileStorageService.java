package net.javaguides.springbootbackend.service;

import net.javaguides.springbootbackend.exception.ResourceNotFoundException;
import net.javaguides.springbootbackend.model.Diary;
import net.javaguides.springbootbackend.repository.DiaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class FileStorageService {

    @Autowired
    private DiaryRepository diaryRepository;

    public Diary store(Long id, MultipartFile file) throws IOException {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        Diary diary = diaryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Diary not exist with id :" + id));;
        diary.setImg_name(fileName);
        diary.setData(file.getBytes());

        return diaryRepository.save(diary);
    }

    public Diary getFile(Long id) {
        return diaryRepository.findById(id).get();
    }

    public Stream<Diary> getAllFiles() {
        return diaryRepository.findAll().stream();
    }

}