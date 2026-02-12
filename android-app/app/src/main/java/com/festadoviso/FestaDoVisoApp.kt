package com.festadoviso

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class com suporte Hilt.
 * Anotação @HiltAndroidApp gera componentes Hilt necessários.
 */
@HiltAndroidApp
class FestaDoVisoApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // TODO: Adicionar notificações WorkManager numa versão futura
        // WorkManagerUtils.scheduleWeeklyReminder(this)
    }
}
