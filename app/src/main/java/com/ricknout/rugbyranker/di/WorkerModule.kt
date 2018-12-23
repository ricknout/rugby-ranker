package com.ricknout.rugbyranker.di

import androidx.work.ListenableWorker
import com.ricknout.rugbyranker.matches.work.MensCompleteWorldRugbyMatchesWorker
import com.ricknout.rugbyranker.matches.work.MensUnplayedWorldRugbyMatchesWorker
import com.ricknout.rugbyranker.matches.work.WomensCompleteWorldRugbyMatchesWorker
import com.ricknout.rugbyranker.matches.work.WomensUnplayedWorldRugbyMatchesWorker
import com.ricknout.rugbyranker.rankings.work.MensWorldRugbyRankingsWorker
import com.ricknout.rugbyranker.rankings.work.WomensWorldRugbyRankingsWorker
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class WorkerModule {

    @Binds
    @IntoMap
    @WorkerKey(MensWorldRugbyRankingsWorker::class)
    abstract fun bindMensWorldRugbyRankingsWorker(mensWorldRugbyRankingsWorker: MensWorldRugbyRankingsWorker): ListenableWorker

    @Binds
    @IntoMap
    @WorkerKey(WomensWorldRugbyRankingsWorker::class)
    abstract fun bindWomensWorldRugbyRankingsWorker(womensWorldRugbyRankingsWorker: WomensWorldRugbyRankingsWorker): ListenableWorker

    @Binds
    @IntoMap
    @WorkerKey(MensUnplayedWorldRugbyMatchesWorker::class)
    abstract fun bindMensUnplayedWorldRugbyMatchesWorker(mensUnplayedWorldRugbyMatchesWorker: MensUnplayedWorldRugbyMatchesWorker): ListenableWorker

    @Binds
    @IntoMap
    @WorkerKey(WomensUnplayedWorldRugbyMatchesWorker::class)
    abstract fun bindWomensUnplayedWorldRugbyMatchesWorker(womensUnplayedWorldRugbyMatchesWorker: WomensUnplayedWorldRugbyMatchesWorker): ListenableWorker

    @Binds
    @IntoMap
    @WorkerKey(MensCompleteWorldRugbyMatchesWorker::class)
    abstract fun bindMensCompleteWorldRugbyMatchesWorker(mensCompleteWorldRugbyMatchesWorker: MensCompleteWorldRugbyMatchesWorker): ListenableWorker

    @Binds
    @IntoMap
    @WorkerKey(WomensCompleteWorldRugbyMatchesWorker::class)
    abstract fun bindWomensCompleteWorldRugbyMatchesWorker(womensCompleteWorldRugbyMatchesWorker: WomensCompleteWorldRugbyMatchesWorker): ListenableWorker
}
