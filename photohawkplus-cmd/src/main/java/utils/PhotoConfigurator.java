package utils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.NoSuchElementException;

/**
 * Created by artur on 22/10/15.
 */
public class PhotoConfigurator {
    private static class ConfiguratorHolder{
        public static PhotoConfigurator configurator=new PhotoConfigurator();
    }
    public static PhotoConfigurator getConfigurator(){
        return ConfiguratorHolder.configurator;
    }
    private HashMap<String, Object> map=new LinkedHashMap<>();
    public void setProperty(String key, String value){
        map.put(key,value);
    }
    public String getProperty(String key){
        if (map.containsKey(key))
            return (String)map.get(key);
        NoSuchElementException e=new NoSuchElementException("Configurator. No key entry for: "+ key );
        throw e;
    }
}
