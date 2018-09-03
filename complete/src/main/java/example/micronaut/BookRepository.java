package example.micronaut;

import java.util.List;

public interface BookRepository {

    List<Book> findAll();
}
