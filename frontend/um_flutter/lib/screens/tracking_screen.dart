import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:intl/intl.dart';
import '../providers/tracking_providers.dart';
import '../models/tracking.dart';

class TrackingScreen extends ConsumerStatefulWidget {
  const TrackingScreen({super.key});

  @override
  ConsumerState<TrackingScreen> createState() => _TrackingScreenState();
}

class _TrackingScreenState extends ConsumerState<TrackingScreen> {
  bool _autoRefresh = true;

  @override
  Widget build(BuildContext context) {
    // Use StreamProvider for auto-refresh or FutureProvider for manual
    final activeShuttlesAsync = _autoRefresh
        ? ref.watch(activeShuttlesProvider)
        : ref.watch(activeShuttlesManualProvider);

    return Scaffold(
      appBar: AppBar(
        title: const Text('Shuttle Tracking'),
        actions: [
          // Auto-refresh toggle
          IconButton(
            icon: Icon(_autoRefresh ? Icons.pause_circle : Icons.play_circle),
            tooltip: _autoRefresh
                ? 'Disable auto-refresh'
                : 'Enable auto-refresh',
            onPressed: () {
              setState(() {
                _autoRefresh = !_autoRefresh;
              });
            },
          ),
          // Manual refresh button
          IconButton(
            icon: const Icon(Icons.refresh),
            tooltip: 'Refresh now',
            onPressed: () {
              if (_autoRefresh) {
                ref.invalidate(activeShuttlesProvider);
              } else {
                ref.invalidate(activeShuttlesManualProvider);
              }
            },
          ),
        ],
      ),
      body: Column(
        children: [
          // Status banner
          if (_autoRefresh)
            Container(
              width: double.infinity,
              padding: const EdgeInsets.symmetric(vertical: 8, horizontal: 16),
              color: Colors.green.shade50,
              child: Row(
                children: [
                  Icon(Icons.sync, size: 16, color: Colors.green.shade700),
                  const SizedBox(width: 8),
                  Text(
                    'Auto-refreshing every 10 seconds',
                    style: TextStyle(
                      fontSize: 12,
                      color: Colors.green.shade700,
                    ),
                  ),
                ],
              ),
            ),

          // Shuttle list
          Expanded(
            child: activeShuttlesAsync.when(
              data: (shuttles) {
                if (shuttles.isEmpty) {
                  return Center(
                    child: Column(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        Icon(
                          Icons.directions_bus_outlined,
                          size: 64,
                          color: Colors.grey.shade400,
                        ),
                        const SizedBox(height: 16),
                        Text(
                          'No active shuttles',
                          style: TextStyle(
                            fontSize: 18,
                            color: Colors.grey.shade600,
                            fontWeight: FontWeight.w500,
                          ),
                        ),
                        const SizedBox(height: 8),
                        Text(
                          'Shuttles will appear here when they become active',
                          style: TextStyle(
                            fontSize: 14,
                            color: Colors.grey.shade500,
                          ),
                        ),
                      ],
                    ),
                  );
                }

                return RefreshIndicator(
                  onRefresh: () async {
                    if (_autoRefresh) {
                      ref.invalidate(activeShuttlesProvider);
                    } else {
                      ref.invalidate(activeShuttlesManualProvider);
                    }
                  },
                  child: ListView.builder(
                    padding: const EdgeInsets.all(16),
                    itemCount: shuttles.length,
                    itemBuilder: (context, index) {
                      return _ShuttleCard(location: shuttles[index]);
                    },
                  ),
                );
              },
              loading: () => const Center(
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    CircularProgressIndicator(),
                    SizedBox(height: 16),
                    Text('Loading shuttles...'),
                  ],
                ),
              ),
              error: (error, stackTrace) {
                String errorMessage = 'An error occurred';
                IconData errorIcon = Icons.error_outline;
                Color errorColor = Colors.red;

                if (error.toString().contains('Unauthorized')) {
                  errorMessage = 'Please login to view shuttles';
                  errorIcon = Icons.lock_outline;
                  errorColor = Colors.orange;
                } else if (error.toString().contains('Network')) {
                  errorMessage = 'Network error. Check your connection.';
                  errorIcon = Icons.wifi_off;
                } else if (error.toString().contains('Server')) {
                  errorMessage = 'Server error. Please try again later.';
                  errorIcon = Icons.cloud_off;
                }

                return Center(
                  child: Padding(
                    padding: const EdgeInsets.all(32),
                    child: Column(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        Icon(errorIcon, size: 64, color: errorColor),
                        const SizedBox(height: 16),
                        Text(
                          errorMessage,
                          textAlign: TextAlign.center,
                          style: TextStyle(
                            fontSize: 16,
                            color: errorColor,
                            fontWeight: FontWeight.w500,
                          ),
                        ),
                        const SizedBox(height: 8),
                        Text(
                          error.toString(),
                          textAlign: TextAlign.center,
                          style: TextStyle(
                            fontSize: 12,
                            color: Colors.grey.shade600,
                          ),
                        ),
                        const SizedBox(height: 24),
                        ElevatedButton.icon(
                          onPressed: () {
                            if (_autoRefresh) {
                              ref.invalidate(activeShuttlesProvider);
                            } else {
                              ref.invalidate(activeShuttlesManualProvider);
                            }
                          },
                          icon: const Icon(Icons.refresh),
                          label: const Text('Retry'),
                        ),
                      ],
                    ),
                  ),
                );
              },
            ),
          ),
        ],
      ),
    );
  }
}

