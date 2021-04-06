package gson;

/**
 * Author:boshuai.li
 * Time:2021/5/6   15:17
 * Description: 文本消息
 */
public class TextBean {
    private String msg;
    private String type;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "TextBean{" +
                "msg='" + msg + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
