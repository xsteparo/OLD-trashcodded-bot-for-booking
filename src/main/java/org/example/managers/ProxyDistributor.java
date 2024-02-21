package org.example.managers;

import lombok.Data;
import lombok.Getter;
import org.example.model.ChinaProxy;

import java.util.List;

@Data
public class ProxyDistributor {
    public ProxyDistributor(List<ChinaProxy> chinaProxies) {
        this.chinaProxies = chinaProxies;
    }

    private List<ChinaProxy> chinaProxies;

    private int currentIndexCheckerProxy = 0;
    @Getter
    private int circleProxiesCounter = 0;

    public synchronized ChinaProxy getValidProxyForBooker() {
        for (ChinaProxy chinaProxy : chinaProxies) {
            if (chinaProxy.isValid()) {
                chinaProxy.setValid(false);
                return chinaProxy;
            }
        }
        //refreshProxyValidationForBooker();
        return null;
    }

    public synchronized void refreshProxyValidationForBooker() {
        if(circleProxiesCounter == 3){
            return;
        }
        for (ChinaProxy chinaProxy : chinaProxies) {
            chinaProxy.setValid(true);
        }
    }

    public ChinaProxy getValidProxyForChecker(){
        if(currentIndexCheckerProxy>chinaProxies.size()-1){
            currentIndexCheckerProxy = 0;
        }
        ChinaProxy proxy = chinaProxies.get(currentIndexCheckerProxy);
        currentIndexCheckerProxy++;
        return proxy;
    }
}
