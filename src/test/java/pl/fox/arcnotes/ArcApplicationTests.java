package pl.fox.arcnotes;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;
import pl.fox.arcnotes.model.Note;
import pl.fox.arcnotes.repository.NoteRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class ArcApplicationTests {

    private final NoteRepository n_repo;

    @Autowired
    public ArcApplicationTests(NoteRepository n_repo) {
        this.n_repo = n_repo;
    }

    @Test
    public void checkNoteInit() {
        Assert.notNull(n_repo.getNotes());
        Assert.notEmpty(n_repo.getNotes());
    }

    @Test
    public void checkIfMergeLessThan2ReturnsNull() throws IOException {
        List<Note> notes = new ArrayList<>();
        notes.add(n_repo.getNotes().get(0));

        assert n_repo.merge(notes) == null;
    }

    @Test
    public void checkIfMergerMoreThanOneReturnsFile() throws IOException {
        assert n_repo.merge(n_repo.getNotes()) != null;
    }

}
