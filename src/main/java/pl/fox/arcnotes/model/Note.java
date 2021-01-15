package pl.fox.arcnotes.model;

import java.util.Objects;

public class Note {

    private final String type;   //C, D, E, F, G, A, H, C
    private final double score;  //Just of curiosity ;)

    public Note(String type, double score) {
        this.type = type;
        this.score = score;
    }

    public String getType() {
        return type;
    }

    public double getScore() {
        return score;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Note note = (Note) o;
        return Double.compare(note.score, score) == 0 &&
                Objects.equals(type, note.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, score);
    }

    @Override
    public String toString() {
        return "Note{" +
                "type='" + type + '\'' +
                ", score=" + score +
                '}';
    }
}
