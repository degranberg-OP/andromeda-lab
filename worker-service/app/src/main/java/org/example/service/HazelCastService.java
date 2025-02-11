package org.example.service;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;

public class HazelCastService {
    private final HazelcastInstance hazelCastInstance;

    public HazelCastService() {
        final ClientConfig clientConfig = new ClientConfig();
        clientConfig.getNetworkConfig().addAddress("hazelcast-service:5701");
        this.hazelCastInstance = HazelcastClient.newHazelcastClient(clientConfig);
    }

    public IMap<String, String> getMetaDataCache() {
        return hazelCastInstance.getMap("fileMetaData");
    }
}
