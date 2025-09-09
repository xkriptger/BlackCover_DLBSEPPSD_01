package de.xkript.blackcover.core.util

import de.xkript.blackcover.R
import de.xkript.blackcover.core.BlackCoverApp
import retrofit2.Response
import java.io.IOException

abstract class
ResponseHandler {
    
    suspend fun <T> call(request: suspend () -> Response<T>): DataState<T?> {
        try {
            val response = request()
            return if (response.isSuccessful) { // Successful request
                DataState.Success(
                    data = response.body()
                )
            }
            else {
                DataState.Error(
                    code = response.code(),
                    message = BlackCoverApp.getInstance().getString(R.string.there_is_a_problem_contact_support)
                )
            }
        } catch (e: IOException) { // Internet connection error
            return DataState.Error(
                code = Constant.RETROFIT_ERROR_NO_INTERNET,
                message = BlackCoverApp.getInstance().getString(R.string.please_connect_your_phone_to_the_internet),
            )
        } catch (e: Exception) { // Unknown error
            return DataState.Error(
                code = Constant.RETROFIT_ERROR_UNKNOWN,
                message = BlackCoverApp.getInstance().getString(R.string.unknown_error_please_contact_support),
            )
        }
    }
    
}