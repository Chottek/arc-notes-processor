package pl.fox.arcnotes.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.fox.arcnotes.model.User;
import pl.fox.arcnotes.repository.UserRepository;

import java.util.List;

@Service
public class UserService {

    private final UserRepository repository;

    @Autowired
    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public List<User> findAll(){
        return repository.findAll();
    }

    public void addOrSave(User u){
        repository.save(u);
    }

    public void remove(User u){
        repository.delete(u);
    }




}
