package com.example.help.board;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
@RequiredArgsConstructor
public class BoardController {

    private final BoardRepository boardRepository;

    @GetMapping("/list")
    public ResponseEntity<List<Board>> list() {
        List<Board> result = boardRepository.findAll();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Value("${image.upload.dir}")
    private String imageUploadDir;

    @PostMapping("/write")
    public ResponseEntity<Map<String, Object>> write(@RequestParam("title") String title,
                                                     @RequestParam("content") String content,
                                                     @RequestParam(value = "image", required = false) MultipartFile imageFile) {
        Board board = new Board();
        board.setTitle(title);
        board.setContent(content);

        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(imageFile.getOriginalFilename()));
            Path filePath = Paths.get(imageUploadDir + File.separator + fileName);
            try {
                imageFile.transferTo(filePath.toFile());
                board.setImage("/images/" + fileName);
            } catch (IOException e) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Image upload failed");
                return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        boardRepository.save(board);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Board entry saved successfully");
        response.put("board", board);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/read/{id}")
    public ResponseEntity<?> read(@PathVariable Long id) {
        Optional<Board> result = boardRepository.findById(id);

        if (result.isPresent()) {
            return new ResponseEntity<>(result.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Board entry not found", HttpStatus.NOT_FOUND);
        }
    }
}