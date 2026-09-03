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

@Controller
public class ItemController {

    private final ItemDao itemDao;

    public ItemController(ItemDao itemDao) {
        this.itemDao = itemDao;
    }

   

    // 🟩 【課題1】組織情報照会
    @GetMapping("/task1")
    public String task1(@RequestParam(required = false) String orgId, Model model) {
        List<ItemOrgBean> list = (orgId != null && !orgId.trim().isEmpty()) ? itemDao.findOrganizations(orgId) : null;
        model.addAttribute("list", list);
        model.addAttribute("orgId", orgId);
        model.addAttribute("activeMenu", "task1");
        return "task1";
    }

    // 🟩 【課題2】組織別品目照会
    @GetMapping("/task2")
    public String task2(@RequestParam(required = false) String orgId, 
                        @RequestParam(required = false) String itemId, Model model) {
        List<ItemOrgBean> list = (orgId != null && itemId != null && !orgId.trim().isEmpty() && !itemId.trim().isEmpty()) 
                ? itemDao.findItemOrg(orgId, itemId) : null;
        model.addAttribute("list", list);
        model.addAttribute("orgId", orgId);
        model.addAttribute("itemId", itemId);
        model.addAttribute("activeMenu", "task2");
        return "task2";
    }

    // 🟩 【課題3】複数品目・組織照会
    @GetMapping("/task3")
    public String task3(@RequestParam(required = false) String itemIds, Model model) {
        List<ItemOrgBean> list = (itemIds != null && !itemIds.trim().isEmpty()) ? itemDao.findMultipleItems(itemIds) : null;
        model.addAttribute("list", list);
        model.addAttribute("itemIds", itemIds);
        model.addAttribute("activeMenu", "task3");
        return "task3";
    }

    // 🟩 【課題4】組織別品目原価照会
    @GetMapping("/task4")
    public String task4(@RequestParam(required = false) String orgId, 
                        @RequestParam(required = false) String itemId, Model model) {
        List<ItemCostBean> list = (orgId != null && itemId != null && !orgId.trim().isEmpty() && !itemId.trim().isEmpty()) 
                ? itemDao.findItemCost(orgId, itemId) : null;
        model.addAttribute("list", list);
        model.addAttribute("orgId", orgId);
        model.addAttribute("itemId", itemId);
        model.addAttribute("activeMenu", "task4");
        return "task4";
    }

    // 🟩 【課題5】保管場所・在庫照会
    @GetMapping("/task5")
    public String task5(@RequestParam(required = false) String orgId, 
                        @RequestParam(required = false) String itemId, Model model) {
        List<ItemStockBean> list = (orgId != null && itemId != null && !orgId.trim().isEmpty() && !itemId.trim().isEmpty()) 
                ? itemDao.findItemStock(orgId, itemId) : null;
        model.addAttribute("list", list);
        model.addAttribute("orgId", orgId);
        model.addAttribute("itemId", itemId);
        model.addAttribute("activeMenu", "task5");
        return "task5";
    }
}