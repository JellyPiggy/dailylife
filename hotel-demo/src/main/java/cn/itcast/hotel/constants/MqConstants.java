package cn.itcast.hotel.constants;

/**
 * @author 晓蝈
 * @version 1.0
 */
public class MqConstants {
    public final static String HOTEL_EXCHANGE = "hotel.topic";

    public final static String HOTEL_INSERT_QUEUE = "hotel.insert.queue";

    public final static String HOTEL_DELETE_QUEUE = "hotel.delete.queue";

    /**
     * 新增或修改的RoutingKey
     */
    public final static String HOTEL_INSERT_KEY = "hotel.insert";

    /**
     * 删除的RoutingKey
     */
    public final static String HOTEL_DELETE_KEY = "hotel.delete";


}
