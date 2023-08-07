package api_assured;

import api_assured.exceptions.FailedCallException;
import com.fasterxml.jackson.core.JsonProcessingException;
import records.Pair;
import retrofit2.Call;
import retrofit2.Response;
import utils.*;
import java.io.IOException;
import static utils.StringUtilities.Color.*;

/**
 * This abstract class represents a caller that performs API calls, logs the results and returns objects in response bodies.
 * It contains methods for performing and getting responses from API calls, as well as a method for printing the response body.
 * It also has a static boolean variable for keeping logs, an ObjectMapper for JSON serialization and deserialization,
 * a StringUtilities object for string manipulation, and a Printer object for logging.
 *
 * @author Umut Ay Bora
 * @version 1.4.0 (Documented in 1.4.0, released in an earlier version)
 */
@SuppressWarnings("unused")
public abstract class Caller {

    /**
     * A static boolean variable that determines whether logs should be kept for API calls.
     */
    static boolean keepLogs;

    /**
     * A StringUtilities object for string manipulation.
     */
    static StringUtilities strUtils = new StringUtilities();

    /**
     * A Printer object for logging.
     */
    static Printer log = new Printer(Caller.class);

    /**
     * Constructs a Caller object and initializes the ObjectMapper object and the keepLogs variable.
     */
    public Caller(){
        keepLogs = Boolean.parseBoolean(PropertyUtility.getProperty("keep-api-logs", "true"));
    }

    /**
     * Performs the given call and processes the response. This method provides advanced error handling capabilities.
     *
     * @param call The call to be executed. This is a retrofit2.Call object, which represents a request that has been prepared for execution.
     * @param strict If true, throws a FailedCallException when the call fails or the response is not successful. If false, returns null in these cases.
     * @param printBody If true, prints the body of the response. This may be useful for debugging purposes.
     * @param errorModels Varargs parameter. Each ErrorModel class is used to try to parse the error response if the call was not successful.
     *
     * @return A ResponseType object. If the call was successful, this is the body of the response. If the call was not successful and strict is false, this may be a parsed error response, or null if parsing the error response failed.
     *
     * @throws FailedCallException If strict is true and the call failed or the response was not successful.
     *
     * @param <SuccessModel> The type of the successful response body.
     * @param <ErrorModel> The type of the error response body.
     * @param <ResponseType> The type of the return value of this method. This is either SuccessModel or ErrorModel.
     */
    @SafeVarargs
    @SuppressWarnings("unchecked")
    protected static <SuccessModel, ErrorModel, ResponseType> ResponseType perform(
            Call<SuccessModel> call,
            Boolean strict,
            Boolean printBody,
            Class<ErrorModel>... errorModels){

        String serviceName = getRequestMethod();
        if (keepLogs)
            log.info("Performing " +
                    strUtils.markup(PALE, call.request().method()) +
                    " call for '" +
                    strUtils.markup(PALE, serviceName) +
                    "' service on url: " + call.request().url()
            );
        try {
            Response<SuccessModel> response = call.execute();
            okhttp3.Response okHttpResponse = response.raw().newBuilder().build();

            if (okHttpResponse.isSuccessful()){
                if (keepLogs) log.success("The response code is: " + okHttpResponse.code());
                if (okHttpResponse.message().length()>0 && keepLogs) log.info(okHttpResponse.message());
                if (printBody) printBody(okHttpResponse);
                return (ResponseType) response.body();
            }
            else{
                log.warning("The response code is: " + response.code());
                if (response.message().length()>0) log.warning(response.message());
                log.warning(response.raw().toString());
                String errorString = getResponseString(okHttpResponse);
                if (printBody) printBody(okHttpResponse);
                if (strict) throw new FailedCallException(
                        "The strict call performed for " + serviceName + " service returned response code " + response.code()
                );
                else {
                    ResponseType result;

                    for (Class<ErrorModel> errorModel:errorModels) {
                        try {return (ResponseType) MappingUtilities.Json.mapper.readValue(errorString, errorModel);}
                        catch (JsonProcessingException ignored){}
                    }
                    return null;
                }
            }
        }
        catch (IOException exception) {
            if (strict){
                log.error(exception.getLocalizedMessage(), exception);
                throw new FailedCallException("The call performed for " + serviceName + " has failed.");
            }
            else return null;
        }
    }

    /**
     * Gets the response from an API call and logs the results.
     *
     * @param call the Call object representing the API call
     * @param strict a boolean indicating whether the call should be strict (i.e. throw an exception if the response is not successful)
     * @param printBody a boolean indicating whether the response body should be printed
     * @return the Response object representing the API response
     * @throws FailedCallException if the call is strict and the response is not successful
     */
    protected static <Model> Response<Model> getResponse(Call<Model> call, Boolean strict, Boolean printBody){
        String serviceName = getRequestMethod();
        if (keepLogs)
            log.info("Performing " +
                    strUtils.markup(PALE, call.request().method()) +
                    " call for '" +
                    strUtils.markup(PALE, serviceName) +
                    "' service on url: " + call.request().url()
            );
        try {
            Response<Model> response = call.execute();
            okhttp3.Response okHttpResponse = response.raw().newBuilder().build();

            if (okHttpResponse.isSuccessful()){
                if (keepLogs) log.success("The response code is: " + okHttpResponse.code());
                if (okHttpResponse.message().length()>0 && keepLogs) log.info(okHttpResponse.message());
                if (printBody) printBody(okHttpResponse);
            }
            else{
                log.warning("The response code is: " + okHttpResponse.code());
                if (okHttpResponse.message().length()>0) log.warning(okHttpResponse.message());
                log.warning(okHttpResponse.toString());
                if (printBody) printBody(okHttpResponse);
                if (strict) throw new FailedCallException(
                        "The strict call performed for " + serviceName + " service returned response code " + okHttpResponse.code()
                );
            }
            return response;
        }
        catch (IOException exception) {
            if (strict){
                log.error(exception.getLocalizedMessage(), exception);
                throw new FailedCallException("The call performed for " + serviceName + " has failed.");
            }
            else return null;
        }
    }

