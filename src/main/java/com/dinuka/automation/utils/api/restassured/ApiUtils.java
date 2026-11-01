package com.dinuka.automation.utils.api.restassured;

import io.restassured.response.Response;
import static io.restassured.RestAssured.given;

import java.io.File;
import java.io.InputStream;
import java.util.Map;
import io.restassured.http.ContentType;
import io.qameta.allure.restassured.AllureRestAssured;

public class ApiUtils {

        public static final String AUTHORIZATION = "Authorization";
        public static final String BEARER = "Bearer";

        private ApiUtils() {
                throw new UnsupportedOperationException("Utility class");
        }

        // ############################## GET ##############################

        // Basic GET request ✅
        public static Response getRequest(String baseUrl, String endPoint) {
                return given()
                                .filter(new AllureRestAssured())
                                .baseUri(baseUrl)
                                .when()
                                .get(endPoint)
                                .then()
                                .extract().response();
        }

        // GET with headers only ✅
        public static Response getReqWithHeaders(String baseUrl, String endPoint, Map<String, String> headersMap) {
                return given()
                                .filter(new AllureRestAssured())
                                .baseUri(baseUrl)
                                .headers(headersMap)
                                .when()
                                .get(endPoint)
                                .then()
                                .extract().response();
        }

        // GET with query parameters ✅
        public static Response getReqWithQueryParams(String baseUrl, String endPoint, Map<String, String> paramsMap) {
                return given()
                                .filter(new AllureRestAssured())
                                .baseUri(baseUrl)
                                .queryParams(paramsMap)
                                .when()
                                .get(endPoint)
                                .then()
                                .extract().response();
        }

        // GET with query parameters and headers ✅
        public static Response getReqWithQueryParamsAndHeaders(String baseUrl,
                        Map<String, String> paramsMap, Map<String, String> headersMap, String endPoint) {
                return given()
                                .filter(new AllureRestAssured())
                                .baseUri(baseUrl)
                                .queryParams(paramsMap)
                                .headers(headersMap)
                                .when()
                                .get(endPoint)
                                .then()
                                .extract().response();
        }

        // GET with Bearer Token authorization
        public static Response getReqWithBearerToken(String baseUrl, String endPoint, String bearerToken) {
                return given()
                                .baseUri(baseUrl)
                                .header(AUTHORIZATION, BEARER + bearerToken)
                                .when()
                                .get(endPoint)
                                .then()
                                .extract().response();
        }

        // GET with Basic Authentication (username and password)
        public static Response getReqWithBasicAuth(String baseUrl, String endPoint, String username, String password) {
                return given()
                                .baseUri(baseUrl)
                                .auth().basic(username, password)
                                .when()
                                .get(endPoint)
                                .then()
                                .extract().response();
        }

        // GET with API Key in header
        public static Response getReqWithApiKeyHeader(String baseUrl, String endPoint, String apiKeyName,
                        String apiKeyValue) {
                return given()
                                .baseUri(baseUrl)
                                .header(apiKeyName, apiKeyValue)
                                .when()
                                .get(endPoint)
                                .then()
                                .extract().response();
        }

        // GET request returning InputStream ✅
        public static InputStream getReqForStreams(String baseUrl, String endPoint, Map<String, String> paramsMap) {
                return given()
                                .baseUri(baseUrl)
                                .queryParams(paramsMap)
                                .when()
                                .get(endPoint)
                                .then()
                                .extract().response().asInputStream();
        }

        // GET request for streaming response ✅
        public static Response getStreamingResponse(String baseUrl, String endPoint, Map<String, String> paramsMap) {
                return given()
                                .baseUri(baseUrl)
                                .queryParams(paramsMap)
                                .when()
                                .get(endPoint);
        }

        // GET with single path param ✅
        public static Response getReqWithSinglePathParam(String baseUrl, String endPoint, String paramKey,
                        String paramValue) {
                return given()
                                .baseUri(baseUrl)
                                .pathParam(paramKey, paramValue)
                                .when()
                                .get(endPoint)
                                .then()
                                .extract().response();
        }

        // GET with multiple path params ✅
        public static Response getReqWithMultiplePathParams(String baseUrl, String endPoint,
                        Map<String, String> pathParams) {
                return given()
                                .baseUri(baseUrl)
                                .pathParams(pathParams)
                                .when()
                                .get(endPoint)
                                .then()
                                .extract().response();
        }

