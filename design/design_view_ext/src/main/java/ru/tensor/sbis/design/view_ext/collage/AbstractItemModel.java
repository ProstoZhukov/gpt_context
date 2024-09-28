package ru.tensor.sbis.design.view_ext.collage;

/**
 * @author am.boldinov
 */
@SuppressWarnings({"unused", "JavaDoc"})
public class AbstractItemModel {

    private int width;
    private int height;

    public AbstractItemModel() {

    }

    public AbstractItemModel(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /** @SelfDocumented */
    public int getWidth() {
        return width;
    }

    /** @SelfDocumented */
    public void setWidth(int width) {
        this.width = width;
    }

    /** @SelfDocumented */
    public int getHeight() {
        return height;
    }

    /** @SelfDocumented */
    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractItemModel that = (AbstractItemModel) o;
        return width == that.width && height == that.height;
    }

    @Override
    public int hashCode() {
        int result = width;
        result = 31 * result + height;
        return result;
    }
}