    /**
     * Executes the given call and returns a Pair containing the response and potential error model.
     * This method provides advanced error handling capabilities.
     *
     * @param call The call to be executed. This is a retrofit2.Call object, which represents a request that has been prepared for execution.
     * @param strict If true, throws a FailedCallException when the call fails or the response is not successful. If false, returns null in these cases.
     * @param printBody If true, prints the body of the response. This may be useful for debugging purposes.
     * @param errorModels Varargs parameter. Each ErrorModel class is used to try to parse the error response if the call was not successful.
     *
     * @return A Pair object with the SuccessModel response as the first element and ErrorModel as the second element. If the call was successful, the second element is null.
     * If the call was not successful and strict is false, the second element may be a parsed error response, or null if parsing the error response failed.
     *
     * @throws FailedCallException If strict is true and the call failed or the response was not successful.
     *
     * @param <SuccessModel> The type of the successful response body.
     * @param <ErrorModel> The type of the error response body.
     */
    @SafeVarargs
    protected static <SuccessModel, ErrorModel> Pair<Response<SuccessModel>, ErrorModel> getResponse(
            Call<SuccessModel> call,
            Boolean strict,
            Boolean printBody,
            Class<ErrorModel>... errorModels){

        String serviceName = getRequestMethod();
        if (keepLogs)
            log.info("Performing " +
                    strUtils.markup(PALE, call.request().method()) +
                    " call for '" +
                    strUtils.markup(PALE, serviceName) +
                    "' service on url: " + call.request().url()
            );
        try {
            Response<SuccessModel> response = call.execute();
            okhttp3.Response okHttpResponse = response.raw().newBuilder().build();

            if (okHttpResponse.isSuccessful()){
                if (keepLogs) log.success("The response code is: " + okHttpResponse.code());
                if (okHttpResponse.message().length()>0 && keepLogs) log.info(okHttpResponse.message());
                if (printBody) printBody(okHttpResponse);
                return new Pair<>(response, null);
            }
            else{
                log.warning("The response code is: " + response.code());
                if (okHttpResponse.message().length()>0) log.warning(okHttpResponse.message());
                log.warning(okHttpResponse.toString());
                String errorString = getResponseString(okHttpResponse);
                if (printBody) printBody(okHttpResponse);
                if (strict) throw new FailedCallException(
                        "The strict call performed for " + serviceName + " service returned response code " + okHttpResponse.code()
                );
                else
                    for (Class<ErrorModel> errorModel:errorModels){
                        Pair<Response<SuccessModel>, ErrorModel> pair;
                        try {
                            pair = new Pair<>(
                                    response,
                                    MappingUtilities.Json.mapper.readValue(errorString, errorModel)
                            );
                        }
                        catch (JsonProcessingException e) {throw new RuntimeException(e);}
                        return pair;
                    }

            }
            return null;
        }
        catch (IOException exception) {
            if (strict){
                log.error(exception.getLocalizedMessage(), exception);
                throw new FailedCallException("The call performed for " + serviceName + " has failed.");
            }
            else return null;
        }
    }

    /**
     * Prints the body of the response. This method provides log information based on the type of the response.
     *
     * @param response The response whose body to print. This is a retrofit2.Response object.
     *
     */
    static void printBody(okhttp3.Response response) {
        String message = "The response body is: \n";
        if (response.body() != null) // Success response with a non-null body
            log.info(message + getResponseString(response));
        else log.info("The response body is empty."); // Success response with a null body
    }

    /**
     * Converts the body of the response to a string. This method handles both successful and error responses.
     *
     * @param response The response whose body to convert to a string. This is a retrofit2.Response object.
     *
     * @return The body of the response as a string. If the body of the response or the error body could not be converted to a string,
     * this method returns null.
     *
     */
    static String getResponseString(okhttp3.Response response){
        return MappingUtilities.Json.mapper.valueToTree(response.body()).toPrettyString();
    }

    /**
     * Gets the name of the method that called the API.
     *
     * @return the name of the method that called the API
     */
    private static String getRequestMethod(){
        Throwable dummyException = new Throwable();
        StackTraceElement[] stackTrace = dummyException.getStackTrace();
        // LOGGING-132: use the provided logger name instead of the class name
        String method = stackTrace[0].getMethodName();
        // Caller will be the third element
        if( stackTrace.length > 2 ) {
            StackTraceElement caller = stackTrace[2];
            method = caller.getMethodName();
        }
        return method;
    }

    /**
     * Returns whether logs are being kept for API calls.
     *
     * @return a boolean indicating whether logs are being kept for API calls
     */
    public static boolean keepsLogs() {
        return keepLogs;
    }

    /**
     * Sets whether logs should be kept for API calls.
     *
     * @param keepLogs a boolean indicating whether logs should be kept for API calls
     */
    public static void keepLogs(boolean keepLogs) {
        Caller.keepLogs = keepLogs;
    }
}