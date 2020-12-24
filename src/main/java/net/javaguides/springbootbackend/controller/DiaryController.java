package net.javaguides.springbootbackend.controller;

import net.javaguides.springbootbackend.exception.ResourceNotFoundException;
import net.javaguides.springbootbackend.message.ResponseMessage;
import net.javaguides.springbootbackend.model.Diary;
import net.javaguides.springbootbackend.repository.DiaryRepository;
import net.javaguides.springbootbackend.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1/")
public class DiaryController {

    @Autowired
    private DiaryRepository diaryRepository;
    @Autowired
    private FileStorageService storageService;

    // get all diary
    @GetMapping("/diaries")
    public List < Diary > getAllDiary() {
        return diaryRepository.findAll();
    }

    // create diary rest api
    @PostMapping("/diaries")
    public Long createDiary(@RequestBody Diary diary) {
        Diary saved_diary = diaryRepository.save(diary);
        return saved_diary.getId();
    }

    // create diary image
    @PostMapping("/diaries/image")
    public ResponseEntity<ResponseMessage> createDiary(@RequestParam("image") MultipartFile image, @RequestParam("id") Long id) {
        String message = "";
        try {
            storageService.store(id, image);
            message = "Uploaded the diary successfully: " + image.getOriginalFilename();
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
        } catch (Exception e) {
            message = "Could not upload the file: " + image.getOriginalFilename() + "!";
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
        }

    }


    //image url
    @GetMapping("/diaries/image/{id}")
    public ResponseEntity<byte[]> getFile(@PathVariable Long id) {

        Diary diary = storageService.getFile(id);

        byte[] imageBytes = null;
        imageBytes = diary.getData();
        String imgType = diary.getType();

        if(imgType.equals("image/jpeg"))
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageBytes);
        else if(imgType.equals("image/jpg"))
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageBytes);
        else if (imgType.equals("image/png"))
            return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(imageBytes);
        //else if (imgType.equals("image/gif"))
            //return ResponseEntity.ok().contentType(MediaType.IMAGE_GIF).body(imageBytes);
        else
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageBytes);
    }

    // get diary by id rest api
    @GetMapping("/diaries/{id}")
    public ResponseEntity < Diary > getDiaryById(@PathVariable Long id) {
        Diary diary = diaryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Diary not exist with id :" + id));
        return ResponseEntity.ok(diary);
    }

    // update diary rest api
    @PutMapping("/diaries/{id}")
    public ResponseEntity < Diary > updateDiary(@PathVariable Long id, @RequestBody Diary diaryDetails) throws IOException {
        Diary diary = diaryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Diary not exist with id :" + id));

        diary.setTitle(diaryDetails.getTitle());
        diary.setContent(diaryDetails.getContent());
        diary.setContent(diaryDetails.getDate());
        diary.setEmotion(diaryDetails.getEmotion());

        Diary updatedDiary = diaryRepository.save(diary);
        return ResponseEntity.ok(updatedDiary);
    }

    // delete diary rest api
    @DeleteMapping("/diaries/{id}")
    public ResponseEntity <Map< String, Boolean >> deleteDiary(@PathVariable Long id) {
        Diary diary = diaryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Diary not exist with id :" + id));

        diaryRepository.delete(diary);
        Map< String, Boolean > response = new HashMap< >();
        response.put("deleted", Boolean.TRUE);
        return ResponseEntity.ok(response);
    }
}
