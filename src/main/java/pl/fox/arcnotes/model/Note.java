package pl.fox.arcnotes.model;

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
}
