package com.wallet.wallet_api.entities;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EntriesSummary {

    private BigDecimal totalDebit;

    private BigDecimal totalCredit;

    private List<Entry> entries;

}