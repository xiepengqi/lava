package lava.constant;

import lava.util.Util;

import java.lang.reflect.Field;

/**
 * Created by xie,pengqi on 2017/12/21.
 */
public class MsgConstants {
    public static String wrong_args_num;
    public static String wrong_arg_name;
    public static String wrong_arg_type;


    static {
        Field[] fields=MsgConstants.class.getDeclaredFields();
        for(Field field:fields){
            field.setAccessible(true);
            try {
                field.set(null,field.getName());
            } catch (IllegalAccessException e) {
                Util.runtimeError("lava start failed");
            }
        }
    }
}
