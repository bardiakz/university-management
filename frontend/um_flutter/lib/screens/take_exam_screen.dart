import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../models/exam.dart';
import '../providers/exam_providers.dart';

class TakeExamScreen extends ConsumerStatefulWidget {
  final int examId;
  final String title;

  const TakeExamScreen({super.key, required this.examId, required this.title});

  @override
  ConsumerState<TakeExamScreen> createState() => _TakeExamScreenState();
}

class _TakeExamScreenState extends ConsumerState<TakeExamScreen> {
  final Map<int, String> _answers = {};
  bool _submitting = false;

  void _setAnswer(int questionId, String value) {
    setState(() {
      _answers[questionId] = value;
    });
  }

  Future<void> _submit(Exam exam) async {
    if (_submitting) return;

    final missing = exam.questions
        .where((q) => q.id == null || (_answers[q.id!] ?? '').trim().isEmpty)
        .toList();
    if (missing.isNotEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Please answer all questions.')),
      );
      return;
    }

    setState(() {
      _submitting = true;
    });

    try {
      await ref.read(examServiceProvider).submitExam(exam.id!, _answers);
      ref.invalidate(mySubmissionsProvider);
      if (!mounted) return;
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Exam submitted successfully.')),
      );
      Navigator.pop(context, true);
    } catch (e) {
      if (!mounted) return;
      ScaffoldMessenger.of(context)
          .showSnackBar(SnackBar(content: Text('Submit failed: $e')));
    } finally {
      if (mounted) {
        setState(() {
          _submitting = false;
        });
      }
    }
  }

  Widget _buildQuestion(Question question) {
    final id = question.id ?? -1;
    final current = _answers[id] ?? '';

    switch (question.type) {
      case QuestionType.MULTIPLE_CHOICE:
      case QuestionType.TRUE_FALSE:
        return Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            ...question.options.map(
              (option) => RadioListTile<String>(
                value: option,
                groupValue: current.isEmpty ? null : current,
                onChanged: (value) {
                  if (value == null || id < 0) return;
                  _setAnswer(id, value);
                },
                title: Text(option),
              ),
            ),
          ],
        );
      case QuestionType.SHORT_ANSWER:
        return TextFormField(
          initialValue: current,
          onChanged: (value) {
            if (id < 0) return;
            _setAnswer(id, value);
          },
          decoration: const InputDecoration(
            labelText: 'Your answer',
            border: OutlineInputBorder(),
          ),
          minLines: 2,
          maxLines: 4,
        );
    }
  }

  @override
  Widget build(BuildContext context) {
    final examAsync = ref.watch(examDetailsProvider(widget.examId));
    final submissionsAsync = ref.watch(mySubmissionsProvider);

    return Scaffold(
      appBar: AppBar(
        title: Text(widget.title),
      ),
      body: examAsync.when(
        data: (exam) {
          return submissionsAsync.when(
            data: (submissions) {
              final alreadySubmitted =
                  submissions.any((s) => s.examId == exam.id);
              if (alreadySubmitted) {
                return const Center(
                  child: Text('You have already submitted this exam.'),
                );
              }
              if (exam.status != ExamStatus.ACTIVE) {
                return const Center(child: Text('This exam is not active yet.'));
              }
              return ListView.builder(
                padding: const EdgeInsets.all(16),
                itemCount: exam.questions.length + 1,
                itemBuilder: (context, index) {
                  if (index == exam.questions.length) {
                    return Padding(
                      padding: const EdgeInsets.only(top: 16),
                      child: SizedBox(
                        width: double.infinity,
                        child: ElevatedButton(
                          onPressed: _submitting ? null : () => _submit(exam),
                          child: _submitting
                              ? const SizedBox(
                                  width: 20,
                                  height: 20,
                                  child:
                                      CircularProgressIndicator(strokeWidth: 2),
                                )
                              : const Text('Submit Exam'),
                        ),
                      ),
                    );
                  }

                  final question = exam.questions[index];
                  return Card(
                    margin: const EdgeInsets.only(bottom: 16),
                    child: Padding(
                      padding: const EdgeInsets.all(16),
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text(
                            'Q${index + 1}: ${question.text}',
                            style: Theme.of(context).textTheme.titleMedium,
                          ),
                          const SizedBox(height: 12),
                          _buildQuestion(question),
                        ],
                      ),
                    ),
                  );
                },
              );
            },
            loading: () => const Center(child: CircularProgressIndicator()),
            error: (err, stack) => Center(child: Text('Error: $err')),
          );
        },
        loading: () => const Center(child: CircularProgressIndicator()),
        error: (err, stack) => Center(child: Text('Error: $err')),
      ),
    );
  }
}
