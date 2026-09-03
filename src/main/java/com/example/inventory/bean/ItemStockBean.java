package com.example.inventory.bean;

import lombok.Data;

@Data
public class ItemStockBean {
    private String organizationCode;
    private String segment1;
    private String subinventoryCode;
    private String uomCode;
    private Integer primaryQuantity;
}