package me.mortaldev.gtop.menus;

public class PageData {
  private int page = 1;
  private GTopMenu.ViewType gtopViewType = GTopMenu.ViewType.ALL_TIME;
  private GangStatsMenu.ViewType gangStatsViewType = GangStatsMenu.ViewType.DAY_TOTAL;

  public PageData setGangStatsViewType(GangStatsMenu.ViewType gangStatsViewType) {
    this.gangStatsViewType = gangStatsViewType;
    return this;
  }

  public PageData setGtopViewType(GTopMenu.ViewType gtopViewType) {
    this.gtopViewType = gtopViewType;
    return this;
  }

  public PageData setPage(int page) {
    this.page = page;
    return this;
  }

  public int getPage() {
    return page;
  }

  public GTopMenu.ViewType getGtopViewType() {
    return gtopViewType;
  }

  public GangStatsMenu.ViewType getGangStatsViewType() {
    return gangStatsViewType;
  }
}
