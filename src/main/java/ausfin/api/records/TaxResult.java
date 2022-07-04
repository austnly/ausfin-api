package ausfin.api.records;

public record TaxResult(Integer baseTax, Float taxRate, Integer totalTax, Integer afterTax) {
}
