import 'sensor_type.dart';

class IotSensor {
  final int id;
  final String sensorId;
  final String name;
  final SensorType type;
  final String location;
  final String unit;
  final bool active;
  final double? lastValue;
  final DateTime? lastUpdate;

  IotSensor({
    required this.id,
    required this.sensorId,
    required this.name,
    required this.type,
    required this.location,
    required this.unit,
    required this.active,
    this.lastValue,
    this.lastUpdate,
  });

  factory IotSensor.fromJson(Map<String, dynamic> json) {
    return IotSensor(
      id: json['id'],
      sensorId: json['sensorId'],
      name: json['name'],
      type: SensorType.values.firstWhere(
        (e) => e.name == json['type'],
        orElse: () => SensorType.TEMPERATURE,
      ),
      location: json['location'],
      unit: json['unit'],
      active: json['active'],
      lastValue: json['lastValue']?.toDouble(),
      lastUpdate: json['lastUpdate'] != null
          ? DateTime.parse(json['lastUpdate'])
          : null,
    );
  }
}
