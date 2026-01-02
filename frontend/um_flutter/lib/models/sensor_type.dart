enum SensorType {
  TEMPERATURE,
  HUMIDITY,
  CO2,
  LIGHT,
  MOTION,
  OCCUPANCY;

  String get label {
    switch (this) {
      case SensorType.TEMPERATURE:
        return 'Temperature';
      case SensorType.HUMIDITY:
        return 'Humidity';
      case SensorType.CO2:
        return 'CO2 Level';
      case SensorType.LIGHT:
        return 'Light Level';
      case SensorType.MOTION:
        return 'Motion';
      case SensorType.OCCUPANCY:
        return 'Occupancy';
    }
  }
}
