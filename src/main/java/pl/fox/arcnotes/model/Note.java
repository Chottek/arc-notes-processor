package pl.fox.arcnotes.model;

import javax.sound.sampled.AudioInputStream;

public class Note {

    private final String type;   //C, D, E, F, G, A, H, C
    private final AudioInputStream soundFile;

    public Note(String type, AudioInputStream soundFile) {
        this.type = type;
        this.soundFile = soundFile;
    }

    public String getType() {
        return type;
    }

    public AudioInputStream getSoundFile() {
        return soundFile;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Note note = (Note) o;
        return java.util.Objects.equals(type, note.type) &&
                java.util.Objects.equals(soundFile, note.soundFile);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(type, soundFile);
    }

    @Override
    public String toString() {
        return "Note{" +
                "type='" + type + '\'' +
                ", soundFile=" + soundFile +
                '}';
    }
}
