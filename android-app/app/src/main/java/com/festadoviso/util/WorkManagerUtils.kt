package com.festadoviso.util

import android.content.Context
import androidx.work.*
import com.festadoviso.workers.SorteioReminderWorker
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Utilitários para agendar notificações com WorkManager.
 */
object WorkManagerUtils {

    /**
     * Agenda notificação semanal para sextas-feiras às 22h.
     */
    fun scheduleWeeklyReminder(context: Context) {
        // Calcular delay até a próxima sexta-feira às 22h
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY)
            set(Calendar.HOUR_OF_DAY, 22)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            // Se já passou esta sexta-feira às 22h, agendar para a próxima semana
            if (before(now)) {
                add(Calendar.WEEK_OF_YEAR, 1)
            }
        }

        val initialDelay = target.timeInMillis - now.timeInMillis

        // Constraints: apenas executar com bateria suficiente
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .build()

        // Request semanal periódico
        val weeklyWorkRequest = PeriodicWorkRequestBuilder<SorteioReminderWorker>(
            7, TimeUnit.DAYS  // Repetir a cada 7 dias
        )
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .setConstraints(constraints)
            .build()

        // Agendar work (substituir se já existir)
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            SorteioReminderWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.REPLACE,
            weeklyWorkRequest
        )
    }

    /**
     * Cancelar notificações agendadas.
     */
    fun cancelReminders(context: Context) {
        WorkManager.getInstance(context)
            .cancelUniqueWork(SorteioReminderWorker.WORK_NAME)
    }
}
