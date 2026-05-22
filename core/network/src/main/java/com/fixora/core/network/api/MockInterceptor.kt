package com.fixora.core.network.api

import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import java.util.UUID

class MockInterceptor : Interceptor {

    private val categoriesJson = """
        [
            {"id": "cat_1", "name": "Plumbing", "description": "Fix pipe leaks, unclog drains, install faucets", "iconName": "plumbing"},
            {"id": "cat_2", "name": "Electrical", "description": "Wiring, lighting installations, repair circuits", "iconName": "electrical"},
            {"id": "cat_3", "name": "Cleaning", "description": "Deep home cleaning, disinfection, vacuuming", "iconName": "cleaning"},
            {"id": "cat_4", "name": "Painting", "description": "Interior and exterior wall painting, touch-ups", "iconName": "painting"},
            {"id": "cat_5", "name": "AC Repair", "description": "Air conditioner servicing, filter cleaning, cooling issues", "iconName": "ac_repair"}
        ]
    """.trimIndent()

    private val providersJson = """
        [
            {
                "id": "prov_1",
                "name": "Alex Rivera",
                "title": "Master Plumber",
                "categoryId": "cat_1",
                "rating": 4.8,
                "reviewCount": 124,
                "pricePerHour": 45.0,
                "bio": "Certified plumber with 8+ years of experience in residential repairs. Prompt, clean, and reliable. Specializes in leak detection and bathroom remodels.",
                "avatarUrl": "https://images.unsplash.com/photo-1540569014015-19a7be504e3a?auto=format&fit=crop&q=80&w=200",
                "workImages": [
                    "https://images.unsplash.com/photo-1584622650111-993a426fbf0a?auto=format&fit=crop&q=80&w=400",
                    "https://images.unsplash.com/photo-1504148455328-c376907d081c?auto=format&fit=crop&q=80&w=400"
                ],
                "reviews": [
                    {"id": "rev_1_1", "reviewerName": "Sophia Bennett", "reviewerAvatarUrl": null, "rating": 5.0, "comment": "Alex fixed our kitchen sink leak in under an hour. Very professional!", "date": "May 12, 2026"},
                    {"id": "rev_1_2", "reviewerName": "Liam Johnson", "reviewerAvatarUrl": null, "rating": 4.5, "comment": "Did a great job installing the new faucet. Arrived on time.", "date": "May 08, 2026"}
                ],
                "availableSlots": ["09:00 AM", "11:00 AM", "01:30 PM", "03:30 PM"]
            },
            {
                "id": "prov_2",
                "name": "Marcus Chen",
                "title": "Licensed Electrician",
                "categoryId": "cat_2",
                "rating": 4.9,
                "reviewCount": 98,
                "pricePerHour": 50.0,
                "bio": "Experienced commercial and residential electrician. Safety-first mindset. Can handle everything from replacing switches to rewiring old homes.",
                "avatarUrl": "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&q=80&w=200",
                "workImages": [
                    "https://images.unsplash.com/photo-1621905251189-08b45d6a269e?auto=format&fit=crop&q=80&w=400"
                ],
                "reviews": [
                    {"id": "rev_2_1", "reviewerName": "Emma Davis", "reviewerAvatarUrl": null, "rating": 5.0, "comment": "Marcus installed dimmers and new lights throughout our living room. Super clean work!", "date": "May 14, 2026"}
                ],
                "availableSlots": ["10:00 AM", "12:30 PM", "03:00 PM"]
            },
            {
                "id": "prov_3",
                "name": "Sarah Jenkins",
                "title": "Professional Cleaner",
                "categoryId": "cat_3",
                "rating": 4.7,
                "reviewCount": 85,
                "pricePerHour": 30.0,
                "bio": "Detail-oriented cleaner providing environment-friendly sanitization services. I bring my own supply of eco-friendly cleaning agents.",
                "avatarUrl": "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&q=80&w=200",
                "workImages": [
                    "https://images.unsplash.com/photo-1581578731548-c64695cc6952?auto=format&fit=crop&q=80&w=400"
                ],
                "reviews": [
                    {"id": "rev_3_1", "reviewerName": "Olivia Martinez", "reviewerAvatarUrl": null, "rating": 4.0, "comment": "House is sparkling clean. Will book again.", "date": "May 15, 2026"}
                ],
                "availableSlots": ["08:30 AM", "12:00 PM", "02:30 PM"]
            },
            {
                "id": "prov_4",
                "name": "Elena Rostova",
                "title": "Interior Painter",
                "categoryId": "cat_4",
                "rating": 4.6,
                "reviewCount": 54,
                "pricePerHour": 35.0,
                "bio": "Transforming spaces with color! Specializing in wall prep, precise borders, and wallpaper application. Let's make your home beautiful.",
                "avatarUrl": "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&q=80&w=200",
                "workImages": [
                    "https://images.unsplash.com/photo-1562259949-e8e7689d7828?auto=format&fit=crop&q=80&w=400"
                ],
                "reviews": [
                    {"id": "rev_4_1", "reviewerName": "Noah Taylor", "reviewerAvatarUrl": null, "rating": 5.0, "comment": "Fantastic paint job in our bedroom! Perfect lines and no mess.", "date": "May 01, 2026"}
                ],
                "availableSlots": ["09:00 AM", "02:00 PM"]
            },
            {
                "id": "prov_5",
                "name": "David Kim",
                "title": "AC HVAC Technician",
                "categoryId": "cat_5",
                "rating": 4.9,
                "reviewCount": 142,
                "pricePerHour": 55.0,
                "bio": "Specialist in heating, ventilation, and air conditioning. Diagnoses cooling faults instantly. Fast parts replacement and unit cleaning services.",
                "avatarUrl": "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&q=80&w=200",
                "workImages": [
                    "https://images.unsplash.com/photo-1527689368864-3a821dbccc34?auto=format&fit=crop&q=80&w=400"
                ],
                "reviews": [
                    {"id": "rev_5_1", "reviewerName": "Mason Williams", "reviewerAvatarUrl": null, "rating": 5.0, "comment": "AC was blowing warm air. David found a freon leak and fixed it in 30 mins. A lifesaver in this heat!", "date": "May 19, 2026"}
                ],
                "availableSlots": ["11:00 AM", "01:00 PM", "03:00 PM", "05:00 PM"]
            }
        ]
    """.trimIndent()

