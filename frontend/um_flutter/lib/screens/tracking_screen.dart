import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../providers/tracking_providers.dart';
import '../models/tracking.dart';

class TrackingScreen extends ConsumerWidget {
  const TrackingScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final activeShuttlesAsync = ref.watch(activeShuttlesProvider);

    return Scaffold(
      appBar: AppBar(
        title: const Text('Shuttle Tracking'),
        actions: [
          IconButton(
            icon: const Icon(Icons.refresh),
            onPressed: () => ref.invalidate(activeShuttlesProvider),
          ),
        ],
      ),
      body: activeShuttlesAsync.when(
        data: (shuttles) {
          if (shuttles.isEmpty) {
            return const Center(child: Text('No active shuttles found.'));
          }
          return ListView.builder(
            padding: const EdgeInsets.all(16),
            itemCount: shuttles.length,
            itemBuilder: (context, index) {
              return _ShuttleCard(location: shuttles[index]);
            },
          );
        },
        loading: () => const Center(child: CircularProgressIndicator()),
        error: (e, stack) => Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              const Icon(Icons.error_outline, size: 48, color: Colors.red),
              const SizedBox(height: 16),
              Text('Error: $e', textAlign: TextAlign.center),
              const SizedBox(height: 16),
              ElevatedButton(
                onPressed: () => ref.invalidate(activeShuttlesProvider),
                child: const Text('Retry'),
              ),
            ],
          ),
        ),
      ),
    );
  }
}

class _ShuttleCard extends StatelessWidget {
  final ShuttleLocation location;

  const _ShuttleCard({required this.location});

  @override
  Widget build(BuildContext context) {
    final color = location.status == ShuttleStatus.ACTIVE ? Colors.green : Colors.grey;

    return Card(
      child: ListTile(
        leading: Icon(Icons.directions_bus, color: color, size: 32),
        title: Text(
          location.routeName,
          style: const TextStyle(fontWeight: FontWeight.bold),
        ),
        subtitle: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text('Vehicle: ${location.vehicleNumber}'),
            Text('Status: ${location.status.name}'),
            const SizedBox(height: 4),
            Row(
              children: [
                const Icon(Icons.location_on, size: 16, color: Colors.blue),
                const SizedBox(width: 4),
                Expanded(
                  child: Text(
                    '${location.latitude.toStringAsFixed(4)}, ${location.longitude.toStringAsFixed(4)}',
                    style: const TextStyle(fontSize: 12),
                  ),
                ),
              ],
            ),
            if (location.lastUpdate != null)
              Text(
                'Last Updated: ${_formatDate(location.lastUpdate!)}',
                style: const TextStyle(fontSize: 10, color: Colors.grey),
              ),
          ],
        ),
        isThreeLine: true,
      ),
    );
  }

  String _formatDate(DateTime date) {
    // Simple formatter, can use intl package if available
    return '${date.hour.toString().padLeft(2, '0')}:${date.minute.toString().padLeft(2, '0')}';
  }
}
