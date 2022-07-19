# ausfin API

## Description

A Java/Spring API project to handle the calculation logic for my aus/fin project [front-end](https://github.com/austnly/ausfin).

### 🚀 [Live API @ https://ausfin-api.herokuapp.com/](https://ausfin-api.herokuapp.com/)

## Current Working Endpoints

### /tax

For calculating your tax based only on taxable income.

Send a GET request with the body

```
{
    "income": 100000 // Any positive integer value
}
```

### /super

For calculating how much super is paid from or on top of your income, depending on parameters passed, and your income after super.

Send a GET request with the body

```
{
    "income": 100000, // Any positive integer value
    "superInclusive": true, // Whether above income includes super
    "rate": 0.1, // Your super payment rate as a decimal (i.e. 10% is 0.1)
    "maxSuperContributions": false // Whether you want the result for the maximum concessional contributions you can make for Super
}
```

### /help-repay

For calculating your HELP repayment based only on an income value.

Send a GET request with the body

```
{
    "income": 0 // Any positive integer value
}
```

### /mls

For calculating your Medicare Levy Surcharge based only on an income value.

Send a GET request with the body

```
{
    "income": 0 // Any positive integer value
}
```

### /detailed-tax

For calculating your detailed annual tax, HELP-repayment, Medicare, and MLS liability, based on income, investments and super.

Send a GET request with the body

```
{
    "income": 0,
    "helpBalance": 20000,
    "superBalance": 35000,
    "investmentsBalance": 75000,
    "superInclusive": true,
    "rate": 10,
    "maxSuperContributions": false,
    "expenses": 35000,
    "deductions": 5000,
    "fringeBenefits": 0,
    "privateHospitalCover": false,
    "maxSuper": false,
    "growth": 10,
    "drawingPhase": false,
    "paySuper": true
}
```
