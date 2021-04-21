package platform;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CodeSnippetRepository extends CrudRepository<CodeSnippet, String> {

    long count();

    @Query(value = "select * from CodeSnippets where views_limit<=0 and time_bomb<=0 order by date desc limit 10", nativeQuery = true)
    List<CodeSnippet> findTop10ByOrderByDateDesc();

    Optional<CodeSnippet> findTop1ByOrderByDateDesc();
}
