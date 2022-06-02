package ausfin.api;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
public class CalculatorController {
    @PostMapping
    public String test(@RequestParam(name="income") Integer income) {
        return String.format("You posted an income of %d", income);
    }

    @GetMapping(value="camel")
    public String convert() {
        return StringUtils.camelCase("Hi There my Name iS AuStiN");
    }

    @GetMapping(path="hello")
    public String greeting() {
        return "Hello";
    }
}
