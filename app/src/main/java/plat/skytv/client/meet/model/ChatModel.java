package plat.skytv.client.meet.model;

import com.liboshuai.framework.utils.JsonUtil;

/**
 * Author:boshuai.li
 * Time:2021/4/9
 * Description: 聊天页面得实体类
 */
public class ChatModel {
    /**
     * 左边得消息类型
     */
    public static final int TYPE_LEFT_TEXT = 0;
    public static final int TYPE_LEFT_IMG = 1;
    public static final int TYPE_LEFT_LOCATION = 2;

    /**
     * 右侧得消息类型
     */
    public static final int TYPE_RIGHT_TEXT = 3;
    public static final int TYPE_RIGHT_IMG = 4;
    public static final int TYPE_RIGHT_LOCATION = 5;

    /**
     * 消息类型
     */
    private int type;
    // 文本消息
    private String text;

    // TODO 其他类型消息


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return JsonUtil.toJSON(this);
    }
}
