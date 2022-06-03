package ausfin.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping
public class CalculatorController {
    @Autowired
    CalculatorService calculatorService;
    @Autowired
    TablesService tablesService;

    @PostMapping
    public String test(@RequestParam(name="income") Integer income) {
        return String.format("You posted an income of %d", income);
    }

    @GetMapping(value="camel")
    public String convert() {
        return StringUtils.camelCase("Hi There my Name iS AuStiN");
    }

    @GetMapping(value="tables")
    public ArrayList<ArrayList<Float>> table() {
        return tablesService.getTaxTable();
    }

    @GetMapping(path="hello")
    public String greeting() {
        return "Hello";
    }
}
