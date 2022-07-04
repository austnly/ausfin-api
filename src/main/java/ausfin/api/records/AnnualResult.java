package ausfin.api.records;

public record AnnualResult(NetWorthDTO netWorth, TaxResult taxResult, SuperResult superResult, HelpResult helpResult, MlsResult mlsResult, Integer medicareLevy, Integer availableToInvest) {
}
