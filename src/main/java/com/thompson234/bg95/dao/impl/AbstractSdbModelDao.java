package com.thompson234.bg95.dao.impl;

import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.amazonaws.services.simpledb.model.BatchDeleteAttributesRequest;
import com.amazonaws.services.simpledb.model.BatchPutAttributesRequest;
import com.amazonaws.services.simpledb.model.CreateDomainRequest;
import com.amazonaws.services.simpledb.model.DeletableItem;
import com.amazonaws.services.simpledb.model.DeleteDomainRequest;
import com.amazonaws.services.simpledb.model.DomainMetadataRequest;
import com.amazonaws.services.simpledb.model.Item;
import com.amazonaws.services.simpledb.model.NoSuchDomainException;
import com.amazonaws.services.simpledb.model.PutAttributesRequest;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.amazonaws.services.simpledb.model.ReplaceableItem;
import com.amazonaws.services.simpledb.model.SelectRequest;
import com.amazonaws.services.simpledb.model.SelectResult;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.thompson234.bg95.model.Model;
import com.yammer.dropwizard.logging.Log;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public abstract class AbstractSdbModelDao<T extends Model> extends AbstractModelDao<T> {
    private static final Log _sLog = Log.forClass(AbstractSdbModelDao.class);

    private static final String SELECT_ONE_FORMAT = "select * from %s where itemName() = '%s'";
    private static final String SELECT_ALL_FORMAT = "select * from %s limit 2500";

    protected static final int MAX_PUT_BATCH_SIZE = 25;
    protected static final int MAX_DELETE_BATCH_SIZE = 25;
    protected static final int MAX_ATTRIBUTES_PER_PUT = 256;

    protected static final int DISABLE_BATCH = 1;
    protected static final String TYPE = "_type";

    private final AmazonSimpleDBClient _client;
    private final String _domainName;

    public AbstractSdbModelDao(AmazonSimpleDBClient client, String domainName, boolean forceReset) {

        _client = client;
        _domainName = domainName;
        init(forceReset);
    }

    protected abstract List<T> fromItems(List<Item> items);

    protected abstract List<ReplaceableItem> toReplaceableItems(T model);

    protected String getSelectOneStatement(String id) {
        return String.format(SELECT_ONE_FORMAT, getDomainName(), id);
    }

    protected boolean domainExists(String domainName) {
        try {
            _client.domainMetadata(new DomainMetadataRequest().withDomainName(domainName));
            return true;
        } catch (NoSuchDomainException ex) {
            return false;
        }
    }

    protected void init(boolean forceReset) {

        final String domainName = getDomainName();
        boolean domainExists = domainExists(domainName);

        if (forceReset && domainExists) {
            _sLog.debug("Forcing reset of domain {}.", domainName);
            _client.deleteDomain(new DeleteDomainRequest(domainName));
            domainExists = false;
        }

        if (!domainExists) {
            _sLog.debug("Creating domain {}.", domainName);
            _client.createDomain(new CreateDomainRequest().withDomainName(domainName));
        }
    }

    protected AmazonSimpleDBClient getClient() {
        return _client;
    }

    protected String getDomainName() {
        return _domainName;
    }

    protected List<T> select(String select) {
        _sLog.debug("Select: {}", select);

        final SelectRequest request = new SelectRequest().withSelectExpression(select);

        final List<Item> allItems = Lists.newArrayList();
        while (true) {
            final SelectResult result = _client.select(request);
            allItems.addAll(result.getItems());

            if (result.getNextToken() != null) {
                request.setNextToken(result.getNextToken());
            } else {
                break;
            }
        }

        return fromItems(allItems);
    }

    @Override
    protected T doFindById(String id) {
        List<T> models = select(getSelectOneStatement(id));

        if (models.size() != 1) {
            throw new IllegalArgumentException("Query return 0 or multiple results for id " + id);
        }

        return models.get(0);
    }

    @Override
    protected List<T> doFindAll() {
        return select(String.format(SELECT_ALL_FORMAT, getDomainName()));
    }

    @Override
    protected void doSaveAll(Collection<T> all) {
        doSaveAll(all, MAX_PUT_BATCH_SIZE);
    }

    protected void doSaveAll(Collection<T> all, int putBatchSize) {
        if (all == null || all.isEmpty()) {
            return;
        }

        final List<ReplaceableItem> toBatch = Lists.newArrayList();
        final List<ReplaceableItem> singles = Lists.newArrayList();

        for (T model : all) {
            model.sanitize();
            final List<ReplaceableItem> items = toReplaceableItems(model);

            for (ReplaceableItem item : items) {
                if (item.getAttributes().size() > MAX_ATTRIBUTES_PER_PUT || putBatchSize == DISABLE_BATCH) {
                    _sLog.debug("Batching disabled or item {} has too many attributes ({}), adding to single queue.", item.getName(), item.getAttributes().size());
                    singles.add(item);
                } else {
                    toBatch.add(item);
                }
            }
        }

        if (toBatch.size() + singles.size() == 0) {
            return;
        }

        int batchSize = Math.min(MAX_PUT_BATCH_SIZE, putBatchSize);
        if (batchSize != putBatchSize) {
            _sLog.debug("Batch size {} too large, using max of {}.", putBatchSize, batchSize);
            batchSize = MAX_PUT_BATCH_SIZE;
        }

        final List<ReplaceableItem> currentBatch = Lists.newArrayList();
        for (Iterator<ReplaceableItem> it = toBatch.iterator(); it.hasNext(); ) {

            currentBatch.add(it.next());

            if (currentBatch.size() >= batchSize || !it.hasNext()) {
                final BatchPutAttributesRequest bpar =
                        new BatchPutAttributesRequest().withDomainName(getDomainName()).withItems(currentBatch);
                _sLog.debug("batchPutAttributes: {}, {}", getDomainName(), currentBatch.size());
                _client.batchPutAttributes(bpar);
                currentBatch.clear();
            }
        }

        for (ReplaceableItem single : singles) {
            final List<ReplaceableAttribute> attributeBatch = Lists.newArrayList();

            for (Iterator<ReplaceableAttribute> it = single.getAttributes().iterator(); it.hasNext(); ) {
                attributeBatch.add(it.next());

                if (attributeBatch.size() == MAX_ATTRIBUTES_PER_PUT || !it.hasNext()) {
                    final PutAttributesRequest par = new PutAttributesRequest()
                            .withDomainName(getDomainName())
                            .withItemName(single.getName())
                            .withAttributes(attributeBatch);
                    _sLog.debug("putAttributes: {}, {}:{}", getDomainName(), single.getName(), attributeBatch.size());
                    _client.putAttributes(par);
                    attributeBatch.clear();
                }
            }
        }
    }

    @Override
    protected void doDeleteAll(Collection<String> ids) {
        doDeleteAll(ids, MAX_DELETE_BATCH_SIZE);
    }

    protected void doDeleteAll(Collection<String> ids, int deleteBatchSize) {

        if (ids == null || ids.isEmpty()) {
            return;
        }

        Collection<DeletableItem> items = Collections2.transform(ids, new Function<String, DeletableItem>() {
            @Override
            public DeletableItem apply(@Nullable String id) {
                return new DeletableItem().withName(id);
            }
        });

        if (items == null || items.isEmpty()) {
            return;
        }

        int batchSize = Math.min(MAX_DELETE_BATCH_SIZE, deleteBatchSize);
        if (batchSize != deleteBatchSize) {
            _sLog.debug("Delete batch size {} too large, using max of {}.", deleteBatchSize, batchSize);
            batchSize = MAX_DELETE_BATCH_SIZE;
        }

        final List<DeletableItem> currentBatch = Lists.newArrayList();
        for (Iterator<DeletableItem> it = items.iterator(); it.hasNext(); ) {

            currentBatch.add(it.next());

            if (currentBatch.size() >= batchSize || !it.hasNext()) {
                final BatchDeleteAttributesRequest bdar =
                        new BatchDeleteAttributesRequest().withDomainName(getDomainName()).withItems(currentBatch);

                _sLog.debug("batchDeleteAttributes: {}, {}", getDomainName(), currentBatch.size());
                _client.batchDeleteAttributes(bdar);
                currentBatch.clear();
            }
        }
    }
}
