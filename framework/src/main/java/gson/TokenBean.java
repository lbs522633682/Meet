package gson;

/**
 * Author:boshuai.li
 * Time:2020/7/30   15:17
 * Description: 融云Token获取结果
 */
public class TokenBean {

    /**
     * code : 200
     * userId : b757a3c83d
     * token : Edr2bmMy5wa59uh+UAR0Oa3dR1vFJod+bYKsvjVGsBI=@yby3.cn.rongnav.com;yby3.cn.rongcfg.com
     */

    private int code;
    private String userId;
    private String token;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
