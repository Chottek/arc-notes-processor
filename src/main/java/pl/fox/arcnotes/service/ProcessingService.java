package pl.fox.arcnotes.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

@Service
public class ProcessingService {

    private static final Logger LOG = LoggerFactory.getLogger(ProcessingService.class);

    private static final String FILE_EXT = "mp3";
    private static final AudioFileFormat.Type FILE_TYPE = AudioFileFormat.Type.WAVE;


    /*
    *
    * @TODO: List<File> file = new ArrayList<>();
    * @TODO: List<AudioInputStream> clips = new ArrayList<>();
    * @TODO: file.forEach(e -> {clips.add(AudioSystem.getAudioInputStream(e))});
    *
    * */


    private String returnFile() {

        java.io.File file = new java.io.File(filePath);
        AudioInputStream clip = AudioSystem.getAudioInputStream(file); //THROWS EXCEPTION

        java.util.List<AudioInputStream> musicFiles = new java.util.ArrayList<>();
        //@TODO: Figure a way to fill List with music to merge and return path to merged file

        if (musicFiles.size() == 0 || musicFiles.size() == 1) {
            return null;
        }

        java.util.UUID randomUUID = java.util.UUID.randomUUID();
        String response = randomUUID.toString().concat(FILE_EXT);
        AudioInputStream appendedFiles = null;

        for (int i = 0; i < musicFiles.size() - 1; i++) {
            if (i == 0) {
                appendedFiles = new AudioInputStream(
                        new java.io.SequenceInputStream(musicFiles.get(i), musicFiles.get(i + 1)),
                        musicFiles.get(i).getFormat(),
                        musicFiles.get(i).getFrameLength() + musicFiles.get(i + 1).getFrameLength());
                continue;
            }
            appendedFiles = new AudioInputStream(
                    new java.io.SequenceInputStream(appendedFiles, musicFiles.get(i + 1)),
                            appendedFiles.getFormat(),
                            appendedFiles.getFrameLength() + musicFiles.get(i + 1).getFrameLength());
        }
        try {
            AudioSystem.write(appendedFiles, FILE_TYPE, new java.io.File(response));
        } catch (java.io.IOException ie) {
            LOG.error("{}", ie.getMessage());
        }
        return response;


    }


}