        // GET with cookies
        public static Response getReqWithCookies(String baseUrl, String endPoint, Map<String, String> cookies) {
                return given()
                                .baseUri(baseUrl)
                                .cookies(cookies)
                                .when()
                                .get(endPoint)
                                .then()
                                .extract().response();
        }

        // ############################## POST ##############################

        // POST with raw JSON (string body) ✅
        public static Response postReqWithRawJson(String baseUrl, String endPoint, String requestBodyJson) {
                return given()
                                .baseUri(baseUrl)
                                .contentType(ContentType.JSON)
                                .body(requestBodyJson)
                                .when()
                                .post(endPoint)
                                .then()
                                .extract().response();
        }

        // POST with headers and body object ✅
        public static Response postReqWithHeadersAndBody(String baseUrl, String endPoint, Map<String, String> headers,
                        String requestBody) {
                return given()
                                .filter(new AllureRestAssured())
                                .baseUri(baseUrl)
                                .headers(headers)
                                .contentType(ContentType.JSON)
                                .body(requestBody)
                                .when()
                                .post(endPoint)
                                .then()
                                .extract().response();
        }

        // POST with headers, body object, and query parameters ✅
        public static Response postReqWithHeadersBodyAndQueryParams(
                        String baseUrl,
                        String endPoint,
                        Map<String, String> headers,
                        Map<String, String> queryParams,
                        Object requestBody) {

                return given()
                                .baseUri(baseUrl)
                                .headers(headers)
                                .queryParams(queryParams)
                                .contentType(ContentType.JSON)
                                .body(requestBody)
                                .when()
                                .post(endPoint)
                                .then()
                                .extract().response();
        }

        // POST with only custom headers, no body ✅
        public static Response postReqWithHeaders(String baseUrl, String endPoint, Map<String, String> headers) {
                return given()
                                .baseUri(baseUrl)
                                .headers(headers)
                                .when()
                                .post(endPoint)
                                .then()
                                .extract().response();
        }

        // POST with Bearer Token
        public static Response postReqWithBearerToken(String baseUrl, String endPoint, String bearerToken,
                        Object requestBody) {
                return given()
                                .baseUri(baseUrl)
                                .header(AUTHORIZATION, BEARER + bearerToken)
                                .contentType(ContentType.JSON)
                                .body(requestBody)
                                .when()
                                .post(endPoint)
                                .then()
                                .extract().response();
        }

        // POST with Basic Authentication
        public static Response postReqWithBasicAuth(String baseUrl, String endPoint, String username, String password,
                        Object requestBody) {
                return given()
                                .baseUri(baseUrl)
                                .auth().basic(username, password)
                                .contentType(ContentType.JSON)
                                .body(requestBody)
                                .when()
                                .post(endPoint)
                                .then()
                                .extract().response();
        }

        // POST with multipart/form-data (file upload)
        public static Response postReqWithFileUpload(String baseUrl, String endPoint, String fileParamName, File file) {
                return given()
                                .baseUri(baseUrl)
                                .contentType(ContentType.MULTIPART)
                                .multiPart(fileParamName, file)
                                .when()
                                .post(endPoint)
                                .then()
                                .extract().response();
        }

        // POST with plain text body
        public static Response postReqWithTextBody(String baseUrl, String endPoint, String textBody) {
                return given()
                                .baseUri(baseUrl)
                                .contentType(ContentType.TEXT)
                                .body(textBody)
                                .when()
                                .post(endPoint)
                                .then()
                                .extract().response();
        }

        // ############################## PUT ##############################

        // PUT with raw JSON ✅
        public static Response putRequestWithRawJson(String baseUrl, String endPoint, String requestBodyJson) {
                return given()
                                .baseUri(baseUrl)
                                .contentType(ContentType.JSON)
                                .body(requestBodyJson)
                                .when()
                                .put(endPoint)
                                .then()
                                .extract().response();
        }

        // PUT with headers and body object ✅
        public static Response putRequestWithHeadersAndBody(String baseUrl, String endPoint,
                        Map<String, String> headers, String requestBody) {
                return given()
                                .baseUri(baseUrl)
                                .headers(headers)
                                .contentType(ContentType.JSON)
                                .body(requestBody)
                                .when()
                                .put(endPoint)
                                .then()
                                .extract()
                                .response();
        }

