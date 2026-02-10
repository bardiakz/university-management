class Submission {
  final int id;
  final int examId;
  final String studentId;
  final String status;
  final int? totalScore;
  final int? obtainedScore;
  final DateTime? submittedAt;
  final DateTime? gradedAt;
  final String? feedback;

  Submission({
    required this.id,
    required this.examId,
    required this.studentId,
    required this.status,
    this.totalScore,
    this.obtainedScore,
    this.submittedAt,
    this.gradedAt,
    this.feedback,
  });

  factory Submission.fromJson(Map<String, dynamic> json) {
    return Submission(
      id: json['id'],
      examId: json['examId'],
      studentId: json['studentId'],
      status: json['status'],
      totalScore: json['totalScore'],
      obtainedScore: json['obtainedScore'],
      submittedAt: json['submittedAt'] != null
          ? DateTime.parse(json['submittedAt'])
          : null,
      gradedAt:
          json['gradedAt'] != null ? DateTime.parse(json['gradedAt']) : null,
      feedback: json['feedback'],
    );
  }
}
