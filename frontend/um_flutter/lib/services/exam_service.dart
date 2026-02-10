import 'dart:convert';
import 'package:http/http.dart' as http;
import '../models/exam.dart';
import '../models/submission.dart';
import 'api_service.dart';

class ExamService {
  final String? _token;

  ExamService(this._token);

  Map<String, String> _getHeaders() {
    final headers = {'Content-Type': 'application/json'};
    if (_token != null) {
      headers['Authorization'] = 'Bearer $_token';
    }
    return headers;
  }

  Future<List<Exam>> getMyExams() async {
    try {
      final response = await http
          .get(
            Uri.parse('${ApiService.baseUrl}/api/exams/instructor/my-exams'),
            headers: _getHeaders(),
          )
          .timeout(const Duration(seconds: 10));

      if (response.statusCode == 200) {
        final List<dynamic> data = jsonDecode(response.body);
        return data.map((json) => Exam.fromJson(json)).toList();
      } else if (response.statusCode == 401) {
        throw AuthException('Unauthorized');
      } else {
        throw ServerException('Failed to load exams');
      }
    } catch (e) {
      if (e is AuthException || e is ServerException) rethrow;
      throw NetworkException('Error loading exams: ${e.toString()}');
    }
  }

  Future<List<Exam>> getActiveExams() async {
    try {
      final response = await http
          .get(
            Uri.parse('${ApiService.baseUrl}/api/exams/active'),
            headers: _getHeaders(),
          )
          .timeout(const Duration(seconds: 10));

      if (response.statusCode == 200) {
        final List<dynamic> data = jsonDecode(response.body);
        return data.map((json) => Exam.fromJson(json)).toList();
      } else if (response.statusCode == 401) {
        throw AuthException('Unauthorized');
      } else {
        throw ServerException('Failed to load active exams');
      }
    } catch (e) {
      if (e is AuthException || e is ServerException) rethrow;
      throw NetworkException('Error loading active exams: ${e.toString()}');
    }
  }

  Future<List<Exam>> getUpcomingExams() async {
    try {
      final response = await http
          .get(
            Uri.parse('${ApiService.baseUrl}/api/exams/upcoming'),
            headers: _getHeaders(),
          )
          .timeout(const Duration(seconds: 10));

      if (response.statusCode == 200) {
        final List<dynamic> data = jsonDecode(response.body);
        return data.map((json) => Exam.fromJson(json)).toList();
      } else if (response.statusCode == 401) {
        throw AuthException('Unauthorized');
      } else {
        throw ServerException('Failed to load upcoming exams');
      }
    } catch (e) {
      if (e is AuthException || e is ServerException) rethrow;
      throw NetworkException('Error loading upcoming exams: ${e.toString()}');
    }
  }

  Future<Exam> getExamById(int id) async {
    try {
      final response = await http
          .get(
            Uri.parse('${ApiService.baseUrl}/api/exams/$id'),
            headers: _getHeaders(),
          )
          .timeout(const Duration(seconds: 10));

      if (response.statusCode == 200) {
        return Exam.fromJson(jsonDecode(response.body));
      } else if (response.statusCode == 401) {
        throw AuthException('Unauthorized');
      } else {
        throw ServerException('Failed to load exam details');
      }
    } catch (e) {
      if (e is AuthException || e is ServerException) rethrow;
      throw NetworkException('Error loading exam details: ${e.toString()}');
    }
  }

