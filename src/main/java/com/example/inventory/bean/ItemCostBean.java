package com.example.inventory.bean;

import lombok.Data;

@Data
public class ItemCostBean {
    private String organizationCode;
    private String segment1;
    private Integer itemCost; // 四捨五入済みの確定原価
}