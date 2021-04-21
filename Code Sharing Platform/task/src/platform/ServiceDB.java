package platform;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class ServiceDB {

    private final String HTML_BASE = "<html><head><title>%s</title>" +
            "<link rel=\"stylesheet\" href=\"//cdn.jsdelivr.net/gh/highlightjs/cdn-release@10.2.1/build/styles/default.min.css\">" +
            "<script src=\"//cdn.jsdelivr.net/gh/highlightjs/cdn-release@10.2.1/build/highlight.min.js\"></script>" +
            "<script>hljs.initHighlightingOnLoad();</script>" +
            "</head><body>%s</body></html>";

    @Autowired
    private CodeSnippetRepository codeSnippetRepository;

    public ResponseEntity<String> getLatestSnippetsJson() {
        String body;
        String template = "{ \"time\":%s,\"date\":\"%s\",\"code\":\"%s\",\"views\":%s}";
        if (codeSnippetRepository.count() > 0) {
            body = "[" + codeSnippetRepository.findTop10ByOrderByDateDesc().stream()
                    .map(n -> String.format(template, n.getTimeBomb(), n.getDate(), n.getCode(), n.getViewsLimit()))
                    .reduce("", (seed, t) -> seed + t + ",").replaceAll(",$", "") + "]";
        } else body = "[]";
        return ResponseEntity.ok().header("Content-Type", "application/json").body(body);

    }

    public ResponseEntity<String> getLatestSnippetsHtml() {
        String body;
        String template = "<span id=\"load_date\">%s</span><pre id=\"code_snippet\" i><code>%s</code></pre>";
        if (codeSnippetRepository.count() > 0) {
            body = codeSnippetRepository.findTop10ByOrderByDateDesc().stream()
                    .map(n -> String.format(template, n.getDate(), n.getCode()))
                    .reduce("", (seed, t) -> seed + t);
        } else body = "Snippets list is empty";
        body = String.format(HTML_BASE, "Latest", body);
        return ResponseEntity.ok().header("Content-Type", "text/html").body(body);
    }

    public ResponseEntity<String> getItemJson(Optional<String> id) {
        String body = "{}";
        String template = "{ \"date\":\"%s\",\"code\":\"%s\",\"time\":%d,\"views\":%d}";
        Optional<CodeSnippet> temp;

        if (id.isPresent()) temp = codeSnippetRepository.findById(id.get());
        else temp = codeSnippetRepository.findTop1ByOrderByDateDesc();

        if (temp.isPresent()) {
            CodeSnippet cs = temp.get();
            cs.incViewed();
            if (cs.isAccessAllowed()) {
                body = String.format(template, cs.getDate(), cs.getCode(), cs.getTimeLimit(), cs.getViewed());
                codeSnippetRepository.save(cs);
            } else {
                codeSnippetRepository.deleteById(cs.getId());
                return ResponseEntity.notFound().build();
            }
        } else return ResponseEntity.notFound().build();
        return ResponseEntity.ok().header("Content-Type", "application/json").body(body);
    }

    public ResponseEntity<String> getItemHtml(Optional<String> id) {
        String body = "There is тo matching snippet";
        String template = "<span id=\"load_date\">%s</span>" +
                "<span id=\"views_restriction\">%d more views allowed</span>" +
                "<span id=\"time_restriction\">The code willbe available for %d seconds</span>" +
                "<pre id=\"code_snippet\" i><code>%s</code></pre>";
        Optional<CodeSnippet> temp;
        if (id.isPresent()) temp = codeSnippetRepository.findById(id.get());
        else temp = codeSnippetRepository.findTop1ByOrderByDateDesc();

        if (temp.isPresent()) {
            CodeSnippet cs = temp.get();
            cs.incViewed();
            if (cs.isAccessAllowed()) {
                body = String.format(template, cs.getDate(), cs.getViewed(), cs.getTimeLimit(), cs.getCode());
                if (cs.getTimeBomb() <= 0)
                    body = body.replaceAll("<span id=\"time_restriction\">[\\d\\w\\s]+</span>", "");
                if (cs.getViewsLimit() <= 0)
                    body = body.replaceAll("<span id=\"views_restriction\">[\\d\\w\\s]+</span>", "");
                codeSnippetRepository.save(cs);
            } else {
                codeSnippetRepository.deleteById(cs.getId());
                return ResponseEntity.notFound().build();
            }
        } else return ResponseEntity.notFound().build();
        body = String.format(HTML_BASE, "Code", body);
        return ResponseEntity.ok().header("Content-Type", "text/html").body(body);
    }

    public ResponseEntity<String> getCode() {
        String FORM_TEMPLATE = String.format("<input id=\"time_restriction\" type=\"text\"/><br/>" +
                "<input id=\"views_restriction\" type=\"text\"/><br/>" +
                "<textarea id=\"code_snippet\"></textarea><br/>" +
                "<button id=\"send_snippet\" type=\"submit\" onclick=\"send()\">Submit</button>", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm:ss")));
        return ResponseEntity.ok().body(String.format(HTML_BASE, "Create", FORM_TEMPLATE));
    }

    public String postCode(String code) {
        slowdown();
        String codeSnippet = new JSONObject(code).getString("code");
        int time = new JSONObject(code).getInt("time");
        int views = new JSONObject(code).getInt("views");

        CodeSnippet temp = new CodeSnippet(LocalDateTime.now(), codeSnippet, views, time);
        codeSnippetRepository.save(temp);
        return "{\"id\":\"" + temp.getId() + "\"}";

    }

    public void slowdown() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void clearRestricted(){

    }
}
