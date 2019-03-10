package com.cache.oldCache;

public class MyAccountService {
    private MyCacheManager<Account> cacheManager;

    public MyAccountService() {
        cacheManager = new MyCacheManager<>();// 构造一个缓存管理器
    }

    public Account getAccountByName(String acctName) {
        //1. 首先查询缓存
        Account result = cacheManager.getValue(acctName);
        if(result!=null) {
            // 如果在缓存中，则直接返回缓存的结果
            System.out.println("get from cache..."+acctName);
            return result;
        }
        //2. 否则到数据库中查询
        result = getFromDB(acctName);
        //3. 将数据库查询的结果更新到缓存中
        if(result!=null) {
            cacheManager.addOrUpdateCache(acctName, result);
        }
        return result;
    }

    public void reload() {
        cacheManager.evictCache();
    }

    private Account getFromDB(String acctName) {
        System.out.println("real querying db..."+acctName);
        return new Account(acctName);
    }
}
