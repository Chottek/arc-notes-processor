package pl.fox.arcnotes.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@RestController
@RequestMapping("/api/notes/")
public class NoteController {

    private static final Logger LOG = LoggerFactory.getLogger(NoteController.class);

    @PostMapping("/upload")
    public BodyBuilder uploadImage(@RequestParam("image") MultipartFile file) throws IOException {
        LOG.info("IMAGE BYTESIZE: {}", file.getBytes().length);

        return ResponseEntity.status(HttpStatus.OK);
    }

}
