package net.coma112.axshop.utils;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@UtilityClass
public class MapUtils {
    public Map<String, Object> convertListToMap(@NotNull List<Map<Object, Object>> mapList) {
        Map<String, Object> resultMap = new LinkedHashMap<>(mapList.size() * 2);

        for (Map<Object, Object> map : mapList) {
            map.forEach((key, value) -> {
                if (key instanceof String strKey) resultMap.put(strKey, value);
            });
        }

        return resultMap;
    }
}
