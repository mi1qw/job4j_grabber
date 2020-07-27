package ru.job4j.grabber;

import ru.job4j.grabber.SqlRuParse.Post;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;
import java.util.Properties;

/**
 * The type Psql store.
 */
public class PsqlStore implements Store, AutoCloseable {
    private Connection cnn;

    /**
     * Instantiates a new Psql store.
     *
     * @param cfg the cfg
     */
    public PsqlStore(final Properties cfg) {
        try {
            Class.forName(cfg.getProperty("jdbc.driver"));
            cnn = DriverManager.getConnection(
                    cfg.getProperty("url"),
                    cfg.getProperty("username"),
                    cfg.getProperty("password")
            );
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * save.
     *
     * @param post post
     */
    @Override
    public void save(final Post post) {

    }

    /**
     * getAll.
     *
     * @return List<Post>
     */
    @Override
    public List<Post> getAll() {
        return null;
    }

    /**
     * Find by id post.
     *
     * @param id the id
     * @return the post
     */
    @Override
    public Post findById(final String id) {
        return null;
    }

    /**
     * close.
     *
     * @throws Exception Exception
     */
    @Override
    public void close() throws Exception {
        if (cnn != null) {
            cnn.close();
        }
    }
}