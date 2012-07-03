package com.thompson234.bg95.health;

import com.thompson234.bg95.model.Type;
import com.yammer.metrics.core.HealthCheck;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;

public class IndexHealthCheck extends HealthCheck {

    private final Directory _directory;

    public IndexHealthCheck(Directory directory) {
        super("index");
        _directory = directory;
    }

    @Override
    protected Result check() throws Exception {

        final IndexReader reader = IndexReader.open(_directory);

        final int numDocs = reader.numDocs();

        if (numDocs <= 0) {
            return Result.unhealthy("No docs.");
        }

        int airmenCount = getTypeCount(reader, numDocs, Type.AIRMAN);
        int aircraftCount = getTypeCount(reader, numDocs, Type.AIRCRAFT);
        int missionCount = getTypeCount(reader, numDocs, Type.MISSION);

        return Result.healthy(String.format("%d Airmen; %d Aircraft; %d Missions", airmenCount, aircraftCount, missionCount));
    }

    private int getTypeCount(IndexReader reader, int maxDocs, String type) throws Exception {
        final IndexSearcher searcher = new IndexSearcher(reader);
        final TopDocs topDocs = searcher.search(new TermQuery(new Term("type", type)), maxDocs);
        return topDocs.totalHits;
    }
}
