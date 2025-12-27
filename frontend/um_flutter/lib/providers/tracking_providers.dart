import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'app_providers.dart';
import '../services/tracking_service.dart';
import '../models/tracking.dart';

final trackingServiceProvider = Provider<TrackingService>((ref) {
  final authState = ref.watch(authProvider);
  final token = authState.value?.token;
  return TrackingService(token);
});

final activeShuttlesProvider = FutureProvider.autoDispose<List<ShuttleLocation>>((ref) async {
  final service = ref.watch(trackingServiceProvider);
  return service.getActiveShuttles();
});
