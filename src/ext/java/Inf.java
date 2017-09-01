package ext.java;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/8/18.
 */
public class Inf {

    private static Map<String,String> sfMap=new HashMap<String,String>();

    public static boolean inf(Object obj,String className){
        Class objClass=obj.getClass();
        if(className.equals(sfMap.get(objClass.getCanonicalName()))){
            return true;
        }
        if(objClass.getCanonicalName().equals(className)){
            sfMap.put(objClass.getCanonicalName(),className);
            return true;
        }
        while(objClass.getSuperclass()!=null){
            objClass=objClass.getSuperclass();
            if(objClass.getCanonicalName().equals(className)){
                sfMap.put(objClass.getCanonicalName(),className);
                return true;
            }
        }
        return false;
    }


    public static void main(String[] args){
        System.out.println(inf("","java.lang.Object"));
    }
}
