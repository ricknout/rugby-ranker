package dev.ricknout.rugbyranker.prediction.data

import dev.ricknout.rugbyranker.core.db.RankingDao
import dev.ricknout.rugbyranker.core.model.Sport

class PredictionRepository(private val dao: RankingDao) {
    fun loadRankings(sport: Sport) = dao.loadByTeamName(sport)
}
