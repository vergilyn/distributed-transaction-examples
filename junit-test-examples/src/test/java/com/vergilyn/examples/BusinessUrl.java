package com.vergilyn.examples;

/**
 * @author VergiLyn
 * @date 2019-04-10
 */
public class BusinessUrl {
    private static final String URL_BUY = "%s/business/buy";
    private static final String URL_DECREASE_STORAGE = "%s/business/decrease-storage?beforeMillis=%d&afterMillis=%d&rollback=%b";
    private static final String URL_GET_STORAGE = "%s/business/get-storage?beforeMillis=%d&afterMillis=%d";

    private String hostname;

    public BusinessUrl(String hostname) {
        this.hostname = hostname;
    }

    public String buy(){
        return String.format(URL_BUY, hostname);
    }

    public String decreaseStorage(Long beforeMillis, Long afterMillis){
        return decreaseStorage(beforeMillis, afterMillis, false);
    }

    public String decreaseStorage(Long beforeMillis, Long afterMillis, boolean rollback){
        return String.format(URL_DECREASE_STORAGE, hostname, beforeMillis, afterMillis, rollback);
    }

    public String getStorage(Long beforeMillis, Long afterMillis){
        return String.format(URL_GET_STORAGE, hostname, beforeMillis, afterMillis);
    }
}
