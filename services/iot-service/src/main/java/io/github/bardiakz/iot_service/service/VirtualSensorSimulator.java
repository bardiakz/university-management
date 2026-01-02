package io.github.bardiakz.iot_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simulates virtual sensor data for demonstration
 */
@Service
@EnableScheduling
public class VirtualSensorSimulator {

    private static final Logger log = LoggerFactory.getLogger(VirtualSensorSimulator.class);
    private final SensorService sensorService;
    private final Random random = new Random();

    // Track which sensors exist to avoid repeated failed queries
    private final Set<String> knownMissingSensors = ConcurrentHashMap.newKeySet();

    public VirtualSensorSimulator(SensorService sensorService) {
        this.sensorService = sensorService;
    }

    @Scheduled(fixedRate = 5000) // Every 5 seconds
    public void simulateTemperature() {
        String sensorId = "TEMP-CLASS-101";

        // Skip if we know this sensor doesn't exist
        if (knownMissingSensors.contains(sensorId)) {
            return;
        }

        try {
            // Simulate classroom temperature: 18-26°C
            double temperature = 20 + (random.nextDouble() * 6);
            sensorService.recordReading(sensorId, temperature);

            // If we successfully recorded, remove from missing set (in case it was added)
            knownMissingSensors.remove(sensorId);
        } catch (Exception e) {
            // Mark as missing to avoid repeated queries
            knownMissingSensors.add(sensorId);
            log.debug("Sensor not registered yet: {}", sensorId);
        }
    }

    @Scheduled(fixedRate = 5000)
    public void simulateHumidity() {
        String sensorId = "HUM-CLASS-101";

        // Skip if we know this sensor doesn't exist
        if (knownMissingSensors.contains(sensorId)) {
            return;
        }

        try {
            // Simulate humidity: 40-70%
            double humidity = 40 + (random.nextDouble() * 30);
            sensorService.recordReading(sensorId, humidity);

            // If we successfully recorded, remove from missing set
            knownMissingSensors.remove(sensorId);
        } catch (Exception e) {
            // Mark as missing to avoid repeated queries
            knownMissingSensors.add(sensorId);
            log.debug("Sensor not registered yet: {}", sensorId);
        }
    }

    // Optional: Method to clear the cache if sensors are registered dynamically
    public void clearMissingCache() {
        knownMissingSensors.clear();
    }
}