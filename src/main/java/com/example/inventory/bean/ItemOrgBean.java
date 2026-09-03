package com.example.inventory.bean;

import lombok.Data;

@Data
public class ItemOrgBean {
    private Integer organizationId;
    private String organizationCode;
    private String organizationName;
    private Integer itemId;
    private String segment1; // 品目コード（品番）
}