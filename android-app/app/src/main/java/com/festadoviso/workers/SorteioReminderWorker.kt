package com.festadoviso.workers

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.festadoviso.R
import com.festadoviso.data.repository.FolhaRepository
import com.festadoviso.ui.MainActivity
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.*

/**
 * Worker que envia notificação todas as sextas-feiras às 22h
 * para lembrar os utilizadores do sorteio do Euromilhões.
 */
@HiltWorker
class SorteioReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val folhaRepository: FolhaRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        // Verificar se hoje é sexta-feira
        val calendar = Calendar.getInstance()
        val diaSemana = calendar.get(Calendar.DAY_OF_WEEK)
        val hora = calendar.get(Calendar.HOUR_OF_DAY)

        // Apenas enviar notificação às sextas-feiras (Calendar.FRIDAY = 6) às 22h
        if (diaSemana == Calendar.FRIDAY && hora == 22) {
            // Verificar se existem folhas ativas
            val folhasAtivas = folhaRepository.countFolhasAtivas()

            if (folhasAtivas > 0) {
                enviarNotificacao()
            }
        }

        return Result.success()
    }

    private fun enviarNotificacao() {
        createNotificationChannel()

        // Intent para abrir a app quando tocar na notificação
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        // Construir notificação
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Sorteio Euromilhões Hoje!")
            .setContentText("Não se esqueça! O sorteio é hoje às 22h. Boa sorte!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        // Enviar notificação
        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(applicationContext)
                .notify(NOTIFICATION_ID, notification)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Lembretes de Sorteio"
            val descriptionText = "Notificações sobre o sorteio semanal do Euromilhões"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID = "sorteio_channel"
        const val NOTIFICATION_ID = 1
        const val WORK_NAME = "sorteio_reminder"
    }
}
