import 'dart:convert';
import 'package:http/http.dart' as http;
import '../models/product.dart';
import '../models/order.dart';
import 'api_service.dart';

class MarketplaceService {
  final String? _token;

  MarketplaceService(this._token);

  Map<String, String> _getHeaders() {
    final headers = {'Content-Type': 'application/json'};
    if (_token != null) {
      headers['Authorization'] = 'Bearer $_token';
    }
    return headers;
  }

  Future<List<Product>> getProducts() async {
    try {
      final response = await http
          .get(
            Uri.parse('${ApiService.baseUrl}/api/marketplace/products'),
            headers: _getHeaders(),
          )
          .timeout(const Duration(seconds: 10));

      if (response.statusCode == 200) {
        final List<dynamic> data = jsonDecode(response.body);
        return data.map((json) => Product.fromJson(json)).toList();
      } else if (response.statusCode == 401) {
        throw AuthException('Unauthorized');
      } else {
        throw ServerException('Failed to load products');
      }
    } catch (e) {
      if (e is AuthException || e is ServerException) rethrow;
      throw NetworkException('Error loading products: ${e.toString()}');
    }
  }

  Future<MarketOrder> createOrder(int productId, int quantity) async {
    try {
      // Backend expects a list of items: { "items": [ { "productId": 1, "quantity": 2 } ] }
      final response = await http
          .post(
            Uri.parse('${ApiService.baseUrl}/api/marketplace/orders'),
            headers: _getHeaders(),
            body: jsonEncode({
              'items': [
                {
                  'productId': productId,
                  'quantity': quantity,
                }
              ],
            }),
          )
          .timeout(const Duration(seconds: 10));

      if (response.statusCode == 201 || response.statusCode == 200) {
        return MarketOrder.fromJson(jsonDecode(response.body));
      } else if (response.statusCode == 401) {
        throw AuthException('Unauthorized');
      } else {
        final error = jsonDecode(response.body);
        throw ServerException(error['error'] ?? 'Failed to create order');
      }
    } catch (e) {
      if (e is AuthException || e is ServerException) rethrow;
      throw NetworkException('Error creating order: ${e.toString()}');
    }
  }

  Future<List<MarketOrder>> getMyOrders() async {
    try {
      final response = await http
          .get(
            Uri.parse('${ApiService.baseUrl}/api/marketplace/orders/my-orders'),
            headers: _getHeaders(),
          )
          .timeout(const Duration(seconds: 10));

      if (response.statusCode == 200) {
        final List<dynamic> data = jsonDecode(response.body);
        return data.map((json) => MarketOrder.fromJson(json)).toList();
      } else if (response.statusCode == 401) {
        throw AuthException('Unauthorized');
      } else {
        throw ServerException('Failed to load orders');
      }
    } catch (e) {
      if (e is AuthException || e is ServerException) rethrow;
      throw NetworkException('Error loading orders: ${e.toString()}');
    }
  }
}