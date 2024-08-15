package com.example.help.board;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
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

    @PostMapping("/write")
    public ResponseEntity<Map<String, Object>> write(
            @RequestBody Board board,
            @RequestParam(value = "image", required = false) MultipartFile imageFile) {

        // Check if an image file is provided
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                // Convert MultipartFile to byte array
                byte[] imageBytes = imageFile.getBytes();
                board.setImage(imageBytes);
            } catch (IOException e) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Image upload failed");
                return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        // Save the board entry to the database
        boardRepository.save(board);

        // Prepare and return the response
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

    @GetMapping("/images/{id}")
    public ResponseEntity<?> getImage(@PathVariable Long id) {
        Optional<Board> result = boardRepository.findById(id);

        if (result.isPresent()) {
            Board board = result.get();
            byte[] imageBytes = board.getImage();

            if (imageBytes != null && imageBytes.length > 0) {
                ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(bis);
            } else {
                return new ResponseEntity<>("Image not found", HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>("Board entry not found", HttpStatus.NOT_FOUND);
        }
    }
}