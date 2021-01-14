package pl.fox.arcnotes.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;
import java.io.IOException;
import java.io.SequenceInputStream;
import java.util.List;
import java.util.UUID;

@Service
public class ProcessingService {

    private static final Logger LOG = LoggerFactory.getLogger(ProcessingService.class);

    private static final String FILE_EXT = "wav";
    private static final AudioFileFormat.Type FILE_TYPE = AudioFileFormat.Type.WAVE;

    /*
    * @TODO:
        File file = new File(filePath);
        AudioInputStream clip = AudioSystem.getAudioInputStream(file); //THROWS EXCEPTION
        List<File> file = new ArrayList<>();
        List<AudioInputStream> clips = new ArrayList<>();
        file.forEach(e -> {clips.add(AudioSystem.getAudioInputStream(e))});
    * */

    private String returnFile(List<AudioInputStream> clips) {
        if (clips.size() == 0 || clips.size() == 1) {
            return null;
        }

        String res = UUID.randomUUID().toString().concat(FILE_EXT);
        AudioInputStream appendedFiles = null;

        for (int i = 0; i < clips.size() - 1; i++) {
            if (i == 0) {
                appendedFiles = new AudioInputStream(
                        new SequenceInputStream(clips.get(i), clips.get(i + 1)), clips.get(i).getFormat(),
                        clips.get(i).getFrameLength() + clips.get(i + 1).getFrameLength());
                continue;
            }
            appendedFiles = new AudioInputStream(
                    new SequenceInputStream(appendedFiles, clips.get(i + 1)), appendedFiles.getFormat(),
                            appendedFiles.getFrameLength() + clips.get(i + 1).getFrameLength());
        }
        try {
            assert appendedFiles != null;
            AudioSystem.write(appendedFiles, FILE_TYPE, new File(res));
        } catch (IOException ie) {
            LOG.error("{}", ie.getMessage());
        }
        return res;
    }


}
