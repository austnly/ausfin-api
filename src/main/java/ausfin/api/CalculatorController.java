package ausfin.api;

import ausfin.api.records.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping
public class CalculatorController {
    @Autowired
    CalculatorService calculatorService;
    @Autowired
    TablesService tablesService;

    @GetMapping(path="tax")
    public TaxResult tax(@RequestBody @Valid IncomeDTO data) {
        return calculatorService.calculateTax(data.income());
    }

    @GetMapping(path="super")
    public SuperResult superann(@RequestBody @Valid IncomeSuperDTO data) {
        return calculatorService.calculateSuper(data);
    }

    @GetMapping(path="help-repay")
    public HelpResult help(@RequestBody @Valid IncomeDTO data) {
        return calculatorService.calculateHELP(data.income());
    }

    @GetMapping(path="mls")
    public MlsResult mls(@RequestBody @Valid IncomeDTO data) {
        return calculatorService.calculateMLS(data.income());
    }

}