        // PUT with query parameters and body
        public static Response putReqWithQueryParamsAndBody(String baseUrl, String endPoint,
                        Map<String, String> queryParams, Object requestBody) {
                return given()
                                .baseUri(baseUrl)
                                .queryParams(queryParams)
                                .contentType(ContentType.JSON)
                                .body(requestBody)
                                .when()
                                .put(endPoint)
                                .then()
                                .extract().response();
        }

        // PUT with path parameters and body
        public static Response putReqWithPathParamsAndBody(String baseUrl, String endPoint,
                        Map<String, String> pathParams, Object requestBody) {
                return given()
                                .baseUri(baseUrl)
                                .pathParams(pathParams)
                                .contentType(ContentType.JSON)
                                .body(requestBody)
                                .when()
                                .put(endPoint)
                                .then()
                                .extract().response();
        }

        // ############################## PATCH ##############################

        // PATCH with raw JSON
        public static Response patchRequestWithRawJson(String baseUrl, String endPoint, String requestBodyJson) {
                return given()
                                .baseUri(baseUrl)
                                .contentType(ContentType.JSON)
                                .body(requestBodyJson)
                                .when()
                                .patch(endPoint)
                                .then()
                                .extract().response();
        }

        // PATCH with headers and body
        public static Response patchReqWithHeadersAndBody(String baseUrl, String endPoint,
                        Map<String, String> headers, Object requestBody) {
                return given()
                                .baseUri(baseUrl)
                                .headers(headers)
                                .contentType(ContentType.JSON)
                                .body(requestBody)
                                .when()
                                .patch(endPoint)
                                .then()
                                .extract().response();
        }

        // PATCH with Bearer Token
        public static Response patchReqWithBearerToken(String baseUrl, String endPoint, String bearerToken,
                        Object requestBody) {
                return given()
                                .baseUri(baseUrl)
                                .header(AUTHORIZATION, BEARER + bearerToken)
                                .contentType(ContentType.JSON)
                                .body(requestBody)
                                .when()
                                .patch(endPoint)
                                .then()
                                .extract().response();
        }

        // PATCH with query parameters
        public static Response patchReqWithQueryParams(String baseUrl, String endPoint,
                        Map<String, String> queryParams, Object requestBody) {
                return given()
                                .baseUri(baseUrl)
                                .queryParams(queryParams)
                                .contentType(ContentType.JSON)
                                .body(requestBody)
                                .when()
                                .patch(endPoint)
                                .then()
                                .extract().response();
        }

        // ############################### DELETE ##############################

        // DELETE request ✅
        public static Response deleteRequest(String baseUrl, String endPoint) {
                return given()
                                .baseUri(baseUrl)
                                .when()
                                .delete(endPoint)
                                .then()
                                .extract().response();
        }

        // DELETE with headers
        public static Response deleteReqWithHeaders(String baseUrl, String endPoint, Map<String, String> headers) {
                return given()
                                .baseUri(baseUrl)
                                .headers(headers)
                                .when()
                                .delete(endPoint)
                                .then()
                                .extract().response();
        }

        // DELETE with Bearer Token
        public static Response deleteReqWithBearerToken(String baseUrl, String endPoint, String bearerToken) {
                return given()
                                .baseUri(baseUrl)
                                .header(AUTHORIZATION, BEARER + bearerToken)
                                .when()
                                .delete(endPoint)
                                .then()
                                .extract().response();
        }

        // DELETE with query parameters
        public static Response deleteReqWithQueryParams(String baseUrl, String endPoint,
                        Map<String, String> queryParams) {
                return given()
                                .baseUri(baseUrl)
                                .queryParams(queryParams)
                                .when()
                                .delete(endPoint)
                                .then()
                                .extract().response();
        }

        // DELETE with path parameters
        public static Response deleteReqWithPathParams(String baseUrl, String endPoint,
                        Map<String, String> pathParams) {
                return given()
                                .baseUri(baseUrl)
                                .pathParams(pathParams)
                                .when()
                                .delete(endPoint)
                                .then()
                                .extract().response();
        }

}
