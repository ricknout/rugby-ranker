package dev.ricknout.rugbyranker.live.work

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.emoji2.text.EmojiCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import dev.ricknout.rugbyranker.core.model.Sport
import dev.ricknout.rugbyranker.core.util.DateUtils
import dev.ricknout.rugbyranker.core.util.FlagUtils
import dev.ricknout.rugbyranker.core.util.IdUtils
import dev.ricknout.rugbyranker.core.util.NotificationUtils
import dev.ricknout.rugbyranker.live.R
import dev.ricknout.rugbyranker.match.data.MatchRepository
import dev.ricknout.rugbyranker.match.model.Half
import dev.ricknout.rugbyranker.match.model.Match
import dev.ricknout.rugbyranker.match.model.Status
import kotlinx.coroutines.delay
import java.lang.IllegalArgumentException

open class LiveMatchWorker(
    appContext: Context,
    params: WorkerParameters,
    private val sport: Sport,
    private val repository: MatchRepository,
    private val workManager: WorkManager,
    private val notificationManager: NotificationManagerCompat,
) : CoroutineWorker(appContext, params) {

    private val emojiCompat = try {
        EmojiCompat.get()
    } catch (e: Exception) {
        Log.e(TAG, e.toString())
        null
    }

    @SuppressLint("MissingPermission")
    override suspend fun doWork(): Result {
        val matchId = inputData.getLong(LiveMatchWorkManager.KEY_MATCH_ID, DEFAULT_MATCH_ID).also { matchId ->
            if (matchId == DEFAULT_MATCH_ID) throw IllegalArgumentException("Invalid match ID")
        }
        if (!areNotificationsFullyEnabled()) return Result.failure()
        val matchNotificationId = matchId.toInt()
        val initialForegroundInfo = createInitialForegroundInfo(matchNotificationId)
        setForeground(initialForegroundInfo)
        while (true) {
            if (!areNotificationsFullyEnabled()) return Result.failure()
            val (success, match) = repository.fetchMatchSummarySync(matchId, sport)
            if (success) {
                when (match!!.status) {
                    Status.LIVE -> {
                        val foregroundInfo = createLiveForegroundInfo(matchNotificationId, match)
                        setForeground(foregroundInfo)
                    }
                    Status.COMPLETE -> {
                        val notificationId = IdUtils.getID()
                        val notification = createResultNotification(match)
                        notificationManager.notify(notificationId, notification)
                        return Result.success()
                    }
                    else -> {
                        // Do nothing, wait for match result
                    }
                }
            }
            delay(DateUtils.MINUTE_MILLIS)
        }
    }

    private fun areNotificationsFullyEnabled(): Boolean {
        val notificationsEnabled = NotificationUtils.areNotificationsEnabled(applicationContext)
        val liveNotificationChannelEnabled = NotificationUtils.isNotificationChannelEnabled(
            notificationManager = notificationManager,
            channelId = NotificationUtils.NOTIFICATION_CHANNEL_ID_LIVE,
        )
        val resultNotificationChannelEnabled = NotificationUtils.isNotificationChannelEnabled(
            notificationManager = notificationManager,
            channelId = NotificationUtils.NOTIFICATION_CHANNEL_ID_RESULT,
        )
        return notificationsEnabled && liveNotificationChannelEnabled && resultNotificationChannelEnabled
    }

    private fun createInitialForegroundInfo(notificationId: Int): ForegroundInfo {
        val notification = createInitialNotification()
        return ForegroundInfo(notificationId, notification)
    }

    private fun createInitialNotification(): Notification {
        val title = applicationContext.getString(R.string.fetching_live_match)
        val cancelTitle = applicationContext.getString(R.string.unpin)
        val openPendingIntent = createOpenPendingIntent()
        val cancelPendingIntent = createCancelPendingIntent()
        val builder = NotificationCompat.Builder(applicationContext, NotificationUtils.NOTIFICATION_CHANNEL_ID_LIVE)
            .setContentTitle(title)
            .setTicker(title)
            .setContentIntent(openPendingIntent)
            .setSmallIcon(R.drawable.ic_rugby_ranker_24dp)
            .setOngoing(true)
            .addAction(R.drawable.ic_close_24dp, cancelTitle, cancelPendingIntent)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = createLiveNotificationChannel()
            builder.setChannelId(notificationChannel.id)
        }
        return builder.build()
    }

    private fun createLiveForegroundInfo(notificationId: Int, match: Match): ForegroundInfo {
        val notification = createLiveNotification(match)
        return ForegroundInfo(notificationId, notification)
    }

    private fun createLiveNotification(match: Match): Notification {
        val homeFlag = FlagUtils.getFlagEmojiForTeamAbbreviation(match.firstTeamAbbreviation)
        val awayFlag = FlagUtils.getFlagEmojiForTeamAbbreviation(match.secondTeamAbbreviation)
        val title = applicationContext.getString(
            R.string.match,
            homeFlag,
            match.firstTeamName,
            match.firstTeamScore,
            match.secondTeamScore,
            match.secondTeamName,
            awayFlag,
        ).run {
            emojiCompat?.process(this) ?: this
        }
        val half = when (match.half) {
            Half.FIRST -> applicationContext.getString(R.string.first_half)
            Half.SECOND -> applicationContext.getString(R.string.second_half)
            Half.HALF_TIME -> applicationContext.getString(R.string.half_time)
            else -> null
        }
        val sport = when (match.sport) {
            Sport.MENS -> applicationContext.getString(R.string.mens)
            Sport.WOMENS -> applicationContext.getString(R.string.womens)
        }
        val text = if (match.minute != null && half != null) {
            applicationContext.getString(R.string.live_half_minute_sport, half, match.minute, sport)
        } else {
            null
        }
        val cancelTitle = applicationContext.getString(R.string.unpin)
        val openPendingIntent = createOpenPendingIntent()
        val cancelPendingIntent = createCancelPendingIntent()
        val builder = NotificationCompat.Builder(applicationContext, NotificationUtils.NOTIFICATION_CHANNEL_ID_LIVE)
            .setContentTitle(title)
            .setTicker(title)
            .setContentText(text)
            .setContentIntent(openPendingIntent)
            .setSmallIcon(R.drawable.ic_rugby_ranker_24dp)
            .setOngoing(true)
            .addAction(R.drawable.ic_close_24dp, cancelTitle, cancelPendingIntent)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = createLiveNotificationChannel()
            builder.setChannelId(notificationChannel.id)
        }
        return builder.build()
    }

    private fun createResultNotification(match: Match): Notification {
        val homeFlag = FlagUtils.getFlagEmojiForTeamAbbreviation(match.firstTeamAbbreviation)
        val awayFlag = FlagUtils.getFlagEmojiForTeamAbbreviation(match.secondTeamAbbreviation)
        val title = applicationContext.getString(
            R.string.match,
            homeFlag,
            match.firstTeamName,
            match.firstTeamScore,
            match.secondTeamScore,
            match.secondTeamName,
            awayFlag,
        ).run {
            emojiCompat?.process(this) ?: this
        }
        val half = applicationContext.getString(R.string.full_time)
        val sport = when (match.sport) {
            Sport.MENS -> applicationContext.getString(R.string.mens)
            Sport.WOMENS -> applicationContext.getString(R.string.womens)
        }
        val text = applicationContext.getString(R.string.half_sport, half, sport)
        val openPendingIntent = createOpenPendingIntent()
        val builder = NotificationCompat.Builder(applicationContext, NotificationUtils.NOTIFICATION_CHANNEL_ID_RESULT)
            .setContentTitle(title)
            .setTicker(title)
            .setContentText(text)
            .setContentIntent(openPendingIntent)
            .setSmallIcon(R.drawable.ic_rugby_ranker_24dp)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = createResultNotificationChannel()
            builder.setChannelId(notificationChannel.id)
        }
        return builder.build()
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun createLiveNotificationChannel(): NotificationChannelCompat {
        return NotificationUtils.createNotificationChannel(
            notificationManager = notificationManager,
            id = NotificationUtils.NOTIFICATION_CHANNEL_ID_LIVE,
            name = applicationContext.getString(R.string.live_matches),
            importance = NotificationManagerCompat.IMPORTANCE_LOW,
        )
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun createResultNotificationChannel(): NotificationChannelCompat {
        return NotificationUtils.createNotificationChannel(
            notificationManager = notificationManager,
            id = NotificationUtils.NOTIFICATION_CHANNEL_ID_RESULT,
            name = applicationContext.getString(R.string.match_results),
        )
    }

    private fun createOpenPendingIntent(): PendingIntent {
        val intent = applicationContext.packageManager.getLaunchIntentForPackage(applicationContext.packageName)
        return PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private fun createCancelPendingIntent() = workManager.createCancelPendingIntent(id)

    companion object {
        private const val TAG = "LiveMatchWorker"
        private const val DEFAULT_MATCH_ID = -1L
    }
}
