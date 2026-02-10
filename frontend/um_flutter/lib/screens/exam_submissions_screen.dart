import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:intl/intl.dart';
import '../providers/exam_providers.dart';

class ExamSubmissionsScreen extends ConsumerWidget {
  final int examId;
  final String title;

  const ExamSubmissionsScreen({
    super.key,
    required this.examId,
    required this.title,
  });

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final submissionsAsync = ref.watch(examSubmissionsProvider(examId));
    final dateFormat = DateFormat('MMM d, h:mm a');

    return Scaffold(
      appBar: AppBar(title: Text('Submissions - $title')),
      body: submissionsAsync.when(
        data: (submissions) {
          if (submissions.isEmpty) {
            return const Center(child: Text('No submissions yet.'));
          }
          return ListView.builder(
            padding: const EdgeInsets.all(16),
            itemCount: submissions.length,
            itemBuilder: (context, index) {
              final submission = submissions[index];
              final scoreText = submission.obtainedScore != null &&
                      submission.totalScore != null
                  ? '${submission.obtainedScore}/${submission.totalScore}'
                  : 'Pending';

              return Card(
                margin: const EdgeInsets.only(bottom: 16),
                child: Padding(
                  padding: const EdgeInsets.all(16),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        submission.studentId,
                        style: Theme.of(context).textTheme.titleMedium,
                      ),
                      const SizedBox(height: 8),
                      Text('Status: ${submission.status}'),
                      Text('Score: $scoreText'),
                      if (submission.submittedAt != null)
                        Text(
                          'Submitted: ${dateFormat.format(submission.submittedAt!)}',
                        ),
                    ],
                  ),
                ),
              );
            },
          );
        },
        loading: () => const Center(child: CircularProgressIndicator()),
        error: (err, stack) => Center(child: Text('Error: $err')),
      ),
    );
  }
}