class _ShuttleCard extends StatelessWidget {
  final ShuttleLocation location;

  const _ShuttleCard({required this.location});

  Color _getStatusColor() {
    switch (location.status) {
      case ShuttleStatus.ACTIVE:
        return Colors.green;
      case ShuttleStatus.INACTIVE:
        return Colors.grey;
      case ShuttleStatus.MAINTENANCE:
        return Colors.orange;
      case ShuttleStatus.OUT_OF_SERVICE:
        return Colors.red;
    }
  }

  IconData _getStatusIcon() {
    switch (location.status) {
      case ShuttleStatus.ACTIVE:
        return Icons.check_circle;
      case ShuttleStatus.INACTIVE:
        return Icons.pause_circle;
      case ShuttleStatus.MAINTENANCE:
        return Icons.build_circle;
      case ShuttleStatus.OUT_OF_SERVICE:
        return Icons.cancel;
    }
  }

  @override
  Widget build(BuildContext context) {
    final statusColor = _getStatusColor();
    final statusIcon = _getStatusIcon();

    return Card(
      elevation: 2,
      margin: const EdgeInsets.only(bottom: 12),
      child: InkWell(
        onTap: () {
          // TODO: Navigate to detailed shuttle view with map
          showDialog(
            context: context,
            builder: (context) => AlertDialog(
              title: Text(location.vehicleNumber),
              content: Column(
                mainAxisSize: MainAxisSize.min,
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text('Route: ${location.routeName}'),
                  Text('Status: ${location.status.name}'),
                  const SizedBox(height: 8),
                  Text('Location:'),
                  Text(
                    'Lat: ${location.latitude.toStringAsFixed(6)}',
                    style: const TextStyle(fontFamily: 'monospace'),
                  ),
                  Text(
                    'Lng: ${location.longitude.toStringAsFixed(6)}',
                    style: const TextStyle(fontFamily: 'monospace'),
                  ),
                ],
              ),
              actions: [
                TextButton(
                  onPressed: () => Navigator.pop(context),
                  child: const Text('Close'),
                ),
              ],
            ),
          );
        },
        child: Padding(
          padding: const EdgeInsets.all(16),
          child: Row(
            children: [
              // Shuttle icon
              Container(
                padding: const EdgeInsets.all(12),
                decoration: BoxDecoration(
                  color: statusColor.withOpacity(0.1),
                  borderRadius: BorderRadius.circular(12),
                ),
                child: Icon(Icons.directions_bus, color: statusColor, size: 32),
              ),

              const SizedBox(width: 16),

              // Shuttle info
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      location.routeName,
                      style: const TextStyle(
                        fontWeight: FontWeight.bold,
                        fontSize: 16,
                      ),
                    ),
                    const SizedBox(height: 4),
                    Text(
                      location.vehicleNumber,
                      style: TextStyle(
                        color: Colors.grey.shade600,
                        fontSize: 14,
                      ),
                    ),
                    const SizedBox(height: 8),
                    Row(
                      children: [
                        Icon(statusIcon, size: 16, color: statusColor),
                        const SizedBox(width: 4),
                        Text(
                          location.status.name,
                          style: TextStyle(
                            color: statusColor,
                            fontWeight: FontWeight.w500,
                            fontSize: 12,
                          ),
                        ),
                      ],
                    ),
                    const SizedBox(height: 4),
                    Row(
                      children: [
                        Icon(
                          Icons.location_on,
                          size: 14,
                          color: Colors.grey.shade500,
                        ),
                        const SizedBox(width: 4),
                        Expanded(
                          child: Text(
                            '${location.latitude.toStringAsFixed(4)}, ${location.longitude.toStringAsFixed(4)}',
                            style: TextStyle(
                              fontSize: 11,
                              color: Colors.grey.shade600,
                            ),
                          ),
                        ),
                      ],
                    ),
                    if (location.lastUpdate != null) ...[
                      const SizedBox(height: 4),
                      Row(
                        children: [
                          Icon(
                            Icons.access_time,
                            size: 14,
                            color: Colors.grey.shade500,
                          ),
                          const SizedBox(width: 4),
                          Text(
                            _formatDateTime(location.lastUpdate!),
                            style: TextStyle(
                              fontSize: 11,
                              color: Colors.grey.shade500,
                            ),
                          ),
                        ],
                      ),
                    ],
                  ],
                ),
              ),

              // Arrow icon
              Icon(Icons.chevron_right, color: Colors.grey.shade400),
            ],
          ),
        ),
      ),
    );
  }

  String _formatDateTime(DateTime date) {
    final now = DateTime.now();
    final difference = now.difference(date);

    if (difference.inMinutes < 1) {
      return 'Just now';
    } else if (difference.inMinutes < 60) {
      return '${difference.inMinutes}m ago';
    } else if (difference.inHours < 24) {
      return '${difference.inHours}h ago';
    } else {
      return DateFormat('MMM dd, HH:mm').format(date);
    }
  }
}
