# ausfin API

## Description

A Java/Spring API project to handle the calculation logic for my aus/fin project [front-end](https://github.com/austnly/ausfin).

_Project is currently in progress + calculations may be buggy!_

### 🚀 [Live API @ https://ausfin-api-production.up.railway.app/](https://ausfin-api-production.up.railway.app/)

## Current Working Endpoints

### /tax

For calculating your tax based only on taxable income.

Send a POST request with the body

```
{
    "income": 100000 // Any positive integer value
}
```

### /super

For calculating how much super is paid from or on top of your income, depending on parameters passed, and your income after super.

Send a POST request with the body

```
{
    "income": 100000, // Any positive integer value
    "superInclusive": true, // Whether above income includes super
    "rate": 15.4, // Your super payment rate as a percentage (e.g. 15.4% is 15.4)
    "maxSuperContributions": false // Whether you want the result for the maximum concessional contributions you can make for Super
}
```

### /help-repay

For calculating your HELP repayment based only on an income value.

Send a GET request with the body

```
{
    "income": 0, // Any positive integer value
    "reportableSuperContributions": 0, // Any positive integer value
    "reportableFringeBenefits": 0 // Any positive integer value
}
```

### /mls

For calculating your Medicare Levy Surcharge based only on an income value.

Send a POST request with the body

```
{
    "income": 0, // Any positive integer value
    "reportableFringeBenefits": 0  // Any positive integer value
}
```

### /detailed-tax

For calculating your detailed annual tax, HELP-repayment, Medicare, and MLS liability, based on income, investments and super.

Send a POST request with the body

```
{
    "grossIncome": 90000,
    "helpBalance": 50000,
    "superBalance": 50000,
    "investmentsBalance": 50000,
    "superInclusive": true,
    "superContributionRate": 10,
    "maxSuperContributions": false,
    "expenses": 35000,
    "deductions": 5000,
    "fringeBenefits": 0,
    "privateHospitalCover": false,
    "maxSuper": false,
    "assumedGrowth": 7
}
```

### /fire

For calculating your years to reach FIRE and final net worth.

Send a POST request with the body

```
{
    "age": "30", // Your current age
    "income": "90000", // Your current gross income
    "helpBalance": 50000, // Current HELP debt balance
    "superBalance": 50000, // Current Superannuation balance
    "investmentsBalance": 50000, // Current balance of index fund ETF investments
    "superInclusive": true, // true if your gross income includes super contributions, false (or null) if your employer pays super on top of your income
    "superContributionRate": "15", // Rate of super contribution as a percentage of your income, i.e. 10 for 10%
    "maxSuperContributions": false, // false if you do not want to maximise your concessional (low tax) super contributions, true if you want to assume the max concessional contribution is being paid to super from the start (currently $27,500)
    "expenses": 35000, // Your after tax annual expenses
    "deductions": 5000, // Any pre-tax deductions you can make from your gross income
    "fringeBenefits": 0, // Value of any fringe benefits you receive from your employer
    "assumedGrowth": "7", // Assumed growth rate of investments and super as a percentage , i.e. 7 for 7%
    "privateHospitalCover": true, // true if you have Hospital insurance that exempts you from the Medicare Levy Surcharge
}
```
