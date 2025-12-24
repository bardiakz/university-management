notification-event-contract.md
	•	specVersion: "1.0"
	•	eventId: UUID
	•	domain: یکی از این‌ها:
	•	USER_AUTH
	•	MARKETPLACE_ORDERS
	•	BOOKING_RESOURCES
	•	PAYMENTS
	•	EXAMS
	•	SYSTEM_ADMIN
	•	FAILURE_EXCEPTION_INTERNAL
	•	eventType: مثل UserRegistered, ExamScheduled, …
	•	occurredAt: زمان UTC (ISO-8601)
	•	correlationId: برای ردیابی جریان (اختیاری ولی توصیه‌شده)
	•	producer.service: نام سرویس تولیدکننده (مثلاً exam-service)
	•	recipient.mode:
	•	USER → باید userId داشته باشد (email اختیاری)
	•	BROADCAST → برای اعلان عمومی
	•	channelHints: آرایه‌ای از EMAIL, IN_APP (پیشنهاد است)
	•	priority: LOW|MEDIUM|HIGH|CRITICAL
	•	idempotencyKey: کلید ضد تکرار
	•	payload: دیتاهای مخصوص همان eventType

قاعده idempotencyKey
	•	شخصی: DOMAIN|eventType|userId|businessId
	•	عمومی: DOMAIN|eventType|broadcast|businessId
