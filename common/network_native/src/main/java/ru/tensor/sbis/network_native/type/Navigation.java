package ru.tensor.sbis.network_native.type;

/**
 * Тип навигация, использующийся в списочныйх методах бизнес-логики
 */
public class Navigation {

    private int page;
    private int pageSize;
    private boolean hasMore;

    /**
     * Конструктор. Не запрашивает у бизнес-логики инфорацию о "ЕстьЕще"
     *
     * @param page     Номер страницы
     * @param pageSize Количество записей на странице
     */
    public Navigation(int page, int pageSize) {
        this(page, pageSize, true);
    }

    /**
     * Конструктор. Запрашивает у бизнес-логики инфорацию о "ЕстьЕще"
     *
     * @param page     Номер страницы
     * @param pageSize Количество записей на странице
     * @param hasMore  Необходимость получения информации о том, есть ли заиси на следующих страницах или нет
     */
    public Navigation(int page, int pageSize, boolean hasMore) {
        this.page = page;
        this.pageSize = pageSize;
        this.hasMore = hasMore;
    }

    /// <summary>
    /// Номер страницы
    /// </summary>
    public int getPage() {
        return page;
    }

    @SuppressWarnings("unused")
    public void setPage(int page) {
        this.page = page;
    }

    /// <summary>
    /// Размер страницы
    /// </summary>
    public int getPageSize() {
        return pageSize;
    }

    @SuppressWarnings("unused")
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    /// <summary>
    /// Необходимость получения информация о том, есть ли записи на следующих страницах или нет
    /// </summary>
    public boolean isHasMore() {
        return hasMore;
    }

    @SuppressWarnings("unused")
    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }
}
