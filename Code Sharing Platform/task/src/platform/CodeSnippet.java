package platform;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Entity
@Table(name = "CODESNIPPETS")
public class CodeSnippet implements Comparable<CodeSnippet> {
    @Id
    private String id;
    @NotNull
    private LocalDateTime date;
    @NotNull
    private String code;
    @NotNull
    private int viewsLimit;
    @NotNull
    private int timeBomb;
    @NotNull
    private int viewed = 0;

    public CodeSnippet() {
    }

    public CodeSnippet(LocalDateTime date, @NotNull String code, @NotNull int viewsLimit, @NotNull int timeBomb) {
        this.id = UUID.randomUUID().toString();
        this.date = date;
        this.code = code;
        this.viewsLimit = viewsLimit;
        this.timeBomb = timeBomb;
    }

    @Override
    public int compareTo(CodeSnippet codeSnippet) {
        return this.getDate().compareTo(codeSnippet.getDate());
    }

    public String getId() {
        return id;
    }

    public String getDate() {
        return date.format(DateTimeFormatter.ofPattern("dd/MM/uuuu hh:mm:ss"));
    }

    public String getCode() {
        return code;
    }

    public int getViewsLimit() {
        return viewsLimit;
    }

    public int getTimeBomb() {
        return timeBomb;
    }

    public long getTimeLimit() {
        if (timeBomb <= 0) return 0;
        return timeBomb - Duration.between(date, LocalDateTime.now()).toSeconds();
    }

    public int getViewed() {
        if (viewsLimit <= 0) return viewsLimit;
        else return viewsLimit - viewed;
    }

    public void incViewed() {
        viewed++;
    }

    public boolean isAccessAllowed() {
        if ((viewsLimit > 0 && viewed > viewsLimit) ||
                (timeBomb > 0 && Duration.between(date, LocalDateTime.now()).toSeconds() > timeBomb)) {
            System.out.println(viewsLimit);
            System.out.println(viewed);
            System.out.println(LocalDateTime.now());
            System.out.println(date);
            System.out.println(timeBomb);
            return false;
        }
        System.out.println();
        return true;
    }
}
