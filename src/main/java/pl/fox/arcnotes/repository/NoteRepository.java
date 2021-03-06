package pl.fox.arcnotes.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;
import org.springframework.util.ResourceUtils;
import pl.fox.arcnotes.model.Note;

import javax.annotation.PostConstruct;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.io.SequenceInputStream;

/**
 * @author Chottek
 * A class that contains List of music files and methods to process them
 */
@Repository
public class NoteRepository {

    private static final Logger LOG = LoggerFactory.getLogger(NoteRepository.class);

    private static final String FILE_EXT = "WAV";             //file extension static
    private static final AudioFileFormat.Type FILE_TYPE = AudioFileFormat.Type.WAVE;  //file type as codex to process
    private static final String NOTES_PATH = "notes";
    private static final String OUTPUT_PATH = "merged";

    private final java.util.List<Note> notes = new java.util.ArrayList<>();

    private final String[] notesArr = {"C", "D", "E", "F", "G", "A", "H"};


    /**
     * A method used to initialize list of notes,
     * gather music files as AudioInputStreams
     */
    @PostConstruct
    private void initNotesList() {
        try {
            for (String s : notesArr) {
                notes.add(new Note(s, AudioSystem.getAudioInputStream(
                        new ClassPathResource(NOTES_PATH + "/" + s + "." + FILE_EXT).getInputStream())));
            }
            LOG.info("Finished Notes initializing [size: {}]", notes.size());
        } catch (UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param notes List of notes gathered from Google Vision data
     * @return File that contains a music file merged from notes
     * @throws IOException Of writing to file
     */
    public File merge(java.util.List<Note> notes) throws IOException{
        if (notes.size() < 2) {
            LOG.error("Too few notes found to make a music file!");
            return null;
        }

        LOG.info("GOT {} notes to merge!", notes.size());

        AudioFormat af = null;
        java.util.List<AudioInputStream> clips = new java.util.ArrayList<>();
        long frameLen = 0;

        for(Note n: notes){
            if(af == null){
                af = n.getSoundFile().getFormat();
            }

            clips.add(n.getSoundFile());
            frameLen += n.getSoundFile().getFrameLength();
        }

        String res = java.util.UUID.randomUUID().toString().concat(FILE_EXT);

        File f = new File(".\\" + OUTPUT_PATH + "\\" + res + "." + FILE_EXT);

        AudioSystem.write(
                new AudioInputStream(
                        new SequenceInputStream(java.util.Collections.enumeration(clips)), af, frameLen),
                FILE_TYPE, f);

        LOG.info("Created new File: {}", f.getAbsolutePath());

        return f;
    }

    public java.util.List<Note> getNotes() {
        return notes;
    }
}
