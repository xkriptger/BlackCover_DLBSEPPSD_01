package de.xkript.blackcover.core.util

import dagger.hilt.android.qualifiers.ApplicationContext
import de.xkript.blackcover.core.BlackCoverApp
import de.xkript.blackcover.core.util.dataStores.DataStoreBlackCover
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class HeaderInterceptor @Inject constructor(
    @ApplicationContext private val app: BlackCoverApp,
    private val dataStoreBlackCover: DataStoreBlackCover,
) : Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        //        val userToken = dataStoresBlackCover.getString(Constant.DS_USER_TOKEN) ?: ""
        request = request.newBuilder().apply {
            addHeader("Accept", "application/json; charset=utf-8")
            addHeader("Content-Type", "application/json; charset=utf-8")
            //            addHeader("Authorization", "Bearer $userToken")
            //            if (BuildConfig.DEBUG || BuildConfig.FLAVOR == Constant.FLAVOR_DEV) {
            //                addHeader("Release-Mode", Constant.RELEASE_MODE_DEV)
            //            }
            //            else {
            //                addHeader("Release-Mode", Constant.RELEASE_MODE_PRO)
            //            }
        }.build()
        return chain.proceed(request)
    }
}