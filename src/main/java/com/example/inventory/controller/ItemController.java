package com.example.inventory.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.inventory.bean.ItemCostBean;
import com.example.inventory.bean.ItemOrgBean;
import com.example.inventory.bean.ItemStockBean;
import com.example.inventory.dao.ItemDao;

/**
 * 各業務課題（Task1〜5）の画面遷移およびデータ中継を統括するコントローラー
 * 画面（Thymeleaf）からのGETリクエストを受け付け、DAO呼び出し結果をModelへ格納してテンプレートへ渡します。
 */
@Controller
public class ItemController {

    private final ItemDao itemDao;

    /**
     * コンストラクタインジェクションにより ItemDao を注入
     * @param itemDao データアクセス層のDAOコンポーネント
     */
    public ItemController(ItemDao itemDao) {
        this.itemDao = itemDao;
    }

    /**
     * 【課題1】組織情報照会
     * 
     * @param orgId 画面の入力フォーム(name="orgId")から送信された組織ID (未入力時はnull)
     * @param model Thymeleafテンプレートへ値を渡すためのコンテナ
     * @return 遷移先テンプレート名 ("task1" -> task1.html)
     */
    @GetMapping("/task1")
    public String task1(@RequestParam(required = false) String orgId, Model model) {
        // パラメータが入力されている場合のみDAOを呼び出して検索を実行
        List<ItemOrgBean> list = (orgId != null && !orgId.trim().isEmpty()) 
                ? itemDao.findOrganizations(orgId) 
                : null;

        // Thymeleaf 側で参照できるように Model へ属性を登録
        model.addAttribute("list", list);            // 検索結果リスト (th:each="item : ${list}")
        model.addAttribute("orgId", orgId);          // 入力値の保持 (th:value="${orgId}")
        model.addAttribute("activeMenu", "task1");   // 共通サイドバーのカレント表示制御

        return "task1";
    }

    /**
     * 【課題2】組織別品目照会
     * 
     * @param orgId 組織ID
     * @param itemId 品目ID
     * @param model 画面へデータを渡すModelオブジェクト
     * @return 遷移先テンプレート名 ("task2" -> task2.html)
     */
    @GetMapping("/task2")
    public String task2(@RequestParam(required = false) String orgId, 
                        @RequestParam(required = false) String itemId, Model model) {
        // 組織ID・品目IDの双方が入力された場合のみ検索を実行
        List<ItemOrgBean> list = (orgId != null && itemId != null && !orgId.trim().isEmpty() && !itemId.trim().isEmpty()) 
                ? itemDao.findItemOrg(orgId, itemId) 
                : null;

        model.addAttribute("list", list);
        model.addAttribute("orgId", orgId);
        model.addAttribute("itemId", itemId);
        model.addAttribute("activeMenu", "task2");

        return "task2";
    }

    /**
     * 【課題3】複数品目・組織照会
     * 
     * @param itemIds カンマ区切りの品目ID文字列 (例: "10005,10008")
     * @param model 画面へデータを渡すModelオブジェクト
     * @return 遷移先テンプレート名 ("task3" -> task3.html)
     */
    @GetMapping("/task3")
    public String task3(@RequestParam(required = false) String itemIds, Model model) {
        // 品目IDが入力されている場合にDAOを呼び出してIN句検索を実行
        List<ItemOrgBean> list = (itemIds != null && !itemIds.trim().isEmpty()) 
                ? itemDao.findMultipleItems(itemIds) 
                : null;

        model.addAttribute("list", list);
        model.addAttribute("itemIds", itemIds);
        model.addAttribute("activeMenu", "task3");

        return "task3";
    }

    /**
     * 【課題4】組織別品目原価照会
     * 
     * @param orgId 組織ID
     * @param itemId 品目ID
     * @param model 画面へデータを渡すModelオブジェクト
     * @return 遷移先テンプレート名 ("task4" -> task4.html)
     */
    @GetMapping("/task4")
    public String task4(@RequestParam(required = false) String orgId, 
                        @RequestParam(required = false) String itemId, Model model) {
        List<ItemCostBean> list = (orgId != null && itemId != null && !orgId.trim().isEmpty() && !itemId.trim().isEmpty()) 
                ? itemDao.findItemCost(orgId, itemId) 
                : null;

        model.addAttribute("list", list);
        model.addAttribute("orgId", orgId);
        model.addAttribute("itemId", itemId);
        model.addAttribute("activeMenu", "task4");

        return "task4";
    }

    /**
     * 【課題5】手持在庫照会
     * 
     * @param orgId 組織ID
     * @param itemId 品目ID
     * @param model 画面へデータを渡すModelオブジェクト
     * @return 遷移先テンプレート名 ("task5" -> task5.html)
     */
    @GetMapping("/task5")
    public String task5(@RequestParam(required = false) String orgId, 
                        @RequestParam(required = false) String itemId, Model model) {
        List<ItemStockBean> list = (orgId != null && itemId != null && !orgId.trim().isEmpty() && !itemId.trim().isEmpty()) 
                ? itemDao.findItemStock(orgId, itemId) 
                : null;

        model.addAttribute("list", list);
        model.addAttribute("orgId", orgId);
        model.addAttribute("itemId", itemId);
        model.addAttribute("activeMenu", "task5");

        return "task5";
    }
}