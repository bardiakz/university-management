class SensorReading {
  final String sensorId;
  final double value;
  final DateTime timestamp;
  final String unit;

  SensorReading({
    required this.sensorId,
    required this.value,
    required this.timestamp,
    required this.unit,
  });

  factory SensorReading.fromJson(Map<String, dynamic> json) {
    return SensorReading(
      sensorId: json['sensorId'],
      value: json['value'].toDouble(),
      timestamp: DateTime.parse(json['timestamp']),
      unit: json['unit'],
    );
  }
}
