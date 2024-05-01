package ru.morozovit.plugin.bedfight;

import pl.mikigal.config.Config;
import pl.mikigal.config.annotation.ConfigName;

import java.util.HashMap;
import java.util.Map;

@ConfigName("bedfight.yml")
public interface BedfightConfig extends Config {
    default Map<String,Map<String,Object>> getPlayers() {
        Map<String,Map<String,Object>> map = new HashMap<>();
        Map<String,Object> defaultData = new HashMap<>();
        defaultData.put("Default","true");
        map.put("default",defaultData);
        return map;
    }
    void setPlayers(Map<String, Map<String, Object>> map);
}
