package platform;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@RestController
public class Api {

    private final ServiceDB service;

    @Autowired
    public Api(ServiceDB service) {
        this.service = service;
    }

    @GetMapping(path = "/api/code/latest")
    public ResponseEntity<String> showMultipleSnippetsJson() {
        return service.getLatestSnippetsJson();
    }

    @GetMapping(path = "/code/latest")
    public ResponseEntity<String> showMultipleSnippetsHtml() {
        service.slowdown();
        return service.getLatestSnippetsHtml();
    }

    @GetMapping(value = {"/api/code", "/api/code/{id}"})
    public ResponseEntity<String> showOneSnippetJson(HttpServletRequest request, @PathVariable Optional<String> id) {
        return service.getItemJson(id);
    }

    @GetMapping(value = {"/code", "/code/{id}"})
    public ResponseEntity<String> showOneSnippet(HttpServletRequest request, @PathVariable Optional<String> id) {
        System.out.println(request.getRequestURI());
        return service.getItemHtml(id);
    }

    @PostMapping(value = "/api/code/new")
    public String postNewCode(@RequestBody String code) {
        return service.postCode(code);
    }

    @GetMapping(path = "/code/new")
    public ResponseEntity<String> getNewCode() {
        return service.getCode();
    }
}