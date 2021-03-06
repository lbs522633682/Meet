package plat.skytv.client.meet.model;

import com.liboshuai.framework.utils.JsonUtil;

import java.io.File;

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

    // 图片消息
    private String imgUrl;
    // 图片消息 本地图片
    private File localFile;

    // 位置消息
    private double la;
    private double lo;
    private String address;
    private String mapUrl;

    public double getLa() {
        return la;
    }

    public void setLa(double la) {
        this.la = la;
    }

    public double getLo() {
        return lo;
    }

    public void setLo(double lo) {
        this.lo = lo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMapUrl() {
        return mapUrl;
    }

    public void setMapUrl(String mapUrl) {
        this.mapUrl = mapUrl;
    }

    public File getLocalFile() {
        return localFile;
    }

    public void setLocalFile(File localFile) {
        this.localFile = localFile;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

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
