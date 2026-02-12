package com.festadoviso

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.festadoviso.util.WorkManagerUtils
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * Application class com suporte Hilt.
 * Anotação @HiltAndroidApp gera componentes Hilt necessários.
 */
@HiltAndroidApp
class FestaDoVisoApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()

        // Agendar notificações semanais
        WorkManagerUtils.scheduleWeeklyReminder(this)
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    }
}
