package lava.core.keyword;

import lava.constant.Constants;
import lava.core.DataMap.DataInfo;
import lava.core.Form;
import lava.util.Util;

import java.util.*;

public class GsForm extends Form {
    @Override
    public void parse() throws Exception {
        super.parse();

    }

    @Override
    public void check() {
        super.check();

    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void run() throws Exception {
        super.run();

        Object[] keys = this.args.toArray();

        Object main = parseFormArg(keys[0].toString()).getValue();
        DataInfo keyArg;

        for (int i = 1; i < keys.length; i++) {
            keyArg = parseFormArg(keys[i].toString());
            boolean isMap = main instanceof Map;
            boolean isList = main instanceof List;
            boolean isKeyMap = keyArg.getValue() instanceof Map;
            boolean isKeyList = keyArg.getValue() instanceof List;

            if (!(isMap || isList)) {
                Util.runtimeError(this, keys[i - 1].toString());
            }

            if(isKeyList){
                List keyList = (List) keyArg.getValue();
                if (isMap) {
                    for (int j = 0; j < keyList.size() - 1; j += 2) {
                        ((Map) main).put(keyList.get(j), keyList.get(j + 1));
                    }

                    if (keyList.size() % 2 == 1) {
                        ((Map) main).put(keyList.get(keyList.size() - 1), null);
                    }
                } else {
                    ((List) main).addAll(keyList);
                }
                continue;
            }

            if (isKeyMap) {
                Map keyMap = (Map) keyArg.getValue();
                List result=new ArrayList();
                for (Object key : keyMap.keySet()) {
                    Object value = keyMap.get(key);

                    if (isMap) {
                        ((Map) main).put(key, value);
                    } else {
                        ((List) main).set(Integer.parseInt(key.toString()), value);
                    }
                    result.add(value);
                }
                main=result;
                continue;
            }

            if (isMap) {
                main = ((Map) main).get(keyArg.getValue());
                continue;
            }

            if (isList) {
                List list=new ArrayList();
                String indexStr=keyArg.getValue().toString();
                String[] indexs=indexStr.split(",");
                for(String index:indexs){
                    fillList(index,main,list);
                }
                main=list;
            }
        }
        this.value = main;
        this.type = main==null? void.class:main.getClass();
    }

    private int prepareIndex(String index,int size){
        int result;
        if(Constants.empty.equals(index)){
            result=0;
        }else{
            result=Integer.parseInt(index);
        }
        if(result<0){
            result+=size;
        }

        return result;
    }

    private void fillList(String index,Object main, List list){
        if(index.contains(":")){
            String[] elems=index.split(":",2);
            int fromIndex=prepareIndex(elems[0],((List) main).size());
            int toIndex=prepareIndex(elems[1],((List) main).size());

            if(fromIndex>toIndex){
                List temp=((List) main).subList(toIndex,fromIndex+1);
                Collections.reverse(temp);
                list.addAll(temp);
            }else{
                list.addAll(((List) main).subList(fromIndex,toIndex+1));
            }
        }else{
            list.add(((List) main).get(Integer.parseInt(index)));
        }
    }
}
