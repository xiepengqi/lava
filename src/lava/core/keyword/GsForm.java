package lava.core.keyword;

import lava.core.DataMap.DataInfo;
import lava.core.Form;
import lava.core.Sub;
import lava.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
            boolean isKeySub = keyArg.getValue() instanceof Sub;
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
            }

            if (isKeySub) {
                Sub sub = (Sub) keyArg.getValue();
                List result = new ArrayList();
                Iterable it;
                if (isList) {
                    it = (Iterable) main;
                } else {
                    it = ((Map) main).entrySet();
                }

                for (Object obj : it) {
                    List args = new ArrayList();
                    args.add(obj);

                    sub.getDataMap().put("$args", args);
                    sub.getDataMap().put("$0", obj);
                    sub.getDataMap().put("$-1", obj);

                    sub.run();

                    result.add(sub.getAsForm().getValue());
                }
                main=result;
                continue;
            }
            if (isKeyMap) {
                Map keyMap = (Map) keyArg.getValue();
                List result=new ArrayList();
                for (Object key : keyMap.keySet()) {
                    Object value = keyMap.get(key);
                    Object resultValue;
                    if (value instanceof Sub) {
                        Sub sub = (Sub) value;
                        Object oriValue = isMap ? ((Map) main).get(key) : ((List) main).get(Integer.parseInt(key
                                .toString()));
                        List args = new ArrayList();
                        args.add(oriValue);

                        sub.getDataMap().put("$args", args);
                        sub.getDataMap().put("$0", oriValue);
                        sub.getDataMap().put("$-1", oriValue);

                        sub.run();

                        resultValue = sub.getAsForm().getValue();
                    } else {
                        resultValue = value;
                    }
                    if (isMap) {
                        ((Map) main).put(key, resultValue);
                    } else {
                        ((List) main).set(Integer.parseInt(key.toString()), resultValue);
                    }
                    result.add(resultValue);
                }
                main=result;
                continue;
            }

            if (isMap) {
                main = ((Map) main).get(keyArg.getValue());
                continue;
            }

            if (isList) {
                main = ((List) main).get(Integer.parseInt(keyArg.getValue().toString()));
                continue;
            }
        }
        this.value = main;
        this.type = main==null? void.class:main.getClass();
    }
}
