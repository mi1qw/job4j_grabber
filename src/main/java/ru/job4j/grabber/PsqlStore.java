package ru.job4j.grabber;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.job4j.grabber.SqlRuParse.Post;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

/**
 * The type Psql store.
 */
public class PsqlStore implements Store, AutoCloseable {
    private static final Logger LOG = LoggerFactory.getLogger(PsqlStore.class);
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
     * save Post to DB.
     *
     * @param posts List<Post>
     */
    @Override
    public void save(final List<Post> posts) {
        try (PreparedStatement prst = cnn.prepareStatement("INSERT INTO post VALUES (Default,?,?,?,?,?)")) {
            boolean auto = cnn.getAutoCommit();
            cnn.setAutoCommit(false);
            for (Post post : posts) {
                prst.setInt(1, Integer.parseInt(post.getId()));
                prst.setString(2, post.getName());
                prst.setString(3, post.getText());
                prst.setString(4, post.getHref());
                prst.setTimestamp(5, post.getCreated());
                prst.addBatch();
            }
            prst.executeBatch();
            cnn.commit();
            cnn.setAutoCommit(auto);
        } catch (SQLException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    /**
     * getAll Posts.
     *
     * @return List<Post>
     */
    @Override
    public List<Post> getAll() {
        List<Post> list = new ArrayList<>();
        try (PreparedStatement prst = cnn.prepareStatement("SELECT * FROM post")) {
            prst.executeQuery();
            ResultSet rs = prst.getResultSet();
            while (rs.next()) {
                list.add(new Post(
                        String.valueOf(rs.getInt(2)),
                        rs.getString(5),
                        rs.getString(3),
                        null,
                        null,
                        rs.getTimestamp(6),
                        rs.getString(4)));
            }
        } catch (SQLException e) {
            LOG.error(e.getMessage(), e);
        }
        return list;
    }

    /**
     * Find by id post.
     *
     * @param id the id
     * @return the post
     */
    @Override
    public Post findById(final String id) {
        Post post = null;
        ResultSet rs = null;
        try (PreparedStatement prst = cnn.prepareStatement("SELECT * FROM post where post_id=?")) {
            prst.setInt(1, Integer.parseInt(id));
            rs = prst.executeQuery();
            if (rs.next()) {
                post = new Post(
                        String.valueOf(rs.getInt(2)),
                        rs.getString(5),
                        rs.getString(3),
                        null,
                        null,
                        rs.getTimestamp(6),
                        rs.getString(4));
            }
        } catch (SQLException e) {
            LOG.error(e.getMessage(), e);
        } finally {
            try {
                Objects.requireNonNull(rs).close();
            } catch (SQLException e) {
                LOG.error(e.getMessage(), e);
            }
        }
        return post;
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