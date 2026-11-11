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

/**
 * 在庫管理システムのデータアクセス層 (DAO)
 * Springの JdbcTemplate を使用してデータベースへのクエリ実行を担当します。
 */
@Repository
public class ItemDao {

    private final JdbcTemplate jdbcTemplate;

    /**
     * コンストラクタインジェクションにより JdbcTemplate を注入
     * @param jdbcTemplate Spring Bootが自動構成したデータソース接続
     */
    public ItemDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 【課題1】組織情報照会
     * 
     * 【データ連携の流れ】
     * 1. 画面(task1.html): ユーザーが組織IDを入力し「検索」ボタン押下 (GET /task1?orgId=43)
     * 2. コントローラー(ItemController): リクエストパラメータ "orgId" を受け取り、このメソッドを呼出
     * 3. 本メソッド(ItemDao): SQLを実行し、取得結果を ItemOrgBean に自動マッピング
     * 4. コントローラー: 戻り値の List<ItemOrgBean> を Model に登録 (model.addAttribute("list", list))
     * 5. 画面(task1.html): Thymeleaf (th:each="item : ${list}") がテーブルを描画
     * 
     * @param orgId 組織ID (nullまたは空文字の場合は全件取得)
     * @return 組織情報リスト (ItemOrgBean)
     */
    public List<ItemOrgBean> findOrganizations(String orgId) {
        String sql = "SELECT organization_id, organization_code FROM mtl_parameters";

        // 検索条件が指定されている場合はWHERE句を追加してプレースホルダー(?)でバインド
        if (orgId != null && !orgId.trim().isEmpty()) {
            sql += " WHERE organization_id = ?";
            // BeanPropertyRowMapper: DBカラム名 (organization_id) と Beanのフィールド名 (organizationId) を自動変換
            return jdbcTemplate.query(
                sql,
                new BeanPropertyRowMapper<>(ItemOrgBean.class),
                Integer.parseInt(orgId.trim())
            );
        }

        // 未入力時は全件取得
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(ItemOrgBean.class));
    }

    /**
     * 【課題2】組織別品目照会
     * 組織マスタと品目マスタを内部結合し、指定組織・品目の組み合わせを照会します。
     * 
     * @param orgId 組織ID
     * @param itemId 品目ID
     * @return 組織・品目情報リスト
     */
    public List<ItemOrgBean> findItemOrg(String orgId, String itemId) {
        String sql = "SELECT p.organization_code, i.segment1 "
                   + "FROM mtl_system_items_b i "
                   + "JOIN mtl_parameters p ON i.organization_id = p.organization_id "
                   + "WHERE i.organization_id = ? AND i.inventory_item_id = ?";

        return jdbcTemplate.query(
            sql,
            new BeanPropertyRowMapper<>(ItemOrgBean.class),
            Integer.parseInt(orgId.trim()),
            Integer.parseInt(itemId.trim())
        );
    }

    /**
     * 【課題3】複数品目・組織照会
     * カンマ区切りの複数品目IDを展開し、IN句を用いて複数拠点と品目を一括照会します。
     * 
     * @param itemIdsStr カンマ区切りの品目ID文字列 (例: "10005,10008")
     * @return 該当する組織・品目情報リスト (組織コード降順、品目コード昇順)
     */
    public List<ItemOrgBean> findMultipleItems(String itemIdsStr) {
        List<Integer> idList = Arrays.stream(itemIdsStr.split(","))
                                     .map(String::trim)
                                     .filter(s -> !s.isEmpty())
                                     .map(Integer::parseInt)
                                     .collect(Collectors.toList());

        if (idList.isEmpty()) {
            return new ArrayList<>();
        }

        // 展開したIDの個数分、SQLのIN句プレースホルダー (?, ?, ...) を生成
        String inClause = idList.stream().map(id -> "?").collect(Collectors.joining(","));

        String sql = "SELECT p.organization_code, hou.name AS organization_name, i.segment1 "
                   + "FROM mtl_system_items_b i "
                   + "JOIN mtl_parameters p ON i.organization_id = p.organization_id "
                   + "JOIN hr_all_organization_units hou ON p.organization_id = hou.organization_id "
                   + "WHERE i.inventory_item_id IN (" + inClause + ") "
                   + "ORDER BY p.organization_code DESC, i.segment1 ASC";

        return jdbcTemplate.query(
            sql,
            new BeanPropertyRowMapper<>(ItemOrgBean.class),
            idList.toArray()
        );
    }

    /**
     * 【課題4】組織別品目原価照会
     * 指定組織・品目の確定原価を照会し、ROUND関数により整数へ四捨五入します。
     * 
     * @param orgId 組織ID
     * @param itemId 品目ID
     * @return 原価情報リスト
     */
    public List<ItemCostBean> findItemCost(String orgId, String itemId) {
        String sql = "SELECT p.organization_code, i.segment1, ROUND(c.item_cost) AS item_cost "
                   + "FROM cst_item_costs c "
                   + "JOIN mtl_system_items_b i ON c.inventory_item_id = i.inventory_item_id AND c.organization_id = i.organization_id "
                   + "JOIN mtl_parameters p ON c.organization_id = p.organization_id "
                   + "WHERE c.organization_id = ? AND c.inventory_item_id = ?";

        return jdbcTemplate.query(
            sql,
            new BeanPropertyRowMapper<>(ItemCostBean.class),
            Integer.parseInt(orgId.trim()),
            Integer.parseInt(itemId.trim())
        );
    }

    /**
     * 【課題5】手持在庫照会
     * 指定組織・品目の保管場所(棚番)ごとの手持数量および単位を照会します。
     * 
     * @param orgId 組織ID
     * @param itemId 品目ID
     * @return 保管場所別在庫情報リスト
     */
    public List<ItemStockBean> findItemStock(String orgId, String itemId) {
        String sql = "SELECT p.organization_code, i.segment1, q.subinventory_code, "
                   + "q.transaction_uom_code AS uom_code, q.transaction_quantity AS primary_quantity "
                   + "FROM mtl_onhand_quantities_detail q "
                   + "JOIN mtl_system_items_b i ON q.inventory_item_id = i.inventory_item_id AND q.organization_id = i.organization_id "
                   + "JOIN mtl_parameters p ON q.organization_id = p.organization_id "
                   + "WHERE q.organization_id = ? AND q.inventory_item_id = ?";

        return jdbcTemplate.query(
            sql,
            new BeanPropertyRowMapper<>(ItemStockBean.class),
            Integer.parseInt(orgId.trim()),
            Integer.parseInt(itemId.trim())
        );
    }
}