import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../models/iot_sensor.dart';
import '../models/sensor_reading.dart';
import 'app_providers.dart';

// ==================== Sensors Provider ====================
final sensorsProvider = FutureProvider.autoDispose<List<IotSensor>>((
  ref,
) async {
  final apiService = ref.read(apiServiceProvider);
  return apiService.getSensors();
});

// ==================== Single Sensor Provider ====================
final sensorProvider = FutureProvider.autoDispose.family<IotSensor, String>((
  ref,
  sensorId,
) async {
  final apiService = ref.read(apiServiceProvider);
  return apiService.getSensor(sensorId);
});

// ==================== Sensor Readings Provider ====================
final sensorReadingsProvider = FutureProvider.autoDispose
    .family<List<SensorReading>, String>((ref, sensorId) async {
      final apiService = ref.read(apiServiceProvider);
      return apiService.getSensorReadings(sensorId);
    });