  Future<Exam> createExam(Exam exam) async {
    // Manually construct body to match backend ExamRequest DTO exactly
    // and avoid sending read-only fields like id, status, etc.
    final body = {
      'title': exam.title,
      'description': exam.description,
      'startTime': exam.startTime.toIso8601String(),
      'endTime': exam.endTime.toIso8601String(),
      'durationMinutes': exam.durationMinutes,
      'totalMarks': exam.totalMarks,
      'questions': exam.questions
          .map(
            (q) => {
              'text': q.text,
              'type': q.type.toString().split('.').last,
              'options': q.options,
              'correctAnswer': q.correctAnswer,
              'marks': q.marks,
              'orderNumber': q.orderNumber,
            },
          )
          .toList(),
    };

    try {
      final response = await http
          .post(
            Uri.parse('${ApiService.baseUrl}/api/exams'),
            headers: _getHeaders(),
            body: jsonEncode(body),
          )
          .timeout(const Duration(seconds: 10));

      if (response.statusCode == 201) {
        return Exam.fromJson(jsonDecode(response.body));
      } else if (response.statusCode == 401) {
        throw AuthException('Unauthorized');
      } else {
        final error = jsonDecode(response.body);
        throw ServerException(error['error'] ?? 'Failed to create exam');
      }
    } catch (e) {
      if (e is AuthException || e is ServerException) rethrow;
      throw NetworkException('Error creating exam: ${e.toString()}');
    }
  }

  Future<Exam> publishExam(int id) async {
    try {
      final response = await http
          .post(
            Uri.parse('${ApiService.baseUrl}/api/exams/$id/publish'),
            headers: _getHeaders(),
          )
          .timeout(const Duration(seconds: 10));

      if (response.statusCode == 200) {
        return Exam.fromJson(jsonDecode(response.body));
      } else if (response.statusCode == 401) {
        throw AuthException('Unauthorized');
      } else {
        final error = jsonDecode(response.body);
        throw ServerException(error['error'] ?? 'Failed to publish exam');
      }
    } catch (e) {
      if (e is AuthException || e is ServerException) rethrow;
      throw NetworkException('Error publishing exam: ${e.toString()}');
    }
  }

  Future<void> submitExam(int examId, Map<int, String> answers) async {
    final payload = {
      'examId': examId,
      'answers': answers.entries
          .map(
            (entry) => {
              'questionId': entry.key,
              'answerText': entry.value,
            },
          )
          .toList(),
    };

    try {
      final response = await http
          .post(
            Uri.parse('${ApiService.baseUrl}/api/submissions'),
            headers: _getHeaders(),
            body: jsonEncode(payload),
          )
          .timeout(const Duration(seconds: 10));

      if (response.statusCode == 201) {
        return;
      } else if (response.statusCode == 401) {
        throw AuthException('Unauthorized');
      } else {
        final error = jsonDecode(response.body);
        throw ServerException(error['error'] ?? 'Failed to submit exam');
      }
    } catch (e) {
      if (e is AuthException || e is ServerException) rethrow;
      throw NetworkException('Error submitting exam: ${e.toString()}');
    }
  }

  Future<List<Submission>> getMySubmissions() async {
    try {
      final response = await http
          .get(
            Uri.parse('${ApiService.baseUrl}/api/submissions/my-submissions'),
            headers: _getHeaders(),
          )
          .timeout(const Duration(seconds: 10));

      if (response.statusCode == 200) {
        final List<dynamic> data = jsonDecode(response.body);
        return data.map((json) => Submission.fromJson(json)).toList();
      } else if (response.statusCode == 401) {
        throw AuthException('Unauthorized');
      } else {
        throw ServerException('Failed to load submissions');
      }
    } catch (e) {
      if (e is AuthException || e is ServerException) rethrow;
      throw NetworkException('Error loading submissions: ${e.toString()}');
    }
  }

  Future<List<Submission>> getSubmissionsByExam(int examId) async {
    try {
      final response = await http
          .get(
            Uri.parse('${ApiService.baseUrl}/api/submissions/exam/$examId'),
            headers: _getHeaders(),
          )
          .timeout(const Duration(seconds: 10));

      if (response.statusCode == 200) {
        final List<dynamic> data = jsonDecode(response.body);
        return data.map((json) => Submission.fromJson(json)).toList();
      } else if (response.statusCode == 401) {
        throw AuthException('Unauthorized');
      } else {
        throw ServerException('Failed to load submissions');
      }
    } catch (e) {
      if (e is AuthException || e is ServerException) rethrow;
      throw NetworkException('Error loading submissions: ${e.toString()}');
    }
  }
}
