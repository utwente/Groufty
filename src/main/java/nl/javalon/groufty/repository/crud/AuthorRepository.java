package nl.javalon.groufty.repository.crud;

import nl.javalon.groufty.domain.user.Author;
import nl.javalon.groufty.domain.user.AuthorType;
import nl.javalon.groufty.domain.user.Grouping;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface AuthorRepository extends PagingAndSortingRepository<Author, Long> {

}
