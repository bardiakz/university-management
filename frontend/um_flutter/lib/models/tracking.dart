enum ShuttleStatus {
  ACTIVE,      // Shuttle is currently running
  INACTIVE,    // Shuttle is not in service
  MAINTENANCE, // Shuttle is under maintenance
  OUT_OF_SERVICE
}

class ShuttleLocation {
  final int shuttleId;
  final String vehicleNumber;
  final String routeName;
  final ShuttleStatus status;
  final double latitude;
  final double longitude;
  final DateTime? lastUpdate;

  ShuttleLocation({
    required this.shuttleId,
    required this.vehicleNumber,
    required this.routeName,
    required this.status,
    required this.latitude,
    required this.longitude,
    this.lastUpdate,
  });

  factory ShuttleLocation.fromJson(Map<String, dynamic> json) {
    return ShuttleLocation(
      shuttleId: json['shuttleId'],
      vehicleNumber: json['vehicleNumber'],
      routeName: json['routeName'],
      status: ShuttleStatus.values.firstWhere(
        (e) => e.toString().split('.').last == json['status'],
        orElse: () => ShuttleStatus.INACTIVE,
      ),
      latitude: (json['latitude'] as num).toDouble(),
      longitude: (json['longitude'] as num).toDouble(),
      lastUpdate: json['lastUpdate'] != null
          ? DateTime.parse(json['lastUpdate'])
          : null,
    );
  }
}

class Shuttle {
  final int id;
  final String vehicleNumber;
  final String routeName;
  final String? driver;
  final int? capacity;
  final ShuttleStatus status;
  final double? currentLatitude;
  final double? currentLongitude;
  final DateTime? lastLocationUpdate;

  Shuttle({
    required this.id,
    required this.vehicleNumber,
    required this.routeName,
    this.driver,
    this.capacity,
    required this.status,
    this.currentLatitude,
    this.currentLongitude,
    this.lastLocationUpdate,
  });

  factory Shuttle.fromJson(Map<String, dynamic> json) {
    return Shuttle(
      id: json['id'],
      vehicleNumber: json['vehicleNumber'],
      routeName: json['routeName'],
      driver: json['driver'],
      capacity: json['capacity'],
      status: ShuttleStatus.values.firstWhere(
        (e) => e.toString().split('.').last == json['status'],
        orElse: () => ShuttleStatus.INACTIVE,
      ),
      currentLatitude: json['currentLatitude'] != null
          ? (json['currentLatitude'] as num).toDouble()
          : null,
      currentLongitude: json['currentLongitude'] != null
          ? (json['currentLongitude'] as num).toDouble()
          : null,
      lastLocationUpdate: json['lastLocationUpdate'] != null
          ? DateTime.parse(json['lastLocationUpdate'])
          : null,
    );
  }
}
