package ru.job4j.grabber;

import ru.job4j.grabber.SqlRuParse.Post;

import java.util.List;

public interface Store {
    void save(List<Post> posts);

    List<Post> getAll();

    Post findById(String id);

    void close() throws Exception;
}
