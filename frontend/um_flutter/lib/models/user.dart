class User {
  final String username;
  final String token;
  String? role;
  String? email;

  User({required this.username, required this.token, this.role, this.email});

  factory User.fromJson(Map<String, dynamic> json) {
    return User(
      username: json['username'],
      token: json['token'],
      role: json['role'],
      email: json['email'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'username': username,
      'token': token,
      'role': role,
      'email': email,
    };
  }
}

enum UserRole { ADMIN, STUDENT, INSTRUCTOR, FACULTY }
