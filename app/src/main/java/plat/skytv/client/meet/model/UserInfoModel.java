package plat.skytv.client.meet.model;

/**
 * Author:boshuai.li
 * Time:2021/4/2   15:48
 * Description: 用户信息的模型类
 */
public class UserInfoModel {

    private int bgColor;
    // 标题
    private String title;
    // 对应内容
    private String content;
    // 背景颜色
    public int getBgColor() {
        return bgColor;
    }

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "UserInfoModel{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
