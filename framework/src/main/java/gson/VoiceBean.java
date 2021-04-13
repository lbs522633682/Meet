package gson;

import com.liboshuai.framework.utils.JsonUtil;

import java.util.List;

/**
 * Author:boshuai.li
 * Time:2021/4/13
 * Description: 语音识别类
 */
public class VoiceBean {

    /**
     * sn : 1
     * ls : false
     * bg : 0
     * ed : 0
     * ws : [{"bg":49,"cw":[{"sc":0,"w":"你"}]},{"bg":73,"cw":[{"sc":0,"w":"是"}]},{"bg":93,"cw":[{"sc":0,"w":"谁"}]}]
     */

    /**
     * 第几句
     */
    private int sn;
    /**
     * 是否最后一句
     */
    private boolean ls;
    /**
     * 保留字段，无需关注
     */
    private int bg;
    /**
     * 保留字段，无需关注
     */
    private int ed;
    /**
     * 词
     */
    private List<WsBean> ws;

    public int getSn() {
        return sn;
    }

    public void setSn(int sn) {
        this.sn = sn;
    }

    public boolean getLs() {
        return ls;
    }

    public void setLs(boolean ls) {
        this.ls = ls;
    }

    public int getBg() {
        return bg;
    }

    public void setBg(int bg) {
        this.bg = bg;
    }

    public int getEd() {
        return ed;
    }

    public void setEd(int ed) {
        this.ed = ed;
    }

    public List<WsBean> getWs() {
        return ws;
    }

    public void setWs(List<WsBean> ws) {
        this.ws = ws;
    }

    public static class WsBean {
        /**
         * bg : 49
         * cw : [{"sc":0,"w":"你"}]
         */

        private int bg;
        /**
         * 中文分词
         */
        private List<CwBean> cw;

        public int getBg() {
            return bg;
        }

        public void setBg(int bg) {
            this.bg = bg;
        }

        public List<CwBean> getCw() {
            return cw;
        }

        public void setCw(List<CwBean> cw) {
            this.cw = cw;
        }

        public static class CwBean {
            /**
             * sc : 0.0
             * w : 你
             */

            /**
             * 分数
             */
            private double sc;
            /**
             * 单字
             */
            private String w;

            public double getSc() {
                return sc;
            }

            public void setSc(double sc) {
                this.sc = sc;
            }

            public String getW() {
                return w;
            }

            public void setW(String w) {
                this.w = w;
            }

            @Override
            public String toString() {
                return JsonUtil.toJSON(this);
            }
        }

        @Override
        public String toString() {
            return JsonUtil.toJSON(this);
        }
    }

    @Override
    public String toString() {
        return JsonUtil.toJSON(this);
    }
}