    // Temporary in-memory storage for mock bookings
    private val bookingsList = mutableListOf<String>()

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url.toString()
        val method = request.method

        // Simulate network latency (800ms)
        Thread.sleep(800)

        val responseBuilder = Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .message("OK")

        val mediaType = "application/json".toMediaTypeOrNull()

        return when {
            // Service Categories
            url.contains("categories") && method == "GET" -> {
                responseBuilder.code(200)
                    .body(categoriesJson.toResponseBody(mediaType))
                    .build()
            }

            // Providers details or list
            url.contains("providers") && method == "GET" -> {
                val pathSegments = request.url.pathSegments
                val lastSegment = pathSegments.lastOrNull()

                if (lastSegment != null && lastSegment.startsWith("prov_")) {
                    // Specific provider
                    val providerJson = getSingleProviderJson(lastSegment)
                    if (providerJson != null) {
                        responseBuilder.code(200)
                            .body(providerJson.toResponseBody(mediaType))
                            .build()
                    } else {
                        responseBuilder.code(404)
                            .message("Provider not found")
                            .body("{}".toResponseBody(mediaType))
                            .build()
                    }
                } else {
                    // Providers list (optionally filtered)
                    val categoryId = request.url.queryParameter("categoryId")
                    val query = request.url.queryParameter("q")
                    val filteredJson = filterProviders(categoryId, query)
                    responseBuilder.code(200)
                        .body(filteredJson.toResponseBody(mediaType))
                        .build()
                }
            }

            // Authentication - Login
            url.contains("auth/login") && method == "POST" -> {
                // Parse request body or return standard mock user
                val mockUserJson = """
                    {
                        "id": "usr_999",
                        "name": "John Doe",
                        "email": "john.doe@example.com",
                        "profileImageUrl": "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&q=80&w=200",
                        "location": "San Francisco, CA",
                        "role": "CUSTOMER"
                    }
                """.trimIndent()
                responseBuilder.code(200)
                    .body(mockUserJson.toResponseBody(mediaType))
                    .build()
            }

            // Authentication - Register
            url.contains("auth/register") && method == "POST" -> {
                val mockUserJson = """
                    {
                        "id": "usr_999",
                        "name": "New User",
                        "email": "user@example.com",
                        "profileImageUrl": null,
                        "location": "San Francisco, CA",
                        "role": "CUSTOMER"
                    }
                """.trimIndent()
                responseBuilder.code(200)
                    .body(mockUserJson.toResponseBody(mediaType))
                    .build()
            }

            // Bookings List
            url.contains("bookings") && method == "GET" -> {
                val listAsString = bookingsList.joinToString(separator = ",", prefix = "[", postfix = "]")
                responseBuilder.code(200)
                    .body(listAsString.toResponseBody(mediaType))
                    .build()
            }

            // Create Booking
            url.contains("bookings") && method == "POST" -> {
                val body = request.body
                val buffer = okio.Buffer()
                body?.writeTo(buffer)
                val bodyString = buffer.readUtf8()

                // Extract fields dynamically via simple string parsers
                val providerId = extractJsonValue(bodyString, "providerId") ?: "prov_1"
                val dateTime = extractJsonValue(bodyString, "dateTime") ?: "May 22, 2026"
                val timeSlot = extractJsonValue(bodyString, "timeSlot") ?: "10:00 AM"
                val issueDescription = extractJsonValue(bodyString, "issueDescription") ?: "Routine check"
                val attachmentUrl = extractJsonValue(bodyString, "attachmentUrl")

                val providerName = when (providerId) {
                    "prov_1" -> "Alex Rivera"
                    "prov_2" -> "Marcus Chen"
                    "prov_3" -> "Sarah Jenkins"
                    "prov_4" -> "Elena Rostova"
                    "prov_5" -> "David Kim"
                    else -> "Fixora Professional"
                }

                val categoryName = when (providerId) {
                    "prov_1" -> "Plumbing"
                    "prov_2" -> "Electrical"
                    "prov_3" -> "Cleaning"
                    "prov_4" -> "Painting"
                    "prov_5" -> "AC Repair"
                    else -> "Home Maintenance"
                }

                val providerPrice = when (providerId) {
                    "prov_1" -> 45.0
                    "prov_2" -> 50.0
                    "prov_3" -> 30.0
                    "prov_4" -> 35.0
                    "prov_5" -> 55.0
                    else -> 40.0
                }

                val providerAvatarUrl = when (providerId) {
                    "prov_1" -> "https://images.unsplash.com/photo-1540569014015-19a7be504e3a?auto=format&fit=crop&q=80&w=200"
                    "prov_2" -> "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&q=80&w=200"
                    "prov_3" -> "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&q=80&w=200"
                    "prov_4" -> "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&q=80&w=200"
                    "prov_5" -> "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&q=80&w=200"
                    else -> null
                }

                val newBookingJson = """
                    {
                        "id": "book_${UUID.randomUUID().toString().take(8)}",
                        "userId": "usr_999",
                        "providerId": "$providerId",
                        "providerName": "$providerName",
                        "providerAvatarUrl": ${if (providerAvatarUrl != null) "\"$providerAvatarUrl\"" else "null"},
                        "categoryName": "$categoryName",
                        "dateTime": "$dateTime",
                        "timeSlot": "$timeSlot",
                        "status": "CONFIRMED",
                        "issueDescription": "$issueDescription",
                        "attachmentUrl": ${if (attachmentUrl != null) "\"$attachmentUrl\"" else "null"},
                        "totalPrice": ${providerPrice * 2.0}
                    }
                """.trimIndent()

                bookingsList.add(newBookingJson)

                responseBuilder.code(200)
                    .body(newBookingJson.toResponseBody(mediaType))
                    .build()
            }

            else -> {
                responseBuilder.code(404)
                    .message("Endpoint not mocked")
                    .body("{}".toResponseBody(mediaType))
                    .build()
            }
        }
    }

    private fun getSingleProviderJson(providerId: String): String? {
        val providers = filterProviders(null, null)
        // A simple matcher to extract the specific block by ID
        // Note: In mock environment, parsing lists is easiest by finding the specific index
        return when (providerId) {
            "prov_1" -> extractProviderBlock(providers, "prov_1")
            "prov_2" -> extractProviderBlock(providers, "prov_2")
            "prov_3" -> extractProviderBlock(providers, "prov_3")
            "prov_4" -> extractProviderBlock(providers, "prov_4")
            "prov_5" -> extractProviderBlock(providers, "prov_5")
            else -> null
        }
    }

    private fun extractProviderBlock(allProviders: String, id: String): String? {
        // Fallback: we return the exact provider block since we know they exist
        val index = allProviders.indexOf("\"id\": \"$id\"")
        if (index == -1) return null

        // Trace backward to '{' and forward to matching '}'
        var start = index
        while (start > 0 && allProviders[start] != '{') {
            start--
        }

        var bracketCount = 0
        var end = start
        while (end < allProviders.length) {
            if (allProviders[end] == '{') bracketCount++
            if (allProviders[end] == '}') {
                bracketCount--
                if (bracketCount == 0) {
                    break
                }
            }
            end++
        }
        return allProviders.substring(start, end + 1)
    }

    private fun filterProviders(categoryId: String?, query: String?): String {
        // Simulating filters
        if (categoryId == null && query == null) return providersJson

        // Minimal manual JSON filtering
        val list = mutableListOf<String>()
        val matches = listOf("prov_1", "prov_2", "prov_3", "prov_4", "prov_5")
        for (id in matches) {
            val providerBlock = getSingleProviderJson(id) ?: continue
            var include = true
            if (categoryId != null && !providerBlock.contains("\"categoryId\": \"$categoryId\"")) {
                include = false
            }
            if (query != null) {
                val q = query.lowercase()
                val name = extractJsonValue(providerBlock, "name")?.lowercase() ?: ""
                val title = extractJsonValue(providerBlock, "title")?.lowercase() ?: ""
                val bio = extractJsonValue(providerBlock, "bio")?.lowercase() ?: ""
                if (!name.contains(q) && !title.contains(q) && !bio.contains(q)) {
                    include = false
                }
            }
            if (include) {
                list.add(providerBlock)
            }
        }
        return list.joinToString(separator = ",", prefix = "[", postfix = "]")
    }

    private fun extractJsonValue(json: String, key: String): String? {
        val pattern = "\"$key\"\\s*:\\s*\"([^\"]+)\"".toRegex()
        return pattern.find(json)?.groupValues?.get(1)
    }
}
