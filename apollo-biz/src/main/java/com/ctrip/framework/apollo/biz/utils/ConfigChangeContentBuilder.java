package com.ctrip.framework.apollo.biz.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.ctrip.framework.apollo.biz.entity.Item;
import com.ctrip.framework.apollo.core.utils.StringUtils;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import org.springframework.beans.BeanUtils;

/**
 * 配置变更内容构建器
 */
public class ConfigChangeContentBuilder {

  private static final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

  /**
   * 创建Item集合
   */
  private List<Item> createItems = new LinkedList<>();

  /**
   * 更新Item集合
   */
  private List<ItemPair> updateItems = new LinkedList<>();

  /**
   * 删除Item集合
   */
  private List<Item> deleteItems = new LinkedList<>();


  public ConfigChangeContentBuilder createItem(Item item) {
    if (!StringUtils.isEmpty(item.getKey())){
      createItems.add(cloneItem(item));
    }
    return this;
  }

  public ConfigChangeContentBuilder updateItem(Item oldItem, Item newItem) {
    if (!oldItem.getValue().equals(newItem.getValue())){
      ItemPair itemPair = new ItemPair(cloneItem(oldItem), cloneItem(newItem));
      updateItems.add(itemPair);
    }
    return this;
  }

  /**
   *
   * @param item
   * @return
   */
  public ConfigChangeContentBuilder deleteItem(Item item) {
    if (!StringUtils.isEmpty(item.getKey())) {
      deleteItems.add(cloneItem(item));
    }
    return this;
  }

  /**
   * hasContent() 方法，判断是否有变化。当且仅当有变化才记录 Commit
   * @return
   */
  public boolean hasContent(){
    return !createItems.isEmpty() || !updateItems.isEmpty() || !deleteItems.isEmpty();
  }

  /**
   * build() 方法，构建 Item 变化的 JSON 字符串
   * @return
   */
  public String build() {
    //因为事务第一段提交并没有更新时间,所以build时统一更新
    Date now = new Date();

    for (Item item : createItems) {
      item.setDataChangeLastModifiedTime(now);
    }

    for (ItemPair item : updateItems) {
      item.newItem.setDataChangeLastModifiedTime(now);
    }

    for (Item item : deleteItems) {
      item.setDataChangeLastModifiedTime(now);
    }

    //JSON格式化成字符串
    return gson.toJson(this);

  }

  static class ItemPair {

    //老
    Item oldItem;

    //新
    Item newItem;

    public ItemPair(Item oldItem, Item newItem) {
      this.oldItem = oldItem;
      this.newItem = newItem;
    }
  }

  /**
   * 调用 #cloneItem(Item) 方法，克隆 Item 对象。
   * 因为在 #build() 方法中，会修改 Item 对象的属性
   * @param source
   * @return
   */
  Item cloneItem(Item source) {
    Item target = new Item();

    BeanUtils.copyProperties(source, target);

    return target;
  }

}
