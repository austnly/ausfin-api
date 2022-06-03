package ausfin.api.records;

public record TaxResult(Integer baseTax, Integer taxRate, Integer totalTax, Integer afterTax) {
}
