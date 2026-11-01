package com.dinuka.automation.utils.api.playwright;

import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.options.RequestOptions;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class PWAPIUtils {

        private final APIRequestContext apiContext;
        public static final String AUTHORIZATION = "Authorization";
        public static final String BEARER = "Bearer";
        public static final String CONTENT_TYPE = "Content-Type";
        public static final String APPLICATION_JSON = "application/json";

        public PWAPIUtils(APIRequestContext apiContext) {
                this.apiContext = apiContext;
        }

        // =========================================================
        // INTERNAL HELPER
        // =========================================================

        // Build RequestOptions with headers, query params, and body
        private RequestOptions buildOptions(
                        Map<String, String> headers,
                        Map<String, String> queryParams,
                        Object body) {
                RequestOptions options = RequestOptions.create();

                if (headers != null) {
                        headers.forEach(options::setHeader);
                }

                if (queryParams != null) {
                        queryParams.forEach(options::setQueryParam);
                }

                if (body != null) {
                        options.setData(body);
                }

                return options;
        }

        // =========================================================
        // GET REQUESTS
        // =========================================================

        // Basic GET request
        public APIResponse get(String endpoint) {
                return apiContext.get(endpoint);
        }

        // GET with query parameters
        public APIResponse getWithQueryParams(
                        String endpoint,
                        Map<String, String> queryParams) {

                return apiContext.get(
                                endpoint,
                                buildOptions(null, queryParams, null));
        }

        // GET with custom headers only - original
        public APIResponse getWithHeaders(
                        String endpoint,
                        Map<String, String> headers) {

                return apiContext.get(
                                endpoint,
                                buildOptions(headers, null, null));
        }

        // GET with both headers and query parameters
        public APIResponse getWithHeadersAndParams(
                        String endpoint,
                        Map<String, String> headers,
                        Map<String, String> queryParams) {

                return apiContext.get(
                                endpoint,
                                buildOptions(headers, queryParams, null));
        }

        // GET with Bearer Token authentication
        public APIResponse getWithBearerToken(
                        String endpoint,
                        String bearerToken) {

                return getWithHeaders(
                                endpoint,
                                Map.of(AUTHORIZATION, BEARER + bearerToken));
        }

        // GET with Basic Authentication (username and password)
        public APIResponse getWithBasicAuth(
                        String endpoint,
                        String username,
                        String password) {

                String basicAuth = Base64.getEncoder()
                                .encodeToString((username + ":" + password).getBytes());

                return getWithHeaders(
                                endpoint,
                                Map.of(AUTHORIZATION, "Basic " + basicAuth));
        }

        // GET with API Key in header
        public APIResponse getWithApiKey(
                        String endpoint,
                        String apiKeyName,
                        String apiKeyValue) {

                return getWithHeaders(
                                endpoint,
                                Map.of(apiKeyName, apiKeyValue));
        }

        // GET with cookies
        public APIResponse getWithCookies(
                        String endpoint,
                        Map<String, String> cookies) {

                String cookieHeader = cookies.entrySet()
                                .stream()
                                .map(e -> e.getKey() + "=" + e.getValue())
                                .collect(Collectors.joining("; "));

                return getWithHeaders(
                                endpoint,
                                Map.of("Cookie", cookieHeader));
        }

        // =========================================================
        // POST REQUESTS
        // =========================================================

        // POST with raw JSON (string body)
        public APIResponse postJson(
                        String endpoint,
                        String jsonBody) {

                return apiContext.post(
                                endpoint,
                                buildOptions(
                                                Map.of(CONTENT_TYPE, APPLICATION_JSON),
                                                null,
                                                jsonBody));
        }

        // POST with headers and JSON body
        public APIResponse postWithHeadersAndJson(
                        String endpoint,
                        Map<String, String> headers,
                        String jsonBody) {

                Map<String, String> mutableHeaders = new HashMap<>(headers);
                mutableHeaders.putIfAbsent(CONTENT_TYPE, APPLICATION_JSON);

                return apiContext.post(
                                endpoint,
                                buildOptions(mutableHeaders, null, jsonBody));
        }

        // POST with headers, query parameters, and JSON body
        public APIResponse postWithHeadersAndParams(
                        String endpoint,
                        Map<String, String> headers,
                        Map<String, String> queryParams,
                        String jsonBody) {

                Map<String, String> mutableHeaders = new HashMap<>(headers);
                mutableHeaders.putIfAbsent(CONTENT_TYPE, APPLICATION_JSON);

                return apiContext.post(
                                endpoint,
                                buildOptions(mutableHeaders, queryParams, jsonBody));
        }

        // POST with Bearer Token authentication
        public APIResponse postWithBearerToken(
                        String endpoint,
                        String bearerToken,
                        String jsonBody) {

                return postWithHeadersAndJson(
                                endpoint,
                                Map.of(
                                                AUTHORIZATION, BEARER + bearerToken,
                                                CONTENT_TYPE, APPLICATION_JSON),
                                jsonBody);
        }

        // POST with headers but no body
        public APIResponse postWithHeadersNoBody(
                        String endpoint,
                        Map<String, String> headers) {

                return apiContext.post(
                                endpoint,
                                buildOptions(headers, null, null));
        }

        // POST with Basic Authentication
        public APIResponse postWithBasicAuth(
                        String endpoint,
                        String username,
                        String password,
                        String jsonBody) {

                String basicAuth = Base64.getEncoder()
                                .encodeToString((username + ":" + password).getBytes());

                return postWithHeadersAndJson(
                                endpoint,
                                Map.of(
                                                AUTHORIZATION, "Basic " + basicAuth,
                                                CONTENT_TYPE, APPLICATION_JSON),
                                jsonBody);
        }

        // =========================================================
        // PUT REQUESTS
        // =========================================================

        // PUT with raw JSON (string body)
        public APIResponse putJson(
                        String endpoint,
                        String jsonBody) {

                return apiContext.put(
                                endpoint,
                                buildOptions(
                                                Map.of(CONTENT_TYPE, APPLICATION_JSON),
                                                null,
                                                jsonBody));
        }

        // PUT with headers and JSON body
        public APIResponse putWithHeaders(
                        String endpoint,
                        Map<String, String> headers,
                        String jsonBody) {

                Map<String, String> mutableHeaders = new HashMap<>(headers);
                mutableHeaders.putIfAbsent(CONTENT_TYPE, APPLICATION_JSON);

                return apiContext.put(
                                endpoint,
                                buildOptions(mutableHeaders, null, jsonBody));
        }

        // PUT with query parameters and JSON body
        public APIResponse putWithQueryParams(
                        String endpoint,
                        Map<String, String> queryParams,
                        String jsonBody) {

                return apiContext.put(
                                endpoint,
                                buildOptions(
                                                Map.of(CONTENT_TYPE, APPLICATION_JSON),
                                                queryParams,
                                                jsonBody));
        }

        // PUT with Bearer Token authentication
        public APIResponse putWithBearerToken(
                        String endpoint,
                        String bearerToken,
                        String jsonBody) {

                return putWithHeaders(
                                endpoint,
                                Map.of(
                                                AUTHORIZATION, BEARER + bearerToken,
                                                CONTENT_TYPE, APPLICATION_JSON),
                                jsonBody);
        }

        // =========================================================
        // PATCH REQUESTS
        // =========================================================

        // PATCH with raw JSON (string body)
        public APIResponse patchJson(
                        String endpoint,
                        String jsonBody) {

                return apiContext.patch(
                                endpoint,
                                buildOptions(
                                                Map.of(CONTENT_TYPE, APPLICATION_JSON),
                                                null,
                                                jsonBody));
        }

        // PATCH with headers and JSON body
        public APIResponse patchWithHeaders(
                        String endpoint,
                        Map<String, String> headers,
                        String jsonBody) {

                Map<String, String> mutableHeaders = new HashMap<>(headers);
                mutableHeaders.putIfAbsent(CONTENT_TYPE, APPLICATION_JSON);

                return apiContext.patch(
                                endpoint,
                                buildOptions(mutableHeaders, null, jsonBody));
        }

        // PATCH with Bearer Token authorization and JSON body
        public APIResponse patchWithBearerToken(
                        String endpoint,
                        String bearerToken,
                        String jsonBody) {

                return patchWithHeaders(
                                endpoint,
                                Map.of(
                                                AUTHORIZATION, BEARER + bearerToken,
                                                CONTENT_TYPE, APPLICATION_JSON),
                                jsonBody);
        }

        // PATCH with headers, query parameters, and JSON body
        public APIResponse patchWithHeadersAndParams(
                        String endpoint,
                        Map<String, String> headers,
                        Map<String, String> params,
                        String jsonBody) {

                Map<String, String> mutableHeaders = new HashMap<>(headers);
                mutableHeaders.putIfAbsent(CONTENT_TYPE, APPLICATION_JSON);

                return apiContext.patch(
                                endpoint,
                                buildOptions(mutableHeaders, params, jsonBody));
        }

        // =========================================================
        // DELETE REQUESTS
        // =========================================================

        // Basic DELETE request
        public APIResponse delete(
                        String endpoint) {

                return apiContext.delete(endpoint);
        }

        // DELETE with custom headers
        public APIResponse deleteWithHeaders(
                        String endpoint,
                        Map<String, String> headers) {

                return apiContext.delete(
                                endpoint,
                                buildOptions(headers, null, null));
        }

        // DELETE with Bearer Token authentication
        public APIResponse deleteWithBearerToken(
                        String endpoint,
                        String bearerToken) {

                return deleteWithHeaders(
                                endpoint,
                                Map.of(AUTHORIZATION, BEARER + bearerToken));
        }

        // DELETE with query parameters
        public APIResponse deleteWithQueryParams(
                        String endpoint,
                        Map<String, String> queryParams) {

                return apiContext.delete(
                                endpoint,
                                buildOptions(null, queryParams, null));
        }

        // DELETE with headers and JSON body
        // legacyy
        // public APIResponse deleteWithHeadersAndJson(
        // String endpoint,
        // Map<String, String> headers,
        // String jsonBody) {

        // if (headers != null) {
        // headers.putIfAbsent(CONTENT_TYPE, APPLICATION_JSON);
        // }

        // return apiContext.delete(
        // endpoint,
        // buildOptions(headers, null, jsonBody));
        // }

        // DELETE with headers, query parameters, and JSON body
        public APIResponse deleteWithHeadersParamsAndJson(
                        String endpoint,
                        Map<String, String> headers,
                        Map<String, String> queryParams,
                        String jsonBody) {

                // copy so callers' (possibly immutable) maps are never mutated
                Map<String, String> merged = new HashMap<>();
                if (headers != null) {
                        merged.putAll(headers);
                }
                merged.putIfAbsent(CONTENT_TYPE, APPLICATION_JSON);

                return apiContext.delete(
                                endpoint,
                                buildOptions(merged, queryParams, jsonBody));
        }
}
