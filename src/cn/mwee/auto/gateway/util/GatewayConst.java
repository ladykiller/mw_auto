package cn.mwee.auto.gateway.util;

/**
 * Created by huming on 16/6/7.
 */
public class GatewayConst {

    public static final long REQUEST_TIMEOUT = 6000;    //6秒超时

    public static final long THREAD_SLEEP_MSEC = 100;

    public static class ShopConst {

        public static final int BATCH_TASK_MAXNUM = 5;          //每次处理5条记录

        public static final long SHOP_TASK_TIMEOUT = 30000;     //30秒超时
    }

    public static class MongoConst {

        public static final String DB_MSG = "mw_biz_gateway";

        public static final String TB_REQUEST = "shop_request_";

        public static final String TB_RESPONSE = "shop_response_";

        public static final String COLUMN_REQUEST_ID = "request_id";

        public static final String COLUMN_SHOP_ID = "shop_id";

        public static final String COLUMN_DATA = "data";

        public static final String COLUMN_REQ_TIMEOUT = "req_timeout";

        public static final String COLUMN_CREATE_TIME = "create_time";
    }

}
