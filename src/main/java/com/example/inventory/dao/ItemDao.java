package com.example.inventory.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.inventory.bean.ItemCostBean;
import com.example.inventory.bean.ItemOrgBean;
import com.example.inventory.bean.ItemStockBean;

@Repository
public class ItemDao {

    private final JdbcTemplate jdbcTemplate;

    public ItemDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 🟩 【課題1】組織情報照会
    public List<ItemOrgBean> findOrganizations(String orgId) {
        String sql = "SELECT organization_id, organization_code FROM mtl_parameters";
        if (orgId != null && !orgId.trim().isEmpty()) {
            sql += " WHERE organization_id = ?";
            return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(ItemOrgBean.class), Integer.parseInt(orgId.trim()));
        }
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(ItemOrgBean.class));
    }

    // 🟩 【課題2】組織別品目照会
    public List<ItemOrgBean> findItemOrg(String orgId, String itemId) {
        String sql = "SELECT p.organization_code, i.segment1 " +
                     "FROM mtl_system_items_b i " +
                     "JOIN mtl_parameters p ON i.organization_id = p.organization_id " +
                     "WHERE i.organization_id = ? AND i.inventory_item_id = ?";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(ItemOrgBean.class), 
                Integer.parseInt(orgId.trim()), Integer.parseInt(itemId.trim()));
    }

 // 🟩 【課題3】複数品目・組織照会（HR_ALL_ORGANIZATION_UNITSのJOINとORDER BYを追加）
    public List<ItemOrgBean> findMultipleItems(String itemIdsStr) {
        List<Integer> idList = Arrays.stream(itemIdsStr.split(","))
                                    .map(String::trim)
                                    .filter(s -> !s.isEmpty())
                                    .map(Integer::parseInt)
                                    .collect(Collectors.toList());
        if (idList.isEmpty()) return new ArrayList<>();
        String inClause = idList.stream().map(id -> "?").collect(Collectors.joining(","));
        
        // hou.name（組織名）を取得し、ソート順（組織コード降順、品目コード昇順）を指定
        String sql = "SELECT p.organization_code, hou.name AS organization_name, i.segment1 " +
                     "FROM mtl_system_items_b i " +
                     "JOIN mtl_parameters p ON i.organization_id = p.organization_id " +
                     "JOIN hr_all_organization_units hou ON p.organization_id = hou.organization_id " +
                     "WHERE i.inventory_item_id IN (" + inClause + ") " +
                     "ORDER BY p.organization_code DESC, i.segment1 ASC";
                     
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(ItemOrgBean.class), idList.toArray());
    }

    // 🟩 【課題4】組織別品目原価照会（ROUNDで端数処理）
    public List<ItemCostBean> findItemCost(String orgId, String itemId) {
        String sql = "SELECT p.organization_code, i.segment1, ROUND(c.item_cost) AS item_cost " +
                     "FROM cst_item_costs c " +
                     "JOIN mtl_system_items_b i ON c.inventory_item_id = i.inventory_item_id AND c.organization_id = i.organization_id " +
                     "JOIN mtl_parameters p ON c.organization_id = p.organization_id " +
                     "WHERE c.organization_id = ? AND c.inventory_item_id = ?";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(ItemCostBean.class), 
                Integer.parseInt(orgId.trim()), Integer.parseInt(itemId.trim()));
    }

 // 🟩 【課題5】手持在庫照会（テーブル名と単位カラム名を修正）
    public List<ItemStockBean> findItemStock(String orgId, String itemId) {
        String sql = "SELECT p.organization_code, i.segment1, q.subinventory_code, " +
                     "q.transaction_uom_code AS uom_code, q.transaction_quantity AS primary_quantity " +
                     "FROM mtl_onhand_quantities_detail q " +
                     "JOIN mtl_system_items_b i ON q.inventory_item_id = i.inventory_item_id AND q.organization_id = i.organization_id " +
                     "JOIN mtl_parameters p ON q.organization_id = p.organization_id " +
                     "WHERE q.organization_id = ? AND q.inventory_item_id = ?";
                     
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(ItemStockBean.class), 
                Integer.parseInt(orgId.trim()), Integer.parseInt(itemId.trim()));
    }
}